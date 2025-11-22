package persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import domain.Usuario;
import domain.Encuesta;
import domain.Pregunta;
import domain.Respuesta;

/**
 * Gestor de persistencia del sistema con cache LRU.
 * 
 * Implementa cache en memoria (Maps) con límite de 64 elementos por entidad.
 * Cuando se supera el límite, evicta 16 elementos según estrategia LRU.
 * Lazy loading: busca primero en cache, si no está, carga de disco.
 * 
 * RESPONSABILIDADES:
 * - Gestionar cache limitada (64 elementos) con evicción estratégica
 * - Coordinar lectura/escritura con controladores de disco
 * - Mantener IDs autoincrementales (encuestas, preguntas, respuestas)
 * - Proporcionar interfaz única de persistencia al sistema
 * 
 * ATRIBUTOS PRINCIPALES:
 * - usuarios/encuestas/preguntas/respuestas: LinkedHashMaps como cache
 * - CACHE_LIMIT: 64 elementos por entidad
 * - EVICT_SIZE: 16 elementos a descartar cuando se supera límite
 * - estrategiaEviccion: Patrón Strategy para política de descarte (LRU por defecto)
 * - ctrlUsuario/Encuesta/Pregunta/Respuesta: Controladores I/O disco
 * 
 * MÉTODOS PRINCIPALES:
 * - getXxx(): Obtiene entidad (cache → disco si miss)
 * - guardarXxx(): Guarda entidad (cache + disco)
 * - eliminarXxx(): Elimina entidad (cache + disco)
 * - evictCache(): Aplica estrategia de evicción cuando cache llena
 * 
 * @author PROP Cluster XX
 */
public class GestorPersistencia {
    private static GestorPersistencia singletonInstance;

    // Límites de cache
    private static final int CACHE_LIMIT = 64;
    private static final int EVICT_SIZE = 16;

    // Cache con LinkedHashMap (accessOrder=true para LRU tracking)
    private final LinkedHashMap<String, Usuario> usuarios;
    private final LinkedHashMap<Integer, Encuesta> encuestas;
    private final LinkedHashMap<Integer, Pregunta> preguntas;
    private final LinkedHashMap<Integer, Respuesta> respuestas;

    // Controladores de persistencia en disco
    private final CtrlPersistenciaUsuario ctrlUsuario;
    private final CtrlPersistenciaEncuesta ctrlEncuesta;
    private final CtrlPersistenciaPregunta ctrlPregunta;
    private final CtrlPersistenciaRespuesta ctrlRespuesta;

    // Estrategia de evicción (patrón Strategy)
    private EstrategiaEviccion estrategiaEviccion;

    // IDs autoincrementales
    private int nextIdEncuesta;
    private int nextIdPregunta;
    private int nextIdRespuesta;

    /**
     * Inicializa gestor con cache vacía y estrategia LRU.
     */
    private GestorPersistencia() {
        // Inicializar LinkedHashMaps con accessOrder=true para tracking LRU
        usuarios = new LinkedHashMap<>(CACHE_LIMIT, 0.75f, true);
        encuestas = new LinkedHashMap<>(CACHE_LIMIT, 0.75f, true);
        preguntas = new LinkedHashMap<>(CACHE_LIMIT, 0.75f, true);
        respuestas = new LinkedHashMap<>(CACHE_LIMIT, 0.75f, true);

        // Inicializar controladores de disco
        ctrlUsuario = new CtrlPersistenciaUsuario();
        ctrlEncuesta = new CtrlPersistenciaEncuesta();
        ctrlPregunta = new CtrlPersistenciaPregunta();
        ctrlRespuesta = new CtrlPersistenciaRespuesta();

        // Estrategia LRU por defecto
        estrategiaEviccion = new LRU();

        // Calcular nextIds desde disco
        nextIdEncuesta = calcularMaxId(ctrlEncuesta.cargarTodas().values()) + 1;
        nextIdPregunta = calcularMaxId(ctrlPregunta.cargarTodas().values()) + 1;
        nextIdRespuesta = calcularMaxId(ctrlRespuesta.cargarTodas().values()) + 1;
    }

