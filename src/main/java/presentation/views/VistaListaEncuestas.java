package presentation.views;

import presentation.CtrlPresentacion;
import presentation.components.ComponenteBase;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.File;

/**
 * Vista principal de la aplicación (lista de encuestas).
 * 
 * Muestra todas las encuestas con filtros y buscador.
 * Permite crear, responder, modificar y ver estadísticas según permisos.
 * 
 * RESPONSABILIDADES:
 * - Mostrar lista de encuestas con filtros
 * - Proporcionar buscador por palabras clave
 * - Mostrar opciones según permisos del usuario
 * - Navegar a otras vistas
 * 
 * ATRIBUTOS PRINCIPALES:
 * - nombreUsuario: Usuario en sesión
 * - panelEncuestas: Panel scrollable con encuestas
 * - filtros: Filtros activos
 * 
 * MÉTODOS PRINCIPALES:
 * - cargarEncuestas(): Carga y muestra encuestas
 * - aplicarFiltros(): Aplica filtros seleccionados
 * 
 * @author Xiangjun Yepan, Peiyu Wang
 */
public class VistaListaEncuestas extends ComponenteBase {
    
    /** Controlador de presentación */
    private CtrlPresentacion ctrlPresentacion;
    
    /** Ventana principal */
    private JFrame frame;
    
    /** Nombre del usuario en sesión */
    private String nombreUsuario;
    
    /** Panel scrollable con encuestas */
    private JPanel panelEncuestas;
    
    /** Campo de texto para búsqueda */
    private JTextField txtBuscar;
    
    /** ComboBox filtro de autor */
    private JComboBox<String> cmbAutorFilter; // "Todas", "Mis Encuestas", "Encuestas ajenas"
    
    /** ComboBox filtro de respuestas */
    private JComboBox<String> cmbRespuestaFilter; // "Todas", "Respondidas", "No respondidas"
    
    /** Botón para ver todas las encuestas */
    private JButton btnVerTodas;
    
    ///** Indica si hay filtro de búsqueda activo */
    //private boolean hayFiltroActivo = false;
    
    /**
     * Crea vista principal.
     * 
     * @param ctrlPresentacion Controlador de presentación
     * @param nombreUsuario Usuario en sesión
     */
    public VistaListaEncuestas(CtrlPresentacion ctrlPresentacion, String nombreUsuario) {
        super();
        this.ctrlPresentacion = ctrlPresentacion;
        this.nombreUsuario = nombreUsuario;
        inicializar();
    }
    
