package domaincontroller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import classcontroller.CtrlEncuesta;
import classcontroller.CtrlPregunta;
import classcontroller.CtrlUsuario;
import classcontroller.CtrlRespuesta;

import domain.Usuario;
import domain.Encuesta;
import domain.Pregunta;
import domain.Respuesta;

import persistence.CtrlPersistence;

/**
 * Controlador de Dominio - Actúa como fachada (Facade Pattern) del sistema.
 * 
 * DECISIÓN DE DISEÑO: Mantener CtrlPregunta y CtrlRespuesta
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
 * @author subgrup-prop11.5
 * @version 1.0
 */
public class CtrlDominio {

	/** Atributos - Controladores de clase (Singleton) **/
	
	private CtrlUsuario ctrlUsuario;
	private CtrlEncuesta ctrlEncuesta;
	private CtrlPregunta ctrlPregunta;
	private CtrlRespuesta ctrlRespuesta;
	private CtrlPersistence ctrlPersistence;

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
		ctrlUsuario = CtrlUsuario.getInstance();
		ctrlEncuesta = CtrlEncuesta.getInstance();
		ctrlPregunta = CtrlPregunta.getInstance();
		ctrlRespuesta = CtrlRespuesta.getInstance();
		ctrlPersistence = CtrlPersistence.getInstace();
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
	 * @throws IllegalStateException si ya existe un usuario con el mismo nombre (desde CtrlPersistence)
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
		ctrlPersistence.guardarUsuario(u); // (puede lanzar exc si ya existe)
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
		Usuario u = ctrlPersistence.cargarUsuario(nomU);
		
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
		ctrlPersistence.actualizarUsuario(u);
	}


	// pendiente: mirar excepcion de que no estaba iniciado sesión????***********************************
	/**
	 * Cierra la sesión del usuario nomUsuario
	 * 
	 * @param nomUsuario Nombre del usuario
	 */
	public void cerrarSesion(String nomUsuario) {
		Usuario u = ctrlUsuario.getUsuario(nomUsuario);
		u.cerrarSesion();
	}

	// ==================== GESTIÓN DE RESPUESTAS ====================
	
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
		// crear respuesta polimorfica segun el tipo de pregunta
		int idR = ctrlRespuesta.crearRespuesta(nomUsuario, idPregunta, respuesta);
		
		// guardar en Usuario
		Usuario u = ctrlUsuario.getUsuario(nomUsuario);
		u.guardarIdRespuesta(idR);
		
		// guardar en Pregunta
		Pregunta p = ctrlPregunta.getPregunta(idPregunta);
		p.guardarIdRespuesta(idR);
		
		// actualizar en persistencia
		ctrlPersistence.guardarRespuesta(ctrlRespuesta.getRespuesta(idR));

		return idR;
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
		Respuesta r = ctrlRespuesta.getRespuesta(idRespuesta);
		
		// modificar() en Respuesta validara el tipo
		r.modificar(newResp); // puede lanzar: TipoIncorrectoException
		
		// actualizar en persistencia
		ctrlPersistence.actualizarRespuesta(r);
	}
	
	/**
	 * Elimina una respuesta del sistema y actualiza todas las referencias.
	 * 
	 * @param idRespuesta ID de la respuesta a eliminar
	 */
	public void eliminarRespuesta(int idRespuesta) {
		Respuesta r = ctrlRespuesta.getRespuesta(idRespuesta);
		int idPregunta = r.getIdPregunta();
		String nomUsuario = r.getNomUsuario();
		
		// eliminar de Usuario
		Usuario u = ctrlUsuario.getUsuario(nomUsuario);
		u.eliminarIdRespuesta(idRespuesta);
		
		// eliminar de Pregunta
		Pregunta p = ctrlPregunta.getPregunta(idPregunta);
		p.eliminarIdRespuesta(idRespuesta);
		
		// eliminar de persistencia
		ctrlPersistence.eliminarRespuesta(idRespuesta);
		
		// eliminar del controlador (ConjuntoRespuestas)
		ctrlRespuesta.eliminar(r);
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
		// Parser del fichero (implementación específica según formato)
		List<RespuestaImportData> datosImportados = parsearFicheroRespuestas(fichero);
		
		for (RespuestaImportData dato : datosImportados) {
			// Validar que existen los IDs
			if (!ctrlEncuesta.existeEncuesta(dato.idEncuesta)) {
				throw new IllegalStateException("Encuesta con ID " + dato.idEncuesta + " no existe");
			}
			if (!ctrlPregunta.existePregunta(dato.idPregunta)) {
				throw new IllegalStateException("Pregunta con ID " + dato.idPregunta + " no existe");
			}
			if (!ctrlUsuario.existeUsuario(dato.nomUsuario)) {
				throw new IllegalStateException("Usuario " + dato.nomUsuario + " no existe");
			}
			
			// Validar que la pregunta pertenece a la encuesta
			Pregunta p = ctrlPregunta.getPregunta(dato.idPregunta);
			if (p.getIdEncuesta() != dato.idEncuesta) {
				throw new IllegalStateException("La pregunta " + dato.idPregunta + 
					" no pertenece a la encuesta " + dato.idEncuesta);
			}
			
			// Crear respuesta
			darRespuesta(dato.nomUsuario, dato.idPregunta, dato.respuesta);
		}
	}
	
	/**
	 * Clase auxiliar para datos importados de respuestas.
	 */
	private static class RespuestaImportData {
		int idEncuesta;
		int idPregunta;
		String nomUsuario;
		Object respuesta;
	}
	
	/**
	 * Parsea un fichero de respuestas. Implementación específica según formato.
	 * 
	 * @param fichero Archivo a parsear
	 * @return Lista de datos de respuestas
	 * @throws IllegalArgumentException si el formato es incorrecto
	 */
	private List<RespuestaImportData> parsearFicheroRespuestas(File fichero) {
		// TODO: Implementar según formato específico (CSV, JSON, XML, etc.)
		// Esta es una firma de método para mantener la arquitectura limpia
		throw new UnsupportedOperationException("Implementación pendiente según formato de fichero");
	}

	// ==================== GESTIÓN DE ENCUESTAS ====================
	
	/**
	 * Obtiene los IDs de las preguntas de una encuesta.
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @return Lista de IDs de preguntas
	 */
	public List<Integer> getPreguntasEncuesta(int idEncuesta) {
		Encuesta e = ctrlEncuesta.getEncuesta(idEncuesta);
		return e.getIdPreguntas();
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
		// Validación
		if (titulo == null || titulo.trim().isEmpty()) {
			throw new IllegalArgumentException("El título de la encuesta no puede estar vacío");
		}
		
		// Obtener usuario
		Usuario u = ctrlUsuario.getUsuario(nomUsuario);
		
		// Crear encuesta
		int idE = ctrlEncuesta.crearEncuesta(nomUsuario, titulo);
		
		// Guardar referencia en usuario
		u.guardarIdEncuesta(idE);
		
		// Persistir
		ctrlPersistence.guardarEncuesta(ctrlEncuesta.getEncuesta(idE));
		
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
		// Parser del fichero
		EncuestaImportData datos = parsearFicheroEncuesta(fichero);
		
		// Crear encuesta
		int idE = crearEncuesta(datos.nomAutor, datos.titulo);
		
		// Añadir preguntas
		for (PreguntaImportData infoP : datos.preguntas) {
			anadirPregunta(idE, infoP);
		}
		
		return idE;
	}
	
	/**
	 * Clase auxiliar para datos importados de encuesta.
	 */
	private static class EncuestaImportData {
		String titulo;
		String nomAutor;
		List<PreguntaImportData> preguntas;
	}
	
	/**
	 * Clase auxiliar para datos importados de pregunta.
	 */
	private static class PreguntaImportData {
		String enunciado;
		String tipo; // "libre", "numerica", "opcion_unica", "opcion_multiple"
		Map<String, Object> atributosEspecificos; // Opciones, rangos, etc.
	}
	
	/**
	 * Parsea un fichero de encuesta.
	 * 
	 * @param fichero Archivo a parsear
	 * @return Datos de la encuesta
	 * @throws IllegalArgumentException si el formato es incorrecto
	 */
	private EncuestaImportData parsearFicheroEncuesta(File fichero) {
		// TODO: Implementar según formato específico
		throw new UnsupportedOperationException("Implementación pendiente según formato de fichero");
	}
	
	/**
	 * Modifica el título de una encuesta.
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @param titulo Nuevo título
	 * @throws IllegalArgumentException si el título está vacío
	 */
	public void modificarTituloEncuesta(int idEncuesta, String titulo) {
		if (titulo == null || titulo.trim().isEmpty()) {
			throw new IllegalArgumentException("El título no puede estar vacío");
		}
		
		Encuesta e = ctrlEncuesta.getEncuesta(idEncuesta);
		e.cambiarTitulo(titulo);
		
		// Actualizar persistencia
		ctrlPersistence.actualizarEncuesta(e);
	}
	
	/**
	 * Elimina una encuesta y todas sus preguntas y respuestas asociadas.
	 * 
	 * @param idEncuesta ID de la encuesta a eliminar
	 */
	public void eliminarEncuesta(int idEncuesta) {
		// Obtener encuesta
		Encuesta e = ctrlEncuesta.getEncuesta(idEncuesta);
		
		// Obtener preguntas
		List<Integer> idPreguntas = e.getIdPreguntas();
		
		// Eliminar cada pregunta (esto eliminará también sus respuestas)
		for (Integer idP : idPreguntas) {
			eliminarPregunta(idP);
		}
		
		// Eliminar de usuario creador
		String nomCreador = e.getNomCreador();
		Usuario u = ctrlUsuario.getUsuario(nomCreador);
		u.eliminarIdEncuestaCreada(idEncuesta);
		
		// Eliminar de persistencia
		ctrlPersistence.eliminarEncuesta(idEncuesta);
		
		// Eliminar del controlador
		ctrlEncuesta.eliminar(e);
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
		// Obtener encuesta
		Encuesta e = ctrlEncuesta.getEncuesta(idEncuesta);
		
		// Obtener preguntas
		List<Integer> idPreguntas = e.getIdPreguntas();
		
		// Mapa resultado
		Map<Pregunta, Set<Respuesta>> resultado = new HashMap<>();
		
		// Por cada pregunta, obtener sus respuestas
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

	// ==================== GESTIÓN DE PREGUNTAS ====================
	
	/**
	 * Añade una pregunta a una encuesta (método polimórfico según tipo).
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @param infoP Información de la pregunta (enunciado, tipo, atributos específicos)
	 * @return ID de la pregunta creada
	 */
	public int anadirPregunta(int idEncuesta, PreguntaImportData infoP) {
		// Obtener encuesta
		Encuesta e = ctrlEncuesta.getEncuesta(idEncuesta);
		
		// Crear pregunta polimórfica según tipo
		int idP = ctrlPregunta.crearPregunta(
			idEncuesta, 
			infoP.enunciado, 
			infoP.tipo, 
			infoP.atributosEspecificos
		);
		
		// Guardar en encuesta
		e.guardarIdPregunta(idP);
		
		// Persistir
		ctrlPersistence.guardarPregunta(ctrlPregunta.getPregunta(idP));
		
		return idP;
	}
	
	/**
	 * Añade una pregunta libre a una encuesta.
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @param enunciado Enunciado de la pregunta
	 * @return ID de la pregunta creada
	 */
	public int anadirPreguntaLibre(int idEncuesta, String enunciado) {
		PreguntaImportData info = new PreguntaImportData();
		info.enunciado = enunciado;
		info.tipo = "libre";
		info.atributosEspecificos = new HashMap<>();
		return anadirPregunta(idEncuesta, info);
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
		PreguntaImportData info = new PreguntaImportData();
		info.enunciado = enunciado;
		info.tipo = "numerica";
		info.atributosEspecificos = new HashMap<>();
		info.atributosEspecificos.put("min", min);
		info.atributosEspecificos.put("max", max);
		return anadirPregunta(idEncuesta, info);
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
		PreguntaImportData info = new PreguntaImportData();
		info.enunciado = enunciado;
		info.tipo = "opcion_unica";
		info.atributosEspecificos = new HashMap<>();
		info.atributosEspecificos.put("opciones", opciones);
		return anadirPregunta(idEncuesta, info);
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
		PreguntaImportData info = new PreguntaImportData();
		info.enunciado = enunciado;
		info.tipo = "opcion_multiple";
		info.atributosEspecificos = new HashMap<>();
		info.atributosEspecificos.put("opciones", opciones);
		return anadirPregunta(idEncuesta, info);
	}
	
	/**
	 * Modifica una pregunta existente (elimina la antigua y crea una nueva).
	 * 
	 * NOTA: Este enfoque mantiene la integridad referencial pero pierde el historial.
	 * Alternativa: implementar modificación in-situ si se requiere mantener respuestas.
	 * 
	 * @param idPregunta ID de la pregunta a modificar
	 * @param infoP Nueva información de la pregunta
	 * @return ID de la nueva pregunta
	 */
	public int modificarPregunta(int idPregunta, PreguntaImportData infoP) {
		// Obtener pregunta actual para saber su encuesta
		Pregunta pVieja = ctrlPregunta.getPregunta(idPregunta);
		int idEncuesta = pVieja.getIdEncuesta();
		
		// Eliminar pregunta antigua (y sus respuestas)
		eliminarPregunta(idPregunta);
		
		// Crear nueva pregunta
		return anadirPregunta(idEncuesta, infoP);
	}
	
	/**
	 * Elimina una pregunta y todas sus respuestas asociadas.
	 * 
	 * @param idPregunta ID de la pregunta a eliminar
	 */
	public void eliminarPregunta(int idPregunta) {
		// Obtener pregunta
		Pregunta p = ctrlPregunta.getPregunta(idPregunta);
		
		// Obtener respuestas
		List<Integer> idRespuestas = p.getIdRespuestas();
		
		// Eliminar cada respuesta
		for (Integer idR : idRespuestas) {
			eliminarRespuesta(idR);
		}
		
		// Obtener encuesta y eliminar referencia
		int idEncuesta = p.getIdEncuesta();
		Encuesta e = ctrlEncuesta.getEncuesta(idEncuesta);
		e.eliminarIdPregunta(idPregunta);
		
		// Eliminar de persistencia
		ctrlPersistence.eliminarPregunta(idPregunta);
		
		// Eliminar del controlador
		ctrlPregunta.eliminar(p);
	}

	// ==================== GESTIÓN DE PERFILES ====================
	
	/**
	 * Obtiene los perfiles de un usuario (funcionalidad de clustering/análisis).
	 * 
	 * @param nomUsuario Nombre del usuario
	 * @return Lista de perfiles del usuario
	 */
	public List<Integer> getPerfilesUsuario(String nomUsuario) {
		Usuario u = ctrlUsuario.getUsuario(nomUsuario);
		return u.getIdPerfiles();
	}
}
