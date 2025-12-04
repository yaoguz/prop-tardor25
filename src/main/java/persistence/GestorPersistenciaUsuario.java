package persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import domain.Respuesta;
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
        usuarios = new LinkedHashMap<>(CACHE_LIMIT, 2.0f, true) {
            protected boolean removeEldestEntry(Map.Entry<String, Usuario> eldest){
                return size() > CACHE_LIMIT;
            }
        };

        // Inicializar controladores de disco
        ctrlFicheroUsuario = new CtrlPersistenciaUsuario();
    }

    // ==================== MÉTODOS DE USUARIO ====================

    /**
     * Buscar el Usuario en el fichero
     */
    Usuario sacarUsuarioFichero(String nom) {

        Usuario u = ctrlFicheroUsuario.getUsuario(nom);
        usuarios.put(nom, u);

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

        ctrlFicheroUsuario.guardarUsuario(u);
        
        usuarios.put(nom, u);
    
    }

    /**
    * Devuelve una lista con todos los usuarios almacenados.
    *
    * @return una lista con todos los usuarios existentes
    */
    public ArrayList<Usuario> getAllUsuarios() {
        
        //Return arraylist
        return ctrlFicheroUsuario.getAllUsuarios();

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
            try{
                sacarUsuarioFichero(nom);
            }

            catch (Exception e) {
                return false,
            }
            
        }
        return true;
    }

    /**
     * Actualiza un usuario existente.
     * 
     * @param u Usuario a actualizar
     */
    public void actualizarUsuario(Usuario u) {
        String nom = u.getNomUsuario();
        if (!usuarios.containsKey(nom)) sacarUsuarioFichero(nom);
        
        usuarios.put(nom, u); //sobre escribe

        ctrlFicheroUsuario.actualizarUsuario(u);
    }

    /**
     * Elimina un usuario por su nombre.
     * 
     * @param nom Nombre del usuario a eliminar
     * @throws IllegalArgumentException si el usuario no existe
     */
    public void eliminarUsuario(String nom) {
        if(usuarios.containsKey(nom)) usuarios.remove(nom);
        ctrlFicheroUsuario.eliminarUsuario(nom);
    }

}