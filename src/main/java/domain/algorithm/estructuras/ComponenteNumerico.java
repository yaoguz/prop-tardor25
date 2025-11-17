package domain.algorithm.estructuras;

/**
 * Componente numerico del centroide.
 * @author Alessandro Jin
 */
public class ComponenteNumerico extends ComponenteCentroide {
    private final double valor;
    
    /**
     * Constructor del componente numérico.
     * @param valor Valor numérico del centroide
     */
    public ComponenteNumerico(double valor) {
        this.valor = valor;
    }
    
    /**
     * Obtiene el valor numérico del componente.
     * @return Valor numérico
     */
    public double getValor() {
        return valor;
    }
}
