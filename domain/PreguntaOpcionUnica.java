package domain;

import java.util.ArrayList;

public class PreguntaOpcionUnica extends Pregunta {
	
	/** Atributos **/
	
	private ArrayList<String> opciones;


	/** Constructora **/
	
	public PreguntaLibre(Encuesta encuesta, Int id, String enunciado) {
		super(encuesta, id, enunciado);
		opciones = new ArrayList<String>()
	}
	
	/** Métodos públicos **/

	public void addOpcion(String opcion){
		this.opciones.add(opcion);
	}
	
	@override public ArrayList<String> getOpciones() {
		return opciones;
	}

	@override public String tipoPregunta() {
		return "OpcionUnica";
	}
}
