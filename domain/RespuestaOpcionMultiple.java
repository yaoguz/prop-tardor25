package domain;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Respuesta de opción múltiple (multiple choice).
 * 
 * Permite al usuario seleccionar MÚLTIPLES opciones de entre varias disponibles.
 * Los valores almacenados son los índices de las opciones seleccionadas (basado en 0).
 * Corresponde a preguntas de tipo PreguntaOpcionMultiple.
 * 
 * Ejemplos:
 * - "¿Qué idiomas hablas? [Español, Inglés, Francés, Alemán]" → [0, 1] (Español, Inglés)
 * - "Intereses: [Deportes, Música, Lectura, Cine]" → [1, 2, 3] (Música, Lectura, Cine)
 * 
 * DISEÑO: Se usa array de índices (int[]) en lugar de List<String> para:
 * - Menor uso de memoria
 * - Facilitar análisis estadístico
 * - Validación más eficiente
 * - Inmutabilidad controlada
 * 
 * INVARIANTE: No puede haber índices duplicados en el array.
 * 
 * @author subgrup-prop11.5
 * @version 2.0
 */
public class RespuestaOpcionMultiple extends Respuesta {
    
    /** Atributos **/

    private int[] indicesOpciones;

    /** Constructora **/

    /**
     * Crea una nueva respuesta de opción múltiple.
     * 
     * @param id Identificador único de la respuesta
     * @param nomUsuario Nombre del usuario que responde
     * @param idPregunta ID de la pregunta respondida
     * @param indicesOpciones Array con los índices de las opciones seleccionadas (0-based)
     * @throws IllegalArgumentException si el array es nulo, vacío, contiene negativos o duplicados
     */
    public RespuestaOpcionMultiple(int id, String nomUsuario, int idPregunta, int[] indicesOpciones) {
        super(id, nomUsuario, idPregunta);
        
        if (indicesOpciones == null) {
            throw new IllegalArgumentException("El array de índices no puede ser nulo");
        }
        if (indicesOpciones.length == 0) {
            throw new IllegalArgumentException("Debe seleccionar al menos una opción");
        }
        
        // Validar que no hay índices negativos
        for (int indice : indicesOpciones) {
            if (indice < 0) {
                throw new IllegalArgumentException("Los índices de opciones no pueden ser negativos");
            }
        }
        
        // Validar que no hay duplicados
        if (tieneDuplicados(indicesOpciones)) {
            throw new IllegalArgumentException("No puede haber opciones duplicadas");
        }
        
        // Copiar array para inmutabilidad (defensive copy)
        this.indicesOpciones = Arrays.copyOf(indicesOpciones, indicesOpciones.length);
        // Ordenar para facilitar comparaciones y búsquedas
        Arrays.sort(this.indicesOpciones);
    }

    /** Métodos públicos **/
    
    /**
     * Obtiene una copia del array de índices de las opciones seleccionadas.
     * 
     * @return Array de índices (copia defensiva)
     */
    @Override
    public int[] getRespuesta() {
        return Arrays.copyOf(indicesOpciones, indicesOpciones.length);
    }
    
    /**
     * Obtiene el tipo de esta respuesta.
     * 
     * @return "opcion_multiple"
     */
    @Override
    public String getTipo() {
        return "opcion_multiple";
    }
    
    /**
     * Modifica las opciones seleccionadas.
     * 
     * @param nuevaRespuesta Nuevo array de índices (debe ser int[])
     * @throws IllegalArgumentException si nuevaRespuesta no es int[], está vacío, 
     *         contiene negativos o duplicados
     */
    @Override
    public void modificar(Object nuevaRespuesta) {
        if (!(nuevaRespuesta instanceof int[])) {
            throw new IllegalArgumentException("Tipo incorrecto: se esperaba int[] para respuesta de opción múltiple");
        }
        
        int[] nuevosIndices = (int[]) nuevaRespuesta;
        
        if (nuevosIndices == null) {
            throw new IllegalArgumentException("El array de índices no puede ser nulo");
        }
        if (nuevosIndices.length == 0) {
            throw new IllegalArgumentException("Debe seleccionar al menos una opción");
        }
        
        // Validar que no hay índices negativos
        for (int indice : nuevosIndices) {
            if (indice < 0) {
                throw new IllegalArgumentException("Los índices de opciones no pueden ser negativos");
            }
        }
        
        // Validar que no hay duplicados
        if (tieneDuplicados(nuevosIndices)) {
            throw new IllegalArgumentException("No puede haber opciones duplicadas");
        }
        
        // Actualizar con copia defensiva
        this.indicesOpciones = Arrays.copyOf(nuevosIndices, nuevosIndices.length);
        Arrays.sort(this.indicesOpciones);
    }
    
    /**
     * Obtiene el número de opciones seleccionadas.
     * 
     * @return Cantidad de opciones seleccionadas
     */
    public int getNumeroOpcionesSeleccionadas() {
        return indicesOpciones.length;
    }
    
    /**
     * Verifica si una opción específica está seleccionada.
     * 
     * @param indice Índice de la opción a verificar
     * @return true si la opción está seleccionada, false en caso contrario
     */
    public boolean contieneOpcion(int indice) {
        return Arrays.binarySearch(indicesOpciones, indice) >= 0;
    }
    
    /**
     * Obtiene un Set con los índices seleccionados (útil para comparaciones).
     * 
     * @return Set de índices
     */
    public Set<Integer> getOpcionesComoSet() {
        Set<Integer> set = new HashSet<>();
        for (int indice : indicesOpciones) {
            set.add(indice);
        }
        return set;
    }
    
    /** Métodos privados auxiliares **/
    
    /**
     * Verifica si un array contiene elementos duplicados.
     * 
     * @param array Array a verificar
     * @return true si hay duplicados, false en caso contrario
     */
    private boolean tieneDuplicados(int[] array) {
        Set<Integer> set = new HashSet<>();
        for (int valor : array) {
            if (!set.add(valor)) {
                return true; // Ya existía, es duplicado
            }
        }
        return false;
    }
    
    /**
     * Representación en String de la respuesta de opción múltiple.
     * 
     * @return Descripción con los índices de las opciones seleccionadas
     */
    @Override
    public String toString() {
        return String.format("RespuestaOpcionMultiple[id=%d, usuario=%s, opcionesSeleccionadas=%s]", 
                           getId(), getNomUsuario(), Arrays.toString(indicesOpciones));
    }
    
    /**
     * Compara esta respuesta con otra para verificar igualdad de contenido.
     * Dos respuestas de opción múltiple son equivalentes si tienen los mismos
     * índices seleccionados (independientemente del orden).
     * 
     * @param obj Objeto a comparar
     * @return true si las respuestas tienen las mismas opciones seleccionadas
     */
    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (!(obj instanceof RespuestaOpcionMultiple)) return false;
        RespuestaOpcionMultiple other = (RespuestaOpcionMultiple) obj;
        return Arrays.equals(this.indicesOpciones, other.indicesOpciones);
    }
    
    /**
     * Calcula el código hash basado en el ID y las opciones seleccionadas.
     * 
     * @return Código hash
     */
    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + Arrays.hashCode(indicesOpciones);
        return result;
    }
}