package domain;

public class Usuario {

    /** Atributos **/
    private String nomUsuario;
    private String contrasena;

    protected Perfil[] perfiles;
    protected Encuesta[] encuestasCreadas;

    /** Constructora **/
    public Usuario(String nomUsuario, String contrasena) {
        this.nomUsuario = nomUsuario;
        this.contrasena = contrasena;
    }

    /** Metodos publicos**/
    public String getNomUsuario() {
        return nomUsuario;
    }
    public boolean comprobarContrasena(String contrasena) {
        return this.contrasena.equals(contrasena);
    }
    public Perfil[] getPerfiles() {
        return perfiles;
    }
    public Encuesta[] getEncuestasCreadas() {
        return encuestasCreadas;
    }
}