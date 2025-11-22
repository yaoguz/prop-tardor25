package presentation;

/**
 * Clase principal de la aplicación.
 * 
 * Punto de entrada que inicia el controlador de presentación.
 * 
 * RESPONSABILIDADES:
 * - Inicializar aplicación
 * - Configurar look and feel
 * - Lanzar vista inicial
 * 
 * @author PROP Cluster XX
 */
public class Main {
    
    /**
     * Método main que inicia la aplicación.
     * 
     * @param args Argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        // Configurar look and feel del sistema
        try {
            javax.swing.UIManager.setLookAndFeel(
                javax.swing.UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            System.err.println("Error configurando look and feel: " + e.getMessage());
        }
        
        // Ejecutar en el Event Dispatch Thread
        javax.swing.SwingUtilities.invokeLater(() -> {
            CtrlPresentacion ctrlPresentacion = new CtrlPresentacion();
            ctrlPresentacion.inicializarPresentacion();
        });
    }
}
