package domain.algorithm.estructuras;

import java.util.List;

/**
 * @author Alessandro Jin
 */

/**
 * Representa el vector centroide de un clúster de forma dinámica.
 * Almacena los N componentes calculados como una lista de objetos ComponenteCentroide.
 */
public class Centroide {
    
    private final List<ComponenteCentroide> vectorComponentes;

    /**
     * Constructor que acepta el vector completo de componentes calculados.
     */
    public Centroide(List<ComponenteCentroide> vectorComponentes) {
        // Asegura que el centroide se inicializa con los N valores de las variables.
        this.vectorComponentes = vectorComponentes;
    }

    /**
     * Devuelve el vector completo de componentes del centroide (la media, moda, medoide, etc.).
     */
    public List<ComponenteCentroide> getVectorComponentes() {
        return this.vectorComponentes;
    }

    /**
     * Devuelve el componente del centroide para la variable en la posición 'indice'.
     */
    public ComponenteCentroide getComponente(int indice) {
        if (indice >= 0 && indice < vectorComponentes.size()) {
            return vectorComponentes.get(indice);
        }
        throw new IndexOutOfBoundsException("Índice de componente fuera de límites.");
    }
}
