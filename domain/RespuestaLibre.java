package domain;

public class RespuestaLibre extends Respuesta {
    
    /** Atributos **/

    private String respuesta;

    /** Constructora **/

    public RespuestaOpcionMultiple(Usuario usuario, Pregunta pregunta, 
                                   String respuesta) {
        super(usuario, pregunta);
        this.respuesta = respuesta;
    }

    /** Métodos **/
    
    @Override
    public String getRespuesta() {
        return respuesta;
    }

}