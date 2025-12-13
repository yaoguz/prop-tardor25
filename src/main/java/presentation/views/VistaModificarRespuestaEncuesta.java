package presentation.views;

import presentation.CtrlPresentacion;
import domain.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Vista para modificar respuestas de encuesta existentes.
 *
 * Funciona igual que VistaResponderEncuesta pero carga las respuestas
 * existentes del usuario para permitir modificarlas.
 *
 * @author PROP Cluster XX
 */
public class VistaModificarRespuestaEncuesta extends VistaRespuestaEncuestaBase {

	/**
	 * Constructor.
	 * 
     * @param ctrlPresentacion Controlador de presentación
     * @param nombreUsuario Usuario en sesión
     * @param idEncuesta ID de encuesta a modificar
	 */
	public VistaModificarRespuestaEncuesta(CtrlPresentacion ctrlPresentacion, String nombreUsuario, int idEncuesta) {
		super(ctrlPresentacion, nombreUsuario, idEncuesta);
	}

	@Override
	protected String getTituloVentana() {
		return "Modificar Respuestas";
	}

	@Override
	protected String getTextoBotonGuardar() {
		return "Guardar Cambios";
	}

	@Override
	protected String getMensajeExito() {
		return "Respuestas actualizadas correctamente";
	}

	@Override
	protected Respuesta obtenerRespuestaExistente(Pregunta pregunta) {
		Map<Pregunta, Set<Respuesta>> todasRespuestas = ctrlPresentacion.getRespuestasEncuesta(idEncuesta);
		Set<Respuesta> respuestasSet = todasRespuestas.get(pregunta);
		if (respuestasSet != null) {
			for (Respuesta r : respuestasSet) {
				if (r.getNomUsuario().equals(nombreUsuario)) {
					return r;
				}
			}
		}
		return null;
	}

	@Override
	protected PanelRespuesta crearPanelRespuesta(Pregunta pregunta, Respuesta existente) {
		if (pregunta instanceof PreguntaLibre) {
			return new PanelRespuestaLibre((PreguntaLibre) pregunta, (RespuestaLibre) existente);
		} else if (pregunta instanceof PreguntaNumerica) {
			return new PanelRespuestaNumerica((PreguntaNumerica) pregunta, (RespuestaNumerica) existente);
		} else if (pregunta instanceof PreguntaOpcionUnica) {
			return new PanelRespuestaOpcionUnica((PreguntaOpcionUnica) pregunta, (RespuestaOpcionUnica) existente);
		} else if (pregunta instanceof PreguntaOpcionMultiple) {
			return new PanelRespuestaOpcionMultiple((PreguntaOpcionMultiple) pregunta, (RespuestaOpcionMultiple) existente);
		}
		return null;
	}

	@Override
	protected void procesarRespuesta(int idPregunta, Object respuesta, int idRespExistente) {
		if (idRespExistente >= 0) {
			ctrlPresentacion.modificarRespuesta(idRespExistente, respuesta);
		} else {
			ctrlPresentacion.darRespuesta(nombreUsuario, idPregunta, respuesta);
		}
	}

	// ==================== CLASES INTERNAS ====================

	/**
	 * Panel de respuesta para pregunta libre con prefill.
	 */
	private class PanelRespuestaLibre implements PanelRespuesta {
		private PreguntaLibre pregunta;
		private JTextField txtRespuesta;
		private int idRespuesta = -1;

		public PanelRespuestaLibre(PreguntaLibre pregunta, RespuestaLibre existente) {
			this.pregunta = pregunta;
			if (existente != null) this.idRespuesta = existente.getId();
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

			// Prefill si existe respuesta previa
			if (idRespuesta >= 0) {
                // buscar la respuesta del usuario y colocar su texto
				Map<Pregunta, Set<Respuesta>> todas = ctrlPresentacion.getRespuestasEncuesta(idEncuesta);
				Set<Respuesta> set = todas.get(pregunta);
				if (set != null) for (Respuesta r : set) if (r.getId() == idRespuesta && r instanceof RespuestaLibre) txtRespuesta.setText(((RespuestaLibre) r).getRespuesta());
			}

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
			return idRespuesta;
		}
	}

	/**
	 * Panel de respuesta para pregunta numérica con prefill.
	 */
	private class PanelRespuestaNumerica implements PanelRespuesta {
		private PreguntaNumerica pregunta;
		private JSpinner spinRespuesta;
		private int idRespuesta = -1;

