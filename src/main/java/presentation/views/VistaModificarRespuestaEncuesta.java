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
 * Vista para modificar las respuestas de una encuesta (prefill + editar).
 *
 * Funciona igual que VistaResponderEncuesta, pero carga las respuestas
 * existentes del usuario y permite modificarlas en su lugar.
 *
 * @author PROP Cluster XX
 */
public class VistaModificarRespuestaEncuesta extends ComponenteBase {

    /** Controlador de presentación */
    private CtrlPresentacion ctrlPresentacion;
    
    /** Nombre del usuario en sesión */
    private String nombreUsuario;
    
    /** ID de encuesta a modificar respuestas */
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
     * @param nombreUsuario Usuario en sesión
     * @param idEncuesta ID de encuesta a modificar
     */
    public VistaModificarRespuestaEncuesta(CtrlPresentacion ctrlPresentacion, String nombreUsuario, int idEncuesta) {
        this.ctrlPresentacion = ctrlPresentacion;
        this.nombreUsuario = nombreUsuario;
        this.idEncuesta = idEncuesta;
    }

    /**
     * Muestra la vista.
     */
    public void mostrar() {
        frame = new JFrame("Modificar Respuestas");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salir();
            }
        });

        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 10));
        panelPrincipal.setBackground(COLOR_BG);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

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
        // panelPrincipal.add(barra.getPanel(), BorderLayout.NORTH);

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

        JButton btnGuardar = createStyledButton("Guardar Cambios", COLOR_SUCCESS);
        btnGuardar.addActionListener(e -> guardarRespuestas());

        panelBotones.add(btnCancelar);
        panelBotones.add(btnGuardar);
        panelPrincipal.add(panelBotones, BorderLayout.SOUTH);

        frame.add(panelPrincipal);
        frame.setVisible(true);
    }

    /**
     * Carga las preguntas y, si existen, las respuestas previas del usuario.
     * El comportamiento: prefill con la respuesta del usuario (si existe) para poder modificarla.
     *
     * @param panelPreguntas Panel contenedor
     */
    private void cargarPreguntas(JPanel panelPreguntas) {
        List<Pregunta> preguntas = ctrlPresentacion.getPreguntasEncuesta(idEncuesta);

        // Obtener todas las respuestas de la encuesta y buscar las del usuario
        Map<Pregunta, Set<Respuesta>> todasRespuestas = ctrlPresentacion.getRespuestasEncuesta(idEncuesta);

        for (Pregunta pregunta : preguntas) {
            // buscar si el usuario ya tiene respuesta para esta pregunta
            Respuesta respuestaUsuario = null;
            Set<Respuesta> respuestasSet = todasRespuestas.get(pregunta);
            if (respuestasSet != null) {
                for (Respuesta r : respuestasSet) {
                    if (r.getNomUsuario().equals(nombreUsuario)) {
                        respuestaUsuario = r;
                        break;
                    }
                }
            }

            PanelRespuesta panel = crearPanelRespuesta(pregunta, respuestaUsuario);
            paneles.put(pregunta.getIdPregunta(), panel);

            JPanel container = panel.getPanel();
            container.setMaximumSize(new Dimension(Integer.MAX_VALUE, container.getPreferredSize().height));
            panelPreguntas.add(container);
            panelPreguntas.add(Box.createRigidArea(new Dimension(0, 10)));
        }
    }

    /**
     * Crea el panel de respuesta apropiado según el tipo de pregunta y una posible respuesta previa.
     *
     * @param pregunta Pregunta
     * @param existente Respuesta previa del usuario (o null)
     * @return Panel de respuesta configurado
     */
    private PanelRespuesta crearPanelRespuesta(Pregunta pregunta, Respuesta existente) {
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

    /** Guardar respuestas: crea nuevas o modifica existentes según corresponda. */
    private void guardarRespuestas() {
        try {
            // validar
            // for (Map.Entry<Integer, PanelRespuesta> entry : paneles.entrySet()) {
            //     if (!entry.getValue().tieneRespuesta()) {
            //         JOptionPane.showMessageDialog(frame, "Por favor, responde todas las preguntas", "Validación", JOptionPane.WARNING_MESSAGE);
            //         return;
            //     }
            // }

            // Guardar: si el panel tiene idRespuesta existente, modificar; si no, crear nueva
            for (Map.Entry<Integer, PanelRespuesta> entry : paneles.entrySet()) {
                int idPregunta = entry.getKey();
                Object nuevaRespuesta = entry.getValue().getRespuesta();
                int idRespExistente = entry.getValue().getIdRespuesta();

                if (idRespExistente >= 0) {
                    // convertir tipos cuando sea necesario (opcion multiple -> int[])
                    if (nuevaRespuesta instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<Integer> list = (List<Integer>) nuevaRespuesta;
                        int[] arr = new int[list.size()];
                        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
                        ctrlPresentacion.modificarRespuesta(idRespExistente, arr);
                    } else {
                        ctrlPresentacion.modificarRespuesta(idRespExistente, nuevaRespuesta);
                    }
                } else {
                    // crear nueva respuesta a la pregunta
                    if (nuevaRespuesta instanceof List) {
                        @SuppressWarnings("unchecked")
                        List<Integer> list = (List<Integer>) nuevaRespuesta;
                        int[] arr = new int[list.size()];
                        for (int i = 0; i < list.size(); i++) arr[i] = list.get(i);
                        ctrlPresentacion.darRespuesta(nombreUsuario, idPregunta, arr);
                    } else {
                        ctrlPresentacion.darRespuesta(nombreUsuario, idPregunta, nuevaRespuesta);
                    }
                }
            }

            JOptionPane.showMessageDialog(panel, "Respuestas actualizadas correctamente", "Éxito", JOptionPane.INFORMATION_MESSAGE);
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

    private interface PanelRespuesta {
        JPanel getPanel();
        Object getRespuesta();
        boolean tieneRespuesta();
        int getIdRespuesta(); // -1 si no hay respuesta previa
    }

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

            // prefill
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
        public int getIdRespuesta() { return idRespuesta; }
    }

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

            // prefill
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
        public Object getRespuesta() { return spinRespuesta.getValue(); }

        @Override
        public boolean tieneRespuesta() { return true; }

        @Override
        public int getIdRespuesta() { return idRespuesta; }
    }

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
        public boolean tieneRespuesta() { return getRespuesta() != null; }

        @Override
        public int getIdRespuesta() { return idRespuesta; }
    }

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
        public boolean tieneRespuesta() { return checkboxes.stream().anyMatch(JCheckBox::isSelected); }

        @Override
        public int getIdRespuesta() { return idRespuesta; }
    }
}
