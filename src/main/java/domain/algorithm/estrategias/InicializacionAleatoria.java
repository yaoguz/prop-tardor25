package domain.algorithm.estrategias;

import domain.*;
import domain.algorithm.estructuras.*;
import domain.algorithm.funciones.TransformarEnCentroide;
import java.util.*;

/**
 * Inicialización aleatoria de centroides (K-means básico).
 * 
 * Selecciona k usuarios aleatorios del dataset como centroides iniciales.
 * Es la estrategia más simple pero puede caer en óptimos locales.
 * 
 * Algoritmo:
 * 1. Generar k índices aleatorios únicos en [0, n-1]
 * 2. Convertir cada usuario seleccionado en centroide
 * 3. Crear cluster con ese centroide
 * 
 * Complejidad: O(k × m) donde m = número de preguntas
 * 
 * @author Alessandro Jin
 */
public class InicializacionAleatoria implements EstrategiaInicializacion {
    
    @Override
    public List<Cluster> inicializarClusters(int k, List<List<Respuesta>> respuestas, List<Pregunta> variables) {
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
