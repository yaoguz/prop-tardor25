package presentation.views;

import presentation.CtrlPresentacion;
import presentation.components.ComponenteBase;
import domain.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Clase base abstracta para vistas de respuesta de encuestas.
 * 
 * Implementa Template Method para compartir lógica común entre
 * VistaResponderEncuesta y VistaModificarRespuestaEncuesta.
 * 
 * RESPONSABILIDADES:
 * - Definir estructura común de interfaz
 * - Gestionar ciclo de vida de respuestas
 * - Coordinar validación y guardado
 * 
 * PATRONES:
 * - Template Method: define esqueleto de operaciones, subclases implementan hooks
 * 
 * @author Yaoyao Guo
 */
public abstract class VistaRespuestaEncuestaBase extends ComponenteBase {

	/** Controlador de presentación */
	protected CtrlPresentacion ctrlPresentacion;
	
	/** Nombre del usuario en sesión */
	protected String nombreUsuario;
	
	/** ID de encuesta */
	protected int idEncuesta;
	
	/** Ventana principal */
	protected JFrame frame;
	
	/** Indica si hay cambios sin guardar */
	protected boolean cambiosSinGuardar = false;
	
	/** Mapa de paneles de respuesta por ID de pregunta */
	protected Map<Integer, PanelRespuesta> paneles = new LinkedHashMap<>();

	/**
	 * Constructor.
	 * 
	 * @param ctrlPresentacion Controlador
	 * @param nombreUsuario Usuario
	 * @param idEncuesta ID de encuesta
	 */
	protected VistaRespuestaEncuestaBase(CtrlPresentacion ctrlPresentacion, String nombreUsuario, int idEncuesta) {
		this.ctrlPresentacion = ctrlPresentacion;
		this.nombreUsuario = nombreUsuario;
		this.idEncuesta = idEncuesta;
	}

