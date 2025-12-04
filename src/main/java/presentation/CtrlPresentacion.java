package presentation;

import domaincontrollers.CtrlDominio;
import domain.*;
import domain.algorithm.estructuras.*;
import presentation.views.*;

import java.io.File;
import java.util.*;

/**
 * Controlador de Presentación.
 * 
 * Coordina todas las vistas y actúa como intermediario entre
 * la capa de presentación y el controlador de dominio.
 * 
 * RESPONSABILIDADES:
 * - Gestionar navegación entre vistas
 * - Intermediar llamadas al controlador de dominio
 * - Mantener estado de sesión actual
 * 
 * ATRIBUTOS PRINCIPALES:
 * - ctrlDominio: Controlador de dominio
 * - usuarioActual: Usuario en sesión
 * 
 * MÉTODOS PRINCIPALES:
 * - inicializarPresentacion(): Inicia aplicación
 * - mostrarXXX(): Navegación entre vistas
 * - Métodos delegados a CtrlDominio
 * 
 * @author PROP Cluster XX
 */
public class CtrlPresentacion {

	/** Atributos **/

	private CtrlDominio ctrlDominio;
	private String usuarioActual;

	/**
	 * Constructor que inicializa controlador de dominio.
	 */
	public CtrlPresentacion() {
		ctrlDominio = CtrlDominio.getInstance();
	}

	/**
	 * Inicializa la aplicación mostrando login.
	 */
	public void inicializarPresentacion() {
		mostrarLogin();
	}

	// ==================== NAVEGACIÓN ENTRE VISTAS ====================

	/**
	 * Muestra vista de login.
	 */
	public void mostrarLogin() {
		VistaLogin vista = new VistaLogin(this);
		vista.mostrar();
	}

	/**
	 * Muestra vista de registro.
	 */
	public void mostrarRegistro() {
		VistaRegistro vista = new VistaRegistro(this);
		vista.mostrar();
	}

	/**
	 * Muestra vista principal con encuestas.
	 * 
	 * @param nombreUsuario Usuario en sesión
	 */
	public void mostrarPaginaPrincipal(String nombreUsuario) {
		this.usuarioActual = nombreUsuario;
		VistaListaEncuestas vista = new VistaListaEncuestas(this, nombreUsuario);
		vista.mostrar();
	}

	/**
	 * Muestra vista para crear encuesta.
	 * 
	 * @param nombreUsuario Usuario creador
	 */
	public void mostrarCrearEncuesta(String nombreUsuario) {
		VistaModificarEncuesta vista = new VistaModificarEncuesta(this, nombreUsuario);
		vista.mostrar();
	}

	/**
	 * Muestra vista para modificar encuesta.
	 * 
	 * @param nombreUsuario Usuario
	 * @param idEncuesta ID de encuesta a modificar
	 */
	public void mostrarModificarEncuesta(String nombreUsuario, int idEncuesta) {
		VistaModificarEncuesta vista = new VistaModificarEncuesta(this, nombreUsuario, idEncuesta);
		vista.mostrar();
	}

	/**
	 * Muestra vista para responder encuesta.
	 * 
	 * @param nombreUsuario Usuario
	 * @param idEncuesta ID de encuesta
	 */
	public void mostrarResponderEncuesta(String nombreUsuario, int idEncuesta) {
		VistaResponderEncuesta vista = new VistaResponderEncuesta(this, nombreUsuario, idEncuesta);
		vista.mostrar();
	}

	public void mostrarModificarResponderEncuesta(String nombreUsuario, int idEncuesta) {
		VistaModificarRespuestaEncuesta vista = new VistaModificarRespuestaEncuesta(this, nombreUsuario, idEncuesta);
		vista.mostrar();
	}

	/**
	 * Muestra vista de respuestas de encuesta.
	 * 
	 * @param nombreUsuario Usuario
	 * @param idEncuesta ID de encuesta
	 */
	public void mostrarVerRespuestas(String nombreUsuario, int idEncuesta) {
		VistaVerRespuestas vista = new VistaVerRespuestas(this, nombreUsuario, idEncuesta);
		vista.mostrar();
	}

	/**
	 * Muestra vista de estadísticas.
	 * 
	 * @param nombreUsuario Usuario
	 * @param idEncuesta ID de encuesta
	 */
	public void mostrarEstadisticas(String nombreUsuario, int idEncuesta) {
		// Placeholder: Vista no implementada en esta ubicación
		javax.swing.JOptionPane.showMessageDialog(null, "Vista Estadísticas pendiente");
	}

	// ==================== DELEGACIÓN A CONTROLADOR DE DOMINIO ====================

	/**
	 * Obtiene usuario actual en sesión.
	 * 
	 * @return Nombre de usuario
	 */
	public String getUsuarioActual() {
		return usuarioActual;
	}