    /**
     * Inicializa componentes de la vista.
     */
    private void inicializar() {
        frame = crearFrameEstandar("Encuestas - Principal");
        configurarCierreVentana(frame, this::confirmarSalida);
        
        // Layout principal
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
      
        
        JLabel lblUsuario = createStyledLabel(nombreUsuario + " ", FONT_NORMAL);
        
        // Icono de menú (⋮ o ≡) - click abre popup
        JButton btnMenu = new JButton("⋮");
        btnMenu.setFont(new Font("Arial", Font.BOLD, 16));
        btnMenu.setBackground(COLOR_PRIMARY);
        btnMenu.setForeground(Color.WHITE);
        btnMenu.setFocusPainted(false);
        btnMenu.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        btnMenu.setPreferredSize(new Dimension(40, 35));
        
        btnMenu.addActionListener(e -> mostrarMenuUsuario(btnMenu));
        JPanel panelUsuario = new JPanel();
        panelUsuario.setBackground(COLOR_BG);
        panelUsuario.add(lblUsuario);
        panelUsuario.add(btnMenu);
        
        // Panel de controles (buscador, filtros, crear)
        JPanel panelControles = new JPanel();
        panelControles.setLayout(new BoxLayout(panelControles, BoxLayout.Y_AXIS));
        panelControles.setBackground(COLOR_BG);
        
        // Primera fila: Buscador y botón crear
        JPanel panelBuscar = new JPanel();
        panelBuscar.setBackground(COLOR_BG);
        panelBuscar.setLayout(new BoxLayout(panelBuscar, BoxLayout.X_AXIS));
        panelBuscar.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel lblBuscar = createStyledLabel("Buscar:", FONT_NORMAL);
        txtBuscar = createStyledTextField(30);
        // limit max width so it doesn't expand and push other controls
        txtBuscar.setMaximumSize(new Dimension(450, txtBuscar.getPreferredSize().height));

        JButton btnBuscar = createStyledButton("Buscar", COLOR_PRIMARY);
        btnBuscar.setPreferredSize(new Dimension(100, 30));
        btnBuscar.setMaximumSize(new Dimension(100, 30));
        btnBuscar.addActionListener(e -> buscarEncuestas());
        
        // Botón "Ver todas" (inicialmente oculto)
        btnVerTodas = createStyledButton("Ver todas", COLOR_WARNING);
        btnVerTodas.setVisible(false);
        btnVerTodas.setPreferredSize(new Dimension(120, 30));
        btnVerTodas.setMaximumSize(new Dimension(120, 30));
        btnVerTodas.addActionListener(e -> verTodas());
        
        // Botón crear encuesta con menú desplegable
        JButton btnCrear = createStyledButton("+ Crear Encuesta", COLOR_SUCCESS);
        btnCrear.setPreferredSize(new Dimension(160, 30));
        btnCrear.setMaximumSize(new Dimension(160, 30));
        btnCrear.addActionListener(e -> mostrarOpcionesCrearEncuesta());
        
        panelBuscar.add(lblBuscar);
        panelBuscar.add(Box.createRigidArea(new Dimension(8,0)));
        panelBuscar.add(txtBuscar);
        panelBuscar.add(Box.createRigidArea(new Dimension(8,0)));
        panelBuscar.add(btnBuscar);
        panelBuscar.add(Box.createRigidArea(new Dimension(8,0)));
        panelBuscar.add(btnVerTodas);
        panelBuscar.add(Box.createRigidArea(new Dimension(8,0)));
        panelBuscar.add(btnCrear);
        
        // Segunda fila: Filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setBackground(COLOR_BG);
        
        JLabel lblFiltros = createStyledLabel("Filtros:", FONT_NORMAL);
        // Combo para autor: Todas / Mis Encuestas / Encuestas ajenas
        String[] opcionesAutor = new String[]{"Todas", "Mis Encuestas", "Encuestas ajenas"};
        cmbAutorFilter = new JComboBox<>(opcionesAutor);
        cmbAutorFilter.setSelectedIndex(0);
        cmbAutorFilter.setBackground(COLOR_BG);
        cmbAutorFilter.setFont(FONT_NORMAL);
        cmbAutorFilter.addActionListener(e -> cargarEncuestas());

        // Combo para respuesta: Todas / Respondidas / No respondidas
        String[] opcionesRespuesta = new String[]{"Todas", "Respondidas", "No respondidas"};
        cmbRespuestaFilter = new JComboBox<>(opcionesRespuesta);
        cmbRespuestaFilter.setSelectedIndex(0);
        cmbRespuestaFilter.setBackground(COLOR_BG);
        cmbRespuestaFilter.setFont(FONT_NORMAL);
        cmbRespuestaFilter.addActionListener(e -> cargarEncuestas());

    panelFiltros.add(lblFiltros);
    panelFiltros.add(cmbAutorFilter);
    panelFiltros.add(cmbRespuestaFilter);
        
        // Top row: buscador a la izquierda, usuario a la derecha (para que no baje al cambiar resultados)
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBackground(COLOR_BG);
        panelTop.add(panelBuscar, BorderLayout.WEST);
        panelTop.add(panelUsuario, BorderLayout.EAST);

        panelControles.add(panelTop);
        panelControles.add(panelFiltros);
        
        panel.add(panelControles, BorderLayout.NORTH);
        
        // Panel de encuestas (scrollable)
        panelEncuestas = new JPanel();
        panelEncuestas.setLayout(new BoxLayout(panelEncuestas, BoxLayout.Y_AXIS));
        panelEncuestas.setBackground(COLOR_BG);
        
        JScrollPane scroll = new JScrollPane(panelEncuestas);
        scroll.setBorder(BorderFactory.createEmptyBorder());
        scroll.getVerticalScrollBar().setUnitIncrement(16);
        panel.add(scroll, BorderLayout.CENTER);
        
        frame.add(panel);
        
        // Cargar encuestas iniciales
        cargarEncuestas();
    }
    
