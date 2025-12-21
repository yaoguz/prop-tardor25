package domaincontrollers;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import domain.Encuesta;
import domain.Pregunta;
import domain.PreguntaLibre;
import domain.PreguntaNumerica;
import domain.PreguntaOpcionMultiple;
import domain.PreguntaOpcionUnica;
import domain.Respuesta;
import persistence.GestorPersistencia;

/**
 * Controlador de dominio para la gestión de preguntas.
 * 
 * Responsabilidades:
 * - Añadir preguntas de diferentes tipos a encuestas
 * - Modificar preguntas existentes
 * - Eliminar preguntas
 * 
 * @author Yaoyao Guo Zhang
 */
public class CtrlDominioPregunta {

	/** Instancia única del controlador (patrón singleton) */
	private static CtrlDominioPregunta singletonInstance;
	
	/** Gestor de persistencia del sistema */
    private GestorPersistencia gestor;
	
	/** Controlador de respuestas (inyección de dependencia) */
	private CtrlDominioRespuesta ctrlDominioRespuesta;
	
	/** Controlador de encuestas (inyección de dependencia) */
	private CtrlDominioEncuesta ctrlDominioEncuesta;

	/**
	 * Constructor privado (patrón singleton).
	 * 
	 * @param gestor Gestor de persistencia
	 * @param ctrlDominioRespuesta Controlador de respuestas
	 * @param ctrlDominioEncuesta Controlador de encuestas
	 */
    private CtrlDominioPregunta(GestorPersistencia gestor, 
								CtrlDominioRespuesta ctrlDominioRespuesta,
								CtrlDominioEncuesta ctrlDominioEncuesta) {
        this.gestor = gestor;
		this.ctrlDominioRespuesta = ctrlDominioRespuesta; 
		this.ctrlDominioEncuesta = ctrlDominioEncuesta; 
    }

	/**
	 * Obtiene la instancia única del controlador (patrón singleton).
	 * Permite actualizar las referencias circulares después de la creación inicial.
	 * 
	 * @param gestor Gestor de persistencia
	 * @param ctrlDominioRespuesta Controlador de respuestas
	 * @param ctrlDominioEncuesta Controlador de encuestas
	 * @return Instancia única de CtrlDominioPregunta
	 */
    public static CtrlDominioPregunta getInstance(GestorPersistencia gestor, 
	                              CtrlDominioRespuesta ctrlDominioRespuesta,
	                              CtrlDominioEncuesta ctrlDominioEncuesta) {
        if (singletonInstance == null) {
            singletonInstance = new CtrlDominioPregunta(gestor, 
	                              ctrlDominioRespuesta,
	                              ctrlDominioEncuesta);
        } else {
            // Actualizar referencias circulares si ya existe la instancia
            if (ctrlDominioRespuesta != null) {
                singletonInstance.ctrlDominioRespuesta = ctrlDominioRespuesta;
            }
            if (ctrlDominioEncuesta != null) {
                singletonInstance.ctrlDominioEncuesta = ctrlDominioEncuesta;
            }
        }
        return singletonInstance;
    }

	/**
	 * Se verifica si existe una pregunta con id idP.
	 * 
	 * @param idP ID de la pregunta
	 * @return true si existe, false en caso contrario
	 */
	public boolean existePregunta(int idP) {
        return gestor.existePregunta(idP);
    }

	/**
	 * Se obtiene la instancia de Pregunta con id idPregunta.
	 * 
	 * @param idPregunta ID de la pregunta a obtener.
	 * @return la Pregunta cuyo identificador se ha pasado por parámetro.
	 * @throws IllegalArgumentException si no existe una instancia de Pregunta con identificador idPregunta.
	 */
    public Pregunta getPregunta(int idPregunta) {
        Pregunta p = gestor.getPregunta(idPregunta);
        if (p == null) {
            throw new IllegalStateException("No existe pregunta con id " + idPregunta);
        }
        return p;
    }


