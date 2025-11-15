package domaincontrollers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domain.Respuesta;
import domain.Usuario;
import domain.Pregunta;
import domain.clustering_ejemplo.Cluster;
import domain.clustering_ejemplo.ClusterEvaluator;
import domain.clustering_ejemplo.ClusteringAlgorithm;
import domain.clustering_ejemplo.KMeans;
import domain.clustering_ejemplo.KMeansPlusPlus;
import persistence.GestorPersistencia;

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
	private GestorPersistencia gestor;
	
	/** Controlador de encuestas (inyección de dependencia) */
	private CtrlDominioEncuesta ctrlDominioEncuesta;

	/** Constructor **/

	/**
	 * Constructor privado (patrón singleton).
	 * 
	 * @param gestor Gestor de persistencia del sistema
	 */
	private CtrlDominioPerfil(GestorPersistencia gestor, CtrlDominioRespuesta ctrlDominioRespuesta, CtrlDominioEncuesta ctrlDominioEncuesta) {
		this.gestor = gestor;
	
		this.ctrlDominioEncuesta = ctrlDominioEncuesta;
	}

	/**
	 * Obtiene la instancia única del controlador (patrón singleton).
	 * 
	 * @param gestor Gestor de persistencia
	 * @return Instancia única de CtrlDominioPerfil
	 */
	public static CtrlDominioPerfil getInstance(GestorPersistencia gestor) {
		if (singletonInstance == null) {
			singletonInstance = new CtrlDominioPerfil(gestor);
		}
		return singletonInstance;
	}

	/** Métodos públicos **/
	
	/**
	 * Obtiene los perfiles de un usuario.
	 * 
	 * @param nomUsuario Nombre del usuario
	 * @param ctrlDominioUsuario Controlador de usuarios (inyección)
	 * @return Lista de IDs de perfiles del usuario
	 */
	public List<Integer> getPerfilesUsuario(String nomUsuario) {
		Usuario u = ctrlDominioUsuario.getUsuario(nomUsuario);
		return new ArrayList<>(u.getIdPerfiles());
	}

	/**
     * Ejecuta análisis de clustering sobre una encuesta.
     * 
     * @param idEncuesta ID de la encuesta a analizar
     * @param k Número de clusters (perfiles) deseados
     * @param algoritmo "kmeans" o "kmeans++"
     * @param ctrlDominioUsuario Controlador de usuarios (inyección)
     * @param ctrlDominioRespuesta Controlador de respuestas (inyección)
     * @return Lista de clusters obtenidos
     */
    public List<Perfil> ejecutarClustering(int idEncuesta, int k, String algoritmo) {

        // 1. Obtener todas las respuestas de la encuesta agrupadas por usuario
        List<List<Respuesta>> respuestasPorUsuario = ctrlDominioEncuesta.getRespuestasParaClustering(idEncuesta);

		List<Pregunta> variables = ctrlDominioEncuesta.getPreguntaEncuesta(idEncuesta);
        
        // 3. Seleccionar algoritmo
        ClusteringAlgorithm alg = seleccionarAlgoritmo(algoritmo);
        
        // 4. Ejecutar clustering
        List<Perfil> perfiles = alg.ejecutarClustering(variables, respuestasPorUsuario, k);
        
        // 5. Evaluar calidad
        //double silueta = ClusterEvaluator.calcularCoeficienteSilueta(clusters, respuestasPorUsuario);
        
        return perfiles;
    }

	/** Métodos privados **/
    
	/**
	 * Selecciona el algoritmo de clustering según el nombre.
	 * 
	 * @param nombre Nombre del algoritmo ("kmeans" o "kmeans++")
	 * @return Instancia del algoritmo seleccionado
	 */
    private ClusteringAlgorithm seleccionarAlgoritmo(String nombre) {
        switch (nombre.toLowerCase()) {
            case "kmeans++":
                return new KMeansPlusPlus();
            case "kmeans":
            default:
                return new KMeans();
        }
    }

}
