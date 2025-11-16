package domain.algorithm.funciones;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import domain.*;
import domain.algorithm.estructuras.*;

/**
 * @author Xiangjun Yepan
 */
public class CalculoCentroideMedoids {
    public static Centride calculoCentroideMedoids(List<List<Respuesta>> cluster, List<Pregunta> variables) {
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