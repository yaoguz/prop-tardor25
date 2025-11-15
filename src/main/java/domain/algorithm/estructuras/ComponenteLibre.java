package domain.algorithm.estructuras;

/**
 * @author Alessandro Jin
 */
/**
 * Componente de texto libre del centroide.
 */
public class ComponenteLibre extends ComponenteCentroide {
    private final String texto;
    
    public ComponenteLibre(String texto) {
        this.texto = texto;
    }
    
    public String getTexto() {
        return texto;
    }
}
