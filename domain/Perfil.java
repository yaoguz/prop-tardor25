package domain;

public class Perfil {

    /** Atributos **/
    private int idPerfil;
    private Usuario representante;
    /** Constructora **/
    public Perfil(int idPerfil) {
        this.idPerfil = idPerfil;
    }
    
    /** Metodos publicos**/
    public Usuario getRepresentante() {
        return representante;
    }

    public void setRepresentante(Usuario representante) {
        this.representante = representante;
    }
    
}