    /**
     * Busca encuestas por palabra clave.
     */
    private void buscarEncuestas() {
        String busqueda = txtBuscar.getText().trim().toLowerCase();
        if (busqueda.isEmpty()) {
            verTodas();
            return;
        }
        
        panelEncuestas.removeAll();
        //hayFiltroActivo = true;
        btnVerTodas.setVisible(true);
        
        // Obtener todas las encuestas y filtrar por título
        List<Integer> todas = ctrlPresentacion.obtenerTodasEncuestas();
        int encontradas = 0;
        
        for (int idEncuesta : todas) {
            String titulo = ctrlPresentacion.obtenerTituloEncuesta(idEncuesta).toLowerCase();
            if (titulo.contains(busqueda)) {
                JPanel panelEncuesta = crearPanelEncuesta(idEncuesta);
                panelEncuestas.add(panelEncuesta);
                panelEncuestas.add(Box.createRigidArea(new Dimension(0, 10)));
                encontradas++;
            }
        }
        
        if (encontradas == 0) {
            JLabel lblVacio = createStyledLabel("No se encontraron encuestas con ese término", FONT_NORMAL);
            lblVacio.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelEncuestas.add(lblVacio);
        }
        
        panelEncuestas.revalidate();
        panelEncuestas.repaint();
    }
    
    /**
     * Restaura todas las encuestas (quita búsqueda).
     */
    private void verTodas() {
        // Restaurar búsqueda y recargar lista completa aplicando filtros actuales
        txtBuscar.setText("");
        //hayFiltroActivo = false;
        btnVerTodas.setVisible(false);
        cargarEncuestas();
    }

    /**
     * Carga encuestas aplicando los filtros seleccionados.
     */
    private void cargarEncuestas() {
        panelEncuestas.removeAll();

        // Leer filtros desde los combo boxes
        String autorSel = (String) cmbAutorFilter.getSelectedItem();
        String respSel = (String) cmbRespuestaFilter.getSelectedItem();

        List<Integer> idsEncuestas = ctrlPresentacion.obtenerTodasEncuestas();

        int mostradas = 0;
        for (int idEncuesta : idsEncuestas) {
            String autor = ctrlPresentacion.obtenerAutorEncuesta(idEncuesta);
            boolean respondida = ctrlPresentacion.haRespondido(nombreUsuario, idEncuesta);

            // Filtro por autor
            if (!"Todas".equals(autorSel)) {
                if ("Mis Encuestas".equals(autorSel) && !nombreUsuario.equals(autor)) continue;
                if ("Encuestas ajenas".equals(autorSel) && nombreUsuario.equals(autor)) continue;
            }

            // Filtro por respondidas/no respondidas
            if (!"Todas".equals(respSel)) {
                if ("Respondidas".equals(respSel) && !respondida) continue;
                if ("No respondidas".equals(respSel) && respondida) continue;
            }

            JPanel panelEncuesta = crearPanelEncuesta(idEncuesta);
            panelEncuestas.add(panelEncuesta);
            panelEncuestas.add(Box.createRigidArea(new Dimension(0, 10)));
            mostradas++;
        }
        if (mostradas == 0) {
            JLabel lblVacio = createStyledLabel("No hay encuestas que cumplan los filtros seleccionados", FONT_NORMAL);
            lblVacio.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelEncuestas.add(lblVacio);
        }

        panelEncuestas.revalidate();
        panelEncuestas.repaint();
    }
    
