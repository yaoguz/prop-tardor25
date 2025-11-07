// domain/clustering/ClusterEvaluator.java
package domain.clustering_ejemplo;

import domain.Respuesta;
import java.util.*;

/**
 * Evalúa la calidad de un clustering usando el Coeficiente de Silueta.
 */
public class ClusterEvaluator {
    
    /**
     * Calcula el coeficiente de silueta global del clustering.
     * 
     * @param clusters Lista de clusters
     * @param respuestas Mapa de respuestas de usuarios
     * @return Valor en [-1, 1]. Cercano a 1 = buen clustering
     */
    public static double calcularCoeficienteSilueta(List<Cluster> clusters, 
                                                     Map<String, List<Respuesta>> respuestas) {
        double sumaSiluetas = 0.0;
        int totalUsuarios = 0;
        
        for (Cluster cluster : clusters) {
            for (String usuario : cluster.getMiembros()) {
                double silueta = calcularSiluetaIndividual(usuario, cluster, clusters, respuestas);
                sumaSiluetas += silueta;
                totalUsuarios++;
            }
        }
        
        return totalUsuarios > 0 ? sumaSiluetas / totalUsuarios : 0.0;
    }
    
    /**
     * Calcula s(i) para un individuo.
     * s(i) = (b(i) - a(i)) / max{a(i), b(i)}
     */
    private static double calcularSiluetaIndividual(String usuario, Cluster clusterPropio,
                                                     List<Cluster> todosLosClusters,
                                                     Map<String, List<Respuesta>> respuestas) {
        // a(i): distancia media con miembros del propio cluster (cohesión)
        double a = calcularDistanciaMediaIntraCluster(usuario, clusterPropio, respuestas);
        
        // b(i): distancia media mínima con miembros de otros clusters (separación)
        double b = calcularDistanciaMediaMinimaInterCluster(usuario, clusterPropio, 
                                                             todosLosClusters, respuestas);
        
        if (Math.max(a, b) == 0) return 0.0;
        
        return (b - a) / Math.max(a, b);
    }
    
    /**
     * Calcula a(i): distancia media entre i y miembros de su cluster.
     */
    private static double calcularDistanciaMediaIntraCluster(String usuario, Cluster cluster,
                                                              Map<String, List<Respuesta>> respuestas) {
        List<Respuesta> respuestasUsuario = respuestas.get(usuario);
        double sumaDistancias = 0.0;
        int count = 0;
        
        for (String otroUsuario : cluster.getMiembros()) {
            if (!otroUsuario.equals(usuario)) {
                double distancia = DistanceCalculator.calcularDistanciaGlobal(
                    respuestasUsuario, respuestas.get(otroUsuario)
                );
                sumaDistancias += distancia;
                count++;
            }
        }
        
        return count > 0 ? sumaDistancias / count : 0.0;
    }
    
    /**
     * Calcula b(i): distancia media mínima con otros clusters.
     */
    private static double calcularDistanciaMediaMinimaInterCluster(String usuario, 
                                                                    Cluster clusterPropio,
                                                                    List<Cluster> todosLosClusters,
                                                                    Map<String, List<Respuesta>> respuestas) {
        double distanciaMinima = Double.MAX_VALUE;
        
        for (Cluster otroCluster : todosLosClusters) {
            if (otroCluster.getId() == clusterPropio.getId()) continue;
            
            double distanciaMedia = calcularDistanciaMediaACluster(usuario, otroCluster, respuestas);
            distanciaMinima = Math.min(distanciaMinima, distanciaMedia);
        }
        
        return distanciaMinima;
    }
    
    /**
     * Calcula distancia media entre un usuario y todos los miembros de un cluster.
     */
    private static double calcularDistanciaMediaACluster(String usuario, Cluster cluster,
                                                          Map<String, List<Respuesta>> respuestas) {
        if (cluster.isEmpty()) return Double.MAX_VALUE;
        
        List<Respuesta> respuestasUsuario = respuestas.get(usuario);
        double sumaDistancias = 0.0;
        
        for (String miembro : cluster.getMiembros()) {
            double distancia = DistanceCalculator.calcularDistanciaGlobal(
                respuestasUsuario, respuestas.get(miembro)
            );
            sumaDistancias += distancia;
        }
        
        return sumaDistancias / cluster.size();
    }
}