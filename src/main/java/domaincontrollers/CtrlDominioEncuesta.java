package domaincontrollers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import domain.Usuario;
import persistence.GestorPersistencia;
import domain.Encuesta;
import domain.Pregunta;
import domain.Respuesta;

/**
 * Controlador de Dominio para la gestión de encuestas.
 * 
 * @author Yaoyao Guo Zhang (yaoyao.guo@estudiantat.upc.edu)
 */
public class CtrlDominioEncuesta {

	/** Instancia única del controlador (patrón singleton) */
	private static CtrlDominioEncuesta singletonInstance;
	
	/** Gestor de persistencia del sistema */
	private GestorPersistencia gestor;
	
	/** Controlador de preguntas (inyección de dependencia) */
	private CtrlDominioPregunta ctrlDominioPregunta;
	
	/** Controlador de respuestas (inyección de dependencia) */
	private CtrlDominioRespuesta ctrlDominioRespuesta;
	
	/** Controlador de usuarios (inyección de dependencia) */
	private CtrlDominioUsuario ctrlDominioUsuario;

	private CtrlDominioEncuesta(GestorPersistencia gestor,
								CtrlDominioPregunta ctrlDominioPregunta,
								CtrlDominioRespuesta ctrlDominioRespuesta,
								CtrlDominioUsuario ctrlDominioUsuario) {
		this.gestor = gestor;
		this.ctrlDominioPregunta = ctrlDominioPregunta; 
		this.ctrlDominioRespuesta = ctrlDominioRespuesta; 
		this.ctrlDominioUsuario = ctrlDominioUsuario; 
	}

	/**
	 * Obtiene la instancia única del controlador (patrón singleton).
	 * Permite actualizar las referencias circulares después de la creación inicial.
	 * 
	 * @param gestor Gestor de persistencia
	 * @param ctrlDominioPregunta Controlador de preguntas
	 * @param ctrlDominioRespuesta Controlador de respuestas
	 * @param ctrlDominioUsuario Controlador de usuarios
	 * @return Instancia única de CtrlDominioEncuesta
	 */
	public static CtrlDominioEncuesta getInstance(GestorPersistencia gestor, 
	                              CtrlDominioPregunta ctrlDominioPregunta,
	                              CtrlDominioRespuesta ctrlDominioRespuesta,
	                              CtrlDominioUsuario ctrlDominioUsuario) {
		if (singletonInstance == null) {
			singletonInstance = new CtrlDominioEncuesta(gestor, 
														ctrlDominioPregunta,
														ctrlDominioRespuesta,
														ctrlDominioUsuario);
		} else {
			// Actualizar referencias circulares si ya existe la instancia
			if (ctrlDominioPregunta != null) {
				singletonInstance.ctrlDominioPregunta = ctrlDominioPregunta;
			}
			if (ctrlDominioRespuesta != null) {
				singletonInstance.ctrlDominioRespuesta = ctrlDominioRespuesta;
			}
			if (ctrlDominioUsuario != null) {
				singletonInstance.ctrlDominioUsuario = ctrlDominioUsuario;
			}
		}
		return singletonInstance;
	}

	// /** Mapa de encuestas (clave: idEncuesta, valor: Encuesta) */
	// private TreeMap<Integer, Encuesta> encuestas;
	
	// /** ID para la próxima encuesta a crear */
	// private int nextId;

	// /**
	//  * Verifica si existe una encuesta con el ID especificado.
	//  * 
	//  * @param idE ID de la encuesta
	//  * @return true si existe, false en caso contrario
	//  */
	// public boolean existeEncuesta(int idE) {
	// 	return encuestas.containsKey(idE);
	// }

	/**
	 * Verifica si existe una encuesta con el ID especificado.
	 * 
	 * @param idE ID de la encuesta
	 * @return true si existe, false en caso contrario
	 */
	public boolean existeEncuesta(int idE) {
		return gestor.existeEncuesta(idE);
	}

	
	// /**
	//  * Obtiene una instancia de Encuesta con el id idEncuesta.
	//  * 
	//  * @param idEncuesta ID de la encuesta
	//  * @return Instancia de Encuesta
	//  * @throws IllegalStateException si no existe una encuesta con ese ID
	//  */
	// public Encuesta getEncuesta(int idEncuesta) {
	// 	Encuesta e = encuestas.get(idEncuesta);
	// 	if (e == null) {
	// 		throw new IllegalStateException("No existe encuesta con id " + idEncuesta);
	// 	}
	// 	return e;
	// }

	/**
	 * Obtiene una instancia de Encuesta con el id "idEncuesta".
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @return Instancia de Encuesta
	 * @throws IllegalStateException si no existe una encuesta con ese ID
	 */
	public Encuesta getEncuesta(int idEncuesta) {
		Encuesta e = gestor.getEncuesta(idEncuesta);
		if (e == null) {
			throw new IllegalStateException("No existe encuesta con id " + idEncuesta);
		}
		return e;
	}

	
	/**
	 * Obtiene todas las encuestas del sistema.
	 * 
	 * @return HashMap con todas las encuestas (clave: id encuesta)
	 */
	public HashMap<Integer, Encuesta> getAllEncuestas() {
		HashMap<Integer, Encuesta> map = new HashMap<>();
		ArrayList<Encuesta> lista = gestor.getEncuestas();
		for (Encuesta e : lista) {
			map.put(e.getIdEncuesta(), e);
		}
		return map;
	}

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
	 * Crea una nueva encuesta (aún sin preguntas).
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

