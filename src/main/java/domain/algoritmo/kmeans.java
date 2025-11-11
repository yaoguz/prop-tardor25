package domain.algoritmo;

import domain.*;
import java.util.*;

/**
 * @author Alessandro Jin
 */

public class Kmeans implements ClusteringAlgorithm {

    private static final int MAX_ITERACIONES = 150;

    //devolver listado de nombre por grupo
    @Override
    public String getNombre() {
        return "Kmeans"
    }
    
    @Override
    public Map<Centroide,List<String>> ejecutarClustering(List<List<Respuesta>> respuestas, List<Pregunta> variables, int k) {
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
        
        return listaPerfil(clusters, respuestas);
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

    private static Boolean asignarUsuariosAClusters(List<Cluster> clusters, List<List<Respuesta>> respuestas, List<Pregunta> variables, Boolean [] cambiados) {
        int N = respuestas.size();
        int M = clusters.size();

        List<List<Integer>> miembrosAntiguos = new ArrayList<>();
        for (int i = 0; i < M; ++i) {
            miembrosAntiguos.add(clusters.get(i).getMiembros());
            clusters.get(i).borrarMiembros();
        }

        //recorre todas las respuestas
        for (int i = 0; i < N; ++i) {
            List<Respuesta> respuesta = respuestas.get(i);

            
            double [] distancias = new double [M];
            
        
            for (int k = 0; k < M; ++k) {
                distancias[k] = CalculoDistanciaCentroide.calcularDistanciaCentroide(respuesta, clusters.get(k).getCentroide(), variables);
            }

            int minIndex = 0;

            for (int k = 1; k < M; ++k) {
                if (distancias[k] < distancias[minIndex]) minIndex = k;
            }


            clusters.get(minIndex).addMiembro(i);

        }

        //a ver si ha habido cambios

        return cambiosClusters(miembrosAntiguos, clusters, cambiados);
    }

    private static Boolean cambiosClusters(List<List<Integer>> antiguos, List<Cluster> clusters, Boolean[] cambiados) {
        Boolean cambio = false;

        int N = clusters.size();
        for (int i = 0; i < N; ++i) {
            List<Integer> nuevo = clusters.get(i).getMiembros();

            //si tuvo misma asignación el orden debe ser igual 
            //ya que el orden de las filas no cambia

            cambiados[i] = !(nuevo.equals(antiguos.get(i)));

            if (!cambio) cambio = cambiados[i];
        }

        return cambio;
    }

    private static void recalcularCentroides(List<Cluster> clusters, List<List<Respuesta>> respuestas, List<Pregunta> variables,  Boolean [] cambiados) {
        int N = clusters.size();

        for (int i = 0; i < N; ++i) {
            if (cambiados[i]) {
                //si ha cambiado recalculamos
                List<Integer> miembros = clusters.get(i).getMiembros();

                List<List<Respuesta>> cluster = sacarMiembros(miembros, respuestas);

                Centroide c = CalculoCentroide.calcularCentroide(cluster, variables);

                clusters.get(i).changeCentroide(c);
            }
        }
    }

    private static List<List<Respuesta>> sacarMiembros(List<Integer> miembros, List<List<Respuesta>> respuestas ) {
        List<List<Respuesta>> cluster = new ArrayList<> ();

        for (Integer i: miembros) cluster.add(respuestas.get(i));
        return cluster;
    }

    private static Map<Centroide,List<String>> listaPerfil(List<Cluster> clusters,List<List<Respuesta>> respuestas) {
        Map<Centroide,List<String>> perfiles = new HashMap<>();
        for (Cluster cluster: clusters) {
            List<String> usuarios = new ArrayList<>();

            List<Integer> miembros = cluster.getMiembros();

            for (Integer i : miembros) {
                String name = respuestas.get(i).get(0).getNomUsuario(); // toda la fila es de un usuario
                usuarios.add(name);
            }

            perfiles.put(cluster.getCentroide(), usuarios);
        }

        return perfiles;
    }
}
