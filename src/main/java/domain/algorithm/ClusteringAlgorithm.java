package domain.algorithm;

import java.util.List;

import domain.Respuesta;
import domain.Pregunta;
import domain.algorithm.estructuras.ResultadoClustering;


/**
 * @author Alessandro Jin
 */
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
    ResultadoClustering ejecutarClustering(List<Pregunta> variables,List<List<Respuesta>> respuestas, int k);
    
    /**
     * Nombre del algoritmo (para logging/reporting).
     */
    String getNombre();
}