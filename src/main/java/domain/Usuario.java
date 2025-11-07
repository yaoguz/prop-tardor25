package domain;
import java.util.ArrayList;

public class Usuario {

    /** Atributos **/
    private String nomUsuario;
    private String contrasena;

    protected ArrayList<Integer> idPerfiles;
    protected ArrayList<Integer> idEncuestasCreadas;
    protected ArrayList<Integer> idRespuestas;

    /** Constructora **/
    /**
     * Se crea un nuevo Usuario con un nombre y una contrasena. Se inicializan las ArraysLists de idPerfiles,
     * idEncuestasCreadas e idRespuestas con ArrayList vacios.
     * @param nomUsuario nombre del Usuario
     * @param contrasena contrasena del Usuario
     */
    public Usuario(String nomUsuario, String contrasena) {
        this.nomUsuario = nomUsuario;
        this.contrasena = contrasena;
        this.idPerfiles = new ArrayList<>();
        this.idEncuestasCreadas = new ArrayList<>();
        this.idRespuestas = new ArrayList<>();
    }

    /** Metodos publicos**/
    /**
     * Se obtiene el nombre del Usuario.
     * @return {String} nombre del Usuario.
     */
    public String getNomUsuario() {
        return nomUsuario;
    }

    /**
     * Se comprueba si la contrasena pasada por parametro es igual a la contrasena del Usuario.
     * @param {String} contrasena - Contrasena a comprobar.
     * @return {boolean} true si la contrasena es correcta, false en caso contrario.
     */
    public boolean comprobarContrasena(String contrasena) {
        return this.contrasena.equals(contrasena);
    }

    /**
     * Se cambia la contrasena del usuario por el el newContrasena que se le pasa.
     * @param {String} newContrasena - Nueva contrasena del Usuario.
     * @throws {IllegalArgumentException} Si la nueva contrasena es igual a la anterior.
     */
    public void newContrasena(String newContrasena) {
        if (this.contrasena.equals(newContrasena)) {
            throw new IllegalArgumentException("La nueva contrasena no puede ser igual a la anterior.");
        }
        this.contrasena = newContrasena;
    }

    /**
     * Se obtiene un ArrayList con todas las idRespuestas respondidas por el Usuario.
     * @return {ArrayList<Integer>} idRespuestas de las Respuestas respondidas por el Usuario.
     */
    public ArrayList<Integer> getIdRespuestas() {
        return idRespuestas;
    }

    /**
     * Se anade en idRespuestas una nueva respuesta del Usuario con idR
     * @param {int} idR - Id de la Respuesta respondida por el Usuario.
     */
    public void guardarIdRespuesta(int idR) {
        this.idRespuestas.add(idR);
    }

    /**
     * Se elimina de idRespuestas la respuesta con idR.
     * @param {int} idR - Id de la Respuesta que se desea eliminar.
     */
    public void eliminarIdRespuesta(int idR) {
        this.idRespuestas.remove(Integer.valueOf(idR)); 
    }

    /**
     * Se obtiene un ArrayList con todas las idEncuestas creadas por el Usuario.
     * @return {ArrayList<Integer>} idEncuestas de las Encuestas creadas por el Usuario.
     */
    public ArrayList<Integer> getIdEncuestasCreadas() {
        return idEncuestasCreadas;
    }

    /**
     * Se anade en idEncuestasCreadas una nueva encuesta creada por el Usuario con idE.
     * @param {int} idE - Id de la Encuesta creada por el Usuario.
     */
    public void guardarIdEncuesta(int idE) {
        this.idEncuestasCreadas.add(idE);
    }
    
    /**
     * Se elimina de idEncuestasCreadas la encuesta con idE.
     * @param {int} idE - Id de la Encuesta que se desea eliminar.
     */
    public void eliminarIdEncuestaCreada(int idE) {
        this.idEncuestasCreadas.remove(Integer.valueOf(idE));
    }

    /**
     * Se obtiene un ArrayList con todas los idPerfiles del Usuario.
     * @return {ArrayList<Integer>} idPerfiles de los Perfiles del Usuario.
     */
    public ArrayList<Integer> getIdPerfiles() {
        return idPerfiles;
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
}