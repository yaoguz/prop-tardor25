package driver;

import java.util.Scanner;
import classcontroller.CtrlUsuario;
import classcontroller.CtrlEncuesta;
import classcontroller.CtrlPregunta;
import classcontroller.CtrlRespuesta;
import persistence.CtrlPersistence;
import domaincontroller.CtrlDominioUsuario;

/**
 * Driver interactivo para probar CtrlDominioUsuario.
 * 
 * Este es un programa MANUAL con menú para el 1er lliurament.
 * El profesor lo ejecuta y prueba casos de uso por consola.
 * 
 * @author Equipo PROP
 * @version 1.0
 */
public class DriverCtrlDominioUsuario {
    
    private static CtrlDominioUsuario ctrlDominioUsuario;
    private static Scanner scanner;
    private static String usuarioActual = null; // Usuario con sesión iniciada
    
    public static void main(String[] args) {
        // Inicializar dependencias (capa real, no mocks)
        CtrlUsuario ctrlUsuario = new CtrlUsuario();
        CtrlPersistence ctrlPersistence = new CtrlPersistence();
        
        ctrlDominioUsuario = new CtrlDominioUsuario(ctrlUsuario, ctrlPersistence);
        scanner = new Scanner(System.in);
        
        boolean salir = false;
        
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║     DRIVER - CONTROLADOR DE DOMINIO DE USUARIO          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        while (!salir) {
            mostrarMenu();
            int opcion = leerEntero();
            
            switch (opcion) {
                case 1:
                    testRegistrarUsuario();
                    break;
                case 2:
                    testIniciarSesion();
                    break;
                case 3:
                    testModificarContrasena();
                    break;
                case 4:
                    testCerrarSesion();
                    break;
                case 0:
                    salir = true;
                    System.out.println("\n👋 Saliendo del driver...\n");
                    break;
                default:
                    System.out.println("\n❌ Opción no válida. Intenta de nuevo.\n");
            }
        }
        
        scanner.close();
    }
    
    private static void mostrarMenu() {
        System.out.println("\n┌───────────────────────────────────────────────────────────┐");
        System.out.println("│                    MENÚ PRINCIPAL                         │");
        System.out.println("├───────────────────────────────────────────────────────────┤");
        System.out.println("│  1. Registrar usuario                                     │");
        System.out.println("│  2. Iniciar sesión                                        │");
        System.out.println("│  3. Modificar contraseña                                  │");
        System.out.println("│  4. Cerrar sesión                                         │");
        System.out.println("│  0. Salir                                                 │");
        System.out.println("└───────────────────────────────────────────────────────────┘");
        
        if (usuarioActual != null) {
            System.out.println("📌 Sesión activa: " + usuarioActual);
        }
        
        System.out.print("\nSelecciona una opción: ");
    }
    
    private static void testRegistrarUsuario() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║              TEST: REGISTRAR USUARIO                      ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        System.out.print("Nombre de usuario: ");
        String nomUsuario = scanner.nextLine();
        
        System.out.print("Contraseña: ");
        String contrasena = scanner.nextLine();
        
        System.out.print("Confirmar contraseña: ");
        String confirmacion = scanner.nextLine();
        
        try {
            String resultado = ctrlDominioUsuario.registrarUsuario(nomUsuario, contrasena, confirmacion);
            System.out.println("\n✅ " + resultado);
        } catch (IllegalArgumentException e) {
            System.out.println("\n❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n💥 Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testIniciarSesion() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║               TEST: INICIAR SESIÓN                        ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        System.out.print("Nombre de usuario: ");
        String nomUsuario = scanner.nextLine();
        
        System.out.print("Contraseña: ");
        String contrasena = scanner.nextLine();
        
        try {
            boolean exito = ctrlDominioUsuario.iniciarSesion(nomUsuario, contrasena);
            
            if (exito) {
                usuarioActual = nomUsuario;
                System.out.println("\n✅ Sesión iniciada correctamente. Bienvenido, " + nomUsuario + "!");
            } else {
                System.out.println("\n❌ Credenciales incorrectas. Inicio de sesión fallido.");
            }
        } catch (Exception e) {
            System.out.println("\n💥 Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testModificarContrasena() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║            TEST: MODIFICAR CONTRASEÑA                     ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        if (usuarioActual == null) {
            System.out.println("\n⚠️  Debes iniciar sesión primero.");
            return;
        }
        
        System.out.println("Usuario: " + usuarioActual);
        System.out.print("Nueva contraseña: ");
        String nuevaContrasena = scanner.nextLine();
        
        try {
            ctrlDominioUsuario.modificarContrasena(usuarioActual, nuevaContrasena);
            System.out.println("\n✅ Contraseña modificada correctamente.");
        } catch (Exception e) {
            System.out.println("\n💥 Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testCerrarSesion() {
        System.out.println("\n╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║              TEST: CERRAR SESIÓN                          ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝");
        
        if (usuarioActual == null) {
            System.out.println("\n⚠️  No hay ninguna sesión activa.");
            return;
        }
        
        try {
            ctrlDominioUsuario.cerrarSesion(usuarioActual);
            System.out.println("\n✅ Sesión cerrada correctamente. Hasta pronto, " + usuarioActual + "!");
            usuarioActual = null;
        } catch (Exception e) {
            System.out.println("\n💥 Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static int leerEntero() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("❌ Entrada inválida. Introduce un número: ");
            }
        }
    }
}
