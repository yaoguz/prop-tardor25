package domain.algorithm;

import java.util.List;
import java.util.ArrayList;

import domain.*;
import domain.algorithm.estructuras.*;
import domain.algorithm.funciones.*;


/**
 * @author Alessandro Jin
 */

public class Kalgorithm implements ClusteringAlgorithm  {
    /**
     * Las subclases deben implementar este método para especificar
     * cómo inicializar los clusters.
     */
    public abstract List<domain.algorithm.estructuras.Perfil> ejecutarClustering(List<Pregunta> variables, List<List<Respuesta>> respuestas, int k);
    
    @Override
    public abstract String getNombre();

    
    protected static Boolean asignarUsuariosAClusters(List<Cluster> clusters, List<List<Respuesta>> respuestas, List<Pregunta> variables, Boolean [] cambiados) {
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

            
            int minIndex = -1;
            double minim = Double.MAX_VALUE;
            for (int k = 0; k < M; ++k) {
                double distancia = CalculoDistanciaCentroide.calcularDistanciaCentroide(respuesta, clusters.get(k).getCentroide(), variables);

                if (distancia < minim) {
                    minim = distancia;
                    minIndex = k;
                }
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

    protected static void recalcularCentroides(List<Cluster> clusters, List<List<Respuesta>> respuestas, List<Pregunta> variables,  Boolean [] cambiados) {
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

    protected static List<domain.algorithm.estructuras.Perfil> listaPerfil(List<Cluster> clusters,List<List<Respuesta>> respuestas, List<Pregunta> variables) {
        List<domain.algorithm.estructuras.Perfil> perfiles = new ArrayList<>();

        for (Cluster cluster: clusters) {
            List<String> usuarios = new ArrayList<>();

            List<Integer> miembros = cluster.getMiembros();

            String representante = representanteCluster (cluster, respuestas, variables);

            for (Integer i : miembros) {
                String name = respuestas.get(i).get(0).getNomUsuario(); // toda la fila es de un usuario
                usuarios.add(name);
            }

            perfiles.add(new domain.algorithm.estructuras.Perfil(representante, cluster.getCentroide(), usuarios));
        }

        return perfiles;
    }

    private static String representanteCluster (Cluster cluster, List<List<Respuesta>> respuestas, List<Pregunta> variables) {
        Centroide c = cluster.getCentroide();
        List<Integer> miembros = cluster.getMiembros();
        List<List<Respuesta>> respuestasGente = sacarMiembros (miembros, respuestas);

        int n = respuestasGente.size();

        double minim = Double.MAX_VALUE;
        int index = -1;
        for (int i = 0; i < n; ++i) {
            double distancia = CalculoDistanciaCentroide.calcularDistanciaCentroide(respuestasGente.get(i), c, variables);

            if (distancia < minim) {
                minim = distancia;
                index = i;
            }
        }

        return respuestasGente.get(index).get(0).getNomUsuario();
    }
}