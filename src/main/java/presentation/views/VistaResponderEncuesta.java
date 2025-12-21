package presentation.views;

import presentation.CtrlPresentacion;
import domain.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Vista para responder una encuesta.
 * 
 * Permite al usuario proporcionar respuestas nuevas a una encuesta.
 * Cada tipo de pregunta tiene un panel específico para capturar la respuesta.
 * 
 * @author Xiangjun Yepan
 */
public class VistaResponderEncuesta extends VistaRespuestaEncuestaBase {

	/**
	 * Constructor.
	 * 
	 * @param ctrlPresentacion Controlador
	 * @param nombreUsuario Usuario
	 * @param idEncuesta ID de encuesta a responder
	 */
	public VistaResponderEncuesta(CtrlPresentacion ctrlPresentacion, String nombreUsuario, int idEncuesta) {
		super(ctrlPresentacion, nombreUsuario, idEncuesta);
	}

	@Override
	protected String getTituloVentana() {
		return "Responder Encuesta";
	}

	@Override
	protected String getTextoBotonGuardar() {
		return "Guardar Respuestas";
	}

	@Override
	protected String getMensajeExito() {
		return "Respuestas guardadas correctamente";
	}

	@Override
	protected Respuesta obtenerRespuestaExistente(Pregunta pregunta) {
		return null; // No hay respuestas previas al responder por primera vez
	}

	@Override
	protected PanelRespuesta crearPanelRespuesta(Pregunta pregunta, Respuesta existente) {
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

	@Override
	protected void procesarRespuesta(int idPregunta, Object respuesta, int idRespExistente) {
		ctrlPresentacion.darRespuesta(nombreUsuario, idPregunta, respuesta);
	}

	// ==================== CLASES INTERNAS ====================

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

		@Override
		public int getIdRespuesta() {
			return -1; // No hay respuesta previa
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
		
		/** Checkbox para indicar que no se responde */
		private JCheckBox chkSinResponder;

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
			
			// Checkbox para no responder
			chkSinResponder = new JCheckBox("No responder");
			chkSinResponder.setBackground(Color.WHITE);
			chkSinResponder.addActionListener(e -> {
				spinRespuesta.setEnabled(!chkSinResponder.isSelected());
				cambiosSinGuardar = true;
			});
			
			JPanel panelInput = new JPanel(new FlowLayout(FlowLayout.LEFT));
			panelInput.setBackground(Color.WHITE);
			panelInput.add(spinRespuesta);
			panelInput.add(lblRango);
			panelInput.add(chkSinResponder);
			
			panel.add(panelInput, BorderLayout.CENTER);

			return panel;
		}

		@Override
		public Object getRespuesta() {
			if (chkSinResponder.isSelected()) {
				return null; // No respondida
			}
			return spinRespuesta.getValue();
		}

		@Override
		public boolean tieneRespuesta() {
			return !chkSinResponder.isSelected();
		}

		@Override
		public int getIdRespuesta() {
			return -1;
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

		@Override
		public int getIdRespuesta() {
			return -1;
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

		@Override
		public int getIdRespuesta() {
			return -1;
		}
	}
}