		Usuario u = ctrlDominioUsuario.getUsuario(nomUsuario);
		
		// crear encuesta directamente
		int idE = gestor.getNextIdEncuesta();
		Encuesta e = new Encuesta(idE, titulo, nomUsuario);
		gestor.guardarEncuesta(e); // en persistencia: encuestas.put(idE, e); nextId++;

		u.guardarIdEncuesta(idE);
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

		gestor.actualizarEncuesta(getEncuesta(idE));
		return idE;
	}
	
	/**
	 * Clase auxiliar para datos importados de encuesta.
	 */
	public static class EncuestaImportData {
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
		// TO DO: Implementar según formato específico ************
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
		gestor.actualizarEncuesta(e);
	}
	
	/**
	 * Elimina una encuesta y todas sus preguntas y respuestas asociadas.
	 * Requiere acceso a CtrlDominioPregunta para eliminar preguntas.
	 * 
	 * @param idEncuesta ID de la encuesta a eliminar
	 */
	public void eliminarEncuesta(int idEncuesta) {
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
		
		// eliminar de persistencia
		gestor.eliminarEncuesta(idEncuesta); //persistencia: encuestas.remove(idEncuesta);
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
	 * @return Mapa de Pregunta -> Set de Respuestas
	 */
	public Map<Pregunta, Set<Respuesta>> getRespuestasEncuesta(int idEncuesta) {
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
	 * @return ID de la pregunta creada
	 */
	public int anadirPregunta(int idEncuesta, PreguntaImportData infoP) {
		Encuesta e = getEncuesta(idEncuesta);
		
		// Crear pregunta polimórfica según tipo
		int idP = ctrlDominioPregunta.crearPregunta(idEncuesta, infoP.enunciado,
                  infoP.tipo, infoP.atributosEspecificos);

		e.guardarPregunta(idP);
		gestor.actualizarEncuesta(e);
		return idP;
	}

		// // pregunta numerica
        // PreguntaImportData infoP1 = new PreguntaImportData();
        // infoP1.enunciado = "¿Cuántas horas a la semana haces deporte?";
        // infoP1.tipo = "numerica";
        // infoP1.atributosEspecificos = new HashMap<>();
        // infoP1.atributosEspecificos.put("min", 0);
        // infoP1.atributosEspecificos.put("max", 40);
        // int idP1 = ctrlEncuesta.anadirPregunta(idEncuesta1, infoP1, ctrlPregunta);

	/**
	 * Obtiene las respuestas de todos los usuarios que han respondido una encuesta.
	 * Formato requerido para algoritmos de clustering: matriz M x N donde:
	 * - M = numero de usuarios que han respondido
	 * - N = numero de preguntas de la encuesta
	 * - Las preguntas no respondidas se representan con null
	 * 
	 * El algoritmo de clustering ignora los valores nulos en los calculos.
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @return List<List<Respuesta>> donde cada sublista es un usuario con sus respuestas ordenadas por pregunta
	 */
	public List<List<Respuesta>> getRespuestasParaClustering(int idEncuesta) {
		Encuesta e = getEncuesta(idEncuesta);
		List<Integer> idPreguntas = e.getIdPreguntas();
		Map<String, Map<Integer, Respuesta>> respuestasPorUsuario = new HashMap<>();
		
		for (Integer idP : idPreguntas) {
			Pregunta p = ctrlDominioPregunta.getPregunta(idP);
			List<Integer> idRespuestas = p.getIdRespuestas();
			
			for (Integer idR : idRespuestas) {
				Respuesta r = ctrlDominioRespuesta.getRespuesta(idR);
				String usuario = r.getNomUsuario();
				
				respuestasPorUsuario.putIfAbsent(usuario, new HashMap<>());
				respuestasPorUsuario.get(usuario).put(idP, r);
			}
		}
		
		// convertir a matriz List<List<Respuesta>>
		List<List<Respuesta>> matriz = new ArrayList<>();
		
		for (Map<Integer, Respuesta> respuestasUsuario : respuestasPorUsuario.values()) {
			List<Respuesta> filaUsuario = new ArrayList<>();
			for (Integer idP : idPreguntas) { // respuestas ordenadas segun id de preguntas
				Respuesta r = respuestasUsuario.get(idP);
				filaUsuario.add(r); // puede ser nulo (pregunta no respondida)
			}
			matriz.add(filaUsuario);
		}
		
		return matriz;
	}
	
	// /**
	//  * Importa encuestas desde persistencia al iniciar aplicación.
	//  * 
	//  * IMPORTANTE: Este método solo debe ser llamado por CtrlDominio.inicializarCtrlDominio()
	//  * para cargar los datos guardados de la sesión anterior.
	//  * 
	//  * @param encuestasImportadas TreeMap con las encuestas a cargar
	//  */
	// public void cargarEncuestas(TreeMap<Integer, Encuesta> encuestasImportadas) {
	// 	this.encuestas = encuestasImportadas;
		
	// 	// Actualizar nextId para que sea mayor que el ID más alto
	// 	if (!encuestas.isEmpty()) {
	// 		this.nextId = encuestas.lastKey() + 1;
	// 	}
	// }
}
