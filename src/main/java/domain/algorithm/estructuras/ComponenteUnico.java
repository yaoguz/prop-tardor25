package domain.algorithm.estructuras;

/**
 * @author Alessandro Jin
 */
/**
 * Componente de opcion unica del centroide.
 */
public class ComponenteUnico extends ComponenteCentroide {
    private final int indice;
    
    public ComponenteUnico(int indice) {
        this.indice = indice;
    }
    
    public int getIndice() {
        return indice;
    }
}
