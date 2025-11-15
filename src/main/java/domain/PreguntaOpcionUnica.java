package domain;
import java.util.ArrayList;

/**
 * Pregunta de tipo de respuesta de escoger entre varias opciones.
 * 
 * RESPONSABILIDADES:
 * - Indicar que una pregunta es de tipo de opción única.
 * - Almacenar los datos específicos para el tipo de opción única respectivos a la pregunta.
 * - Proporcionar respuestas de tipo opción única al controlador de respuesta.
 * 
 * @author Denis Roca
 */

public class PreguntaOpcionUnica extends Pregunta {
	
	/** Atributos **/
	
	/**
	 * Lista de Strings que guarda los textos de las múltiples opciones de la pregunta.
	 */
	private ArrayList<String> opciones;

	/**
	 * Booleano para indicar si las opciones de la pregunta tienen un orden.
	 */
	private boolean ordenada;


	/** Constructora **/
	/**
	 * Se crea una pregunta de opción única llamando a la superclase y se inicializa la lista de opciones.
	 * @param idEncuesta el identificador único de la encuesta en la que se crea la pregunta.
	 * @param idPregunta el identificador de la pregunta.
	 * @param enunciado el enunciado de la pregunta.
	 * @throws IllegalArgumentException si el enunciado está vacio o es null.
	 */
	public PreguntaOpcionUnica(int idEncuesta, int idPregunta, String enunciado, boolean ordenada) throws IllegalArgumentException {
		super(idEncuesta, idPregunta, enunciado);
		opciones = new ArrayList<String>();
		this.ordenada = ordenada;
	}
	
	/** Métodos públicos **/

	/**
	 * Añade una opción al conjunto de opciones ded la pregunta.
	 * @param opcion el texto de la opción a añadir.
	 */
	@Override 
	public void addOpcion(String opcion) throws IllegalArgumentException {
		if(opcion == null || opcion == "") throw new IllegalArgumentException("opción no válida");
		this.opciones.add(opcion);
	}
	
	/**
	 * Devuelve todas las opciones de la pregunta.
	 * @return la lista de opciones de la pregunta
	 */
	@Override public ArrayList<String> getOpciones() {
		return opciones;
	}
	
	/**
	 * Devuelve si las opciones siguen un orden.
	 * @return booleano indicando si es ordenada o no
	 */
	@Override public boolean esOrdenada() {
		return ordenada;
	}

	/**
	 * Devuelve el número de opciones de la pregunta.
	 * @return entero indicando el número de opciones que tiene la pregunta
	 */
	@Override 
	public int getCantidad() {
		return opciones.size();
	}

	/**
	 * Devuelve el tipo de pregunta.
	 * @return el string "opcion_unica".
	 */
	@Override 
	public String tipoPregunta() {
		return "opcion_unica";
	}
	
	/**
	 * Factory method: Crea una RespuestaOpcionUnica.
	 * 
	 * @param <T> Tipo de la respuesta (debe ser Integer)
	 * @param idRespuesta ID único de la respuesta
	 * @param nomUsuario Nombre del usuario que responde
	 * @param respuesta Contenido de la respuesta (índice de la opción seleccionada)
	 * @return RespuestaOpcionUnica creada
	 */
	@Override
	public <T> Respuesta crearRespuesta(int idRespuesta, String nomUsuario, T respuesta) {
		return new RespuestaOpcionUnica(idRespuesta, nomUsuario, getIdPregunta(), (Integer) respuesta);
	}
}
