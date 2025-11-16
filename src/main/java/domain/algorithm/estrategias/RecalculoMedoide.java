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
 * @author 
 */
public class RecalculoMedoide implements EstrategiaRecalculo {
    
    @Override
    public Centroide recalcularCentroide(List<Integer> miembros, List<List<Respuesta>> respuestas, List<Pregunta> variables) {
        if (miembros.isEmpty()) {
            // Cluster vacío, mantener centroide anterior
            return null;
        }
        
        // Calcular centroide promedio temporal
        List<List<Respuesta>> cluster = new ArrayList<>();
        for (Integer i : miembros) {
            cluster.add(respuestas.get(i));
        }
        Centroide centroideTemp = CalculoCentroide.calcularCentroide(cluster, variables);
        
        // Buscar miembro más cercano al centroide (medoide)
        double minDistancia = Double.MAX_VALUE;
        int indiceMedoide = miembros.get(0);
        
        for (Integer i : miembros) {
            double distancia = CalculoDistanciaCentroide.calcularDistanciaCentroide(
                respuestas.get(i), centroideTemp, variables);
            if (distancia < minDistancia) {
                minDistancia = distancia;
                indiceMedoide = i;
            }
        }
        
        // Convertir el medoide (usuario real) en centroide
        return TransformarEnCentroide.transformarEnCentroide(respuestas.get(indiceMedoide));
    }
}
