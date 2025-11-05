package domain;

/**
 * Clase PreguntaLibre
 */

public class PreguntaLibre extends Pregunta {
	
	/** Atributos **/
	
	//No hay


	/** Constructora **/
	/**
	 * Se crea una pregunta libre llamando a la superclase
	 * @param idEncuesta el identificador único de la encuesta en la que se crea la pregunta.
	 * @param idPregunta el identificador de la pregunta.
	 * @param enunciado el enunciado de la pregunta.
	 * @throws IllegalArgumentException si el enunciado está vacio o es null.
	 */
	public PreguntaLibre(int idEncuesta, int idPregunta, String enunciado) throws IllegalArgumentException {
		super(idEncuesta, idPregunta, enunciado);
	}
	
	/** Métodos públicos **/
	/**
	 * Devuelve el tipo de pregunta.
	 * @return el string "Libre".
	 */
	@override public String tipoPregunta() {
		return "Libre";
	}
}