	/**
	 * Registra nuevo usuario.
	 * 
	 * @param nomU Nombre de usuario
	 * @param contrasena Contraseña
	 * @param confirmCont Confirmación contraseña
	 * @return Nombre de usuario creado
	 */
	public String registrarUsuario(String nomU, String contrasena, String confirmCont) {
		return ctrlDominio.registrarUsuario(nomU, contrasena, confirmCont);
	}

	/**
	 * Inicia sesión de usuario.
	 * 
	 * @param nomU Nombre de usuario
	 * @param contrasena Contraseña
	 */
	public void iniciarSesion(String nomU, String contrasena) {
		ctrlDominio.iniciarSesion(nomU, contrasena);
		this.usuarioActual = nomU;
	}

	/**
	 * Cierra sesión actual.
	 */
	public void cerrarSesion() {
		ctrlDominio.cerrarSesion();
		this.usuarioActual = null;
	}

	/**
	 * Cambia contraseña del usuario actual.
	 * 
	 * @param nuevaContrasena Nueva contraseña
	 */
	public void cambiarContrasena(String nuevaContrasena) {
		ctrlDominio.modificarContrasena(nuevaContrasena);
	}

	// ==================== ENCUESTAS ====================

	/**
	 * Crea nueva encuesta.
	 * 
	 * @param nombreUsuario Usuario creador
	 * @param titulo Título
	 * @return ID de encuesta creada
	 */
	public int crearEncuesta(String nombreUsuario, String titulo) {
		return ctrlDominio.crearEncuesta(nombreUsuario, titulo);
	}

	/**
	 * Modifica título de encuesta.
	 * 
	 * @param idEncuesta ID de encuesta
	 * @param titulo Nuevo título
	 */
	public void modificarTituloEncuesta(int idEncuesta, String titulo) {
		ctrlDominio.modificarTituloEncuesta(idEncuesta, titulo);
	}

	/**
	 * Elimina encuesta.
	 * 
	 * @param idEncuesta ID de encuesta
	 */
	public void eliminarEncuesta(int idEncuesta) {
		ctrlDominio.eliminarEncuesta(idEncuesta);
	}

	/**
	 * Importa encuesta desde fichero.
	 * 
	 * @param fichero Fichero con datos
	 * @return ID de encuesta creada
	 */
	public int importarEncuesta(File fichero) {
		return ctrlDominio.importarEncuesta(fichero);
	}

	/**
	 * Obtiene lista de IDs de todas las encuestas.
	 * 
	 * @return Lista de IDs
	 */
	public List<Integer> obtenerTodasEncuestas() {
		// Implementación placeholder - en real se obtendría del dominio
		List<Integer> result = new ArrayList<>();
		result = ctrlDominio.getIdEncuestas();
		return result;
	}

	/**
	 * Obtiene preguntas de una encuesta.
	 * 
	 * @param idEncuesta ID de encuesta
	 * @return Lista de preguntas
	 */
	public List<Pregunta> getPreguntasEncuesta(int idEncuesta) {
		return ctrlDominio.getPreguntasEncuesta(idEncuesta);
	}

	/**
	 * Obtiene IDs de preguntas de encuesta.
	 * 
	 * @param idEncuesta ID de encuesta
	 * @return Lista de IDs
	 */
	public List<Integer> getIdPreguntasEncuesta(int idEncuesta) {
		return ctrlDominio.getIdPreguntasEncuesta(idEncuesta);
	}

	/**
	 * Obtiene título de una encuesta.
	 * 
	 * @param idEncuesta ID de encuesta
	 * @return Título
	 */
	public String obtenerTituloEncuesta(int idEncuesta) {
		// Placeholder
		return ctrlDominio.getTituloEncuesta(idEncuesta);
	}

	/**
	 * Obtiene autor de una encuesta.
	 * 
	 * @param idEncuesta ID de encuesta
	 * @return Nombre del autor
	 */
	public String obtenerAutorEncuesta(int idEncuesta) {
		// Placeholder
		return ctrlDominio.getAutorEncuesta(idEncuesta);
	}

	// ==================== PREGUNTAS ====================

	/**
	 * Añade pregunta libre.
	 * 
	 * @param idEncuesta ID de encuesta
	 * @param enunciado Enunciado
	 * @return ID de pregunta
	 */
	public int anadirPreguntaLibre(int idEncuesta, String enunciado) {
		return ctrlDominio.anadirPreguntaLibre(idEncuesta, enunciado);
	}

