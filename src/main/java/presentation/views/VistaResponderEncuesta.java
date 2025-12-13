package presentation.views;

import presentation.CtrlPresentacion;
import presentation.components.ComponenteBase;
import domain.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Vista para responder una encuesta.
 * 
 * Muestra las preguntas de la encuesta y permite al usuario proporcionar respuestas.
 * Cada tipo de pregunta tiene un panel específico para capturar la respuesta.
 * 
 * CARACTERÍSTICAS:
 * - Muestra todas las preguntas de forma secuencial
 * - Inputs específicos según tipo de pregunta
 * - Validación de respuestas antes de guardar
 * - Confirmación de salida si hay cambios sin guardar
 * 
 * @author PROP Cluster XX
 */
public class VistaResponderEncuesta extends ComponenteBase {

	/** Controlador de presentación */
	private CtrlPresentacion ctrlPresentacion;
	
	/** Nombre del usuario en sesión */
	private String nombreUsuario;
	
	/** ID de encuesta a responder */
	private int idEncuesta;
	
	/** Ventana principal */
	private JFrame frame;
	
	/** Indica si hay cambios sin guardar */
	private boolean cambiosSinGuardar = false;
	
	/** Mapa de paneles de respuesta por ID de pregunta */
	private Map<Integer, PanelRespuesta> paneles = new LinkedHashMap<>();

	/**
	 * Constructor.
	 * 
	 * @param ctrlPresentacion Controlador de presentación
	 * @param nombreUsuario Usuario
	 * @param idEncuesta ID de encuesta a responder
	 */
	public VistaResponderEncuesta(CtrlPresentacion ctrlPresentacion, String nombreUsuario, int idEncuesta) {
		this.ctrlPresentacion = ctrlPresentacion;
		this.nombreUsuario = nombreUsuario;
		this.idEncuesta = idEncuesta;
	}

