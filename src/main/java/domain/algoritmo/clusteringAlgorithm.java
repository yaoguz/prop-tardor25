package domain.clustering_ejemplo;

import java.util.List;
import java.util.Map;

import domain.Respuesta;

/**
 * Interfaz para algoritmos de clustering.
 * Permite añadir nuevos algoritmos sin modificar código existente (Open/Closed).
 */
public interface ClusteringAlgorithm {
    /**
     * Ejecuta el algoritmo de clustering.
     * 
     * @param respuestas Mapa de idUsuario -> lista de respuestas
     * @param k Número de clusters deseados
     * @return Lista de clusters obtenidos
     */
    Map<Centroide,List<String>> ejecutarClustering(List<List<Respuesta>> respuestas, int k);
    
    /**
     * Nombre del algoritmo (para logging/reporting).
     */
    String getNombre();
}