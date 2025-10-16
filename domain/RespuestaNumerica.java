package domain;

public class RespuestaNumerica extends Respuesta {
    
    /** Atributos **/

    private int respuesta;

    // respuesta tiene que estar dentro del rango

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