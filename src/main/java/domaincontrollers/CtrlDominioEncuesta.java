package domaincontrollers;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import domain.Usuario;
import domain.Encuesta;
import domain.Pregunta;
import domain.Respuesta;

/**
 * Controlador de Dominio para la gestión de encuestas.
 * 
 * Responsabilidades:
 * - Creación de encuestas
 * - Modificación de encuestas
 * - Eliminación de encuestas
 * - Importación/Exportación de encuestas
 * - Consulta de preguntas y respuestas de encuestas
 * 
 * @author Yaoyao Guo Zhang (yaoyao.guo@estudiantat.upc.edu)
 */
public class CtrlDominioEncuesta {

	// ==================== ATRIBUTOS ====================
	
	/** Mapa de encuestas (clave: idEncuesta, valor: Encuesta) */
	private TreeMap<Integer, Encuesta> encuestas;
	
	/** ID para la próxima encuesta a crear */
	private int nextId;

	// ==================== CONSTRUCTOR ====================

	/**
	 * Constructora. Inicializa las estructuras de datos.
	 */
	public CtrlDominioEncuesta() {
		this.encuestas = new TreeMap<>();
		this.nextId = 1;
	}
	
	// ==================== MÉTODOS AUXILIARES ====================
	
	/**
	 * Verifica si existe una encuesta con el ID especificado.
	 * 
	 * @param idE ID de la encuesta
	 * @return true si existe, false en caso contrario
	 */
	public boolean existeEncuesta(int idE) {
		return encuestas.containsKey(idE);
	}
	
	/**
	 * Obtiene una instancia de Encuesta con el id "idEncuesta".
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @return Instancia de Encuesta
	 * @throws IllegalStateException si no existe una encuesta con ese ID
	 */
	public Encuesta getEncuesta(int idEncuesta) {
		Encuesta e = encuestas.get(idEncuesta);
		if (e == null) {
			throw new IllegalStateException("No existe encuesta con id " + idEncuesta);
		}
		return e;
	}

	// ==================== GESTIÓN DE ENCUESTAS ====================
	
	/**
	 * Obtiene los IDs de las preguntas de una encuesta.
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @return Lista de IDs de preguntas
	 */
	public List<Integer> getPreguntasEncuesta(int idEncuesta) {
		Encuesta e = getEncuesta(idEncuesta);
		return e.getIdPreguntas();
	}
	
	/**
	 * Crea una nueva encuesta.
	 * 
	 * @param nomUsuario Nombre del usuario creador
	 * @param titulo Título de la encuesta
	 * @param ctrlDominioUsuario Controlador de usuarios (inyección de dependencia)
	 * @return ID de la encuesta creada
	 * @throws IllegalArgumentException si el título está vacío
	 */
	public int crearEncuesta(String nomUsuario, String titulo, CtrlDominioUsuario ctrlDominioUsuario) {
		if (titulo == null || titulo.trim().isEmpty()) {
			throw new IllegalArgumentException("El título de la encuesta no puede estar vacío");
		}

		Usuario u = ctrlDominioUsuario.getUsuario(nomUsuario);
		
		// crear encuesta directamente
		int idE = nextId;
		nextId++;
		Encuesta e = new Encuesta(idE, titulo, nomUsuario);
		encuestas.put(idE, e);
		
		// guardar en usuario (NO persistencia - solo en memoria)
		u.guardarIdEncuesta(idE);
		return idE;
	}
	
