package presentation.components;

import javax.swing.*;
import java.awt.*;

/**
 * Clase base abstracta para componentes reutilizables de la UI.
 * 
 * Proporciona estructura común y métodos de utilidad para todos los
 * componentes visuales del sistema.
 * 
 * RESPONSABILIDADES:
 * - Definir interfaz común para componentes UI
 * - Proporcionar métodos de utilidad para configuración
 * - Gestionar estilos consistentes
 * 
 * MÉTODOS PRINCIPALES:
 * - getPanel(): Obtiene el panel principal del componente
 * - createStyledButton(): Crea botón con estilo consistente
 * - createStyledLabel(): Crea label con estilo consistente
 * 
 * @author PROP Cluster XX
 */
public abstract class ComponenteBase {
    
    protected JPanel panel;
    
    /** Colores del tema */
    protected static final Color COLOR_PRIMARY = new Color(52, 152, 219);
    protected static final Color COLOR_SECONDARY = new Color(149, 165, 166);
    protected static final Color COLOR_SUCCESS = new Color(46, 204, 113);
    protected static final Color COLOR_DANGER = new Color(231, 76, 60);
    protected static final Color COLOR_WARNING = new Color(241, 196, 15);
    protected static final Color COLOR_BG = new Color(236, 240, 241);
    protected static final Color COLOR_TEXT = new Color(44, 62, 80);
    
    /** Fuentes */
    protected static final Font FONT_TITLE = new Font("Arial", Font.BOLD, 24);
    protected static final Font FONT_SUBTITLE = new Font("Arial", Font.BOLD, 16);
    protected static final Font FONT_NORMAL = new Font("Arial", Font.PLAIN, 14);
    protected static final Font FONT_SMALL = new Font("Arial", Font.PLAIN, 12);
    
    /** Dimensiones de ventana */
    // Increase default window size so controls in the header don't overlap when resizing/searching
    protected static final int WINDOW_WIDTH = 1280;
    protected static final int WINDOW_HEIGHT = 820;
    
    /**
     * Constructor base que inicializa el panel.
     */
    protected ComponenteBase() {
        panel = new JPanel();
        panel.setBackground(COLOR_BG);
    }
    
    /**
     * Obtiene el panel principal del componente.
     * 
     * @return Panel del componente
     */
    public JPanel getPanel() {
        return panel;
    }
    
    /**
     * Crea botón con estilo consistente.
     * 
     * @param text Texto del botón
     * @param color Color de fondo
     * @return Botón estilizado
     */
    protected JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(FONT_NORMAL);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }
    
    /**
     * Crea label con estilo consistente.
     * 
     * @param text Texto del label
     * @param font Fuente
     * @return Label estilizado
     */
    protected JLabel createStyledLabel(String text, Font font) {
        JLabel label = new JLabel(text);
        label.setFont(font);
        label.setForeground(COLOR_TEXT);
        return label;
    }
    
    /**
     * Crea campo de texto con estilo consistente.
     * 
     * @param columns Número de columnas
     * @return Campo de texto estilizado
     */
    protected JTextField createStyledTextField(int columns) {
        JTextField field = new JTextField(columns);
        field.setFont(FONT_NORMAL);
        return field;
    }
    
    /**
     * Crea campo de contraseña con estilo consistente.
     * 
     * @param columns Número de columnas
     * @return Campo de contraseña estilizado
     */
    protected JPasswordField createStyledPasswordField(int columns) {
        JPasswordField field = new JPasswordField(columns);
        field.setFont(FONT_NORMAL);
        return field;
    }
}
