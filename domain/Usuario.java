package domain;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Usuario {

    /** Atributos **/
    private String nomUsuario;
    private String contrasena;

    protected ArrayList<Perfil> perfiles;
    protected ArrayList<Encuesta> encuestasCreadas;
    protected ArrayList<Respuesta> respuestas;

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
    public ArrayList<Perfil> getPerfiles() {
        return perfiles;
    }
    public ArrayList<Encuesta> getEncuestasCreadas() {
        return encuestasCreadas;
    }
    public ArrayList<Perfil> getRespuestas() {
        return respuestas;
    }
    public Set<Encuesta> getEncuestasRespondidas() {
        Set<Encuesta> aux = new HashSet<>();
        for (int i = 0; i < respuestas.size(); ++i)
        {
            aux.add(respuesta.get(i).getEncuesta());
        }
        return aux;
    }
    public ArrayList<Respuesta> getRespuestasEncuesta(int idEncuesta)
    {
        ArrayList<Respuesta> aux;
        for (int i = 0; i < respuestas.size(); ++i)
        {
            if (respuesta.get(i).getIdEncuesta() == idEncuesta) {
                aux.add(respuesta.get(i));
            }
        }
        return aux;
    }
}