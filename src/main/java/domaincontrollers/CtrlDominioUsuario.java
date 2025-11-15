package domaincontrollers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import domain.Pregunta;
import domain.Respuesta;
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
 * - Gestión del conjunto de usuarios en memoria (TreeMap)
 * 
 * ARQUITECTURA: Gestiona datos directamente (TreeMap), no delega a otro controlador.
 * 
 * @author Yaoyao Guo Zhang (yaoyao.guo@estudiantat.upc.edu)
 */
public class CtrlDominioUsuario {

	/** Atributos **/

	/** Instancia única del controlador (patrón singleton) */
	private static CtrlDominioUsuario singletonInstance;
	
	/** Gestor de persistencia del sistema */
	private GestorPersistencia gestor;
	
	/** Nombre del usuario con sesión activa (null si no hay sesión) */
	private String usuarioActivo;
	
	/** Controlador de respuestas (inyección de dependencia) */
	CtrlDominioRespuesta ctrlDominioRespuesta;
	
	/** Controlador de preguntas (inyección de dependencia) */
	CtrlDominioPregunta ctrlDominioPregunta;

	/**
	 * Constructor privado (patrón singleton).
	 * 
	 * @param gestor Gestor de persistencia
	 * @param ctrlDominioRespuesta Controlador de respuestas
	 * @param ctrlDominioPregunta Controlador de preguntas
	 */
	private CtrlDominioUsuario(GestorPersistencia gestor, 
								CtrlDominioRespuesta ctrlDominioRespuesta,
								CtrlDominioPregunta ctrlDominioPregunta) {
		this.gestor = gestor;
		this.usuarioActivo = null;
		this.ctrlDominioRespuesta = ctrlDominioRespuesta;
		this.ctrlDominioPregunta = ctrlDominioPregunta;
	}

	/**
	 * Obtiene la instancia única del controlador (patrón singleton).
	 * Permite actualizar las referencias circulares después de la creación inicial.
	 * 
	 * @param gestor Gestor de persistencia
	 * @param ctrlDominioRespuesta Controlador de respuestas
	 * @param ctrlDominioPregunta Controlador de preguntas
	 * @return Instancia única de CtrlDominioUsuario
	 */
	public static CtrlDominioUsuario getInstance(GestorPersistencia gestor, 
									CtrlDominioRespuesta ctrlDominioRespuesta,
									CtrlDominioPregunta ctrlDominioPregunta) {
		if (singletonInstance == null) {
			singletonInstance = new CtrlDominioUsuario(gestor,
										ctrlDominioRespuesta,
										ctrlDominioPregunta);
		} else {
			// Actualizar referencias circulares si ya existe la instancia
			if (ctrlDominioRespuesta != null) {
				singletonInstance.ctrlDominioRespuesta = ctrlDominioRespuesta;
			}
			if (ctrlDominioPregunta != null) {
				singletonInstance.ctrlDominioPregunta = ctrlDominioPregunta;
			}
		}
		return singletonInstance;
	}	
	/**
	 * Verifica si existe un usuario con el nombre dado.
	 * 
	 * @param nomU Nombre del usuario
	 * @return true si existe, false en caso contrario
	 */
	public boolean existeUsuario(String nomU) {
		return gestor.existeUsuario(nomU);
	}
	
	/**
	 * Obtiene un usuario por su nombre.
	 * 
	 * @param nomUsuario Nombre del usuario
	 * @return Usuario correspondiente
	 * @throws IllegalArgumentException si el usuario no existe
	 */
	public Usuario getUsuario(String nomUsuario) {
		Usuario u = gestor.getUsuario(nomUsuario);
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
		
		// crear y guardar
		Usuario u = new Usuario(nomU, contrasena);
		gestor.guardarUsuario(u); //usuarios.put(nomU, u);
		return nomU;
	}
	
