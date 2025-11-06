package domaincontrollers;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import domain.Usuario;
import domain.Encuesta;
import domain.Pregunta;
import domain.Respuesta;

import persistence.GestorPersistencia;

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
	private GestorPersistencia gestorPersistencia;

	/** Constructor **/

	/**
	 * Constructora. Inicializa los controladores necesarios.
	 * 
	 * @param ctrlUsuario Controlador de usuarios
	 * @param ctrlEncuesta Controlador de encuestas
	 * @param ctrlPregunta Controlador de preguntas
	 * @param ctrlRespuesta Controlador de respuestas
	 * @param gestorPersistencia Controlador de persistencia
	 */
	public CtrlDominioEncuesta(CtrlUsuario ctrlUsuario, CtrlEncuesta ctrlEncuesta,
	                            CtrlPregunta ctrlPregunta, CtrlRespuesta ctrlRespuesta,
	                            GestorPersistencia gestorPersistencia) {
		this.ctrlUsuario = ctrlUsuario;
		this.ctrlEncuesta = ctrlEncuesta;
		this.ctrlPregunta = ctrlPregunta;
		this.ctrlRespuesta = ctrlRespuesta;
		this.gestorPersistencia = gestorPersistencia;
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
		gestorPersistencia.actualizarEncuesta(e);
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
		gestorPersistencia.eliminarEncuesta(idEncuesta);
		ctrlEncuesta.eliminar(e);
	}
}
