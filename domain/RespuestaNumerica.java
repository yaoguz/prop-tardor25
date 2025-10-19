package domain;

/**
 * Respuesta de tipo numérico.
 * 
 * Permite al usuario proporcionar una respuesta en formato numérico (double).
 * Corresponde a preguntas de tipo PreguntaNumerica, que pueden tener rangos
 * mínimo y máximo opcionales.
 * 
 * Ejemplos:
 * - "¿Cuál es tu edad?" → 25
 * - "¿Cuántas horas estudias al día?" → 3.5
 * - "Valoración del 1 al 10:" → 8.5
 * 
 * NOTA: La validación de rangos la realiza PreguntaNumerica. Esta clase
 * solo almacena y gestiona el valor numérico.
 * 
 * @author subgrup-prop11.5
 * @version 2.0
 */
public class RespuestaNumerica extends Respuesta {
    
    /** Atributos **/

    private double valor;

    /** Constructora **/

    /**
     * Crea una nueva respuesta numérica.
     * 
     * @param id Identificador único de la respuesta
     * @param nomUsuario Nombre del usuario que responde
     * @param idPregunta ID de la pregunta respondida
     * @param valor Valor numérico de la respuesta
     * @throws IllegalArgumentException si el valor no es un número válido (NaN o Infinito)
     */
    public RespuestaNumerica(int id, String nomUsuario, int idPregunta, double valor) {
        super(id, nomUsuario, idPregunta);
        
        if (Double.isNaN(valor)) {
            throw new IllegalArgumentException("El valor no puede ser NaN (Not a Number)");
        }
        if (Double.isInfinite(valor)) {
            throw new IllegalArgumentException("El valor no puede ser infinito");
        }
        
        this.valor = valor;
    }

    /** Métodos públicos **/
    
    /**
     * Obtiene el valor numérico de la respuesta.
     * 
     * @return Valor numérico (double)
     */
    @Override
    public Double getRespuesta() {
        return valor;
    }
    
    /**
     * Obtiene el tipo de esta respuesta.
     * 
     * @return "numerica"
     */
    @Override
    public String getTipo() {
        return "numerica";
    }
    
    /**
     * Modifica el valor numérico de la respuesta.
     * 
     * @param nuevaRespuesta Nuevo valor (debe ser Number: Integer, Double, Float, etc.)
     * @throws IllegalArgumentException si nuevaRespuesta no es Number o es NaN/Infinito
     */
    @Override
    public void modificar(Object nuevaRespuesta) {
        if (!(nuevaRespuesta instanceof Number)) {
            throw new IllegalArgumentException("Tipo incorrecto: se esperaba Number para respuesta numérica");
        }
        
        double nuevoValor = ((Number) nuevaRespuesta).doubleValue();
        
        if (Double.isNaN(nuevoValor)) {
            throw new IllegalArgumentException("El valor no puede ser NaN (Not a Number)");
        }
        if (Double.isInfinite(nuevoValor)) {
            throw new IllegalArgumentException("El valor no puede ser infinito");
        }
        
        this.valor = nuevoValor;
    }
    
    /**
     * Obtiene el valor como entero (truncando decimales).
     * 
     * @return Valor como int
     */
    public int getValorEntero() {
        return (int) valor;
    }
    
    /**
     * Verifica si el valor es un número entero.
     * 
     * @return true si no tiene decimales, false en caso contrario
     */
    public boolean esEntero() {
        return valor == Math.floor(valor);
    }
    
    /**
     * Representación en String de la respuesta numérica.
     * 
     * @return Descripción con el valor numérico
     */
    @Override
    public String toString() {
        String valorStr = esEntero() ? String.valueOf(getValorEntero()) : String.valueOf(valor);
        return String.format("RespuestaNumerica[id=%d, usuario=%s, valor=%s]", 
                           getId(), getNomUsuario(), valorStr);
    }
}