package domain;

import java.util.ArrayList;

public class Encuesta {

    /** Atributos **/
    private int idEncuesta;
    private String nombreEncuesta;
    private ArrayList<Pregunta> preguntas = new ArrayList<>();
    private Usuario creador;

    /** Constructora **/
    public Encuesta(int idEncuesta, String nombreEncuesta, Usuario creador) {
        this.idEncuesta = idEncuesta;
        this.nombreEncuesta = nombreEncuesta;
        this.creador = creador;
        if (nombreEncuesta == null) {
            throw new IllegalArgumentException("Nombre de encuesta vacio");
        }
        
    }

    /** Metodos **/
    public int getidEncuesta() {
        return idEncuesta;
    }

    public ArrayList<Pregunta> getPreguntas() {
        return preguntas;
    }

    public Usuario getCreador() {
        return creador;
    }

    public void addPregunta(Pregunta pregunta) {
        this.preguntas.add(pregunta);
    }

    public ArrayList<ArrayList<Respuesta>> verRespuestas() {

        ArrayList<ArrayList<Respuesta>> respuestas = new ArrayList<>(); 
        for (Pregunta p : preguntas) {
            respuestas.add(p.getRespuestas());
        }
        return respuestas;
    }
    //ver estadisticas ????????

}