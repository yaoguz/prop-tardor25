package presentation.views;

import presentation.CtrlPresentacion;
import presentation.components.ComponenteBase;
import presentation.components.BarraNavegacion;
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
 * @author PROP Cluster XX
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
		frame = new JFrame("Ver Respuestas");
		frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);

		// Panel principal
		JPanel panelPrincipal = new JPanel(new BorderLayout(0, 10));
		panelPrincipal.setBackground(COLOR_BG);
		panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// Barra de navegación
		BarraNavegacion barra = new BarraNavegacion(nombreUsuario);
        barra.setOnCerrarSesion(e -> {
            ctrlPresentacion.cerrarSesion();
            frame.dispose();
            ctrlPresentacion.mostrarLogin();
        });
        barra.setOnCambiarContrasena(e -> {
            String nuevaPass = JOptionPane.showInputDialog(frame, "Nueva contraseña:", "Cambiar Contraseña", JOptionPane.PLAIN_MESSAGE);
            if (nuevaPass != null && !nuevaPass.trim().isEmpty()) {
                try {
                    ctrlPresentacion.cambiarContrasena(nuevaPass);
                    JOptionPane.showMessageDialog(frame, "Contraseña cambiada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        barra.setOnSalir(e -> frame.dispose());
        panelPrincipal.add(barra.getPanel(), BorderLayout.NORTH);		// Panel superior: título + toggle
		JPanel panelSuperior = new JPanel(new BorderLayout(10, 10));
		panelSuperior.setBackground(COLOR_BG);
		
		String titulo = ctrlPresentacion.obtenerTituloEncuesta(idEncuesta);
		JLabel lblTitulo = createStyledLabel("Respuestas: " + titulo, FONT_TITLE);
		panelSuperior.add(lblTitulo, BorderLayout.WEST);

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
		panelSuperior.add(toggleVista, BorderLayout.EAST);
		
		panelPrincipal.add(panelSuperior, BorderLayout.NORTH);

		// Panel de contenido (scrollable)
		panelContenido = new JPanel();
		panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
		panelContenido.setBackground(COLOR_BG);

		JScrollPane scrollContenido = new JScrollPane(panelContenido);
		scrollContenido.setBorder(BorderFactory.createEmptyBorder());
		scrollContenido.getVerticalScrollBar().setUnitIncrement(16);
		panelPrincipal.add(scrollContenido, BorderLayout.CENTER);

		// Botón volver
		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panelBotones.setBackground(COLOR_BG);
		JButton btnVolver = createStyledButton("Volver", COLOR_PRIMARY);
		btnVolver.addActionListener(e -> frame.dispose());
		panelBotones.add(btnVolver);
		panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

		frame.add(panelPrincipal);
		
		// Cargar respuestas inicialmente
		actualizarVista();
		
		frame.setVisible(true);
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
					formatearRespuesta(respuesta)
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
			panelContenido.add(Box.createRigidArea(new Dimension(0, 10)));
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
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(COLOR_PRIMARY, 2),
			BorderFactory.createEmptyBorder(15, 15, 15, 15)
		));
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));

		// Título: nombre de usuario
		JLabel lblUsuario = createStyledLabel("👤 " + usuario, FONT_SUBTITLE);
		panel.add(lblUsuario, BorderLayout.NORTH);

		// Respuestas
		JPanel panelRespuestas = new JPanel();
		panelRespuestas.setLayout(new BoxLayout(panelRespuestas, BoxLayout.Y_AXIS));
		panelRespuestas.setBackground(Color.WHITE);
		panelRespuestas.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 0));

		for (Map.Entry<String, String> entry : respuestas.entrySet()) {
			JLabel lblPregunta = createStyledLabel("• " + entry.getKey(), FONT_NORMAL);
			lblPregunta.setForeground(new Color(80, 80, 80));
			panelRespuestas.add(lblPregunta);

			JLabel lblRespuesta = createStyledLabel("  → " + entry.getValue(), FONT_NORMAL);
			lblRespuesta.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
			panelRespuestas.add(lblRespuesta);
		}

		panel.add(panelRespuestas, BorderLayout.CENTER);

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
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(COLOR_PRIMARY, 2),
			BorderFactory.createEmptyBorder(15, 15, 15, 15)
		));
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));

		// Enunciado de pregunta
		JLabel lblPregunta = createStyledLabel("❓ " + pregunta.getEnunciado(), FONT_SUBTITLE);
		panel.add(lblPregunta, BorderLayout.NORTH);

		// Respuestas
		JPanel panelRespuestas = new JPanel();
		panelRespuestas.setLayout(new BoxLayout(panelRespuestas, BoxLayout.Y_AXIS));
		panelRespuestas.setBackground(Color.WHITE);
		panelRespuestas.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 0));

		for (Respuesta respuesta : respuestas) {
			String texto = "• " + respuesta.getNomUsuario() + ": " + formatearRespuesta(respuesta);
			JLabel lblRespuesta = createStyledLabel(texto, FONT_NORMAL);
			lblRespuesta.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
			panelRespuestas.add(lblRespuesta);
		}

		panel.add(panelRespuestas, BorderLayout.CENTER);

		return panel;
	}

	/**
	 * Formatea una respuesta para mostrarla.
	 * 
	 * @param respuesta Respuesta
	 * @return Texto formateado
	 */
	private String formatearRespuesta(Respuesta respuesta) {
		if (respuesta instanceof RespuestaLibre) {
			return ((RespuestaLibre) respuesta).getRespuesta();
		} else if (respuesta instanceof RespuestaNumerica) {
			return String.valueOf(((RespuestaNumerica) respuesta).getRespuesta());
		} else if (respuesta instanceof RespuestaOpcionUnica) {
			int opcionIdx = ((RespuestaOpcionUnica) respuesta).getRespuesta();
			return "Opción " + (opcionIdx + 1);
		} else if (respuesta instanceof RespuestaOpcionMultiple) {
			int[] opcionesArray = ((RespuestaOpcionMultiple) respuesta).getRespuesta();
		List<Integer> opciones = new ArrayList<>();
		for (int op : opcionesArray) opciones.add(op);
			StringBuilder sb = new StringBuilder("Opciones: ");
			for (int i = 0; i < opciones.size(); i++) {
				sb.append(opciones.get(i) + 1);
				if (i < opciones.size() - 1) sb.append(", ");
			}
			return sb.toString();
		}
		return "N/A";
	}
}