	/**
	 * Añade pregunta numérica.
	 * 
	 * @param idEncuesta ID de encuesta
	 * @param enunciado Enunciado
	 * @param min Valor mínimo
	 * @param max Valor máximo
	 * @return ID de pregunta
	 */
	public int anadirPreguntaNumerica(int idEncuesta, String enunciado, double min, double max) {
		return ctrlDominio.anadirPreguntaNumerica(idEncuesta, enunciado, min, max);
	}

	/**
	 * Añade pregunta de opción única.
	 * 
	 * @param idEncuesta ID de encuesta
	 * @param enunciado Enunciado
	 * @param opciones Lista de opciones
	 * @param ordenada Si está ordenada
	 * @return ID de pregunta
	 */
	public int anadirPreguntaOpcionUnica(int idEncuesta, String enunciado, List<String> opciones, boolean ordenada) {
		return ctrlDominio.anadirPreguntaOpcionUnica(idEncuesta, enunciado, opciones, ordenada);
	}

	/**
	 * Añade pregunta de opción múltiple.
	 * 
	 * @param idEncuesta ID de encuesta
	 * @param enunciado Enunciado
	 * @param opciones Lista de opciones
	 * @param numMaxOpc Número máximo de opciones seleccionables
	 * @return ID de pregunta
	 */
	public int anadirPreguntaOpcionMultiple(int idEncuesta, String enunciado, List<String> opciones, int numMaxOpc) {
		return ctrlDominio.anadirPreguntaOpcionMultiple(idEncuesta, enunciado, opciones, numMaxOpc);
	}

	/**
	 * Elimina pregunta.
	 * 
	 * @param idPregunta ID de pregunta
	 */
	public void eliminarPregunta(int idPregunta) {
		ctrlDominio.eliminarPregunta(idPregunta);
	}

	// ==================== RESPUESTAS ====================

	/**
	 * Da respuesta a pregunta.
	 * 
	 * @param <T> Tipo de respuesta
	 * @param nomUsuario Usuario
	 * @param idPregunta ID de pregunta
	 * @param respuesta Contenido de respuesta
	 * @return ID de respuesta
	 */
	public <T> int darRespuesta(String nomUsuario, int idPregunta, T respuesta) {
		return ctrlDominio.darRespuesta(nomUsuario, idPregunta, respuesta);
	}

	/**
	 * Modifica respuesta existente.
	 * 
	 * @param <T> Tipo de respuesta
	 * @param idRespuesta ID de respuesta
	 * @param nuevaRespuesta Nueva respuesta
	 */
	public <T> void modificarRespuesta(int idRespuesta, T nuevaRespuesta) {
		ctrlDominio.modificarRespuesta(idRespuesta, nuevaRespuesta);
	}

	/**
	 * Importa respuestas desde fichero.
	 * 
	 * @param fichero Fichero con respuestas
	 */
	public void importarRespuestas(File fichero) {
		ctrlDominio.importarRespuestas(fichero);
	}

	/**
	 * Obtiene respuestas de encuesta.
	 * 
	 * @param idEncuesta ID de encuesta
	 * @return Mapa de Pregunta → Set de Respuestas
	 */
	public Map<Pregunta, Set<Respuesta>> getRespuestasEncuesta(int idEncuesta) {
		return ctrlDominio.getRespuestasEncuesta(idEncuesta);
	}

	/**
	 * Verifica si usuario ha respondido encuesta.
	 * 
	 * @param nombreUsuario Usuario
	 * @param idEncuesta ID de encuesta
	 * @return true si ha respondido
	 */
	public boolean haRespondido(String nombreUsuario, int idEncuesta) {
		List<Integer> respondidas = ctrlDominio.getEncuestasRespondidas();
		return respondidas.contains(idEncuesta);
	}

	// ==================== CLUSTERING ====================

	/**
	 * Ejecuta clustering sobre encuesta.
	 * 
	 * @param idEncuesta ID de encuesta
	 * @param k Número de clusters
	 * @param algoritmo Algoritmo ("kmeans", "kmeans++", "kmedoids")
	 * @return Resultado de clustering
	 */
	public ResultadoClustering ejecutarClustering(int idEncuesta, int k, String algoritmo) {
		return ctrlDominio.ejecutarClustering(idEncuesta, k, algoritmo);
	}

	/**
	 * Obtiene miembros de un perfil.
	 * 
	 * @param perfil Perfil
	 * @return Lista de nombres de usuarios
	 */
	public List<String> obtenerMiembrosPerfil(Perfil perfil) {
		return ctrlDominio.obtenerMiembrosPerfil(perfil);
	}

	/**
	 * Obtiene representante de un perfil.
	 * 
	 * @param perfil Perfil
	 * @return Nombre del representante
	 */
	public String obtenerRepresentantePerfil(Perfil perfil) {
		return ctrlDominio.obtenerRepresentantePerfil(perfil);
	}
}
