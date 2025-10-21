package domaincontroller;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import classcontroller.CtrlUsuario;
import classcontroller.CtrlRespuesta;
import classcontroller.CtrlPregunta;
import classcontroller.CtrlEncuesta;

import domain.Usuario;
import domain.Respuesta;
import domain.Pregunta;

import persistence.CtrlPersistence;

/**
 * Controlador de Dominio para la gestión de respuestas.
 * 
 * Responsabilidades:
 * - Creación de respuestas
 * - Modificación de respuestas
 * - Eliminación de respuestas
 * - Importación de respuestas desde ficheros
 * - Consulta de encuestas respondidas por usuario
 * 
 * @author Yaoyao Guo
 * @version 2.0
 */
public class CtrlDominioRespuesta {

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
	public CtrlDominioRespuesta(CtrlUsuario ctrlUsuario, CtrlEncuesta ctrlEncuesta,
	                             CtrlPregunta ctrlPregunta, CtrlRespuesta ctrlRespuesta,
	                             CtrlPersistence ctrlPersistence) {
		this.ctrlUsuario = ctrlUsuario;
		this.ctrlEncuesta = ctrlEncuesta;
		this.ctrlPregunta = ctrlPregunta;
		this.ctrlRespuesta = ctrlRespuesta;
		this.ctrlPersistence = ctrlPersistence;
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
		// TO DO: Implementar según formato específico (CSV, JSON, XML, etc.) **********************
		// Esta es una firma de método para mantener la arquitectura limpia
		throw new UnsupportedOperationException("Implementación pendiente según formato de fichero");
	}
}
