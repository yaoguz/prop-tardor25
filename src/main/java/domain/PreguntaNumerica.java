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
	 * @throws IllegalArgumentException si el mínimo es igual o mayor al máximo.
	 * @throws IllegalArgumentException si el enunciado está vacio o es null.
	 */
	public PreguntaNumerica(int idEncuesta, int idPregunta, String enunciado, int rangoInf, int rangoSup) throws IllegalArgumentException {
		super(idEncuesta, idPregunta, enunciado);
		if(rangoInf >= rangoSup) throw new IllegalArgumentException("La pregunta tiene una o menos posibles respuestas diferentes");
		this.rangoInf = rangoInf;
		this.rangoSup = rangoSup;
	}
	
	/** Métodos públicos **/
	
	/**
	 * Devuelve el valor mínimo que pueden tener las respuestas de la pregunta.
	 */
	@Override public int getRangoInf() {
		return rangoInf;
	}

	/**
	 * Devuelve el valor máximo que pueden tener las respuestas de la pregunta.
	 */
	@Override public int getRangoSup() {
		return rangoSup;
	}

	/**
	 * Devuelve el tipo de pregunta.
	 * @return el string "Numerica".
	 */
	@Override 
	public String tipoPregunta() {
		return "Numerica";
	}
	
	/**
	 * Factory method: Crea una RespuestaNumerica.
	 * 
	 * @param <T> Tipo de la respuesta (debe ser Double)
	 * @param idRespuesta ID único de la respuesta
	 * @param nomUsuario Nombre del usuario que responde
	 * @param respuesta Contenido de la respuesta (Double)
	 * @return RespuestaNumerica creada
	 */
	@Override
	public <T> Respuesta crearRespuesta(int idRespuesta, String nomUsuario, T respuesta) {
		return new RespuestaNumerica(idRespuesta, nomUsuario, getIdPregunta(), (Double) respuesta);
	}
}
