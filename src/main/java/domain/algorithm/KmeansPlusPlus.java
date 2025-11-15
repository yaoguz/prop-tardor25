package domain.algorithm;

import domain.*;
import java.util.*;
import domain.algorithm.estructuras.*;
import domain.algorithm.funciones.*;

// pnd de revisar

/**
 * Implementación de K-means++ con inicialización inteligente de centroides.
 * 
 * K-means++ mejora la inicialización de K-means estándar seleccionando
 * centroides iniciales que estén bien separados entre sí, lo que resulta
 * en una convergencia más rápida y mejor calidad de clusters.
 * 
 * Algoritmo:
 * 1. Seleccionar primer centroide aleatoriamente
 * 2. Para cada centroide subsecuente:
 *    - Calcular distancia de cada punto al centroide más cercano
 *    - Seleccionar nuevo centroide con probabilidad proporcional a distancia²
 * 3. Ejecutar K-means estándar con estos centroides
 * 
 * @author Yaoyao Guo
 */
public class KmeansPlusPlus extends Kalgorithm {

    private static final int MAX_ITERACIONES = 150;

    @Override
    public String getNombre() {
        return "KMeans++";
    }
    
    @Override
    public List<domain.algorithm.estructuras.Perfil> ejecutarClustering(List<Pregunta> variables, List<List<Respuesta>> respuestas, int k) {
        // 1. Inicialización inteligente de centroides (K-means++)
        List<Cluster> clusters = inicializarClustersPlusPlus(k, respuestas, variables);
        
        // 2. Iteración hasta convergencia (igual que K-means estándar)
        boolean cambios = true;
        int iteracion = 0;
        
        while (cambios && iteracion < MAX_ITERACIONES) {
            Boolean [] cambiados = new Boolean[k];
            // Asignar cada usuario al cluster más cercano
            cambios = asignarUsuariosAClusters(clusters, respuestas, variables, cambiados);
            
            // Recalcular centroides
            recalcularCentroides(clusters, respuestas, variables, cambiados);
            
            iteracion++;
        }
        
        return listaPerfil(clusters, respuestas, variables);
    }

    /**
     * Inicialización K-means++: selecciona centroides bien separados.
     * 
     * @param k Número de clusters
     * @param respuestas Todas las respuestas
     * @param variables Variables (preguntas) del cuestionario
     * @return Lista de clusters inicializados
     */
    private static List<Cluster> inicializarClustersPlusPlus(int k, List<List<Respuesta>> respuestas, List<Pregunta> variables) {
        List<Cluster> clusters = new ArrayList<>();
        Random random = new Random();
        int N = respuestas.size();
        
        // Array para trackear qué respuestas ya son centroides
        Set<Integer> centroidesSeleccionados = new HashSet<>();
        
        // 1. Seleccionar primer centroide aleatoriamente
        int primerIndice = random.nextInt(N);
        centroidesSeleccionados.add(primerIndice);
        Centroide primerCentroide = TransformarEnCentroide.transformarEnCentroide(respuestas.get(primerIndice));
        clusters.add(new Cluster(primerCentroide));
        
        // 2. Seleccionar k-1 centroides restantes con probabilidad proporcional a distancia²
        for (int i = 1; i < k; i++) {
            // Calcular distancias mínimas de cada punto a su centroide más cercano
            double[] distanciasMinimas = new double[N];
            double sumDistanciasCuadradas = 0.0;
            
            for (int j = 0; j < N; j++) {
                if (centroidesSeleccionados.contains(j)) {
                    distanciasMinimas[j] = 0.0;
                    continue;
                }
                
                // Encontrar distancia al centroide más cercano
                double minDistancia = Double.MAX_VALUE;
                for (Cluster cluster : clusters) {
                    double distancia = CalculoDistanciaCentroide.calcularDistanciaCentroide(
                        respuestas.get(j), 
                        cluster.getCentroide(), 
                        variables
                    );
                    if (distancia < minDistancia) {
                        minDistancia = distancia;
                    }
                }
                
                distanciasMinimas[j] = minDistancia;
                sumDistanciasCuadradas += minDistancia * minDistancia;
            }
            
            // Seleccionar siguiente centroide con probabilidad proporcional a distancia²
            double randomValue = random.nextDouble() * sumDistanciasCuadradas;
            double acumulado = 0.0;
            int nuevoIndice = 0;
            
            for (int j = 0; j < N; j++) {
                if (centroidesSeleccionados.contains(j)) continue;
                
                acumulado += distanciasMinimas[j] * distanciasMinimas[j];
                if (acumulado >= randomValue) {
                    nuevoIndice = j;
                    break;
                }
            }
            
            // Agregar nuevo centroide
            centroidesSeleccionados.add(nuevoIndice);
            Centroide nuevoCentroide = TransformarEnCentroide.transformarEnCentroide(respuestas.get(nuevoIndice));
            clusters.add(new Cluster(nuevoCentroide));
        }
        
        return clusters;
    }
}
