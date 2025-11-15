package domain;

/**
 * Clase PreguntaOpcionMultiple
 */

import java.util.ArrayList;

public class PreguntaOpcionMultiple extends Pregunta {
	
	/** Atributos **/
	
	/**
	 * Lista de Strings que guarda los textos de las múltiples opciones de la pregunta.
	 */
	private ArrayList<String> opciones;

	/**
	 * Entero que guarda el número máximo de opciones seleccionables.
	 */
	private int numMaxOpc;


	/** Constructora **/
	/**
	 * Se crea una pregunta de opción múltiple llamando a la superclase y se inicializa la lista de opciones.
	 * @param idEncuesta el identificador único de la encuesta en la que se crea la pregunta.
	 * @param idPregunta el identificador de la pregunta.
	 * @param enunciado el enunciado de la pregunta.
	 * @throws IllegalArgumentException si el enunciado está vacio o es null.
	 */
	public PreguntaOpcionMultiple(int idEncuesta, int idPregunta, String enunciado) throws IllegalArgumentException {
		super(idEncuesta, idPregunta, enunciado);
		opciones = new ArrayList<String>();
		numMaxOpc = 2;
	}
	
	/** Métodos públicos **/

	/**
	 * Añade una opción al conjunto de opciones ded la pregunta.
	 * @param opcion el texto de la opción a añadir.
	 * @throws IllegalArgumentException si la opción es vacía o es null.
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
	 * Devuelve el número máximo de opciones a escoger de la pregunta.
	 * @return el número máximo de opciones que puede seleccionar la respuesta.
	 */
	@Override public int getNumMaxOpc() {
		return numMaxOpc;
	}
	
	/**
	 * Asigna el número máximo de opciones seleccionables para esta pregunta.
	 */
	@Override public void setNumMaxOpc(int numMaxOpc) throws IllegalArgumentException {
		if(numMaxOpc < 2 || numMaxOpc > opciones.size()) throw new IllegalArgumentException("número máximo de opciones menor a 2 o mayor que el número de opciones");
		this.numMaxOpc = numMaxOpc;
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
	 * @return el string "opcion_multiple".
	 */
	@Override 
	public String tipoPregunta() {
		return "opcion_multiple";
	}
	
	/**
	 * Factory method: Crea una RespuestaOpcionMultiple.
	 * 
	 * @param <T> Tipo de la respuesta (debe ser int[])
	 * @param idRespuesta ID único de la respuesta
	 * @param nomUsuario Nombre del usuario que responde
	 * @param respuesta Contenido de la respuesta (array de índices seleccionados)
	 * @return RespuestaOpcionMultiple creada
	 */
	@Override
	public <T> Respuesta crearRespuesta(int idRespuesta, String nomUsuario, T respuesta) {
		return new RespuestaOpcionMultiple(idRespuesta, nomUsuario, getIdPregunta(), (int[]) respuesta);
	}
}
