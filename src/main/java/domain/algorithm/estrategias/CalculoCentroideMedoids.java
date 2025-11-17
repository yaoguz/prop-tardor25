package domain.algorithm.estrategias;

import domain.*;
import domain.algorithm.estructuras.*;
import domain.algorithm.funciones.*;
import java.util.*;

/**
 * Recálculo de centroide usando medoide (K-medoids).
 * Selecciona el miembro más cercano al centro como nuevo centroide.
 * El medoide es un punto real del dataset, no un promedio calculado.
 * 
 * @author Xiangjun Yepan
 */
public class CalculoCentroideMedoids implements EstrategiaCalculoCentroide {
    
    @Override
    public static Centroide calcularCentroide(List<List<Respuesta>> cluster, List<Pregunta> variables) {
        double minDistanciaTotal = Double.MAX_VALUE;
        int index = -1;
        for(int i = 0; i < cluster.size(); ++i) {
            double distanciaTotal = 0.0;
            for(int j = 0; j < cluster.size(); ++j) {
                if (i == j) continue;
                distanciaTotal += CalculoDistanciaUsuario.calcularDistanciaUsuario(cluster.get(i), cluster.get(j), variables);
            }
            if(distanciaTotal < minDistanciaTotal) {
                minDistanciaTotal = distanciaTotal;
                index = i;
            }
        }
        return TransformarEnCentroide.transformarEnCentroide(cluster.get(index));
    }
}
