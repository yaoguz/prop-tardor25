
package domaincontrollers;

import domain.Encuesta;
import domain.Usuario;
import java.util.HashMap;
import java.util.Map;

/**
 * Implementación de la classe gestionadora de datos para la clase "Asignatura".
En una implementaci�n completa tendr�a que haber tambi�n un "CtrlAlumnosFichero" que cargara/volcara los alumnos modificados en cada sesi�n.
 **/
public class CtrlRespuesta {

	/**
	 * Implementación del patrón de diseño "Singleton", con el objetivo de que
	 * haya una única instancia de esta clase en el sistema. En este caso, esta
	 * propiedad es deseada ya que esta classe no tiene estado (astributos no
	 * estáticos). Para lograrlo, declaramos la constructora como privada y
	 * añadimos una operación estática que retorne siempre la misma instancia.
	 * Para acceder a esta instancia lo haremos mediante la llamada
	 * CtrlAsignaturaFicher.getInstance();
	 **/
	
	private static CtrlRespuesta singletonObject;
	private Map<Integer, Encuesta> encuestas;
    private int nextId;
    
    private CtrlEncuesta() {
        encuestas = new HashMap<>();
        nextId = 1;
    }

	public static CtrlRespuesta getInstance() {
		if (singletonObject == null)
			singletonObject = new CtrlRespuesta() {
			};
		return singletonObject;
	}

	/**
     * Crea una nueva encuesta.
     * @return ID de la encuesta creada
     */
    public int crearEncuesta(String nomUsuario, String titulo) {
        int id = nextId++;
        Encuesta e = new Encuesta(id, titulo, nomUsuario);
        encuestas.put(id, e);
        return id;
    }
    
    /**
     * Obtiene una encuesta por su ID.
     */
    public Encuesta getEncuesta(int idEncuesta) {
        Encuesta e = encuestas.get(idEncuesta);
        if (e == null) {
            throw new IllegalArgumentException("Encuesta con ID " + idEncuesta + " no existe");
        }
        return e;
    }
    
    /**
     * Verifica si existe una encuesta.
     */
    public boolean existeEncuesta(int idEncuesta) {
        return encuestas.containsKey(idEncuesta);
    }
    
    /**
     * Elimina una encuesta.
     */
    public void eliminar(Encuesta e) {
        encuestas.remove(e.getIdEncuesta());
    }
    
    /**
     * Obtiene todas las encuestas.
     */
    public Map<Integer, Encuesta> getTodasEncuestas() {
        return new HashMap<>(encuestas);
    }
}

