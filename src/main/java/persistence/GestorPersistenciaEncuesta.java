package persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import domain.Encuesta;
import domain.Respuesta;

/**
 * Gestor de persistencia de encuestas con cache LRU.
 * 
 * Mantiene una cache limitada de encuestas más recientemente usadas
 * y delega operaciones de disco a CtrlPersistenciaEncuesta.
 * 
 * RESPONSABILIDADES:
 * - Gestionar cache LRU de encuestas (máximo 16 entradas)
 * - Coordinar con CtrlPersistenciaEncuesta para operaciones de disco
 * - Gestionar IDs secuenciales para nuevas encuestas
 * - Proporcionar operaciones CRUD con cache transparente
 * - Implementar patrón Singleton
 * 
 * ATRIBUTOS PRINCIPALES:
 * - encuestas: LinkedHashMap con política LRU (accessOrder=true)
 * - ctrlFicheroEncuesta: Controlador de persistencia en disco
 * - nextIdEncuesta: ID para la próxima encuesta a crear
 * - CACHE_LIMIT: Límite de 16 encuestas en cache
 * 
 * ESTRUCTURA:
 * - Cache: LinkedHashMap con removeEldestEntry para LRU automático
 * - Disco: Archivos JSON en data/encuestas/{idEncuesta}.json
 * - IDs: Gestionados en data/nextId.json
 * 
 * @author Alessandro Jin
 */
public class GestorPersistenciaEncuesta {
    private static GestorPersistenciaEncuesta singletonInstance;

    // Límites de cache
    private static final int CACHE_LIMIT = 16;

    // Cache con LinkedHashMap (accessOrder=true para LRU tracking)
    private final LinkedHashMap<Integer, Encuesta> encuestas;

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
            protected boolean removeEldestEntry(Map.Entry<Integer, Encuesta> eldest){
                return size() > CACHE_LIMIT;
            }
        };

        // Inicializar controladores de disco
        ctrlFicheroEncuesta = CtrlPersistenciaEncuesta.getInstance();

        nextIdEncuesta = ctrlFicheroEncuesta.getNextId();
    }

    // ==================== MÉTODOS DE ENCUESTA ====================

    /**
     * Busca y carga una encuesta desde disco a la cache.
     * 
     * @param id ID de la encuesta
     * @return Encuesta cargada desde disco
     */
    Encuesta sacarEncuestaFichero(int id) {

        Encuesta e = ctrlFicheroEncuesta.getEncuesta(id);
        encuestas.put(id, e);
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
    public ArrayList<Encuesta> getAllEncuestas() {
        return ctrlFicheroEncuesta.getAllEncuestas();
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
            try {
                 sacarEncuestaFichero(id);
            }            
            catch (Exception e) {
                return false;
            }
           
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
        if (!encuestas.containsKey(id)) sacarEncuestaFichero(id);
        
        encuestas.put(id, e); //sobre escribe

        ctrlFicheroEncuesta.actualizarEncuesta(e);
    }

    /**
     * Elimina una encuesta por su ID.
     * 
     * @param id ID de la encuesta a eliminar
     * @throws IllegalArgumentException si la encuesta no existe
     */
    public void eliminarEncuesta(int id) {
        if (encuestas.containsKey(id)) encuestas.remove(id);
        ctrlFicheroEncuesta.eliminarEncuesta(id);
    }

    /**
     * Obtiene todas las respuestas de una encuesta.
     * 
     * @param id ID de la encuesta
     * @return Lista con todas las respuestas
     */
    public ArrayList<Respuesta> getRespuestasEncuesta (int id) {

        return ctrlFicheroEncuesta.getRespuestasEncuesta(id);

    }

    /**
     * Guarda el siguiente ID disponible en disco.
     */
    public void guardarNextId () {
        ctrlFicheroEncuesta.guardarNextId(nextIdEncuesta);
    }
}