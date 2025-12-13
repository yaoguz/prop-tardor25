package presentation.views;

import presentation.CtrlPresentacion;
import presentation.components.ComponenteBase;
import domain.*;
import domain.algorithm.estructuras.*;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Vista para mostrar las respuestas de todos los miembros de un perfil.
 * 
 * Muestra las respuestas de cada miembro del perfil de clustering.
 * Las respuestas del representante aparecen en negrita para destacarlas.
 * Permite volver a la vista de estadísticas.
 * 
 * @author PROP Cluster XX
 */
public class VistaRespuestasMiembros extends ComponenteBase {

    /** Controlador de presentación */
    private CtrlPresentacion ctrlPresentacion;
    
    /** Nombre del usuario en sesión */
    private String nombreUsuario;
    
    /** ID de encuesta */
    private int idEncuesta;
    
    /** Perfil cuyos miembros se muestran */
    private Perfil perfil;
    
    /** Nombre del representante del perfil */
    private String representante;
    
    /** Ventana principal */
    private JFrame frame;
    
    /** Panel con respuestas de miembros */
    private JPanel panelRespuestas;

    /**
     * Constructor.
     * 
     * @param ctrlPresentacion Controlador de presentación
     * @param nombreUsuario Usuario en sesión
     * @param idEncuesta ID de encuesta
     * @param perfil Perfil del cual mostrar respuestas
     * @param representante Nombre del representante del perfil
     */
    public VistaRespuestasMiembros(CtrlPresentacion ctrlPresentacion, String nombreUsuario, 
                                    int idEncuesta, Perfil perfil, String representante) {
        this.ctrlPresentacion = ctrlPresentacion;
        this.nombreUsuario = nombreUsuario;
        this.idEncuesta = idEncuesta;
        this.perfil = perfil;
        this.representante = representante;
    }

