// domain/clustering/KMeans.java
package domain.clustering_ejemplo;

import domain.Respuesta;
import java.util.*;

/**
 * Implementación del algoritmo k-means.
 */
public class KMeans implements ClusteringAlgorithm {
    
    private Random random;
    private int maxIteraciones;
    
    public KMeans() {
        this.random = new Random();
        this.maxIteraciones = 100;  // Límite para evitar bucles infinitos
    }
    
    @Override
    public List<Cluster> ejecutarClustering(Map<String, List<Respuesta>> respuestas, int k) {
        // 1. Inicialización aleatoria de centroides
        List<Cluster> clusters = inicializarClusters(k, respuestas);
        
        // 2. Iteración hasta convergencia
        boolean cambios = true;
        int iteracion = 0;
        
        while (cambios && iteracion < maxIteraciones) {
            // Asignar cada usuario al cluster más cercano
            cambios = asignarUsuariosAClusters(clusters, respuestas);
            
            // Recalcular centroides
            recalcularCentroides(clusters, respuestas);
            
            iteracion++;
        }
        
        return clusters;
    }
    
    /**
     * Inicialización aleatoria: selecciona k usuarios al azar como centroides.
     * Protected para permitir sobrescritura en KMeansPlusPlus.
     * 
     * @param k Número de clusters
     * @param respuestas Mapa de respuestas por usuario
     * @return Lista de clusters con centroides inicializados
     */
    protected List<Cluster> inicializarClusters(int k, Map<String, List<Respuesta>> respuestas) {
        List<Cluster> clusters = new ArrayList<>();
        List<String> usuarios = new ArrayList<>(respuestas.keySet());
        
        // Seleccionar k usuarios aleatorios como centroides iniciales
        Collections.shuffle(usuarios, random);
        
        for (int i = 0; i < k && i < usuarios.size(); i++) {
            Cluster cluster = new Cluster(i);
            // TODO: Convertir respuestas del usuario a centroide numérico
            // usando CentroidCalculator
            clusters.add(cluster);
        }
        
        return clusters;
    }
    
    /**
     * Asigna cada usuario al cluster con centroide más cercano.
     * 
     * @return true si hubo cambios en las asignaciones
     */
    private boolean asignarUsuariosAClusters(List<Cluster> clusters, 
                                              Map<String, List<Respuesta>> respuestas) {
        boolean cambios = false;
        
        // Guardar asignaciones anteriores
        Map<String, Integer> asignacionesAnteriores = new HashMap<>();
        for (Cluster c : clusters) {
            for (String usuario : c.getMiembros()) {
                asignacionesAnteriores.put(usuario, c.getId());
            }
        }
        
        // Limpiar clusters
        for (Cluster c : clusters) {
            c.limpiarMiembros();
        }
        
        // Reasignar cada usuario
        for (Map.Entry<String, List<Respuesta>> entry : respuestas.entrySet()) {
            String usuario = entry.getKey();
            List<Respuesta> respuestasUsuario = entry.getValue();
            
            // Encontrar cluster más cercano
            int clusterMasCercano = encontrarClusterMasCercano(respuestasUsuario, clusters);
            clusters.get(clusterMasCercano).agregarMiembro(usuario);
            
            // Verificar si hubo cambio
            Integer asignacionAnterior = asignacionesAnteriores.get(usuario);
            if (asignacionAnterior == null || asignacionAnterior != clusterMasCercano) {
                cambios = true;
            }
        }
        
        return cambios;
    }
    
    /**
     * Encuentra el cluster con centroide más cercano al usuario.
     */
    private int encontrarClusterMasCercano(List<Respuesta> respuestasUsuario, 
                                            List<Cluster> clusters) {
        double distanciaMinima = Double.MAX_VALUE;
        int clusterMasCercano = 0;
        
        for (int i = 0; i < clusters.size(); i++) {
            // Calcular distancia entre usuario y centroide del cluster
            // (simplificación: necesitarías convertir centroide a formato comparable)
            double distancia = calcularDistanciaAlCentroide(respuestasUsuario, 
                                                             clusters.get(i).getCentroide());
            
            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                clusterMasCercano = i;
            }
        }
        
        return clusterMasCercano;
    }
    
    /**
     * Recalcula centroides como media de los miembros de cada cluster.
     */
    private void recalcularCentroides(List<Cluster> clusters, 
                                       Map<String, List<Respuesta>> respuestas) {
        for (Cluster cluster : clusters) {
            if (cluster.isEmpty()) continue;
            
            // Calcular media de las respuestas de todos los miembros
            List<Double> nuevoCentroide = calcularMediaRespuestas(cluster.getMiembros(), respuestas);
            cluster.setCentroide(nuevoCentroide);
        }
    }
    
    // Métodos auxiliares (placeholders - necesitarías implementar según vuestras clases)
    private double calcularDistanciaAlCentroide(List<Respuesta> respuestas, List<Double> centroide) {
        // TODO: Implementar conversión respuesta -> vector numérico
        return 0.0;
    }
    
    private List<Double> calcularMediaRespuestas(List<String> miembros, 
                                                   Map<String, List<Respuesta>> respuestas) {
        // TODO: Implementar cálculo de media según fórmulas del PDF
        return new ArrayList<>();
    }
    
    @Override
    public String getNombre() {
        return "K-Means";
    }
}