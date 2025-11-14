package domaincontrollers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import domain.Respuesta;
import domain.Usuario;
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

	/** Constructor **/

	/**
	 * Constructor privado (patrón singleton).
	 * 
	 * @param gestor Gestor de persistencia del sistema
	 */
	private CtrlDominioPerfil(GestorPersistencia gestor) {
		this.gestor = gestor;
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
	public List<Integer> getPerfilesUsuario(String nomUsuario, CtrlDominioUsuario ctrlDominioUsuario) {
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
    public List<Cluster> ejecutarClustering(int idEncuesta, int k, String algoritmo,
                                              CtrlDominioUsuario ctrlDominioUsuario,
                                              CtrlDominioRespuesta ctrlDominioRespuesta) {
        // 1. Obtener todas las respuestas de la encuesta agrupadas por usuario
        Map<String, List<Respuesta>> respuestasPorUsuario = 
            obtenerRespuestasPorUsuario(idEncuesta, ctrlDominioRespuesta);
        
        // 2. Seleccionar algoritmo
        ClusteringAlgorithm alg = seleccionarAlgoritmo(algoritmo);
        
        // 3. Ejecutar clustering
        List<Cluster> clusters = alg.ejecutarClustering(respuestasPorUsuario, k);
        
        // 4. Evaluar calidad
        double silueta = ClusterEvaluator.calcularCoeficienteSilueta(clusters, respuestasPorUsuario);
        System.out.println("Coeficiente de Silueta: " + silueta);
        
        return clusters;
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
    
	/**
	 * Obtiene todas las respuestas de una encuesta agrupadas por usuario.
	 * 
	 * @param idEncuesta ID de la encuesta
	 * @param ctrlDominioRespuesta Controlador de respuestas
	 * @return Mapa usuario -> lista de respuestas
	 */
    private Map<String, List<Respuesta>> obtenerRespuestasPorUsuario(
            int idEncuesta, CtrlDominioRespuesta ctrlDominioRespuesta) {
        // TODO: Obtener todas las respuestas de la encuesta
        // y agruparlas por usuario
        return new HashMap<>();
    }
}
