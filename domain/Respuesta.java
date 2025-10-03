package domain;

public abstract class Respuesta {

    /** Atributos **/

    protected String nomUsuario;
    protected int idEncuesta;
    protected int idPregunta;

    /** Constructora **/

    public Respuesta(String nomUsuario, int idEncuesta, int idPregunta) {
        this.nomUsuario = nomUsuario;
        this.idEncuesta = idEncuesta;
        this.idPregunta = idPregunta;
    }

    /** Métodos públicos **/
    
    public abstract Object getRespuesta();

}