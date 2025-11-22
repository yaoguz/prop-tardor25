package presentation.views;

import presentation.CtrlPresentacion;
import presentation.components.ComponenteBase;
import presentation.components.BarraNavegacion;
import domain.*;
import domain.algorithm.estructuras.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

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
 * @author PROP Cluster XX
 */
public class VistaEstadisticas extends ComponenteBase {

	private CtrlPresentacion ctrlPresentacion;
	private String nombreUsuario;
	private int idEncuesta;
	private JFrame frame;
	private JPanel panelPerfiles;
	
	private JSpinner spinK;
	private JComboBox<String> comboAlgoritmo;

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
		frame = new JFrame("Estadísticas y Clustering");
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
        panelPrincipal.add(barra.getPanel(), BorderLayout.NORTH);		// Título
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
		btnVolver.addActionListener(e -> frame.dispose());
		panelBotones.add(btnVolver);
		panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

		frame.add(panelPrincipal);
		
		// Ejecutar clustering inicial
		ejecutarClustering();
		
		frame.setVisible(true);
	}

	/**
	 * Crea panel de configuración de clustering.
	 * 
	 * @return Panel configurado
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
		
		// Valor por defecto: random entre 2 y numRespuestas/2
		// Para simplificar, usamos 2-10
		int kInicial = 2 + (int)(Math.random() * 4); // 2-5
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

		// Botón ejecutar manualmente
		JButton btnEjecutar = createStyledButton("Ejecutar Clustering", COLOR_SUCCESS);
		btnEjecutar.addActionListener(e -> ejecutarClustering());

		panel.add(lblK);
		panel.add(spinK);
		panel.add(Box.createRigidArea(new Dimension(20, 0)));
		panel.add(lblAlgoritmo);
		panel.add(comboAlgoritmo);
		panel.add(Box.createRigidArea(new Dimension(20, 0)));
		panel.add(btnEjecutar);

		return panel;
	}

	/**
	 * Ejecuta el clustering con los parámetros actuales.
	 */
	private void ejecutarClustering() {
		panelPerfiles.removeAll();

		try {
			int k = (int) spinK.getValue();
			String algoritmo = convertirAlgoritmo((String) comboAlgoritmo.getSelectedItem());

			ResultadoClustering resultado = ctrlPresentacion.ejecutarClustering(idEncuesta, k, algoritmo);
			
			if (resultado == null || resultado.getPerfiles().isEmpty()) {
				JLabel lblVacio = createStyledLabel("No se pudieron generar perfiles. Verifica que haya suficientes respuestas.", FONT_NORMAL);
				lblVacio.setAlignmentX(Component.CENTER_ALIGNMENT);
				panelPerfiles.add(lblVacio);
			} else {
				mostrarPerfiles(resultado.getPerfiles());
			}

		} catch (Exception ex) {
			JLabel lblError = createStyledLabel("Error: " + ex.getMessage(), FONT_NORMAL);
			lblError.setForeground(COLOR_DANGER);
			lblError.setAlignmentX(Component.CENTER_ALIGNMENT);
			panelPerfiles.add(lblError);
		}

		panelPerfiles.revalidate();
		panelPerfiles.repaint();
	}

	/**
	 * Convierte nombre de algoritmo de UI a formato de dominio.
	 * 
	 * @param nombreUI Nombre en UI
	 * @return Nombre para dominio
	 */
	private String convertirAlgoritmo(String nombreUI) {
		switch (nombreUI) {
			case "K-Means": return "kmeans";
			case "K-Means++": return "kmeans++";
			case "K-Medoids": return "kmedoids";
			default: return "kmeans";
		}
	}

	/**
	 * Muestra los perfiles resultantes.
	 * 
	 * @param perfiles Lista de perfiles
	 */
	private void mostrarPerfiles(List<Perfil> perfiles) {
		for (int i = 0; i < perfiles.size(); i++) {
			Perfil perfil = perfiles.get(i);
			JPanel panelPerfil = crearPanelPerfil(i + 1, perfil);
			panelPerfiles.add(panelPerfil);
			panelPerfiles.add(Box.createRigidArea(new Dimension(0, 15)));
		}
	}

	/**
	 * Crea panel para un perfil.
	 * 
	 * @param numero Número de perfil
	 * @param perfil Perfil
	 * @return Panel configurado
	 */
	private JPanel crearPanelPerfil(int numero, Perfil perfil) {
		JPanel panel = new JPanel(new BorderLayout(10, 10));
		panel.setBackground(Color.WHITE);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(COLOR_PRIMARY, 3),
			BorderFactory.createEmptyBorder(15, 15, 15, 15)
		));
		panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, panel.getPreferredSize().height));

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
		JLabel lblRepresentante = createStyledLabel("👤 Representante: " + representante, FONT_NORMAL);
		lblRepresentante.setBorder(BorderFactory.createEmptyBorder(5, 0, 10, 0));
		panelInfo.add(lblRepresentante);

		// Miembros
		List<String> miembros = ctrlPresentacion.obtenerMiembrosPerfil(perfil);
		JLabel lblMiembros = createStyledLabel("👥 Miembros (" + miembros.size() + "):", FONT_NORMAL);
		panelInfo.add(lblMiembros);

		JPanel panelMiembros = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
		panelMiembros.setBackground(Color.WHITE);
		panelMiembros.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 0));

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
		btnVerRespuestas.addActionListener(e -> {
			// Mostrar diálogo con lista de miembros
			String[] opciones = miembros.toArray(new String[0]);
			String seleccionado = (String) JOptionPane.showInputDialog(
				frame,
				"Selecciona un miembro para ver sus respuestas:",
				"Respuestas de Miembros",
				JOptionPane.PLAIN_MESSAGE,
				null,
				opciones,
				opciones[0]
			);
			
			if (seleccionado != null) {
				JOptionPane.showMessageDialog(frame, 
					"Funcionalidad de ver respuestas individuales pendiente de implementación.\nMiembro seleccionado: " + seleccionado,
					"Info",
					JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		panelBoton.add(btnVerRespuestas);
		panel.add(panelBoton, BorderLayout.SOUTH);

		return panel;
	}
}