	/**
	 * Inicia sesión de un usuario existente.
	 * 
	 * @param nomU Nombre de usuario
	 * @param contrasena Contraseña del usuario
	 * @throws IllegalArgumentException si el usuario no existe
	 * @throws IllegalStateException si la contraseña es incorrecta
	 * @throws IllegalStateException si ya hay un usuario activo
	 */
	public void iniciarSesion(String nomU, String contrasena) {
		// comprobar que no hay usuario activo
		if (usuarioActivo != null)
			throw new IllegalStateException("Ya hay un usuario activo: " + usuarioActivo);
		
		// validar que el usuario existe
		Usuario u = getUsuario(nomU);  // lanza exc si no existe
		
		// validar contraseña
		if (!u.comprobarContrasena(contrasena))
			throw new IllegalStateException("Contraseña incorrecta");
		
		// marcar sesión activa
		usuarioActivo = u.getNomUsuario();
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
	 * @param newContrasena Nueva contraseña
	 * @throws IllegalStateException si no hay un usuario activo
	 * @throws IllegalArgumentException si la nueva contraseña no cumple requisitos
	 */
	public void modificarContrasena(String newContrasena) {
	    if (usuarioActivo == null) {
			throw new IllegalStateException("No hay usuario activo");
		}
		if (newContrasena == null || newContrasena.isEmpty()) {
			throw new IllegalArgumentException("La nueva contraseña no puede estar vacía");
		}
		if (newContrasena.length() < 6) {
			throw new IllegalArgumentException("La contraseña debe tener al menos 6 caracteres");
		}
		
		Usuario u = getUsuario(usuarioActivo);
		u.newContrasena(newContrasena); // puede lanzar exc: newContrasena es igual a la actual
		gestor.actualizarUsuario(u);
	}

	/**
	 * Obtiene los IDs de las encuestas respondidas por el usuario iniciado sesion
	 * 
	 * @param ctrlDominioRespuesta Controlador de respuestas
	 * @param ctrlDominioPregunta Controlador de preguntas
	 * @return Lista de IDs de encuestas respondidas
	 * @throws IllegalStateException si no hay un usuario activo
	 */
	public List<Integer> getEncuestasRespondidas() {
		if (usuarioActivo == null) {
			throw new IllegalStateException("No hay usuario activo");
		}
		Usuario u = getUsuario(usuarioActivo);
		ArrayList<Integer> idRespuestas = u.getIdRespuestas();
		
		// set para evitar duplicados (un usuario puede responder multiples preguntas de la misma encuesta)
		Set<Integer> idEncuestas = new HashSet<>();
		
		// por cada respuesta, obtener la pregunta y luego la encuesta
		for (Integer idR : idRespuestas) {
			Respuesta r = ctrlDominioRespuesta.getRespuesta(idR);
			Pregunta p = ctrlDominioPregunta.getPregunta(r.getIdPregunta());
			idEncuestas.add(p.getIdEncuesta());
		}
		return new ArrayList<>(idEncuestas); // convertir set en un arraylist
	}

	/**
	 * Obtiene todos los usuarios del sistema.
	 * 
	 * @return HashMap con todos los usuarios (clave: nombre usuario)
	 */
	public HashMap<String, Usuario> getAllUsuarios() {
		HashMap<String, Usuario> map = new HashMap<>();
		ArrayList<Usuario> lista = gestor.getUsuarios();
		for (Usuario u : lista) {
			map.put(u.getNomUsuario(), u);
		}
		return map;
	}

	/**
	 * Obtiene las respuestas que ha respondido el usuario nomUsuario en la encuesta idEncuesta
	 * 
	 * @param nomUsuario Nombre del usuario
	 * @param idEncuesta ID de la encuesta
	 * @param ctrlDominioRespuesta Controlador de respuestas
	 * @param ctrlDominioPregunta Controlador de preguntas
	 * @return Lista de las respuestas respondidas por el usuario en esta encuesta
	 * @throws IllegalArgumentException si el usuario no ha respondido la encuesta
	 */
	public List<Respuesta> getRespuestasEncuesta(String nomUsuario, int idEncuesta) {
		Usuario u = getUsuario(nomUsuario);
		ArrayList<Integer> idRespuestas = u.getIdRespuestas();
		
		ArrayList<Respuesta> respuestas = new ArrayList<>();
												
		// por cada respuesta, mirar si coincide el idEncuesta
		for (Integer idR : idRespuestas) {
			Respuesta r = ctrlDominioRespuesta.getRespuesta(idR);
			Pregunta p = ctrlDominioPregunta.getPregunta(r.getIdPregunta());
			if (p.getIdEncuesta() == idEncuesta) respuestas.add(r);
		}

		if (respuestas.isEmpty()) {
			throw new IllegalArgumentException("El usuario " + nomUsuario + " no ha respondido la encuesta con ID " + idEncuesta);
		}

		return respuestas;
	}	
	
	// /**
	//  * Importa usuarios desde persistencia al iniciar aplicación.
	//  * 
	//  * IMPORTANTE: Este método solo debe ser llamado por CtrlDominio.inicializarCtrlDominio()
	//  * para cargar los datos guardados de la sesión anterior.
	//  * 
	//  * @param usuariosImportados TreeMap con los usuarios a cargar
	//  */
	// public void cargarUsuarios(TreeMap<String, Usuario> usuariosImportados) {
	// 	this.usuarios = usuariosImportados;
	// }
}
