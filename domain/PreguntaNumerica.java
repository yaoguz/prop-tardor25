package domain;

/**
 * Clase PreguntaNumerica
 */

public class PreguntaNumerica extends Pregunta {
	
	/** Atributos **/
	/**
	 * Entero que guarda el valor mínimo del rango numérico que pueden tener las respuestas de la pregunta.
	 */
	private int rangoInf;
	/**
	 * Entero que guarda el valor máximo del rango numérico que pueden tener las respuestas de la pregunta.
	 */
	private int rangoSup;


	/** Constructora **/
	/**
	 * Se crea una pregunta numérica llamando a la superclase y se asignan los valores máximo y mínimo para sus respuestas.
	 * @param idEncuesta el identificador único de la encuesta en la que se crea la pregunta.
	 * @param idPregunta el identificador de la pregunta.
	 * @param enunciado el enunciado de la pregunta.
	 * @param rangoInf el valor mínimo que puede tener la respuesta de la pregunta.
	 * @param rangoSup el valor máximo que puede tener la respuesta de la pregunta.
	 */
	public PreguntaNumerica(int idEncuesta, int idPregunta, String enunciado, int rangoInf, int rangoSup) {
		super(idEncuesta, idPregunta, enunciado);
		this.rangoInf = rangoInf;
		this.rangoSup = rangoSup;
	}
	
	/** Métodos públicos **/
	
	/**
	 * Devuelve el valor mínimo que pueden tener las respuestas de la pregunta.
	 */
	@override public int getRangoInf() {
		return -1;
	}

	/**
	 * Devuelve el valor máximo que pueden tener las respuestas de la pregunta.
	 */
	@override public int getRangoSup() {
		return -1;
	}

	/**
	 * Devuelve el tipo de pregunta.
	 * @return el string "Numerica".
	 */
	@override public String tipoPregunta() {
		return "Numerica";
	}
}
