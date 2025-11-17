package domain.algorithm.estructuras;

import java.util.List;

/**
 * Componente de opcion multiple del centroide.
 * @author Alessandro Jin
 */
public class ComponenteMultiple extends ComponenteCentroide {
    private final List<Integer> indices;
    
    /**
     * Constructor del componente de opción múltiple.
     * @param indices Lista de índices de opciones seleccionadas
     */
    public ComponenteMultiple(List<Integer> indices) {
        this.indices = indices;
    }
    
    /**
     * Obtiene la lista de índices de opciones.
     * @return Lista de índices
     */
    public List<Integer> getIndices() {
        return indices;
    }
}