	/**
	 * Crea una nueva pregunta de tipo libre, se añade al mapa de preguntas y se devuelve el idPregunta asignado.
	 * 
	 * @param idEncuesta identificador de la encuesta a la cual pertenece la pregunta
	 * @param enunciado el enunciado de la pregunta
	 * @return idPregunta de la pregunta creada
	 * @throws IllegalArgumentException si el enunciado está vacío o es null
	 */
	public int crearPreguntaLibre(int idEncuesta, String enunciado) 
								   throws IllegalArgumentException {
		if (enunciado == null || enunciado.trim().isEmpty()) {
            throw new IllegalArgumentException("El enunciado no puede estar vacío");
        }

		// comprobar que la encuesta existe
		Encuesta e = ctrlDominioEncuesta.getEncuesta(idEncuesta);

        int idP = gestor.getNextIdPregunta();
		Pregunta p = new PreguntaLibre(idEncuesta, idP, enunciado);
        gestor.guardarPregunta(p); // persistencia: preguntas.put(idPregunta, p);
		
		// actualizar encuesta
		e.guardarPregunta(idP);
		gestor.actualizarEncuesta(e);

		return idP;
	}

	/**
	 * Se crea una nueva pregunta de tipo numérica, se añade al mapa de preguntas y se devuelve el idPregunta asignado.
	 *
	 * @param idEncuesta identificador de la encuesta a la cual pertenece la pregunta
	 * @param enunciado el enunciado de la pregunta
	 * @param min el valor numérico mínimo que pueden tener las respuestas de la pregunta
	 * @param max el valor numérico máximo que pueden tener las respuestas de la pregunta
	 * @return idPregunta de la pregunta creada
	 * @throws IllegalArgumentException si el enunciado está vacío o es null
	 */
	public int crearPreguntaNumerica(int idEncuesta, String enunciado, int min, int max) 
									  throws IllegalArgumentException {
		if (enunciado == null || enunciado.trim().isEmpty()) {
            throw new IllegalArgumentException("El enunciado no puede estar vacío");
        }
        if (min >= max) {
            throw new IllegalArgumentException("El rango [" + min + ", " + max + "] no es válido");
        }

		// comprobar que la encuesta existe
		Encuesta e = ctrlDominioEncuesta.getEncuesta(idEncuesta);

		int idP = gestor.getNextIdPregunta();
		Pregunta p = new PreguntaNumerica(idEncuesta, idP, enunciado, min, max);
        gestor.guardarPregunta(p);
		
		// actualizar encuesta
		e.guardarPregunta(idP);
		gestor.actualizarEncuesta(e);

		return idP;
	}

	/**
	 * Se crea una nueva pregunta de tipo opción múltiple, se añade al mapa de preguntas y se devuelve el idPregunta asignado.
	 * 
	 * @param idEncuesta identificador de la encuesta a la cual pertenece la pregunta
	 * @param enunciado el enunciado de la pregunta
	 * @param opciones lista de opciones disponibles (puede ser null o vacía)
	 * @param numMaxOpc número máximo de opciones que se pueden seleccionar
	 * @return idPregunta de la pregunta creada
	 * @throws IllegalArgumentException si el enunciado está vacío o es null
	 */
	public int crearPreguntaOpcionMultiple(int idEncuesta, String enunciado, 
	                                        List<String> opciones, int numMaxOpc) 
											throws IllegalArgumentException {
		if (enunciado == null || enunciado.trim().isEmpty()) {
            throw new IllegalArgumentException("El enunciado no puede estar vacío");
        }
        if (opciones == null || opciones.size() < 2) {
            throw new IllegalArgumentException("Se requieren al menos 2 opciones");
        }

		// comprobar que la encuesta existe
		Encuesta e = ctrlDominioEncuesta.getEncuesta(idEncuesta);

		int idP = gestor.getNextIdPregunta();
		PreguntaOpcionMultiple p = new PreguntaOpcionMultiple(idEncuesta, idP, enunciado);

		// anadir opciones
		if (opciones != null) {
			for (String opcion : opciones) p.addOpcion(opcion);
		}
		p.setNumMaxOpc(numMaxOpc);

        gestor.guardarPregunta(p);
		
		e.guardarPregunta(idP);
		gestor.actualizarEncuesta(e);

		return idP;
	}

