package domain.algoritmo.estructuras;

/**
 * @author Alessandro Jin
 */
/**
 * Componente numerico del centroide.
 */
public class ComponenteNumerico extends ComponenteCentroide {
    private final double valor;
    
    public ComponenteNumerico(double valor) {
        this.valor = valor;
    }
    
    public double getValor() {
        return valor;
    }
}
