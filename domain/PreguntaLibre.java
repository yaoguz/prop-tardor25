package domain;

public class PreguntaLibre extends Pregunta {
	
	/** Atributos **/
	
	//No hay


	/** Constructora **/
	
	public PreguntaLibre(Encuesta encuesta, Int id, String enunciado) {
		super(encuesta, id, enunciado);
	}
	
	/** Métodos públicos **/

	@override public String tipoPregunta() {
		return "Libre";
	}
}