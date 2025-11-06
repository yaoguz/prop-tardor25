package domaincontrollers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeMap;

import domain.Pregunta;
import domain.Respuesta;
import domain.RespuestaLibre;
import domain.RespuestaNumerica;
import domain.RespuestaOpcionUnica;
import domain.RespuestaOpcionMultiple;
import domain.Usuario;

/**
 * Controlador de Dominio para la gestión de respuestas.
 * 
 * Responsabilidades:
 * - Creación de respuestas
 * - Modificación de respuestas
 * - Eliminación de respuestas
 * - Importación de respuestas desde ficheros
 * - Consulta de encuestas respondidas por usuario
 * - Gestión del conjunto de respuestas en memoria (TreeMap)
 * 
 * @author Yaoyao Guo Zhang (yaoyao.guo@estudiantat.upc.edu)
 */
public class CtrlDominioRespuesta {

	/** Atributos **/
	
	// Los datos se guardan en un TreeMap con clave - idRespuesta (int)
	private TreeMap<Integer, Respuesta> respuestas;
	private int nextId;
	
	// Referencias a otros controladores de dominio
	private CtrlDominioUsuario ctrlDominioUsuario;
	private CtrlDominioEncuesta ctrlDominioEncuesta;
	private CtrlDominioPregunta ctrlDominioPregunta;

	/** Constructor **/

	/**
	 * Constructora. Inicializa el TreeMap de respuestas y las referencias a otros controladores.
	 * 
	 * @param ctrlDominioUsuario Controlador de dominio de usuarios
	 * @param ctrlDominioEncuesta Controlador de dominio de encuestas
	 * @param ctrlDominioPregunta Controlador de dominio de preguntas
	 */
	public CtrlDominioRespuesta(CtrlDominioUsuario ctrlDominioUsuario, 
	                             CtrlDominioEncuesta ctrlDominioEncuesta,
	                             CtrlDominioPregunta ctrlDominioPregunta) {
		this.respuestas = new TreeMap<Integer, Respuesta>();
		this.nextId = 1;
		this.ctrlDominioUsuario = ctrlDominioUsuario;
		this.ctrlDominioEncuesta = ctrlDominioEncuesta;
		this.ctrlDominioPregunta = ctrlDominioPregunta;
	}
	
	// ==================== METODOS AUXILIARES PRIVADOS ====================
	
	/**
	 * Verifica si existe una respuesta con el ID dado.
	 * 
	 * @param idRespuesta ID de la respuesta
	 * @return true si existe, false en caso contrario
	 */
	private boolean existeRespuesta(int idRespuesta) {
		return respuestas.containsKey(Integer.valueOf(idRespuesta));
	}
	
	/**
	 * Obtiene una respuesta por su ID.
	 * 
	 * @param idRespuesta ID de la respuesta
	 * @return Respuesta correspondiente
	 * @throws IllegalArgumentException si la respuesta no existe
	 */
	private Respuesta getRespuesta(int idRespuesta) {
		Respuesta r = respuestas.get(Integer.valueOf(idRespuesta));
		if (r == null) {
			throw new IllegalArgumentException("Respuesta con ID " + idRespuesta + " no existe");
		}
		return r;
	}

	
	// ==================== GESTIÓN DE RESPUESTAS ====================

