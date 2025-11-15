package domain.algoritmo;

import java.util.List;
import java.util.Map;

import domain.Respuesta;
import domain.algoritmo.estructuras.Centroide;

/**
 * Interfaz para algoritmos de clustering.
 * Permite añadir nuevos algoritmos sin modificar código existente (Open/Closed).
 */
public class Kalgorithm implements ClusteringAlgorithm  {
    /**
     * Ejecuta el algoritmo de clustering.
     * 
     * @param respuestas Mapa de idUsuario -> lista de respuestas
     * @param k Número de clusters deseados
     * @return Lista de clusters obtenidos
     */
    
    @Override
    List<Perfil> ejecutarClustering(List<Pregunta> variables, List<List<Respuesta>> respuestas, int k);

    @Override
    String getNombre();

    
    private static Boolean asignarUsuarioClusters(List<Cluster> clusters, List<List<Respuesta>> respuestas, List<Pregunta> variables, Boolean [] cambiados) {
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

    private static List<Perfil> listaPerfil(List<Cluster> clusters,List<List<Respuesta>> respuestas, List<Pregunta> variables) {
        List<Perfil> perfiles = new ArrayList<>();

        for (Cluster cluster: clusters) {
            List<String> usuarios = new ArrayList<>();

            List<Integer> miembros = cluster.getMiembros();

            String representante = representanteCluster (cluster, respuestas, variables);

            for (Integer i : miembros) {
                String name = respuestas.get(i).get(0).getNomUsuario(); // toda la fila es de un usuario
                usuarios.add(name);
            }

            perfiles.add(Perfil(representante, cluster.getCentroide(), usuarios));
        }

        return perfiles;
    }

    private static String representanteCluster (Cluster cluster, List<List<Respuesta>> respuestas, List<Pregunta> variables) {
        Centride c = cluster.getCentroide();
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