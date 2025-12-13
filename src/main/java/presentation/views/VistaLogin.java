package presentation.views;

import presentation.CtrlPresentacion;
import presentation.components.ComponenteBase;

import javax.swing.*;
import java.awt.*;

/**
 * Vista de inicio de sesión.
 * 
 * Permite al usuario iniciar sesión con nombre de usuario y contraseña,
 * o navegar a la página de registro.
 * 
 * RESPONSABILIDADES:
 * - Validar credenciales de usuario
 * - Navegar a registro si no tiene cuenta
 * - Iniciar sesión y mostrar página principal
 * 
 * ATRIBUTOS PRINCIPALES:
 * - ctrlPresentacion: Controlador de presentación
 * - frame: Ventana principal
 * - txtUsuario: Campo de nombre de usuario
 * - txtPassword: Campo de contraseña
 * 
 * MÉTODOS PRINCIPALES:
 * - mostrar(): Muestra la vista
 * - ocultar(): Oculta la vista
 * 
 * @author PROP Cluster XX
 */
public class VistaLogin extends ComponenteBase {
    
    /** Controlador de presentación */
    private CtrlPresentacion ctrlPresentacion;
    
    /** Ventana principal */
    private JFrame frame;
    
    /** Campo de texto para nombre de usuario */
    private JTextField txtUsuario;
    
    /** Campo de contraseña */
    private JPasswordField txtPassword;
    
    /**
     * Crea vista de login.
     * 
     * @param ctrlPresentacion Controlador de presentación
     */
    public VistaLogin(CtrlPresentacion ctrlPresentacion) {
        super();
        this.ctrlPresentacion = ctrlPresentacion;
        inicializar();
    }
    
    /**
     * Inicializa componentes de la vista.
     */
    private void inicializar() {
        frame = new JFrame("Login - Encuestas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //frame.setLocationRelativeTo(null);
        
        panel.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Título
        JLabel lblTitulo = createStyledLabel("Encuestas con extractor de perfiles", FONT_TITLE);
        lblTitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblTitulo, gbc);
        
        // Subtítulo
        JLabel lblSubtitulo = createStyledLabel("Iniciar sesión", FONT_SUBTITLE);
        lblSubtitulo.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        panel.add(lblSubtitulo, gbc);
        
        // Usuario
        JLabel lblUsuario = createStyledLabel("Nombre de usuario:", FONT_NORMAL);
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        panel.add(lblUsuario, gbc);
        
        txtUsuario = createStyledTextField(20);
        gbc.gridx = 1;
        panel.add(txtUsuario, gbc);
        
        // Contraseña
        JLabel lblPassword = createStyledLabel("Contraseña:", FONT_NORMAL);
        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(lblPassword, gbc);
        
        txtPassword = createStyledPasswordField(20);
        gbc.gridx = 1;
        panel.add(txtPassword, gbc);
        
        // Botón entrar
        JButton btnEntrar = createStyledButton("Entrar", COLOR_PRIMARY);
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 2;
        btnEntrar.addActionListener(e -> login());
        panel.add(btnEntrar, gbc);
        
        // Link a registro
        JButton btnRegistro = new JButton("<html><u>¿No tienes cuenta? Regístrate</u></html>");
        btnRegistro.setFont(FONT_SMALL);
        btnRegistro.setForeground(COLOR_PRIMARY);
        btnRegistro.setBorderPainted(false);
        btnRegistro.setContentAreaFilled(false);
        btnRegistro.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnRegistro.addActionListener(e -> irARegistro());
        gbc.gridy = 5;
        panel.add(btnRegistro, gbc);
        frame.add(panel);
        
        //frame.pack();
        frame.setSize(600, 500);
        frame.setMinimumSize(frame.getSize()); 
        frame.setLocationRelativeTo(null);

        // Enter en campos ejecuta login
        txtUsuario.addActionListener(e -> login());
        txtPassword.addActionListener(e -> login());
    }
    
    /**
     * Intenta iniciar sesión.
     */
    private void login() {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());
        
        if (usuario.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(panel, 
                "Por favor, introduce usuario y contraseña", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            ctrlPresentacion.iniciarSesion(usuario, password);
            frame.dispose();
            ctrlPresentacion.mostrarPaginaPrincipal(usuario);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(panel, 
                "Error: " + ex.getMessage(), 
                "Error de inicio de sesión", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Navega a la vista de registro.
     */
    private void irARegistro() {
        frame.dispose();
        ctrlPresentacion.mostrarRegistro();
    }
    
    /**
     * Muestra la vista.
     */
    public void mostrar() {
        frame.setVisible(true);
    }
    
    /**
     * Oculta la vista.
     */
    public void ocultar() {
        frame.setVisible(false);
    }
}
