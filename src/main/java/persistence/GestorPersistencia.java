package persistence;

import java.util.HashMap;
import java.util.ArrayList;
import domain.Usuario;
import domain.Encuesta;
import domain.Pregunta;
import domain.Respuesta;

/**
 * Gestor de persistencia del sistema (stub de base de datos).
 * 
 * Almacena todas las entidades del dominio en memoria con HashMaps.
 * Proporciona operaciones CRUD para Usuario, Encuesta, Pregunta y Respuesta.
 * 
 * @author Alessandro Jin
 */
public class GestorPersistencia {
    /** Atributos **/

    private static GestorPersistencia singletonInstance;

    /**
     * Mapa que almacena los usuarios existentes del sistema.
     * <p>
     * La clave es el nombre de usuario y el valor es el objeto {@link domain.Usuario}.
     */
    private HashMap<String,Usuario> usuarios; 

    /**
     * Mapa que almacena las encuestas existentes del sistema.
     * <p>
     * La clave es un Integer id y el valor es el objeto {@link domain.Encuesta}.
     */
    private HashMap<Integer,Encuesta> encuestas;

    /**
     * Mapa que almacena las preguntas de todas las encuestas del sistema.
     * <p>
     * La clave es un Integer id y el valor es el objeto {@link domain.Pregunta}.
     */
    private HashMap<Integer,Pregunta> preguntas;

    /**
     * Mapa que almacena las respuestas de todas las preguntas del sistema.
     * <p>
     * La clave es un Integer id y el valor es el objeto {@link domain.Respuesta}.
     */
    private HashMap<Integer,Respuesta> respuestas;

    /** ID para la próxima encuesta a crear */
    private int nextIdEncuesta;

    /** ID para la próxima pregunta a crear */
    private int nextIdPregunta;

    /** ID para la próxima respuesta a crear */
    private int nextIdRespuesta;


    /** Constructor y metodos de inicializacion **/

    /**
     * Inicialización de los atributos de la clase.
     */
	private GestorPersistencia() {
        usuarios = new HashMap<>();
        encuestas = new HashMap<>();
        preguntas = new HashMap<>();
        respuestas = new HashMap<>();
        nextIdEncuesta = 1;
        nextIdPregunta = 1;
        nextIdRespuesta = 1;
    }

    /**
     * Obtiene la instancia única del gestor (patrón singleton).
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
	 * Funciones de gestión de datos del sistema.
	 * <p>
     * Organizadas por entidad: Usuario, Encuesta, Pregunta, Respuesta.
	 **/

    
    // ==================== MÉTODOS DE USUARIO ====================

    /**
    * Devuelve una lista con todos los usuarios almacenados.
    *
    * @return una lista con todos los usuarios existentes
    */
    public ArrayList<Usuario> getUsuarios() {
        return new ArrayList<>(usuarios.values());
    }

    /**
     * Obtiene un usuario por su nombre.
     * 
     * @param nom Nombre del usuario
     * @return Usuario correspondiente, o null si no existe
     */
    public Usuario getUsuario(String nom) {
        return usuarios.get(nom);
    }

    /**
     * Verifica si existe un usuario con el nombre dado.
     * 
     * @param nom Nombre del usuario
     * @return true si existe, false en caso contrario
     */
    public boolean existeUsuario(String nom) {
        return usuarios.containsKey(nom);
    }

    /**
    * Guarda un nuevo usuario en el mapa {@code usuarios}.
    *
    * @param u Usuario a guardar
    * @throws IllegalArgumentException si ya existe un usuario con el mismo nombre
    */
    public void guardarUsuario(Usuario u) {
        String nom = u.getNomUsuario();
        if (usuarios.containsKey(nom)) {
            throw new IllegalArgumentException("El usuario con nom: " + nom + " ya existe");
        }
        usuarios.put(nom, u);
    }

    /**
     * Actualiza un usuario existente.
     * 
     * @param u Usuario a actualizar
     */
    public void actualizarUsuario(Usuario u) {
        usuarios.put(u.getNomUsuario(), u);
    }

    /**
    * Elimina un usuario por el objeto Usuario.
    *
    * @param u Usuario a eliminar
    * @throws IllegalArgumentException si el usuario no existe
    */
    public void eliminarUsuario(Usuario u) {
        String name = u.getNomUsuario();
        if(!usuarios.containsKey(name)) {
            throw new IllegalArgumentException("El usuario con name: " + name + " no existe");
        }
        usuarios.remove(name);
    }

