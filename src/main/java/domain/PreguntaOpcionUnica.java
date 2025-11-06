package domain;

/**
 * Clase PreguntaOpcionMultiple
 */

import java.util.ArrayList;

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
		opciones = new ArrayList<String>()
		this.ordenada = ordenada;
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
	 * Devuelve si las opciones siguen un orden.
	 * @return booleano indicando si es ordenada o no
	 */
	@Override public boolean esOrdenada() {
		return ordenada;
	}

	/**
	 * Devuelve el tipo de pregunta.
	 * @return el string "OpcionUnica".
	 */
	@Override 
	public String tipoPregunta() {
		if(ordenada) return "OpcionUnicaOrdenada";
		else return "OpcionUnicaNoOrdenada";
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
