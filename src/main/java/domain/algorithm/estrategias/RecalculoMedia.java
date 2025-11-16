package domain.algorithm.estrategias;

import domain.*;
import domain.algorithm.estructuras.*;
import domain.algorithm.funciones.CalculoCentroide;
import java.util.*;

/**
 * Recalculo de centroide calculando la media/moda de miembros (K-means).
 * Calcula centroide como punto promedio en espacio multidimensional.
 * 
 * @author 
 */
public class RecalculoMedia implements EstrategiaRecalculo {
    
    @Override
    public Centroide recalcularCentroide(List<Integer> miembros, List<List<Respuesta>> respuestas, List<Pregunta> variables) {
        List<List<Respuesta>> cluster = new ArrayList<>();
        for (Integer i : miembros) {
            cluster.add(respuestas.get(i));
        }
        return CalculoCentroide.calcularCentroide(cluster, variables);
    }
}
