package domain.algorithm.estructuras;

import java.util.List;

/**
 * Componente de opcion multiple del centroide.
 */
public class ComponenteMultiple extends ComponenteCentroide {
    private final List<Integer> indices;
    
    public ComponenteMultiple(List<Integer> indices) {
        this.indices = indices;
    }
    
    public List<Integer> getIndices() {
        return indices;
    }
}
