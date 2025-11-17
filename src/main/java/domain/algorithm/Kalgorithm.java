package domain.algorithm;

import java.util.List;
import java.util.ArrayList;

import domain.*;
import domain.algorithm.estructuras.*;
import domain.algorithm.funciones.*;
import domain.algorithm.estrategias.*;


/**
 * Clase abstracta base para algoritmos de clustering tipo K.
 * Implementa el patrón Template Method con dos estrategias:
 * - EstrategiaInicializacion: cómo seleccionar centroides iniciales
 * - EstrategiaRecalculo: cómo recalcular centroides tras reasignación
 * 
 * Algoritmo general:
 * 1. Inicializar k clusters con centroides según estrategia
 * 2. Asignar cada usuario al cluster más cercano
 * 3. Recalcular centroides según estrategia
 * 4. Repetir 2-3 hasta convergencia o MAX_ITERACIONES
 * 5. Evaluar calidad con coeficiente de silueta
 * 
 * @author Alessandro Jin
 */

public abstract class Kalgorithm implements ClusteringAlgorithm  {
    
    private static final int MAX_ITERACIONES = 150;
    
    /**
     * Estrategia para inicializar centroides.
     * @return Estrategia de inicialización (aleatoria, K-means++, etc.)
     */
    protected abstract EstrategiaInicializacion getEstrategiaInicializacion();
    
    /**
     * Estrategia para recalcular centroides.
     * @return Estrategia de recálculo (media para K-means, medoide para K-medoids)
     */
    protected abstract EstrategiaCalculoCentroide getEstrategiaCalculoCentroide();
    
    @Override
    public abstract String getNombre();
    
    @Override
    public ResultadoClustering ejecutarClustering(List<Pregunta> variables, List<List<Respuesta>> respuestas, int k) {
        List<Cluster> clusters = getEstrategiaInicializacion().inicializarClusters(k, respuestas, variables);
        
        // Iteración hasta convergencia
        boolean cambios = true;
        int iteracion = 0;
        
        while (cambios && iteracion < MAX_ITERACIONES) {
            Boolean [] cambiados = new Boolean[k];
            // Asignar cada usuario al cluster más cercano
            cambios = asignarUsuariosAClusters(clusters, respuestas, variables, cambiados);
            
            // Recalcular centroides según estrategia
            recalcularCentroides(clusters, respuestas, variables, cambiados);
           
            iteracion++;
        }
        
        List<Perfil> perfiles = listaPerfil(clusters, respuestas, variables);
        double calidad = EvaluarCluster.calcularCalidadCluster(clusters, respuestas, variables);

        ResultadoClustering res = new ResultadoClustering(perfiles, calidad);
        return res;
    }

    /**
     * Asigna cada usuario al cluster más cercano.
     * 
     * @param clusters Lista de clusters actuales
     * @param respuestas Matriz de respuestas de usuarios
     * @param variables Preguntas de la encuesta
     * @param cambiados Array de flags indicando qué clusters cambiaron
     * @return true si hubo cambios, false si convergió
     */
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

    /**
     * Detecta si hubo cambios en la asignación de usuarios a clusters.
     * 
     * @param antiguos Listas de miembros antes de reasignar
     * @param clusters Clusters después de reasignar
     * @param cambiados Array de salida con flags de cambio por cluster
     * @return true si algún cluster cambió
     */
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

    /**
     * Recalcula centroides de clusters que cambiaron usando estrategia específica.
     * 
     * @param clusters Lista de clusters
     * @param respuestas Matriz de respuestas
     * @param variables Preguntas de la encuesta
     * @param cambiados Array indicando qué clusters cambiaron
     */
    private void recalcularCentroides(List<Cluster> clusters, List<List<Respuesta>> respuestas, List<Pregunta> variables, Boolean[] cambiados) {
        int N = clusters.size();

        for (int i = 0; i < N; ++i) {
            if (cambiados[i]) {
                //si ha cambiado recalculamos
                List<Integer> miembros = clusters.get(i).getMiembros();

                List<List<Respuesta>> cluster = sacarMiembros(miembros, respuestas);

                Centroide c = getEstrategiaCalculoCentroide().calcularCentroide(cluster, variables);

                clusters.get(i).changeCentroide(c);
            }
        }
    }

    /**
     * Extrae respuestas de usuarios específicos por sus índices.
     * 
     * @param miembros Índices de usuarios
     * @param respuestas Matriz completa de respuestas
     * @return Lista de respuestas de los miembros
     */
    private static List<List<Respuesta>> sacarMiembros(List<Integer> miembros, List<List<Respuesta>> respuestas) {
        List<List<Respuesta>> cluster = new ArrayList<> ();

        for (Integer i: miembros) cluster.add(respuestas.get(i));
        return cluster;
    }

    /**
     * Convierte clusters en perfiles con nombres de usuarios.
     * 
     * @param clusters Lista de clusters con índices
     * @param respuestas Matriz de respuestas
     * @param variables Preguntas de la encuesta
     * @return Lista de perfiles con nombres de usuarios
     */
    private static List<domain.algorithm.estructuras.Perfil> listaPerfil(List<Cluster> clusters, List<List<Respuesta>> respuestas, List<Pregunta> variables) {
        List<domain.algorithm.estructuras.Perfil> perfiles = new ArrayList<>();

        for (Cluster cluster: clusters) {
            List<String> usuarios = new ArrayList<>();

            List<Integer> miembros = cluster.getMiembros();

            String representante = representanteCluster (cluster, respuestas, variables);

            for (Integer i : miembros) {
                for (Respuesta res : respuestas.get(i)) { // recorre hasta encontrar una respuesta no nula del usuario
                    if (res != null) {
                        String name = res.getNomUsuario(); // toda la fila es de un usuario
                        usuarios.add(name);
                        break;
                    }
                }
            }

            perfiles.add(new domain.algorithm.estructuras.Perfil(representante, cluster.getCentroide(), usuarios));
        }

        return perfiles;
    }

    /**
     * Encuentra el usuario más representativo (más cercano al centroide).
     * 
     * @param cluster Cluster del que obtener representante
     * @param respuestas Matriz de respuestas
     * @param variables Preguntas de la encuesta
     * @return Nombre del usuario representante
     */
    private static String representanteCluster(Cluster cluster, List<List<Respuesta>> respuestas, List<Pregunta> variables) {
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

        for (Respuesta res : respuestasGente.get(index)) { // recorre hasta encontrar una respuesta no nula del usuario
            if (res != null) {
                return res.getNomUsuario(); // toda la fila es de un usuario
            }
        }
        return "";
    }

}