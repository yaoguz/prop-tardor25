package domain;

import java.util.ArrayList;

public abstract class Pregunta {
	
	/** Atributos **/
	
	private int idEncuesta;
	private int id;
	private String enunciado;
	private ArrayList<Integer> idRespuestas;


	/** Constructora **/
	
	public Pregunta(Encuesta encuesta, int id, String enunciado) {
		this.encuesta = encuesta;
		this.id = id;
		this.enunciado = enunciado;
		this.respuestas = new ArrayList<Respuesta>();
	}
	
	/** Métodos públicos **/

	public Encuesta getEncuesta() {
		return this.encuesta;
	}
	
	public int getIdEncuesta() {
		return encuesta.getId();
	}
	
	public int getId() {
		return this.id;
	}
	
	public String getEnunciado() {
		return this.enunciado;
	}
	
	public ArrayList<Respuesta> getRespuestas() {
		return this.respuestas;
	}

	public void addRespuesta(Respuesta r) {
		this.respuestas.add(r);
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
