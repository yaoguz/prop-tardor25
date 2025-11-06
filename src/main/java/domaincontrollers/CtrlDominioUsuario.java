package domaincontrollers;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import domain.Pregunta;
import domain.Respuesta;
import domain.Usuario;

/**
 * Controlador de Dominio para la gestión de usuarios.
 * 
 * Responsabilidades:
 * - Registro de usuarios
 * - Inicio/cierre de sesión
 * - Modificación de contraseña
 * - Validaciones de usuario
 * - Gestión del conjunto de usuarios en memoria (TreeMap)
 * 
 * ARQUITECTURA: Gestiona datos directamente (TreeMap), no delega a otro controlador.
 * 
 * @author Yaoyao Guo Zhang (yaoyao.guo@estudiantat.upc.edu)
 */
public class CtrlDominioUsuario {

	/** Atributos **/
	
	// Los datos se guardan en un TreeMap con clave - nomUsuario (String)
	private TreeMap<String, Usuario> usuarios;
	private String usuarioActivo;  // Usuario con sesión activa

	/**
	 * Constructora. Inicializa el TreeMap de usuarios.
	 */
	public CtrlDominioUsuario() {
		this.usuarios = new TreeMap<String, Usuario>();
		this.usuarioActivo = null;
	}
	
	/**
	 * Verifica si existe un usuario con el nombre dado.
	 * 
	 * @param nomU Nombre del usuario
	 * @return true si existe, false en caso contrario
	 */
	public boolean existeUsuario(String nomU) {
		return usuarios.containsKey(nomU);
	}
	
	/**
	 * Obtiene un usuario por su nombre.
	 * 
	 * @param nomUsuario Nombre del usuario
	 * @return Usuario correspondiente
	 * @throws IllegalArgumentException si el usuario no existe
	 */
	public Usuario getUsuario(String nomUsuario) {
		Usuario u = usuarios.get(nomUsuario);
		if (u == null) {
			throw new IllegalArgumentException("Usuario " + nomUsuario + " no existe");
		}
		return u;
	}
	
	/**
	 * Registra un nuevo usuario en el sistema.
	 * 
	 * @param nomU Nombre de usuario (no puede estar vacío) - usado como clave primaria
	 * @param contrasena Contraseña del usuario
	 * @param confirmCont Confirmación de la contraseña
	 * @return Nombre de usuario creado
	 * @throws IllegalArgumentException si algún campo está vacío o tiene formato incorrecto
	 * @throws IllegalStateException si las contraseñas no coinciden
	 * @throws IllegalStateException si ya existe un usuario con el mismo nombre
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
		
		// comprobar que no existe
		if (existeUsuario(nomU)) {
			throw new IllegalStateException("Ya existe un usuario con el nombre " + nomU);
		}
		
		// crear y guardar EN MEMORIA
		Usuario u = new Usuario(nomU, contrasena);
		usuarios.put(nomU, u);
		
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
	 * @throws IllegalStateException si ya hay un usuario activo
	 */
	public boolean iniciarSesion(String nomU, String contrasena) {
		// validar que no hay usuario activo
		if (usuarioActivo != null) {
			throw new IllegalStateException("Ya hay un usuario activo: " + usuarioActivo);
		}
		
		// validar que el usuario existe
		Usuario u = getUsuario(nomU);  // lanza exc si no existe
		
		// validar contraseña
		if (!u.comprobarContrasena(contrasena)) {
			throw new IllegalStateException("Contraseña incorrecta");
		}
		
		// marcar sesión activa
		usuarioActivo = u.getNomUsuario();
		return true;
	}
	
	/**
	 * Cierra la sesión del usuario activo.
	 */
	public void cerrarSesionActual() {
		usuarioActivo = null;
	}
	
	/**
	 * Obtiene el nombre del usuario activo.
	 * 
	 * @return Nombre del usuario activo, o null si no hay sesión activa
	 */
	public String getUsuarioActivo() {
		return usuarioActivo;
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
		
		Usuario u = getUsuario(nomUsuario);
		u.newContrasena(newContrasena); // puede lanzar exc: newContrasena es igual a la actual
	}

	/**
	 * Obtiene los IDs de las encuestas respondidas por un usuario.
	 * 
	 * Esta operación pertenece a CtrlDominioUsuario porque el aggregate root es Usuario.
	 * Representa: "Qué encuestas respondió ESTE usuario".
	 * 
	 * @param nomUsuario Nombre del usuario
	 * @param ctrlDominioRespuesta Controlador de respuestas (inyección de dependencia)
	 * @param ctrlDominioPregunta Controlador de preguntas (inyección de dependencia)
	 * @return Lista de IDs de encuestas respondidas
	 */
	public List<Integer> getEncuestasRespondidas(String nomUsuario,
	                                               CtrlDominioRespuesta ctrlDominioRespuesta,
	                                               CtrlDominioPregunta ctrlDominioPregunta) {
		Usuario u = getUsuario(nomUsuario);
		ArrayList<Integer> idRespuestas = u.getIdRespuestas();
		
		// set para evitar duplicados (un usuario puede responder multiples preguntas de la misma encuesta)
		Set<Integer> idEncuestas = new HashSet<>();
		
		// por cada respuesta, obtener la pregunta y luego la encuesta
		for (Integer idR : idRespuestas) {
			Respuesta r = ctrlDominioRespuesta.getRespuesta(idR);
			int idPregunta = r.getIdPregunta();
			
			Pregunta p = ctrlDominioPregunta.getPregunta(idPregunta);
			int idEncuesta = p.getIdEncuesta();
			
			idEncuestas.add(idEncuesta);
		}
		return new ArrayList<>(idEncuestas); // convertir set en un arraylist
	}

	/**
	 * Obtiene todos los usuarios almacenados en memoria.
	 * 
	 * IMPORTANTE: Este método solo debe ser llamado por CtrlDominio.cerrarSesion()
	 * para exportar todos los datos a persistencia al cerrar la aplicación.
	 * 
	 * @return TreeMap con todos los usuarios (clave: nomUsuario)
	 */
	public TreeMap<String, Usuario> getAllUsuarios() {
		return new TreeMap<>(usuarios); // devolver copia para evitar modificaciones externas
	}
	
	/**
	 * Importa usuarios desde persistencia al iniciar aplicación.
	 * 
	 * IMPORTANTE: Este método solo debe ser llamado por CtrlDominio.inicializarCtrlDominio()
	 * para cargar los datos guardados de la sesión anterior.
	 * 
	 * @param usuariosImportados TreeMap con los usuarios a cargar
	 */
	public void cargarUsuarios(TreeMap<String, Usuario> usuariosImportados) {
		this.usuarios = usuariosImportados;
	}
}