    /**
     * Muestra la vista.
     */
    public void mostrar() {
        frame = new JFrame("Respuestas de Miembros del Perfil");
        frame.setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        // Panel principal
        JPanel panelPrincipal = new JPanel(new BorderLayout(0, 10));
        panelPrincipal.setBackground(COLOR_BG);
        panelPrincipal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Encabezado
        JPanel panelTop = new JPanel(new BorderLayout());
        panelTop.setBackground(COLOR_BG);
        
        JButton btnVolver = createStyledButton("← Volver", COLOR_SECONDARY);
        btnVolver.addActionListener(e -> volver());
        panelTop.add(btnVolver, BorderLayout.WEST);
        
        //String titulo = ctrlPresentacion.obtenerTituloEncuesta(idEncuesta);
        JLabel lblTitulo = createStyledLabel("Respuestas - Perfil (" + representante + ")", FONT_TITLE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        panelTop.add(lblTitulo, BorderLayout.CENTER);
        
        panelPrincipal.add(panelTop, BorderLayout.NORTH);

        // Panel de respuestas (scrollable)
        panelRespuestas = new JPanel();
        panelRespuestas.setLayout(new BoxLayout(panelRespuestas, BoxLayout.Y_AXIS));
        panelRespuestas.setBackground(COLOR_BG);

        JScrollPane scrollRespuestas = new JScrollPane(panelRespuestas);
        scrollRespuestas.setBorder(BorderFactory.createEmptyBorder());
        scrollRespuestas.getVerticalScrollBar().setUnitIncrement(16);
        panelPrincipal.add(scrollRespuestas, BorderLayout.CENTER);

        frame.add(panelPrincipal);
        
        // Cargar respuestas de miembros
        cargarRespuestasMiembros();
        
        frame.setVisible(true);
    }

    /**
     * Vuelve a la vista de estadísticas.
     */
    private void volver() {
        frame.dispose();
		ctrlPresentacion.mostrarEstadisticas(nombreUsuario, idEncuesta);
    }

    /**
     * Carga y muestra las respuestas de todos los miembros del perfil.
     */
    private void cargarRespuestasMiembros() {
        panelRespuestas.removeAll();

        // Obtener miembros del perfil
        List<String> miembros = ctrlPresentacion.obtenerMiembrosPerfil(perfil);
        // Asegurar que el representante aparezca primero
        if (representante != null && !representante.isEmpty() && miembros.contains(representante)) {
            miembros.remove(representante);
            miembros.add(0, representante);
        }
        
        // Obtener todas las respuestas de la encuesta
        Map<Pregunta, Set<Respuesta>> todasRespuestas = ctrlPresentacion.getRespuestasEncuesta(idEncuesta);
        
        // Obtener preguntas en orden
        List<Pregunta> preguntas = ctrlPresentacion.getPreguntasEncuesta(idEncuesta);

        if (miembros.isEmpty()) {
            JLabel lblVacio = createStyledLabel("No hay miembros en este perfil", FONT_NORMAL);
            lblVacio.setAlignmentX(Component.CENTER_ALIGNMENT);
            panelRespuestas.add(lblVacio);
            panelRespuestas.revalidate();
            panelRespuestas.repaint();
            return;
        }

        // Para cada miembro, mostrar sus respuestas
        for (String miembro : miembros) {
            JPanel panelMiembro = crearPanelMiembro(miembro, preguntas, todasRespuestas);
            panelRespuestas.add(panelMiembro);
            panelRespuestas.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        panelRespuestas.revalidate();
        panelRespuestas.repaint();
    }

    /**
     * Crea un panel con las respuestas de un miembro.
     * 
     * @param miembro Nombre del miembro
     * @param preguntas Lista de preguntas en orden
     * @param todasRespuestas Mapa de todas las respuestas
     * @return Panel configurado
     */
    private JPanel crearPanelMiembro(String miembro, List<Pregunta> preguntas, 
                                      Map<Pregunta, Set<Respuesta>> todasRespuestas) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(COLOR_PRIMARY, 2),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Título: nombre del miembro (negrita si es representante)
        boolean esRepresentante = miembro.equals(representante);
        String textoMiembro = miembro + (esRepresentante ? " (Representante)" : "");
        JLabel lblMiembro = createStyledLabel(textoMiembro, esRepresentante ? FONT_SUBTITLE : FONT_NORMAL);
        if (esRepresentante) {
            lblMiembro.setFont(new Font(lblMiembro.getFont().getName(), Font.BOLD, FONT_SUBTITLE.getSize()));
            lblMiembro.setForeground(COLOR_PRIMARY);
        }
        panel.add(lblMiembro, BorderLayout.NORTH);

        // Respuestas del miembro
        JPanel panelRespuestas = new JPanel();
        panelRespuestas.setLayout(new BoxLayout(panelRespuestas, BoxLayout.Y_AXIS));
        panelRespuestas.setBackground(Color.WHITE);
        panelRespuestas.setBorder(BorderFactory.createEmptyBorder(10, 20, 0, 0));

        for (Pregunta pregunta : preguntas) {
            // Encontrar respuesta de este miembro a esta pregunta
            String respuestaTexto = "No respondida";
            Set<Respuesta> respuestasSet = todasRespuestas.get(pregunta);
            if (respuestasSet != null) {
                for (Respuesta r : respuestasSet) {
                    if (r.getNomUsuario().equals(miembro)) {
                        respuestaTexto = formatearRespuestaParaPregunta(r, pregunta);
                        break;
                    }
                }
            }

            // Pregunta
            JLabel lblPregunta = createStyledLabel("• " + pregunta.getEnunciado() + " (" + pregunta.getTipo() + ")", FONT_NORMAL);
            lblPregunta.setForeground(new Color(80, 80, 80));
            panelRespuestas.add(lblPregunta);

            // Respuesta (negrita si es representante)
            JLabel lblRespuesta = createStyledLabel("  → " + respuestaTexto, FONT_NORMAL);
            if (esRepresentante) {
                lblRespuesta.setFont(new Font(lblRespuesta.getFont().getName(), Font.BOLD, FONT_NORMAL.getSize()));
            }
            lblRespuesta.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
            panelRespuestas.add(lblRespuesta);
        }

        panel.add(panelRespuestas, BorderLayout.CENTER);

        return panel;
    }

    // /**
    //  * Formatea una respuesta para mostrarla.
    //  * 
    //  * @param respuesta Respuesta
    //  * @return Texto formateado
    //  */
    // private String formatearRespuesta(Respuesta respuesta) {
    //     if (respuesta instanceof RespuestaLibre) {
    //         return ((RespuestaLibre) respuesta).getRespuesta();
    //     } else if (respuesta instanceof RespuestaNumerica) {
    //         return String.valueOf(((RespuestaNumerica) respuesta).getRespuesta());
    //     } else if (respuesta instanceof RespuestaOpcionUnica) {
    //         int opcionIdx = ((RespuestaOpcionUnica) respuesta).getRespuesta();
    //         return "Opción " + (opcionIdx + 1);
    //     } else if (respuesta instanceof RespuestaOpcionMultiple) {
    //         int[] opcionesArray = ((RespuestaOpcionMultiple) respuesta).getRespuesta();
    //         List<Integer> opciones = new ArrayList<>();
    //         for (int op : opcionesArray) opciones.add(op);
    //         StringBuilder sb = new StringBuilder("Opciones: ");
    //         for (int i = 0; i < opciones.size(); i++) {
    //             sb.append(opciones.get(i) + 1);
    //             if (i < opciones.size() - 1) sb.append(", ");
    //         }
    //         return sb.toString();
    //     }
    //     return "N/A";
    // }

    /**
     * Formatea una respuesta usando la información de la pregunta para mostrar
     * el texto de las opciones en lugar de índices.
     *
     * @param respuesta Respuesta
     * @param pregunta Pregunta asociada
     * @return Texto formateado
     */
    private String formatearRespuestaParaPregunta(Respuesta respuesta, Pregunta pregunta) {
        if (respuesta == null) return "No respondida";

        if (respuesta instanceof RespuestaLibre) {
            return ((RespuestaLibre) respuesta).getRespuesta();
        } else if (respuesta instanceof RespuestaNumerica) {
            return String.valueOf(((RespuestaNumerica) respuesta).getRespuesta());
        } else if (respuesta instanceof RespuestaOpcionUnica) {
            int opcionIdx = ((RespuestaOpcionUnica) respuesta).getRespuesta();
            List<String> opciones = pregunta.getOpciones();
            if (opcionIdx >= 0 && opcionIdx < opciones.size()) return opciones.get(opcionIdx);
            return "Opción " + (opcionIdx + 1);
        } else if (respuesta instanceof RespuestaOpcionMultiple) {
            int[] opcionesArray = ((RespuestaOpcionMultiple) respuesta).getRespuesta();
            List<String> opciones = pregunta.getOpciones();
            List<String> seleccionadas = new ArrayList<>();
            for (int idx : opcionesArray) {
                if (idx >= 0 && idx < opciones.size()) seleccionadas.add(opciones.get(idx));
                else seleccionadas.add(String.valueOf(idx + 1));
            }
            if (seleccionadas.isEmpty()) return "(sin opciones)";
            return String.join(", ", seleccionadas);
        }
        return "N/A";
    }
}
