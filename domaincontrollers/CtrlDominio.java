package domaincontrollers;

/*import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import data.CtrlAsignaturaFichero;
import domain.Alumno;
import domain.Asignatura;*/

import java.util.Map;
import java.util.HashMap;

import domain.Encuesta;
import domain.Pregunta;
import domain.Usuario;
import domain.Respuesta;
import data.ConjuntoObjetos;

/** Ejemplo de Controlador de Dominio. **/
public class CtrlDominio {

	/** Atributos **/
	
	/*private CtrlAsignaturaFichero controladorAsignaturaFichero;
	private Map<String, Asignatura> asignaturas;
	private Map<String, Alumno> alumnos;
	private String alumnoSeleccionado;*/
	private CtrlEncuestaFichero controladorEncuestaFichero;
	private CtrlUsuarioFichero controladorUsuarioFichero;
	private CtrlPreguntaFichero controladorPreguntaFichero;
	private CtrlRespuestaFichero controladorRespuetaFichero;

	private ConjuntoObjetos<Usuario> conjuntoUsuarios;
	private ConjuntoObjetos<Encuesta> conjuntoEncuestas;
	private ConjuntoObjetos<Pregunta> conjuntoPreguntas;
	private ConjuntoObjetos<Respuesta> conjuntoRespuesta;

	
	/** Constructor y metodos de inicializacion **/

	public CtrlDominio() {
		inicializarCtrlDominio();
	}

	public void inicializarCtrlDominio() {
		/*controladorAsignaturaFichero = CtrlAsignaturaFichero.getInstance();
		asignaturas = new HashMap<String, Asignatura>();
		alumnos = new HashMap<String, Alumno>();
		alumnoSeleccionado = null;*/
		controladorEncuestaFichero = CtrlEncuestaFichero.getInstance();
		controladorPreguntaFichero = CtrlPreguntaFichero.getInstance();
		controladorRespuetaFichero = CtrlRespuestaFichero.getInstance();
		controladorUsuarioFichero = CtrlUsuarioFichero.getInstance();

		encuestas = new HashMap<Integer,Encuesta> ();
		usuarios = new HashMap<Integer, Usuario> ();
		preguntas = new HashMap<Integer, Preguntas> ();
		respuestas = new HashMap<Integer, Respuesta> ();
	}

	/**
	 * Funciones que se llaman desde el controlador de presentacion. Por
	 * convención, únicamente se usan Strings para la comunicación entre las dos
	 * capas.
	 **/

	

	/*public ArrayList<String> cargarAsignaturas(String filename)
			throws FileNotFoundException {
		List<String> asignaturasdata = controladorAsignaturaFichero
				.getAll(filename + ".txt");
		LinkedList<Asignatura> asignaturas = new LinkedList<Asignatura>();
		for (String asignaturadata : asignaturasdata)
			asignaturas.add(new Asignatura(asignaturadata));
		ArrayList<String> lista = new ArrayList<String>();
		for (Asignatura asignatura : asignaturas) {
			lista.add(asignatura.toString());
			this.asignaturas.put(asignatura.toString(), asignatura);
		}
		Collections.sort(lista);
		return lista;
	}

	public boolean crearAlumno(String nombreAlumno) {
		if (alumnos.containsKey(nombreAlumno))
			return false;
		alumnos.put(nombreAlumno, new Alumno(nombreAlumno));
		return true;
	}
     
     // por consistencia se elimina tb de la asignatura
	public void eliminarAlumno(String nombreAlumno) {
		if (nombreAlumno.equals(alumnoSeleccionado))
			alumnoSeleccionado = null;
		alumnos.remove(nombreAlumno);
           for (String asignatura : asignaturas.keySet()) {
                Alumno alumno1= alumnos.get(nombreAlumno);
		 asignaturas.get(asignatura).eliminarAlumno(alumno1);
		}

	}

	public void asignarNota(String nombreAlumno, String nombreAsignatura,
			String nota) {
		alumnos.get(nombreAlumno).anadirAsignatura(
				asignaturas.get(nombreAsignatura), Double.parseDouble(nota));
	}

	public String calcularMedia(String nombreAlumno) {
		return alumnos.get(nombreAlumno).calcularMedia() + "";
	}

	public String[] getAllAlumnos() {
		Collection<Alumno> alumnos = this.alumnos.values();
		String[] result = new String[alumnos.size()];
		int i = 0;
		for (Alumno alumno : alumnos)
			result[i++] = alumno.toString();
		return result;
	}

	public String getNota(String nombreAlumno, String nombreAsignatura) {
		return alumnos.get(nombreAlumno).getNota(nombreAsignatura) + "";
	}

	public String getAlumnoSeleccionado() {
		return alumnoSeleccionado;
	}

	public void setAlumnoSeleccionado(String alumno) {
		alumnoSeleccionado = alumno;
	}*/
}
