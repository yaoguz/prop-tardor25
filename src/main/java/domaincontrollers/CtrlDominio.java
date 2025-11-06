package domaincontrollers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import domain.Encuesta;
import domain.Pregunta;
import domain.Respuesta;
import domain.Usuario;
import persistence.GestorPersistencia;

/**
 * Controlador de Dominio - Actúa como fachada (Facade Pattern) del sistema.
 * 
 * REFACTORIZACIÓN v2.0:
 * 
 * Este controlador ahora actúa como ORQUESTADOR PRINCIPAL que delega en
 * controladores de dominio especializados:
 * 
 * - CtrlDominioUsuario: Gestión de usuarios (registro, login, sesión)
 * - CtrlDominioEncuesta: Gestión de encuestas (CRUD, importar/exportar)
 * - CtrlDominioPregunta: Gestión de preguntas (añadir, modificar, eliminar)
 * - CtrlDominioRespuesta: Gestión de respuestas (crear, modificar, eliminar)
 * - CtrlDominioPerfil: Gestión de perfiles (clustering, análisis)
 * 
 * VENTAJAS:
 * 1. **Single Responsibility**: Cada controlador tiene una responsabilidad clara
 * 2. **Mantenibilidad**: Cambios en una funcionalidad no afectan a otras
 * 3. **Testabilidad**: Cada controlador se puede testear independientemente
 * 4. **Escalabilidad**: Fácil añadir nuevas funcionalidades
 * 
 * DECISIÓN DE DISEÑO: Mantener CtrlPregunta y CtrlRespuesta (capa de datos)
 * 
 * RAZONES:
 * 1. **Single Responsibility Principle (SRP)**: Cada controlador de clase maneja 
 *    exclusivamente la colección de sus objetos (CRUD + gestión de IDs).
 * 
 * 2. **Separation of Concerns**: 
 *    - CtrlPregunta: Gestiona ConjuntoPreguntas, crea preguntas polimórficas 
 *      (Libre, Numérica, OpciónÚnica, OpciónMúltiple)
 *    - CtrlRespuesta: Gestiona ConjuntoRespuestas, crea respuestas polimórficas 
 *      correspondientes
 * 
 * 3. **Facade Pattern**: CtrlDominio orquesta las operaciones complejas entre 
 *    múltiples controladores, pero NO debe asumir la responsabilidad de gestionar 
 *    colecciones de objetos.
 * 
 * 4. **Cohesión y Acoplamiento**: Mantener controladores específicos reduce el 
 *    acoplamiento y mejora la cohesión del sistema.
 * 
 * 5. **Polimorfismo**: Los controladores de clase son el lugar natural para 
 *    implementar factory methods que crean el tipo correcto de Pregunta/Respuesta.
 * 
 * ALTERNATIVA RECHAZADA: Eliminar CtrlPregunta/CtrlRespuesta haría que CtrlDominio
 * se convierta en un "God Object" que viola SRP y dificulta el mantenimiento.
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
	 * Inicializa todas las instancias singleton de los controladores.
	 */
	public void inicializarCtrlDominio() {
		// Inicializar gestor de persistencia
		gestorPersistencia = GestorPersistencia.getInstace();
		
		// Inicializar controladores de dominio especializados
		ctrlDominioUsuario = new CtrlDominioUsuario(ctrlUsuario, gestorPersistencia);
		ctrlDominioEncuesta = new CtrlDominioEncuesta(ctrlUsuario, ctrlEncuesta, 
		                                                ctrlPregunta, ctrlRespuesta, gestorPersistencia);
		ctrlDominioPregunta = new CtrlDominioPregunta(ctrlEncuesta, ctrlPregunta, gestorPersistencia);
		ctrlDominioRespuesta = new CtrlDominioRespuesta(ctrlUsuario, ctrlEncuesta, 
		                                                 ctrlPregunta, ctrlRespuesta);
		ctrlDominioPerfil = new CtrlDominioPerfil(ctrlUsuario);
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
		ctrlUsuario.cerrarSesion();
	}

	// ==================== GESTIÓN DE RESPUESTAS ====================
	// Delega en CtrlDominioRespuesta
	
	/**
	 * Obtiene los IDs de las encuestas respondidas por un usuario.
	 * 
	 * @param nomUsuario Nombre del usuario
	 * @return Lista de IDs de encuestas respondidas
	 */
	public List<Integer> getEncuestasRespondidas(String nomUsuario) {
		Usuario u = ctrlUsuario.getUsuario(nomUsuario);
		List<Integer> idRespuestas = u.getIdRespuestas();
		
		// set para evitar duplicados (un usuario puede responder multiples preguntas de la misma encuesta)
		Set<Integer> idEncuestasSet = new HashSet<>();
		
		// por cada respuesta, obtener la pregunta y luego la encuesta
		for (Integer idR : idRespuestas) {
			Respuesta r = ctrlRespuesta.getRespuesta(idR);
			int idPregunta = r.getIdPregunta();
			
			Pregunta p = ctrlPregunta.getPregunta(idPregunta);
			int idEncuesta = p.getIdEncuesta();
			
			idEncuestasSet.add(idEncuesta);
		}
		return new ArrayList<>(idEncuestasSet); // convertir set en un arraylist
	}

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
		if (titulo == null || titulo.trim().isEmpty()) {
			throw new IllegalArgumentException("El título de la encuesta no puede estar vacío");
		}

		Usuario u = ctrlUsuario.getUsuario(nomUsuario);
		
		// crear encuesta
		int idE = ctrlEncuesta.crearEncuesta(nomUsuario, titulo);
		
		// guardar en usuario y en persistencia
		u.guardarIdEncuesta(idE);
		gestorPersistencia.guardarEncuesta(ctrlEncuesta.getEncuesta(idE));
		return idE;
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
		ctrlDominioEncuesta.eliminarEncuesta(idEncuesta, ctrlDominioPregunta);
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
		Encuesta e = ctrlEncuesta.getEncuesta(idEncuesta);
		List<Integer> idPreguntas = e.getIdPreguntas();

		Map<Pregunta, Set<Respuesta>> resultado = new HashMap<>();
		
		// por cada pregunta, obtener sus respuestas
		for (Integer idP : idPreguntas) {
			Pregunta p = ctrlPregunta.getPregunta(idP);
			List<Integer> idRespuestas = p.getIdRespuestas();
			
			Set<Respuesta> respuestas = new HashSet<>();
			for (Integer idR : idRespuestas) {
				Respuesta r = ctrlRespuesta.getRespuesta(idR);
				respuestas.add(r);
			}
			
			resultado.put(p, respuestas);
		}
		return resultado;
	}

	/**
	 * Añade una pregunta a una encuesta (método polimórfico según tipo).
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @param infoP Información de la pregunta (enunciado, tipo, atributos específicos)
	 * @return ID de la pregunta creada
	 */
	private int anadirPregunta(int idEncuesta, PreguntaImportData infoP) {
		Encuesta e = ctrlEncuesta.getEncuesta(idEncuesta);
		
		// Crear pregunta polimórfica según tipo
		int idP = ctrlPregunta.crearPregunta(idEncuesta, infoP.enunciado,
                  infoP.tipo, infoP.atributosEspecificos);

		e.guardarIdPregunta(idP);
		gestorPersistencia.guardarPregunta(ctrlPregunta.getPregunta(idP));
		return idP;
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
		return ctrlDominioPregunta.anadirPreguntaLibre(idEncuesta, enunciado);
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
		return ctrlDominioPregunta.anadirPreguntaNumerica(idEncuesta, enunciado, min, max);
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
		return ctrlDominioPregunta.anadirPreguntaOpcionUnica(idEncuesta, enunciado, opciones);
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
		return ctrlDominioPregunta.anadirPreguntaOpcionMultiple(idEncuesta, enunciado, opciones);
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
		return ctrlDominioPregunta.modificarPregunta(idPregunta, infoP, ctrlDominioRespuesta);
	}
	
	/**
	 * Elimina una pregunta y todas sus respuestas asociadas.
	 * 
	 * @param idPregunta ID de la pregunta a eliminar
	 */
	public void eliminarPregunta(int idPregunta) {
		ctrlDominioPregunta.eliminarPregunta(idPregunta, ctrlDominioRespuesta);
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
}
