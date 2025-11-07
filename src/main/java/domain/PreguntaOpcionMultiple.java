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
	 */
	public void addOpcion(String opcion){
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
	@Override public void setNumMaxOpc(int numMaxOpc) {
		this.numMaxOpc = numMaxOpc;
	}

	/**
	 * Devuelve el tipo de pregunta.
	 * @return el string "OpcionMultiple".
	 */
	@Override 
	public String tipoPregunta() {
		return "OpcionMultiple";
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