	/**
	 * Importa una encuesta desde un fichero externo.
	 * 
	 * Formato esperado: {titulo, [{enunciado, [info según tipo]}], idAutor}
	 * 
	 * @param fichero Archivo con los datos de la encuesta
	 * @param ctrlDominioUsuario Controlador de usuarios (inyección de dependencia)
	 * @param ctrlDominioPregunta Controlador de preguntas (inyección de dependencia)
	 * @return ID de la encuesta creada
	 * @throws IllegalArgumentException si el formato es incorrecto
	 */
	public int importarEncuesta(File fichero,
	                             CtrlDominioUsuario ctrlDominioUsuario,
	                             CtrlDominioPregunta ctrlDominioPregunta) {
		// parser del fichero
		EncuestaImportData datos = parsearFicheroEncuesta(fichero);
		
		// crear encuesta
		int idE = crearEncuesta(datos.nomAutor, datos.titulo, ctrlDominioUsuario);
		
		// anadir preguntas a la encuesta
		for (PreguntaImportData infoP : datos.preguntas) {
			anadirPregunta(idE, infoP, ctrlDominioPregunta);
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
	public static class PreguntaImportData {
		public String enunciado;
		public String tipo; // "libre", "numerica", "opcion_unica", "opcion_multiple"
		public Map<String, Object> atributosEspecificos; // Opciones, rangos, etc.
	}
	
	/**
	 * Parsea un fichero de encuesta.
	 * 
	 * @param fichero Archivo a parsear
	 * @return Datos de la encuesta
	 * @throws IllegalArgumentException si el formato es incorrecto
	 */
	private EncuestaImportData parsearFicheroEncuesta(File fichero) {
		// TODO: Implementar según formato específico ************
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
		Encuesta e = getEncuesta(idEncuesta);
		e.cambiarTitulo(titulo);
		// NO persistencia - solo en memoria
	}
	
	/**
	 * Elimina una encuesta y todas sus preguntas y respuestas asociadas.
	 * Requiere acceso a CtrlDominioPregunta para eliminar preguntas.
	 * 
	 * @param idEncuesta ID de la encuesta a eliminar
	 * @param ctrlDominioPregunta Controlador de preguntas (inyección de dependencia)
	 * @param ctrlDominioUsuario Controlador de usuarios (inyección de dependencia)
	 */
	public void eliminarEncuesta(int idEncuesta, 
	                              CtrlDominioPregunta ctrlDominioPregunta,
	                              CtrlDominioUsuario ctrlDominioUsuario) {
		Encuesta e = getEncuesta(idEncuesta);
		List<Integer> idPreguntas = e.getIdPreguntas();
		
		// eliminar cada pregunta (esto eliminará tambien sus respuestas)
		for (Integer idP : idPreguntas) {
			ctrlDominioPregunta.eliminarPregunta(idP);
		}
		
		// eliminar de usuario creador
		String nomCreador = e.getIdCreador();
		Usuario u = ctrlDominioUsuario.getUsuario(nomCreador);
		u.eliminarIdEncuestaCreada(idEncuesta);
		
		// eliminar del TreeMap (NO persistencia - solo en memoria)
		encuestas.remove(idEncuesta);
	}
	
	/**
	 * Obtiene todas las respuestas de una encuesta organizadas por pregunta.
	 * 
	 * Esta operación pertenece a CtrlDominioEncuesta porque el aggregate root es Encuesta.
	 * Representa: "Todas las respuestas DE esta encuesta".
	 * 
	 * NOTA: Solo el autor de la encuesta debería tener acceso (validación en capa de presentación).
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @param ctrlDominioRespuesta Controlador de respuestas (inyección de dependencia)
	 * @param ctrlDominioPregunta Controlador de preguntas (inyección de dependencia)
	 * @return Mapa de Pregunta -> Set de Respuestas
	 */
	public Map<Pregunta, Set<Respuesta>> getRespuestasEncuesta(int idEncuesta,
	                            CtrlDominioRespuesta ctrlDominioRespuesta,
	                            CtrlDominioPregunta ctrlDominioPregunta) {
		Encuesta e = getEncuesta(idEncuesta);
		List<Integer> idPreguntas = e.getIdPreguntas();

		Map<Pregunta, Set<Respuesta>> resultado = new HashMap<>();
		
		// por cada pregunta, obtener sus respuestas
		for (Integer idP : idPreguntas) {
			Pregunta p = ctrlDominioPregunta.getPregunta(idP);
			List<Integer> idRespuestas = p.getIdRespuestas();
			
			Set<Respuesta> respuestas = new HashSet<>();
			for (Integer idR : idRespuestas) {
				Respuesta r = ctrlDominioRespuesta.getRespuesta(idR);
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
	 * @param ctrlDominioPregunta Controlador de preguntas (inyección de dependencia)
	 * @return ID de la pregunta creada
	 */
	private int anadirPregunta(int idEncuesta, PreguntaImportData infoP,
	                            CtrlDominioPregunta ctrlDominioPregunta) {
		Encuesta e = getEncuesta(idEncuesta);
		
		// Crear pregunta polimórfica según tipo
		int idP = ctrlDominioPregunta.crearPregunta(idEncuesta, infoP.enunciado,
                  infoP.tipo, infoP.atributosEspecificos);

		e.guardarPregunta(idP);
		// NO persistencia - solo en memoria
		return idP;
	}
	
	// ==================== MÉTODOS PARA PERSISTENCIA ====================
	
	/**
	 * Obtiene todas las encuestas almacenadas en memoria.
	 * 
	 * IMPORTANTE: Este método solo debe ser llamado por CtrlDominio.cerrarSesion()
	 * para exportar todos los datos a persistencia al cerrar la aplicación.
	 * 
	 * @return TreeMap con todas las encuestas (clave: idEncuesta)
	 */
	public TreeMap<Integer, Encuesta> getAllEncuestas() {
		return new TreeMap<>(encuestas); // devolver copia para evitar modificaciones externas
	}
	
	/**
	 * Importa encuestas desde persistencia al iniciar aplicación.
	 * 
	 * IMPORTANTE: Este método solo debe ser llamado por CtrlDominio.inicializarCtrlDominio()
	 * para cargar los datos guardados de la sesión anterior.
	 * 
	 * @param encuestasImportadas TreeMap con las encuestas a cargar
	 */
	public void cargarEncuestas(TreeMap<Integer, Encuesta> encuestasImportadas) {
		this.encuestas = encuestasImportadas;
		
		// Actualizar nextId para que sea mayor que el ID más alto
		if (!encuestas.isEmpty()) {
			this.nextId = encuestas.lastKey() + 1;
		}
	}
}
