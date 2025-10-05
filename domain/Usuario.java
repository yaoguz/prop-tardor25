package domain;

public class Usuario {

    /** Atributos **/
    private String nomUsuario;
    private String contrasena;

    protected Perfil[] perfiles;
    protected Encuesta[] encuestas;

    /** Constructora **/
    public Usuario(String nomUsuario, String contrasena) {
        this.nomUsuario = nomUsuario;
        this.contrasena = contrasena;
    }

    /** Metodos publicos**/
    public class Usuario getUsuario() {
        return this;
    }
    public String getNomUsuario() {
        return nomUsuario;
    }
    public Perfil[] getPerfiles() {
        return perfiles;
    }
    public Encuesta[] getEncuestas() {
        return encuestas;
    }
}