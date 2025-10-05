package domain;

import java.util.ArrayList;

public class Encuesta {

    /** Atributos **/
    private Int idEncuesta;
    private String nombreEncuesta;
    private ArrayList<Pregunta> preguntas;
    private Usuario creador;

    /** Constructora **/
    public Encuesta(Int idEncuesta, String nombreEncuesta, Usuario creador) {
        this.idEncuesta = idEncuesta;
        this.nombreEncuesta = nombreEncuesta;
        this.creador = creador;
    }

    /** Metodos **/
    public Int getidEncuesta() {
        return idEncuesta;
    }

    public Pregunta[] getPreguntas() {
        return preguntas;
    }

    public Usuario getCreador() {
        return creador;
    }

    public void addPregunta(Pregunta pregunta) {
        this.preguntas.add(pregunta);
    }

    //ver respuestas ???
    //ver estadisticas ????????

}