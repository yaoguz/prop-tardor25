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
    private GestorPersistenciaUsuario usuarios; 

    /**
     * Mapa que almacena las encuestas existentes del sistema.
     * <p>
     * La clave es un Integer id y el valor es el objeto {@link domain.Encuesta}.
     */
    private GestorPersistenciaEncuesta encuestas;

    /**
     * Mapa que almacena las preguntas de todas las encuestas del sistema.
     * <p>
     * La clave es un Integer id y el valor es el objeto {@link domain.Pregunta}.
     */
    private GestorPersistenciaPregunta preguntas;

    /**
     * Mapa que almacena las respuestas de todas las preguntas del sistema.
     * <p>
     * La clave es un Integer id y el valor es el objeto {@link domain.Respuesta}.
     */
    private GestorPersistenciaRespuesta respuestas;



    /** Constructor y metodos de inicializacion **/

    /**
     * Inicialización de los atributos de la clase.
     */
	private GestorPersistencia() {
        usuarios = GestorPersistenciaUsuario.getInstance();
        encuestas = GestorPersistenciaEncuesta.getInstance();
        preguntas = GestorPersistenciaPregunta.getInstance();
        respuestas = GestorPersistenciaRespuesta.getInstance();
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
        return usuarios.getAllUsuarios();
    }

    /**
     * Obtiene un usuario por su nombre.
     * 
     * @param nom Nombre del usuario
     * @return Usuario correspondiente, o null si no existe
     */
    public Usuario getUsuario(String nom) {
        return usuarios.getUsuario(nom);
    }

    /**
     * Verifica si existe un usuario con el nombre dado.
     * 
     * @param nom Nombre del usuario
     * @return true si existe, false en caso contrario
     */
    public boolean existeUsuario(String nom) {
        return usuarios.existeUsuario(nom);
    }

    /**
    * Guarda un nuevo usuario en el mapa {@code usuarios}.
    *
    * @param u Usuario a guardar
    * @throws IllegalArgumentException si ya existe un usuario con el mismo nombre
    */
    public void guardarUsuario(Usuario u) {

        usuarios.guardarUsuario(u);
    }

    /**
     * Actualiza un usuario existente.
     * 
     * @param u Usuario a actualizar
     */
    public void actualizarUsuario(Usuario u) {
        usuarios.actualizarUsuario(u); 
    }


    /**
     * Elimina un usuario por su nombre.
     * 
     * @param nom Nombre del usuario a eliminar
     * @throws IllegalArgumentException si el usuario no existe
     */
    public void eliminarUsuario(String nom) {
     
        usuarios.eliminarUsuario(nom);
    }


    // ==================== MÉTODOS DE ENCUESTA ====================

    /**
    * Devuelve una lista con todas las encuestas almacenadas.
    *
    * @return Lista con todas las encuestas existentes
    */
    public ArrayList<Encuesta> getEncuestas() {
        return encuestas.getAllEncuestas();
    }

    /**
     * Obtiene una encuesta por su ID.
     * 
     * @param id ID de la encuesta
     * @return Encuesta correspondiente, o null si no existe
     */
    public Encuesta getEncuesta(int id) {
        return encuestas.getEncuesta(id);
    }

    /**
     * Verifica si existe una encuesta con el ID dado.
     * 
     * @param id ID de la encuesta
     * @return true si existe, false en caso contrario
     */
    public boolean existeEncuesta(int id) {
        return encuestas.existeEncuesta(id);
    }

    /**
     * Obtiene el siguiente ID disponible para una nueva encuesta.
     * 
     * @return Siguiente ID disponible
     */
    public int getNextIdEncuesta() {
        return encuestas.getNextIdEncuesta();
    }

    /**
    * Guarda una nueva encuesta en el mapa {@code encuestas}.
    *
    * @param e Encuesta a guardar
    * @throws IllegalArgumentException si ya existe una encuesta con el mismo id
    */
    public void guardarEncuesta(Encuesta e) {
     
        encuestas.guardarEncuesta(e);
    }

    /**
     * Actualiza una encuesta existente.
     * 
     * @param e Encuesta a actualizar
     */
    public void actualizarEncuesta(Encuesta e) {
        encuestas.actualizarEncuesta(e);
    }

    /**
     * Elimina una encuesta por su ID.
     * 
     * @param id ID de la encuesta a eliminar
     * @throws IllegalArgumentException si la encuesta no existe
     */
    public void eliminarEncuesta(int id) {
   
        encuestas.eliminarEncuesta(id);
    }


    // ==================== MÉTODOS DE PREGUNTA ====================

    /**
    * Devuelve una lista con todas las preguntas almacenadas.
    *
    * @return Lista con todas las preguntas existentes
    */
    public ArrayList<Pregunta> getPreguntas() {
        return preguntas.getAllPreguntas();
    }

    /**
     * Obtiene una pregunta por su ID.
     * 
     * @param id ID de la pregunta
     * @return Pregunta correspondiente, o null si no existe
     */
    public Pregunta getPregunta(int id) {
        return preguntas.getPregunta(id);
    }

    /**
     * Verifica si existe una pregunta con el ID dado.
     * 
     * @param id ID de la pregunta
     * @return true si existe, false en caso contrario
     */
    public boolean existePregunta(int id) {
        return preguntas.existePregunta(id);
    }

    /**
     * Obtiene el siguiente ID disponible para una nueva pregunta.
     * 
     * @return Siguiente ID disponible
     */
    public int getNextIdPregunta() {
        return preguntas.getNextIdPregunta();
    }

    /**
    * Guarda una nueva pregunta en el mapa {@code preguntas}.
    *
    * @param p Pregunta a guardar
    * @throws IllegalArgumentException si ya existe una pregunta con el mismo id
    */
    public void guardarPregunta(Pregunta p) {
    
        preguntas.guardarPregunta(p);
    }

    /**
     * Actualiza una pregunta existente.
     * 
     * @param p Pregunta a actualizar
     */
    public void actualizarPregunta(Pregunta p) {
        preguntas.actualizarPregunta(p);
    }

    /**
     * Elimina una pregunta por su ID.
     * 
     * @param id ID de la pregunta a eliminar
     * @throws IllegalArgumentException si la pregunta no existe
     */
    public void eliminarPregunta(int id) {

        preguntas.eliminarPregunta(id);
    }


    // ==================== MÉTODOS DE RESPUESTA ====================

    /**
    * Devuelve una lista con todas las respuestas almacenadas.
    *
    * @return Lista con todas las respuestas existentes
    */
    public ArrayList<Respuesta> getRespuestas() {
        return respuestas.getAllRespuestas();
    }

    /**
     * Obtiene una respuesta por su ID.
     * 
     * ESTRUCTURA DE PERSISTENCIA:                                 ////////////////// comentario nuevo
     * - Archivo: data/encuestas/[idEncuesta]/[idRespuesta].json
     * - Requiere conocer idEncuesta para construir la ruta
     * 
     * NOTA: Para obtener respuesta individual sin conocer idEncuesta,
     * usar getRespuestasEncuesta() y filtrar, o mantener índice en memoria.
     * 
     * @param id ID de la respuesta
     * @return Respuesta correspondiente, o null si no existe
     */
    public Respuesta getRespuesta(int id) {
        return respuestas.getRespuesta(id);
    }

    /**
     * Verifica si existe una respuesta con el ID dado.
     * 
     * @param id ID de la respuesta
     * @return true si existe, false en caso contrario
     */
    public boolean existeRespuesta(int id) {
        return respuestas.existeRespuesta(id);
    }

    /**
     * Obtiene el siguiente ID disponible para una nueva respuesta.
     * 
     * @return Siguiente ID disponible
     */
    public int getNextIdRespuesta() {
        return respuestas.getNextIdRespuesta();
    }

    /**
    * Guarda una nueva respuesta en el mapa {@code respuestas}.
    *
    * @param r Respuesta a guardar
    * @throws IllegalArgumentException si ya existe una respuesta con el mismo id
    */
    public void guardarRepuesta(Respuesta r) {

        respuestas.guardarRepuesta(r);
    }

    /**
     * Guarda una nueva respuesta en el sistema.
     * 
     * @param r Respuesta a guardar
     * @throws IllegalArgumentException si ya existe una respuesta con el mismo ID
     */
    public void guardarRespuesta(Respuesta r) {
    
        respuestas.guardarRespuesta(r);
    }

    /**
     * Actualiza una respuesta existente.
     * 
     * @param r Respuesta a actualizar
     */
    public void actualizarRespuesta(Respuesta r) {
        respuestas.actualizarRespuesta(r);
    }


    /**
     * Elimina una respuesta por su ID.
     * 
     * @param id ID de la respuesta a eliminar
     * @throws IllegalArgumentException si la respuesta no existe
     */
    public void eliminarRespuesta(int id) {
 
        respuestas.eliminarRespuesta(id);
    }


    
    ///indefinida
    /**
     * Obtiene todas las respuestas de una encuesta específica.
     * 
     * ESTRUCTURA DE PERSISTENCIA:
     * - Cada respuesta es un archivo JSON individual
     * - Ubicación: data/encuestas/[idEncuesta]/[idRespuesta].json
     * - Ejemplo: data/encuestas/1/1.json, data/encuestas/1/2.json, etc.
     * 
     * IMPLEMENTACIÓN CON ARCHIVOS JSON:
     * 1. Listar archivos en data/encuestas/[idEncuesta]/
     * 2. Filtrar solo archivos numéricos (*.json excepto encuesta.json)
     * 3. Leer y deserializar cada archivo de respuesta
     * 4. Retornar lista completa
     * 
     * VENTAJA: Cada respuesta es independiente, fácil de añadir/eliminar/modificar
     * 
     * @param idEncuesta ID de la encuesta
     * @return Lista con todas las respuestas de la encuesta
     */
    public ArrayList<Respuesta> getRespuestasEncuesta(int idEncuesta) {                 ////////////////// nuevo

        return encuestas.getRespuestasEncuesta();
    }   

    public void cerrarSesion() {
        encuestas.guardarNextId();
    
        preguntas.guardarNextId();

        respuestas.guardarNextId();
    }

}

