package domain.algorithm;

import domain.*;
import java.util.*;
import domain.algorithm.estructuras.*;
import domain.algorithm.estructuras.Perfil;

/**
 * @author Alessandro Jin
 */

public class Kmeans extends Kalgorithm {

    private static final int MAX_ITERACIONES = 150;

    //devolver listado de nombre por grupo
    @Override
    public String getNombre() {
        return "Kmeans";
    }
    
    @Override
    public List<Perfil> ejecutarClustering(List<Pregunta> variables,List<List<Respuesta>> respuestas, int k) {
        // 1. Inicialización aleatoria de centroides
        List<Cluster> clusters = inicializarClusters(k, respuestas);
        
        // 2. Iteración hasta convergencia
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

    private static List<Cluster> inicializarClusters (int k, List<List<Respuesta>> respuestas) {
        List<Cluster> clusters = new ArrayList<>();
        Set<Integer> indicesRandoms = new HashSet<>();
        Random random = new Random();

        int N = respuestas.size();

        while(indicesRandoms.size() < k) {
            int indice = random.nextInt(N);
            indicesRandoms.add(indice);
        }

        for (int indice: indicesRandoms) {
            List<Respuesta> respuesta = respuestas.get(indice);

            Centroide centroide = TransformarEnCentroide.transformarEnCentroide(respuesta);

            clusters.add(new Cluster(centroide));
        }

        return clusters;
    }

    
}
