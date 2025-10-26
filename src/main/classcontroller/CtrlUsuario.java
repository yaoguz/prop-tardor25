
package classcontroller;

import domain.Usuario;
import java.util.HashMap;
import java.util.Map;


public class CtrlUsuario {
	
	private static CtrlUsuario singletonObject;
	private Map<String, Usuario> usuarios;  
    private String usuarioActivo;

	private CtrlUsuario() {
        usuarios = new HashMap<>();
    }

	public static CtrlUsuario getInstance() {
		if (singletonObject == null)
			singletonObject = new CtrlUsuario() {
			};
		return singletonObject;
	}

	/** Constructora privada. **/
	
	/**
     * Crea un nuevo usuario.
	 * @param {String} nomU - Nombre del nuevo usuario.
	 * @param {String} contrasena - Contrasena del nuevo usuario.
     * @return {Usuario} nuevo usuario creado
     */
    public Usuario crearUsuario(String nomU, String contrasena) {
        if (usuarios.containsKey(nomU)) {
            throw new IllegalStateException("Ya existe un usuario con el nombre " + nomU);
        }
        
        Usuario u = new Usuario(nomU, contrasena);  
        usuarios.put(nomU, u);
        return u;
    }

	/**
     * Obtiene un Usuario con nombre nomUsuario.
	 * @param {String} nomUsuario - Nombre del usuario a obtener.
	 * @return {Usuario} usuario con nombre nomUsuario
     */
    public Usuario getUsuario(String nomUsuario) {
        Usuario u = usuarios.get(nomUsuario);
        if (u == null) {
            throw new IllegalArgumentException("Usuario " + nomUsuario + " no existe");
        }
        return u;
    }

	/**
     * Usuario con nombre nomU inicia sesion con contrasena. Si no existe un usario 
	 * con nomU o la contrasena es incorrecta, se lanza una exception.
	 * @param {String} nomU - Nombre del usuario que inicia sesion.
	 * @param {String} contrasena - Contrasena introducida por el usuario.
     */
    public void iniciarSesion(String nomU, String contrasena) {
        Usuario u = getUsuario(nomU);
        if (!u.comprobarContrasena(contrasena)) {
            System.out.println("Contrasena incorrecta para el usuario " + nomU);
			// bucle a iniciar sesion
        }
        usuarioActivo = u.getNomUsuario();
    }

	/**
     * Cierra sesión el usuario activo.
     */
    public void cerrarSesion() {
        usuarioActivo = null;
    }

    /**
     * Obtiene el nombre del usuario activo.
     * @return {String} nombre del usuario activo
     */
    public String getUsuarioActivo() {
        return usuarioActivo;
    }
}

