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
	 */
	public PreguntaOpcionMultiple(int idEncuesta, int idPregunta, String enunciado) {
		super(idEncuesta, idPregunta, enunciado);
		opciones = new ArrayList<String>()
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
	@override public ArrayList<String> getOpciones() {
		return opciones;
	}
	
	/**
	 * Devuelve el número máximo de opciones a escoger de la pregunta.
	 * @return el número máximo de opciones que puede seleccionar la respuesta.
	 */
	@override public int getNumMaxOpc() {
		return numMaxOpc;
	}

	/**
	 * Devuelve el tipo de pregunta.
	 * @return el string "OpcionMultiple".
	 */
	@override public String tipoPregunta() {
		return "OpcionMultiple";
	}
}
