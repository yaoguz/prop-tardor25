package domaincontrollers;

import domain.Usuario;
import persistence.GestorPersistencia;

/**
 * Controlador de Dominio para la gestión de usuarios.
 * 
 * Responsabilidades:
 * - Registro de usuarios
 * - Inicio/cierre de sesión
 * - Modificación de contraseña
 * - Validaciones de usuario
 * 
 * @author Yaoyao Guo
 * @version 2.0
 */
public class CtrlDominioUsuario {

	/** Atributos - Controladores de clase **/
	
	private CtrlUsuario ctrlUsuario;
	private GestorPersistencia gestorPersistencia;

	/** Constructor **/

	/**
	 * Constructora. Inicializa los controladores necesarios.
	 * 
	 * @param ctrlUsuario Controlador de usuarios
	 * @param gestorPersistencia Controlador de persistencia
	 */
	public CtrlDominioUsuario(CtrlUsuario ctrlUsuario, GestorPersistencia gestorPersistencia) {
		this.ctrlUsuario = ctrlUsuario;
		this.gestorPersistencia = gestorPersistencia;
	}

	// ==================== GESTIÓN DE USUARIOS ====================
	
	/**
	 * Registra un nuevo usuario en el sistema.
	 * 
	 * @param nomU Nombre de usuario (no puede estar vacío) - usado como clave primaria
	 * @param contrasena Contraseña del usuario
	 * @param confirmCont Confirmación de la contraseña
	 * @return Nombre de usuario creado
	 * @throws IllegalArgumentException si algún campo está vacío o tiene formato incorrecto
	 * @throws IllegalStateException si las contraseñas no coinciden
	 * @throws IllegalStateException si ya existe un usuario con el mismo nombre (desde gestorPersistencia)
	 */
	public String registrarUsuario(String nomU, String contrasena, String confirmCont) {
		// comprobar campos vacíos
		if (nomU == null || nomU.trim().isEmpty()) {
			throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
		}
		if (contrasena == null || contrasena.isEmpty()) {
			throw new IllegalArgumentException("La contraseña no puede estar vacía");
		}
		if (confirmCont == null || confirmCont.isEmpty()) {
			throw new IllegalArgumentException("La confirmación de contraseña no puede estar vacía");
		}
		
		// comprobar formato
		if (nomU.length() < 3) {
			throw new IllegalArgumentException("El nombre de usuario debe tener al menos 3 caracteres");
		}
		if (contrasena.length() < 6) {
			throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
		}
		
		// comprobar contraseña
		if (!contrasena.equals(confirmCont)) {
			throw new IllegalStateException("La contraseña y su confirmación no coinciden");
		}
		
		// crear usuario
		Usuario u = ctrlUsuario.crearUsuario(nomU, contrasena);
		gestorPersistencia.guardarUsuario(u); // (puede lanzar exc si ya existe)
		return u.getNomUsuario();
	}
	
	/**
	 * Inicia sesión de un usuario existente.
	 * 
	 * @param nomU Nombre de usuario
	 * @param contrasena Contraseña del usuario
	 * @return true si el inicio de sesión es exitoso
	 * @throws IllegalArgumentException si el usuario no existe
	 * @throws IllegalStateException si la contraseña es incorrecta
	 */
	public boolean iniciarSesion(String nomU, String contrasena) {
		// validar que el usuario existe (puede lanzar exc)
		// cargar desde persistencia si es necesario
		Usuario u = gestorPersistencia.cargarUsuario(nomU);
		
		if (u == null) {
			throw new IllegalArgumentException("Usuario no existente");
		}
		if (!u.comprobarContrasena(contrasena)) {
			throw new IllegalStateException("Contraseña incorrecta");
		}
		
		// marcar sesion activa en CtrlUsuario
		ctrlUsuario.iniciarSesion(nomU, contrasena);
		return true;
	}
	
	/**
	 * Modifica la contraseña del usuario actualmente en sesión.
	 * 
	 * @param nomUsuario Nombre del usuario cuya contraseña se modificará
	 * @param newContrasena Nueva contraseña
	 * @throws IllegalStateException si la nueva contraseña es igual a la actual
	 * @throws IllegalArgumentException si la nueva contraseña no cumple requisitos
	 */
	public void modificarContrasena(String nomUsuario, String newContrasena) {
		if (newContrasena == null || newContrasena.isEmpty()) {
			throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
		}
		if (newContrasena.length() < 6) {
			throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
		}
		
		Usuario u = ctrlUsuario.getUsuario(nomUsuario);
		
		u.newContrasena(newContrasena); // puede lanzar exc: newContrasena es igual a la actual
		
		// actualizar en persistencia
		gestorPersistencia.actualizarUsuario(u);
	}
}
