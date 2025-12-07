package persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import domain.Respuesta;

/**
 * Cache de Respuesta
 * 
 * @author Yaoyao
 */

public class GestorPersistenciaRespuesta {

    private static GestorPersistenciaRespuesta singletonInstance;

    // Límites de cache
    private static final int CACHE_LIMIT = 64;

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
            singletonInstance = new GestorPersistenciaRespuesta();
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
    }

    // ==================== MÉTODOS DE RESPUESTA ====================

    /**
     * Buscar la Respuesta en el fichero
     * 
     * @throws IllegalArgumentException si la respuesta no existe
     */
    Respuesta sacarRespuestaFichero(int idR) {
        Respuesta r = ctrlFicheroRespuesta.getRespuesta(idR); // puede lanzar exc no existe
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
     * Elimina una respuesta por su ID.
     * 
     * @param id ID de la respuesta a eliminar
     * @throws IllegalArgumentException si la respuesta no existe
     */
    public void eliminarRespuesta(int id) {
        if (respuestas.containsKey(id)) respuestas.remove(id);
        ctrlFicheroRespuesta.eliminarRespuesta(id);
    }

    public void guardarNextId () {
        ctrlFicheroRespuesta.guardarNextId(nextIdRespuesta);
    }
}