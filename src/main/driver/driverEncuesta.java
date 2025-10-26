package driver;

import java.util.Scanner;
import classcontroller.CtrlUsuario;
import classcontroller.CtrlEncuesta;
import classcontroller.CtrlPregunta;
import classcontroller.CtrlRespuesta;
import persistence.CtrlPersistence;
import domaincontroller.CtrlDominioUsuario;

/**
 * Driver interactivo para probar CtrlDominioEncuesta.
 * 
 * Este es un programa MANUAL con menú para el 1er lliurament.
 * El profesor lo ejecuta y prueba casos de uso por consola.
 * 
 * @author Peiyu Wang
 * @version 1.0
 */

/*Clase que testea la clase Encuesta */
public class DriverEncuesta {
    private static CtrlDominioEncuesta ctrlDominioEncuesta;
    private static Scanner scanner;
    //private static String u = null; // Usuario con sesión iniciada
    

    public static void main (String[] args) {
        // Inicializar dependencias (capa real, no mocks)
        CtrlEncuesta ctrlEncuesta = new CtrlEncuesta();
        CtrlUsuario ctrlUsuario = new CtrlUsuario();
        CtrlPregunta ctrlPregunta = new CtrlPregunta();
        CtrlRespuesta ctrlRespuesta = new CtrlRespuesta();
        CtrlPersistence ctrlPersistence = new CtrlPersistence();

        ctrlDominioEncuesta = new CtrlDominioEncuesta(ctrlUsuario,ctrlEncuesta,
	                             ctrlPregunta,ctrlRespuesta, ctrlPersistence);
        scanner = new Scanner(System.in);

        boolean salir = false;

        System.out.println("DRIVER - CONTROLADOR DE ENCUESTA");

        while (!salir) {
            mostrarMenu();

        }
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
}