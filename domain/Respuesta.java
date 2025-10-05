package domain;

public abstract class Respuesta {

    /** Atributos **/

    protected Usuario usuario;
    protected Pregunta pregunta;

    /** Constructora **/

    public Respuesta(Usuario usuario, Pregunta pregunta) {
        this.usuario = usuario;
        this.pregunta = pregunta;

        // comprobar tipo de pregunta coincide
        String tipoP = pregunta.tipoPregunta();
        if (!tipoP.equals(this.tipoRespuesta())) {
            throw new IllegalArgumentException("El tipo de la respuesta no coincide con el de la pregunta");
        }
    }

    /** Métodos públicos **/
    
    public abstract Object getRespuesta();

    public abstract String tipoRespuesta();

    public Usuario getUsuario() {
        return usuario;
    }

    public Pregunta getPregunta() {
        return pregunta;
    }

}