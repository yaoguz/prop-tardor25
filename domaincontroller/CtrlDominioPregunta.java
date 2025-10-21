package domaincontroller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import classcontroller.CtrlEncuesta;
import classcontroller.CtrlPregunta;

import domain.Encuesta;
import domain.Pregunta;

import persistence.CtrlPersistence;

/**
 * Controlador de Dominio para la gestión de preguntas.
 * 
 * Responsabilidades:
 * - Añadir preguntas de diferentes tipos a encuestas
 * - Modificar preguntas existentes
 * - Eliminar preguntas
 * 
 * @author Yaoyao Guo
 * @version 2.0
 */
public class CtrlDominioPregunta {

	/** Atributos - Controladores de clase **/
	
	private CtrlEncuesta ctrlEncuesta;
	private CtrlPregunta ctrlPregunta;
	private CtrlPersistence ctrlPersistence;

	/** Constructor **/

	/**
	 * Constructora. Inicializa los controladores necesarios.
	 * 
	 * @param ctrlEncuesta Controlador de encuestas
	 * @param ctrlPregunta Controlador de preguntas
	 * @param ctrlPersistence Controlador de persistencia
	 */
	public CtrlDominioPregunta(CtrlEncuesta ctrlEncuesta, CtrlPregunta ctrlPregunta,
	                            CtrlPersistence ctrlPersistence) {
		this.ctrlEncuesta = ctrlEncuesta;
		this.ctrlPregunta = ctrlPregunta;
		this.ctrlPersistence = ctrlPersistence;
	}

	// ==================== GESTIÓN DE PREGUNTAS ====================
	
	/**
	 * Clase auxiliar para datos importados de pregunta (debe ser compartida).
	 */
	public static class PreguntaImportData {
		public String enunciado;
		public String tipo; // "libre", "numerica", "opcion_unica", "opcion_multiple"
		public Map<String, Object> atributosEspecificos; // Opciones, rangos, etc.
	}
	
	/**
	 * Añade una pregunta a una encuesta (método polimórfico según tipo).
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @param infoP Información de la pregunta (enunciado, tipo, atributos específicos)
	 * @return ID de la pregunta creada
	 */
	public int anadirPregunta(int idEncuesta, PreguntaImportData infoP) {
		Encuesta e = ctrlEncuesta.getEncuesta(idEncuesta);
		
		// Crear pregunta polimórfica según tipo
		int idP = ctrlPregunta.crearPregunta(idEncuesta, infoP.enunciado,
                  infoP.tipo, infoP.atributosEspecificos);

		e.guardarIdPregunta(idP);
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
	 * (Alternativa: implementar modificación in-situ si se requiere mantener respuestas.)
	 * 
	 * @param idPregunta ID de la pregunta a modificar
	 * @param infoP Nueva información de la pregunta
	 * @param ctrlDominioRespuesta Controlador de respuestas (inyección de dependencia)
	 * @return ID de la nueva pregunta
	 */
	public int modificarPregunta(int idPregunta, PreguntaImportData infoP,
	                              CtrlDominioRespuesta ctrlDominioRespuesta) {
		// obtener pregunta actual para saber su encuesta
		Pregunta pVieja = ctrlPregunta.getPregunta(idPregunta);
		int idEncuesta = pVieja.getIdEncuesta();
		
		// eliminar pregunta antigua (y sus respuestas)
		eliminarPregunta(idPregunta, ctrlDominioRespuesta);
		
		// crear nueva pregunta
		return anadirPregunta(idEncuesta, infoP);
	}
	
	/**
	 * Elimina una pregunta y todas sus respuestas asociadas.
	 * 
	 * @param idPregunta ID de la pregunta a eliminar
	 * @param ctrlDominioRespuesta Controlador de respuestas (inyección de dependencia)
	 */
	public void eliminarPregunta(int idPregunta, CtrlDominioRespuesta ctrlDominioRespuesta) {
		// obtener pregunta y sus respuestas
		Pregunta p = ctrlPregunta.getPregunta(idPregunta);
		List<Integer> idRespuestas = p.getIdRespuestas();
		
		// eliminar cada respuesta
		for (Integer idR : idRespuestas) {
			ctrlDominioRespuesta.eliminarRespuesta(idR);
		}
		
		// eliminar la pregunta de la encuesta
		int idEncuesta = p.getIdEncuesta();
		Encuesta e = ctrlEncuesta.getEncuesta(idEncuesta);
		e.eliminarIdPregunta(idPregunta);
		
		// eliminar de persistencia y del controlador
		ctrlPersistence.eliminarPregunta(idPregunta);
		ctrlPregunta.eliminar(p);
	}

	/**
	 * Sobrecarga de eliminarPregunta sin necesidad de CtrlDominioRespuesta.
	 * Usado cuando se llama desde CtrlDominioEncuesta.
	 * 
	 * @param idPregunta ID de la pregunta a eliminar
	 */
	public void eliminarPregunta(int idPregunta) {
		// obtener pregunta y sus respuestas
		Pregunta p = ctrlPregunta.getPregunta(idPregunta);
		List<Integer> idRespuestas = p.getIdRespuestas();
		
		// eliminar cada respuesta (necesitamos CtrlRespuesta directamente)
		// NOTA: Esta es una solución temporal. Idealmente debería inyectarse CtrlDominioRespuesta
		// Por ahora, solo eliminamos las referencias
		for (Integer idR : idRespuestas) {
			ctrlPersistence.eliminarRespuesta(idR);
		}
		
		// eliminar la pregunta de la encuesta
		int idEncuesta = p.getIdEncuesta();
		Encuesta e = ctrlEncuesta.getEncuesta(idEncuesta);
		e.eliminarIdPregunta(idPregunta);
		
		// eliminar de persistencia y del controlador
		ctrlPersistence.eliminarPregunta(idPregunta);
		ctrlPregunta.eliminar(p);
	}
}
