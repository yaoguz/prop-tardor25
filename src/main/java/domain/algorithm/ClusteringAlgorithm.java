package domain.algorithm;

import java.util.List;

import domain.Respuesta;
import domain.Pregunta;
import domain.algorithm.estructuras.ResultadoClustering;

/**
 * Interfaz para algoritmos de clustering.
 * Permite añadir nuevos algoritmos sin modificar código existente (Open/Closed).
 * 
 * @author Alessandro Jin
 */
public interface ClusteringAlgorithm {
    /**
     * Ejecuta el algoritmo de clustering.
     * 
     * @param variables Lista de preguntas/variables
     * @param respuestas Mapa de idUsuario -&gt; lista de respuestas
     * @param k Número de clusters deseados
     * @return Lista de clusters obtenidos
     */
    ResultadoClustering ejecutarClustering(List<Pregunta> variables,List<List<Respuesta>> respuestas, int k);
    
    /**
     * Nombre del algoritmo (para logging/reporting).
     * @return Nombre del algoritmo
     */
    String getNombre();
}