package presentation.views;

import presentation.CtrlPresentacion;
import presentation.components.ComponenteBase;
import domain.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;

/**
 * Vista para crear o modificar encuesta.
 * 
 * Permite editar título, añadir preguntas de diferentes tipos,
 * modificar preguntas existentes y guardar cambios.
 * 
 * RESPONSABILIDADES:
 * - Editar título de encuesta
 * - Añadir preguntas (libre, numérica, opción única, opción múltiple)
 * - Modificar/eliminar preguntas existentes
 * - Validar y guardar cambios
 * 
 * PATRONES APLICADOS:
 * - Template Method: Hereda de ComponenteBase
 * - Strategy: Diferentes editores según tipo de pregunta
 * 
 * @author PROP Cluster XX
 */
public class VistaModificarEncuesta extends ComponenteBase {
    
    private CtrlPresentacion ctrlPresentacion;
    private JFrame frame;
    private int idEncuesta; // -1 si es nueva
    private String nombreUsuario;
    
    private JTextField txtTitulo;
    private JPanel panelPreguntas;
    private List<PanelPregunta> preguntasEditables;
    private List<PreguntaExistente> preguntasExistentes; // Preguntas ya guardadas
    private boolean cambiosSinGuardar;
    
    /**
     * Constructor para crear nueva encuesta.
     * 
     * @param ctrlPresentacion Controlador
     * @param nombreUsuario Usuario creador
     */
    public VistaModificarEncuesta(CtrlPresentacion ctrlPresentacion, String nombreUsuario) {
        this(ctrlPresentacion, nombreUsuario, -1);
    }
    
    /**
     * Constructor para modificar encuesta existente.
     * 
     * @param ctrlPresentacion Controlador
     * @param nombreUsuario Usuario
     * @param idEncuesta ID de encuesta a modificar
     */
    public VistaModificarEncuesta(CtrlPresentacion ctrlPresentacion, String nombreUsuario, int idEncuesta) {
        super();
        this.ctrlPresentacion = ctrlPresentacion;
        this.nombreUsuario = nombreUsuario;
        this.idEncuesta = idEncuesta;
        this.preguntasEditables = new ArrayList<>();
        this.preguntasExistentes = new ArrayList<>();
        this.cambiosSinGuardar = false;
        inicializar();
    }
    
