
package classcontroller;

import java.util.HashMap;
import java.util.Map;
import domain.Encuesta;

/**
 * Implementación de la classe gestionadora de datos para la clase "Encuesta".
 **/
public class CtrlEncuesta {

	/**
	 * Implementación del patrón de diseño "Singleton", con el objetivo de que
	 * haya una única instancia de esta clase en el sistema. En este caso, esta
	 * propiedad es deseada ya que esta classe no tiene estado (atributos no
	 * estáticos). Para lograrlo, declaramos la constructora como privada y
	 * añadimos una operación estática que retorne siempre la misma instancia.
	 * Para acceder a esta instancia lo haremos mediante la llamada
	 * CtrlEncuesta.getInstance();
	 **/
	
	private static CtrlEncuesta singletonObject;
	private Map<Integer,Encuesta> encuestas;
	private int nextId;

	public static CtrlEncuesta getInstance() {
		if (singletonObject == null)
			singletonObject = new CtrlEncuesta() {
			};
		return singletonObject;
	}

	/**
	 * Constructora privada, el nextId se inicializa a 0 y se crea 
	 * un HashMap de las encuestas
	 */
	private CtrlEncuesta() {
		nextId = 0;
		encuestas = new HashMap<>();
	}

	/**
	 * Se crea una nueva encuesta que se le asigna el nextId como idEncuesta,
	 * se añade al Map de encuestas y se retorna el idEncuesta asignado
	 * @param nomUsuario nombre del usuario creador de la encuesta
	 * @param titulo el titulo de la encuesta
	 * @return idEncuesta de esta nueva encuesta creada
	 * @throws IllegalArgumentException si el titulo esta vacio
	 */
	public int crearEncuesta(String nomUsuario, String titulo) {
		try {
			
			if (titulo == null) {
				throw new IllegalArgumentException("Nombre de encuesta vacio");
			} 
			int idEncuesta = nextId;
			nextId++;
			Encuesta e = new Encuesta(idEncuesta, titulo, nomUsuario);
			encuestas.put(idEncuesta,e);
			return idEncuesta;

		}
		catch (IllegalArgumentException e) {
			System.out.println("Error al crear encuesta: " + e.getMessage());
		}
	}

	/**
	 * Se obtiene la instancia de Encuesta con el id "idEncuesta"
	 * @param idEncuesta id de la encuesta de la que se quiere obtener la instancia
	 * @return instancia e de Encuesta con id idEncuesta
	 * @throws IllegalArgumentException si no existe una instancia de encuesta con id idEncuesta
	 */
	public Encuesta getEncuesta(int idEncuesta) {
		try {
			Encuesta e = encuestas.get(idEncuesta);
			if (e == null) {
				throw new IllegalArgumentException("No existe encuesta con id" + idEncuesta);
			}
			return e;
		}
		catch (IllegalArgumentException ex) {
			System.out.println("Error al obtener encuesta: " + ex.getMessage());
		}
	}

	/**
	 * Se elimina del mapa la encuesta e, es decir, se elimina una encuesta
	 * @param e instancia de Encuesta que se quiere eliminar 
	 */
	public void eliminarEncuesta(Encuesta e) {
		encuestas.remove(e.getIdEncuesta);
	}

}
