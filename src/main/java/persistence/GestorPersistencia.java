package persistence;

import java.util.Map;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.ArrayList;
import domain.Usuario;
import domain.Encuesta;
import domain.Pregunta;
import domain.Respuesta;

/*

PND:

existeEncuesta
getEncuesta
getNextIdEncuesta
actualizarEncuesta(Encuesta e)
eliminarEncuesta(idE)

existePregunta
getPregunta
getNextIdPregunta
actualizarPregunta
eliminarPregunta

existeRespuesta
getRespuesta
getNextIdRespuesta
guardarRespuesta 
actualizarRespuesta
eliminarRespuesta

existeUsuario
getUsuario
actualizarUsuario
 */

/**
 * Controlador de datos del sistema
 * 
 * @author Alessandro Jin
 */
public class GestorPersistencia {
    /** Atributos **/

    private static GestorPersistencia singletonInstance;

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

    /**
     * Inicialización de los atributos de la clase
     */
	public GestorPersistencia() {
        usuarios = new HashMap<>();
        encuestas = new HashMap<>();
        preguntas = new HashMap<>();
        respuestas = new HashMap<>();
    }

    public static GestorPersistencia getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new GestorPersistencia();
        }
        return singletonInstance;
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
        Integer id = e.getIdEncuesta();
        
        if(encuestas.containsKey(id)) {
            throw new IllegalArgumentException("La encuesta con id: "+ id + " ya existe");
        }

        encuestas.put(id, e);
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
        Integer id = p.getIdPregunta();

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

        if (!respuestas.containsKey(id)) {
            throw new IllegalArgumentException("La respuesta con id: " + id + " no existe");
        }
        respuestas.remove(id);
    }

    // // ==================== MÉTODOS PARA NUEVA ARQUITECTURA (Singleton + TreeMap bulk operations) ====================
    
    // /**
    //  * Instancia única de GestorPersistencia (Singleton).
    //  * Implementado por el copilot, pendiente de revisar.
    //  */
    // private static GestorPersistencia singletonInstance;
    
    // /**
    //  * Obtiene la instancia única de GestorPersistencia (patrón Singleton).
    //  * Implementado por el copilot, pendiente de revisar.
    //  * 
    //  * @return Instancia única de GestorPersistencia
    //  */
    // public static GestorPersistencia getInstance() {
    //     if (singletonInstance == null) {
    //         singletonInstance = new GestorPersistencia();
    //     }
    //     return singletonInstance;
    // }
    
    // /**
    //  * Carga todos los usuarios desde persistencia al iniciar la aplicación.
    //  * Implementado por el copilot, pendiente de revisar.
    //  * 
    //  * @return TreeMap con todos los usuarios (clave: nomUsuario)
    //  */
    // public TreeMap<String, Usuario> cargarTodosUsuarios() {
    //     // Implementado por el copilot, pendiente de revisar
    //     TreeMap<String, Usuario> resultado = new TreeMap<>();
    //     for (Map.Entry<String, Usuario> entry : usuarios.entrySet()) {
    //         resultado.put(entry.getKey(), entry.getValue());
    //     }
    //     return resultado;
    // }
    
    // /**
    //  * Guarda todos los usuarios en persistencia al cerrar la aplicación.
    //  * Implementado por el copilot, pendiente de revisar.
    //  * 
    //  * @param todosUsuarios TreeMap con todos los usuarios a guardar
    //  */
    // public void guardarTodosUsuarios(TreeMap<String, Usuario> todosUsuarios) {
    //     // Implementado por el copilot, pendiente de revisar
    //     this.usuarios.clear();
    //     this.usuarios.putAll(todosUsuarios);
    // }
    
    // /**
    //  * Carga todas las encuestas desde persistencia al iniciar la aplicación.
    //  * Implementado por el copilot, pendiente de revisar.
    //  * 
    //  * @return TreeMap con todas las encuestas (clave: idEncuesta)
    //  */
    // public TreeMap<Integer, Encuesta> cargarTodasEncuestas() {
    //     // Implementado por el copilot, pendiente de revisar
    //     TreeMap<Integer, Encuesta> resultado = new TreeMap<>();
    //     for (Map.Entry<Integer, Encuesta> entry : encuestas.entrySet()) {
    //         resultado.put(entry.getKey(), entry.getValue());
    //     }
    //     return resultado;
    // }
    
    // /**
    //  * Guarda todas las encuestas en persistencia al cerrar la aplicación.
    //  * Implementado por el copilot, pendiente de revisar.
    //  * 
    //  * @param todasEncuestas TreeMap con todas las encuestas a guardar
    //  */
    // public void guardarTodasEncuestas(TreeMap<Integer, Encuesta> todasEncuestas) {
    //     // Implementado por el copilot, pendiente de revisar
    //     this.encuestas.clear();
    //     this.encuestas.putAll(todasEncuestas);
    // }
    
    // /**
    //  * Carga todas las preguntas desde persistencia al iniciar la aplicación.
    //  * Implementado por el copilot, pendiente de revisar.
    //  * 
    //  * @return TreeMap con todas las preguntas (clave: idPregunta)
    //  */
    // public TreeMap<Integer, Pregunta> cargarTodasPreguntas() {
    //     // Implementado por el copilot, pendiente de revisar
    //     TreeMap<Integer, Pregunta> resultado = new TreeMap<>();
    //     for (Map.Entry<Integer, Pregunta> entry : preguntas.entrySet()) {
    //         resultado.put(entry.getKey(), entry.getValue());
    //     }
    //     return resultado;
    // }
    
    // /**
    //  * Guarda todas las preguntas en persistencia al cerrar la aplicación.
    //  * Implementado por el copilot, pendiente de revisar.
    //  * 
    //  * @param todasPreguntas TreeMap con todas las preguntas a guardar
    //  */
    // public void guardarTodasPreguntas(TreeMap<Integer, Pregunta> todasPreguntas) {
    //     // Implementado por el copilot, pendiente de revisar
    //     this.preguntas.clear();
    //     this.preguntas.putAll(todasPreguntas);
    // }
    
    // /**
    //  * Carga todas las respuestas desde persistencia al iniciar la aplicación.
    //  * Implementado por el copilot, pendiente de revisar.
    //  * 
    //  * @return TreeMap con todas las respuestas (clave: idRespuesta)
    //  */
    // public TreeMap<Integer, Respuesta> cargarTodasRespuestas() {
    //     // Implementado por el copilot, pendiente de revisar
    //     TreeMap<Integer, Respuesta> resultado = new TreeMap<>();
    //     for (Map.Entry<Integer, Respuesta> entry : respuestas.entrySet()) {
    //         resultado.put(entry.getKey(), entry.getValue());
    //     }
    //     return resultado;
    // }
    
    // /**
    //  * Guarda todas las respuestas en persistencia al cerrar la aplicación.
    //  * Implementado por el copilot, pendiente de revisar.
    //  * 
    //  * @param todasRespuestas TreeMap con todas las respuestas a guardar
    //  */
    // public void guardarTodasRespuestas(TreeMap<Integer, Respuesta> todasRespuestas) {
    //     // Implementado por el copilot, pendiente de revisar
    //     this.respuestas.clear();
    //     this.respuestas.putAll(todasRespuestas);
    // }

    // public void actualizarUsuario(Usuario u) {
    //     String userName = u.getNomUsuario();

    //     if (!usuarios.remove(u)) {
    //         throw new IllegalArgumentException("El usuario con nombre: " + userName + " no existe");
    //     }

        
    // }
}