    /**
     * Obtiene instancia única (patrón Singleton).
     * 
     * @return Instancia única de GestorPersistencia
     */
    public static GestorPersistencia getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new GestorPersistencia();
        }
        return singletonInstance;
    }

    /**
     * Cambia estrategia de evicción (patrón Strategy).
     * 
     * @param estrategia Nueva estrategia de evicción
     */
    public void setEstrategiaEviccion(EstrategiaEviccion estrategia) {
        this.estrategiaEviccion = estrategia;
    }

    /**
     * Evicta elementos de la cache según estrategia.
     * 
     * @param <K> Tipo de clave
     * @param <V> Tipo de valor
     * @param cache Mapa a evictar
     */
    private <K, V> void evictCache(LinkedHashMap<K, V> cache) {
        if (cache.size() <= CACHE_LIMIT) return;
        
        List<K> victimas = estrategiaEviccion.seleccionarVictimas(cache.keySet(), EVICT_SIZE);
        for (K key : victimas) {
            cache.remove(key);
        }
    }

    /**
     * Calcula ID máximo de una colección.
     * 
     * @param <T> Tipo de entidad
     * @param collection Colección de entidades
     * @return ID máximo encontrado
     */
    private <T> int calcularMaxId(java.util.Collection<T> collection) {
        int max = 0;
        for (T obj : collection) {
            int id = 0;
            if (obj instanceof Encuesta) id = ((Encuesta) obj).getIdEncuesta();
            else if (obj instanceof Pregunta) id = ((Pregunta) obj).getIdPregunta();
            else if (obj instanceof Respuesta) id = ((Respuesta) obj).getId();
            if (id > max) max = id;
        }
        return max;
    }

    
    // ==================== MÉTODOS DE USUARIO ====================

    /**
     * Devuelve lista con todos los usuarios.
     * 
     * Carga de cache si disponible, sino carga todo desde disco.
     * 
     * @return Lista con todos los usuarios
     */
    public ArrayList<Usuario> getUsuarios() {
        if (usuarios.isEmpty()) {
            Map<String, Usuario> todosDisco = ctrlUsuario.cargarTodos();
            for (Map.Entry<String, Usuario> e : todosDisco.entrySet()) {
                usuarios.put(e.getKey(), e.getValue());
                evictCache(usuarios);
            }
        }
        return new ArrayList<>(usuarios.values());
    }

    /**
     * Obtiene usuario por nombre (lazy loading).
     * 
     * Busca primero en cache, si no está, carga de disco y actualiza cache.
     * 
     * @param nom Nombre del usuario
     * @return Usuario o null si no existe
     */
    public Usuario getUsuario(String nom) {
        // Buscar en cache primero
        if (usuarios.containsKey(nom)) {
            return usuarios.get(nom); // LinkedHashMap actualiza orden de acceso
        }
        
        // Cache miss: cargar de disco
        Usuario u = ctrlUsuario.cargarUsuario(nom);
        if (u != null) {
            usuarios.put(nom, u);
            evictCache(usuarios);
        }
        return u;
    }

    /**
     * Verifica si existe un usuario.
     * 
     * @param nom Nombre del usuario
     * @return true si existe, false en caso contrario
     */
    public boolean existeUsuario(String nom) {
        // Primero verificar en cache
        if (usuarios.containsKey(nom)) return true;
        
        // Si no está en cache, buscar en disco
        return ctrlUsuario.cargarUsuario(nom) != null;
    }

    /**
     * Guarda nuevo usuario (cache + disco).
     * 
     * @param u Usuario a guardar
     * @throws IllegalArgumentException si ya existe
     */
    public void guardarUsuario(Usuario u) {
        String nom = u.getNomUsuario();
        if (existeUsuario(nom)) {
            throw new IllegalArgumentException("El usuario con nombre: " + nom + " ya existe");
        }
        usuarios.put(nom, u);
        evictCache(usuarios);
        ctrlUsuario.guardar(usuarios);
    }

    /**
     * Actualiza usuario existente (cache + disco).
     * 
     * @param u Usuario a actualizar
     */
    public void actualizarUsuario(Usuario u) {
        usuarios.put(u.getNomUsuario(), u);
        ctrlUsuario.guardar(usuarios);
    }

    /**
     * Elimina usuario por objeto (cache + disco).
     * 
     * @param u Usuario a eliminar
     * @throws IllegalArgumentException si no existe
     */
    public void eliminarUsuario(Usuario u) {
        String nom = u.getNomUsuario();
        if (!existeUsuario(nom)) {
            throw new IllegalArgumentException("El usuario con nombre: " + nom + " no existe");
        }
        usuarios.remove(nom);
        ctrlUsuario.guardar(usuarios);
    }

    /**
     * Elimina usuario por nombre (cache + disco).
     * 
     * @param nom Nombre del usuario
     * @throws IllegalArgumentException si no existe
     */
    public void eliminarUsuario(String nom) {
        if (!existeUsuario(nom)) {
            throw new IllegalArgumentException("El usuario con nombre: " + nom + " no existe");
        }
        usuarios.remove(nom);
        ctrlUsuario.guardar(usuarios);
    }

    
    // ==================== MÉTODOS DE ENCUESTA ====================

    /**
     * Devuelve lista con todas las encuestas.
     * 
     * Carga de cache si disponible, sino carga todo desde disco.
     * 
     * @return Lista con todas las encuestas
     */
    public ArrayList<Encuesta> getEncuestas() {
        if (encuestas.isEmpty()) {
            Map<Integer, Encuesta> todasDisco = ctrlEncuesta.cargarTodas();
            for (Map.Entry<Integer, Encuesta> e : todasDisco.entrySet()) {
                encuestas.put(e.getKey(), e.getValue());
                evictCache(encuestas);
            }
        }
        return new ArrayList<>(encuestas.values());
    }

    /**
     * Obtiene encuesta por ID (lazy loading).
     * 
     * Busca primero en cache, si no está, carga de disco y actualiza cache.
     * 
     * @param id ID de la encuesta
     * @return Encuesta o null si no existe
     */
    public Encuesta getEncuesta(int id) {
        if (encuestas.containsKey(id)) {
            return encuestas.get(id);
        }
        
        Encuesta e = ctrlEncuesta.cargarEncuesta(id);
        if (e != null) {
            encuestas.put(id, e);
            evictCache(encuestas);
        }
        return e;
    }

    /**
     * Verifica si existe una encuesta.
     * 
     * @param id ID de la encuesta
     * @return true si existe, false en caso contrario
     */
    public boolean existeEncuesta(int id) {
        if (encuestas.containsKey(id)) return true;
        return ctrlEncuesta.cargarEncuesta(id) != null;
    }

    /**
     * Obtiene siguiente ID disponible para encuesta.
     * 
     * @return Siguiente ID
     */
    public int getNextIdEncuesta() {
        return nextIdEncuesta++;
    }

    /**
     * Guarda nueva encuesta (cache + disco).
     * 
     * @param e Encuesta a guardar
     * @throws IllegalArgumentException si ya existe
     */
    public void guardarEncuesta(Encuesta e) {
        Integer id = e.getIdEncuesta();
        if (existeEncuesta(id)) {
            throw new IllegalArgumentException("La encuesta con id: " + id + " ya existe");
        }
        encuestas.put(id, e);
        evictCache(encuestas);
        ctrlEncuesta.guardar(encuestas);
    }

    /**
     * Actualiza encuesta existente (cache + disco).
     * 
     * @param e Encuesta a actualizar
     */
    public void actualizarEncuesta(Encuesta e) {
        encuestas.put(e.getIdEncuesta(), e);
        ctrlEncuesta.guardar(encuestas);
    }

    /**
     * Elimina encuesta por objeto (cache + disco).
     * 
     * @param e Encuesta a eliminar
     * @throws IllegalArgumentException si no existe
     */
    public void eliminarEncuesta(Encuesta e) {
        Integer id = e.getIdEncuesta();
        if (!existeEncuesta(id)) {
            throw new IllegalArgumentException("La encuesta con id: " + id + " no existe");
        }
        encuestas.remove(id);
        ctrlEncuesta.guardar(encuestas);
    }

    /**
     * Elimina encuesta por ID (cache + disco).
     * 
     * @param id ID de la encuesta
     * @throws IllegalArgumentException si no existe
     */
    public void eliminarEncuesta(int id) {
        if (!existeEncuesta(id)) {
            throw new IllegalArgumentException("La encuesta con id: " + id + " no existe");
        }
        encuestas.remove(id);
        ctrlEncuesta.guardar(encuestas);
    }

    
    // ==================== MÉTODOS DE PREGUNTA ====================

    /**
     * Devuelve lista con todas las preguntas.
     * 
     * Carga de cache si disponible, sino carga todo desde disco.
     * 
     * @return Lista con todas las preguntas
     */
    public ArrayList<Pregunta> getPreguntas() {
        if (preguntas.isEmpty()) {
            Map<Integer, Pregunta> todasDisco = ctrlPregunta.cargarTodas();
            for (Map.Entry<Integer, Pregunta> e : todasDisco.entrySet()) {
                preguntas.put(e.getKey(), e.getValue());
                evictCache(preguntas);
            }
        }
        return new ArrayList<>(preguntas.values());
    }

    /**
     * Obtiene pregunta por ID (lazy loading).
     * 
     * Busca primero en cache, si no está, carga de disco y actualiza cache.
     * 
     * @param id ID de la pregunta
     * @return Pregunta o null si no existe
     */
    public Pregunta getPregunta(int id) {
        if (preguntas.containsKey(id)) {
            return preguntas.get(id);
        }
        
        Pregunta p = ctrlPregunta.cargarPregunta(id);
        if (p != null) {
            preguntas.put(id, p);
            evictCache(preguntas);
        }
        return p;
    }

    /**
     * Verifica si existe una pregunta.
     * 
     * @param id ID de la pregunta
     * @return true si existe, false en caso contrario
     */
    public boolean existePregunta(int id) {
        if (preguntas.containsKey(id)) return true;
        return ctrlPregunta.cargarPregunta(id) != null;
    }

    /**
     * Obtiene siguiente ID disponible para pregunta.
     * 
     * @return Siguiente ID
     */
    public int getNextIdPregunta() {
        return nextIdPregunta++;
    }

    /**
     * Guarda nueva pregunta (cache + disco).
     * 
     * @param p Pregunta a guardar
     * @throws IllegalArgumentException si ya existe
     */
    public void guardarPregunta(Pregunta p) {
        Integer id = p.getIdPregunta();
        if (existePregunta(id)) {
            throw new IllegalArgumentException("La pregunta con id: " + id + " ya existe");
        }
        preguntas.put(id, p);
        evictCache(preguntas);
        ctrlPregunta.guardar(preguntas);
    }

    /**
     * Actualiza pregunta existente (cache + disco).
     * 
     * @param p Pregunta a actualizar
     */
    public void actualizarPregunta(Pregunta p) {
        preguntas.put(p.getIdPregunta(), p);
        ctrlPregunta.guardar(preguntas);
    }

    /**
     * Elimina pregunta por objeto (cache + disco).
     * 
     * @param p Pregunta a eliminar
     * @throws IllegalArgumentException si no existe
     */
    public void eliminarPregunta(Pregunta p) {
        Integer id = p.getIdPregunta();
        if (!existePregunta(id)) {
            throw new IllegalArgumentException("La pregunta con id: " + id + " no existe");
        }
        preguntas.remove(id);
        ctrlPregunta.guardar(preguntas);
    }

    /**
     * Elimina pregunta por ID (cache + disco).
     * 
     * @param id ID de la pregunta
     * @throws IllegalArgumentException si no existe
     */
    public void eliminarPregunta(int id) {
        if (!existePregunta(id)) {
            throw new IllegalArgumentException("La pregunta con id: " + id + " no existe");
        }
        preguntas.remove(id);
        ctrlPregunta.guardar(preguntas);
    }

    
    // ==================== MÉTODOS DE RESPUESTA ====================

    /**
     * Devuelve lista con todas las respuestas.
     * 
     * Carga de cache si disponible, sino carga todo desde disco.
     * 
     * @return Lista con todas las respuestas
     */
    public ArrayList<Respuesta> getRespuestas() {
        if (respuestas.isEmpty()) {
            Map<Integer, Respuesta> todasDisco = ctrlRespuesta.cargarTodas();
            for (Map.Entry<Integer, Respuesta> e : todasDisco.entrySet()) {
                respuestas.put(e.getKey(), e.getValue());
                evictCache(respuestas);
            }
        }
        return new ArrayList<>(respuestas.values());
    }

    /**
     * Obtiene respuesta por ID (lazy loading).
     * 
     * Busca primero en cache, si no está, carga de disco y actualiza cache.
     * 
     * @param id ID de la respuesta
     * @return Respuesta o null si no existe
     */
    public Respuesta getRespuesta(int id) {
        if (respuestas.containsKey(id)) {
            return respuestas.get(id);
        }
        
        Respuesta r = ctrlRespuesta.cargarRespuesta(id);
        if (r != null) {
            respuestas.put(id, r);
            evictCache(respuestas);
        }
        return r;
    }

    /**
     * Verifica si existe una respuesta.
     * 
     * @param id ID de la respuesta
     * @return true si existe, false en caso contrario
     */
    public boolean existeRespuesta(int id) {
        if (respuestas.containsKey(id)) return true;
        return ctrlRespuesta.cargarRespuesta(id) != null;
    }

    /**
     * Obtiene siguiente ID disponible para respuesta.
     * 
     * @return Siguiente ID
     */
    public int getNextIdRespuesta() {
        return nextIdRespuesta++;
    }

    /**
     * Guarda nueva respuesta (cache + disco).
     * 
     * @param r Respuesta a guardar
     * @throws IllegalArgumentException si ya existe
     */
    public void guardarRepuesta(Respuesta r) {
        guardarRespuesta(r);
    }

    /**
     * Guarda nueva respuesta en sistema (cache + disco).
     * 
     * @param r Respuesta a guardar
     * @throws IllegalArgumentException si ya existe
     */
    public void guardarRespuesta(Respuesta r) {
        Integer id = r.getId();
        if (existeRespuesta(id)) {
            throw new IllegalArgumentException("La respuesta con id: " + id + " ya existe");
        }
        respuestas.put(id, r);
        evictCache(respuestas);
        ctrlRespuesta.guardar(respuestas);
    }

    /**
     * Actualiza respuesta existente (cache + disco).
     * 
     * @param r Respuesta a actualizar
     */
    public void actualizarRespuesta(Respuesta r) {
        respuestas.put(r.getId(), r);
        ctrlRespuesta.guardar(respuestas);
    }

    /**
     * Elimina respuesta por objeto (cache + disco).
     * 
     * @param r Respuesta a eliminar
     * @throws IllegalArgumentException si no existe
     */
    public void eliminarRespuesta(Respuesta r) {
        Integer id = r.getId();
        if (!existeRespuesta(id)) {
            throw new IllegalArgumentException("La respuesta con id: " + id + " no existe");
        }
        respuestas.remove(id);
        ctrlRespuesta.guardar(respuestas);
    }

    /**
     * Elimina respuesta por ID (cache + disco).
     * 
     * @param id ID de la respuesta
     * @throws IllegalArgumentException si no existe
     */
    public void eliminarRespuesta(int id) {
        if (!existeRespuesta(id)) {
            throw new IllegalArgumentException("La respuesta con id: " + id + " no existe");
        }
        respuestas.remove(id);
        ctrlRespuesta.guardar(respuestas);
    }
}

