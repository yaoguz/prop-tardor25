package persistence;

import domain.Usuario;
import domain.Encuesta;
import domain.Pregunta;
import domain.Respuesta;

/**
 * Interfaz del Controlador de Persistencia.
 * Permite crear implementaciones reales (GestorPersistencia) o mocks para testing.
 * 
 * @author Equipo PROP
 * @version 1.0
 */
public interface IGestorPersistencia {
    
    // ==================== GESTIÓN DE USUARIOS ====================
    
    /**
     * Guarda un usuario en el sistema de persistencia.
     * 
     * @param u Usuario a guardar
     */
    void guardarUsuario(Usuario u);
    
    /**
     * Carga un usuario del sistema de persistencia.
     * 
     * @param nomUsuario Nombre del usuario
     * @return Usuario cargado, o null si no existe
     */
    Usuario cargarUsuario(String nomUsuario);
    
    /**
     * Elimina un usuario del sistema de persistencia.
     * 
     * @param nomUsuario Nombre del usuario a eliminar
     */
    void eliminarUsuario(String nomUsuario);
    
    // ==================== GESTIÓN DE ENCUESTAS ====================
    
    /**
     * Guarda una encuesta en el sistema de persistencia.
     * 
     * @param e Encuesta a guardar
     */
    void guardarEncuesta(Encuesta e);
    
    /**
     * Carga una encuesta del sistema de persistencia.
     * 
     * @param idEncuesta ID de la encuesta
     * @return Encuesta cargada, o null si no existe
     */
    Encuesta cargarEncuesta(int idEncuesta);
    
    /**
     * Elimina una encuesta del sistema de persistencia.
     * 
     * @param idEncuesta ID de la encuesta a eliminar
     */
    void eliminarEncuesta(int idEncuesta);
    
    // ==================== GESTIÓN DE PREGUNTAS ====================
    
    /**
     * Guarda una pregunta en el sistema de persistencia.
     * 
     * @param p Pregunta a guardar
     */
    void guardarPregunta(Pregunta p);
    
    /**
     * Carga una pregunta del sistema de persistencia.
     * 
     * @param idPregunta ID de la pregunta
     * @return Pregunta cargada, o null si no existe
     */
    Pregunta cargarPregunta(int idPregunta);
    
    /**
     * Elimina una pregunta del sistema de persistencia.
     * 
     * @param idPregunta ID de la pregunta a eliminar
     */
    void eliminarPregunta(int idPregunta);
    
    // ==================== GESTIÓN DE RESPUESTAS ====================
    
    /**
     * Guarda una respuesta en el sistema de persistencia.
     * 
     * @param r Respuesta a guardar
     */
    void guardarRespuesta(Respuesta r);
    
    /**
     * Carga una respuesta del sistema de persistencia.
     * 
     * @param idRespuesta ID de la respuesta
     * @return Respuesta cargada, o null si no existe
     */
    Respuesta cargarRespuesta(int idRespuesta);
    
    /**
     * Elimina una respuesta del sistema de persistencia.
     * 
     * @param idRespuesta ID de la respuesta a eliminar
     */
    void eliminarRespuesta(int idRespuesta);
}
