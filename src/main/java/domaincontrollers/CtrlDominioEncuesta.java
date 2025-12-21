package domaincontrollers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import domain.Usuario;
import persistence.GestorPersistencia;
import domain.Encuesta;
import domain.Pregunta;
import domain.Respuesta;

/**
 * Controlador de Dominio para la gestión de encuestas.
 * 
 * Responsabilidades:
 * - CRUD de encuestas (crear, leer, modificar, eliminar)
 * - Importar encuestas desde JSON
 * - Gestionar preguntas asociadas a encuestas
 * - Obtener respuestas de encuestas para clustering
 * - Validar existencia y permisos de encuestas
 * 
 * Patrón Singleton para mantener estado consistente.
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
	
	/** Controlador de usuarios (inyección de dependencia) */
	private CtrlDominioUsuario ctrlDominioUsuario;

	private CtrlDominioEncuesta(GestorPersistencia gestor,
								CtrlDominioPregunta ctrlDominioPregunta,
								CtrlDominioUsuario ctrlDominioUsuario) {
		this.gestor = gestor;
		this.ctrlDominioPregunta = ctrlDominioPregunta; 
		this.ctrlDominioUsuario = ctrlDominioUsuario; 
	}

	/**
	 * Obtiene la instancia única del controlador (patrón singleton).
	 * Permite actualizar las referencias circulares después de la creación inicial.
	 * 
	 * @param gestor Gestor de persistencia
	 * @param ctrlDominioPregunta Controlador de preguntas
	 * @param ctrlDominioUsuario Controlador de usuarios
	 * @return Instancia única de CtrlDominioEncuesta
	 */
	public static CtrlDominioEncuesta getInstance(GestorPersistencia gestor, 
	                              CtrlDominioPregunta ctrlDominioPregunta,
	                              CtrlDominioUsuario ctrlDominioUsuario) {
		if (singletonInstance == null) {
			singletonInstance = new CtrlDominioEncuesta(gestor, 
														ctrlDominioPregunta,
														ctrlDominioUsuario);
		} else {
			// Actualizar referencias circulares si ya existe la instancia
			if (ctrlDominioPregunta != null) {
				singletonInstance.ctrlDominioPregunta = ctrlDominioPregunta;
			}
			if (ctrlDominioUsuario != null) {
				singletonInstance.ctrlDominioUsuario = ctrlDominioUsuario;
			}
		}
		return singletonInstance;
	}

	/**
	 * Verifica si existe una encuesta con el ID especificado.
	 * 
	 * @param idE ID de la encuesta
	 * @return true si existe, false en caso contrario
	 */
	public boolean existeEncuesta(int idE) {
		return gestor.existeEncuesta(idE);
	}

	/**
	 * Verifica si un autor ya tiene una encuesta con el título especificado.
	 * RT: un mismo autor no puede tener dos encuestas con el mismo título.
	 * 
	 * @param nomAutor Nombre del autor
	 * @param titulo Título de la encuesta
	 * @return true si ya existe, false en caso contrario
	 */
	public boolean existeEncuesta(String nomAutor, String titulo) {
		Usuario u = ctrlDominioUsuario.getUsuario(nomAutor);
		ArrayList<Integer> idEncuestas = u.getIdEncuestasCreadas();
		
		for (Integer idE : idEncuestas) {
			Encuesta e = gestor.getEncuesta(idE);
			if (e != null && e.getTitulo().equals(titulo)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Obtiene una encuesta por título y autor.
	 * 
	 * @param titulo Título de la encuesta
	 * @param nomAutor Nombre del autor
	 * @return Encuesta encontrada
	 * @throws IllegalStateException si no existe la encuesta
	 */
	public Encuesta getEncuesta(String titulo, String nomAutor) {
		Usuario u = ctrlDominioUsuario.getUsuario(nomAutor);
		ArrayList<Integer> idEncuestas = u.getIdEncuestasCreadas();
		
		for (Integer idE : idEncuestas) {
			Encuesta e = gestor.getEncuesta(idE);
			if (e != null && e.getTitulo().equals(titulo)) {
				return e;
			}
		}
		throw new IllegalStateException("No existe encuesta '" + titulo + "' del autor '" + nomAutor + "'");
	}

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
	public List<Integer> getIdPreguntasEncuesta(int idEncuesta) {
		Encuesta e = getEncuesta(idEncuesta);
		return e.getIdPreguntas();
	}

	/**
	 * Obtiene las preguntas de una encuesta.
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @return Lista de preguntas
	 */
	public List<Pregunta> getPreguntasEncuesta(int idEncuesta) {
		Encuesta e = getEncuesta(idEncuesta);
		List<Pregunta> preguntas = new ArrayList<>();
		for (int idP : e.getIdPreguntas()) {
			preguntas.add(ctrlDominioPregunta.getPregunta(idP));
		}
		return preguntas;
	}
	
	/**
	 * Crea una nueva encuesta (aún sin preguntas).
	 * 
	 * @param nomUsuario Nombre del usuario creador
	 * @param titulo Título de la encuesta
	 * @return ID de la encuesta creada
	 * @throws IllegalArgumentException si el título está vacío o ya existe para este autor
	 */
	public int crearEncuesta(String nomUsuario, String titulo) {
		if (titulo == null || titulo.trim().isEmpty()) {
			throw new IllegalArgumentException("El título de la encuesta no puede estar vacío");
		}
		if (existeEncuesta(nomUsuario, titulo)) {
			throw new IllegalArgumentException("El autor '" + nomUsuario + "' ya tiene una encuesta con título '" + titulo + "'");
		}

		Usuario u = ctrlDominioUsuario.getUsuario(nomUsuario);
		
		// crear encuesta directamente
		int idE = gestor.getNextIdEncuesta();
		Encuesta e = new Encuesta(idE, titulo, nomUsuario);
		gestor.guardarEncuesta(e); // en persistencia: encuestas.put(idE, e); nextId++;

		u.guardarIdEncuesta(idE);
		gestor.actualizarUsuario(u);
		return idE;
	}
	
	/**
	 * Importa una encuesta desde un fichero externo.
	 * La encuesta se crea para el usuario activo actual.
	 * 
	 * Formato esperado: {titulo, preguntas: [{tipo, enunciado, ...}]}
	 * 
	 * @param fichero Archivo con los datos de la encuesta
	 * @return ID de la encuesta creada
	 * @throws IllegalArgumentException si el formato es incorrecto
	 * @throws IllegalStateException si no hay usuario activo
	 */
	public int importarEncuesta(File fichero) {
		// Validar que hay usuario activo
		String usuarioActivo = ctrlDominioUsuario.getUsuarioActivo();
		if (usuarioActivo == null) {
			throw new IllegalStateException("No hay usuario activo. Debe iniciar sesión antes de importar una encuesta.");
		}
		
		// parser del fichero
		EncuestaImportData datos = parsearFicheroEncuesta(fichero);
		
		// crear encuesta para el usuario activo
		int idE = crearEncuesta(usuarioActivo, datos.titulo);
		
		// anadir preguntas a la encuesta
		for (PreguntaImportData infoP : datos.preguntas) {
			anadirPregunta(idE, infoP);
		}

		return idE;
	}
	
	/**
	 * Clase auxiliar para datos importados de encuesta.
	 */
	public static class EncuestaImportData {
		String titulo;
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
	 * Parsea un fichero JSON de encuesta.
	 * Formato: {"titulo": "...", "preguntas": [{"tipo": "...", ...}]}
	 * 
	 * @param fichero Archivo JSON a parsear
	 * @return Datos de la encuesta
	 * @throws IllegalArgumentException si el formato es incorrecto
	 */
	private EncuestaImportData parsearFicheroEncuesta(File fichero) {
		try (BufferedReader reader = new BufferedReader(new FileReader(fichero))) {
			Gson gson = new Gson();
			JsonObject json = gson.fromJson(reader, JsonObject.class);
			
			EncuestaImportData data = new EncuestaImportData();
			data.titulo = json.get("titulo").getAsString();
			data.preguntas = new ArrayList<>();
			
			JsonArray preguntasArray = json.getAsJsonArray("preguntas");
			for (int i = 0; i < preguntasArray.size(); i++) {
				JsonObject pregJson = preguntasArray.get(i).getAsJsonObject();
				PreguntaImportData pregunta = new PreguntaImportData();
				pregunta.tipo = pregJson.get("tipo").getAsString();
				pregunta.enunciado = pregJson.get("enunciado").getAsString();
				pregunta.atributosEspecificos = new HashMap<>();
				
				// Parsear atributos según tipo
				switch (pregunta.tipo) {
					case "numerica":
						pregunta.atributosEspecificos.put("min", pregJson.get("min").getAsInt());
						pregunta.atributosEspecificos.put("max", pregJson.get("max").getAsInt());
						break;
					case "opcion_unica":
						List<String> opcionesUnica = new ArrayList<>();
						JsonArray opcsUnica = pregJson.getAsJsonArray("opciones");
						for (int j = 0; j < opcsUnica.size(); j++) {
							opcionesUnica.add(opcsUnica.get(j).getAsString());
						}
						pregunta.atributosEspecificos.put("opciones", opcionesUnica);
						pregunta.atributosEspecificos.put("ordenada", pregJson.get("ordenada").getAsBoolean());
						break;
					case "opcion_multiple":
						List<String> opcionesMultiple = new ArrayList<>();
						JsonArray opcsMultiple = pregJson.getAsJsonArray("opciones");
						for (int j = 0; j < opcsMultiple.size(); j++) {
							opcionesMultiple.add(opcsMultiple.get(j).getAsString());
						}
						pregunta.atributosEspecificos.put("opciones", opcionesMultiple);
						pregunta.atributosEspecificos.put("numMaxOpc", pregJson.get("numMaxOpc").getAsInt());
						break;
					case "libre":
						// Sin atributos adicionales
						break;
					default:
						throw new IllegalArgumentException("Tipo de pregunta desconocido: " + pregunta.tipo);
				}
				
				data.preguntas.add(pregunta);
			}
			
			return data;
		} catch (IOException e) {
			throw new IllegalArgumentException("Error al leer el fichero: " + e.getMessage());
		} catch (Exception e) {
			throw new IllegalArgumentException("Formato JSON incorrecto: " + e.getMessage());
		}
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
		try {
			Usuario u = null;
			if (nomCreador != null) {
				u = ctrlDominioUsuario.getUsuario(nomCreador);
			}
			if (u != null) {
				u.eliminarIdEncuestaCreada(idEncuesta);
				gestor.actualizarUsuario(u);
			} else {
				// si no existe el usuario creador en persistencia, no fallar la operacion
				// se asume que la encuesta sera igualmente eliminada de persistencia
				System.err.println("Warning: usuario creador '" + nomCreador + "' no encontrado al eliminar encuesta " + idEncuesta);
			}
		} catch (Exception ex) {
			// no propagar problemas al eliminar encuesta, simplemente loguear y continuar
			System.err.println("Error actualizando usuario creador al eliminar encuesta " + idEncuesta + ": " + ex.getMessage());
		}
		
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
	 * ESTRUCTURA DE PERSISTENCIA:                                            /////////////// cambios
	 * - Cada respuesta: data/encuestas/[idEncuesta]/[idRespuesta].json
	 * - Ejemplo: encuesta 1 tiene 40 archivos (1.json, 2.json, ..., 40.json)
	 * 
	 * OPTIMIZACIÓN: Con getRespuestasEncuesta(idEncuesta):
	 * - Lee todos los archivos de respuesta de la carpeta de la encuesta
	 * - Agrupa en memoria por pregunta (muy rápido)
	 * - Complejidad: O(M) donde M = número de respuestas
	 * 
	 * vs acceso individual por idRespuestas de cada pregunta:
	 * - Requeriría N loops + M accesos individuales
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @return Mapa de Pregunta → Set de Respuestas
	 * @throws IllegalArgumentException si la encuesta no existe
	 */
	public Map<Pregunta, Set<Respuesta>> getRespuestasEncuesta(int idEncuesta) {
		Encuesta e = getEncuesta(idEncuesta);
		List<Integer> idPreguntas = e.getIdPreguntas();

		// optimizacion: leer todas las respuestas de la encuesta de una vez
		// en persistencia: lee carpeta data/encuestas/{idEncuesta}/*.json
		ArrayList<Respuesta> todasRespuestas = gestor.getRespuestasEncuesta(idEncuesta);

		Map<Pregunta, Set<Respuesta>> resultado = new HashMap<>();
		
		// inicializar mapa con preguntas
		for (Integer idP : idPreguntas) {
			Pregunta p = ctrlDominioPregunta.getPregunta(idP);
			resultado.put(p, new HashSet<>());
		}
		
		// agrupar respuestas por pregunta
		for (Respuesta r : todasRespuestas) {
			Pregunta p = ctrlDominioPregunta.getPregunta(r.getIdPregunta());
			if (resultado.containsKey(p)) resultado.get(p).add(r);
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
		// Crear pregunta polimórfica según tipo
		// NOTA: crearPregunta ya añade la pregunta a la encuesta internamente
		int idP = ctrlDominioPregunta.crearPregunta(idEncuesta, infoP.enunciado,
                  infoP.tipo, infoP.atributosEspecificos);
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
	 * ESTRUCTURA DE PERSISTENCIA:                                               /////////////// cambios
	 * - Cada respuesta: data/encuestas/[idEncuesta]/[idRespuesta].json
	 * - Todas las respuestas de la encuesta están en la misma carpeta
	 * 
	 * OPTIMIZACIÓN: Con getRespuestasEncuesta(idEncuesta):
	 * - Lee todos los archivos de la carpeta de la encuesta
	 * - Reorganiza en memoria en matriz [usuarios × preguntas]
	 * - Complejidad: O(M) donde M = número de respuestas
	 * 
	 * vs estructura anterior con archivos por pregunta:
	 * - Requeriría leer N archivos diferentes (uno por pregunta)
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @return Lista de listas donde cada sublista es un usuario con sus respuestas ordenadas por pregunta
	 * @throws IllegalArgumentException si la encuesta no existe
	 */
	public List<List<Respuesta>> getRespuestasParaClustering(int idEncuesta) {
		Encuesta e = getEncuesta(idEncuesta);
		List<Integer> idPreguntas = e.getIdPreguntas();
		
		// optimizacion: leer todas las respuestas de la encuesta de una vez
		// en persistencia: lee carpeta data/encuestas/{idEncuesta}/*.json
		ArrayList<Respuesta> todasRespuestas = gestor.getRespuestasEncuesta(idEncuesta);
		
		// Organizar respuestas por usuario
		Map<String, Map<Integer, Respuesta>> respuestasPorUsuario = new HashMap<>();
		
		for (Respuesta r : todasRespuestas) {
			String usuario = r.getNomUsuario();
			int idP = r.getIdPregunta();
			
			respuestasPorUsuario.putIfAbsent(usuario, new HashMap<>());
			respuestasPorUsuario.get(usuario).put(idP, r);
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
