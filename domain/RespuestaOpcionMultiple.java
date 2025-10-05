package domain;

public class RespuestaOpcionMultiple extends Respuesta {
    
    /** Atributos **/

    private int[] respuesta;

    /** Constructora **/

    public RespuestaOpcionMultiple(String nomUsuario, int idEncuesta, 
                                    int idPregunta, int[] respuesta) {
        super(nomUsuario, idEncuesta, idPregunta);
        this.respuesta = respuesta;
    }

    /** Métodos **/
    
    @Override
    public int[] getRespuesta() {
        return respuesta;
    }

}