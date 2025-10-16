package domain;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Usuario {

    /** Atributos **/
    private String nomUsuario;
    private String contrasena;

    protected ArrayList<Integer> idPerfiles;
    protected ArrayList<Integer> idEncuestasCreadas;
    protected ArrayList<Integer> idRespuestas;

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
    public ArrayList<Integer> getPerfiles() {
        return idPerfiles;
    }
    public ArrayList<Integer> getEncuestasCreadas() {
        return idEncuestasCreadas;
    }
    public ArrayList<Integer> getRespuestas() {
        return idRespuestas;
    }
    public Set<Integer> getEncuestasRespondidas() {
        Set<Integer> aux = new HashSet<>();
        for (int i = 0; i < idRespuestas.size(); ++i)
        {
            aux.add(idRespuestas.get(i).getEncuesta());
        }
        return aux;
    }
    public ArrayList<Integer> getRespuestasEncuesta(int idEncuesta)
    {
        ArrayList<Integer> aux = new ArrayList<>();
        for (int i = 0; i < idRespuestas.size(); ++i)
        {
            if (idRespuestas.get(i).getIdEncuesta() == idEncuesta) {
                aux.add(idRespuestas.get(i));
            }
        }
        return aux;
    }
}