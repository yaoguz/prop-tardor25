package domain.algoritmo;

import java.util.List;
import domain.*; 

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

    // Nota: Los métodos 'cambiarX' ya no son necesarios porque el vector es final.
    // El centroide se construye una vez en el constructor.
}

public class TransformarEnCentroide {
    public static Centroide transformarEnCentroide(List<Respuesta> respuesta) {
        List<ComponenteCentroide> centroide = new ArrayList<>();

        for (Respuesta r: respuesta) {
            if (r instanceof RespuestaNumerica) {
                double valor = r.getRespuesta();

                centroide.add(new ComponenteNumerico(valor));
            }

            else if (r instanceof RespuestaOpcionUnica) {
                int indice = r.getRespuesta();

                centroide.add(new ComponenteUnico(indice));
            }

            else if (r instanceof RespuestaOpcionMultiple) {
                List<Integer> indices = r.getRespuesta();

                centroide.add(new ComponenteMultiple(indices));
            }

            else if (r instanceof RespuestaLibre) {
                String text = r.getRespuesta();

                centroide.add(new ComponenteLibre(text));
            }
        }

        return Centroide(centroide);
    }
}

// Superclase para todos los valores prototipo que componen el centroide.
public abstract class ComponenteCentroide {
    // La clase base que permite almacenar cualquier tipo de valor calculado en una lista.
    // Es la contraparte conceptual de la clase 'Respuesta'.
}

public class ComponenteNumerico extends ComponenteCentroide {
    private final double valor;
    
    public ComponenteNumerico(double valor) {
        this.valor = valor;
    }
    public double getValor() {
        return valor;
    }
}

public class ComponenteUnico extends ComponenteCentroide {
    private final int indice;
    
    public ComponenteUnico(int indice) {
        this.indice = indice;
    }
    public int getIndice() {
        return indice;
    }
}


public class ComponenteMultiple extends ComponenteCentroide {
    private final List<Integer> indices;
    
    public ComponenteMultiple(List<Integer> indices) {
        this.indices = indices;
    }
    public List<Integer> getIndices() {
        return indices;
    }
}


public class ComponenteLibre extends ComponenteCentroide {
    private final String texto;
    
    public ComponenteLibre(String texto) {
        this.texto = texto;
    }
    public String getTexto() {
        return texto;
    }
}