	/**
	 * Se crea una nueva pregunta de tipo opción única, se añade al mapa de preguntas y se devuelve el idPregunta asignado.
	 * 
	 * @param idEncuesta identificador de la encuesta a la cual pertenece la pregunta
	 * @param enunciado el enunciado de la pregunta
	 * @param opciones lista de opciones disponibles (puede ser null o vacía)
	 * @param ordenada indica si la pregunta es ordenada
	 * @return idPregunta de la pregunta creada
	 * @throws IllegalArgumentException si el enunciado está vacío o es null
	 */
	public int crearPreguntaOpcionUnica(int idEncuesta, String enunciado, 
	                                     List<String> opciones, boolean ordenada) 
										 throws IllegalArgumentException {
		if (enunciado == null || enunciado.trim().isEmpty()) {
            throw new IllegalArgumentException("El enunciado no puede estar vacío");
        }
        if (opciones == null || opciones.size() < 2) {
            throw new IllegalArgumentException("Se requieren al menos 2 opciones");
        }

		// comprobar que la encuesta existe
		Encuesta e = ctrlDominioEncuesta.getEncuesta(idEncuesta);

		int idP = gestor.getNextIdPregunta();
		PreguntaOpcionUnica p = new PreguntaOpcionUnica(idEncuesta, idP, enunciado, ordenada);

		// anadir opciones
		if (opciones != null) {
			for (String opcion : opciones) p.addOpcion(opcion);
		}

        gestor.guardarPregunta(p);
		
		e.guardarPregunta(idP);
		gestor.actualizarEncuesta(e);

		return idP;
	}

	/**
	 * Se añade una respuesta a la lista de respuestas de la pregunta.
	 * 
	 * @param idPregunta identificador de la pregunta a la que se quiere añadir la respuesta
	 * @param idRespuesta identificador de la respuesta a añadir
	 * @throws IllegalArgumentException si no existe la pregunta
	 */
	public void anadirRespuesta(int idPregunta, int idRespuesta) 
								throws IllegalArgumentException {
		Pregunta p = getPregunta(idPregunta);
		if (p == null) {
			throw new IllegalArgumentException("No existe la pregunta");
		}
		p.guardarIdRespuesta(idRespuesta);
		gestor.actualizarPregunta(p);
	}

	/**
	 * Se elimina una respuesta de la lista de respuestas de la pregunta.
	 * 
	 * IMPORTANTE: solo elimina la referencia en Pregunta, NO elimina la respuesta.
	 * Usado por CtrlDominioRespuesta.eliminarRespuesta()
	 * 
	 * @param idPregunta identificador de la pregunta a la cual se le quiere quitar la respuesta
	 * @param idRespuesta identificador de la respuesta a eliminar
	 * @throws IllegalArgumentException si no existe la pregunta o la pregunta no tiene esa respuesta
	 */
	public void eliminarRespuestaReferencia(int idPregunta, int idRespuesta) 
											throws IllegalArgumentException {
		Pregunta p = getPregunta(idPregunta);
		if (p == null) {
			throw new IllegalArgumentException("No existe la pregunta");
		}
		p.eliminarIdRespuesta(idRespuesta);
		gestor.actualizarPregunta(p);
	}
	
	/**
	 * Clase auxiliar para datos importados de pregunta (compatible con CtrlDominioEncuesta).
	 */
	public static class PreguntaImportData {
		public String enunciado;
		public String tipo; // "libre", "numerica", "opcion_unica", "opcion_multiple"
		public Map<String, Object> atributosEspecificos; // Opciones, rangos, etc.
	}
	
