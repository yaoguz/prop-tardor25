package domain;

public class PreguntaNumerica extends Pregunta {
	
	/** Atributos **/
	
	private Int rangoInf;
	private Int rangoSup;


	/** Constructora **/
	
	public PreguntaLibre(Encuesta encuesta, Int id, String enunciado, Int rangoInf, Int rangoSup) {
		super(encuesta, id, enunciado);
		this.rangoInf = rangoInf;
		this.rangoSup;
	}
	
	/** Métodos públicos **/
	
	@override public Int getRangoInf() {
		return -1;
	}
	
	@override public Int getRangoSup() {
		return -1;
	}

	@override public String tipoPregunta() {
		return "Numerica";
	}
}
