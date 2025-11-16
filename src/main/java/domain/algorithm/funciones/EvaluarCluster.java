package domain.algorithm;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import domain.*;
import domain.algorithm.estructuras.*;
import domain.algorithm.funciones.*;
import domain.algorithm.estrategias.*;

public class EvaluarCluster {
        public static double calcularCalidadCluster(List<Cluster> clusters, List<List<Respuesta>> respuestas, List<Pregunta> variables) {
        if (clusters.size() <= 1) {
            // Si solo hay un cluster, la silueta no está definida
            return 0.0;
        }
        int n = clusters.size();
        Map<Integer, List<List<Respuesta>>> clustersAnswers = new HashMap<>();

        for (int i = 0; i < n; ++i) {
            //separas las respuestas por grupos de cluster
            List<List<Respuesta>> clusterAnswer = separaRespuestasPorCluster(clusters.get(i), respuestas);
            clustersAnswers.put(i, clusterAnswer);
        }

        double sumaSiluetaTotal = 0.0;
        int totalPersonas = 0; 

        //para todos los cluster
        for (int i = 0; i < n; ++i) {
            List<List<Respuesta>> cluster = clustersAnswers.get(i);
            if (cluster.isEmpty()) continue;

            if (cluster.size() == 1) {
                totalPersonas++; //cluster de 1, mala separacion
                continue;
            }

            //para cada persona (como se mira todos los cluster es para todas las personas)
            for (int j = 0; j < cluster.size(); ++j) {
                List<Respuesta> persona = cluster.get(j);

                double siluetaDelCluster = calcularSiluetaPersonaCluster(persona, cluster, variables, j);

                double siluetaMinOtros = Double.MAX_VALUE;
               
                for (int k = 0; k < n; ++k) {
                    if (k == i) continue; //con su cluster no se hace
                    List<List<Respuesta>> other = clustersAnswers.get(k);

                    if (other.size() == 1) continue; //Es solo uno, no representan un grupo bueno
                    
                    double siluetaOtro = calcularSiluetaPersonaCluster(persona, other , variables, -1);

                    siluetaMinOtros = Math.min(siluetaMinOtros, siluetaOtro);
            
                }

                if (siluetaMinOtros == Double.MAX_VALUE) siluetaMinOtros = 0.0;

                double denom = Math.max(siluetaMinOtros,siluetaDelCluster);

                double s_i = (denom == 0.0) ? 0.0 : (siluetaMinOtros - siluetaDelCluster) / denom;

                sumaSiluetaTotal += s_i;

                totalPersonas++;
            }

        }
       
        return (totalPersonas > 0) ? sumaSiluetaTotal / totalPersonas : 0.0;
    }

    private static List<List<Respuesta>> separaRespuestasPorCluster(Cluster cluster, List<List<Respuesta>> respuestas) {
        List<Integer> miembros = cluster.getMiembros();

        List<List<Respuesta>> res = new ArrayList<> ();

        for (Integer i: miembros) res.add(respuestas.get(i));
        return res;
    }

    private static double calcularSiluetaPersonaCluster (List<Respuesta> persona, List<List<Respuesta>> grupo, List<Pregunta> variables, int elMismo) {
        double sumaDistancias = 0.0;
        int n = grupo.size();
        if (n == 0) return 0.0;

        for (int i = 0; i < n; ++i) {
            if (i == elMismo) continue;
            List<Respuesta> otro = grupo.get(i);
            sumaDistancias += CalculoDistanciaUsuario.calcularDistanciaUsuario(persona, otro, variables);
        }

        return (elMismo >= 0) ? sumaDistancias/ (n-1)  : sumaDistancias/n;
    }
}
