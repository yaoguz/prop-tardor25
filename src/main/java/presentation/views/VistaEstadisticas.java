package presentation.views;

import presentation.CtrlPresentacion;
import presentation.components.ComponenteBase;
import domain.*;
import domain.algorithm.estructuras.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.concurrent.*;

/**
 * Vista para ejecutar clustering y ver estadísticas de una encuesta.
 * 
 * Permite al autor ejecutar algoritmos de clustering sobre las respuestas
 * y visualizar los perfiles resultantes.
 * 
 * CARACTERÍSTICAS:
 * - Selección de k (número de clusters)
 * - Selección de algoritmo (K-Means, K-Means++, K-Medoids)
 * - Ejecución automática al cambiar parámetros
 * - Visualización de perfiles con representantes y miembros
 * - Botón para ver respuestas de miembros
 * 
 * @author Peiyu Wang
 */
public class VistaEstadisticas extends ComponenteBase {

	/** Controlador de presentación */
	private CtrlPresentacion ctrlPresentacion;
	
	/** Nombre del usuario en sesión */
	private String nombreUsuario;
	
	/** ID de encuesta */
	private int idEncuesta;
	
	/** Ventana principal */
	private JFrame frame;
	
	/** Panel con perfiles resultantes de clustering */
	private JPanel panelPerfiles;
	
	/** Spinner para número de clusters */
	private JSpinner spinK;
	
	/** ComboBox para selección de algoritmo */
	private JComboBox<String> comboAlgoritmo;
	
	/** Botón de ejecución de clustering */
	private JButton btnEjecutar;
	
	/** Barra de progreso para ejecución */
	private JProgressBar progressBar;
	
	/** Label de estado de ejecución */
	private JLabel lblEstado;
	
	/** Label para mostrar coeficiente de silhouette */
	private JLabel lblSilhouette;

	/** Worker para ejecución asíncrona de clustering */
	private SwingWorker<ResultadoClustering, Void> clusteringWorker;
	
	/** Futuro para permitir cancelación y timeout */
	private Future<ResultadoClustering> clusteringFuture;
	
	/** Timeout de ejecución de clustering en segundos */
	private static final int CLUSTER_TIMEOUT_SECONDS = 30;

	/**
	 * Constructor.
	 * 
	 * @param ctrlPresentacion Controlador de presentación
	 * @param nombreUsuario Usuario
	 * @param idEncuesta ID de encuesta
	 */
	public VistaEstadisticas(CtrlPresentacion ctrlPresentacion, String nombreUsuario, int idEncuesta) {
		this.ctrlPresentacion = ctrlPresentacion;
		this.nombreUsuario = nombreUsuario;
		this.idEncuesta = idEncuesta;
	}

	/**
	 * Muestra la vista.
	 */
	public void mostrar() {
		frame = crearFrameEstandar("Estadísticas y Clustering");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		// Panel principal
		JPanel panelPrincipal = new JPanel(new BorderLayout(0, 10));
		panelPrincipal.setBackground(COLOR_BG);
		panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		String titulo = ctrlPresentacion.obtenerTituloEncuesta(idEncuesta);
		JLabel lblTitulo = createStyledLabel("Estadísticas: " + titulo, FONT_TITLE);
		panelPrincipal.add(lblTitulo, BorderLayout.NORTH);

		// Panel de configuración
		JPanel panelConfiguracion = crearPanelConfiguracion();
		panelPrincipal.add(panelConfiguracion, BorderLayout.NORTH);

		// Panel de perfiles (scrollable)
		panelPerfiles = new JPanel();
		panelPerfiles.setLayout(new BoxLayout(panelPerfiles, BoxLayout.Y_AXIS));
		panelPerfiles.setBackground(COLOR_BG);

		JScrollPane scrollPerfiles = new JScrollPane(panelPerfiles);
		scrollPerfiles.setBorder(BorderFactory.createEmptyBorder());
		scrollPerfiles.getVerticalScrollBar().setUnitIncrement(16);
		panelPrincipal.add(scrollPerfiles, BorderLayout.CENTER);

		// Botón volver
		JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panelBotones.setBackground(COLOR_BG);
		JButton btnVolver = createStyledButton("Volver", COLOR_PRIMARY);
		btnVolver.addActionListener(e -> salir());
		panelBotones.add(btnVolver);
		panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

		frame.add(panelPrincipal);
		
		// Ejecutar clustering inicial
		ejecutarClustering();
		
		frame.setVisible(true);
	}

