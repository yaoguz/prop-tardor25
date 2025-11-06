package domaincontrollers;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Set;

import domain.Pregunta;
import domain.Respuesta;
import persistence.GestorPersistencia;

/**
 * Controlador de Dominio - Actúa como fachada (Facade Pattern) del sistema.
 * 
 * ARQUITECTURA 3 CAPAS (según PDF info_3capas.txt):
 * 
 * CAPA DE PRESENTACIÓN
 *     ↓
 * CAPA DE DOMINIO:
 *   - CtrlDominio (Facade MINIMALISTA - punto de entrada, solo delega)
 *   - CtrlDominioUsuario (gestiona TreeMap<String, Usuario> + lógica de usuarios)
 *   - CtrlDominioEncuesta (gestiona TreeMap<Integer, Encuesta> + lógica de encuestas)
 *   - CtrlDominioPregunta (gestiona TreeMap<Integer, Pregunta> + lógica de preguntas)
 *   - CtrlDominioRespuesta (gestiona TreeMap<Integer, Respuesta> + lógica de respuestas)
 *   - CtrlDominioPerfil (lógica de clustering, no gestiona datos propios)
 *     ↓
 * CAPA DE PERSISTENCIA:
 *   - GestorPersistencia (STUB - import/export en apertura/cierre de sesión)
 * 
 * RESPONSABILIDAD DE CtrlDominio (Facade):
 * - Punto de entrada único para la capa de presentación
 * - Delegación pura a controladores especializados
 * - Coordinación de operaciones que cruzan múltiples controladores
 * - NO contiene lógica de negocio compleja
 * - NO gestiona datos (TreeMaps están en CtrlDominioXXX)
 * 
 * VENTAJAS:
 * 1. **Facade Pattern**: Interfaz simplificada para presentación
 * 2. **Single Responsibility**: Cada CtrlDominioXXX gestiona una entidad
 * 3. **Low Coupling**: Presentación solo conoce CtrlDominio
 * 4. **High Cohesion**: Lógica de negocio encapsulada en controladores especializados
 * 
 * PATRÓN DE DISEÑO APLICADO:
 * - Facade Pattern: CtrlDominio oculta la complejidad de los controladores internos
 * - Dependency Injection: CtrlDominio inyecta dependencias entre controladores
 * 
 * @author Yaoyao Guo Zhang (yaoyao.guo@estudiantat.upc.edu)
 */
public class CtrlDominio {

	/** Atributos - Capa de Datos **/

	private GestorPersistencia gestorPersistencia;

	/** Atributos - Controladores de dominio especializados **/
	
	private CtrlDominioUsuario ctrlDominioUsuario;
	private CtrlDominioEncuesta ctrlDominioEncuesta;
	private CtrlDominioPregunta ctrlDominioPregunta;
	private CtrlDominioRespuesta ctrlDominioRespuesta;
	private CtrlDominioPerfil ctrlDominioPerfil;

	/** Constructor y métodos de inicialización **/

	/**
	 * Constructora por defecto. Inicializa todos los controladores.
	 */
	public CtrlDominio() {
		inicializarCtrlDominio();
	}

	/**
	 * Inicializa y carga todos los datos del sistema.
	 * 
	 * IMPORTANTE: Este método se llama al iniciar la aplicación.
	 * Carga todos los datos desde persistencia a memoria.
	 */
	public void inicializarCtrlDominio() {
		// inicializar gestor de persistencia
		gestorPersistencia = GestorPersistencia.getInstance();
		
		// inicializar controladores de dominio especializados (sin dependencias)
		ctrlDominioUsuario = new CtrlDominioUsuario();
		ctrlDominioEncuesta = new CtrlDominioEncuesta();
		ctrlDominioPregunta = new CtrlDominioPregunta();
		ctrlDominioRespuesta = new CtrlDominioRespuesta();
		ctrlDominioPerfil = new CtrlDominioPerfil();
		
		// cargar datos desde persistencia
		try {
			ctrlDominioUsuario.cargarUsuarios(gestorPersistencia.cargarTodosUsuarios());
			ctrlDominioEncuesta.cargarEncuestas(gestorPersistencia.cargarTodasEncuestas());
			ctrlDominioPregunta.cargarPreguntas(gestorPersistencia.cargarTodasPreguntas());
			ctrlDominioRespuesta.cargarRespuestas(gestorPersistencia.cargarTodasRespuestas());
		} catch (Exception e) {
			// Si es la primera vez que se ejecuta la app, no hay datos que cargar
			System.out.println("Iniciando aplicación sin datos previos (primera ejecución)");
		}
	}

