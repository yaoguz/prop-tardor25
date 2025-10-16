package domain;

public class RespuestaOpcionMultiple extends Respuesta {
    
    /** Atributos **/

    private int[] respuesta;

    /** Constructora **/

    public RespuestaOpcionMultiple(Usuario usuario, Pregunta pregunta, 
                                   int[] respuesta) {
        super(usuario, pregunta);
        this.respuesta = respuesta;

        // comprobar respuesta.size() no supera numMaxOpc
    }

    /** Métodos **/
    
    @Override
    public int[] getRespuesta() {
        return respuesta;
    }

}