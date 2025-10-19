package domain;

import java.util.ArrayList;

/**
 * Clase Pregunta
 */

public abstract class Pregunta {
	
	/** Atributos **/
	/**
	 * Entero que guarda el identificador de la Encuesta a la que pertenece la pregunta.
	 */
	private int idEncuesta;

	/**
	 * Entero que guarda el identificador de la pregunta.
	 */
	private int id;

	/**
	 * String que guarda el enunciado de la pregunta.
	 */
	private String enunciado;

	/**
	 * Lista de enteros que guarda los identificadores de las respuestas a la pregunta.
	 */
	private ArrayList<Integer> idRespuestas;


	/** Constructora **/
	/**
	 * Se crea una pregunta con un nuevo id, un enunciado, el identificador de la encuesta a la que pertenece e inicializa la lista de identificadores de respuestas.
	 * @param idEncuesta el identificador único de la encuesta en la que se crea la pregunta.
	 * @param id el identificador de la respuesta.
	 * @param enunciado el enunciado de la pregunta.
	 * @throws IllegalArgumentException si el enunciado no es válido.
	 */
	public Pregunta(int idEncuesta, int id, String enunciado) throws IllegalArgumentException {
		if (enunciado == null or enunciado == "") throw new IllegalArgumentException("Enunciado no válido")
		this.idEncuesta = encuesta;
		this.id = id;
		this.enunciado = enunciado;
		this.idRespuestas = new ArrayList<Integer>();
	}
	
	/** Métodos públicos **/
	/**
	 * Devuelve el identificador de la encuesta de la pregunta.
	 * @return identificador de la encuesta.
	 */
	public int getIdEncuesta() {
		return this.idEncuesta;
	}
	
	/**
	 * Devuelve el identificador de la pregunta.
	 * @return identificador de la pregunta.
	 */
	public int getId() {
		return this.id;
	}
	
	/**
	 * Devuelve el enunciado de la pregunta.
	 * @return enunciado de la pregunta.
	 */
	public String getEnunciado() {
		return this.enunciado;
	}
	
	/**
	 * Devuelve la lista de identificadores de las respuestas de las preguntas
	 * @return lista con identificadores de respuesta.
	 */
	public ArrayList<Integer> getIdRespuestas() {
		return this.idRespuestas;
	}

	public void addRespuesta(Integer r) {
		this.idRespuestas.add(r);
	}

	public void eliminarRespuesta(Integer r) {
		boolean b = this.idRespuestas.remove(r);
		//if(!b) throws
	}
	
	/** Getters de las subclases **/
	
	//opciones (de opcionMuliple y opcionUnica)
	public ArrayList<String> getOpciones() {
		return new ArrayList<String>();
	}
	
	//numMaxOpc (de opcionMultiple)
	public int getNumMaxOpc() {
		return -1;
	}
	
	//rangoInf (de numerica)
	public int getRangoInf() {
		return -1;
	}
	
	//rangoSup (de numerica)
	public int getRangoSup() {
		return -1;
	}
	
	//Operación abstracta (cada subclase devuelve su tipo)
	public abstract String tipoPregunta();
}
