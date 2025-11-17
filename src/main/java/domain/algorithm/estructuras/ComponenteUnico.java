package domain.algorithm.estructuras;

/**
 * Componente de opcion unica del centroide.
 * @author Alessandro Jin
 */
public class ComponenteUnico extends ComponenteCentroide {
    private final int indice;
    
    /**
     * Constructor del componente de opción única.
     * @param indice Índice de la opción seleccionada
     */
    public ComponenteUnico(int indice) {
        this.indice = indice;
    }
    
    /**
     * Obtiene el índice de la opción.
     * @return Índice de la opción
     */
    public int getIndice() {
        return indice;
    }
}
