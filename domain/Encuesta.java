package domain;

import java.util.ArrayList;

public class Encuesta {

    /** Atributos **/
    private int idEncuesta;
    private String titulo;
    private ArrayList<Integer> IdPreguntas = new ArrayList<>();
    private String creador;

    /** Constructora **/
    /**
     * Se crea uan encuesta con un nuevo idEncuesta, un titulo y el 
     * nombre del usuario creador de esta encuesta
     * @param idEncuesta id unico de cada encuesta disponible
     * @param titulo de la encuesta
     * @param creador nombre del usuario creador de esta encuesta
     * @throws IllegalArgumentException si el titulo esta vacio 
     */
    public Encuesta(int idEncuesta, String titulo, String creador) {
        this.idEncuesta = idEncuesta;
        this.titulo = titulo;
        this.creador = creador;
        if (titulo == null) {
            throw new IllegalArgumentException("Nombre de encuesta vacio");
        }    
    }

    /** Metodos **/
    /**
     * Devuelve el idEncuesta, unico para cada encuesta disponible
     * @return idEncuesta
    */
    public int getidEncuesta() {
        return idEncuesta;
    }

    /**
     * Se obtiene todo el ArrayList de iDPreguntas de las preguntas
     * contenidas en una encuesta
     * @return El ArrayList de IdPreguntas
     */
    public ArrayList<Integer> getIdPreguntas() {
        return IdPreguntas;
    }

    /**
     * Se obtiene el nombre del usuario creador de esta encuesta
     * @return nombre del creador
     */
    public String getIdCreador() {
        return creador;
    }

    /**
     * Se añade una pregunta con IdPregunta en la encuesta, se guarda
     * en el ArrayList de idPreguntas
     * @param IdPregunta de la nueva pregunta que se quiere añadir
     */
    public void guardarPregunta(Integer IdPregunta) {
        this.preguntas.add(IdPregunta);
    }

    /*
    public ArrayList<ArrayList<Respuesta>> verRespuestas() {

        ArrayList<ArrayList<Respuesta>> respuestas = new ArrayList<>(); 
        for (Pregunta p : preguntas) {
            respuestas.add(p.getRespuestas());
        }
        return respuestas;
    }
    */

    /**
     * Se elimina un IdPregunta, equivalente a eliminar una pregunta 
     * de la encuesta
     * @param IdPregunta de la pegunta que se quiere eliminar
     */
    public void eliminarPregunta(Integer IdPregunta) {
        this.IdPreguntas.remove(Integer.valueOf(IdPregunta));
    }

    /**
     * Se quiere actualizar/cambiar el titulo de la encuesta
     * @param titulo al que se quiere actualizar
     */
    public void cambiarTitulo(String titulo) {
        this.titulo = titulo;
    }



}