	/**
	 * Muestra la vista.
	 */
	public void mostrar() {
		frame = new JFrame("Responder Encuesta");
		frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
		frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setResizable(false);

		// Listener para confirmar salida
		frame.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				salir();
			}
		});

		// Panel principal
		JPanel panelPrincipal = new JPanel(new BorderLayout(0, 10));
		panelPrincipal.setBackground(COLOR_BG);
		panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		// // Barra de navegación
		// BarraNavegacion barra = new BarraNavegacion(nombreUsuario);
        // barra.setOnCerrarSesion(e -> {
        //     ctrlPresentacion.cerrarSesion();
        //     frame.dispose();
        //     ctrlPresentacion.mostrarLogin();
        // });
        // barra.setOnCambiarContrasena(e -> {
        //     String nuevaPass = JOptionPane.showInputDialog(frame, "Nueva contraseña:", "Cambiar Contraseña", JOptionPane.PLAIN_MESSAGE);
        //     if (nuevaPass != null && !nuevaPass.trim().isEmpty()) {
        //         try {
        //             ctrlPresentacion.cambiarContrasena(nuevaPass);
        //             JOptionPane.showMessageDialog(frame, "Contraseña cambiada correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
        //         } catch (Exception ex) {
        //             JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        //         }
        //     }
        // });
        // barra.setOnSalir(e -> salir());
        // panelPrincipal.add(barra.getPanel(), BorderLayout.NORTH);		// Título
		String titulo = ctrlPresentacion.obtenerTituloEncuesta(idEncuesta);
		JLabel lblTitulo = createStyledLabel(titulo, FONT_TITLE);
		lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

		// Panel de preguntas (scrollable)
		JPanel panelPreguntas = new JPanel();
		panelPreguntas.setLayout(new BoxLayout(panelPreguntas, BoxLayout.Y_AXIS));
		panelPreguntas.setBackground(COLOR_BG);

		cargarPreguntas(panelPreguntas);

		JScrollPane scrollPreguntas = new JScrollPane(panelPreguntas);
		scrollPreguntas.setBorder(BorderFactory.createEmptyBorder());
		scrollPreguntas.getVerticalScrollBar().setUnitIncrement(16);
		panelPrincipal.add(scrollPreguntas, BorderLayout.CENTER);

		// Botones
		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		panelBotones.setBackground(COLOR_BG);

		JButton btnCancelar = createStyledButton("Cancelar", COLOR_DANGER);
		btnCancelar.addActionListener(e -> salir());

		JButton btnGuardar = createStyledButton("Guardar Respuestas", COLOR_SUCCESS);
		btnGuardar.addActionListener(e -> guardarRespuestas());

		panelBotones.add(btnCancelar);
		panelBotones.add(btnGuardar);
		panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

		frame.add(panelPrincipal);
		frame.setVisible(true);
	}

	/**
	 * Carga preguntas de encuesta y crea paneles de respuesta.
	 * 
	 * @param panelPreguntas Panel donde añadir preguntas
	 */
	private void cargarPreguntas(JPanel panelPreguntas) {
		List<Pregunta> preguntas = ctrlPresentacion.getPreguntasEncuesta(idEncuesta);
		
		for (Pregunta pregunta : preguntas) {
			PanelRespuesta panel = crearPanelRespuesta(pregunta);
			paneles.put(pregunta.getIdPregunta(), panel);
			
			JPanel container = panel.getPanel();
			container.setMaximumSize(new Dimension(Integer.MAX_VALUE, container.getPreferredSize().height));
			panelPreguntas.add(container);
			panelPreguntas.add(Box.createRigidArea(new Dimension(0, 10)));
		}
	}

	/**
	 * Crea panel de respuesta según tipo de pregunta.
	 * 
	 * @param pregunta Pregunta para la cual crear panel
	 * @return Panel de respuesta apropiado
	 */
	private PanelRespuesta crearPanelRespuesta(Pregunta pregunta) {
		if (pregunta instanceof PreguntaLibre) {
			return new PanelRespuestaLibre((PreguntaLibre) pregunta);
		} else if (pregunta instanceof PreguntaNumerica) {
			return new PanelRespuestaNumerica((PreguntaNumerica) pregunta);
		} else if (pregunta instanceof PreguntaOpcionUnica) {
			return new PanelRespuestaOpcionUnica((PreguntaOpcionUnica) pregunta);
		} else if (pregunta instanceof PreguntaOpcionMultiple) {
			return new PanelRespuestaOpcionMultiple((PreguntaOpcionMultiple) pregunta);
		}
		return null;
	}

	/**
	 * Guarda todas las respuestas validando completitud.
	 */
	private void guardarRespuestas() {
		try {
			// Validar que todas las preguntas tienen respuesta
			// for (Map.Entry<Integer, PanelRespuesta> entry : paneles.entrySet()) {
			// 	if (!entry.getValue().tieneRespuesta()) {
			// 		JOptionPane.showMessageDialog(frame, "Por favor, responde todas las preguntas", "Validación", JOptionPane.WARNING_MESSAGE);
			// 		return;
			// 	}
			// }

			// Guardar respuestas
			for (Map.Entry<Integer, PanelRespuesta> entry : paneles.entrySet()) {
				int idPregunta = entry.getKey();
				Object respuesta = entry.getValue().getRespuesta();
				//ctrlPresentacion.darRespuesta(nombreUsuario, idPregunta, respuesta);
				if (respuesta instanceof List) {
					@SuppressWarnings("unchecked")
					List<Integer> list = (List<Integer>) respuesta;
					int[] arr = new int[list.size()];
					for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
					ctrlPresentacion.darRespuesta(nombreUsuario, idPregunta, arr);
				} else {
					ctrlPresentacion.darRespuesta(nombreUsuario, idPregunta, respuesta);
				}
			}

			JOptionPane.showMessageDialog(panel, "Respuestas guardadas correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
			cambiosSinGuardar = false;
			frame.dispose();
			ctrlPresentacion.mostrarPaginaPrincipal(nombreUsuario);

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(panel, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Sale de la vista con confirmación.
	 */
	private void salir() {
		if (cambiosSinGuardar) {
			int opcion = JOptionPane.showConfirmDialog(null,
				"Hay cambios sin guardar. ¿Deseas salir sin guardar?",
				"Confirmar Salida",
				JOptionPane.YES_NO_OPTION,
				JOptionPane.WARNING_MESSAGE);
			if (opcion != JOptionPane.YES_OPTION) return;
		}
		frame.dispose();
		ctrlPresentacion.mostrarPaginaPrincipal(nombreUsuario);
	}

	// ==================== INTERFACES Y CLASES INTERNAS ====================

	/**
	 * Interfaz común para paneles de respuesta.
	 * 
	 * Define métodos para obtener panel visual, capturar respuesta y validar.
	 */
	private interface PanelRespuesta {
		/** Obtiene panel visual del componente */
		JPanel getPanel();
		
		/** Obtiene respuesta capturada */
		Object getRespuesta();
		
		/** Verifica si se ha dado una respuesta válida */
		boolean tieneRespuesta();
	}

	/**
	 * Panel de respuesta para pregunta libre.
	 * 
	 * Muestra campo de texto para respuesta de texto libre.
	 */
	private class PanelRespuestaLibre implements PanelRespuesta {
		/** Pregunta asociada */
		private PreguntaLibre pregunta;
		
		/** Campo de texto para respuesta */
		private JTextField txtRespuesta;

		/**
		 * Constructor.
		 * 
		 * @param pregunta Pregunta libre
		 */
		public PanelRespuestaLibre(PreguntaLibre pregunta) {
			this.pregunta = pregunta;
		}

		@Override
		public JPanel getPanel() {
			JPanel panel = new JPanel(new BorderLayout(10, 10));
			panel.setBackground(Color.WHITE);
			panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(COLOR_PRIMARY, 2),
				BorderFactory.createEmptyBorder(15, 15, 15, 15)
			));

			JLabel lblEnunciado = createStyledLabel(pregunta.getEnunciado(), FONT_NORMAL);
			panel.add(lblEnunciado, BorderLayout.NORTH);

			txtRespuesta = createStyledTextField(50);
			txtRespuesta.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
				public void insertUpdate(javax.swing.event.DocumentEvent e) { cambiosSinGuardar = true; }
				public void removeUpdate(javax.swing.event.DocumentEvent e) { cambiosSinGuardar = true; }
				public void changedUpdate(javax.swing.event.DocumentEvent e) { cambiosSinGuardar = true; }
			});
			panel.add(txtRespuesta, BorderLayout.CENTER);

			return panel;
		}

		@Override
		public Object getRespuesta() {
			return txtRespuesta.getText();
		}

		@Override
		public boolean tieneRespuesta() {
			return !txtRespuesta.getText().trim().isEmpty();
		}
	}

	/**
	 * Panel de respuesta para pregunta numérica.
	 * 
	 * Muestra spinner con rango válido definido en pregunta.
	 */
	private class PanelRespuestaNumerica implements PanelRespuesta {
		/** Pregunta asociada */
		private PreguntaNumerica pregunta;
		
		/** Spinner para valor numérico */
		private JSpinner spinRespuesta;

		/**
		 * Constructor.
		 * 
		 * @param pregunta Pregunta numérica
		 */
		public PanelRespuestaNumerica(PreguntaNumerica pregunta) {
			this.pregunta = pregunta;
		}

		@Override
		public JPanel getPanel() {
			JPanel panel = new JPanel(new BorderLayout(10, 10));
			panel.setBackground(Color.WHITE);
			panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(COLOR_PRIMARY, 2),
				BorderFactory.createEmptyBorder(15, 15, 15, 15)
			));

			JLabel lblEnunciado = createStyledLabel(pregunta.getEnunciado(), FONT_NORMAL);
			panel.add(lblEnunciado, BorderLayout.NORTH);

			double min = pregunta.getRangoInf();
			double max = pregunta.getRangoSup();
			SpinnerNumberModel model = new SpinnerNumberModel((min + max) / 2, min, max, 1.0);
			spinRespuesta = new JSpinner(model);
			spinRespuesta.setFont(FONT_NORMAL);
			spinRespuesta.addChangeListener(e -> cambiosSinGuardar = true);
			
			JLabel lblRango = createStyledLabel("Rango: [" + min + ", " + max + "]", FONT_SMALL);
			
			JPanel panelInput = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panelInput.setBackground(Color.WHITE);
			panelInput.add(spinRespuesta);
			panelInput.add(lblRango);
			
			panel.add(panelInput, BorderLayout.CENTER);

			return panel;
		}

		@Override
		public Object getRespuesta() {
			return spinRespuesta.getValue();
		}

		@Override
		public boolean tieneRespuesta() {
			return true; // Siempre tiene valor
		}
	}

	/**
	 * Panel de respuesta para pregunta de opción única.
	 * 
	 * Muestra radio buttons para selección exclusiva.
	 */
	private class PanelRespuestaOpcionUnica implements PanelRespuesta {
		/** Pregunta asociada */
		private PreguntaOpcionUnica pregunta;
		
		/** Grupo de radio buttons */
		private ButtonGroup grupo;
		
		/** Mapa de radio button a índice de opción */
		private Map<JRadioButton, Integer> opcionesMap = new HashMap<>();

		/**
		 * Constructor.
		 * 
		 * @param pregunta Pregunta de opción única
		 */
		public PanelRespuestaOpcionUnica(PreguntaOpcionUnica pregunta) {
			this.pregunta = pregunta;
		}

		@Override
		public JPanel getPanel() {
			JPanel panel = new JPanel(new BorderLayout(10, 10));
			panel.setBackground(Color.WHITE);
			panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(COLOR_PRIMARY, 2),
				BorderFactory.createEmptyBorder(15, 15, 15, 15)
			));

			JLabel lblEnunciado = createStyledLabel(pregunta.getEnunciado(), FONT_NORMAL);
			panel.add(lblEnunciado, BorderLayout.NORTH);

			JPanel panelOpciones = new JPanel();
			panelOpciones.setLayout(new BoxLayout(panelOpciones, BoxLayout.Y_AXIS));
			panelOpciones.setBackground(Color.WHITE);

			grupo = new ButtonGroup();
			List<String> opciones = pregunta.getOpciones();
			
			for (int i = 0; i < opciones.size(); i++) {
				JRadioButton radio = new JRadioButton(opciones.get(i));
				radio.setFont(FONT_NORMAL);
				radio.setBackground(Color.WHITE);
				radio.addActionListener(e -> cambiosSinGuardar = true);
				grupo.add(radio);
				opcionesMap.put(radio, i);
				panelOpciones.add(radio);
			}

			panel.add(panelOpciones, BorderLayout.CENTER);

			return panel;
		}

		@Override
		public Object getRespuesta() {
			for (Map.Entry<JRadioButton, Integer> entry : opcionesMap.entrySet()) {
				if (entry.getKey().isSelected()) {
					return entry.getValue();
				}
			}
			return null;
		}

		@Override
		public boolean tieneRespuesta() {
			return getRespuesta() != null;
		}
	}

	/**
	 * Panel de respuesta para pregunta de opción múltiple.
	 * 
	 * Muestra checkboxes con límite de selecciones configurado.
	 */
	private class PanelRespuestaOpcionMultiple implements PanelRespuesta {
		/** Pregunta asociada */
		private PreguntaOpcionMultiple pregunta;
		
		/** Lista de checkboxes de opciones */
		private List<JCheckBox> checkboxes = new ArrayList<>();

		/**
		 * Constructor.
		 * 
		 * @param pregunta Pregunta de opción múltiple
		 */
		public PanelRespuestaOpcionMultiple(PreguntaOpcionMultiple pregunta) {
			this.pregunta = pregunta;
		}

		@Override
		public JPanel getPanel() {
			JPanel panel = new JPanel(new BorderLayout(10, 10));
			panel.setBackground(Color.WHITE);
			panel.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(COLOR_PRIMARY, 2),
				BorderFactory.createEmptyBorder(15, 15, 15, 15)
			));

			String info = " (Selecciona hasta " + pregunta.getNumMaxOpc() + " opciones)";
			JLabel lblEnunciado = createStyledLabel(pregunta.getEnunciado() + info, FONT_NORMAL);
			panel.add(lblEnunciado, BorderLayout.NORTH);

			JPanel panelOpciones = new JPanel();
			panelOpciones.setLayout(new BoxLayout(panelOpciones, BoxLayout.Y_AXIS));
			panelOpciones.setBackground(Color.WHITE);

			List<String> opciones = pregunta.getOpciones();
			int numMax = pregunta.getNumMaxOpc();
			
			for (String opcion : opciones) {
				JCheckBox check = new JCheckBox(opcion);
				check.setFont(FONT_NORMAL);
				check.setBackground(Color.WHITE);
				check.addActionListener(e -> {
					cambiosSinGuardar = true;
					// Limitar selecciones
					long seleccionadas = checkboxes.stream().filter(JCheckBox::isSelected).count();
					if (seleccionadas > numMax) {
						check.setSelected(false);
						JOptionPane.showMessageDialog(frame, "Solo puedes seleccionar hasta " + numMax + " opciones", "Límite alcanzado", JOptionPane.WARNING_MESSAGE);
					}
				});
				checkboxes.add(check);
				panelOpciones.add(check);
			}

			panel.add(panelOpciones, BorderLayout.CENTER);

			return panel;
		}

		@Override
		public Object getRespuesta() {
			List<Integer> seleccionadas = new ArrayList<>();
			for (int i = 0; i < checkboxes.size(); i++) {
				if (checkboxes.get(i).isSelected()) {
					seleccionadas.add(i);
				}
			}
			return seleccionadas;
		}

		@Override
		public boolean tieneRespuesta() {
			return checkboxes.stream().anyMatch(JCheckBox::isSelected);
		}
	}
}
