package domain;

import java.util.ArrayList;

public class PreguntaOpcionMultiple extends Pregunta {
	
	/** Atributos **/
	
	private ArrayList<String> opciones;
	private Int numMaxOpc;


	/** Constructora **/
	
	public PreguntaLibre(Encuesta encuesta, Int id, String enunciado) {
		super(encuesta, id, enunciado);
		opciones = new ArrayList<String>()
	}
	
	/** Métodos públicos **/

	public void addOpcion(String opcion){
		this.opciones.add(opcion);
	}

	@override public String tipoPregunta() {
		return "OpcionMultiple";
	}
}