package domain.algorithm.estrategias;

import domain.*;
import domain.algorithm.estructuras.*;
import java.util.*;

/**
 * Estrategia para recalcular centroides de clusters.
 * Define cómo actualizar el centroide tras reasignar miembros.
 * 
 * @author Alessandro Jin
 */
public interface EstrategiaCalculoCentroide {
    /**
     * Recalcula el centroide de un cluster.
     * 
     * @param respuestas Matriz de respuestas (todas)
     * @param variables Preguntas de la encuesta
     * @return Nuevo centroide calculado
     */
    Centroide calcularCentroide(List<List<Respuesta>> respuestas, List<Pregunta> variables);
}