	/**
	 * Crea una pregunta genérica a partir de PreguntaImportData.
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @param enunciado Enunciado de la pregunta
	 * @param tipo Tipo de pregunta
	 * @param atributosEspecificos Atributos específicos según tipo
	 * @return ID de la pregunta creada
	 */
	@SuppressWarnings("unchecked")
	public int crearPregunta(int idEncuesta, String enunciado, String tipo, 
	                          Map<String, Object> atributosEspecificos) {
		switch (tipo) {
			case "libre":
				return crearPreguntaLibre(idEncuesta, enunciado);
			case "numerica":
				int min = (int) atributosEspecificos.get("min");
				int max = (int) atributosEspecificos.get("max");
				return crearPreguntaNumerica(idEncuesta, enunciado, min, max);
			case "opcion_unica":
				List<String> opcionesOU = atributosEspecificos.containsKey("opciones")
				                          ? (List<String>) atributosEspecificos.get("opciones")
				                          : null;
				boolean ordenada = atributosEspecificos.containsKey("ordenada") 
				                   ? (boolean) atributosEspecificos.get("ordenada") 
				                   : false;
				return crearPreguntaOpcionUnica(idEncuesta, enunciado, opcionesOU, ordenada);
				
			case "opcion_multiple":
				List<String> opcionesOM = atributosEspecificos.containsKey("opciones")
				                          ? (List<String>) atributosEspecificos.get("opciones")
				                          : null;
				int numMaxOpc = atributosEspecificos.containsKey("numMaxOpc")
				                ? (int) atributosEspecificos.get("numMaxOpc")
				                : 2; // valor por defecto
				return crearPreguntaOpcionMultiple(idEncuesta, enunciado, opcionesOM, numMaxOpc);
				
			default:
				throw new IllegalArgumentException("Tipo de pregunta desconocido: " + tipo);
		}
	}
	
	/**
	 * Se modifica una pregunta existente (elimina la antigua y crea una nueva).
	 * 
	 * @param idPregunta ID de la pregunta a modificar
	 * @param infoP Nueva información de la pregunta (el tipo da igual, se sobreescribe con el de la pregunta a modificar)
	 * @return ID de la nueva pregunta
	 */
	public int modificarPregunta(int idPregunta, PreguntaImportData infoP) {
		// obtener pregunta actual para saber su encuesta
		Pregunta pVieja = getPregunta(idPregunta);
		int idEncuesta = pVieja.getIdEncuesta();
		infoP.tipo = pVieja.tipoPregunta();
		
		// eliminar pregunta antigua (y sus respuestas)
		eliminarPregunta(idPregunta);
		
		// crear nueva pregunta
		return crearPregunta(idEncuesta, infoP.enunciado, infoP.tipo, 
		                      infoP.atributosEspecificos);
	}
	
	/**
	 * Se elimina una pregunta y todas sus respuestas asociadas.
	 * 
	 * IMPORTANTE: Al eliminar la pregunta, también elimina todas sus respuestas
	 * actualizando las referencias en Usuario
	 * 
	 * @param idPregunta ID de la pregunta a eliminar
	 */
	public void eliminarPregunta(int idPregunta) {
		// obtener pregunta y sus respuestas
		Pregunta p = getPregunta(idPregunta);
		List<Integer> idRespuestas = new ArrayList<>(p.getIdRespuestas()); // copia para evitar ConcurrentModificationException
		
		// eliminar cada respuesta (las respuestas se encargarán de actualizar Usuario y Pregunta)
		for (Integer idR : idRespuestas) {
			ctrlDominioRespuesta.eliminarRespuesta(idR);
		}
		
		// eliminar la pregunta de la encuesta (solo si la encuesta aún existe)
		int idEncuesta = p.getIdEncuesta();
		if (ctrlDominioEncuesta.existeEncuesta(idEncuesta)) {
			Encuesta e = ctrlDominioEncuesta.getEncuesta(idEncuesta);
			e.eliminarPregunta(idPregunta);
			gestor.actualizarEncuesta(e);
		}
		
		gestor.eliminarPregunta(idPregunta);
	}

	/**
	 * Obtiene todas las respuestas de una pregunta específica.
	 * 
	 * Esta operación pertenece a CtrlDominioPregunta porque el aggregate root es Pregunta.
	 * Representa: "Todas las respuestas DE esta pregunta".
	 * 
	 * @param idPregunta ID de la pregunta
	 * @return Lista de respuestas de la pregunta
	 * @throws IllegalArgumentException si la pregunta no existe
	 */
	public List<Respuesta> getRespuestasPregunta(int idPregunta) {
		Pregunta p = getPregunta(idPregunta);
		List<Integer> idRespuestas = p.getIdRespuestas();
		
		List<Respuesta> respuestas = new ArrayList<>();
		for (Integer idR : idRespuestas) {
			respuestas.add(ctrlDominioRespuesta.getRespuesta(idR));
		}
		
		return respuestas;
	}
}