	// ==================== GESTIÓN DE USUARIOS ====================
	// Delega en CtrlDominioUsuario
	
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
		return ctrlDominioUsuario.registrarUsuario(nomU, contrasena, confirmCont);
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
		return ctrlDominioUsuario.iniciarSesion(nomU, contrasena);
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
		ctrlDominioUsuario.modificarContrasena(nomUsuario, newContrasena);
	}

	/**
	 * Obtiene los IDs de las encuestas respondidas por un usuario.
	 * 
	 * @param nomUsuario Nombre del usuario
	 * @return Lista de IDs de encuestas respondidas
	 */
	public List<Integer> getEncuestasRespondidas(String nomUsuario) {
		return ctrlDominioUsuario.getEncuestasRespondidas(nomUsuario, 
		                                                   ctrlDominioRespuesta, 
		                                                   ctrlDominioPregunta);
	}

	/**
	 * Cierra la sesión del usuario actual y guarda todos los datos en persistencia.
	 * 
	 * ARQUITECTURA: Este es el ÚNICO momento en que se llama a persistencia para guardar.
	 * Durante toda la sesión, los datos viven en memoria (TreeMaps en cada CtrlDominioXXX).
	 * 
	 * Pasos:
	 * 1. Exportar todos los datos a persistencia (GestorPersistencia)
	 * 2. Cerrar sesión del usuario actual
	 * 
	 * @throws IllegalStateException si hay errores al guardar en persistencia
	 */
	public void cerrarSesion() {
		// 1. GUARDAR TODO EN PERSISTENCIA (solo aquí, no durante operaciones CRUD)
		try {
			// Cada CtrlDominioXXX debe implementar un método getAllXXX() que devuelva su TreeMap
			gestorPersistencia.guardarTodosUsuarios(ctrlDominioUsuario.getAllUsuarios());
			gestorPersistencia.guardarTodasEncuestas(ctrlDominioEncuesta.getAllEncuestas());
			gestorPersistencia.guardarTodasPreguntas(ctrlDominioPregunta.getAllPreguntas());
			gestorPersistencia.guardarTodasRespuestas(ctrlDominioRespuesta.getAllRespuestas());
			// ctrlDominioPerfil no tiene datos propios, solo calcula sobre usuarios
		} catch (Exception e) {
			throw new IllegalStateException("Error al guardar datos en persistencia: " + e.getMessage());
		}
		
		// 2. Cerrar sesión del usuario
		ctrlDominioUsuario.cerrarSesionActual();
	}

	// ==================== GESTIÓN DE RESPUESTAS ====================
	// Delega en CtrlDominioRespuesta
	
	/**
	 * Crea una nueva respuesta para una pregunta específica.
	 * 
	 * @param <T> Tipo de la respuesta
	 * @param nomUsuario Nombre del usuario que responde
	 * @param idPregunta ID de la pregunta
	 * @param respuesta Contenido de la respuesta
	 * @return ID de la respuesta creada
	 */
	public <T> int darRespuesta(String nomUsuario, int idPregunta, T respuesta) {
		return ctrlDominioRespuesta.darRespuesta(nomUsuario, idPregunta, respuesta,
		                                          ctrlDominioUsuario, ctrlDominioPregunta);
	}
	
	/**
	 * Modifica una respuesta existente.
	 * 
	 * @param <T> Tipo de la respuesta (String, Integer, List<String>, etc.)
	 * @param idRespuesta ID de la respuesta a modificar
	 * @param newResp Nueva respuesta
	 * @throws IllegalArgumentException si el tipo de respuesta no coincide con el tipo de pregunta
	 */
	public <T> void modificarRespuesta(int idRespuesta, T newResp) {
		ctrlDominioRespuesta.modificarRespuesta(idRespuesta, newResp);
	}
	
	/**
	 * Elimina una respuesta del sistema y actualiza todas las referencias.
	 * 
	 * @param idRespuesta ID de la respuesta a eliminar
	 */
	public void eliminarRespuesta(int idRespuesta) {
		ctrlDominioRespuesta.eliminarRespuesta(idRespuesta, ctrlDominioUsuario, ctrlDominioPregunta);
	}
	
	/**
	 * Importa respuestas desde un fichero externo.
	 * 
	 * DECISIÓN: Cada fichero contiene el idEncuesta para validación.
	 * 
	 * @param fichero Archivo con formato: idEncuesta, idPregunta, nomUsuario, respuesta
	 * @throws IllegalArgumentException si el formato es incorrecto
	 * @throws IllegalStateException si los IDs no existen en el sistema
	 */
	public void importarRespuestas(File fichero) {
		ctrlDominioRespuesta.importarRespuestas(fichero, ctrlDominioUsuario, 
		                                         ctrlDominioEncuesta, ctrlDominioPregunta);
	}

	// ==================== GESTIÓN DE ENCUESTAS ====================
	// Delega en CtrlDominioEncuesta
	
	/**
	 * Obtiene los IDs de las preguntas de una encuesta.
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @return Lista de IDs de preguntas
	 */
	public List<Integer> getPreguntasEncuesta(int idEncuesta) {
		return ctrlDominioEncuesta.getPreguntasEncuesta(idEncuesta);
	}
	
	/**
	 * Crea una nueva encuesta.
	 * 
	 * @param nomUsuario Nombre del usuario creador
	 * @param titulo Título de la encuesta
	 * @return ID de la encuesta creada
	 * @throws IllegalArgumentException si el título está vacío
	 */
	public int crearEncuesta(String nomUsuario, String titulo) {
		return ctrlDominioEncuesta.crearEncuesta(nomUsuario, titulo, ctrlDominioUsuario);
	}
	
	/**
	 * Importa una encuesta desde un fichero externo.
	 * 
	 * Formato esperado: {titulo, [{enunciado, [info según tipo]}], idAutor}
	 * 
	 * @param fichero Archivo con los datos de la encuesta
	 * @return ID de la encuesta creada
	 * @throws IllegalArgumentException si el formato es incorrecto
	 */
	public int importarEncuesta(File fichero) {
		return ctrlDominioEncuesta.importarEncuesta(fichero, ctrlDominioUsuario, ctrlDominioPregunta);
	}
	
	/**
	 * Modifica el título de una encuesta.
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @param titulo Nuevo título
	 * @throws IllegalArgumentException si el título está vacío
	 */
	public void modificarTituloEncuesta(int idEncuesta, String titulo) {
		ctrlDominioEncuesta.modificarTituloEncuesta(idEncuesta, titulo);
	}
	
	/**
	 * Elimina una encuesta y todas sus preguntas y respuestas asociadas.
	 * 
	 * @param idEncuesta ID de la encuesta a eliminar
	 */
	public void eliminarEncuesta(int idEncuesta) {
		ctrlDominioEncuesta.eliminarEncuesta(idEncuesta, ctrlDominioPregunta, 
									ctrlDominioRespuesta, ctrlDominioUsuario);
	}
	
	/**
	 * Obtiene todas las respuestas de una encuesta organizadas por pregunta.
	 * 
	 * NOTA: Solo el autor de la encuesta debería tener acceso (validación en capa de presentación).
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @return Mapa de Pregunta -> Set de Respuestas
	 */
	public Map<Pregunta, Set<Respuesta>> getRespuestasEncuesta(int idEncuesta) {
		return ctrlDominioEncuesta.getRespuestasEncuesta(idEncuesta, ctrlDominioRespuesta, 
		                                                   ctrlDominioPregunta);
	}

	// ==================== GESTIÓN DE PREGUNTAS ====================
	// Delega en CtrlDominioPregunta
	
	/**
	 * Añade una pregunta libre a una encuesta.
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @param enunciado Enunciado de la pregunta
	 * @return ID de la pregunta creada
	 */
	public int anadirPreguntaLibre(int idEncuesta, String enunciado) {
		return ctrlDominioPregunta.anadirPreguntaLibre(idEncuesta, enunciado, ctrlDominioEncuesta);
	}
	
	/**
	 * Añade una pregunta numérica a una encuesta.
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @param enunciado Enunciado de la pregunta
	 * @param min Valor mínimo permitido
	 * @param max Valor máximo permitido
	 * @return ID de la pregunta creada
	 */
	public int anadirPreguntaNumerica(int idEncuesta, String enunciado, double min, double max) {
		return ctrlDominioPregunta.anadirPreguntaNumerica(idEncuesta, enunciado, min, max, ctrlDominioEncuesta);
	}
	
	/**
	 * Añade una pregunta de opción única a una encuesta.
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @param enunciado Enunciado de la pregunta
	 * @param opciones Lista de opciones disponibles
	 * @return ID de la pregunta creada
	 */
	public int anadirPreguntaOpcionUnica(int idEncuesta, String enunciado, List<String> opciones) {
		return ctrlDominioPregunta.anadirPreguntaOpcionUnica(idEncuesta, enunciado, opciones, ctrlDominioEncuesta);
	}
	
	/**
	 * Añade una pregunta de opción múltiple a una encuesta.
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @param enunciado Enunciado de la pregunta
	 * @param opciones Lista de opciones disponibles
	 * @return ID de la pregunta creada
	 */
	public int anadirPreguntaOpcionMultiple(int idEncuesta, String enunciado, List<String> opciones) {
		return ctrlDominioPregunta.anadirPreguntaOpcionMultiple(idEncuesta, enunciado, opciones, ctrlDominioEncuesta);
	}
	
	/**
	 * Modifica una pregunta existente (elimina la antigua y crea una nueva).
	 * 
	 * NOTA: Este enfoque mantiene la integridad referencial pero pierde el historial.
	 * (Alternativa: implementar modificación in-situ si se requiere mantener respuestas.)
	 * 
	 * @param idPregunta ID de la pregunta a modificar
	 * @param infoP Nueva información de la pregunta
	 * @return ID de la nueva pregunta
	 */
	public int modificarPregunta(int idPregunta, CtrlDominioPregunta.PreguntaImportData infoP) {
		return ctrlDominioPregunta.modificarPregunta(idPregunta, infoP, ctrlDominioRespuesta, ctrlDominioEncuesta, ctrlDominioUsuario);
	}
	
	/**
	 * Elimina una pregunta y todas sus respuestas asociadas.
	 * 
	 * @param idPregunta ID de la pregunta a eliminar
	 */
	public void eliminarPregunta(int idPregunta) {
		ctrlDominioPregunta.eliminarPregunta(idPregunta, ctrlDominioRespuesta, ctrlDominioEncuesta, ctrlDominioUsuario);
	}

	// ==================== GESTIÓN DE PERFILES ====================
	// Delega en CtrlDominioPerfil
	
	/**
	 * Obtiene los perfiles de un usuario (funcionalidad de clustering/análisis).
	 * 
	 * @param nomUsuario Nombre del usuario
	 * @return Lista de perfiles del usuario
	 */
	public List<Integer> getPerfilesUsuario(String nomUsuario) {
		return ctrlDominioPerfil.getPerfilesUsuario(nomUsuario, ctrlDominioUsuario);
	}
}
