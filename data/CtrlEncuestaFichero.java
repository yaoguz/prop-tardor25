package data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * Implementación de la classe gestionadora de datos para la clase "Asignatura".
En una implementaci�n completa tendr�a que haber tambi�n un "CtrlAlumnosFichero" que cargara/volcara los alumnos modificados en cada sesi�n.
 **/
public class CtrlEncuestaFichero {

	/**
	 * Implementación del patrón de diseño "Singleton", con el objetivo de que
	 * haya una única instancia de esta clase en el sistema. En este caso, esta
	 * propiedad es deseada ya que esta classe no tiene estado (astributos no
	 * estáticos). Para lograrlo, declaramos la constructora como privada y
	 * añadimos una operación estática que retorne siempre la misma instancia.
	 * Para acceder a esta instancia lo haremos mediante la llamada
	 * CtrlAsignaturaFicher.getInstance();
	 **/
	
	private static CtrlEncuestaFichero singletonObject;

	public static CtrlEncuestaFichero getInstance() {
		if (singletonObject == null)
			singletonObject = new CtrlEncuestaFichero() {
			};
		return singletonObject;
	}

	/** Constructora privada. **/
	
	private CtrlEncuestaFichero() {
	}

	/*public List<String> getAll(String filename)
			throws FileNotFoundException {
		LinkedList<String> asignaturas = new LinkedList<String>();

		FileReader fr = new FileReader("../DATA/"+filename);
		Scanner scan = new Scanner(fr);

		while (scan.hasNextLine())
			asignaturas.add(new String(scan.nextLine()));

		return asignaturas;
	}*/
}