	/**
	 * Cierra la vista y vuelve a la lista de encuestas.
	 */
	private void salir() {
		frame.dispose();
		ctrlPresentacion.mostrarPaginaPrincipal(nombreUsuario);
	}

	/**
	 * Crea panel de configuración con controles de clustering.
	 * 
	 * @return Panel de configuración
	 */
	private JPanel crearPanelConfiguracion() {
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(COLOR_PRIMARY, 2),
			BorderFactory.createEmptyBorder(10, 15, 10, 15)
		));

		// Selector de k
		JLabel lblK = createStyledLabel("Número de clusters (k):", FONT_NORMAL);
		
		// Valor por defecto: 2
		int kInicial = 2;
		SpinnerNumberModel modelK = new SpinnerNumberModel(kInicial, 2, 20, 1);
		spinK = new JSpinner(modelK);
		spinK.setFont(FONT_NORMAL);
		spinK.addChangeListener(e -> ejecutarClustering());

		// Selector de algoritmo
		JLabel lblAlgoritmo = createStyledLabel("Algoritmo:", FONT_NORMAL);
		
		String[] algoritmos = {"K-Means", "K-Means++", "K-Medoids"};
		comboAlgoritmo = new JComboBox<>(algoritmos);
		comboAlgoritmo.setFont(FONT_NORMAL);
		comboAlgoritmo.addActionListener(e -> ejecutarClustering());

		// Botón ejecutar / cancelar
		btnEjecutar = createStyledButton("Ejecutar Clustering", COLOR_SUCCESS);
		btnEjecutar.addActionListener(e -> {
			if (clusteringWorker != null && !clusteringWorker.isDone()) {
				// cancelar ejecución en curso
				if (clusteringFuture != null) clusteringFuture.cancel(true);
				clusteringWorker.cancel(true);
				lblEstado.setText("Cancelando...");
			} else {
				ejecutarClustering();
			}
		});

		// Estado / indicador
		progressBar = new JProgressBar();
		progressBar.setVisible(false);
		progressBar.setIndeterminate(true);
		progressBar.setPreferredSize(new Dimension(120, 16));
		lblEstado = createStyledLabel("", FONT_SMALL);

		JPanel estadoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
		estadoPanel.setBackground(Color.WHITE);
		estadoPanel.add(progressBar);
		estadoPanel.add(lblEstado);
		
		// Label de coeficiente de silhouette
		lblSilhouette = createStyledLabel("", FONT_NORMAL);
		lblSilhouette.setForeground(COLOR_PRIMARY);

		panel.add(lblK);
		panel.add(spinK);
		panel.add(Box.createRigidArea(new Dimension(20, 0)));
		panel.add(lblAlgoritmo);
		panel.add(comboAlgoritmo);
		panel.add(Box.createRigidArea(new Dimension(20, 0)));
		panel.add(btnEjecutar);
		panel.add(Box.createRigidArea(new Dimension(10, 0)));
		panel.add(estadoPanel);
		panel.add(Box.createRigidArea(new Dimension(20, 0)));
		panel.add(lblSilhouette);

		return panel;
	}

	/**
	 * Ejecuta el clustering con los parámetros actuales.
	 */
	private void ejecutarClustering() {
    panelPerfiles.removeAll();

	// Comprobar que hay al menos 2 usuarios que hayan respondido la encuesta
	Map<Pregunta, Set<Respuesta>> todasRespuestas = ctrlPresentacion.getRespuestasEncuesta(idEncuesta);
	Set<String> usuariosRespondieron = new HashSet<>();
	if (todasRespuestas != null) {
		for (Set<Respuesta> conjunto : todasRespuestas.values()) {
			if (conjunto == null) continue;
			for (Respuesta r : conjunto) {
				if (r == null) continue;
				String u = r.getNomUsuario();
				if (u != null) usuariosRespondieron.add(u);
			}
		}
	}
	if (usuariosRespondieron.size() < 2) {
		JLabel lblError = createStyledLabel("Se necesitan al menos 2 respuestas en la encuesta para ejecutar clustering.", FONT_NORMAL);
		lblError.setForeground(COLOR_DANGER);
		lblError.setAlignmentX(Component.CENTER_ALIGNMENT);
		panelPerfiles.add(Box.createVerticalGlue());
		panelPerfiles.add(lblError);
		panelPerfiles.add(Box.createVerticalGlue());
		panelPerfiles.revalidate();
		panelPerfiles.repaint();
		// Asegurar controles disponibles
		if (spinK != null) spinK.setEnabled(true);
		if (comboAlgoritmo != null) comboAlgoritmo.setEnabled(true);
		if (btnEjecutar != null) btnEjecutar.setEnabled(true);
		return;
	}

    // Crear panel para los componentes centrados (progressBar + lblEstado)
    JPanel panelCentro = new JPanel();
    panelCentro.setLayout(new BoxLayout(panelCentro, BoxLayout.Y_AXIS)); // Asegura alineación vertical
    panelCentro.setAlignmentX(Component.CENTER_ALIGNMENT);

    // Añadir barra de progreso
    progressBar.setIndeterminate(true);  // Habilitar la animación de la barra de progreso
    progressBar.setVisible(true);        // Asegura que sea visible
    panelCentro.add(progressBar);

    // Añadir espacio entre la barra de progreso y el texto
    panelCentro.add(Box.createRigidArea(new Dimension(0, 10)));

    // Añadir texto "Ejecutando clustering..."
    lblEstado.setText("Ejecutando clustering...");
    lblEstado.setAlignmentX(Component.CENTER_ALIGNMENT);
    panelCentro.add(lblEstado);

    // Añadir el panelCentro al panelPerfiles
    panelPerfiles.setLayout(new BoxLayout(panelPerfiles, BoxLayout.Y_AXIS));  // Asegura alineación vertical
    panelPerfiles.add(Box.createVerticalGlue());  // Añadir espacio antes del panelCentro
    panelPerfiles.add(panelCentro);               // Añadir el panelCentro que contiene la barra de progreso y el texto
    panelPerfiles.add(Box.createVerticalGlue());  // Añadir espacio después del panelCentro

    // Actualizar el panelPerfiles
    panelPerfiles.revalidate();
    panelPerfiles.repaint();

    // Deshabilitar controles mientras se ejecuta
    spinK.setEnabled(false);
    comboAlgoritmo.setEnabled(false);
    if (btnEjecutar != null) btnEjecutar.setEnabled(false);

    if (clusteringWorker != null && !clusteringWorker.isDone()) {
        if (clusteringFuture != null) clusteringFuture.cancel(true);
        clusteringWorker.cancel(true);
    }

    clusteringWorker = new SwingWorker<ResultadoClustering, Void>() {
        @Override
        protected ResultadoClustering doInBackground() throws Exception {
            int k = (int) spinK.getValue();
            String nombreUI = (String) comboAlgoritmo.getSelectedItem();
            ExecutorService executor = Executors.newSingleThreadExecutor();
            try {
                Callable<ResultadoClustering> task = () -> ctrlPresentacion.ejecutarClusteringDesdeUI(idEncuesta, k, nombreUI);
                clusteringFuture = executor.submit(task);
                return clusteringFuture.get(CLUSTER_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (TimeoutException te) {
                if (clusteringFuture != null) clusteringFuture.cancel(true);
                throw new Exception("El clustering ha excedido el tiempo máximo de " + CLUSTER_TIMEOUT_SECONDS + "s y fue cancelado.");
            } finally {
                executor.shutdownNow();
            }
        }

        @Override
        protected void done() {
            try {
                ResultadoClustering resultado = get();
                if (resultado == null || resultado.getPerfiles().isEmpty()) {
                    JLabel lblVacio = createStyledLabel("No se pudieron generar perfiles. Verifica que haya suficientes respuestas.", FONT_NORMAL);
                    lblVacio.setAlignmentX(Component.CENTER_ALIGNMENT);
                    panelPerfiles.add(lblVacio);
                    lblSilhouette.setText("");
                } else {
                    mostrarPerfiles(resultado.getPerfiles());
                    // Mostrar coeficiente de silhouette
                    double calidad = resultado.getCalidad();
                    lblSilhouette.setText(String.format("Coeficiente de Silhouette: %.3f", calidad));
                }
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                JLabel lblError = createStyledLabel("Ejecución interrumpida", FONT_NORMAL);
                lblError.setForeground(COLOR_DANGER);
                lblError.setAlignmentX(Component.CENTER_ALIGNMENT);
                panelPerfiles.add(lblError);
                lblSilhouette.setText("");
            } catch (java.util.concurrent.CancellationException ce) {
                JLabel lblError = createStyledLabel("Clustering cancelado por el usuario.", FONT_NORMAL);
                lblError.setForeground(COLOR_DANGER);
                lblError.setAlignmentX(Component.CENTER_ALIGNMENT);
                panelPerfiles.add(lblError);
                lblSilhouette.setText("");
            } catch (java.util.concurrent.ExecutionException ee) {
                String msg = (ee.getCause() != null) ? ee.getCause().getMessage() : ee.getMessage();
                JLabel lblError = createStyledLabel("Error: " + msg, FONT_NORMAL);
                lblError.setForeground(COLOR_DANGER);
                lblError.setAlignmentX(Component.CENTER_ALIGNMENT);
                panelPerfiles.add(lblError);
                lblSilhouette.setText("");
            } finally {
                // Restaurar estado de controles
                progressBar.setVisible(false);
                lblEstado.setText("");
                spinK.setEnabled(true);
                comboAlgoritmo.setEnabled(true);
                if (btnEjecutar != null) btnEjecutar.setEnabled(true);
                panelPerfiles.revalidate();
                panelPerfiles.repaint();
            }
        }
    };
    clusteringWorker.execute();
}
	
	/**
	 * Muestra perfiles resultantes de clustering.
	 * 
	 * @param perfiles Lista de perfiles
	 */
	private void mostrarPerfiles(List<Perfil> perfiles) {
		// Asegurar panel limpio y evitar solapamientos
		panelPerfiles.removeAll();
		for (int i = 0; i < perfiles.size(); i++) {
			Perfil perfil = perfiles.get(i);
			JPanel panelPerfil = crearPanelPerfil(i + 1, perfil);
			// Alineación para que los paneles se apilen correctamente
			panelPerfil.setAlignmentX(Component.LEFT_ALIGNMENT);
			panelPerfiles.add(panelPerfil);
			panelPerfiles.add(Box.createRigidArea(new Dimension(0, 15)));
		}
		panelPerfiles.revalidate();
		panelPerfiles.repaint();
	}

	/**
	 * Crea panel individual para un perfil.
	 * 
	 * @param numero Número de perfil
	 * @param perfil Perfil a mostrar
	 * @return Panel del perfil
	 */
	private JPanel crearPanelPerfil(int numero, Perfil perfil) {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(COLOR_PRIMARY, 3),
			BorderFactory.createEmptyBorder(15, 15, 15, 15)
		));

		// Título del perfil
		JLabel lblTitulo = createStyledLabel("Perfil " + numero, FONT_SUBTITLE);
		lblTitulo.setForeground(COLOR_PRIMARY);
		panel.add(lblTitulo, BorderLayout.NORTH);

		// Información del perfil
		JPanel panelInfo = new JPanel();
		panelInfo.setLayout(new BoxLayout(panelInfo, BoxLayout.Y_AXIS));
		panelInfo.setBackground(Color.WHITE);

		// Representante
		String representante = ctrlPresentacion.obtenerRepresentantePerfil(perfil);
		JLabel lblRepresentante = createStyledLabel("Representante: " + representante, FONT_NORMAL);
		lblRepresentante.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
		panelInfo.add(lblRepresentante);

		// Miembros
		List<String> miembros = ctrlPresentacion.obtenerMiembrosPerfil(perfil);
		JLabel lblMiembros = createStyledLabel("Miembros (" + miembros.size() + "):", FONT_NORMAL);
		panelInfo.add(lblMiembros);

		JPanel panelMiembros = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		panelMiembros.setBackground(Color.WHITE);
		panelMiembros.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 0));
		panelMiembros.setAlignmentX(Component.LEFT_ALIGNMENT);
		panelMiembros.setMaximumSize(new Dimension(600, Integer.MAX_VALUE));

		for (String miembro : miembros) {
			JLabel lblMiembro = new JLabel(miembro);
			lblMiembro.setFont(FONT_SMALL);
			lblMiembro.setOpaque(true);
			lblMiembro.setBackground(new Color(230, 240, 250));
			lblMiembro.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(COLOR_PRIMARY, 1),
				BorderFactory.createEmptyBorder(3, 8, 3, 8)
			));
			panelMiembros.add(lblMiembro);
		}

		panelInfo.add(panelMiembros);

		panel.add(panelInfo, BorderLayout.CENTER);

		// Botón para ver respuestas de miembros
		JPanel panelBoton = new JPanel(new FlowLayout(FlowLayout.RIGHT));
		panelBoton.setBackground(Color.WHITE);
		
		JButton btnVerRespuestas = createStyledButton("Ver Respuestas Miembros", COLOR_PRIMARY);
		btnVerRespuestas.setFont(FONT_SMALL);
		btnVerRespuestas.addActionListener(e -> ctrlPresentacion.mostrarRespuestasMiembros(nombreUsuario, idEncuesta, perfil, representante));
		
		panelBoton.add(btnVerRespuestas);
		panel.add(panelBoton, BorderLayout.SOUTH);

		return panel;
	}
}
