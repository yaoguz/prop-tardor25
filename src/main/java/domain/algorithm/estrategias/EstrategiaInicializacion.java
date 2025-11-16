package domain.algorithm.estrategias;

import domain.*;
import domain.algorithm.estructuras.*;
import java.util.*;

/**
 * Estrategia para inicializar centroides de clusters (Patrón Strategy).
 * Define cómo seleccionar los k centroides iniciales.
 * 
 * Implementaciones:
 * - InicializacionAleatoria: Selección aleatoria (K-means básico)
 * - InicializacionPlusPlus: Selección inteligente (K-means++)
 * 
 * @author Yaoyao Guo
 */
public interface EstrategiaInicializacion {
    /**
     * Inicializa k clusters con centroides según la estrategia.
     * 
     * @param k Número de clusters deseados
     * @param respuestas Matriz de respuestas (usuarios × preguntas)
     * @param variables Preguntas de la encuesta
     * @return Lista de k clusters inicializados
     */
    List<Cluster> inicializarClusters(int k, List<List<Respuesta>> respuestas, List<Pregunta> variables);
}
