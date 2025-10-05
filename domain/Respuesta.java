package domain;

public abstract class Respuesta {

    /** Atributos **/

    protected Usuario usuario;
    protected Pregunta pregunta;

    /** Constructora **/

    public Respuesta(Usuario usuario, Pregunta pregunta) {
        this.usuario = usuario;
        this.pregunta = pregunta;
    }

    /** Métodos públicos **/
    
    public abstract Object getRespuesta();

    public Usuario getUsuario() {
        return usuario;
    }

    public Pregunta getPregunta() {
        return pregunta;
    }

}