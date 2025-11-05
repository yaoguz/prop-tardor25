
package domaincontrollers;

import java.util.HashMap;
import java.util.Map;
import domain.Encuesta;
import domain.Pregunta;

/**
 * Controlador de Pregunta
 */

public class CtrlPregunta {

	/**
	 * Implementación del patrón de diseño "Singleton", con el objetivo de que
	 * haya una única instancia de esta clase en el sistema. En este caso, esta
	 * propiedad es deseada ya que esta classe no tiene estado (astributos no
	 * estáticos). Para lograrlo, declaramos la constructora como privada y
	 * añadimos una operación estática que retorne siempre la misma instancia.
	 * Para acceder a esta instancia lo haremos mediante la llamada
	 * CtrlPregunta.getInstance();
	 **/

	
	/**
	 * Atributo que guarda la única instancia de la clase.
	 */
	private static CtrlPregunta singletonObject;
	
	/**
	 * Mapa de preguntas, cuyas llaves son sus identificadores.
	 */
	private Map<Integer,Pregunta> preguntas;
	
	/**
	 * Entero que guarda el identificador de la siguiente pregunta que se cree.
	 */
	private int nextId;

	/**
	 * Método que devuelve la instancia de CtrlPregunta. Si es la primera vez que se llama a la función en esta ejecución, se crea.
	 * @return la instancia única de CtrlPregunta
	 */
	public static CtrlPregunta getInstance() {
		if (singletonObject == null)
			singletonObject = new CtrlPregunta() {
			};
		return singletonObject;
	}

	/**
	 * Se crea un CtrlPregunta sin preguntas y con nextId a 0.
	 */
	private CtrlPregunta() {
		preguntas = new HashMap<Integer, Pregunta>();
		nextId = 0;
	}

	/**
	 * Se crea una nueva pregunta de tipo libre con el nextId como idPregunta, se añade al mapa de preguntas y se devuelve el idPregunta asignado.
	 * @param idEncuesta identificador de la encuesta a la cual pertenece la pregunta.
	 * @param enunciado el enunciado de la pregunta.
	 * @return idPregunta de la pregunta creada.
	 * @throws IllegalArgumentException si el enunciado está vacio o es null.
	 */
	public int crearPreguntaLibre(int idEncuesta, String enunciado) throws IllegalArgumentException {
		int idPregunta = nextId;
		nextId++;
		Pregunta p = new PreguntaLibre(idEncuesta, idPregunta, enunciado);
		preguntas.put(idPregunta,p);
		return idPregunta;
	}

	/**
	 * Se crea una nueva pregunta de tipo numérica con el nextId como idPregunta, se añade al mapa de preguntas y se devuelve el idPregunta asignado.
	 * @param idEncuesta identificador de la encuesta a la cual pertenece la pregunta.
	 * @param enunciado el enunciado de la pregunta.
	 * @param min el valor numérico mínimo que pueden tener las respuestas de la pregunta.
	 * @param max el valor numérico máximo que pueden tener las respuestas de la pregunta.
	 * @return idPregunta de la pregunta creada.
	 * @throws IllegalArgumentException si el enunciado está vacio o es null.
	 */
	public int crearPreguntaNumerica(int idEncuesta, String enunciado, int min, int max) throws IllegalArgumentException {
		int idPregunta = nextId;
		nextId++;
		Pregunta p = new PreguntaNumerica(idEncuesta, idPregunta, enunciado, min, max);
		preguntas.put(idPregunta,p);
		return idPregunta;
	}

	/**
	 * Se crea una nueva pregunta de tipo opción múltiple con el nextId como idPregunta, se añade al mapa de preguntas y se devuelve el idPregunta asignado.
	 * @param idEncuesta identificador de la encuesta a la cual pertenece la pregunta.
	 * @param enunciado el enunciado de la pregunta.
	 * @return idPregunta de la pregunta creada.
	 * @throws IllegalArgumentException si el enunciado está vacio o es null.
	 */
	public int crearPreguntaOpcionMultiple(int idEncuesta, String enunciado) throws IllegalArgumentException {
		int idPregunta = nextId;
		nextId++;
		Pregunta p = new PreguntaOpcionMultiple(idEncuesta, idPregunta, enunciado);
		preguntas.put(idPregunta,p);
		return idPregunta;
	}

	/**
	 * Se crea una nueva pregunta de tipo opción única con el nextId como idPregunta, se añade al mapa de preguntas y se devuelve el idPregunta asignado.
	 * @param idEncuesta identificador de la encuesta a la cual pertenece la pregunta.
	 * @param enunciado el enunciado de la pregunta.
	 * @param ordenada indica si la pregunta es ordenada. 
	 * @return idPregunta de la pregunta creada.
	 * @throws IllegalArgumentException si el enunciado está vacio o es null.
	 */
	public int crearPreguntaOpcionUnica(int idEncuesta, String enunciado, boolean ordenada) throws IllegalArgumentException {
		int idPregunta = nextId;
		nextId++;
		Pregunta p = new PreguntaOpcionUnica(idEncuesta, idPregunta, enunciado, ordenada);
		preguntas.put(idPregunta,p);
		return idPregunta;
	}

	/**
	 * Se obtiene la instancia de Pregunta con el id pasasdo por parámetro.
	 * @param idPregunta id de la Pregunta a obtener.
	 * @return la Pregunta cuyo identificador se ha pasado por parámetro.
	 * @throws IllegalArgumentException si no existe una instancia de Pregunta con identificador idPregunta.
	 */
	public Pregunta getPregunta(int idPregunta) {
		Pregunta p = preguntas.get(idPregunta);
		if (p == null) {
			throw new IllegalArgumentException("No existe la pregunta");
		}
		return p;
	}

	/**
	 * Se elimina del mapa la pregunta pasada por parámetro.
	 * @param p la Pregunta a eliminar
	 */
	public void eliminarPregunta(Pregunta p) throws IllegalArgumentException{
		boolean b = preguntas.remove(p.getIdPregunta);
		if(!b) throw new IllegalArgumentException("No existe la pregunta a eliminar");
	}
}