		public PanelRespuestaNumerica(PreguntaNumerica pregunta, RespuestaNumerica existente) {
			this.pregunta = pregunta;
			if (existente != null) this.idRespuesta = existente.getId();
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

			// Prefill si existe respuesta previa
			if (idRespuesta >= 0) {
				Map<Pregunta, Set<Respuesta>> todas = ctrlPresentacion.getRespuestasEncuesta(idEncuesta);
				Set<Respuesta> set = todas.get(pregunta);
				if (set != null) for (Respuesta r : set) if (r.getId() == idRespuesta && r instanceof RespuestaNumerica) spinRespuesta.setValue(((RespuestaNumerica) r).getRespuesta());
			}

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
			return true;
		}

		@Override
		public int getIdRespuesta() {
			return idRespuesta;
		}
	}

	/**
	 * Panel de respuesta para pregunta de opción única con prefill.
	 */
	private class PanelRespuestaOpcionUnica implements PanelRespuesta {
		private PreguntaOpcionUnica pregunta;
		private ButtonGroup grupo;
		private Map<JRadioButton, Integer> opcionesMap = new HashMap<>();
		private int idRespuesta = -1;

		public PanelRespuestaOpcionUnica(PreguntaOpcionUnica pregunta, RespuestaOpcionUnica existente) {
			this.pregunta = pregunta;
			if (existente != null) this.idRespuesta = existente.getId();
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

			// Obtener selección previa si existe
			Integer seleccionInicial = null;
			if (idRespuesta >= 0) {
				Map<Pregunta, Set<Respuesta>> todas = ctrlPresentacion.getRespuestasEncuesta(idEncuesta);
				Set<Respuesta> set = todas.get(pregunta);
				if (set != null) for (Respuesta r : set) if (r.getId() == idRespuesta && r instanceof RespuestaOpcionUnica) seleccionInicial = ((RespuestaOpcionUnica) r).getRespuesta();
			}

			for (int i = 0; i < opciones.size(); i++) {
				JRadioButton radio = new JRadioButton(opciones.get(i));
				radio.setFont(FONT_NORMAL);
				radio.setBackground(Color.WHITE);
				radio.addActionListener(e -> cambiosSinGuardar = true);
				grupo.add(radio);
				opcionesMap.put(radio, i);
				panelOpciones.add(radio);

				if (seleccionInicial != null && seleccionInicial == i) radio.setSelected(true);
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
			return idRespuesta;
		}
	}

	/**
	 * Panel de respuesta para pregunta de opción múltiple con prefill.
	 */
	private class PanelRespuestaOpcionMultiple implements PanelRespuesta {
		private PreguntaOpcionMultiple pregunta;
		private List<JCheckBox> checkboxes = new ArrayList<>();
		private int idRespuesta = -1;

		public PanelRespuestaOpcionMultiple(PreguntaOpcionMultiple pregunta, RespuestaOpcionMultiple existente) {
			this.pregunta = pregunta;
			if (existente != null) this.idRespuesta = existente.getId();
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

			// Obtener selecciones previas si existen
			Set<Integer> seleccionInicial = new HashSet<>();
			if (idRespuesta >= 0) {
				Map<Pregunta, Set<Respuesta>> todas = ctrlPresentacion.getRespuestasEncuesta(idEncuesta);
				Set<Respuesta> set = todas.get(pregunta);
				if (set != null) for (Respuesta r : set) if (r.getId() == idRespuesta && r instanceof RespuestaOpcionMultiple) {
					int[] arr = ((RespuestaOpcionMultiple) r).getRespuesta();
					for (int v : arr) seleccionInicial.add(v);
				}
			}

			for (int i = 0; i < opciones.size(); i++) {
				JCheckBox check = new JCheckBox(opciones.get(i));
				check.setFont(FONT_NORMAL);
				check.setBackground(Color.WHITE);
				check.addActionListener(e -> {
					cambiosSinGuardar = true;
					long seleccionadas = checkboxes.stream().filter(JCheckBox::isSelected).count();
					if (seleccionadas > numMax) {
						check.setSelected(false);
						JOptionPane.showMessageDialog(frame, "Solo puedes seleccionar hasta " + numMax + " opciones", "Límite alcanzado", JOptionPane.WARNING_MESSAGE);
					}
				});
				checkboxes.add(check);
				panelOpciones.add(check);

				if (seleccionInicial.contains(i)) check.setSelected(true);
			}

			panel.add(panelOpciones, BorderLayout.CENTER);
			return panel;
		}

		@Override
		public Object getRespuesta() {
			List<Integer> seleccionadas = new ArrayList<>();
			for (int i = 0; i < checkboxes.size(); i++) {
				if (checkboxes.get(i).isSelected()) seleccionadas.add(i);
			}
			return seleccionadas;
		}

		@Override
		public boolean tieneRespuesta() {
			return checkboxes.stream().anyMatch(JCheckBox::isSelected);
		}

		@Override
		public int getIdRespuesta() {
			return idRespuesta;
		}
	}
}