    /**
     * Elimina un usuario por su nombre.
     * 
     * @param nom Nombre del usuario a eliminar
     * @throws IllegalArgumentException si el usuario no existe
     */
    public void eliminarUsuario(String nom) {
        if (!usuarios.containsKey(nom)) {
            throw new IllegalArgumentException("El usuario con nombre: " + nom + " no existe");
        }
        usuarios.remove(nom);
    }


    // ==================== MÉTODOS DE ENCUESTA ====================

    /**
    * Devuelve una lista con todas las encuestas almacenadas.
    *
    * @return Lista con todas las encuestas existentes
    */
    public ArrayList<Encuesta> getEncuestas() {
        return new ArrayList<>(encuestas.values());
    }

    /**
     * Obtiene una encuesta por su ID.
     * 
     * @param id ID de la encuesta
     * @return Encuesta correspondiente, o null si no existe
     */
    public Encuesta getEncuesta(int id) {
        return encuestas.get(id);
    }

    /**
     * Verifica si existe una encuesta con el ID dado.
     * 
     * @param id ID de la encuesta
     * @return true si existe, false en caso contrario
     */
    public boolean existeEncuesta(int id) {
        return encuestas.containsKey(id);
    }

    /**
     * Obtiene el siguiente ID disponible para una nueva encuesta.
     * 
     * @return Siguiente ID disponible
     */
    public int getNextIdEncuesta() {
        return nextIdEncuesta++;
    }

    /**
    * Guarda una nueva encuesta en el mapa {@code encuestas}.
    *
    * @param e Encuesta a guardar
    * @throws IllegalArgumentException si ya existe una encuesta con el mismo id
    */
    public void guardarEncuesta(Encuesta e) {
        Integer id = e.getIdEncuesta();
        if(encuestas.containsKey(id)) {
            throw new IllegalArgumentException("La encuesta con id: "+ id + " ya existe");
        }
        encuestas.put(id, e);
    }

    /**
     * Actualiza una encuesta existente.
     * 
     * @param e Encuesta a actualizar
     */
    public void actualizarEncuesta(Encuesta e) {
        encuestas.put(e.getIdEncuesta(), e);
    }

    /**
    * Elimina una encuesta por el objeto Encuesta.
    *
    * @param e Encuesta a eliminar
    * @throws IllegalArgumentException si la encuesta no existe
    */
    public void eliminarEncuesta(Encuesta e) {
        Integer id = e.getIdEncuesta();
        if(!encuestas.containsKey(id)) {
            throw new IllegalArgumentException("La encuesta con id: " + id + " no existe");
        }
        encuestas.remove(id);
    }

    /**
     * Elimina una encuesta por su ID.
     * 
     * @param id ID de la encuesta a eliminar
     * @throws IllegalArgumentException si la encuesta no existe
     */
    public void eliminarEncuesta(int id) {
        if (!encuestas.containsKey(id)) {
            throw new IllegalArgumentException("La encuesta con id: " + id + " no existe");
        }
        encuestas.remove(id);
    }


    // ==================== MÉTODOS DE PREGUNTA ====================

    /**
    * Devuelve una lista con todas las preguntas almacenadas.
    *
    * @return Lista con todas las preguntas existentes
    */
    public ArrayList<Pregunta> getPreguntas() {
        return new ArrayList<>(preguntas.values());
    }

    /**
     * Obtiene una pregunta por su ID.
     * 
     * @param id ID de la pregunta
     * @return Pregunta correspondiente, o null si no existe
     */
    public Pregunta getPregunta(int id) {
        return preguntas.get(id);
    }

    /**
     * Verifica si existe una pregunta con el ID dado.
     * 
     * @param id ID de la pregunta
     * @return true si existe, false en caso contrario
     */
    public boolean existePregunta(int id) {
        return preguntas.containsKey(id);
    }

    /**
     * Obtiene el siguiente ID disponible para una nueva pregunta.
     * 
     * @return Siguiente ID disponible
     */
    public int getNextIdPregunta() {
        return nextIdPregunta++;
    }

