package domain.algorithm;

import domain.algorithm.estrategias.*;

/**
 * K-means básico con inicialización aleatoria.
 * 
 * Algoritmo:
 * 1. Selecciona k centroides aleatorios del dataset
 * 2. Asigna cada punto al centroide más cercano
 * 3. Recalcula centroides como media de puntos asignados
 * 4. Repite 2-3 hasta convergencia
 * 
 * Características:
 * - Inicialización: Aleatoria (puede caer en óptimos locales)
 * - Recálculo: Media aritmética (centroide promedio)
 * - Complejidad: O(n × k × i × m) donde i = iteraciones, m = preguntas
 * 
 * @author Alessandro Jin
 */
public class Kmeans extends Kalgorithm {

    @Override
    public String getNombre() {
        return "Kmeans";
    }
    
    @Override
    protected EstrategiaInicializacion getEstrategiaInicializacion() {
        return new InicializacionAleatoria();
    }
    
    @Override
    protected EstrategiaRecalculo getEstrategiaRecalculo() {
        return new RecalculoMedia();
    }
}
