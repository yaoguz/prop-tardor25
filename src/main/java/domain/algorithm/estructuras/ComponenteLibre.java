package domain.algorithm.estructuras;

/**
 * Componente de texto libre del centroide.
 * @author Alessandro Jin
 */
public class ComponenteLibre extends ComponenteCentroide {
    private final String texto;
    
    /**
     * Constructor del componente libre.
     * @param texto Texto representativo del centroide
     */
    public ComponenteLibre(String texto) {
        this.texto = texto;
    }
    
    /**
     * Obtiene el texto del componente.
     * @return Texto representativo
     */
    public String getTexto() {
        return texto;
    }
}
