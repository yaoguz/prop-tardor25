package presentation.views;

import presentation.CtrlPresentacion;
import presentation.components.ComponenteBase;
import domain.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Vista para ver respuestas de una encuesta.
 * 
 * Permite visualizar las respuestas de dos formas:
 * - Por usuario: Muestra todos los usuarios y sus respuestas
 * - Por pregunta: Muestra cada pregunta y todas las respuestas recibidas
 * 
 * CARACTERÍSTICAS:
 * - Toggle para cambiar entre vistas (usuario/pregunta)
 * - Visualización organizada de respuestas
 * - Solo accesible por el autor de la encuesta
 * 
 * @author Alessandro Jin
 */
public class VistaVerRespuestas extends ComponenteBase {

	private CtrlPresentacion ctrlPresentacion;
	private String nombreUsuario;
	private int idEncuesta;
	private JFrame frame;
	private JPanel panelContenido;
	private boolean vistaPorUsuario = true;

	/**
	 * Constructor.
	 * 
	 * @param ctrlPresentacion Controlador de presentación
	 * @param nombreUsuario Usuario
	 * @param idEncuesta ID de encuesta
	 */
	public VistaVerRespuestas(CtrlPresentacion ctrlPresentacion, String nombreUsuario, int idEncuesta) {
		this.ctrlPresentacion = ctrlPresentacion;
		this.nombreUsuario = nombreUsuario;
		this.idEncuesta = idEncuesta;
	}

	/**
	 * Muestra la vista.
	 */
	public void mostrar() {
		frame = crearFrameEstandar("Ver Respuestas");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Panel principal
		JPanel panelPrincipal = new JPanel(new BorderLayout(0, 10));
		panelPrincipal.setBackground(COLOR_BG);
		panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		
		
		// Header: fila superior con botón "Salir" y debajo el título + toggle
		JPanel panelTop = new JPanel();
		panelTop.setLayout(new BoxLayout(panelTop, BoxLayout.Y_AXIS));
		panelTop.setBackground(COLOR_BG);

		// Fila 1: botón salir a la izquierda
		JPanel filaSalir = new JPanel(new BorderLayout());
		filaSalir.setBackground(COLOR_BG);
		JButton btnSalir = createStyledButton("← Salir", COLOR_SECONDARY);
		btnSalir.addActionListener(e -> salir());
		filaSalir.add(btnSalir, BorderLayout.WEST);
		panelTop.add(filaSalir);

		// Fila 2: título a la izquierda y toggle a la derecha
		JPanel filaTitulo = new JPanel(new BorderLayout(10, 10));
		filaTitulo.setBackground(COLOR_BG);
		String titulo = ctrlPresentacion.obtenerTituloEncuesta(idEncuesta);
		JLabel lblTitulo = createStyledLabel("Respuestas: " + titulo, FONT_TITLE);
		filaTitulo.add(lblTitulo, BorderLayout.WEST);

		// Toggle para cambiar vista
		JToggleButton toggleVista = new JToggleButton("Por Usuario");
		toggleVista.setSelected(true);
		toggleVista.setFont(FONT_NORMAL);
		toggleVista.setBackground(COLOR_PRIMARY);
		toggleVista.setForeground(Color.WHITE);
		toggleVista.setFocusPainted(false);
		toggleVista.addActionListener(e -> {
			vistaPorUsuario = !vistaPorUsuario;
			toggleVista.setText(vistaPorUsuario ? "Por Usuario" : "Por Pregunta");
			actualizarVista();
		});
		// Para evitar que el texto se corte, envolver el toggle en un panel a la derecha
		JPanel panelToggle = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panelToggle.setBackground(COLOR_BG);
		// Asegurar tamaño mínimo para que el texto quepa
		Dimension pref = toggleVista.getPreferredSize();
		toggleVista.setPreferredSize(new Dimension(Math.max(pref.width + 20, 140), pref.height + 6));
		panelToggle.add(toggleVista);
		filaTitulo.add(panelToggle, BorderLayout.EAST);
		panelTop.add(filaTitulo);
        
		panelPrincipal.add(panelTop, BorderLayout.NORTH);

		// Panel de contenido (scrollable)
		panelContenido = new JPanel();
		panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
		panelContenido.setBackground(COLOR_BG);

		JScrollPane scrollContenido = new JScrollPane(panelContenido);
		scrollContenido.setBorder(BorderFactory.createEmptyBorder());
		scrollContenido.getVerticalScrollBar().setUnitIncrement(16);
		panelPrincipal.add(scrollContenido, BorderLayout.CENTER);

		// // Botón volver
		// JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		// panelBotones.setBackground(COLOR_BG);
		// JButton btnVolver = createStyledButton("Volver", COLOR_PRIMARY);
		// btnVolver.addActionListener(e -> frame.dispose());
		// panelBotones.add(btnVolver);
		// panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

		frame.add(panelPrincipal);
		
		// Cargar respuestas inicialmente
		actualizarVista();
		
		frame.setVisible(true);
	}

