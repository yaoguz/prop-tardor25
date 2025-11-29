package presentation.views;

import presentation.CtrlPresentacion;
import presentation.components.ComponenteBase;
import presentation.components.BarraNavegacion;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
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
 * @author PROP Cluster XX
 */
public class VistaListaEncuestas extends ComponenteBase {
    
    private CtrlPresentacion ctrlPresentacion;
    private JFrame frame;
    private String nombreUsuario;
    private JPanel panelEncuestas;
    private JTextField txtBuscar;
    private JCheckBox chkMisEncuestas;
    private JCheckBox chkAjenas;
    private JCheckBox chkRespondidas;
    private JCheckBox chkNoRespondidas;
    private JButton btnVerTodas;
    private boolean hayFiltroActivo = false;
    
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
        frame = new JFrame("Encuestas - Principal");
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        
        // Confirmación de salida
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                confirmarSalida();
            }
        });
        
        // Layout principal
        panel.setLayout(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Barra de navegación
        BarraNavegacion barra = new BarraNavegacion(nombreUsuario);
        barra.setOnCerrarSesion(e -> cerrarSesion());
        barra.setOnCambiarContrasena(e -> cambiarContrasena());
        barra.setOnSalir(e -> confirmarSalida());
        panel.add(barra.getPanel(), BorderLayout.NORTH);
        
        // Panel de controles (buscador, filtros, crear)
        JPanel panelControles = new JPanel();
        panelControles.setLayout(new BoxLayout(panelControles, BoxLayout.Y_AXIS));
        panelControles.setBackground(COLOR_BG);
        
        // Primera fila: Buscador y botón crear
        JPanel panelBuscar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelBuscar.setBackground(COLOR_BG);
        
        JLabel lblBuscar = createStyledLabel("Buscar:", FONT_NORMAL);
        txtBuscar = createStyledTextField(30);
        
        JButton btnBuscar = createStyledButton("Buscar", COLOR_PRIMARY);
        btnBuscar.addActionListener(e -> buscarEncuestas());
        
        // Botón "Ver todas" (inicialmente oculto)
        btnVerTodas = createStyledButton("Ver todas", COLOR_WARNING);
        btnVerTodas.setVisible(false);
        btnVerTodas.addActionListener(e -> verTodas());
        
        // Botón crear encuesta con menú desplegable
        JButton btnCrear = createStyledButton("+ Crear Encuesta", COLOR_SUCCESS);
        btnCrear.addActionListener(e -> mostrarOpcionesCrearEncuesta());
        
        panelBuscar.add(lblBuscar);
        panelBuscar.add(txtBuscar);
        panelBuscar.add(btnBuscar);
        panelBuscar.add(btnVerTodas);
        panelBuscar.add(Box.createHorizontalGlue());
        panelBuscar.add(btnCrear);
        
        // Segunda fila: Filtros
        JPanel panelFiltros = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        panelFiltros.setBackground(COLOR_BG);
        
        JLabel lblFiltros = createStyledLabel("Filtros:", FONT_NORMAL);
        chkMisEncuestas = new JCheckBox("Mis Encuestas");
        chkAjenas = new JCheckBox("Encuestas ajenas");
        chkRespondidas = new JCheckBox("Respondidas");
        chkNoRespondidas = new JCheckBox("No respondidas");
        
        for (JCheckBox chk : new JCheckBox[]{chkMisEncuestas, chkAjenas, chkRespondidas, chkNoRespondidas}) {
            chk.setBackground(COLOR_BG);
            chk.setFont(FONT_NORMAL);
            chk.addItemListener(e -> cargarEncuestas());
        }
        
        panelFiltros.add(lblFiltros);
        panelFiltros.add(chkMisEncuestas);
        panelFiltros.add(chkAjenas);
        panelFiltros.add(chkRespondidas);
        panelFiltros.add(chkNoRespondidas);
        
        panelControles.add(panelBuscar);
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
        hayFiltroActivo = true;
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
        txtBuscar.setText("");
        hayFiltroActivo = false;
        btnVerTodas.setVisible(false);
        cargarEncuestas();
    }
    
    /**
     * Carga encuestas aplicando filtros.
     */
    private void cargarEncuestas() {
        panelEncuestas.removeAll();
        
        boolean misEncuestas = chkMisEncuestas.isSelected();
        boolean ajenas = chkAjenas.isSelected();
        boolean respondidas = chkRespondidas.isSelected();
        boolean noRespondidas = chkNoRespondidas.isSelected();
        
        // Obtener encuestas del controlador
        List<Integer> idsEncuestas = ctrlPresentacion.obtenerTodasEncuestas();
        
        for (int idEncuesta : idsEncuestas) {
            JPanel panelEncuesta = crearPanelEncuesta(idEncuesta);
            panelEncuestas.add(panelEncuesta);
            panelEncuestas.add(Box.createRigidArea(new Dimension(0, 10)));
        }
        
        if (idsEncuestas.isEmpty()) {
            JLabel lblVacio = createStyledLabel("No hay encuestas disponibles", FONT_NORMAL);
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
            
            btnEstadisticas.addActionListener(e -> verEstadisticas(idEncuesta));
            btnModificar.addActionListener(e -> modificarEncuesta(idEncuesta));
            btnVerRespuestas.addActionListener(e -> verRespuestas(idEncuesta));
            
            panelBotones.add(btnEstadisticas);
            panelBotones.add(btnModificar);
            panelBotones.add(btnVerRespuestas);
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
            frame,
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
        
        int resultado = fileChooser.showOpenDialog(frame);
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
    
    private void modificarEncuesta(int id) {
        frame.dispose();
        ctrlPresentacion.mostrarModificarEncuesta(nombreUsuario, id);
    }
    
    private void responderEncuesta(int id) {
        frame.dispose();
        ctrlPresentacion.mostrarResponderEncuesta(nombreUsuario, id);
    }
    private void modificarResponderEncuesta(int id) {
        frame.dispose();
        ctrlPresentacion.mostrarModificarResponderEncuesta(nombreUsuario, id);
    }
    
    private void verRespuestas(int id) {
        ctrlPresentacion.mostrarVerRespuestas(nombreUsuario, id);
    }
    
    private void verEstadisticas(int id) {
        ctrlPresentacion.mostrarEstadisticas(nombreUsuario, id);
    }
    
    private void cambiarContrasena() {
        String nuevaPass = JOptionPane.showInputDialog(frame, "Nueva contraseña:");
        if (nuevaPass != null && !nuevaPass.trim().isEmpty()) {
            try {
                ctrlPresentacion.cambiarContrasena(nuevaPass);
                JOptionPane.showMessageDialog(frame, "Contraseña cambiada correctamente");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
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
            frame,
            "¿Estás seguro de que deseas salir?",
            "Confirmar Salida",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (opcion == JOptionPane.YES_OPTION) {
            System.exit(0);
        }
    }
    
    /**
     * Muestra la vista.
     */
    public void mostrar() {
        frame.setVisible(true);
    }
}
