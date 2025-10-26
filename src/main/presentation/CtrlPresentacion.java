package presentation;

// import java.io.FileNotFoundException;
// import java.util.ArrayList;

import domaincontrollers.CtrlDominio;

public class CtrlPresentacion {

    // ejemplo

	/** Atributos **/

	private CtrlDominio controladorDominio;

	/*private VistaPrincipal vistaPrincipal;
	private VistaSecundaria vistaSecundaria;
	private VistaLEEME vistaLEEME;*/

	/** Constructor y metodos de inicializacion **/

	public CtrlPresentacion() {
		controladorDominio = new CtrlDominio();
		/*vistaPrincipal = new VistaPrincipal(this);
		vistaSecundaria = new VistaSecundaria(this);
		vistaLEEME = new VistaLEEME();*/
	}

	public void inicializarPresentacion() {
		controladorDominio.inicializarCtrlDominio();

		/*vistaLEEME.hacerVisible();
		vistaPrincipal
				.hacerVisible(controladorDominio.getAlumnoSeleccionado() != null);*/
	}

	/** Métodos de sincronizacion entre vistas **/

	public void sincronizacionVistaPrincipalASecundaria() {
		//vistaPrincipal.desactivar();
		//vistaSecundaria.hacerVisible();
	}

	public void sincronizacionVistaSecundariaAPrincipal() {
		// Se hace invisible la vista secundaria (podria anularse)
		/*vistaPrincipal
				.activar(controladorDominio.getAlumnoSeleccionado() != null);
		vistaSecundaria.hacerInvisible();*/
	}

	/** Llamadas al controlador de dominio **/

	/*public ArrayList<String> cargarAsignaturas(String selectedItem)
			throws FileNotFoundException {
		return controladorDominio.cargarAsignaturas(selectedItem);
	}

	public boolean crearAlumno(String nombreAlumno) {
		return controladorDominio.crearAlumno(nombreAlumno);
	}

	public void eliminarAlumno(String nombreAlumno) {
		controladorDominio.eliminarAlumno(nombreAlumno);
	}

	public void asignarNota(String nombreAsignatura, Double nota) {
		controladorDominio.asignarNota(
				controladorDominio.getAlumnoSeleccionado(), nombreAsignatura,
				nota+"");
	}

	public String calcularMedia(String nombreAlumno) {
		return controladorDominio.calcularMedia(nombreAlumno);
	}

	public String[] getAllAlumnos() {
		return controladorDominio.getAllAlumnos();
	}

	public String getNota(String nombreAsignatura) {
		return controladorDominio.getNota(
				controladorDominio.getAlumnoSeleccionado(), nombreAsignatura);
	}

	public String getAlumnoSeleccionado() {
		return controladorDominio.getAlumnoSeleccionado();
	}

	public void setAlumnoSeleccionado(String alumnoSeleccionado) {
		controladorDominio.setAlumnoSeleccionado(alumnoSeleccionado);
	}*/
}
