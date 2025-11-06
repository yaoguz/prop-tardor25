package persistence;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import domain.Usuario;
import domain.Encuesta;
import domain.Pregunta;
import domain.Respuesta;

/*
PND:
actualizarUsuario(Usuario u); // cuando se modifica la contrasena de un usuario u
actualizarRespuesta(Respuesta r); // cuando se modifica una respuesta
actualizarEncuesta(Encuesta e); // cuando se modifica el titulo de una encuesta

funciones de eliminar

*/

/**
 * Controlador de datos del sistema
 */

public class CtrlPersistence {
    /** Atributos **/
    /**
     * Mapa que almacena los usuarios existentes del sistema.
     * <p>
     * La clave es el nombre de usuario y el valor es el objeto {@link domain.Usuario}.
     */
    private Map<String,Usuario> usuarios; 

    /**
     * Mapa que almacena las encuestas existentes del sistema.
     * <p>
     * La clave es un Integer id y el valor es el objeto {@link domain.Encuesta}.
     */
    private Map<Integer,Encuesta> encuestas;

    /**
     * Mapa que almacena las preguntas de todas las encuestas del sistema
     * <p>
     * La clave es un Integer id y el valor es el objeto {@link domain.Pregunta}.
     */
    private Map<Integer,Pregunta> preguntas;

    /**
     * Mapa que almacena las respuestas de todas las preguntas del sistema
     * <p>
     * La clave es un Integer id y el valor es el objeto {@link domain.Respuesta}.
     */
    private Map<Integer,Respuesta> respuestas;


    /** Constructor y metodos de inicializacion **/

	public CtrlPersistence() {
		inicializarCtrlPersistence();
	}


    /**
     * Inicialización de los atributos de la clase
     */
	public void inicializarCtrlPersistence() {
        usuarios = new HashMap();
        encuestas = new HashMap();
        preguntas = new HashMap();
        respuestas = new HashMap();
    }

    /**
	 * Funciones que se llaman desde el controlador de Dominio. 
	 * <p>
     * Gestión de datos del sistema.
	 **/

    
    /**
    * Devuelve una lista con todos los usuarios almacenados en el mapa {@code usuarios}.
    *
    * <p>
    * El método recorre todos los elementos del mapa y devuelve una lista
    * que contiene cada objeto {@link domain.Usuario} registrado.
    *
    * @return una lista con todos los usuarios existentes
    */


    public ArrayList<Usuario> getUsuarios() {

        return new ArrayList<>(usuarios.values());

    }

    /**
    * Devuelve una lista con todos las encuestas almacenados en el mapa {@code encuestas}.
    *
    * <p>
    * El método recorre todos los elementos del mapa y devuelve una lista
    * que contiene cada objeto {@link domain.Encuesta} registrado.
    *
    * @return una lista con todos las encuestas existentes
    */
    public ArrayList<Encuesta> getEncuestas() {

        return new ArrayList<>(encuestas.values());
    }

    /**
    * Devuelve una lista con todos las preguntas almacenados en el mapa {@code preguntas}.
    *
    * <p>
    * El método recorre todos los elementos del mapa y devuelve una lista
    * que contiene cada objeto {@link domain.Pregunta} registrado.
    *
    * @return una lista con todos las preguntas existentes
    */

    public ArrayList<Pregunta> getPreguntas() {
       
        return new ArrayList<>(preguntas.values());
    }

    /**
    * Devuelve una lista con todos las respuestas almacenados en el mapa {@code respuestas}.
    *
    * <p>
    * El método recorre todos los elementos del mapa y devuelve una lista
    * que contiene cada objeto {@link domian.Respuesta} registrado.
    *
    * @return una lista con todos las respuestas existentes
    */

    public ArrayList<Respuesta> getRespuestas() {
        
        return new ArrayList<> (respuestas.values());
    }


    /**
    * Guarda un nuevo usuario en el mapa {@code usuarios}.
    *
    * <p>
    * Si ya existe un usuario con el mismo nombre, se lanza una
    * {@link IllegalArgumentException} para evitar duplicados.
    *
    * @param u el objeto {@link Usuario} que se desea guardar
    * @throws IllegalArgumentException si ya existe un usuario con el mismo nombre
    */
    public void guardarUsuario(Usuario u) {
        String nom = u.getNomUsuario();


        if (usuarios.containsKey(nom)) {
            throw new IllegalArgumentException("El usuario con nom: " + nom + " ya existe");
        }

        usuarios.put(nom, u);
    }

