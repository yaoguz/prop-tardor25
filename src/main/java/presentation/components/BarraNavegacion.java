package presentation.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

/**
 * Componente de barra de navegación superior.
 * 
 * Muestra título de la aplicación y opciones de usuario (nombre, logout, etc).
 * Reutilizable en múltiples vistas.
 * 
 * RESPONSABILIDADES:
 * - Mostrar título de aplicación
 * - Mostrar nombre de usuario activo
 * - Proporcionar menú de usuario (cambiar contraseña, cerrar sesión, salir)
 * 
 * ATRIBUTOS PRINCIPALES:
 * - nombreUsuario: Usuario actualmente en sesión
 * 
 * MÉTODOS PRINCIPALES:
 * - setOnCerrarSesion(): Configura acción al cerrar sesión
 * - setOnCambiarContrasena(): Configura acción al cambiar contraseña
 * - setOnSalir(): Configura acción al salir
 * 
 * @author Denis Roca
 */
public class BarraNavegacion extends ComponenteBase {
    
    private String nombreUsuario;
    private JButton btnUsuario;
    private JPopupMenu menuUsuario;
    
    /**
     * Crea barra de navegación con usuario.
     * 
     * @param nombreUsuario Nombre del usuario en sesión
     */
    public BarraNavegacion(String nombreUsuario) {
        super();
        this.nombreUsuario = nombreUsuario;
        inicializar();
    }
    
    /**
     * Inicializa componentes de la barra.
     */
    private void inicializar() {
        panel.setLayout(new BorderLayout());
        panel.setPreferredSize(new Dimension(0, 60));
        panel.setBackground(COLOR_PRIMARY);
        
        // Título a la izquierda
        JLabel lblTitulo = new JLabel("Encuestas con extractor de perfiles");
        lblTitulo.setFont(FONT_TITLE);
        lblTitulo.setForeground(Color.WHITE);
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        panel.add(lblTitulo, BorderLayout.WEST);
        
        // Usuario a la derecha
        if (nombreUsuario != null && !nombreUsuario.isEmpty()) {
            btnUsuario = new JButton(nombreUsuario + " ▼");
            btnUsuario.setFont(FONT_NORMAL);
            btnUsuario.setBackground(COLOR_PRIMARY);
            btnUsuario.setForeground(Color.WHITE);
            btnUsuario.setBorderPainted(false);
            btnUsuario.setFocusPainted(false);
            btnUsuario.setCursor(new Cursor(Cursor.HAND_CURSOR));
            btnUsuario.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 20));
            
            // Crear menú popup
            menuUsuario = new JPopupMenu();
            JMenuItem itemCambiarPass = new JMenuItem("Cambiar contraseña");
            JMenuItem itemCerrarSesion = new JMenuItem("Cerrar sesión");
            JMenuItem itemSalir = new JMenuItem("Salir");
            
            menuUsuario.add(itemCambiarPass);
            menuUsuario.add(itemCerrarSesion);
            menuUsuario.addSeparator();
            menuUsuario.add(itemSalir);
            
            btnUsuario.addActionListener(e -> {
                menuUsuario.show(btnUsuario, 0, btnUsuario.getHeight());
            });
            
            panel.add(btnUsuario, BorderLayout.EAST);
        }
    }
    
    /**
     * Configura acción al cerrar sesión.
     * 
     * @param listener Listener de acción
     */
    public void setOnCerrarSesion(ActionListener listener) {
        if (menuUsuario != null) {
            ((JMenuItem) menuUsuario.getComponent(1)).addActionListener(listener);
        }
    }
    
    /**
     * Configura acción al cambiar contraseña.
     * 
     * @param listener Listener de acción
     */
    public void setOnCambiarContrasena(ActionListener listener) {
        if (menuUsuario != null) {
            ((JMenuItem) menuUsuario.getComponent(0)).addActionListener(listener);
        }
    }
    
    /**
     * Configura acción al salir.
     * 
     * @param listener Listener de acción
     */
    public void setOnSalir(ActionListener listener) {
        if (menuUsuario != null) {
            ((JMenuItem) menuUsuario.getComponent(3)).addActionListener(listener);
        }
    }
}
