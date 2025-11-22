package persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import domain.Usuario;

/**
 * Cache del Usuario
 */

/**
 * @author Alessandro Jin
 */

public class GestorPersistenciaUsuario {
    private static GestorPersistenciaUsuario singletonInstance;

    // Límites de cache
    private static final int CACHE_LIMIT = 64;

    // Cache con LinkedHashMap (accessOrder=true para LRU tracking)
    private final LinkedHashMap<String, Usuario> usuarios;

    // Controlador de persistencia usuario en disco
    private final CtrlPersistenciaUsuario ctrlFicheroUsuario;

    /**
     * Obtiene instancia única (patrón Singleton).
     * 
     * @return Instancia única de GestorPersistenciaUsuario
     */
    public static GestorPersistenciaUsuario getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new GestorPersistenciaUsuario();
        }
        return singletonInstance;
    }

    private GestorPersistenciaUsuario() {
        // Inicializar LinkedHashMaps con accessOrder=true para tracking LRU
        usuarios = new LinkedHashMap<>(CACHE_LIMIT, 2.0f, true);

        // Inicializar controladores de disco
        ctrlUsuario = new CtrlPersistenciaUsuario();
    }

    // ==================== MÉTODOS DE USUARIO ====================

    /**
     * Buscar el Usuario en el fichero
     */
    Usuario sacarUsuarioFichero(String nom) {

        Usuario u = ctrlFicheroUsuario.getUsuario(nom);
        usuarios.guardarUsuario(u);

        return u;
    }

    /**
    * Guarda un nuevo usuario en el mapa {@code usuarios}.
    *
    * @param u Usuario a guardar
    * @throws IllegalArgumentException si ya existe un usuario con el mismo nombre
    */
    public void guardarUsuario(Usuario u) {
        String nom = u.getNomUsuario();

        if (usuarios.containsKey(nom)) {
            throw new IllegalArgumentException("El usuario con nom: " + nom + " ya existe");
        }

        ctrlFicheroUsuario.guardarUsuario(nom);

        if (usuarios.size() == CACHE_LIMIT) espacioCache(usuarios);
        usuarios.put(nom, u);
    
    }

    /**
    * Devuelve una lista con todos los usuarios almacenados.
    *
    * @return una lista con todos los usuarios existentes
    */
    public ArrayList<Usuario> getUsuarios() {
        // ???
        return new ArrayList<>(usuarios.values());
    }

    /**
     * Obtiene un usuario por su nombre.
     * 
     * @param nom Nombre del usuario
     * @return Usuario correspondiente, o null si no existe
     */
    public Usuario getUsuario(String nom) {
        Usuario u = usuarios.get(nom);
        
        if (u == null) u = sacarUsuarioFichero(nom);

        return u;
    }

    /**
     * Verifica si existe un usuario con el nombre dado.
     * 
     * @param nom Nombre del usuario
     * @return true si existe, false en caso contrario
     */
    public boolean existeUsuario(String nom) {
        if (!usuarios.containsKey(nom)) {
            if (!ctrlFicheroUsuario.existeUsuario(nom)) return false;
            usuarios.sacarUsuarioFichero(nom);
        }
        return true;
    }

    /**
     * Actualiza un usuario existente.
     * 
     * @param u Usuario a actualizar
     */
    public void actualizarUsuario(Usuario u) {
        if (!usuarios.containsKey(u.getNomUsuario())) usuarios.sacarUsuarioFichero(nom);
        
        usuarios.put(u.getNomUsuario(), u); //sobre escribe

        ctrlFicheroUsuario.actualizarUsuario(u);
    }

    /**
    * Elimina un usuario por el objeto Usuario.
    *
    * @param u Usuario a eliminar
    * @throws IllegalArgumentException si el usuario no existe
    */
    public void eliminarUsuario(Usuario u) {
        String name = u.getNomUsuario();
        if(usuarios.containsKey(name)) usuarios.remove(name);
        ctrlFicheroUsuario.eliminarUsuario(u);
    }

    /**
     * Elimina un usuario por su nombre.
     * 
     * @param nom Nombre del usuario a eliminar
     * @throws IllegalArgumentException si el usuario no existe
     */
    public void eliminarUsuario(String nom) {
        if(usuarios.containsKey(name)) usuarios.remove(name);
        ctrlFicheroUsuario.eliminarUsuario(u);
    }

}
