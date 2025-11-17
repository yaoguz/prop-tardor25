package domain.algorithm.estructuras;

import java.util.List;

/**
 * Representa el vector centroide de un clúster de forma dinámica.
 * 
 * Responsabilidades:
 * - Almacenar N componentes del centroide (media/moda/medoide según tipo)
 * - Proporcionar acceso a componentes individuales o completos
 * - Mantener estructura inmutable del centroide
 * 
 * @author Alessandro Jin
 */
public class Centroide {
    /** Lista de componentes del centroide (uno por cada pregunta de la encuesta) */
    private final List<ComponenteCentroide> vectorComponentes;

    /**
     * Constructor que acepta el vector completo de componentes calculados.
     * @param vectorComponentes Lista de componentes del centroide
     */
    public Centroide(List<ComponenteCentroide> vectorComponentes) {
        // Asegura que el centroide se inicializa con los N valores de las variables.
        this.vectorComponentes = vectorComponentes;
    }

    /**
     * Devuelve el vector completo de componentes del centroide (la media, moda, medoide, etc.).
     * @return Lista de componentes del centroide
     */
    public List<ComponenteCentroide> getVectorComponentes() {
        return this.vectorComponentes;
    }

    /**
     * Devuelve el componente del centroide para la variable en la posición 'indice'.
     * @param indice Posición del componente
     * @return Componente en la posición indicada
     */
    public ComponenteCentroide getComponente(int indice) {
        if (indice >= 0 && indice < vectorComponentes.size()) {
            return vectorComponentes.get(indice);
        }
        throw new IndexOutOfBoundsException("Índice de componente fuera de límites.");
    }
}
