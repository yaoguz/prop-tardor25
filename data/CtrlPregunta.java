package data;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

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

	public Int crearPregunta(Encuesta e, String enunciado) {
		Int id = conjuntoPreguntas.newId();
		Pregunta p = new Pregunta(e, id, enunciado);
		conjuntoPregunta.add(id, p);
		return id;
	}
}
