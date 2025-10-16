package persistence;

import java.util.Map;
import java.util.HashMap;

/**
 * Controlador de datos del sistema
 */

public class CtrlPersistence {
    /** Atributos **/
    /**
     * Mapa que almacena los usuarios existentes del sistema.
     * <p>
     * La clave es el nombre de usuario y el valor es el objeto {@link domain.Usuario}.
     */
    private Map<String,Usuario> usuarios = new HashMap(); 

    /**
     * Mapa que almacena las encuestas existentes del sistema.
     * <p>
     * La clave es un Integer id y el valor es el objeto {@link domain.Encuesta}.
     */
    private Map<Integer,Encuesta> encuestas = new HashMap();

    /**
     * Mapa que almacena las preguntas de todas las encuestas del sistema
     * <p>
     * La clave es un Integer id y el valor es el objeto {@link domain.Pregunta}.
     */
    private Map<Integer,Pregunta> preguntas = new HashMap();

    /**
     * Mapa que almacena las respuestas de todas las preguntas del sistema
     * <p>
     * La clave es un Integer id y el valor es el objeto {@link domain.Respuesta}.
     */
    private Map<Integer,Respuesta> respuestas = new HashMap();


    /** Constructor y metodos de inicializacion **/

	public CtrlPersistence() {
		inicializarCtrlPersistence();
	}

	public void inicializarCtrlPersistence() {

    }

    /**
	 * Funciones que se llaman desde el controlador de Dominio. 
	 * <p>
     * Gestión de datos del sistema.
	 **/

     
}