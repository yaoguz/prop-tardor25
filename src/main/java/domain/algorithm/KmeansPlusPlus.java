package domain.algorithm;

import domain.algorithm.estrategias.*;

/**
 * Implementación de K-means++ con una buena inicialización de centroides.
 * 
 * K-means++ selecciona centroides iniciales que estén bien separados entre sí
 * -> convergencia más rápida y mejor calidad de clusters.
 * 
 * Algoritmo:
 * 1. Seleccionar primer centroide aleatoriamente
 * 2. Para cada centroide siguiente:
 *    - Calcular distancia de cada punto al centroide más cercano
 *    - Seleccionar nuevo centroide con probabilidad proporcional a distancia^2
 * 3. Ejecutar K-means normal con estos centroides
 * 
 * @author Yaoyao Guo
 */
public class KmeansPlusPlus extends Kalgorithm {

    @Override
    public String getNombre() {
        return "KMeans++";
    }
    
    @Override
    protected EstrategiaInicializacion getEstrategiaInicializacion() {
        return new InicializacionPlusPlus();
    }
}