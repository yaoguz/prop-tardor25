package driver;

import java.util.Scanner;
import java.util.ArrayList;
import classcontroller.CtrlUsuario;
import classcontroller.CtrlEncuesta;
import classcontroller.CtrlPregunta;
import classcontroller.CtrlRespuesta;
import persistence.CtrlPersistence;
import domaincontroller.CtrlDominioUsuario;
import domain.Encuesta;
import domain.Pregunta;
import domain.Respuesta;

/**
 * Driver interactivo para probar CtrlDominioPregunta.
 * 
 * Este es un programa MANUAL con menú para el 1er lliurament.
 * El profesor lo ejecuta y prueba casos de uso por consola.
 * 
 * @author Denis Roca
 * @version 1.0
 */

/*Clase que testea la clase Encuesta */
public class DriverEncuesta {
    private static CtrlDominioPregunta ctrlDominioPregunta;
    private static Scanner scanner;

    /*
    Al principio tendrá una pregunta por defecto para que pueda interactuar con ella
    sin tener que darle siempre a la opción de crear pregunta
    */

    public static void main (String[] args) {
        // Inicializar dependencias (capa real, no mocks)
        CtrlPregunta ctrlPregunta = new CtrlPregunta();
        CtrlEncuesta ctrlEncuesta = new CtrlEncuesta();
        CtrlRespuesta ctrlRespuesta = new CtrlRespuesta();
        CtrlPersistence ctrlPersistence = new CtrlPersistence();

        ctrlDominioPregunta = new CtrlDominioPregunta(ctrlEncuesta,
	                             ctrlPregunta, ctrlPersistence);
        scanner = new Scanner(System.in);

        int idE = ctrlEncuesta.crearEncuesta("prueba", "E1");
        int idP = ctrlPregunta.crearPregunta(idE,  "Enunciado de prueba:");
        //ctrlRespuesta no implementado cuando se hizo le driver <---------------
        //int idR = ctrlRespuesta.crearRespuesta(???)



        boolean salir = false;

        System.out.println("DRIVER - CONTROLADOR DE PREGUNTA");
        System.out.println("Dispone de una encuesta con id: " +idE);
        System.out.println("con una pregunta con id: " +idP);

        while (!salir) {
            mostrarMenu();

            int opcion = leerEntero();
            
            switch (opcion) {
                case 1:
                    testObtenerRespuestas();
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
        System.out.println("1. Obtener respuestas de una pregunta [por implementar]");
        System.out.println("2. Crear pregunta [por implementar]");
        System.out.println("3. Añadir Respuestas [por implementar]");
        System.out.println("4. Modificar pregunta [por implementar]");
        System.out.println("5. Eliminar pregunta [por implementar]");
        System.out.println("0. Salir");
        
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

    private static void testObtenerRespuestas() {
        System.out.println("\nTEST: OBTENER RESPUESTAS DE UNA PREGUNTA");
        
        System.out.print("Introduzca un id de pregunta: ");
        int idPregunta = scanner.nextInt();

        try {
            List<Integer> respuestas;// = getRespuestas
            System.out.println("Ids de las respuestas de las preguntas: ");
            System.out.println(respuestas);
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
        
    }
}