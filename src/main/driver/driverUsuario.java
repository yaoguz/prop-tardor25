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
@RunWith(MockitoJUnitRunner.class)
public class DriverUsuario {
    private CtrlUsuario ctrlUsuario;
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
        
        System.out.println("DRIVER - CONTROLADOR DE DOMINIO DE USUARIO");
        
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
                    System.out.println("\nSaliendo del driver...\n");
                    break;
                default:
                    System.out.println("\nOpción no válida. Intenta de nuevo.\n");
            }
        }
        
        scanner.close();
    }
    
    private static void mostrarMenu() {
        System.out.println("\nMENÚ PRINCIPAL");
        System.out.println("1. Registrar usuario");
        System.out.println("2. Iniciar sesión");
        System.out.println("3. Modificar contraseña");
        System.out.println("4. Cerrar sesión");
        System.out.println("0. Salir");
        
        if (usuarioActual != null) {
            System.out.println("Sesión activa: " + usuarioActual);
        }
        
        System.out.print("\nSelecciona una opción: ");
    }
    
    private static void testRegistrarUsuario() {
        System.out.println("\nTEST: REGISTRAR USUARIO");
        
        System.out.print("Nombre de usuario: ");
        String nomUsuario = scanner.nextLine();
        
        System.out.print("Contraseña: ");
        String contrasena = scanner.nextLine();
        
        System.out.print("Confirmar contraseña: ");
        String confirmacion = scanner.nextLine();
        
        try {
            String resultado = ctrlDominioUsuario.registrarUsuario(nomUsuario, contrasena, confirmacion);
            System.out.println("\n" + resultado);
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nError inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testIniciarSesion() {
        System.out.println("TEST: INICIAR SESIÓN");
        
        System.out.print("Nombre de usuario: ");
        String nomUsuario = scanner.nextLine();
        
        System.out.print("Contraseña: ");
        String contrasena = scanner.nextLine();
        
        try {
            boolean exito = ctrlDominioUsuario.iniciarSesion(nomUsuario, contrasena);
            if (exito) {
                usuarioActual = nomUsuario;
                System.out.println("\nSesión iniciada correctamente. Bienvenido, " + nomUsuario + "!");
            } else {
                System.out.println("\nCredenciales incorrectas. Inicio de sesión fallido.");
            }
        } catch (Exception e) {
            System.out.println("\nError inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testModificarContrasena() {
        System.out.println("TEST: MODIFICAR CONTRASEÑA");
        
        if (usuarioActual == null) {
            System.out.println("\nDebes iniciar sesión primero.");
            return;
        }
        
        System.out.println("Usuario: " + usuarioActual);
        System.out.print("Nueva contraseña: ");
        String nuevaContrasena = scanner.nextLine();
        
        try {
            ctrlDominioUsuario.modificarContrasena(usuarioActual, nuevaContrasena);
            System.out.println("\nContraseña modificada correctamente.");
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testCerrarSesion() {
        System.out.println("TEST: CERRAR SESIÓN");
        
        if (usuarioActual == null) {
            System.out.println("\nNo hay ninguna sesión activa.");
            return;
        }
        
        try {
            ctrlDominioUsuario.cerrarSesion(usuarioActual);
            System.out.println("\nSesión cerrada correctamente. Hasta pronto, " + usuarioActual + "!");
            usuarioActual = null;
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static int leerEntero() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("Entrada inválida. Introduce un número: ");
            }
        }
    }
}