    /**
     * Crea panel individual para una encuesta.
     * 
     * @param idEncuesta ID de la encuesta
     * @return Panel de la encuesta
     */
    private JPanel crearPanelEncuesta(int idEncuesta) {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARY, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        
        // Información de la encuesta (izquierda)
        String titulo = ctrlPresentacion.obtenerTituloEncuesta(idEncuesta);
        String autor = ctrlPresentacion.obtenerAutorEncuesta(idEncuesta);
        boolean respondida = ctrlPresentacion.haRespondido(nombreUsuario, idEncuesta);
        
        JPanel panelInfo = new JPanel(new GridLayout(2, 1));
        panelInfo.setBackground(Color.WHITE);
        JLabel lblTitulo = createStyledLabel(titulo + (respondida ? " ✓" : ""), FONT_SUBTITLE);
        JLabel lblAutor = createStyledLabel("Autor: " + autor, FONT_SMALL);
        lblAutor.setForeground(COLOR_SECONDARY);
        panelInfo.add(lblTitulo);
        panelInfo.add(lblAutor);
        p.add(panelInfo, BorderLayout.WEST);
        
        // Botones (derecha)
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelBotones.setBackground(Color.WHITE);
        
        boolean esAutor = autor.equals(nombreUsuario);
        
        if (esAutor) {
            JButton btnEstadisticas = createStyledButton("Estadísticas", COLOR_PRIMARY);
            JButton btnModificar = createStyledButton("Modificar", COLOR_WARNING);
            JButton btnVerRespuestas = createStyledButton("Ver Respuestas", COLOR_SECONDARY);
            JButton btnImportarResp = createStyledButton("Importar Respuestas", COLOR_SECONDARY);
            
            btnEstadisticas.addActionListener(e -> verEstadisticas(idEncuesta));
            btnModificar.addActionListener(e -> modificarEncuesta(idEncuesta));
            btnVerRespuestas.addActionListener(e -> verRespuestas(idEncuesta));
            btnImportarResp.addActionListener(e -> importarRespuestasEncuesta(idEncuesta));
            
            panelBotones.add(btnEstadisticas);
            panelBotones.add(btnModificar);
            panelBotones.add(btnVerRespuestas);
            panelBotones.add(btnImportarResp);
        }
        
        JButton btnResponder = respondida ? 
            createStyledButton("Modificar respuesta", COLOR_WARNING) :
            createStyledButton("Responder", COLOR_SUCCESS);
            
        if (respondida) {
            btnResponder.addActionListener(e -> modificarResponderEncuesta(idEncuesta));
        } else {
            btnResponder.addActionListener(e -> responderEncuesta(idEncuesta));
        }
        //btnResponder.addActionListener(e -> responderEncuesta(idEncuesta));
        panelBotones.add(btnResponder);
        
        p.add(panelBotones, BorderLayout.EAST);
        
        return p;
    }
    
    /**
     * Muestra opciones para crear encuesta.
     */
    private void mostrarOpcionesCrearEncuesta() {
        String[] opciones = {"Nueva Encuesta", "Importar desde Fichero"};
        int eleccion = JOptionPane.showOptionDialog(
            null,
            "¿Cómo deseas crear la encuesta?",
            "Crear Encuesta",
            JOptionPane.DEFAULT_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            opciones,
            opciones[0]
        );
        
        if (eleccion == 0) {
            frame.dispose();
            ctrlPresentacion.mostrarCrearEncuesta(nombreUsuario);
        } else if (eleccion == 1) {
            importarEncuesta();
        }
    }
    
