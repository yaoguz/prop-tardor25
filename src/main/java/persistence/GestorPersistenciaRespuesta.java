package persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import domain.Respuesta;

/**
 * Gestor de persistencia de respuestas con cache LRU.
 * 
 * Mantiene una cache limitada de respuestas más recientemente usadas
 * y delega operaciones de disco a CtrlPersistenciaRespuesta.
 * 
 * RESPONSABILIDADES:
 * - Gestionar cache LRU de respuestas (máximo 256 entradas)
 * - Coordinar con CtrlPersistenciaRespuesta para operaciones de disco
 * - Gestionar IDs secuenciales para nuevas respuestas
 * - Proporcionar operaciones CRUD con cache transparente
 * - Optimizar carga de respuestas por encuesta
 * - Implementar patrón Singleton
 * 
 * ATRIBUTOS PRINCIPALES:
 * - respuestas: LinkedHashMap con política LRU (accessOrder=true)
 * - ctrlFicheroRespuesta: Controlador de persistencia en disco
 * - nextIdRespuesta: ID para la próxima respuesta a crear
 * - CACHE_LIMIT: Límite de 256 respuestas en cache
 * 
 * ESTRUCTURA:
 * - Cache: LinkedHashMap con removeEldestEntry para LRU automático
 * - Disco: Archivos JSON en data/encuestas/{idEncuesta}/{idRespuesta}.json
 * - IDs: Gestionados en data/nextId.json
 * - Índice: data/indexRespuestas.json mapea idRespuesta → idEncuesta
 * 
 * @author Yaoyao Guo
 */
public class GestorPersistenciaRespuesta {

    private static GestorPersistenciaRespuesta singletonInstance;

    // Límites de cache
    private static final int CACHE_LIMIT = 256;

    // Cache con LinkedHashMap (accessOrder=true para LRU tracking)
    private final LinkedHashMap<Integer, Respuesta> respuestas;

    // Controlador de persistencia respuesta en disco
    private final CtrlPersistenciaRespuesta ctrlFicheroRespuesta;

    /** ID para la próxima respuesta a crear */
    private int nextIdRespuesta;

    /**
     * Obtiene instancia única (patrón Singleton).
     * 
     * @return Instancia única de GestorPersistenciaRespuesta
     */
    public static GestorPersistenciaRespuesta getInstance() {
        if (singletonInstance == null) {
            singletonInstance = GestorPersistenciaRespuesta.getInstance();
        }
        return singletonInstance;
    }

    private GestorPersistenciaRespuesta() {
        // Inicializar LinkedHashMaps con accessOrder=true para tracking LRU
        respuestas = new LinkedHashMap<>(CACHE_LIMIT, 2.0f, true) {
            protected boolean removeEldestEntry(Map.Entry<Integer, Respuesta> eldest){
                return size() > CACHE_LIMIT;
            }
        };

        // Inicializar controladores de disco
        ctrlFicheroRespuesta = new CtrlPersistenciaRespuesta();
        nextIdRespuesta = ctrlFicheroRespuesta.getNextId();
    }

    // ==================== MÉTODOS DE RESPUESTA ====================

    /**
     * Busca y carga una respuesta desde disco a la cache.
     * 
     * @param idR ID de la respuesta
     * @return Respuesta cargada desde disco
     * @throws IllegalArgumentException si la respuesta no existe
     */
    Respuesta sacarRespuestaFichero(int idR) {
        Respuesta r = ctrlFicheroRespuesta.getRespuesta(idR); // puede lanzar exc no existe
        respuestas.put(idR, r);
        return r;
    }

    /**
     * Busca y carga una respuesta desde disco a la cache.
     * 
     * @param idR ID de la respuesta
     * @param idEncuesta ID de la encuesta
     * @return Respuesta cargada desde disco
     * @throws IllegalArgumentException si la respuesta no existe
     */
    Respuesta sacarRespuestaFichero(int idR, int idEncuesta) {
        Respuesta r = ctrlFicheroRespuesta.getRespuesta(idR, idEncuesta);
        respuestas.put(idR, r);
        return r;
    }

    /**
    * Guarda una nueva respuesta en el mapa {@code respuestas}.
    *
    * @param r Respuesta a guardar
    * @throws IllegalArgumentException si ya existe una respuesta con el mismo ID
    */
    public void guardarRespuesta(Respuesta r) {
        int idR = r.getId();

        if (respuestas.containsKey(idR)) {
            throw new IllegalArgumentException("La respuesta con id "+idR+" ya existe");
        }

        ctrlFicheroRespuesta.guardarRespuesta(r); // también tendria que lanzar excepcion de que ya existe
        respuestas.put(idR, r);
    }

