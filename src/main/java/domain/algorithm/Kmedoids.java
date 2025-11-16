package domain.algorithm;

import domain.algorithm.estrategias.*;

/**
 * Implementación de K-medoids.
 * 
 * A diferencia de K-means, K-medoids usa medoides (puntos reales del dataset)
 * como centros de los clusters en lugar de centroides calculados.
 * 
 * TO DO: Implementar
 */
public class Kmedoids extends Kalgorithm {

    @Override
    public String getNombre() {
        return "KMedoids";
    }
    
    @Override
    protected EstrategiaInicializacion getEstrategiaInicializacion() {
        // TO DO: Implementar estrategia específica para K-medoids
        return new InicializacionAleatoria();
    }
    
    // TO DO: Sobrescribir ejecutarClustering si es necesario
    // K-medoids puede requerir lógica diferente en la asignación y recálculo
}