    /**
     * Importa encuesta desde fichero.
     */
    private void importarEncuesta() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar fichero de encuesta");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos JSON", "json");
        fileChooser.setFileFilter(filter);
        
        int resultado = fileChooser.showOpenDialog(panel);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            File fichero = fileChooser.getSelectedFile();
            try {
                int idEncuesta = ctrlPresentacion.importarEncuesta(fichero);
                JOptionPane.showMessageDialog(frame, 
                    "Encuesta importada correctamente (ID: " + idEncuesta + ")", 
                    "Éxito", 
                    JOptionPane.INFORMATION_MESSAGE);
                cargarEncuestas();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, 
                    "Error al importar: " + ex.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Navega a la vista de modificar encuesta.
     * 
     * @param id ID de encuesta a modificar
     */
    private void modificarEncuesta(int id) {
        frame.dispose();
        ctrlPresentacion.mostrarModificarEncuesta(nombreUsuario, id);
    }
    
    /**
     * Navega a la vista de responder encuesta.
     * 
     * @param id ID de encuesta a responder
     */
    private void responderEncuesta(int id) {
        frame.dispose();
        ctrlPresentacion.mostrarResponderEncuesta(nombreUsuario, id);
    }
    
    /**
     * Navega a la vista de modificar respuesta de encuesta.
     * 
     * @param id ID de encuesta con respuesta a modificar
     */
    private void modificarResponderEncuesta(int id) {
        frame.dispose();
        ctrlPresentacion.mostrarModificarResponderEncuesta(nombreUsuario, id);
    }
    
    /**
     * Muestra vista de respuestas de la encuesta.
     * 
     * @param id ID de encuesta
     */
    private void verRespuestas(int id) {
        ctrlPresentacion.mostrarVerRespuestas(nombreUsuario, id);
    }
    
    /**
     * Muestra vista de estadísticas y clustering.
     * 
     * @param id ID de encuesta
     */
    private void verEstadisticas(int id) {
        ctrlPresentacion.mostrarEstadisticas(nombreUsuario, id);
    }
    
    /**
     * Solicita nueva contraseña y la cambia.
     */
    private void cambiarContrasena() {
        String nuevaPass = JOptionPane.showInputDialog(frame, "Nueva contraseña:");
        String confirmacion = JOptionPane.showInputDialog(frame, "Confirmar contraseña:");
        if (nuevaPass != null && !nuevaPass.trim().isEmpty()) {
            try {
                ctrlPresentacion.cambiarContrasena(nuevaPass, confirmacion);
                JOptionPane.showMessageDialog(frame, "Contraseña cambiada correctamente");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Cierra sesión y vuelve al login.
     */
    private void cerrarSesion() {
        ctrlPresentacion.cerrarSesion();
        frame.dispose();
        ctrlPresentacion.mostrarLogin();
    }
    
    /**
     * Confirma salida de la aplicación.
     */
    private void confirmarSalida() {
        int opcion = JOptionPane.showConfirmDialog(
            null,
            "¿Estás seguro de que deseas salir?",
            "Confirmar Salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (opcion == JOptionPane.YES_OPTION) {
            ctrlPresentacion.cerrarSesion();
            System.exit(0);
        }
    }

    /**
     * Muestra menú de usuario al hacer clic en el icono.
     */
    private void mostrarMenuUsuario(JButton btnMenu) {
        JPopupMenu menu = new JPopupMenu();
        
        JMenuItem itemCambiarPass = new JMenuItem("Cambiar contraseña");
        itemCambiarPass.addActionListener(e -> cambiarContrasena());
        
        JMenuItem itemCerrarSesion = new JMenuItem("Cerrar sesión");
        itemCerrarSesion.addActionListener(e -> cerrarSesion());
        
        menu.add(itemCambiarPass);
        menu.addSeparator();
        menu.add(itemCerrarSesion);
        
        // Mostrar el popup justo debajo del botón de usuario
        menu.show(btnMenu, 0, btnMenu.getHeight());
    }
    
    /**
     * Muestra la vista.
     */
    public void mostrar() {
        frame.setVisible(true);
    }

    /**
     * Importa respuestas desde fichero CSV para una encuesta específica.
     * Abre un JFileChooser y delega en CtrlPresentacion.importarRespuestas.
     * 
     * @param idEncuesta ID de encuesta para la que se importan respuestas
     */
    private void importarRespuestasEncuesta(int idEncuesta) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Seleccionar fichero de respuestas (CSV)");
        FileNameExtensionFilter filter = new FileNameExtensionFilter("Archivos CSV", "csv");
        fileChooser.setFileFilter(filter);

        int resultado = fileChooser.showOpenDialog(panel);
        if (resultado == JFileChooser.APPROVE_OPTION) {
            java.io.File fichero = fileChooser.getSelectedFile();
            try {
                ctrlPresentacion.importarRespuestas(fichero);
                JOptionPane.showMessageDialog(frame,
                    "Respuestas importadas correctamente.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE);
                // recargar lista por si la importación cambió el estado
                cargarEncuestas();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame,
                    "Error al importar respuestas: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}