    /**
    * Guarda una nueva respuesta en el mapa {@code respuestas}.
    *
    * @param r Respuesta a guardar
    * @param idEncuesta ID de la encuesta
    * @throws IllegalArgumentException si ya existe una respuesta con el mismo ID
    */
    public void guardarRespuesta(Respuesta r, int idEncuesta) {
        int idR = r.getId();

        if (respuestas.containsKey(idR)) {
            throw new IllegalArgumentException("La respuesta con id "+idR+" ya existe");
        }

        ctrlFicheroRespuesta.guardarRespuesta(r, idEncuesta);
        respuestas.put(idR, r);
    }
    
    /**
    * Devuelve una lista con todas las respuestas almacenadas.
    *
    * @return Lista con todas las respuestas existentes
    */
    public ArrayList<Respuesta> getAllRespuestas() {
        return ctrlFicheroRespuesta.getAllRespuestas();
    }

    /**
     * Obtiene una respuesta por su ID.
     * 
     * @param id ID de la respuesta
     * @return Respuesta correspondiente, o null si no existe
     * @throws IllegalArgumentException si la respuesta no existe
     */
    public Respuesta getRespuesta(int id) {
        Respuesta e = respuestas.get(id);
        if (e == null) e = sacarRespuestaFichero(id);
        return e;
    }

    /**
     * Obtiene una respuesta por su ID.
     * 
     * @param id ID de la respuesta
     * @param idEncuesta ID de la encuesta
     * @return Respuesta correspondiente, o null si no existe
     * @throws IllegalArgumentException si la respuesta no existe
     */
    public Respuesta getRespuesta(int id, int idEncuesta) {
        Respuesta e = respuestas.get(id);
        if (e == null) e = sacarRespuestaFichero(id, idEncuesta);
        return e;
    }

    /**
     * Verifica si existe una respuesta con el ID dado.
     * 
     * @param id ID de la respuesta
     * @return true si existe, false en caso contrario
     */
    public boolean existeRespuesta(int id) {
        if (!respuestas.containsKey(id)) {
            try {
                sacarRespuestaFichero(id); // guardar en la cache
            } 
            catch (IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Verifica si existe una respuesta con el ID dado.
     * 
     * @param id ID de la respuesta
     * @param idEncuesta ID de la encuesta
     * @return true si existe, false en caso contrario
     */
    public boolean existeRespuesta(int id, int idEncuesta) {
        if (!respuestas.containsKey(id)) {
            try {
                sacarRespuestaFichero(id, idEncuesta);
            } 
            catch (IllegalArgumentException e) {
                return false;
            }
        }
        return true;
    }

    /**
     * Obtiene el siguiente ID disponible para una nueva respuesta.
     * 
     * @return Siguiente ID disponible
     */
    public int getNextIdRespuesta() {
        return nextIdRespuesta++;
    }
    /**
     * Actualiza una respuesta existente.
     * 
     * @param r Respuesta a actualizar
     */
    public void actualizarRespuesta(Respuesta r) {
        int id = r.getId();
        if (!respuestas.containsKey(id)) sacarRespuestaFichero(id);
        respuestas.put(id, r); // sobreescribe
        ctrlFicheroRespuesta.actualizarRespuesta(r);
    }

    /**
     * Actualiza una respuesta existente.
     * 
     * @param r Respuesta a actualizar
     * @param idEncuesta ID de la encuesta
     */
    public void actualizarRespuesta(Respuesta r, int idEncuesta) {
        int id = r.getId();
        if (!respuestas.containsKey(id)) sacarRespuestaFichero(id, idEncuesta);
        respuestas.put(id, r); // sobreescribe
        ctrlFicheroRespuesta.actualizarRespuesta(r, idEncuesta);
    }

    /**
     * Elimina una respuesta por su ID.
     * 
     * @param id ID de la respuesta a eliminar
     * @throws IllegalArgumentException si la respuesta no existe
     */
    public void eliminarRespuesta(int id) {
        if (respuestas.containsKey(id)) respuestas.remove(id);
        ctrlFicheroRespuesta.eliminarRespuesta(id);
    }

    /**
     * Elimina una respuesta por su ID.
     * 
     * @param id ID de la respuesta a eliminar
     * @param idEncuesta ID de la encuesta
     * @throws IllegalArgumentException si la respuesta no existe
     */
    public void eliminarRespuesta(int id, int idEncuesta) {
        if (respuestas.containsKey(id)) respuestas.remove(id);
        ctrlFicheroRespuesta.eliminarRespuesta(id, idEncuesta);
    }

    /**
     * Guarda el siguiente ID disponible en disco.
     */
    public void guardarNextId () {
        ctrlFicheroRespuesta.guardarNextId(nextIdRespuesta);
    }
}