package persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import domain.Usuario;

/**
 * Gestor de persistencia de usuarios con cache LRU.
 * 
 * Mantiene una cache limitada de usuarios más recientemente usados
 * y delega operaciones de disco a CtrlPersistenciaUsuario.
 * 
 * RESPONSABILIDADES:
 * - Gestionar cache LRU de usuarios (máximo 64 entradas)
 * - Coordinar con CtrlPersistenciaUsuario para operaciones de disco
 * - Proporcionar operaciones CRUD con cache transparente
 * - Implementar patrón Singleton
 * 
 * ATRIBUTOS PRINCIPALES:
 * - usuarios: LinkedHashMap con política LRU (accessOrder=true)
 * - ctrlFicheroUsuario: Controlador de persistencia en disco
 * - CACHE_LIMIT: Límite de 64 usuarios en cache
 * 
 * ESTRUCTURA:
 * - Cache: LinkedHashMap con removeEldestEntry para LRU automático
 * - Disco: Archivos JSON en data/usuarios/{nombreUsuario}.json
 * 
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
     * Busca y carga un usuario desde disco a la cache.
     * 
     * @param nom Nombre del usuario
     * @return Usuario cargado desde disco, o null si no existe
     */
    Usuario sacarUsuarioFichero(String nom) {
        try {
            Usuario u = ctrlFicheroUsuario.getUsuario(nom);
            usuarios.put(nom, u);
            return u;
        } catch (RuntimeException e) {
            // Usuario no existe en disco
            return null;
        }
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
        if (usuarios.containsKey(nom)) {
            return true;
        }
        
        // Intentar cargar desde disco
        Usuario u = sacarUsuarioFichero(nom);
        return u != null;
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