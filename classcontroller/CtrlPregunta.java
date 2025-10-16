
package classcontroller;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

import domain.Encuesta;
import domain.Pregunta;

/**
 * Implementación de la classe gestionadora de datos para la clase "Pregunta".
En una implementaci�n completa tendr�a que haber tambi�n un "CtrlAlumnosFichero" que cargara/volcara los alumnos modificados en cada sesi�n.
 **/
public class CtrlPregunta {

	/**
	 * Implementación del patrón de diseño "Singleton", con el objetivo de que
	 * haya una única instancia de esta clase en el sistema. En este caso, esta
	 * propiedad es deseada ya que esta classe no tiene estado (astributos no
	 * estáticos). Para lograrlo, declaramos la constructora como privada y
	 * añadimos una operación estática que retorne siempre la misma instancia.
	 * Para acceder a esta instancia lo haremos mediante la llamada
	 * CtrlAsignaturaFicher.getInstance();
	 **/

	private static CtrlPregunta singletonObject;

	public static CtrlPregunta getInstance() {
		if (singletonObject == null)
			singletonObject = new CtrlPregunta() {
			};
		return singletonObject;
	}

	/** Constructora privada. **/
	
	/*private CtrlAsignaturaFichero() {
	}

	public List<String> getAll(String filename)
			throws FileNotFoundException {
		LinkedList<String> asignaturas = new LinkedList<String>();

		FileReader fr = new FileReader("../DATA/"+filename);
		Scanner scan = new Scanner(fr);

		while (scan.hasNextLine())
			asignaturas.add(new String(scan.nextLine()));

		return asignaturas;
	}*/

	public int crearPregunta(Encuesta e, String enunciado) {
		int id = conjuntoPreguntas.newId();
		Pregunta p = new Pregunta(e, id, enunciado);
		conjuntoPregunta.add(id, p);
		return id;
	}
}
>>>>>>> 5b9b7c8be6d8b925c348fe73ac750ce71ff56458:classcontroller/CtrlPregunta.java
