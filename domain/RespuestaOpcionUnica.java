package domain;

public class RespuestaOpcionUnica extends Respuesta {
    
    /** Atributos **/

    private int respuesta;

    /** Constructora **/

    public RespuestaOpcionMultiple(Usuario usuario, Pregunta pregunta, 
                                   int respuesta) {
        super(usuario, pregunta);
        this.respuesta = respuesta;
    }

    /** Métodos **/
    
    @Override
    public int getRespuesta() {
        return respuesta;
    }
    
}