	/**
     * Sale de la vista y vuelve a la página principal.
     */
    private void salir() {
        frame.dispose();
        ctrlPresentacion.mostrarPaginaPrincipal(nombreUsuario);
    }

	/**
	 * Actualiza la vista según el modo seleccionado.
	 */
	private void actualizarVista() {
		panelContenido.removeAll();

		if (vistaPorUsuario) {
			mostrarPorUsuario();
		} else {
			mostrarPorPregunta();
		}

		panelContenido.revalidate();
		panelContenido.repaint();
	}

	/**
	 * Muestra respuestas agrupadas por usuario.
	 */
	private void mostrarPorUsuario() {
		Map<Pregunta, Set<Respuesta>> respuestas = ctrlPresentacion.getRespuestasEncuesta(idEncuesta);
		
		// Agrupar por usuario
		Map<String, Map<String, String>> respuestasPorUsuario = new TreeMap<>();
		
		for (Map.Entry<Pregunta, Set<Respuesta>> entry : respuestas.entrySet()) {
			Pregunta pregunta = entry.getKey();
			for (Respuesta respuesta : entry.getValue()) {
				String usuario = respuesta.getNomUsuario();
				respuestasPorUsuario.putIfAbsent(usuario, new LinkedHashMap<>());
				respuestasPorUsuario.get(usuario).put(
					pregunta.getEnunciado(),
					formatearRespuesta(respuesta,pregunta)
				);
			}
		}

		if (respuestasPorUsuario.isEmpty()) {
			JLabel lblVacio = createStyledLabel("No hay respuestas todavía", FONT_NORMAL);
			lblVacio.setAlignmentX(Component.CENTER_ALIGNMENT);
			panelContenido.add(lblVacio);
			return;
		}

		// Mostrar cada usuario
		for (Map.Entry<String, Map<String, String>> entry : respuestasPorUsuario.entrySet()) {
			JPanel panelUsuario = crearPanelUsuario(entry.getKey(), entry.getValue());
			panelContenido.add(panelUsuario);
			panelContenido.add(Box.createRigidArea(new Dimension(0, 10)));
		}
	}

	/**
	 * Muestra respuestas agrupadas por pregunta.
	 */
	private void mostrarPorPregunta() {
		Map<Pregunta, Set<Respuesta>> respuestas = ctrlPresentacion.getRespuestasEncuesta(idEncuesta);

		if (respuestas.isEmpty()) {
			JLabel lblVacio = createStyledLabel("No hay respuestas todavía", FONT_NORMAL);
			lblVacio.setAlignmentX(Component.CENTER_ALIGNMENT);
			panelContenido.add(lblVacio);
			return;
		}

		// Mostrar cada pregunta
		for (Map.Entry<Pregunta, Set<Respuesta>> entry : respuestas.entrySet()) {
			JPanel panelPregunta = crearPanelPregunta(entry.getKey(), entry.getValue());
			panelContenido.add(panelPregunta);
			//panelContenido.add(Box.createRigidArea(new Dimension(0, 10)));
		}
	}

