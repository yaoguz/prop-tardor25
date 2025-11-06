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
	private int idPregunta;

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
	 * @param idPregunta el identificador de la pregunta.
	 * @param enunciado el enunciado de la pregunta.
	 * @throws IllegalArgumentException si el enunciado está vacio o es null.
	 */
	public Pregunta(int idEncuesta, int idPregunta, String enunciado) throws IllegalArgumentException {
		if (enunciado == null || enunciado == "") throw new IllegalArgumentException("Enunciado no válido");
		this.idEncuesta = idEncuesta;
		this.idPregunta = idPregunta;
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
	public int getIdPregunta() {
		return this.idPregunta;
	}
	
	/**
	 * Devuelve el enunciado de la pregunta.
	 * @return enunciado de la pregunta.
	 */
	public String getEnunciado() {
		return this.enunciado;
	}
	
	/**
	 * Devuelve la lista de identificadores de las respuestas de las preguntas.
	 * @return lista de los identificadores de las respuestas de la pregunta.
	 */
	public ArrayList<Integer> getIdRespuestas() {
		return this.idRespuestas;
	}

	/**
	 * Añade un identificador de respuesta a la lista de respuestas.
	 * @param r el identificador de la respuesta a añadir.
	 */
	public void guardarIdRespuesta(int r) {
		this.idRespuestas.add(r);
	}
	
	/**
	 * @deprecated Use guardarIdRespuesta instead
	 */
	public void addRespuesta(Integer r) {
		guardarIdRespuesta(r);
	}

	/**
	 * Elimina un identificador de respuesta de la lista de respuestas.
	 * @param r el identificador de la respuesta a eliminar.
	 */
	public void eliminarIdRespuesta(int r) throws IllegalArgumentException {
		boolean b = this.idRespuestas.remove(Integer.valueOf(r));
		if(!b) throw new IllegalArgumentException("La pregunta no tiene la respuesta del parámetro");
	}
	
	/**
	 * @deprecated Use eliminarIdRespuesta instead
	 */
	public void eliminarRespuesta(Integer r) throws IllegalArgumentException {
		eliminarIdRespuesta(r);
	}
	
	/**
	 * FACTORY METHOD (polimórfico): Crea una respuesta del tipo correspondiente.
	 * 
	 * Cada subclase implementa este método para crear su tipo de respuesta.
	 * Esto respeta el principio Abierto/Cerrado: si se añade un nuevo tipo de pregunta,
	 * solo hay que implementar crearRespuesta() en la nueva subclase.
	 * 
	 * @param <T> Tipo de la respuesta
	 * @param idRespuesta ID único de la respuesta
	 * @param nomUsuario Nombre del usuario que responde
	 * @param respuesta Contenido de la respuesta
	 * @return Respuesta del tipo correspondiente
	 */
	public abstract <T> Respuesta crearRespuesta(int idRespuesta, String nomUsuario, T respuesta);
	
	/** Operaciones de las subclases **/
	
	//opciones (de opcionMuliple y opcionUnica)
	/**
	 * Devuelve una lista de Strings vacía si no es del tipo de subclase correspondiente.
	 * @return una lista de Strings vacía.
	 */
	public ArrayList<String> getOpciones() {
		return new ArrayList<String>();
	}
	
	//numMaxOpc (de opcionMultiple)
	/**
	 * Devuelve un -1 si no es del tipo de subclase correspondiente.
	 * @return un -1.
	 */
	public int getNumMaxOpc() {
		return -1;
	}
	
	
	/**
	 * No hace nada si no es del tipo de subclase correspondiente.
	 */
	public void setNumMaxOpc(numMaxOpc) {}
	//rangoInf (de numerica)
	/**
	 * Devuelve un -1 si no es del tipo de subclase correspondiente.
	 * @return un -1.
	 */
	public int getRangoInf() {
		return -1;
	}
	
	//rangoSup (de numerica)
	/**
	 * Devuelve un -1 si no es del tipo de subclase correspondiente.
	 * @return un -1.
	 */
	public int getRangoSup() {
		return -1;
	}
	
	/**
	 * Devuelve null si no es del tipo de subclase correspondiente.
	 * @return null
	 */
	public boolean esOrdenada() {
		return null;
	}
	
	//Operación abstracta (cada subclase devuelve su tipo)
	/**
	 * Función abstracta para saber el tipo de la subclase.
	 * @return un String con el tipo de Pregunta.
	 */
	public abstract String tipoPregunta();
}
