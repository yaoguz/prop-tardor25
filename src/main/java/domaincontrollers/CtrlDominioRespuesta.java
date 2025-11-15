package domaincontrollers;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import domain.Pregunta;
import domain.Respuesta;
import domain.Usuario;
import persistence.GestorPersistencia;

/**
 * Controlador de dominio para la gestión de respuestas.
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

	/** Instancia única del controlador (patrón singleton) */
	private static CtrlDominioRespuesta singletonInstance;
	
	/** Gestor de persistencia del sistema */
    private GestorPersistencia gestor;
	
	/** Controlador de usuarios (inyección de dependencia) */
	private CtrlDominioUsuario ctrlDominioUsuario;
	
	/** Controlador de encuestas (inyección de dependencia) */
	private CtrlDominioEncuesta ctrlDominioEncuesta;
	
	/** Controlador de preguntas (inyección de dependencia) */
	private CtrlDominioPregunta ctrlDominioPregunta;

	/**
	 * Constructor privado (patrón singleton).
	 * 
	 * @param gestor Gestor de persistencia
	 * @param ctrlDominioUsuario Controlador de usuarios
	 * @param ctrlDominioEncuesta Controlador de encuestas
	 * @param ctrlDominioPregunta Controlador de preguntas
	 */
	private CtrlDominioRespuesta(GestorPersistencia gestor,
								CtrlDominioUsuario ctrlDominioUsuario,
								CtrlDominioEncuesta ctrlDominioEncuesta,
								CtrlDominioPregunta ctrlDominioPregunta) {
        this.gestor = gestor;
		this.ctrlDominioUsuario = ctrlDominioUsuario; 
		this.ctrlDominioEncuesta = ctrlDominioEncuesta; 
		this.ctrlDominioPregunta = ctrlDominioPregunta; 
    }

	/**
	 * Obtiene la instancia única del controlador (patrón singleton).
	 * Permite actualizar las referencias circulares después de la creación inicial.
	 * 
	 * @param gestor Gestor de persistencia
	 * @param ctrlDominioUsuario Controlador de usuarios
	 * @param ctrlDominioEncuesta Controlador de encuestas
	 * @param ctrlDominioPregunta Controlador de preguntas
	 * @return Instancia única de CtrlDominioRespuesta
	 */
    public static CtrlDominioRespuesta getInstance(GestorPersistencia gestor,
	                                CtrlDominioUsuario ctrlDominioUsuario,
	                                CtrlDominioEncuesta ctrlDominioEncuesta,
	                                CtrlDominioPregunta ctrlDominioPregunta) {
        if (singletonInstance == null) {
            singletonInstance = new CtrlDominioRespuesta(gestor, 
														ctrlDominioUsuario,
														ctrlDominioEncuesta,
														ctrlDominioPregunta);
        } else {
            // Actualizar referencias circulares si ya existe la instancia
            if (ctrlDominioUsuario != null) {
                singletonInstance.ctrlDominioUsuario = ctrlDominioUsuario;
            }
            if (ctrlDominioEncuesta != null) {
                singletonInstance.ctrlDominioEncuesta = ctrlDominioEncuesta;
            }
            if (ctrlDominioPregunta != null) {
                singletonInstance.ctrlDominioPregunta = ctrlDominioPregunta;
            }
        }
        return singletonInstance;
    }

	/**
	 * Verifica si existe una respuesta con el id dado.
	 * 
	 * @param idRespuesta ID de la respuesta
	 * @return true si existe, false en caso contrario
	 */
    public boolean existeRespuesta(int idRespuesta) {
        return gestor.existeRespuesta(idRespuesta);
    }

	// private boolean existeRespuesta(int idRespuesta) {
	// 	return respuestas.containsKey(Integer.valueOf(idRespuesta));
	// }
	
	/**
	 * Obtiene una respuesta por su id.
	 * 
	 * @param idRespuesta ID de la respuesta
	 * @return Respuesta correspondiente
	 * @throws IllegalArgumentException si la respuesta no existe
	 */
    public Respuesta getRespuesta(int idRespuesta) {
        Respuesta r = gestor.getRespuesta(idRespuesta);
        if (r == null) {
            throw new IllegalArgumentException("Respuesta con id " + idRespuesta + " no existe");
        }
        return r;
    }

	// public Respuesta getRespuesta(int idRespuesta) {
	// 	Respuesta r = respuestas.get(Integer.valueOf(idRespuesta));
	// 	if (r == null) {
	// 		throw new IllegalArgumentException("Respuesta con ID " + idRespuesta + " no existe");
	// 	}
	// 	return r;
	// }

	/**
	 * Obtiene todas las respuestas del sistema.
	 * 
	 * @return HashMap con todas las respuestas (clave: id respuesta)
	 */
	public HashMap<Integer, Respuesta> getAllRespuestas() {
		HashMap<Integer, Respuesta> map = new HashMap<>();
		ArrayList<Respuesta> lista = gestor.getRespuestas();
		for (Respuesta r : lista) {
			map.put(r.getId(), r);
		}
		return map;
	}

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
		Pregunta p = ctrlDominioPregunta.getPregunta(idPregunta);
		
		int idR = gestor.getNextIdRespuesta();
		Respuesta nuevaRespuesta = p.crearRespuesta(idR, nomUsuario, respuesta);
		gestor.guardarRespuesta(nuevaRespuesta); // persistencia: respuestas.put(Integer.valueOf(idR), nuevaRespuesta);
		
		Usuario u = ctrlDominioUsuario.getUsuario(nomUsuario);
		u.guardarIdRespuesta(idR);
		gestor.actualizarUsuario(u);

		p.guardarIdRespuesta(idR);
		gestor.actualizarPregunta(p);
		
		return idR;
	}	
	
	/**
	 * Modifica una respuesta existente.
	 * 
	 * @param <T> Tipo de la respuesta (String, Integer, List<String>, etc.)
	 * @param idRespuesta ID de la respuesta a modificar
	 * @param newResp Nueva respuesta
	 */
	public <T> void modificarRespuesta(int idRespuesta, T newResp) {
		Respuesta r = getRespuesta(idRespuesta);
		r.modificar(newResp); // polimorfico
		gestor.actualizarRespuesta(r);
	}
	
	/**
	 * Elimina una respuesta del sistema y actualiza todas las referencias.
	 * 
	 * @param idRespuesta ID de la respuesta a eliminar
	 */
	public void eliminarRespuesta(int idRespuesta) {
		Respuesta r = getRespuesta(idRespuesta);
		int idPregunta = r.getIdPregunta();
		String nomUsuario = r.getNomUsuario();
		
		// eliminar de usuario
		Usuario u = ctrlDominioUsuario.getUsuario(nomUsuario);
		u.eliminarIdRespuesta(idRespuesta);
		gestor.actualizarUsuario(u);
		
		// eliminar de pregunta
		Pregunta p = ctrlDominioPregunta.getPregunta(idPregunta);
		p.eliminarIdRespuesta(idRespuesta);
		gestor.actualizarPregunta(p);
		
		// eliminar de persistencia
		gestor.eliminarRespuesta(idRespuesta); // respuestas.remove(Integer.valueOf(idRespuesta));
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
		// parser del fichero (implementacion especifica segun formato)
		List<RespuestaImportData> datosImportados = parsearFicheroRespuestas(fichero);
		
		for (RespuestaImportData dato : datosImportados) {
			// validar que existen los IDs usando los otros controladores de dominio
			if (!ctrlDominioEncuesta.existeEncuesta(dato.idEncuesta)) {
				throw new IllegalStateException("Encuesta con ID " + dato.idEncuesta + " no existe");
			}
			if (!ctrlDominioPregunta.existePregunta(dato.idPregunta)) {
				throw new IllegalStateException("Pregunta con ID " + dato.idPregunta + " no existe");
			}
			if (!ctrlDominioUsuario.existeUsuario(dato.nomUsuario)) {
				throw new IllegalStateException("Usuario " + dato.nomUsuario + " no existe");
			}
			
			// validar que la pregunta pertenece a la encuesta
			Pregunta p = ctrlDominioPregunta.getPregunta(dato.idPregunta);
			if (p.getIdEncuesta() != dato.idEncuesta) {
				throw new IllegalStateException("La pregunta " + dato.idPregunta + 
					" no pertenece a la encuesta " + dato.idEncuesta);
			}
			
			// crear respuesta
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
	
	// /**
	//  * Importa respuestas desde persistencia al iniciar sesión.
	//  * 
	//  * IMPORTANTE: Este método solo debe ser llamado por CtrlDominio al iniciar
	//  * para cargar los datos guardados de la sesión anterior.
	//  * 
	//  * @param respuestasImportadas TreeMap con las respuestas a cargar
	//  */
	// public void cargarRespuestas(TreeMap<Integer, Respuesta> respuestasImportadas) {
	// 	this.respuestas = respuestasImportadas;
	// 	// Actualizar nextId para que sea mayor que el ID más alto
	// 	if (!respuestas.isEmpty()) {
	// 		this.nextId = respuestas.lastKey() + 1;
	// 	}
	// }
}
