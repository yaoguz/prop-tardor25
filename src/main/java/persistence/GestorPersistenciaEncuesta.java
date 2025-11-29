package persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import domain.Encuesta;

/**
 * Cache del Encuesta
 */

/**
 * @author Alessandro Jin
 */

public class GestorPersistenciaEncuesta {
    private static GestorPersistenciaEncuesta singletonInstance;

    // Límites de cache
    private static final int CACHE_LIMIT = 64;

    // Cache con LinkedHashMap (accessOrder=true para LRU tracking)
    private final LinkedHashMap<String, Encuesta> encuestas;

    // Controlador de persistencia usuario en disco
    private final CtrlPersistenciaEncuesta ctrlFicheroEncuesta;

    /** ID para la próxima encuesta a crear */
    private int nextIdEncuesta;

    /**
     * Obtiene instancia única (patrón Singleton).
     * 
     * @return Instancia única de GestorPersistenciaEncuesta
     */
    public static GestorPersistenciaEncuesta getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new GestorPersistenciaEncuesta();
        }
        return singletonInstance;
    }

    private GestorPersistenciaEncuesta() {
        // Inicializar LinkedHashMaps con accessOrder=true para tracking LRU
        encuestas = new LinkedHashMap<>(CACHE_LIMIT, 2.0f, true){
            protected boolean removeEldestEntry(Map.Entry eldest){
                return size() > CACHE_LIMIT;
            }
        };

        // Inicializar controladores de disco
        ctrlFicheroEncuesta = new CtrlPersistenciaEncuesta();

        nextIdEncuesta = ctrlFicheroEncuesta.getId();
    }

    // ==================== MÉTODOS DE ENCUESTA ====================

    /**
     * Buscar la encuesta en el fichero
     */
    Encuesta sacarEncuestaFichero(int id) {

        Encuesta e = ctrlFicheroEncuesta.getEncuesta(id);
        encuesta.guardarEncuesta(e);
        return e;
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

        ctrlFicheroEncuesta.guardarEncuesta(e);
      
        encuestas.put(id, e);
    }

    /**
    * Devuelve una lista con todas las encuestas almacenadas.
    *
    * @return Lista con todas las encuestas existentes
    */
    public ArrayList<Encuesta> getEncuestas() {
        return ctrlFicheroEncuesta.getEncuestas();
    }

    /**
     * Obtiene una encuesta por su ID.
     * 
     * @param id ID de la encuesta
     * @return Encuesta correspondiente, o null si no existe
     */
    public Encuesta getEncuesta(int id) {
        Encuesta e = encuestas.get(id);
        if (e == null) e = sacarEncuestaFichero(id);
        return e;
    }

    /**
     * Verifica si existe una encuesta con el ID dado.
     * 
     * @param id ID de la encuesta
     * @return true si existe, false en caso contrario
     */
    public boolean existeEncuesta(int id) {
        if (!encuestas.containsKey(id)) {
            if (!ctrlFicheroEncuesta.existeEncuesta(id)) return false;
            encuestas.sacarEncuestaFichero(id);
        }
        return true;
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
     * Actualiza una encuesta existente.
     * 
     * @param e Encuesta a actualizar
     */
    public void actualizarEncuesta(Encuesta e) {
        int id = e.getIdEncuesta();
        if (!encuestas.containsKey(id)) encuestas.sacarEncuestaFichero(id);
        
        encuestas.put(id, e); //sobre escribe

        ctrlFicheroEncuesta.actualizarEncuesta(e);
    }

    /**
    * Elimina una encuesta por el objeto Encuesta.
    *
    * @param e Encuesta a eliminar
    * @throws IllegalArgumentException si la encuesta no existe
    */
    public void eliminarEncuesta(Encuesta e) {
        Integer id = e.getIdEncuesta();
        if(encuestas.containsKey(id)) encuestas.remove(id);
        ctrlFicheroEncuesta.eliminarEncuesta(e);
    }

    /**
     * Elimina una encuesta por su ID.
     * 
     * @param id ID de la encuesta a eliminar
     * @throws IllegalArgumentException si la encuesta no existe
     */
    public void eliminarEncuesta(int id) {
        if (encuestas.containsKey(id)) encuestas.remove(id);
        ctrlFicheroEncuesta.eliminarEncuesta(e);
    }
}