    /**
    * Guarda una nueva pregunta en el mapa {@code preguntas}.
    *
    * @param p Pregunta a guardar
    * @throws IllegalArgumentException si ya existe una pregunta con el mismo id
    */
    public void guardarPregunta(Pregunta p) {
        Integer id = p.getIdPregunta();
        if (preguntas.containsKey(id)) {
            throw new IllegalArgumentException("La pregunta con id: " + id + " ya existe");
        }
        preguntas.put(id, p);
    }

    /**
     * Actualiza una pregunta existente.
     * 
     * @param p Pregunta a actualizar
     */
    public void actualizarPregunta(Pregunta p) {
        preguntas.put(p.getIdPregunta(), p);
    }

    /**
    * Elimina una pregunta por el objeto Pregunta.
    *
    * @param p Pregunta a eliminar
    * @throws IllegalArgumentException si la pregunta no existe
    */
    public void eliminarPregunta(Pregunta p) {
        Integer id = p.getIdPregunta();
        if (!preguntas.containsKey(id)) {
            throw new IllegalArgumentException("La pregunta con id: " + id + " no existe");
        }
        preguntas.remove(id);
    }

    /**
     * Elimina una pregunta por su ID.
     * 
     * @param id ID de la pregunta a eliminar
     * @throws IllegalArgumentException si la pregunta no existe
     */
    public void eliminarPregunta(int id) {
        if (!preguntas.containsKey(id)) {
            throw new IllegalArgumentException("La pregunta con id: " + id + " no existe");
        }
        preguntas.remove(id);
    }


    // ==================== MÉTODOS DE RESPUESTA ====================

    /**
    * Devuelve una lista con todas las respuestas almacenadas.
    *
    * @return Lista con todas las respuestas existentes
    */
    public ArrayList<Respuesta> getRespuestas() {
        return new ArrayList<>(respuestas.values());
    }

    /**
     * Obtiene una respuesta por su ID.
     * 
     * @param id ID de la respuesta
     * @return Respuesta correspondiente, o null si no existe
     */
    public Respuesta getRespuesta(int id) {
        return respuestas.get(id);
    }

    /**
     * Verifica si existe una respuesta con el ID dado.
     * 
     * @param id ID de la respuesta
     * @return true si existe, false en caso contrario
     */
    public boolean existeRespuesta(int id) {
        return respuestas.containsKey(id);
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
    * Guarda una nueva respuesta en el mapa {@code respuestas}.
    *
    * @param r Respuesta a guardar
    * @throws IllegalArgumentException si ya existe una respuesta con el mismo id
    */
    public void guardarRepuesta(Respuesta r) {
        Integer id = r.getId();
        if (respuestas.containsKey(id)) {
            throw new IllegalArgumentException("La respuesta con id: " + id + " ya existe");
        }
        respuestas.put(id, r);
    }

    /**
     * Guarda una nueva respuesta en el sistema.
     * 
     * @param r Respuesta a guardar
     * @throws IllegalArgumentException si ya existe una respuesta con el mismo ID
     */
    public void guardarRespuesta(Respuesta r) {
        Integer id = r.getId();
        if (respuestas.containsKey(id)) {
            throw new IllegalArgumentException("La respuesta con id: " + id + " ya existe");
        }
        respuestas.put(id, r);
    }

    /**
     * Actualiza una respuesta existente.
     * 
     * @param r Respuesta a actualizar
     */
    public void actualizarRespuesta(Respuesta r) {
        respuestas.put(r.getId(), r);
    }

    /**
    * Elimina una respuesta por el objeto Respuesta.
    *
    * @param r Respuesta a eliminar
    * @throws IllegalArgumentException si la respuesta no existe
    */
    public void eliminarRespuesta(Respuesta r) {
        Integer id = r.getId();
        if (!respuestas.containsKey(id)) {
            throw new IllegalArgumentException("La respuesta con id: " + id + " no existe");
        }
        respuestas.remove(id);
    }

    /**
     * Elimina una respuesta por su ID.
     * 
     * @param id ID de la respuesta a eliminar
     * @throws IllegalArgumentException si la respuesta no existe
     */
    public void eliminarRespuesta(int id) {
        if (!respuestas.containsKey(id)) {
            throw new IllegalArgumentException("La respuesta con id: " + id + " no existe");
        }
        respuestas.remove(id);
    }
}

