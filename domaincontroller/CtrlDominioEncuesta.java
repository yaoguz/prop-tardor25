package domaincontroller;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import classcontroller.CtrlUsuario;
import classcontroller.CtrlEncuesta;
import classcontroller.CtrlPregunta;
import classcontroller.CtrlRespuesta;

import domain.Usuario;
import domain.Encuesta;
import domain.Pregunta;
import domain.Respuesta;

import persistence.CtrlPersistence;

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
 * @author Yaoyao Guo
 * @version 2.0
 */
public class CtrlDominioEncuesta {

	/** Atributos - Controladores de clase **/
	
	private CtrlUsuario ctrlUsuario;
	private CtrlEncuesta ctrlEncuesta;
	private CtrlPregunta ctrlPregunta;
	private CtrlRespuesta ctrlRespuesta;
	private CtrlPersistence ctrlPersistence;

	/** Constructor **/

	/**
	 * Constructora. Inicializa los controladores necesarios.
	 * 
	 * @param ctrlUsuario Controlador de usuarios
	 * @param ctrlEncuesta Controlador de encuestas
	 * @param ctrlPregunta Controlador de preguntas
	 * @param ctrlRespuesta Controlador de respuestas
	 * @param ctrlPersistence Controlador de persistencia
	 */
	public CtrlDominioEncuesta(CtrlUsuario ctrlUsuario, CtrlEncuesta ctrlEncuesta,
	                            CtrlPregunta ctrlPregunta, CtrlRespuesta ctrlRespuesta,
	                            CtrlPersistence ctrlPersistence) {
		this.ctrlUsuario = ctrlUsuario;
		this.ctrlEncuesta = ctrlEncuesta;
		this.ctrlPregunta = ctrlPregunta;
		this.ctrlRespuesta = ctrlRespuesta;
		this.ctrlPersistence = ctrlPersistence;
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
		if (titulo == null || titulo.trim().isEmpty()) {
			throw new IllegalArgumentException("El título de la encuesta no puede estar vacío");
		}

		Usuario u = ctrlUsuario.getUsuario(nomUsuario);
		
		// crear encuesta
		int idE = ctrlEncuesta.crearEncuesta(nomUsuario, titulo);
		
		// guardar en usuario y en persistencia
		u.guardarIdEncuesta(idE);
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
		// parser del fichero
		EncuestaImportData datos = parsearFicheroEncuesta(fichero);
		
		// crear encuesta
		int idE = crearEncuesta(datos.nomAutor, datos.titulo);
		
		// anadir preguntas a la encuesta
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
		Encuesta e = ctrlEncuesta.getEncuesta(idEncuesta);
		e.cambiarTitulo(titulo);
		ctrlPersistence.actualizarEncuesta(e);
	}
	
	/**
	 * Elimina una encuesta y todas sus preguntas y respuestas asociadas.
	 * Requiere acceso a CtrlDominioPregunta para eliminar preguntas.
	 * 
	 * @param idEncuesta ID de la encuesta a eliminar
	 * @param ctrlDominioPregunta Controlador de preguntas (inyección de dependencia)
	 */
	public void eliminarEncuesta(int idEncuesta, CtrlDominioPregunta ctrlDominioPregunta) {
		Encuesta e = ctrlEncuesta.getEncuesta(idEncuesta);
		List<Integer> idPreguntas = e.getIdPreguntas();
		
		// eliminar cada pregunta (esto eliminará tambien sus respuestas)
		for (Integer idP : idPreguntas) {
			ctrlDominioPregunta.eliminarPregunta(idP);
		}
		
		// eliminar de usuario creador
		String nomCreador = e.getNomCreador();
		Usuario u = ctrlUsuario.getUsuario(nomCreador);
		u.eliminarIdEncuestaCreada(idEncuesta);
		
		// eliminar de persistencia y del controlador
		ctrlPersistence.eliminarEncuesta(idEncuesta);
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
		ctrlPersistence.guardarPregunta(ctrlPregunta.getPregunta(idP));
		return idP;
	}
}
