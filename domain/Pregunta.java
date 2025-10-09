package domain;

import java.util.ArrayList;

public abstract class Pregunta {
	
	/** Atributos **/
	
	private Int idEncuesta;
	private Int id;
	private String enunciado;
	private ArrayList<Int> idRespuestas;


	/** Constructora **/
	
	public Pregunta(Encuesta encuesta, Int id, String enunciado) {
		this.encuesta = encuesta;
		this.id = id;
		this.enunciado = enunciado;
		this.respuestas = new ArrayList<Respuesta>();
	}
	
	/** Métodos públicos **/

	public Encuesta getEncuesta() {
		return this.encuesta;
	}
	
	public Int getIdEncuesta() {
		return encuesta.getId();
	}
	
	public Int getId() {
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
	public Int getNumMaxOpc() {
		return -1;
	}
	
	//rangoInf (de numerica)
	public Int getRangoInf() {
		return -1;
	}
	
	//rangoSup (de numerica)
	public Int getRangoSup() {
		return -1;
	}
	
	//Operación abstracta (cada subclase devuelve su tipo)
	public abstract String tipoPregunta();
}
