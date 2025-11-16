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
 *   - CtrlDominioUsuario (lógica de usuarios)
 *   - CtrlDominioEncuesta (lógica de encuestas)
 *   - CtrlDominioPregunta (lógica de preguntas)
 *   - CtrlDominioRespuesta (lógica de respuestas)
 *   - CtrlDominioPerfil (lógica de clustering, no gestiona datos propios)
 *     ↓
 * CAPA DE PERSISTENCIA:
 *   - GestorPersistencia (STUB - almacenamiento en HashMap, simula BD)
 * 
 * CAMBIO ARQUITECTÓNICO IMPORTANTE:
 * Los datos (HashMap<K,V>) ya NO están en CtrlDominioXXX, sino en GestorPersistencia.
 * Los controladores de dominio solo contienen LÓGICA DE NEGOCIO.
 * 
 * RESPONSABILIDAD DE CtrlDominio (Facade):
 * - Punto de entrada único para la capa de presentación
 * - Delegación pura a controladores especializados
 * - Coordinación de operaciones que cruzan múltiples controladores
 * - NO contiene lógica de negocio compleja
 * - NO gestiona datos (HashMap están en GestorPersistencia)
 * 
 * PATRÓN SINGLETON:
 * Todos los controladores (CtrlDominio, CtrlDominioUsuario, etc.) son singleton
 * para garantizar una única instancia y estado consistente del sistema.
 * 
 * VENTAJAS:
 * 1. **Facade Pattern**: Interfaz simplificada para presentación
 * 2. **Single Responsibility**: Cada CtrlDominioXXX gestiona una entidad
 * 3. **Low Coupling**: Presentación solo conoce CtrlDominio
 * 4. **High Cohesion**: Lógica de negocio encapsulada en controladores especializados
 * 5. **Persistencia independiente**: Los datos están centralizados en un solo lugar
 * 
 * PATRÓN DE DISEÑO APLICADO:
 * - Facade Pattern: CtrlDominio oculta la complejidad de los controladores internos
 * - Dependency Injection: CtrlDominio inyecta dependencias entre controladores
 * - Singleton Pattern: Garantiza única instancia de cada controlador
 * 
 * @author Yaoyao Guo Zhang (yaoyao.guo@estudiantat.upc.edu)
 */
public class CtrlDominio {

	/** Atributos **/

	/** Instancia única del controlador (patrón singleton) */
	private static CtrlDominio singletonInstance;
	
	/** Gestor de persistencia del sistema */
	private GestorPersistencia gestorPersistencia;
	
	/** Controlador de usuarios */
	private CtrlDominioUsuario ctrlDominioUsuario;
	
	/** Controlador de encuestas */
	private CtrlDominioEncuesta ctrlDominioEncuesta;
	
	/** Controlador de preguntas */
	private CtrlDominioPregunta ctrlDominioPregunta;
	
	/** Controlador de respuestas */
	private CtrlDominioRespuesta ctrlDominioRespuesta;
	
	/** Controlador de perfiles y clustering */
	private CtrlDominioPerfil ctrlDominioPerfil;

	/** Constructor y métodos de inicialización **/

	/**
	 * Constructora por defecto. Inicializa todos los controladores.
	 * Primero crea las instancias con referencias parciales (null),
	 * luego actualiza las referencias circulares.
	 */
	private CtrlDominio() {
		gestorPersistencia = GestorPersistencia.getInstance();
		
		// Primera inicialización con referencias parciales (null donde hay dependencias circulares)
		ctrlDominioRespuesta = CtrlDominioRespuesta.getInstance(gestorPersistencia, null, null, null);
		ctrlDominioPregunta = CtrlDominioPregunta.getInstance(gestorPersistencia, ctrlDominioRespuesta, null);
		ctrlDominioUsuario = CtrlDominioUsuario.getInstance(gestorPersistencia, ctrlDominioRespuesta, ctrlDominioPregunta);
		ctrlDominioEncuesta = CtrlDominioEncuesta.getInstance(gestorPersistencia, ctrlDominioPregunta, ctrlDominioRespuesta, ctrlDominioUsuario);
		
		// Actualizar referencias circulares ahora que todos los controladores existen
		ctrlDominioRespuesta = CtrlDominioRespuesta.getInstance(gestorPersistencia, ctrlDominioUsuario, ctrlDominioEncuesta, ctrlDominioPregunta);
		ctrlDominioPregunta = CtrlDominioPregunta.getInstance(gestorPersistencia, ctrlDominioRespuesta, ctrlDominioEncuesta);
		
		ctrlDominioPerfil = CtrlDominioPerfil.getInstance(gestorPersistencia, ctrlDominioUsuario, ctrlDominioRespuesta, ctrlDominioEncuesta);
	}

