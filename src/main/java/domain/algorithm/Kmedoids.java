package domain.algorithm;

import domain.algorithm.estrategias.*;

/**
 * K-medoids (PAM - Partitioning Around Medoids).
 * 
 * Variante de K-means que usa medoides en lugar de centroides.
 * Un medoide es un punto REAL del dataset (usuario existente) que
 * mejor representa su cluster, no un promedio calculado.
 * 
 * Algoritmo:
 * 1. Selecciona k usuarios aleatorios como medoides iniciales
 * 2. Asigna cada usuario al medoide más cercano
 * 3. Para cada cluster, encuentra el usuario más central (nuevo medoide)
 * 4. Repite 2-3 hasta convergencia
 * 
 * Diferencias con K-means:
 * - K-means: centroide = media/moda (punto virtual)
 * - K-medoids: medoide = usuario real del dataset
 * 
 * Ventajas:
 * - Más robusto a outliers
 * - Centros interpretables (usuarios reales)
 * - Mejor para distancias no euclidianas
 * 
 * Desventajas:
 * - Ligeramente más lento (búsqueda de medoide)
 * - Complejidad: O(k × n² × i × m)
 * 
 * Características:
 * - Inicialización: Aleatoria
 * - Recálculo: Medoide (usuario más cercano al centro)
 * 
 * @author Alessandro Jin
 */
public class Kmedoids extends Kalgorithm {

    @Override
    public String getNombre() {
        return "KMedoids";
    }
    
    @Override
    protected EstrategiaInicializacion getEstrategiaInicializacion() {
        return new InicializacionAleatoria();
    }
    
    @Override
    protected EstrategiaRecalculo getEstrategiaRecalculo() {
        return new RecalculoMedoide();
    }
}
