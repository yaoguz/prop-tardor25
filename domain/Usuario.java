package domain;

public class Usuario {

    /** Atributos **/
    private String nomUsuario;
    private String contrasena;

    /** Constructora **/
    public Usuario(String nomUsuario, String contrasena) {
        this.nomUsuario = nomUsuario;
        this.contrasena = contrasena;
    }

    /** Metodos publicos**/
    public String getNomUsuario() {
        return nomUsuario;
    }
}