    /**
	 * Implementación patrón singleton
	 * @return instancia del CtrlDominio
	 */
	public static CtrlDominio getInstance() {
		if (singletonInstance == null) {
			singletonInstance = new CtrlDominio();
		}
		return singletonInstance;
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
	 * @throws IllegalArgumentException si el usuario no existe
	 * @throws IllegalStateException si la contraseña es incorrecta
	 */
	public void iniciarSesion(String nomU, String contrasena) {
		ctrlDominioUsuario.iniciarSesion(nomU, contrasena);
	}
	
	/**
	 * Modifica la contraseña del usuario actualmente en sesión.
	 * 
	 * @param newContrasena Nueva contraseña
	 * @throws IllegalStateException si la nueva contraseña es igual a la actual
	 * @throws IllegalArgumentException si la nueva contraseña no cumple requisitos
	 */
	public void modificarContrasena(String newContrasena) {
		ctrlDominioUsuario.modificarContrasena(newContrasena);
	}

	/**
	 * Obtiene los IDs de las encuestas respondidas por el usuario actualmente en sesión.
	 * 
	 * @return Lista de IDs de encuestas respondidas
	 */
	public List<Integer> getEncuestasRespondidas() {
		return ctrlDominioUsuario.getEncuestasRespondidas();
	}

	/**
	 * Cierra la sesión del usuario actual (todos los datos ya estarán en persistencia.
	 */
	public void cerrarSesion() {
		ctrlDominioUsuario.cerrarSesionActual();
	}

	// ==================== GESTIÓN DE RESPUESTAS ====================
	// Delega en CtrlDominioRespuesta
	
	/**
	 * Crea una nueva respuesta para una pregunta específica.
	 * El nomUsuario será del usuario actualmente en sesión (se le dará desde la capa de presentación)
	 * 
	 * @param <T> Tipo de la respuesta
	 * @param nomUsuario Nombre del usuario que responde
	 * @param idPregunta ID de la pregunta
	 * @param respuesta Contenido de la respuesta
	 * @return ID de la respuesta creada
	 */
	public <T> int darRespuesta(String nomUsuario, int idPregunta, T respuesta) {
		return ctrlDominioRespuesta.darRespuesta(nomUsuario, idPregunta, respuesta);
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
		ctrlDominioRespuesta.eliminarRespuesta(idRespuesta);
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
		ctrlDominioRespuesta.importarRespuestas(fichero);
	}

	// ==================== GESTIÓN DE ENCUESTAS ====================
	// Delega en CtrlDominioEncuesta
	
	/**
	 * Obtiene los IDs de las preguntas de una encuesta.
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @return Lista de IDs de preguntas
	 */
	public List<Integer> getIdPreguntasEncuesta(int idEncuesta) {
		return ctrlDominioEncuesta.getIdPreguntasEncuesta(idEncuesta);
	}

	/**
	 * Obtiene las preguntas de una encuesta.
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @return Lista de preguntas
	 */
	public List<Pregunta> getPreguntasEncuesta(int idEncuesta) {
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
		return ctrlDominioEncuesta.crearEncuesta(nomUsuario, titulo);
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
		return ctrlDominioEncuesta.importarEncuesta(fichero);
	}
	
	/**
	 * Modifica el título de una encuesta.
	 * 
	 * NOTA: Solo el autor de la encuesta puede tener acceso (validación en capa de presentación).
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
	 * NOTA: Solo el autor de la encuesta puede tener acceso (validación en capa de presentación).
	 * 
	 * @param idEncuesta ID de la encuesta a eliminar
	 */
	public void eliminarEncuesta(int idEncuesta) {
		ctrlDominioEncuesta.eliminarEncuesta(idEncuesta);
	}
	
	/**
	 * Obtiene las respuestas de todos los usuarios que han respondido una encuesta.
	 * 
	 * NOTA: Solo el autor de la encuesta puede tener acceso (validación en capa de presentación).
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @return Mapa de Pregunta -> Set de Respuestas
	 */
	public Map<Pregunta, Set<Respuesta>> getRespuestasEncuesta(int idEncuesta) {
		return ctrlDominioEncuesta.getRespuestasEncuesta(idEncuesta);
	}

	// /**
	//  * Obtiene las respuestas de una encuesta en formato matriz para clustering.
	//  * Formato: List<List<Respuesta>> donde cada sublista es un usuario con sus respuestas ordenadas.
	//  * Las respuestas no contestadas se representan con null.
	//  * 
	//  * @param idEncuesta ID de la encuesta
	//  * @return Matriz M x N (M usuarios, N preguntas)
	//  */
	// public List<List<Respuesta>> getRespuestasParaClustering(int idEncuesta) {
	// 	return ctrlDominioEncuesta.getRespuestasParaClustering(idEncuesta);
	// }

	// ==================== GESTIÓN DE PREGUNTAS ====================
	// Delega en CtrlDominioPregunta
	
	/**
	 * Añade una pregunta libre a una encuesta.
	 * 
	 * NOTA: Solo el autor de la encuesta puede tener acceso (validación en capa de presentación).
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @param enunciado Enunciado de la pregunta
	 * @return ID de la pregunta creada
	 */
	public int anadirPreguntaLibre(int idEncuesta, String enunciado) {
		return ctrlDominioPregunta.crearPreguntaLibre(idEncuesta, enunciado);
	}
	
	/**
	 * Añade una pregunta numérica a una encuesta.
	 * 
	 * NOTA: Solo el autor de la encuesta puede tener acceso (validación en capa de presentación).
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @param enunciado Enunciado de la pregunta
	 * @param min Valor mínimo permitido
	 * @param max Valor máximo permitido
	 * @return ID de la pregunta creada
	 */
	public int anadirPreguntaNumerica(int idEncuesta, String enunciado, double min, double max) {
		return ctrlDominioPregunta.crearPreguntaNumerica(idEncuesta, enunciado, (int)min, (int)max);
	}
	
	/**
	 * Añade una pregunta de opción única a una encuesta.
	 * 
	 * NOTA: Solo el autor de la encuesta puede tener acceso (validación en capa de presentación).
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @param enunciado Enunciado de la pregunta
	 * @param opciones Lista de opciones disponibles
	 * @return ID de la pregunta creada
	 */
	public int anadirPreguntaOpcionUnica(int idEncuesta, String enunciado, List<String> opciones, boolean ordenada) {
		return ctrlDominioPregunta.crearPreguntaOpcionUnica(idEncuesta, enunciado, opciones, ordenada);
	}
	
	/**
	 * Añade una pregunta de opción múltiple (sin limite de numMaxOpc) a una encuesta.
	 * 
	 * NOTA: Solo el autor de la encuesta puede tener acceso (validación en capa de presentación).
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @param enunciado Enunciado de la pregunta
	 * @param opciones Lista de opciones disponibles
	 * @return ID de la pregunta creada
	 */
	public int anadirPreguntaOpcionMultiple(int idEncuesta, String enunciado, List<String> opciones) {
		// usar numMaxOpc = tamano de opciones como valor por defecto
		int numMaxOpc = (opciones != null) ? opciones.size() : 10;
		return ctrlDominioPregunta.crearPreguntaOpcionMultiple(idEncuesta, enunciado, opciones, numMaxOpc);
	}

	/**
	 * Añade una pregunta de opción múltiple (con numMaxOpc) a una encuesta.
	 * 
	 * NOTA: Solo el autor de la encuesta puede tener acceso (validación en capa de presentación).
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @param enunciado Enunciado de la pregunta
	 * @param opciones Lista de opciones disponibles
	 * @param numMaxOpc número máximo de opciones que se pueden seleccionar
	 * @return ID de la pregunta creada
	 */
	public int anadirPreguntaOpcionMultiple(int idEncuesta, String enunciado, List<String> opciones, int numMaxOpc) {
		return ctrlDominioPregunta.crearPreguntaOpcionMultiple(idEncuesta, enunciado, opciones, numMaxOpc);
	}
	
	/**
	 * Modifica una pregunta existente (elimina la antigua y crea una nueva).
	 * 
	 * NOTA: Solo el autor de la encuesta puede tener acceso (validación en capa de presentación).
	 * 
	 * @param idPregunta ID de la pregunta a modificar
	 * @param infoP Nueva información de la pregunta (el tipo da igual, se sobreescribe con el de la pregunta a modificar)
	 * @return ID de la nueva pregunta
	 */
	public int modificarPregunta(int idPregunta, CtrlDominioPregunta.PreguntaImportData infoP) {
		return ctrlDominioPregunta.modificarPregunta(idPregunta, infoP);
	}
	
	/**
	 * Elimina una pregunta y todas sus respuestas asociadas.
	 * 
	 * NOTA: Solo el autor de la encuesta puede tener acceso (validación en capa de presentación).
	 * 
	 * @param idPregunta ID de la pregunta a eliminar
	 */
	public void eliminarPregunta(int idPregunta) {
		ctrlDominioPregunta.eliminarPregunta(idPregunta);
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
		return ctrlDominioPerfil.getPerfilesUsuario(nomUsuario);
	}

	/**
     * Ejecuta análisis de clustering sobre una encuesta.
     * 
     * @param idEncuesta ID de la encuesta a analizar
     * @param k Número de clusters (perfiles) deseados
     * @param algoritmo "kmeans" o "kmeans++"
     * @return Lista de perfiles obtenidos
     */
    public domain.algorithm.estructuras.ResultadoClustering ejecutarClustering(int idEncuesta, int k, String algoritmo) {
        return ctrlDominioPerfil.ejecutarClustering(idEncuesta, k, algoritmo);
    }

	/**
	 * Obtiene los miembros de un perfil.
	 * 
	 * @param perfil Perfil
	 * @return Lista de nombres de usuarios
	 */
	public List<String> obtenerMiembrosPerfil(domain.algorithm.estructuras.Perfil perfil) {
		return ctrlDominioPerfil.obtenerMiembrosPerfil(perfil);
	}

	/**
	 * Obtiene el representante de un perfil.
	 * 
	 * @param perfil Perfil
	 * @return Nombre del representante
	 */
	public String obtenerRepresentantePerfil(domain.algorithm.estructuras.Perfil perfil) {
		return ctrlDominioPerfil.obtenerRepresentantePerfil(perfil);
	}
}