	/**
	 * Template Method: muestra la vista.
	 * Define el esqueleto del algoritmo, delegando pasos específicos a subclases.
	 */
	public final void mostrar() {
		frame = crearFrameEstandar(getTituloVentana());
		configurarCierreVentana(frame, this::salir);

		JPanel panelPrincipal = new JPanel(new BorderLayout(0, 10));
		panelPrincipal.setBackground(COLOR_BG);
		panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		String titulo = ctrlPresentacion.obtenerTituloEncuesta(idEncuesta);
		JLabel lblTitulo = createStyledLabel(titulo, FONT_TITLE);
		lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
		lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
		panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

		JPanel panelPreguntas = new JPanel();
		panelPreguntas.setLayout(new BoxLayout(panelPreguntas, BoxLayout.Y_AXIS));
		panelPreguntas.setBackground(COLOR_BG);

		cargarPreguntas(panelPreguntas);

		JScrollPane scrollPreguntas = new JScrollPane(panelPreguntas);
		scrollPreguntas.setBorder(BorderFactory.createEmptyBorder());
		scrollPreguntas.getVerticalScrollBar().setUnitIncrement(16);
		panelPrincipal.add(scrollPreguntas, BorderLayout.CENTER);

		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		panelBotones.setBackground(COLOR_BG);

		JButton btnCancelar = createStyledButton("Cancelar", COLOR_DANGER);
		btnCancelar.addActionListener(e -> salir());

		JButton btnGuardar = createStyledButton(getTextoBotonGuardar(), COLOR_SUCCESS);
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
	protected void cargarPreguntas(JPanel panelPreguntas) {
		List<Pregunta> preguntas = ctrlPresentacion.getPreguntasEncuesta(idEncuesta);
		
		for (Pregunta pregunta : preguntas) {
			Respuesta respuestaExistente = obtenerRespuestaExistente(pregunta);
			PanelRespuesta panel = crearPanelRespuesta(pregunta, respuestaExistente);
			paneles.put(pregunta.getIdPregunta(), panel);
			
			JPanel container = panel.getPanel();
			container.setMaximumSize(new Dimension(Integer.MAX_VALUE, container.getPreferredSize().height));
			panelPreguntas.add(container);
			panelPreguntas.add(Box.createRigidArea(new Dimension(0, 10)));
		}
	}

	/**
	 * Guarda respuestas validando completitud.
	 */
	protected void guardarRespuestas() {
		try {
			for (Map.Entry<Integer, PanelRespuesta> entry : paneles.entrySet()) {
				int idPregunta = entry.getKey();
				Object respuesta = entry.getValue().getRespuesta();
				int idRespExistente = entry.getValue().getIdRespuesta();

				// Procesar respuesta (incluso si es null, para poder eliminar respuestas existentes)
				if (respuesta == null || (respuesta instanceof String && ((String)respuesta).trim().isEmpty())) {
					// Si hay una respuesta existente y ahora está vacía, procesarla (para eliminarla)
					if (idRespExistente >= 0) {
						procesarRespuesta(idPregunta, null, idRespExistente);
					}
					// Si no hay respuesta existente, simplemente permitir no responder
					continue;
				}

				if (respuesta instanceof List) {
					@SuppressWarnings("unchecked")
					List<Integer> list = (List<Integer>) respuesta;
					if (list.isEmpty()) {
						// Si hay una respuesta existente y ahora está vacía, procesarla (para eliminarla)
						if (idRespExistente >= 0) {
							procesarRespuesta(idPregunta, null, idRespExistente);
						}
						continue;
					}
					int[] arr = new int[list.size()];
					for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
					procesarRespuesta(idPregunta, arr, idRespExistente);
				} else {
					procesarRespuesta(idPregunta, respuesta, idRespExistente);
				}
			}

			JOptionPane.showMessageDialog(panel, getMensajeExito(), "Éxito", JOptionPane.INFORMATION_MESSAGE);
			cambiosSinGuardar = false;
			frame.dispose();
			ctrlPresentacion.mostrarPaginaPrincipal(nombreUsuario);

		} catch (Exception ex) {
			JOptionPane.showMessageDialog(panel, "Error al guardar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		}
	}

	/**
	 * Sale de la vista con confirmación si hay cambios sin guardar.
	 */
	protected void salir() {
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

	// ==================== MÉTODOS a implementar los hijos ====================

	/**
	 * Obtiene título de ventana.
	 * 
	 * @return Título específico de la vista
	 */
	protected abstract String getTituloVentana();

	/**
	 * Obtiene texto del botón de guardar.
	 * 
	 * @return Texto del botón
	 */
	protected abstract String getTextoBotonGuardar();

	/**
	 * Obtiene mensaje de éxito al guardar.
	 * 
	 * @return Mensaje de confirmación
	 */
	protected abstract String getMensajeExito();

	/**
	 * Obtiene respuesta existente del usuario para una pregunta (null si no existe).
	 * 
	 * @param pregunta Pregunta
	 * @return Respuesta existente o null
	 */
	protected abstract Respuesta obtenerRespuestaExistente(Pregunta pregunta);

	/**
	 * Crea panel de respuesta según tipo de pregunta y respuesta existente.
	 * 
	 * @param pregunta Pregunta
	 * @param existente Respuesta existente (null si no hay)
	 * @return Panel de respuesta
	 */
	protected abstract PanelRespuesta crearPanelRespuesta(Pregunta pregunta, Respuesta existente);

	/**
	 * Procesa y guarda una respuesta (crear nueva o modificar existente).
	 * 
	 * @param idPregunta ID de pregunta
	 * @param respuesta Valor de respuesta
	 * @param idRespExistente ID de respuesta existente (-1 si no existe)
	 */
	protected abstract void procesarRespuesta(int idPregunta, Object respuesta, int idRespExistente);

	// ==================== INTERFAZ COMÚN ====================

	/**
	 * Interfaz común para paneles de respuesta.
	 */
	protected interface PanelRespuesta {
		/** Obtiene panel visual */
		JPanel getPanel();
		
		/** Obtiene respuesta capturada */
		Object getRespuesta();
		
		/** Verifica si hay respuesta válida */
		boolean tieneRespuesta();
		
		/** Obtiene ID de respuesta existente (-1 si no existe) */
		int getIdRespuesta();
	}
}
