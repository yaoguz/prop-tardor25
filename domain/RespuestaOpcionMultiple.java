package domain;

public class RespuestaOpcionMultiple extends Respuesta {
    
    /** Atributos **/

    private int[] respuesta;

    // respuesta.size() no puede superar numMaxOpc

    /** Constructora **/

    public RespuestaOpcionMultiple(Usuario usuario, Pregunta pregunta, 
                                   int[] respuesta) {
        super(usuario, pregunta);
        this.respuesta = respuesta;
    }

    /** Métodos **/
    
    @Override
    public int[] getRespuesta() {
        return respuesta;
    }

}