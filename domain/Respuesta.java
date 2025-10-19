package domain;

/**
 * Clase abstracta que representa una respuesta a una pregunta de una encuesta.
 * 
 * DISEÑO: Jerarquía polimórfica con validación de tipos en runtime.
 * Cada subclase corresponde a un tipo específico de pregunta (Libre, Numérica, 
 * OpciónÚnica, OpciónMúltiple).
 * 
 * RESPONSABILIDADES:
 * - Almacenar el contenido de la respuesta
 * - Validar que el tipo de respuesta coincida con el tipo de pregunta
 * - Permitir modificación con validación de tipo
 * - Proporcionar acceso a usuario y pregunta asociados
 * 
 * @author subgrup-prop11.5
 * @version 2.0
 */
public abstract class Respuesta {

    /** Atributos **/
    
    private int id;
    private String nomUsuario;
    private int idPregunta;

    /** Constructora **/

    /**
     * Constructora base para todas las respuestas.
     * 
     * @param id Identificador único de la respuesta
     * @param nomUsuario Nombre del usuario que proporciona la respuesta
     * @param idPregunta Identificador de la pregunta respondida
     * @throws IllegalArgumentException si algún parámetro es inválido
     */
    public Respuesta(int id, String nomUsuario, int idPregunta) {
        if (nomUsuario == null || nomUsuario.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de usuario no puede estar vacío");
        }
        if (id < 0) {
            throw new IllegalArgumentException("El ID de respuesta no puede ser negativo");
        }
        if (idPregunta < 0) {
            throw new IllegalArgumentException("El ID de pregunta no puede ser negativo");
        }
        
        this.id = id;
        this.nomUsuario = nomUsuario;
        this.idPregunta = idPregunta;
    }

    /** Métodos públicos - Getters **/
    
    /**
     * Obtiene el ID único de esta respuesta.
     * 
     * @return ID de la respuesta
     */
    public int getId() {
        return id;
    }
    
    /**
     * Obtiene el nombre del usuario que proporcionó esta respuesta.
     * 
     * @return Nombre del usuario
     */
    public String getNomUsuario() {
        return nomUsuario;
    }
    
    /**
     * Obtiene el ID de la pregunta a la que pertenece esta respuesta.
     * 
     * @return ID de la pregunta
     */
    public int getIdPregunta() {
        return idPregunta;
    }

    /** Métodos abstractos - Deben ser implementados por subclases **/
    
    /**
     * Obtiene el contenido de la respuesta.
     * El tipo de retorno depende de la subclase:
     * - RespuestaLibre: String
     * - RespuestaNumerica: Double
     * - RespuestaOpcionUnica: Integer (índice de la opción)
     * - RespuestaOpcionMultiple: int[] (índices de las opciones)
     * 
     * @return Contenido de la respuesta
     */
    public abstract Object getRespuesta();

    /**
     * Obtiene el tipo de esta respuesta.
     * 
     * @return Tipo de respuesta: "libre", "numerica", "opcion_unica", "opcion_multiple"
     */
    public abstract String getTipo();
    
    /**
     * Modifica el contenido de la respuesta.
     * 
     * @param nuevaRespuesta Nuevo contenido de la respuesta
     * @throws IllegalArgumentException si el tipo de nuevaRespuesta no es compatible
     */
    public abstract void modificar(Object nuevaRespuesta);
    
    /** Métodos de comparación y representación **/
    
    /**
     * Compara esta respuesta con otro objeto.
     * Dos respuestas son iguales si tienen el mismo ID.
     * 
     * @param obj Objeto a comparar
     * @return true si son iguales, false en caso contrario
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Respuesta other = (Respuesta) obj;
        return id == other.id;
    }
    
    /**
     * Calcula el código hash de esta respuesta.
     * 
     * @return Código hash basado en el ID
     */
    @Override
    public int hashCode() {
        return Integer.hashCode(id);
    }
    
    /**
     * Representación en String de la respuesta.
     * 
     * @return Descripción de la respuesta
     */
    @Override
    public String toString() {
        return String.format("Respuesta[id=%d, usuario=%s, pregunta=%d, tipo=%s]", 
                           id, nomUsuario, idPregunta, getTipo());
    }
}