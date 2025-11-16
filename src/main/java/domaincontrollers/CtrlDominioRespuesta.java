package domaincontrollers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import domain.Encuesta;
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
		Usuario u = ctrlDominioUsuario.getUsuario(nomUsuario);
		
		int idR = gestor.getNextIdRespuesta();
		Respuesta nuevaRespuesta = p.crearRespuesta(idR, nomUsuario, respuesta);
		gestor.guardarRespuesta(nuevaRespuesta);
		
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
			
			// validar que la pregunta pertenece a la encuesta
			Pregunta p = ctrlDominioPregunta.getPregunta(dato.idPregunta);
			if (p.getIdEncuesta() != dato.idEncuesta) {
				throw new IllegalStateException("La pregunta " + dato.idPregunta + 
					" no pertenece a la encuesta " + dato.idEncuesta);
			}
			
			
			try {
				// crear respuesta
				darRespuesta(dato.nomUsuario, dato.idPregunta, dato.respuesta);
			} catch (IllegalArgumentException ex) {
				// respuesta invalida
				System.out.println(" ! Respuesta no guardada para usuario '" + dato.nomUsuario + 
							   "' en pregunta " + dato.idPregunta + ": " + ex.getMessage());
			}
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
	 * Parsea un fichero CSV de respuestas.
	 * Formato línea 1: titulo,<título>,autor,<autor>
	 * Línea 2: encabezados (ignorada, solo para legibilidad)
	 * Líneas siguientes: <usuario>,<respuesta1>,<respuesta2>,...
	 * 
	 * Las respuestas están en orden secuencial según las preguntas de la encuesta.
	 * No es necesario saber los idPregunta, se usan en el orden definido en la encuesta.
	 * Valores vacíos se ignoran (pregunta no respondida).
	 * 
	 * @param fichero Archivo CSV a parsear
	 * @return Lista de datos de respuestas
	 * @throws IllegalArgumentException si el formato es incorrecto
	 */
	private List<RespuestaImportData> parsearFicheroRespuestas(File fichero) {
		List<RespuestaImportData> resultado = new ArrayList<>();
		
		try (BufferedReader reader = new BufferedReader(new FileReader(fichero))) {
			// Línea 1: titulo,<título>,autor,<autor>
			String linea1 = reader.readLine();
			if (linea1 == null) {
				throw new IllegalArgumentException("Fichero CSV vacío");
			}
			
			String[] metadatos = linea1.split(",");
			if (metadatos.length < 4 || !metadatos[0].equals("titulo") || !metadatos[2].equals("autor")) {
				throw new IllegalArgumentException("Formato de metadatos incorrecto. Esperado: titulo,<título>,autor,<autor>");
			}
			
			String tituloEncuesta = metadatos[1];
			String autorEncuesta = metadatos[3];
			
			// Obtener encuesta por título y autor
			Encuesta e = ctrlDominioEncuesta.getEncuesta(tituloEncuesta, autorEncuesta);
			int idEncuesta = e.getIdEncuesta();
			List<Integer> idPreguntas = ctrlDominioEncuesta.getIdPreguntasEncuesta(idEncuesta);
			
			// Línea 2: encabezados (ignorar, solo para legibilidad del CSV)
			String linea2 = reader.readLine();
			if (linea2 == null) {
				throw new IllegalArgumentException("Fichero CSV sin línea de encabezados");
			}
			
			// Líneas 3+: respuestas en orden secuencial
			String linea;
			while ((linea = reader.readLine()) != null) {
				if (linea.trim().isEmpty()) continue;
				
				String[] valores = linea.split(",", -1); // -1 para mantener campos vacíos
				if (valores.length < 2) continue;
				
				String nomUsuario = valores[0].trim();
				if (nomUsuario.isEmpty()) continue;
				
				// Validar que el usuario existe
				if (!ctrlDominioUsuario.existeUsuario(nomUsuario)) {
					// si el usuario no existe, registrarlo automaticamente con contraseña por defecto
					try {
						ctrlDominioUsuario.registrarUsuario(nomUsuario, "123456", "123456");
						System.out.println("Usuario '" + nomUsuario + "' registrado automáticamente");
					} catch (Exception e1) {
						throw new IllegalStateException("Error al registrar usuario '" + nomUsuario + "': " + e1.getMessage());
					}
				}
				
				// Procesar respuestas en orden secuencial (sin saber idPregunta, solo el orden)
				for (int i = 0; i < idPreguntas.size() && (i + 1) < valores.length; i++) {
					String valorRespuesta = valores[i + 1].trim();
					if (valorRespuesta.isEmpty()) continue; // pregunta no respondida
					
					try {
						RespuestaImportData data = new RespuestaImportData();
						data.idEncuesta = idEncuesta;
						data.idPregunta = idPreguntas.get(i);
						data.nomUsuario = nomUsuario;
						data.respuesta = parsearValorRespuesta(data.idPregunta, valorRespuesta);
						
						resultado.add(data);
					} catch (IllegalArgumentException ex) {
						// respuesta no valida, omitir y continuar
						Pregunta pregunta = ctrlDominioPregunta.getPregunta(idPreguntas.get(i));
						System.out.println(" ! Respuesta inválida omitida para usuario '" + nomUsuario + 
									   "' en pregunta " + (i + 1) + " ('" + pregunta.getEnunciado() + "'): " + ex.getMessage());
					}
				}
			}
			
			return resultado;
		} catch (IOException e) {
			throw new IllegalArgumentException("Error al leer el fichero: " + e.getMessage());
		}
	}
	
	/**
	 * Parsea el valor de una respuesta según el tipo de pregunta.
	 * 
	 * @param idPregunta ID de la pregunta
	 * @param valor Valor en formato String desde CSV
	 * @return Objeto con el tipo correcto (Integer, String, int[])
	 */
	private Object parsearValorRespuesta(int idPregunta, String valor) {
		Pregunta p = ctrlDominioPregunta.getPregunta(idPregunta);
		
		// Usar instanceof en lugar de getClass().getSimpleName() para compatibilidad con mocks
		if (p instanceof domain.PreguntaNumerica) {
			return Double.parseDouble(valor);
		} else if (p instanceof domain.PreguntaOpcionUnica) {
			// intentar parsear como numero primero
			try {
				return Integer.parseInt(valor);
			} catch (NumberFormatException e) {
				// si no es numero, buscar el indice de la opcion por su texto
				ArrayList<String> opciones = p.getOpciones();
				for (int i = 0; i < opciones.size(); i++) {
					if (opciones.get(i).equalsIgnoreCase(valor.trim())) {
						return i;
					}
				}
				throw new IllegalArgumentException("Opción '" + valor + "' no encontrada en pregunta " + idPregunta);
			}
		} else if (p instanceof domain.PreguntaOpcionMultiple) {
			// formato: "0;1" o "0;2;3" o "Casa;Coche"
			if (valor.contains(";")) {
				String[] partes = valor.split(";");
				int[] resultado = new int[partes.length];
				ArrayList<String> opciones = p.getOpciones();
				
				for (int i = 0; i < partes.length; i++) {
					String parte = partes[i].trim();
					try {
						// intentar parsear como número
						resultado[i] = Integer.parseInt(parte);
					} catch (NumberFormatException e) {
						// si no es numero, buscar el índice por texto
						boolean encontrado = false;
						for (int j = 0; j < opciones.size(); j++) {
							if (opciones.get(j).equalsIgnoreCase(parte)) {
								resultado[i] = j;
								encontrado = true;
								break;
							}
						}
						if (!encontrado) {
							throw new IllegalArgumentException("Opción '" + parte + "' no encontrada en pregunta " + idPregunta);
						}
					}
				}
				return resultado;
			} else {
				// una sola opcion
				try {
					return new int[]{Integer.parseInt(valor)};
				} catch (NumberFormatException e) {
					// buscar el índice por texto
					ArrayList<String> opciones = p.getOpciones();
					for (int i = 0; i < opciones.size(); i++) {
						if (opciones.get(i).equalsIgnoreCase(valor.trim())) {
							return new int[]{i};
						}
					}
					throw new IllegalArgumentException("Opción '" + valor + "' no encontrada en pregunta " + idPregunta);
				}
			}
		} else if (p instanceof domain.PreguntaLibre) {
			return valor;
		} else {
			throw new IllegalArgumentException("Tipo de pregunta desconocido: " + p.getClass().getSimpleName());
		}
	}
}
