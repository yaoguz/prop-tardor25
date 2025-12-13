package presentation.views;

import presentation.CtrlPresentacion;
import presentation.components.ComponenteBase;

import javax.swing.*;
import java.awt.*;

/**
 * Vista de registro de nuevos usuarios.
 * 
 * Permite crear una nueva cuenta con validación de contraseña.
 * 
 * RESPONSABILIDADES:
 * - Validar datos de registro
 * - Confirmar coincidencia de contraseñas
 * - Registrar usuario en sistema
 * - Navegar de vuelta a login
 * 
 * ATRIBUTOS PRINCIPALES:
 * - ctrlPresentacion: Controlador de presentación
 * - txtUsuario/txtPassword/txtConfirm: Campos de entrada
 * 
 * MÉTODOS PRINCIPALES:
 * - mostrar(): Muestra la vista
 * 
 * @author Peiyu Wang
 */
public class VistaRegistro extends ComponenteBase {
    
    /** Controlador de presentación */
    private CtrlPresentacion ctrlPresentacion;
    
    /** Ventana principal */
    private JFrame frame;
    
    /** Campo de texto para nombre de usuario */
    private JTextField txtUsuario;
    
    /** Campo de contraseña */
    private JPasswordField txtPassword;
    
    /** Campo de confirmación de contraseña */
    private JPasswordField txtConfirm;
    
    /**
     * Crea vista de registro.
     * 
     * @param ctrlPresentacion Controlador de presentación
     */
    public VistaRegistro(CtrlPresentacion ctrlPresentacion) {
        super();
        this.ctrlPresentacion = ctrlPresentacion;
        inicializar();
    }
    
    /**
     * Inicializa componentes de la vista.
     */
    private void inicializar() {
        frame = crearFrameEstandar("Registro - Encuestas");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
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
        JLabel lblSubtitulo = createStyledLabel("Crear cuenta", FONT_SUBTITLE);
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
        
        // Confirmar contraseña
        JLabel lblConfirm = createStyledLabel("Confirmar contraseña:", FONT_NORMAL);
        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(lblConfirm, gbc);
        
        txtConfirm = createStyledPasswordField(20);
        gbc.gridx = 1;
        panel.add(txtConfirm, gbc);
        
        // Botón registrar
        JButton btnRegistrar = createStyledButton("Registrarse", COLOR_SUCCESS);
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        btnRegistrar.addActionListener(e -> registrar());
        panel.add(btnRegistrar, gbc);
        
        // Link a login
        JButton btnLogin = new JButton("<html><u>¿Ya tienes cuenta? Inicia sesión</u></html>");
        btnLogin.setFont(FONT_SMALL);
        btnLogin.setForeground(COLOR_PRIMARY);
        btnLogin.setBorderPainted(false);
        btnLogin.setContentAreaFilled(false);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> volverALogin());
        gbc.gridy = 6;
        panel.add(btnLogin, gbc);
        frame.add(panel);
        frame.setSize(600, 500);
        frame.setMinimumSize(frame.getSize()); 
        frame.setLocationRelativeTo(null);
        
        // Enter ejecuta registro
        txtConfirm.addActionListener(e -> registrar());
    }
    
    /**
     * Intenta registrar usuario.
     */
    private void registrar() {
        String usuario = txtUsuario.getText().trim();
        String password = new String(txtPassword.getPassword());
        String confirm = new String(txtConfirm.getPassword());
        
        if (usuario.isEmpty() || password.isEmpty() || confirm.isEmpty()) {
            JOptionPane.showMessageDialog(panel, 
                "Por favor, completa todos los campos", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            ctrlPresentacion.registrarUsuario(usuario, password, confirm);
            JOptionPane.showMessageDialog(frame, 
                "Usuario registrado correctamente. Ahora puedes iniciar sesión.", 
                "Éxito", JOptionPane.INFORMATION_MESSAGE);
            volverALogin();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(frame, 
                "Error: " + ex.getMessage(), 
                "Error de registro", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Vuelve a la vista de login.
     */
    private void volverALogin() {
        frame.dispose();
        ctrlPresentacion.mostrarLogin();
    }
    
    /**
     * Muestra la vista.
     */
    public void mostrar() {
        frame.setVisible(true);
    }
}
