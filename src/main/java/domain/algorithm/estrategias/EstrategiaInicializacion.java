package domain.algorithm.estrategias;

import domain.*;
import domain.algorithm.estructuras.*;
import java.util.*;

/**
 * Estrategia de inicialización de clusters.
 * Define cómo seleccionar los centroides iniciales.
 */
public interface EstrategiaInicializacion {
    List<Cluster> inicializarClusters(int k, List<List<Respuesta>> respuestas, List<Pregunta> variables);
}
