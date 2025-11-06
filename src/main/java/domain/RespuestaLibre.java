package domain;

/**
 * Respuesta de tipo texto libre.
 * 
 * Permite al usuario proporcionar una respuesta en formato de texto sin restricciones
 * (salvo longitud máxima opcional). Corresponde a preguntas de tipo PreguntaLibre.
 * 
 * Ejemplos:
 * - "¿Qué te pareció el curso?" → "El curso fue muy interesante y útil..."
 * - "Comentarios adicionales:" → "Ninguno"
 * 
 * @author Yaoyao Guo
 * @version 2.0
 */
public class RespuestaLibre extends Respuesta {
    
    /** Atributos **/

    private String texto;
    private static final int MAX_LENGTH = 5000; // Longitud máxima del texto

    /** Constructora **/

    /**
     * Crea una nueva respuesta de texto libre.
     * 
     * @param id Identificador único de la respuesta
     * @param nomUsuario Nombre del usuario que responde
     * @param idPregunta ID de la pregunta respondida
     * @param texto Contenido de la respuesta en texto libre
     * @throws IllegalArgumentException si el texto es nulo, vacío o excede la longitud máxima
     */
    public RespuestaLibre(int id, String nomUsuario, int idPregunta, String texto) {
        super(id, nomUsuario, idPregunta);
        
        if (texto == null) {
            throw new IllegalArgumentException("El texto de la respuesta no puede ser nulo");
        }
        if (texto.trim().isEmpty()) {
            throw new IllegalArgumentException("El texto de la respuesta no puede estar vacío");
        }
        if (texto.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("El texto excede la longitud máxima de " + 
                                             MAX_LENGTH + " caracteres");
        }
        
        this.texto = texto;
    }

    /** Métodos públicos **/
    
    /**
     * Obtiene el contenido de texto de la respuesta.
     * 
     * @return Texto de la respuesta
     */
    @Override
    public String getRespuesta() {
        return texto;
    }
    
    /**
     * Obtiene el tipo de esta respuesta.
     * 
     * @return "libre"
     */
    @Override
    public String getTipo() {
        return "libre";
    }
    
    /**
     * Modifica el contenido de la respuesta.
     * 
     * @param nuevaRespuesta Nuevo texto (debe ser de tipo String)
     * @throws IllegalArgumentException si nuevaRespuesta no es String, está vacía o excede longitud
     */
    @Override
    public void modificar(Object nuevaRespuesta) {
        if (!(nuevaRespuesta instanceof String)) {
            throw new IllegalArgumentException("Tipo incorrecto: se esperaba String para respuesta libre");
        }
        
        String nuevoTexto = (String) nuevaRespuesta;
        
        if (nuevoTexto.trim().isEmpty()) {
            throw new IllegalArgumentException("El texto de la respuesta no puede estar vacío");
        }
        if (nuevoTexto.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("El texto excede la longitud máxima de " + 
                                             MAX_LENGTH + " caracteres");
        }
        
        this.texto = nuevoTexto;
    }
    
    /**
     * Obtiene la longitud máxima permitida para el texto.
     * 
     * @return Longitud máxima en caracteres
     */
    public static int getMaxLength() {
        return MAX_LENGTH;
    }
    
    /**
     * Obtiene la longitud actual del texto.
     * 
     * @return Número de caracteres del texto
     */
    public int getLongitud() {
        return texto.length();
    }
    
    /**
     * Representación en String de la respuesta libre.
     * 
     * @return Descripción con los primeros 50 caracteres del texto
     */
    @Override
    public String toString() {
        String preview = texto.length() > 50 ? texto.substring(0, 47) + "..." : texto;
        return String.format("RespuestaLibre[id=%d, usuario=%s, texto=\"%s\"]", 
                           getId(), getNomUsuario(), preview);
    }
}