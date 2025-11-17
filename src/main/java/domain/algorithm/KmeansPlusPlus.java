package domain.algorithm;

import domain.algorithm.estrategias.*;

/**
 * K-means++ con inicialización inteligente de centroides.
 * 
 * Mejora sobre K-means básico con mejor selección de centroides iniciales.
 * Los centroides se seleccionan para estar bien separados entre sí,
 * evitando óptimos locales y mejorando convergencia.
 * 
 * Algoritmo:
 * 1. Seleccionar primer centroide aleatoriamente
 * 2. Para cada centroide siguiente (k-1 veces):
 *    - Calcular D(x) = distancia de cada punto al centroide más cercano
 *    - Seleccionar nuevo centroide con probabilidad proporcional a D(x)²
 * 3. Ejecutar K-means normal con estos centroides
 * 
 * Ventajas:
 * - Mejor calidad de clustering (mayor silueta)
 * - Convergencia más rápida (menos iteraciones)
 * - Evita clusters vacíos
 * 
 * Características:
 * - Inicialización: K-means++ (inteligente)
 * - Recálculo: Media aritmética (igual que K-means)
 * - Complejidad init: O(k × n × m)
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
    
    @Override
    protected EstrategiaCalculoCentroide getEstrategiaCalculoCentroide() {
        return new CalculoCentroideKmeans();
    }
}