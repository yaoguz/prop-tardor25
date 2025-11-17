package domaincontrollers;

import java.util.ArrayList;
import java.util.List;

import domain.Respuesta;
import domain.Pregunta;
import domain.algorithm.*;
import domain.algorithm.estructuras.Perfil;
import domain.algorithm.estructuras.ResultadoClustering;

/**
 * Controlador de Dominio para la gestión de perfiles.
 * 
 * Responsabilidades:
 * - Obtener perfiles de usuarios
 * - Generar perfiles basados en respuestas mediante clustering
 * - Comparar perfiles entre usuarios
 * - Orquestación de algoritmos de clustering
 * 
 * NOTA: CtrlDominioPerfil no gestiona datos propios, solo realiza análisis
 * sobre datos existentes en otros controladores.
 * 
 * @author Alessandro Jin
 */
public class CtrlDominioPerfil {

	/** Atributos **/

	private static CtrlDominioPerfil singletonInstance;

	private CtrlDominioEncuesta ctrlDominioEncuesta;

	/** Constructor **/

	/**
	 * Constructor privado (patrón singleton).
	 * 
	 * @param gestor Gestor de persistencia del sistema
	 */
	private CtrlDominioPerfil(CtrlDominioEncuesta ctrlDominioEncuesta) {
	
		this.ctrlDominioEncuesta = ctrlDominioEncuesta;
	}

	/**
	 * Obtiene la instancia única del controlador (patrón singleton).
	 * 
	 * @param ctrlDominioEncuesta CtrlDominioEncuesta
	 * @return Instancia única de CtrlDominioPerfil
	 */
	public static CtrlDominioPerfil getInstance(CtrlDominioEncuesta ctrlDominioEncuesta) {
		if (singletonInstance == null) {
			singletonInstance = new CtrlDominioPerfil(ctrlDominioEncuesta);
		} else {
			// Actualizar referencias circulares si ya existe la instancia
			if (ctrlDominioEncuesta != null) {
				singletonInstance.ctrlDominioEncuesta = ctrlDominioEncuesta;
			}
		}
		return singletonInstance;
	}

	/** Métodos públicos **/
	

    /**
     * Ejecuta análisis de clustering sobre una encuesta.
     * 
     * @param idEncuesta ID de la encuesta a analizar
     * @param k Número de clusters (perfiles) deseados
     * @param algoritmo "kmeans" o "kmeans++"
     * @return ResultadoClustering con perfiles y calidad
     */
    public ResultadoClustering ejecutarClustering(int idEncuesta, int k, String algoritmo) {
        // 1. Obtener todas las respuestas de la encuesta agrupadas por usuario
        List<List<Respuesta>> respuestasPorUsuario = ctrlDominioEncuesta.getRespuestasParaClustering(idEncuesta);

		List<Pregunta> variables = ctrlDominioEncuesta.getPreguntasEncuesta(idEncuesta);
        
        // 2. Seleccionar algoritmo
        ClusteringAlgorithm alg = seleccionarAlgoritmo(algoritmo);
        
        // 3. Ejecutar clustering
        return alg.ejecutarClustering(variables, respuestasPorUsuario, k);
    }	/**
	 * Obtiene todos los miembros de un perfil específico.
	 * 
	 * @param perfil Perfil del que obtener miembros
	 * @return Lista de nombres de usuarios en el perfil
	 */
	public List<String> obtenerMiembrosPerfil(Perfil perfil) {
		return new ArrayList<>(perfil.getMiembros());
	}

	/**
	 * Obtiene el representante de un perfil.
	 * 
	 * @param perfil Perfil del que obtener representante
	 * @return Nombre del usuario representante
	 */
	public String obtenerRepresentantePerfil(Perfil perfil) {
		return perfil.getRepresentante();
	}

	/** Métodos privados **/
    
	/**
	 * Selecciona el algoritmo de clustering según el nombre.
	 * 
	 * @param nombre Nombre del algoritmo ("kmeans", "kmeans++" o "kmedoids")
	 * @return Instancia del algoritmo seleccionado
	 */
    private ClusteringAlgorithm seleccionarAlgoritmo(String nombre) {
        switch (nombre.toLowerCase()) {
            case "kmeans++":
                return new KmeansPlusPlus();
            case "kmedoids":
                return new Kmedoids();
            case "kmeans":
            default:
                return new Kmeans();
        }
    }

}