	/**
	 * Crea panel para mostrar respuestas de un usuario.
	 * 
	 * @param usuario Nombre de usuario
	 * @param respuestas Mapa de pregunta → respuesta
	 * @return Panel configurado
	 */
	private JPanel crearPanelUsuario(String usuario, Map<String, String> respuestas) {

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(COLOR_PRIMARY, 2),
				BorderFactory.createEmptyBorder(15, 15, 15, 15)
		));

		panel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Título
		JLabel lblUsuario = createStyledLabel("Usuario: " + usuario, FONT_SUBTITLE);
		lblUsuario.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblUsuario.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblUsuario.getPreferredSize().height));
		panel.add(lblUsuario);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));

		// Respuestas
		for (Map.Entry<String, String> entry : respuestas.entrySet()) {
			JLabel lblPregunta = createStyledLabel("• " + entry.getKey(), FONT_NORMAL);
			lblPregunta.setAlignmentX(Component.LEFT_ALIGNMENT);
			lblPregunta.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblPregunta.getPreferredSize().height));
			panel.add(lblPregunta);

			JLabel lblRespuesta = createStyledLabel("  → " + entry.getValue(), FONT_NORMAL);
			lblRespuesta.setAlignmentX(Component.LEFT_ALIGNMENT);
			lblRespuesta.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblRespuesta.getPreferredSize().height));
			panel.add(lblRespuesta);

			panel.add(Box.createRigidArea(new Dimension(0, 8)));
		}

		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));

		return panel;
	}


	/**
	 * Crea panel para mostrar respuestas de una pregunta.
	 * 
	 * @param pregunta Pregunta
	 * @param respuestas Set de respuestas
	 * @return Panel configurado
	 */
	private JPanel crearPanelPregunta(Pregunta pregunta, Set<Respuesta> respuestas) {

		JPanel panel = new JPanel();
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(COLOR_PRIMARY, 2),
			BorderFactory.createEmptyBorder(15, 15, 15, 15)
		));

		
		panel.setAlignmentX(Component.LEFT_ALIGNMENT);

		// Enunciado
		JLabel lblPregunta = createStyledLabel("Pregunta: " + pregunta.getEnunciado(), FONT_SUBTITLE);
		lblPregunta.setAlignmentX(Component.LEFT_ALIGNMENT);
		lblPregunta.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblPregunta.getPreferredSize().height));
		panel.add(lblPregunta);
		panel.add(Box.createRigidArea(new Dimension(0, 10)));

		// Panel de respuestas
		JPanel panelRespuestas = new JPanel();
		panelRespuestas.setLayout(new BoxLayout(panelRespuestas, BoxLayout.Y_AXIS));
		panelRespuestas.setBackground(Color.WHITE);
		panelRespuestas.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 0));

		for (Respuesta respuesta : respuestas) {
			String texto = "• " + respuesta.getNomUsuario() + ": " + formatearRespuesta(respuesta, pregunta);
			JLabel lblRespuesta = createStyledLabel(texto, FONT_NORMAL);

			// un poco de espacio
			lblRespuesta.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
			lblRespuesta.setAlignmentX(Component.LEFT_ALIGNMENT);
			lblRespuesta.setMaximumSize(new Dimension(Integer.MAX_VALUE, lblRespuesta.getPreferredSize().height));

			panelRespuestas.add(lblRespuesta);
		}

		panelRespuestas.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelRespuestas.setMaximumSize(new Dimension(Integer.MAX_VALUE, panelRespuestas.getPreferredSize().height));
		panel.add(panelRespuestas);
		panel.add(Box.createRigidArea(new Dimension(0, 8)));

		panel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));

		return panel;
	}

	/**
	 * Formatea una respuesta para mostrarla.
	 * 
	 * @param respuesta Respuesta
	 * @return Texto formateado
	 */
	private String formatearRespuesta(Respuesta respuesta, Pregunta pregunta) {
		if (respuesta instanceof RespuestaLibre) {
			return ((RespuestaLibre) respuesta).getRespuesta();
		} 
		else if (respuesta instanceof RespuestaNumerica) {
			return String.valueOf(((RespuestaNumerica) respuesta).getRespuesta());
		} 
		else if (respuesta instanceof RespuestaOpcionUnica) {
			int opcionIdx = ((RespuestaOpcionUnica) respuesta).getRespuesta();
			ArrayList<String> opciones = ((PreguntaOpcionUnica) pregunta).getOpciones();
			//if (opcionIdx < 0 || opcionIdx >= opciones.size()) return "Opción inválida";
			String textoOpcion = opciones.get(opcionIdx);
			return " Opción " + (opcionIdx + 1) + ": " + textoOpcion;
		} 
		else if (respuesta instanceof RespuestaOpcionMultiple) {
			int[] opcionesArray = ((RespuestaOpcionMultiple) respuesta).getRespuesta();
			List<String> enunciado = new ArrayList<>();
			for (int op : opcionesArray) enunciado.add(pregunta.getOpciones().get(op));
			return " Opciones: "  + String.join(", ", enunciado);
		}
		return "N/A";
	}
}