	/**
	 * Crea una nueva respuesta para una pregunta específica.
	 * 
	 * SOLUCIÓN POLIMÓRFICA: Delega la creación a la pregunta (Factory Pattern).
	 * Esto respeta el principio Abierto/Cerrado: si se añade un nuevo tipo de pregunta,
	 * solo hay que implementar crearRespuesta() en la nueva subclase de Pregunta.
	 * 
	 * @param <T> Tipo de la respuesta
	 * @param nomUsuario Nombre del usuario que responde
	 * @param idPregunta ID de la pregunta
	 * @param respuesta Contenido de la respuesta
	 * @return ID de la respuesta creada
	 */
	public <T> int darRespuesta(String nomUsuario, int idPregunta, T respuesta) {
		// Obtener la pregunta
		Pregunta p = ctrlDominioPregunta.getPregunta(idPregunta);
		
		// FACTORY PATTERN: La pregunta sabe crear su respuesta
		// Esto es polimórfico y respeta abierto/cerrado
		int idR = nextId++;
		Respuesta nuevaRespuesta = p.crearRespuesta(idR, nomUsuario, respuesta);
		
		// Guardar en el TreeMap
		respuestas.put(Integer.valueOf(idR), nuevaRespuesta);
		
		// Actualizar referencias en Usuario
		Usuario u = ctrlDominioUsuario.getUsuario(nomUsuario);
		u.guardarIdRespuesta(idR);
		
		// Actualizar referencias en Pregunta
		p.guardarIdRespuesta(idR);
		
		// NO se guarda en persistencia aquí (solo al cerrar sesión en CtrlDominio.cerrarSesion())
		
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
		if (!existeRespuesta(idRespuesta)) {
			throw new IllegalArgumentException("Respuesta con ID " + idRespuesta + " no existe");
		}
		
		Respuesta r = getRespuesta(idRespuesta);
		
		// modificar() en Respuesta validara el tipo
		r.modificar(newResp); // puede lanzar: TipoIncorrectoException
		
		// NO se actualiza en persistencia aquí (solo al cerrar sesión)
	}
	
	/**
	 * Elimina una respuesta del sistema y actualiza todas las referencias.
	 * 
	 * @param idRespuesta ID de la respuesta a eliminar
	 */
	public void eliminarRespuesta(int idRespuesta) {
		if (!existeRespuesta(idRespuesta)) {
			throw new IllegalArgumentException("Respuesta con ID " + idRespuesta + " no existe");
		}
		
		Respuesta r = getRespuesta(idRespuesta);
		int idPregunta = r.getIdPregunta();
		String nomUsuario = r.getNomUsuario();
		
		// Eliminar de Usuario
		Usuario u = ctrlDominioUsuario.getUsuario(nomUsuario);
		u.eliminarIdRespuesta(idRespuesta);
		
		// Eliminar de Pregunta
		Pregunta p = ctrlDominioPregunta.getPregunta(idPregunta);
		p.eliminarIdRespuesta(idRespuesta);
		
		// Eliminar del TreeMap
		respuestas.remove(Integer.valueOf(idRespuesta));
		
		// NO se elimina en persistencia aquí (solo al cerrar sesión)
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
			// Validar que existen los IDs usando los otros controladores de dominio
			if (!ctrlDominioEncuesta.existeEncuesta(dato.idEncuesta)) {
				throw new IllegalStateException("Encuesta con ID " + dato.idEncuesta + " no existe");
			}
			if (!ctrlDominioPregunta.existePregunta(dato.idPregunta)) {
				throw new IllegalStateException("Pregunta con ID " + dato.idPregunta + " no existe");
			}
			if (!ctrlDominioUsuario.existeUsuario(dato.nomUsuario)) {
				throw new IllegalStateException("Usuario " + dato.nomUsuario + " no existe");
			}
			
			// Validar que la pregunta pertenece a la encuesta
			Pregunta p = ctrlDominioPregunta.getPregunta(dato.idPregunta);
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
	
	// ==================== MÉTODOS PARA PERSISTENCIA ====================
	
	/**
	 * Obtiene todas las respuestas almacenadas en memoria.
	 * 
	 * IMPORTANTE: Este método solo debe ser llamado por CtrlDominio.cerrarSesion()
	 * para exportar todos los datos a persistencia al final de la sesión.
	 * 
	 * @return TreeMap con todas las respuestas (clave: idRespuesta)
	 */
	public TreeMap<Integer, Respuesta> getAllRespuestas() {
		return new TreeMap<>(respuestas); // devolver copia para evitar modificaciones externas
	}
	
	/**
	 * Importa respuestas desde persistencia al iniciar sesión.
	 * 
	 * IMPORTANTE: Este método solo debe ser llamado por CtrlDominio al iniciar
	 * para cargar los datos guardados de la sesión anterior.
	 * 
	 * @param respuestasImportadas TreeMap con las respuestas a cargar
	 */
	public void cargarRespuestas(TreeMap<Integer, Respuesta> respuestasImportadas) {
		this.respuestas = respuestasImportadas;
		// Actualizar nextId para que sea mayor que el ID más alto
		if (!respuestas.isEmpty()) {
			this.nextId = respuestas.lastKey() + 1;
		}
	}
}
