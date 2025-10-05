package domain;

public class Encuesta {

    /** Atributos **/
    private int idEncuesta;
    private String nombreEncuesta;
    private Pregunta[] preguntas;
    private Usuario creador;

    /** Constructora **/
    public Encuesta(int idEncuesta, String nombreEncuesta) {
        this.idEncuesta = idEncuesta;
        this.nombreEncuesta = nombreEncuesta;
    }

    /** Metodos **/
    public int getidEncuesta() {
        return idEncuesta;
    }

    public Pregunta[] getPreguntas() {
        return preguntas;
    }

    public Usuario getCreador() {
        return creador;
    }

}