    /**
    * Guarda una nueva encuesta en el mapa {@code encuestas}.
    *
    * <p>
    * Si ya existe una encuesta con el mismo id, se lanza una
    * {@link IllegalArgumentException} para evitar duplicados.
    *
    * @param e el objeto {@link Encuesta} que se desea guardar
    * @throws IllegalArgumentException si ya existe una encuesta con el mismo id
    */
    public void guardarEncuesta(Encuesta e) {
        Integer id = e.getidEncuesta();
        
        if(encuentas.containsKey(id)) {
            throw new IllegalArgumentException("La encuesta con id: "+ id + " ya existe");
        }

        encuentas.put(id, e);
    }

    /**
    * Guarda una nueva pregunta en el mapa {@code preguntas}.
    *
    * <p>
    * Si ya existe una pregunta con el mismo id, se lanza una
    * {@link IllegalArgumentException} para evitar duplicados.
    *
    * @param p el objeto {@link Pregunta} que se desea guardar
    * @throws IllegalArgumentException si ya existe una Pregunta con el mismo id
    */
    public void guardarPregunta(Pregunta p) {
        Integer id = p.getId();

        if (preguntas.containsKey(id)) {
            throw new IllegalArgumentException("La pregunta con id: " + id + " ya existe");
        }

        preguntas.put(id, p);
    }

    /**
    * Guarda una nueva repuesta en el mapa {@code respuestas}.
    *
    * <p>
    * Si ya existe una respuesta con el mismo id, se lanza una
    * {@link IllegalArgumentException} para evitar duplicados.
    *
    * @param r el objeto {@link Respuesta} que se desea guardar
    * @throws IllegalArgumentException si ya existe una respuesta con el mismo id
    */
    public void guardarRepuesta(Respuesta r) {
        Integer id = r.getId();

        if (respuestas.containsKey(id)) {
            throw new IllegalArgumentException("La respuesta con id: " + id + " ya existe");
        }
        respuestas.put(id, r);
    }


    /**
    * Eliminar un usuario en el mapa {@code usuarios}.
    *
    * <p>
    * Si no existe un usuario con el mismo name, se lanza una
    * {@link IllegalArgumentException}.
    *
    * @param u el objeto {@link Usuario} que se desea borrar
    * @throws IllegalArgumentException si no existe un usuario con el mismo name
    */
    public void eliminarUsuario(Usuario u) {
        String name = u.getNomUsuario();

        if(!usuarios.remove(name)) {
            throw new IllegalArgumentException("El usuario con name: " + name + " no existe");
        }
    }

    /**
    * Eliminar una encuesta en el mapa {@code encuestas}.
    *
    * <p>
    * Si no existe una encuesta con el mismo id, se lanza una
    * {@link IllegalArgumentException}.
    *
    * @param e el objeto {@link Encuesta} que se desea borrar
    * @throws IllegalArgumentException si no existe una encuesta con el mismo id
    */
    public void eliminarEncuesta(Encuesta e) {
        Integer id = e.getIdEncuesta();

        if(!encuestas.remove(id)) {
            throw new IllegalArgumentException("La encuesta con id: " + id + " no existe");
        }
    }


    /**
    * Eliminar una pregunta en el mapa {@code preguntas}.
    *
    * <p>
    * Si no existe una pregunta con el mismo id, se lanza una
    * {@link IllegalArgumentException}.
    *
    * @param p el objeto {@link Pregunta} que se desea borrar
    * @throws IllegalArgumentException si no existe una pregunta con el mismo id
    */
    public void eliminarPregunta(Pregunta p) {
        Integer id = p.getIdPregunta();

        if (!preguntas.remove(p)) {
            throw new IllegalArgumentException("La pregunta con id: " + id + " no existe");
        }
    }

    /**
    * Eliminar una respuesta en el mapa {@code respuestas}.
    *
    * <p>
    * Si no existe una respuesta con el mismo id, se lanza una
    * {@link IllegalArgumentException}.
    *
    * @param r el objeto {@link Respuesta} que se desea borrar
    * @throws IllegalArgumentException si no existe una encuesta con el mismo id
    */
    public void eliminarRespuesta(Respuesta r) {
        Integer id = r.getId();

        if (!respuestas.remove(r)) {
            throw new IllegalArgumentException("La respuesta con id: " + id + " no existe");
        }
    }

    public void actualizarUsuario(Usuario u) {
        String userName = u.getNomUsuario();

        if (!usuarios.remove(u)) {
            throw new IllegalArgumentException("El usuario con nombre: " + userName + " no existe");
        }

        
    }
}

