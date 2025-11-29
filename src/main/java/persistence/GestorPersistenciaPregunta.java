package persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import domain.Pregunta;
import domain.Usuario;

/**
 * Gestor de persistencia de las preguntas (cache).
 * 
 * @author Denis Roca
 */
public class GestorPersistenciaPregunta {
    /** Atributos **/

    private static GestorPersistenciaPregunta singletonInstance;

    /**
     * Entero que indica el número máximo de preguntas que se guardarán en la 'cache'.
     */
    private static final int CACHE_LIMIT = 64;

    /**
     * Mapa ordenado que simula la cache.
     * La clave es el identificador de la pregunta (id) y el valor es el objeto {@link domain.Pregunta}.
     */
    private final LinkedHashMap<Integer, Pregunta> preguntas;

    /**
     * El controlador de ficheros de las preguntas.
     */
    private final CtrlPersistenciaPregunta ctrlFicheroPregunta;


    /** Constructor y metodos de inicializacion **/

    /**
     * Inicialización de los atributos de la clase.
     */
	private GestorPersistenciaPregunta() {
        preguntas = new LinkedHashMap<Integer, Pregunta>(CACHE_LIMIT, 2.0f, true){
            protected boolean removeEldestEntry(Map.Entry eldest){
                return size() > CACHE_LIMIT;
            }
        };
        ctrlFicheroPregunta = CtrlPersistenciaPregunta.getInstance();
    }

    /**
     * Obtiene la instancia única del gestor (patrón singleton).
     * 
     * @return Instancia única de GestorPersistenciaPregunta
     */
    public static GestorPersistenciaPregunta getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new GestorPersistenciaPregunta();
        }
        return singletonInstance;
    }

    /**
    * Devuelve una lista con todas las preguntas almacenadas.
    * 
    * @param idE es el identificador de la encuesta cuyas preguntas queremos obtener.
    *
    * @return Lista con todas las preguntas existentes
    */
    public ArrayList<Pregunta> getPreguntas() {
        return ctrlFicheroPregunta.getAll();
    }

    /**
     * Busca la Pregunta en los ficheros y hace los cambios pertinentes en la cache.
     * 
     * @return Pregunta correspondiente, o null si no existe
     * @throws IllegalArgumentException si no existe la pregunta con el id especificado
     */
    private Pregunta sacarPreguntaFichero(int id) throws IllegalArgumentException{

        Pregunta p = ctrlFicheroPregunta.getPregunta(id);
        preguntas.put(id, p);

        return p;
    }

    /**
     * Obtiene una pregunta por su ID.
     * 
     * @param id ID de la pregunta
     * @return Pregunta correspondiente, o null si no existe
     * @throws IlleaglArgumentException si la pregunta especificada no existe
     */
    public Pregunta getPregunta(int id) throws IllegalArgumentException {
        Pregunta p = preguntas.get(id);
        
        if (p == null) p = sacarPreguntaFichero(id);

        return p;
    }

    /**
     * Verifica si existe una pregunta con el ID dado.
     * 
     * @param id ID de la pregunta
     * @return true si existe, false en caso contrario
     */
    public boolean existePregunta(int id) {
        if(!preguntas.containsKey(id)){
            if(!CtrlPersistenciaPregunta.existePregunta(id)) return false;
            sacarPreguntaFichero(id);
        }
        return true;
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
     * Elimina una pregunta por su ID.
     * 
     * @param id ID de la pregunta a eliminar
     * @throws IllegalArgumentException si la pregunta no existe
     */
    public void eliminarPregunta(int id) {
        if (preguntas.containsKey(id)) preguntas.remove(id);
        ctrlFicheroPregunta.eliminarPregunta(id);
    }
}

