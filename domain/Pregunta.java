package domain;

import java.util.ArrayList;

public abstract class Pregunta {
	
	/** Atributos **/
	
	private Encuesta encuesta;
	private Int id;
	private String enunciado;
	private ArrayList<Respuesta> respuestas;


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
	
	//Operación abstracta (cada subclase devuelve su tipo)
	public abstract String tipoPregunta();
}