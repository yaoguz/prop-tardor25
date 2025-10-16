package domain;

import java.util.ArrayList;

public class Encuesta {

    /** Atributos **/
    private int idEncuesta;
    private String titulo;
    private ArrayList<Integer> preguntas = new ArrayList<>();
    private Usuario creador;

    /** Constructora **/
    public Encuesta(int idEncuesta, String titulo, Usuario creador) {
        this.idEncuesta = idEncuesta;
        this.nombreEncuesta = titulo;
        this.creador = creador;
        if (nombreEncuesta == null) {
            throw new IllegalArgumentException("Nombre de encuesta vacio");
        }
        
    }

    /** Metodos **/
    public int getidEncuesta() {
        return idEncuesta;
    }

    public ArrayList<Integer> getIdPreguntas() {
        return preguntas;
    }

    public Usuario getCreador() {
        return creador;
    }

    public void addPregunta(Integer IdPregunta) {
        this.preguntas.add(IdPregunta);
    }

    public ArrayList<ArrayList<Respuesta>> verRespuestas() {

        ArrayList<ArrayList<Respuesta>> respuestas = new ArrayList<>(); 
        for (Pregunta p : preguntas) {
            respuestas.add(p.getRespuestas());
        }
        return respuestas;
    }

}