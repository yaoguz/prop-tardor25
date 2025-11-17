package domain;

/**
 * Respuesta de opción única (single choice).
 * 
 * Permite al usuario seleccionar UNA opción de entre varias disponibles.
 * El valor almacenado es el índice de la opción seleccionada (basado en 0).
 * Corresponde a preguntas de tipo PreguntaOpcionUnica.
 * 
 * @author Yaoyao Guo Zhang (yaoyao.guo@estudiantat.upc.edu)
 */
public class RespuestaOpcionUnica extends Respuesta {
    
    /** Atributos **/

    private int indiceOpcion;

    /**
     * Crea una nueva respuesta de opción única.
     * 
     * @param id Identificador único de la respuesta
     * @param nomUsuario Nombre del usuario que responde
     * @param idPregunta ID de la pregunta respondida
     * @param indiceOpcion Índice de la opción seleccionada (0-based)
     * @throws IllegalArgumentException si el índice es negativo
     */
    public RespuestaOpcionUnica(int id, String nomUsuario, int idPregunta, int indiceOpcion) {
        super(id, nomUsuario, idPregunta);
        
        if (indiceOpcion < 0) {
            throw new IllegalArgumentException("El índice de la opción no puede ser negativo");
        }
        
        this.indiceOpcion = indiceOpcion;
    }
    
    /**
     * Obtiene el índice de la opción seleccionada.
     * 
     * @return Índice de la opción (0-based)
     */
    @Override
    public Integer getRespuesta() {
        return indiceOpcion;
    }
    
    /**
     * Obtiene el tipo de esta respuesta.
     * 
     * @return "opcion_unica"
     */
    @Override
    public String getTipo() {
        return "opcion_unica";
    }
    
    /**
     * Modifica la opción seleccionada.
     * 
     * @param nuevaRespuesta Nuevo índice (debe ser Integer o Number)
     * @throws IllegalArgumentException si nuevaRespuesta no es Number o es negativo
     */
    @Override
    public void modificar(Object nuevaRespuesta) {
        if (!(nuevaRespuesta instanceof Number)) {
            throw new IllegalArgumentException("Tipo incorrecto: se esperaba Number (índice) para respuesta de opción única");
        }
        
        int nuevoIndice = ((Number) nuevaRespuesta).intValue();
        
        if (nuevoIndice < 0) {
            throw new IllegalArgumentException("El índice de la opción no puede ser negativo");
        }
        
        this.indiceOpcion = nuevoIndice;
    }
    
    /**
     * Verifica si se ha seleccionado una opción específica.
     * 
     * @param indice Índice a comparar
     * @return true si el índice coincide con la opción seleccionada
     */
    public boolean esOpcion(int indice) {
        return this.indiceOpcion == indice;
    }
    
}