    /**
     * Inicializa componentes.
     */
    private void inicializar() {
        String titulo = idEncuesta == -1 ? "Crear Encuesta" : "Modificar Encuesta";
        frame = new JFrame(titulo);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                salir();
            }
        });
        frame.setSize(900, 700);
        frame.setLocationRelativeTo(null);
        
        panel.setLayout(new BorderLayout());
        
        // Panel superior: Botón salir y título
        JPanel panelSuperior = new JPanel(new BorderLayout());
        panelSuperior.setBackground(COLOR_BG);
        
        JButton btnSalir = createStyledButton("← Salir", COLOR_SECONDARY);
        btnSalir.addActionListener(e -> salir());
        panelSuperior.add(btnSalir, BorderLayout.WEST);
        
        panel.add(panelSuperior, BorderLayout.NORTH);
        
        // Panel central scrollable
        JPanel panelCentral = new JPanel();
        panelCentral.setLayout(new BoxLayout(panelCentral, BoxLayout.Y_AXIS));
        panelCentral.setBackground(COLOR_BG);
        
        // Título de encuesta (editable)
        JPanel panelTitulo = crearPanelTitulo();
        panelCentral.add(panelTitulo);
        panelCentral.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Botón añadir pregunta
        JPanel panelAnadir = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panelAnadir.setBackground(COLOR_BG);
        JButton btnAnadirPregunta = createStyledButton("+ Añadir Pregunta", COLOR_SUCCESS);
        btnAnadirPregunta.addActionListener(e -> mostrarMenuTiposPregunta());
        panelAnadir.add(btnAnadirPregunta);
        panelCentral.add(panelAnadir);
        
        // Panel de preguntas
        panelPreguntas = new JPanel();
        panelPreguntas.setLayout(new BoxLayout(panelPreguntas, BoxLayout.Y_AXIS));
        panelPreguntas.setBackground(COLOR_BG);
        panelCentral.add(panelPreguntas);
        
        // Botón guardar
        JPanel panelGuardar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelGuardar.setBackground(COLOR_BG);
        JButton btnGuardar = createStyledButton("Guardar Cambios", COLOR_PRIMARY);
        btnGuardar.addActionListener(e -> guardarEncuesta());
        panelGuardar.add(btnGuardar);
        panelCentral.add(panelGuardar);
        
        JScrollPane scroll = new JScrollPane(panelCentral);
        panel.add(scroll, BorderLayout.CENTER);
        
        frame.add(panel);
        
        // Cargar datos si es modificación
        if (idEncuesta != -1) {
            cargarDatosEncuesta();
        }
    }
    
    /**
     * Crea panel para editar título.
     */
    private JPanel crearPanelTitulo() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBackground(Color.WHITE);
        p.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_SECONDARY),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        
        JLabel lbl = createStyledLabel("Título:", FONT_NORMAL);
        txtTitulo = createStyledTextField(40);
        txtTitulo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent e) {
                cambiosSinGuardar = true;
            }
        });
        
        p.add(lbl, BorderLayout.WEST);
        p.add(txtTitulo, BorderLayout.CENTER);
        
        return p;
    }
    
    /**
     * Muestra menú desplegable para seleccionar tipo de pregunta.
     */
    private void mostrarMenuTiposPregunta() {
        String[] tipos = {"Libre", "Numérica", "Opción Única", "Opción Múltiple"};
        String seleccion = (String) JOptionPane.showInputDialog(
            frame,
            "Selecciona el tipo de pregunta:",
            "Añadir Pregunta",
            JOptionPane.QUESTION_MESSAGE,
            null,
            tipos,
            tipos[0]
        );
        
        if (seleccion != null) {
            anadirPregunta(seleccion);
            cambiosSinGuardar = true;
        }
    }
    
    /**
     * Añade nueva pregunta según tipo.
     */
    private void anadirPregunta(String tipo) {
        PanelPregunta panelPregunta = null;
        
        switch (tipo) {
            case "Libre":
                panelPregunta = new PanelPreguntaLibre();
                break;
            case "Numérica":
                panelPregunta = new PanelPreguntaNumerica();
                break;
            case "Opción Única":
                panelPregunta = new PanelPreguntaOpcionUnica();
                break;
            case "Opción Múltiple":
                panelPregunta = new PanelPreguntaOpcionMultiple();
                break;
        }
        
        if (panelPregunta != null) {
            preguntasEditables.add(panelPregunta);
            panelPreguntas.add(panelPregunta.getPanel());
            panelPreguntas.add(Box.createRigidArea(new Dimension(0, 10)));
            panelPreguntas.revalidate();
            panelPreguntas.repaint();
        }
    }
    
    /**
     * Carga datos de encuesta existente.
     */
    private void cargarDatosEncuesta() {
        try {
            // Cargar título
            String titulo = ctrlPresentacion.obtenerTituloEncuesta(idEncuesta);
            txtTitulo.setText(titulo);
            
            // Cargar preguntas existentes
            List<Pregunta> preguntas = ctrlPresentacion.getPreguntasEncuesta(idEncuesta);
            for (Pregunta p : preguntas) {
                PreguntaExistente pe = new PreguntaExistente(p);
                preguntasExistentes.add(pe);
                panelPreguntas.add(pe.getPanel());
                panelPreguntas.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error al cargar encuesta: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Guarda encuesta (crea o modifica).
     */
    private void guardarEncuesta() {
        String titulo = txtTitulo.getText().trim();
        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(frame, "El título no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Crear o actualizar encuesta
            if (idEncuesta == -1) {
                idEncuesta = ctrlPresentacion.crearEncuesta(nombreUsuario, titulo);
            } else {
                ctrlPresentacion.modificarTituloEncuesta(idEncuesta, titulo);
            }
            
            // Guardar solo las NUEVAS preguntas (no las que ya existen)
            for (PanelPregunta pp : preguntasEditables) {
                pp.guardar(idEncuesta);
            }
            
            cambiosSinGuardar = false;
            JOptionPane.showMessageDialog(frame, "Encuesta guardada correctamente");
            frame.dispose();
            ctrlPresentacion.mostrarPaginaPrincipal(nombreUsuario);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Sale con confirmación si hay cambios.
     */
    private void salir() {
        if (cambiosSinGuardar) {
            int respuesta = JOptionPane.showConfirmDialog(
                frame,
                "Hay cambios sin guardar. ¿Deseas salir de todos modos?",
                "Cambios sin guardar",
                JOptionPane.YES_NO_OPTION
            );
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
        }
        frame.dispose();
        ctrlPresentacion.mostrarPaginaPrincipal(nombreUsuario);
    }
    
    public void mostrar() {
        frame.setVisible(true);
    }
    
    // ==================== CLASES INTERNAS PARA EDICIÓN DE PREGUNTAS ====================
    
    /**
     * Clase para representar preguntas ya existentes en la BD.
     */
    private class PreguntaExistente {
        private Pregunta pregunta;
        private JPanel panel;
        
        public PreguntaExistente(Pregunta p) {
            this.pregunta = p;
            construirPanel();
        }
        
        private void construirPanel() {
            panel = new JPanel(new BorderLayout());
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createLineBorder(COLOR_PRIMARY, 2));
            
            // Panel contenido (enunciado + info específica)
            JPanel panelContenido = new JPanel();
            panelContenido.setLayout(new BoxLayout(panelContenido, BoxLayout.Y_AXIS));
            panelContenido.setBackground(Color.WHITE);
            panelContenido.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            
            // Enunciado
            JLabel lblEnunciado = createStyledLabel(pregunta.getEnunciado(), FONT_SUBTITLE);
            panelContenido.add(lblEnunciado);
            
            // Información específica según tipo
            if (pregunta instanceof PreguntaLibre) {
                JLabel lbl = createStyledLabel("Tipo: Pregunta Libre", FONT_SMALL);
                panelContenido.add(lbl);
            } 
            else if (pregunta instanceof PreguntaNumerica) {
                PreguntaNumerica pn = (PreguntaNumerica) pregunta;
                JLabel lbl = createStyledLabel(
                    String.format("Tipo: Pregunta Numérica (Rango: %d a %d)", pn.getRangoInf(), pn.getRangoSup()), 
                    FONT_SMALL
                );
                panelContenido.add(lbl);
            } 
            else if (pregunta instanceof PreguntaOpcionUnica) {
                PreguntaOpcionUnica pou = (PreguntaOpcionUnica) pregunta;
                String opciones = String.join(", ", pou.getOpciones());
                JLabel lbl = createStyledLabel(
                    String.format("Tipo: Opción Única%s - Opciones: %s", 
                        pou.esOrdenada() ? " (Ordenada)" : "", opciones), 
                    FONT_SMALL
                );
                panelContenido.add(lbl);
            } 
            else if (pregunta instanceof PreguntaOpcionMultiple) {
                PreguntaOpcionMultiple pom = (PreguntaOpcionMultiple) pregunta;
                String opciones = String.join(", ", pom.getOpciones());
                JLabel lbl = createStyledLabel(
                    String.format("Tipo: Opción Múltiple (Máx: %d) - Opciones: %s", 
                        pom.getNumMaxOpc(), opciones), 
                    FONT_SMALL
                );
                panelContenido.add(lbl);
            }
            
            panel.add(panelContenido, BorderLayout.CENTER);
            
            // Botón eliminar
            JButton btnEliminar = createStyledButton("Eliminar", COLOR_DANGER);
            btnEliminar.setMaximumSize(new Dimension(100, 30));
            btnEliminar.addActionListener(e -> eliminarPreguntaExistente());
            
            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panelBotones.setBackground(Color.WHITE);
            panelBotones.add(btnEliminar);
            
            panel.add(panelBotones, BorderLayout.EAST);
        }
        
        private void eliminarPreguntaExistente() {
            int confirm = JOptionPane.showConfirmDialog(
                frame,
                "¿Eliminar esta pregunta y todas sus respuestas asociadas?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    ctrlPresentacion.eliminarPregunta(pregunta.getIdPregunta());
                    preguntasExistentes.remove(this);
                    panelPreguntas.remove(panel);
                    panelPreguntas.revalidate();
                    panelPreguntas.repaint();
                    cambiosSinGuardar = true;
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, "Error al eliminar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
        
        public JPanel getPanel() {
            return panel;
        }
    }
    
    /**
     * Interfaz común para paneles de pregunta editables.
     */
    private abstract class PanelPregunta {
        protected JPanel panel;
        
        public JPanel getPanel() { return panel; }
        public abstract void guardar(int idEncuesta);
    }
    
    /**
     * Panel para editar pregunta libre.
     */
    private class PanelPreguntaLibre extends PanelPregunta {
        private JTextField txtEnunciado;
        
        public PanelPreguntaLibre() {
            panel = new JPanel(new BorderLayout());
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createLineBorder(COLOR_SECONDARY));
            
            JLabel lbl = createStyledLabel("Pregunta Libre:", FONT_SUBTITLE);
            txtEnunciado = createStyledTextField(40);
            
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p.setBackground(Color.WHITE);
            p.add(lbl);
            p.add(txtEnunciado);
            
            panel.add(p, BorderLayout.CENTER);
        }
        
        @Override
        public void guardar(int idEncuesta) {
            String enunciado = txtEnunciado.getText().trim();
            if (!enunciado.isEmpty()) {
                ctrlPresentacion.anadirPreguntaLibre(idEncuesta, enunciado);
            }
        }
    }
    
    /**
     * Panel para editar pregunta numérica.
     */
    private class PanelPreguntaNumerica extends PanelPregunta {
        private JTextField txtEnunciado;
        private JSpinner spinMin;
        private JSpinner spinMax;
        
        public PanelPreguntaNumerica() {
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createLineBorder(COLOR_SECONDARY));
            
            JLabel lbl = createStyledLabel("Pregunta Numérica:", FONT_SUBTITLE);
            txtEnunciado = createStyledTextField(40);
            
            spinMin = new JSpinner(new SpinnerNumberModel(0, -1000, 1000, 1));
            spinMax = new JSpinner(new SpinnerNumberModel(100, -1000, 1000, 1));
            
            JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p1.setBackground(Color.WHITE);
            p1.add(lbl);
            p1.add(txtEnunciado);
            
            JPanel p2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p2.setBackground(Color.WHITE);
            p2.add(createStyledLabel("Rango: ", FONT_NORMAL));
            p2.add(spinMin);
            p2.add(createStyledLabel(" a ", FONT_NORMAL));
            p2.add(spinMax);
            
            panel.add(p1);
            panel.add(p2);
        }
        
        @Override
        public void guardar(int idEncuesta) {
            String enunciado = txtEnunciado.getText().trim();
            if (!enunciado.isEmpty()) {
                double min = (Integer) spinMin.getValue();
                double max = (Integer) spinMax.getValue();
                ctrlPresentacion.anadirPreguntaNumerica(idEncuesta, enunciado, min, max);
            }
        }
    }
    
    /**
     * Panel para editar pregunta de opción única.
     */
    private class PanelPreguntaOpcionUnica extends PanelPregunta {
        private JTextField txtEnunciado;
        private JCheckBox chkOrdenada;
        private List<JTextField> txtOpciones;
        private JPanel panelOpciones;
        
        public PanelPreguntaOpcionUnica() {
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createLineBorder(COLOR_SECONDARY));
            
            txtOpciones = new ArrayList<>();
            
            JLabel lbl = createStyledLabel("Pregunta Opción Única:", FONT_SUBTITLE);
            txtEnunciado = createStyledTextField(40);
            chkOrdenada = new JCheckBox("Ordenada");
            
            JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p1.setBackground(Color.WHITE);
            p1.add(lbl);
            p1.add(txtEnunciado);
            p1.add(chkOrdenada);
            
            panelOpciones = new JPanel();
            panelOpciones.setLayout(new BoxLayout(panelOpciones, BoxLayout.Y_AXIS));
            panelOpciones.setBackground(Color.WHITE);
            
            // 2 opciones por defecto
            anadirOpcion();
            anadirOpcion();
            
            JButton btnAnadir = createStyledButton("+ Opción", COLOR_SUCCESS);
            btnAnadir.addActionListener(e -> anadirOpcion());
            
            panel.add(p1);
            panel.add(panelOpciones);
            panel.add(btnAnadir);
        }
        
        private void anadirOpcion() {
            JTextField txt = createStyledTextField(30);
            txtOpciones.add(txt);
            
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p.setBackground(Color.WHITE);
            p.add(createStyledLabel("Opción " + txtOpciones.size() + ":", FONT_NORMAL));
            p.add(txt);
            
            panelOpciones.add(p);
            panelOpciones.revalidate();
            panelOpciones.repaint();
        }
        
        @Override
        public void guardar(int idEncuesta) {
            String enunciado = txtEnunciado.getText().trim();
            if (!enunciado.isEmpty()) {
                List<String> opciones = new ArrayList<>();
                for (JTextField txt : txtOpciones) {
                    String opt = txt.getText().trim();
                    if (!opt.isEmpty()) opciones.add(opt);
                }
                if (!opciones.isEmpty()) {
                    boolean ordenada = chkOrdenada.isSelected();
                    ctrlPresentacion.anadirPreguntaOpcionUnica(idEncuesta, enunciado, opciones, ordenada);
                }
            }
        }
    }
    
    /**
     * Panel para editar pregunta de opción múltiple.
     */
    private class PanelPreguntaOpcionMultiple extends PanelPregunta {
        private JTextField txtEnunciado;
        private JSpinner spinNumMax;
        private List<JTextField> txtOpciones;
        private JPanel panelOpciones;
        
        public PanelPreguntaOpcionMultiple() {
            panel = new JPanel();
            panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
            panel.setBackground(Color.WHITE);
            panel.setBorder(BorderFactory.createLineBorder(COLOR_SECONDARY));
            
            txtOpciones = new ArrayList<>();
            
            JLabel lbl = createStyledLabel("Pregunta Opción Múltiple:", FONT_SUBTITLE);
            txtEnunciado = createStyledTextField(40);
            spinNumMax = new JSpinner(new SpinnerNumberModel(2, 2, 10, 1));
            
            JPanel p1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p1.setBackground(Color.WHITE);
            p1.add(lbl);
            p1.add(txtEnunciado);
            p1.add(createStyledLabel("Máx opciones:", FONT_NORMAL));
            p1.add(spinNumMax);
            
            panelOpciones = new JPanel();
            panelOpciones.setLayout(new BoxLayout(panelOpciones, BoxLayout.Y_AXIS));
            panelOpciones.setBackground(Color.WHITE);
            
            // 2 opciones por defecto
            anadirOpcion();
            anadirOpcion();
            
            JButton btnAnadir = createStyledButton("+ Opción", COLOR_SUCCESS);
            btnAnadir.addActionListener(e -> anadirOpcion());
            
            panel.add(p1);
            panel.add(panelOpciones);
            panel.add(btnAnadir);
        }
        
        private void anadirOpcion() {
            JTextField txt = createStyledTextField(30);
            txtOpciones.add(txt);
            
            JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT));
            p.setBackground(Color.WHITE);
            p.add(createStyledLabel("Opción " + txtOpciones.size() + ":", FONT_NORMAL));
            p.add(txt);
            
            panelOpciones.add(p);
            panelOpciones.revalidate();
            panelOpciones.repaint();
        }
        
        @Override
        public void guardar(int idEncuesta) {
            String enunciado = txtEnunciado.getText().trim();
            if (!enunciado.isEmpty()) {
                List<String> opciones = new ArrayList<>();
                for (JTextField txt : txtOpciones) {
                    String opt = txt.getText().trim();
                    if (!opt.isEmpty()) opciones.add(opt);
                }
                if (!opciones.isEmpty()) {
                    int numMax = (Integer) spinNumMax.getValue();
                    ctrlPresentacion.anadirPreguntaOpcionMultiple(idEncuesta, enunciado, opciones, numMax);
                }
            }
        }
    }
}
