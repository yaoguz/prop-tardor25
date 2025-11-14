package domaincontrollers;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.ArrayList;

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
	
	// /** Mapa de preguntas (clave: idPregunta, valor: Pregunta) */
	// private TreeMap<Integer, Pregunta> preguntas;
	
	// /** ID para la próxima pregunta a crear */
	// private int nextId;

	private static CtrlDominioPregunta singletonInstance;
    private GestorPersistencia gestor;

    private CtrlDominioPregunta(GestorPersistencia gestor) {
        this.gestor = gestor;
    }

    public static CtrlDominioPregunta getInstance(GestorPersistencia gestor) {
        if (singletonInstance == null) {
            singletonInstance = new CtrlDominioPregunta(gestor);
        }
        return singletonInstance;
    }
	
	// /**
	//  * Se verifica si existe una pregunta con el ID especificado.
	//  * 
	//  * @param idP ID de la pregunta
	//  * @return true si existe, false en caso contrario
	//  */
	// public boolean existePregunta(int idP) {
	// 	return preguntas.containsKey(idP);
	// }
	
	// /**
	//  * Se obtiene la instancia de Pregunta con el id pasasdo por parámetro.
	//  * 
	//  * @param idPregunta ID de la pregunta a obtener.
	//  * @return la Pregunta cuyo identificador se ha pasado por parámetro.
	//  * @throws IllegalArgumentException si no existe una instancia de Pregunta con identificador idPregunta.
	//  */
	// public Pregunta getPregunta(int idPregunta) {
	// 	Pregunta p = preguntas.get(idPregunta);
	// 	if (p == null) {
	// 		throw new IllegalArgumentException("No existe la pregunta");
	// 	}
	// 	return p;
	// }

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
	 * Crea una nueva pregunta de tipo libre con el nextId como idPregunta, se añade al mapa de preguntas y se devuelve el idPregunta asignado.
	 * 
	 * @param idEncuesta identificador de la encuesta a la cual pertenece la pregunta
	 * @param enunciado el enunciado de la pregunta
	 * @param ctrlDominioEncuesta controlador de encuestas (inyección por parámetros)
	 * @return idPregunta de la pregunta creada
	 * @throws IllegalArgumentException si el enunciado está vacío o es null
	 */
	public int crearPreguntaLibre(int idEncuesta, String enunciado, 
	                               CtrlDominioEncuesta ctrlDominioEncuesta) 
								   throws IllegalArgumentException {
		if (enunciado == null || enunciado.trim().isEmpty()) {
            throw new IllegalArgumentException("El enunciado no puede estar vacío");
        }

        int idP = gestor.getNextIdPregunta();
		Pregunta p = new PreguntaLibre(idEncuesta, idP, enunciado);
        gestor.guardarPregunta(p); // persistencia: preguntas.put(idPregunta, p);
		
		// actualizar encuesta
		Encuesta e = ctrlDominioEncuesta.getEncuesta(idEncuesta);
		e.guardarPregunta(idP);
		gestor.actualizarEncuesta(e);

		return idP;
	}

	/**
	 * Se crea una nueva pregunta de tipo numérica con el nextId como idPregunta, se añade al mapa de preguntas y se devuelve el idPregunta asignado.
	 *
	 * @param idEncuesta identificador de la encuesta a la cual pertenece la pregunta
	 * @param enunciado el enunciado de la pregunta
	 * @param min el valor numérico mínimo que pueden tener las respuestas de la pregunta
	 * @param max el valor numérico máximo que pueden tener las respuestas de la pregunta
	 * @param ctrlDominioEncuesta controlador de encuestas (inyección por parámetros)
	 * @return idPregunta de la pregunta creada
	 * @throws IllegalArgumentException si el enunciado está vacío o es null
	 */
	public int crearPreguntaNumerica(int idEncuesta, String enunciado, int min, int max,
	                                  CtrlDominioEncuesta ctrlDominioEncuesta) 
									  throws IllegalArgumentException {
		if (enunciado == null || enunciado.trim().isEmpty()) {
            throw new IllegalArgumentException("El enunciado no puede estar vacío");
        }
        if (min >= max) {
            throw new IllegalArgumentException("El rango [" + min + ", " + max + "] no es válido");
        }

		int idP = gestor.getNextIdPregunta();
		Pregunta p = new PreguntaNumerica(idEncuesta, idP, enunciado, min, max);
        gestor.guardarPregunta(p);
		
		// actualizar encuesta
		Encuesta e = ctrlDominioEncuesta.getEncuesta(idEncuesta);
		e.guardarPregunta(idP);
		gestor.actualizarEncuesta(e);

		return idP;
	}

	/**
	 * Se crea una nueva pregunta de tipo opción múltiple con el nextId como idPregunta, se añade al mapa de preguntas y se devuelve el idPregunta asignado.
	 * 
	 * @param idEncuesta identificador de la encuesta a la cual pertenece la pregunta
	 * @param enunciado el enunciado de la pregunta
	 * @param opciones lista de opciones disponibles (puede ser null o vacía)
	 * @param numMaxOpc número máximo de opciones que se pueden seleccionar
	 * @param ctrlDominioEncuesta controlador de encuestas (inyección por parámetros)
	 * @return idPregunta de la pregunta creada
	 * @throws IllegalArgumentException si el enunciado está vacío o es null
	 */
	public int crearPreguntaOpcionMultiple(int idEncuesta, String enunciado, 
	                                        List<String> opciones, int numMaxOpc,
	                                        CtrlDominioEncuesta ctrlDominioEncuesta) 
											throws IllegalArgumentException {
		if (enunciado == null || enunciado.trim().isEmpty()) {
            throw new IllegalArgumentException("El enunciado no puede estar vacío");
        }
        if (opciones == null || opciones.size() < 2) {
            throw new IllegalArgumentException("Se requieren al menos 2 opciones");
        }

		int idP = gestor.getNextIdPregunta();
		PreguntaOpcionMultiple p = new PreguntaOpcionMultiple(idEncuesta, idP, enunciado);

		// anadir opciones
		if (opciones != null) {
			for (String opcion : opciones) p.addOpcion(opcion);
		}
		p.setNumMaxOpc(numMaxOpc);

        gestor.guardarPregunta(p);
		
		Encuesta e = ctrlDominioEncuesta.getEncuesta(idEncuesta);
		e.guardarPregunta(idP);
		gestor.actualizarEncuesta(e);

		return idP;
	}

	/**
	 * Se crea una nueva pregunta de tipo opción única con el nextId como idPregunta, se añade al mapa de preguntas y se devuelve el idPregunta asignado.
	 * 
	 * @param idEncuesta identificador de la encuesta a la cual pertenece la pregunta
	 * @param enunciado el enunciado de la pregunta
	 * @param opciones lista de opciones disponibles (puede ser null o vacía)
	 * @param ordenada indica si la pregunta es ordenada
	 * @param ctrlDominioEncuesta controlador de encuestas (inyección por parámetros)
	 * @return idPregunta de la pregunta creada
	 * @throws IllegalArgumentException si el enunciado está vacío o es null
	 */
	public int crearPreguntaOpcionUnica(int idEncuesta, String enunciado, 
	                                     List<String> opciones, boolean ordenada,
	                                     CtrlDominioEncuesta ctrlDominioEncuesta) 
										 throws IllegalArgumentException {
		if (enunciado == null || enunciado.trim().isEmpty()) {
            throw new IllegalArgumentException("El enunciado no puede estar vacío");
        }
        if (opciones == null || opciones.size() < 2) {
            throw new IllegalArgumentException("Se requieren al menos 2 opciones");
        }

		int idP = gestor.getNextIdPregunta();
		PreguntaOpcionUnica p = new PreguntaOpcionUnica(idEncuesta, idPregunta, enunciado, ordenada);

		// anadir opciones
		if (opciones != null) {
			for (String opcion : opciones) p.addOpcion(opcion);
		}

        gestor.guardarPregunta(p);
		
		Encuesta e = ctrlDominioEncuesta.getEncuesta(idEncuesta);
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
	 * @param ctrlDominioEncuesta Controlador de encuestas (inyección de dependencias)
	 * @return ID de la pregunta creada
	 */
	public int crearPregunta(int idEncuesta, String enunciado, String tipo, 
	                          Map<String, Object> atributosEspecificos,
	                          CtrlDominioEncuesta ctrlDominioEncuesta) {
		switch (tipo) {
			case "libre":
				return crearPreguntaLibre(idEncuesta, enunciado, ctrlDominioEncuesta);
			case "numerica":
				int min = (int) atributosEspecificos.get("min");
				int max = (int) atributosEspecificos.get("max");
				return crearPreguntaNumerica(idEncuesta, enunciado, min, max, ctrlDominioEncuesta);
			case "opcion_unica":
				List<String> opcionesOU = atributosEspecificos.containsKey("opciones")
				                          ? (List<String>) atributosEspecificos.get("opciones")
				                          : null;
				boolean ordenada = atributosEspecificos.containsKey("ordenada") 
				                   ? (boolean) atributosEspecificos.get("ordenada") 
				                   : false;
				return crearPreguntaOpcionUnica(idEncuesta, enunciado, opcionesOU, ordenada, ctrlDominioEncuesta);
				
			case "opcion_multiple":
				List<String> opcionesOM = atributosEspecificos.containsKey("opciones")
				                          ? (List<String>) atributosEspecificos.get("opciones")
				                          : null;
				int numMaxOpc = atributosEspecificos.containsKey("numMaxOpc")
				                ? (int) atributosEspecificos.get("numMaxOpc")
				                : 2; // valor por defecto
				return crearPreguntaOpcionMultiple(idEncuesta, enunciado, opcionesOM, numMaxOpc, ctrlDominioEncuesta);
				
			default:
				throw new IllegalArgumentException("Tipo de pregunta desconocido: " + tipo);
		}
	}
	
	/**
	 * Se modifica una pregunta existente (elimina la antigua y crea una nueva).
	 * 
	 * @param idPregunta ID de la pregunta a modificar
	 * @param infoP Nueva información de la pregunta (el tipo da igual, se sobreescribe con el de la pregunta a modificar)
	 * @param ctrlDominioRespuesta Controlador de respuestas (inyección de dependencia)
	 * @param ctrlDominioEncuesta Controlador de encuestas (inyección de dependencia)
	 * @param ctrlDominioUsuario Controlador de usuarios (inyección de dependencia)
	 * @return ID de la nueva pregunta
	 */
	public int modificarPregunta(int idPregunta, PreguntaImportData infoP,
	                              CtrlDominioRespuesta ctrlDominioRespuesta,
	                              CtrlDominioEncuesta ctrlDominioEncuesta,
	                              CtrlDominioUsuario ctrlDominioUsuario) {
		// obtener pregunta actual para saber su encuesta
		Pregunta pVieja = getPregunta(idPregunta);
		int idEncuesta = pVieja.getIdEncuesta();
		infoP.tipo = pVieja.tipoPregunta();
		
		// eliminar pregunta antigua (y sus respuestas)
		eliminarPregunta(idPregunta, ctrlDominioRespuesta, ctrlDominioEncuesta, ctrlDominioUsuario);
		
		// crear nueva pregunta
		return crearPregunta(idEncuesta, infoP.enunciado, infoP.tipo, 
		                      infoP.atributosEspecificos, ctrlDominioEncuesta);
	}
	
	/**
	 * Se elimina una pregunta y todas sus respuestas asociadas.
	 * 
	 * IMPORTANTE: Al eliminar la pregunta, también elimina todas sus respuestas
	 * actualizando las referencias en Usuario
	 * 
	 * @param idPregunta ID de la pregunta a eliminar
	 * @param ctrlDominioRespuesta Controlador de respuestas (inyección de dependencia)
	 * @param ctrlDominioEncuesta Controlador de encuestas (inyección de dependencia)
	 * @param ctrlDominioUsuario Controlador de usuarios (inyección de dependencia)
	 */
	public void eliminarPregunta(int idPregunta, 
	                              CtrlDominioRespuesta ctrlDominioRespuesta,
	                              CtrlDominioEncuesta ctrlDominioEncuesta,
	                              CtrlDominioUsuario ctrlDominioUsuario) {
		// obtener pregunta y sus respuestas
		Pregunta p = getPregunta(idPregunta);
		List<Integer> idRespuestas = new ArrayList<>(p.getIdRespuestas()); // copia para evitar ConcurrentModificationException
		
		// eliminar cada respuesta (las respuestas se encargarán de actualizar Usuario y Pregunta)
		for (Integer idR : idRespuestas) {
			ctrlDominioRespuesta.eliminarRespuesta(idR, ctrlDominioUsuario, this);
		}
		
		// eliminar la pregunta de la encuesta
		int idEncuesta = p.getIdEncuesta();
		Encuesta e = ctrlDominioEncuesta.getEncuesta(idEncuesta);
		e.eliminarPregunta(idPregunta);
		
		gestor.eliminarPregunta(idPregunta);
	}
	
	/**
	 * Obtiene todas las respuestas de una pregunta específica.
	 * 
	 * Esta operación pertenece a CtrlDominioPregunta porque el aggregate root es Pregunta.
	 * Representa: "Todas las respuestas DE esta pregunta".
	 * 
	 * @param idPregunta ID de la pregunta
	 * @param ctrlDominioRespuesta Controlador de respuestas (inyección de dependencia)
	 * @return Lista de respuestas de la pregunta
	 * @throws IllegalArgumentException si la pregunta no existe
	 */
	public List<Respuesta> getRespuestasPregunta(int idPregunta, 
	                                              CtrlDominioRespuesta ctrlDominioRespuesta) {
		Pregunta p = getPregunta(idPregunta);
		List<Integer> idRespuestas = p.getIdRespuestas();
		
		List<Respuesta> respuestas = new java.util.ArrayList<>();
		for (Integer idR : idRespuestas) {
			respuestas.add(ctrlDominioRespuesta.getRespuesta(idR));
		}
		
		return respuestas;
	}
	
	// /**
	//  * Obtiene todas las preguntas almacenadas en memoria.
	//  * 
	//  * IMPORTANTE: Este método solo debe ser llamado por CtrlDominio.cerrarSesion()
	//  * para exportar todos los datos a persistencia al cerrar la aplicación.
	//  * 
	//  * @return TreeMap con todas las preguntas (clave: idPregunta)
	//  */
	// public TreeMap<Integer, Pregunta> getAllPreguntas() {
	// 	return new TreeMap<>(preguntas); // devolver copia para evitar modificaciones externas
	// }
	
	// /**
	//  * Importa preguntas desde persistencia al iniciar aplicación.
	//  * 
	//  * IMPORTANTE: Este método solo debe ser llamado por CtrlDominio.inicializarCtrlDominio()
	//  * para cargar los datos guardados de la sesión anterior.
	//  * 
	//  * @param preguntasImportadas TreeMap con las preguntas a cargar
	//  */
	// public void cargarPreguntas(TreeMap<Integer, Pregunta> preguntasImportadas) {
	// 	this.preguntas = preguntasImportadas;
		
	// 	// Actualizar nextId para que sea mayor que el ID más alto
	// 	if (!preguntas.isEmpty()) {
	// 		this.nextId = preguntas.lastKey() + 1;
	// 	}
	// }
}
