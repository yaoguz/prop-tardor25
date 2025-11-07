// domain/clustering/KMeansPlusPlus.java
package domain.clustering_ejemplo;

import domain.Respuesta;
import java.util.*;

/**
 * Implementación de k-means++ (inicialización mejorada).
 */
public class KMeansPlusPlus extends KMeans {
    
    @Override
    protected List<Cluster> inicializarClusters(int k, Map<String, List<Respuesta>> respuestas) {
        List<Cluster> clusters = new ArrayList<>();
        List<String> usuarios = new ArrayList<>(respuestas.keySet());
        Random random = new Random();
        
        // 1. Primer centroide aleatorio
        String primerUsuario = usuarios.get(random.nextInt(usuarios.size()));
        Cluster primerCluster = new Cluster(0);
        // Establecer centroide...
        clusters.add(primerCluster);
        
        // 2. Seleccionar k-1 centroides restantes con probabilidad proporcional a D(x)²
        for (int i = 1; i < k; i++) {
            String nuevoUsuario = seleccionarSiguienteCentroide(usuarios, clusters, respuestas, random);
            Cluster nuevoCluster = new Cluster(i);
            // Establecer centroide...
            clusters.add(nuevoCluster);
        }
        
        return clusters;
    }
    
    /**
     * Selecciona siguiente centroide con probabilidad proporcional a D(x)².
     */
    private String seleccionarSiguienteCentroide(List<String> usuarios, 
                                                   List<Cluster> clusters,
                                                   Map<String, List<Respuesta>> respuestas,
                                                   Random random) {
        // Calcular D(x)² para cada usuario (distancia mínima al centroide más cercano)
        Map<String, Double> distanciasCuadradas = new HashMap<>();
        double sumaTotal = 0.0;
        
        for (String usuario : usuarios) {
            double distanciaMinima = calcularDistanciaMinimaACentroides(
                respuestas.get(usuario), clusters
            );
            double distanciaCuadrada = distanciaMinima * distanciaMinima;
            distanciasCuadradas.put(usuario, distanciaCuadrada);
            sumaTotal += distanciaCuadrada;
        }
        
        // Selección por ruleta
        double valorAleatorio = random.nextDouble() * sumaTotal;
        double acumulado = 0.0;
        
        for (Map.Entry<String, Double> entry : distanciasCuadradas.entrySet()) {
            acumulado += entry.getValue();
            if (acumulado >= valorAleatorio) {
                return entry.getKey();
            }
        }
        
        return usuarios.get(usuarios.size() - 1);  // Fallback
    }
    
    private double calcularDistanciaMinimaACentroides(List<Respuesta> respuestas, 
                                                       List<Cluster> clusters) {
        // TODO: Implementar
        return 1.0;
    }
    
    @Override
    public String getNombre() {
        return "K-Means++";
    }
}