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
    // public String getNomUsuario() {
    //     return nomUsuario;
    // }
    // public boolean comprobarContrasena(String contrasena) {
    //     return this.contrasena.equals(contrasena);
    // }
    // public ArrayList<Integer> getIdPerfiles() {
    //     return idPerfiles;
    // }
    // public ArrayList<Integer> getIdEncuestasCreadas() {
    //     return idEncuestasCreadas;
    // }

    /**
     * @returns {ArrayList<Integer>} idRespuestas de las Respuestas respondidas por el Usuario.
     */
    public ArrayList<Integer> getIdRespuestas() {
        return idRespuestas;
    }
    // public Set<Integer> getIdEncuestasRespondidas() {
    //     Set<Integer> aux = new HashSet<>();
    //     for (int i = 0; i < idRespuestas.size(); ++i)
    //     {
    //         aux.add(idRespuestas.get(i).getEncuesta());
    //     }
    //     return aux;
    // }
    // public ArrayList<Integer> getRespuestasEncuesta(int idEncuesta)
    // {
    //     ArrayList<Integer> aux = new ArrayList<>();
    //     for (int i = 0; i < idRespuestas.size(); ++i)
    //     {
    //         if (idRespuestas.get(i).getIdEncuesta() == idEncuesta) {
    //             aux.add(idRespuestas.get(i));
    //         }
    //     }
    //     return aux;
    // }

    /**
     * @param {String} newContrasena - Nueva contrasena del Usuario.
     * @throws {IllegalArgumentException} Si la nueva contrasena es igual a la anterior.
     */
    public void newContrasena(String newContrasena) {
        if (this.contrasena.equals(newContrasena)) {
            throw new IllegalArgumentException("Error: La contrasena nueva no puede ser la misma que la anterior.");
        }
        this.contrasena = newContrasena;
    }
    /**
     * @param {int} idR - Id de la Respuesta respondida por el Usuario.
     */
    public void guardarIdRespuesta(int idR) {
        this.idRespuestas.add(idR);
    }
    /**
     * @param {int} idR - Id de la Respuesta que se desea eliminar.
     */
    public void eliminarIdRespuesta(int idR) {
        this.idRespuestas.remove(idR); 
    }
    /**
     * @param {int} idE - Id de la Encuesta creada por el Usuario.
     */
    public void guardarIdEncuesta(int idE) {
        this.idEncuestasCreadas.add(idE);
    }
    /**
     * @param {int} idE - Id de la Encuesta que se desea eliminar.
     */
    public void eliminarEncuestaCreada(int idE) {
        this.idEncuestasCreadas.remove(idE);
    }
}