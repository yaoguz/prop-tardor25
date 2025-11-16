package domain.algorithm.estrategias;

import domain.*;
import domain.algorithm.estructuras.*;
import domain.algorithm.funciones.*;
import java.util.*;

/**
 * Inicialización K-means++: selecciona centroides bien separados.
 * 
 * @author Yaoyao Guo Zhang
 */
public class InicializacionPlusPlus implements EstrategiaInicializacion {
    
    /**
     * Inicialización K-means++: selecciona centroides bien separados.
     * 
     * @param k Número de clusters
     * @param respuestas Todas las respuestas
     * @param variables Variables (preguntas) del cuestionario
     * @return Lista de clusters inicializados
     */
    @Override
    public List<Cluster> inicializarClusters(int k, List<List<Respuesta>> respuestas, List<Pregunta> variables) {
        List<Cluster> clusters = new ArrayList<>();
        Random random = new Random();
        int N = respuestas.size();
        
        Set<Integer> centroidesSeleccionados = new HashSet<>();
        
        // seleccionar el primer centroide aleatoriamente
        int primerIndice = random.nextInt(N);
        centroidesSeleccionados.add(primerIndice);
        Centroide primerCentroide = TransformarEnCentroide.transformarEnCentroide(respuestas.get(primerIndice));
        clusters.add(new Cluster(primerCentroide));
        
        // seleccionar k-1 centroides restantes con probabilidad proporcional a distancia^2
        for (int i = 1; i < k; i++) {
            double[] distanciasMinimas = new double[N];
            double sumDistanciasCuadradas = 0.0;
            
            for (int j = 0; j < N; j++) { // recorrer respuestas
                if (centroidesSeleccionados.contains(j)) {
                    distanciasMinimas[j] = 0.0;
                    continue;
                }
                
                double minDistancia = Double.MAX_VALUE; // de esta respuesta al centroide mas cercano
                for (Cluster cluster : clusters) {
                    double distancia = CalculoDistanciaCentroide.calcularDistanciaCentroide(
                                       respuestas.get(j), cluster.getCentroide(), variables);
                    if (distancia < minDistancia) minDistancia = distancia;
                }                
                distanciasMinimas[j] = minDistancia;
                sumDistanciasCuadradas += minDistancia * minDistancia; // asignar intervalo de la respuesta
            }
            
            // generar numero random entre 0 y sumDistanciasCuadradas
            // que caerá en alguno de los intervalos de una respuesta
            double randomValue = random.nextDouble() * sumDistanciasCuadradas;
            double acumulado = 0.0;
            int nuevoIndice = 0;
            
            for (int j = 0; j < N; j++) {
                if (centroidesSeleccionados.contains(j)) continue;
                
                // poner comentarios
                acumulado += distanciasMinimas[j] * distanciasMinimas[j];
                if (acumulado >= randomValue) { // randomValue esta dentro del intervalo asignado a la preg j
                    nuevoIndice = j;
                    break;
                }
            }
            
            // anadir el nuevo centroide
            centroidesSeleccionados.add(nuevoIndice);
            Centroide nuevoCentroide = TransformarEnCentroide.transformarEnCentroide(respuestas.get(nuevoIndice));
            clusters.add(new Cluster(nuevoCentroide));
        }
        
        return clusters;
    }
}
