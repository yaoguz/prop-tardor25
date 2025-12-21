package presentation.views;

import presentation.CtrlPresentacion;
import presentation.components.ComponenteBase;
import domain.*;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

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
 * @author Xiangjun Yepan, Peiyu Wang
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
        frame = crearFrameEstandar(titulo);
        configurarCierreVentana(frame, this::salir);
        
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
        
        // Botón guardar + eliminar
        JPanel panelGuardar = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        panelGuardar.setBackground(COLOR_BG);

        // Botón eliminar (a la izquierda de Guardar)
        JButton btnEliminarEncuesta = createStyledButton("Eliminar Encuesta", COLOR_DANGER);
        btnEliminarEncuesta.addActionListener(e -> {
            // Si la encuesta es nueva (no guardada aún), simplemente confirmar salida
            if (idEncuesta == -1) {
                int resp = JOptionPane.showConfirmDialog(
                    panel,
                    "La encuesta no ha sido guardada. ¿Deseas cancelar la creación y volver?",
                    "Cancelar creación",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                if (resp == JOptionPane.YES_OPTION) {
                    frame.dispose();
                    ctrlPresentacion.mostrarPaginaPrincipal(nombreUsuario);
                }
                return;
            }

            int confirm = JOptionPane.showConfirmDialog(
                panel,
                "¿Eliminar esta encuesta y todas sus preguntas y respuestas asociadas?",
                "Confirmar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                try {
                    ctrlPresentacion.eliminarEncuesta(idEncuesta);
                    JOptionPane.showMessageDialog(panel, "Encuesta eliminada correctamente");
                    frame.dispose();
                    ctrlPresentacion.mostrarPaginaPrincipal(nombreUsuario);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(panel, "Error al eliminar encuesta: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton btnGuardar = createStyledButton("Guardar Cambios", COLOR_PRIMARY);
        btnGuardar.addActionListener(e -> guardarEncuesta());

        // Añadir botones: primero Eliminar, luego Guardar
        panelGuardar.add(btnEliminarEncuesta);
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
            panel,
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
            JOptionPane.showMessageDialog(panel, "Error al cargar encuesta: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Guarda encuesta (crea o modifica).
     */
    private void guardarEncuesta() {
        String titulo = txtTitulo.getText().trim();
        if (titulo.isEmpty()) {
            JOptionPane.showMessageDialog(panel, "El título no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            // Crear o actualizar encuesta
            if (idEncuesta == -1) {
                idEncuesta = ctrlPresentacion.crearEncuesta(nombreUsuario, titulo);
            } else {
                ctrlPresentacion.modificarTituloEncuesta(idEncuesta, titulo);
            }
            
            // Aplicar cambios pendientes en preguntas existentes (eliminaciones/modificaciones)
            for (PreguntaExistente pe : new ArrayList<>(preguntasExistentes)) {
                try {
                    if (pe.marcadoParaEliminar) {
                        // eliminar en dominio
                        ctrlPresentacion.eliminarPregunta(pe.pregunta.getIdPregunta());
                        // eliminar de la lista local
                        preguntasExistentes.remove(pe);
                    } else if (pe.nuevoEnunciado != null || pe.atributosModificados != null) {
                        // aplicar modificación en dominio
                        String enun = (pe.nuevoEnunciado != null) ? pe.nuevoEnunciado : pe.pregunta.getEnunciado();
                        java.util.Map<String,Object> attrs = (pe.atributosModificados != null) ? pe.atributosModificados : new java.util.HashMap<>();
                        ctrlPresentacion.modificarPregunta(pe.pregunta.getIdPregunta(), enun, attrs);
                    }
                } catch (Exception ex) {
                    // mostrar error pero continuar con el resto
                    JOptionPane.showMessageDialog(panel, "Error aplicando cambios a pregunta " + pe.pregunta.getIdPregunta() + ": " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }

            // Guardar solo las NUEVAS preguntas validando campos (no las que ya existen)
            int preguntasGuardadas = 0;
            for (PanelPregunta pp : preguntasEditables) {
                try {
                    pp.guardar(idEncuesta);
                    preguntasGuardadas++;
                } catch (Exception e) {
                    // Pregunta con campos vacíos o inválidos - no se guarda
                    JOptionPane.showMessageDialog(panel, 
                        "Error en pregunta: " + e.getMessage() + "\nEsta pregunta no se ha guardado, hay campos inválidos.",
                        "Advertencia", JOptionPane.WARNING_MESSAGE);
                }
            }
            
            cambiosSinGuardar = false;
            String mensaje = "Encuesta guardada correctamente";
            if (preguntasGuardadas < preguntasEditables.size()) {
                mensaje += "\n(" + preguntasGuardadas + " de " + preguntasEditables.size() + " preguntas guardadas)";
            }
            JOptionPane.showMessageDialog(panel, mensaje);
            frame.dispose();
            ctrlPresentacion.mostrarPaginaPrincipal(nombreUsuario);
        } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Sale con confirmación si hay cambios.
     */
    private void salir() {
        if (cambiosSinGuardar) {
            int respuesta = JOptionPane.showConfirmDialog(
                null,
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
        private JLabel lblEnunciado;
        // flags/pendings: no aplicar cambios a dominio hasta guardarEncuesta()
        private boolean marcadoParaEliminar = false;
        private String nuevoEnunciado = null;
        private java.util.Map<String, Object> atributosModificados = null;
        
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
            lblEnunciado = createStyledLabel(pregunta.getEnunciado(), FONT_SUBTITLE);
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

            // Botones editar / eliminar (acciones locales hasta guardar)
            JButton btnEliminar = createStyledButton("Eliminar", COLOR_DANGER);
            btnEliminar.setMaximumSize(new Dimension(100, 30));
            btnEliminar.addActionListener(e -> marcarEliminarPreguntaExistente());

            JButton btnEditar = createStyledButton("Editar", COLOR_PRIMARY);
            btnEditar.setMaximumSize(new Dimension(100, 30));
            btnEditar.addActionListener(e -> mostrarEditorPreguntaExistente(this));

            JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            panelBotones.setBackground(Color.WHITE);
            panelBotones.add(btnEditar);
            panelBotones.add(btnEliminar);

            panel.add(panelBotones, BorderLayout.EAST);
        }
        
        private void marcarEliminarPreguntaExistente() {
            int confirm = JOptionPane.showConfirmDialog(
                null,
                "Marcar esta pregunta para eliminarla al guardar. ¿Continuar?",
                "Marcar eliminación",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );

            if (confirm == JOptionPane.YES_OPTION) {
                // No eliminar en dominio aún; marcar y ocultar de la UI
                marcadoParaEliminar = true;
                panel.setVisible(false);
                cambiosSinGuardar = true;
                panelPreguntas.revalidate();
                panelPreguntas.repaint();
            }
        }
        
        public JPanel getPanel() {
            return panel;
        }
    }

    /**
     * Muestra un diálogo modal para editar una pregunta existente.
     */
    private void mostrarEditorPreguntaExistente(PreguntaExistente pe) {
        Pregunta p = pe.pregunta;
        JDialog dialog = new JDialog(frame, "Editar pregunta", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
        dialog.setResizable(false);
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(null);

        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Enunciado
        JPanel pEnun = new JPanel(new FlowLayout(FlowLayout.LEFT));
        pEnun.setBackground(Color.WHITE);
        pEnun.add(createStyledLabel("Enunciado:", FONT_NORMAL));
        JTextField txtEnun = createStyledTextField(40);
        txtEnun.setText(p.getEnunciado());
        pEnun.add(txtEnun);
        content.add(pEnun);

        // Panel para atributos específicos
        JPanel atributosPanel = new JPanel();
        atributosPanel.setLayout(new BoxLayout(atributosPanel, BoxLayout.Y_AXIS));
        atributosPanel.setBackground(Color.WHITE);

        // Campos según tipo
        if (p instanceof PreguntaLibre) {
            // no hay más campos
            atributosPanel.add(createStyledLabel("Tipo: Pregunta Libre", FONT_SMALL));
        } else if (p instanceof PreguntaNumerica) {
            PreguntaNumerica pn = (PreguntaNumerica) p;
            JPanel pRange = new JPanel(new FlowLayout(FlowLayout.LEFT));
            pRange.setBackground(Color.WHITE);
            pRange.add(createStyledLabel("Rango mínimo:", FONT_NORMAL));
            JSpinner spMin = new JSpinner(new SpinnerNumberModel(pn.getRangoInf(), -100000, 100000, 1));
            pRange.add(spMin);
            pRange.add(createStyledLabel("Rango máximo:", FONT_NORMAL));
            JSpinner spMax = new JSpinner(new SpinnerNumberModel(pn.getRangoSup(), -100000, 100000, 1));
            pRange.add(spMax);
            atributosPanel.add(pRange);
        } else if (p instanceof PreguntaOpcionUnica) {
            PreguntaOpcionUnica pou = (PreguntaOpcionUnica) p;
            JCheckBox chkOrden = new JCheckBox("Ordenada");
            chkOrden.setSelected(pou.esOrdenada());
            atributosPanel.add(chkOrden);

            JPanel panelOpc = new JPanel();
            panelOpc.setLayout(new BoxLayout(panelOpc, BoxLayout.Y_AXIS));
            panelOpc.setBackground(Color.WHITE);
            java.util.List<JTextField> campos = new ArrayList<>();
            for (String opt : pou.getOpciones()) {
                JTextField t = createStyledTextField(30);
                t.setText(opt);
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
                row.setBackground(Color.WHITE);
                row.add(createStyledLabel("Opción:", FONT_NORMAL));
                row.add(t);
                panelOpc.add(row);
                campos.add(t);
            }
            JButton btnAddOpt = createStyledButton("+ Opción", COLOR_SUCCESS);
            btnAddOpt.addActionListener(e -> {
                JTextField t = createStyledTextField(30);
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
                row.setBackground(Color.WHITE);
                row.add(createStyledLabel("Opción:", FONT_NORMAL));
                row.add(t);
                panelOpc.add(row);
                campos.add(t);
                panelOpc.revalidate();
                panelOpc.repaint();
            });
            atributosPanel.add(panelOpc);
            atributosPanel.add(btnAddOpt);
            // store campos in client property for later retrieval
            atributosPanel.putClientProperty("opcionesFields", campos);
            atributosPanel.putClientProperty("chkOrden", chkOrden);
        } else if (p instanceof PreguntaOpcionMultiple) {
            PreguntaOpcionMultiple pom = (PreguntaOpcionMultiple) p;
            JPanel pMax = new JPanel(new FlowLayout(FlowLayout.LEFT));
            pMax.setBackground(Color.WHITE);
            pMax.add(createStyledLabel("Máx opciones seleccionables:", FONT_NORMAL));
            JSpinner spMaxOpc = new JSpinner(new SpinnerNumberModel(pom.getNumMaxOpc(), 1, 100, 1));
            pMax.add(spMaxOpc);
            atributosPanel.add(pMax);

            JPanel panelOpc = new JPanel();
            panelOpc.setLayout(new BoxLayout(panelOpc, BoxLayout.Y_AXIS));
            panelOpc.setBackground(Color.WHITE);
            java.util.List<JTextField> campos = new ArrayList<>();
            for (String opt : pom.getOpciones()) {
                JTextField t = createStyledTextField(30);
                t.setText(opt);
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
                row.setBackground(Color.WHITE);
                row.add(createStyledLabel("Opción:", FONT_NORMAL));
                row.add(t);
                panelOpc.add(row);
                campos.add(t);
            }
            JButton btnAddOpt = createStyledButton("+ Opción", COLOR_SUCCESS);
            btnAddOpt.addActionListener(e -> {
                JTextField t = createStyledTextField(30);
                JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT));
                row.setBackground(Color.WHITE);
                row.add(createStyledLabel("Opción:", FONT_NORMAL));
                row.add(t);
                panelOpc.add(row);
                campos.add(t);
                panelOpc.revalidate();
                panelOpc.repaint();
            });
            atributosPanel.add(panelOpc);
            atributosPanel.add(btnAddOpt);
            atributosPanel.putClientProperty("opcionesFields", campos);
            atributosPanel.putClientProperty("spMaxOpc", spMaxOpc);
        }

        content.add(atributosPanel);

        // Botones
        JPanel botPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        botPanel.setBackground(Color.WHITE);
        JButton btnSave = createStyledButton("Guardar", COLOR_PRIMARY);
        JButton btnCancel = createStyledButton("Cancelar", COLOR_SECONDARY);
        botPanel.add(btnCancel);
        botPanel.add(btnSave);
        content.add(botPanel);

        btnCancel.addActionListener(e -> dialog.dispose());

        btnSave.addActionListener(e -> {
            String nuevoEnun = txtEnun.getText().trim();
            if (nuevoEnun.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "El enunciado no puede estar vacío", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Map<String, Object> atributos = new HashMap<>();
            if (p instanceof PreguntaNumerica) {
                JSpinner spMin = (JSpinner) ((JPanel) atributosPanel.getComponent(0)).getComponent(1);
                JSpinner spMax = (JSpinner) ((JPanel) atributosPanel.getComponent(0)).getComponent(3);
                atributos.put("min", (Integer) spMin.getValue());
                atributos.put("max", (Integer) spMax.getValue());
            } else if (p instanceof PreguntaOpcionUnica) {
                @SuppressWarnings("unchecked") java.util.List<JTextField> campos = (java.util.List<JTextField>) atributosPanel.getClientProperty("opcionesFields");
                java.util.List<String> opciones = new ArrayList<>();
                for (JTextField t : campos) {
                    String v = t.getText().trim(); if (!v.isEmpty()) opciones.add(v);
                }
                JCheckBox chk = (JCheckBox) atributosPanel.getClientProperty("chkOrden");
                atributos.put("opciones", opciones);
                atributos.put("ordenada", chk != null && chk.isSelected());
            } else if (p instanceof PreguntaOpcionMultiple) {
                @SuppressWarnings("unchecked") java.util.List<JTextField> campos = (java.util.List<JTextField>) atributosPanel.getClientProperty("opcionesFields");
                java.util.List<String> opciones = new ArrayList<>();
                for (JTextField t : campos) {
                    String v = t.getText().trim(); if (!v.isEmpty()) opciones.add(v);
                }
                JSpinner spMaxOpc = (JSpinner) atributosPanel.getClientProperty("spMaxOpc");
                atributos.put("opciones", opciones);
                atributos.put("numMaxOpc", (Integer) (spMaxOpc != null ? spMaxOpc.getValue() : 2));
            }

            // Guardar cambios localmente en el objeto PreguntaExistente; no tocar dominio hasta Guardar
            pe.nuevoEnunciado = nuevoEnun;
            pe.atributosModificados = atributos;
            // actualizar vista local para reflejar cambio pendiente
            pe.lblEnunciado.setText(nuevoEnun + " (pendiente)");
            cambiosSinGuardar = true;
            dialog.dispose();
        });

        dialog.add(content);
        dialog.setVisible(true);
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
