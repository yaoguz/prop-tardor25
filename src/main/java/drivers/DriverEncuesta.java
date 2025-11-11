package drivers;

import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
import java.util.Map; 
import java.util.Set; 
import java.util.TreeMap;


import domaincontrollers.*;
import domain.*;

/**
 * Driver interactivo para probar CtrlDominioEncuesta.
 * 
 * Este es un programa MANUAL con menú para el 1er lliurament.
 * El profesor lo ejecuta y prueba casos de uso por consola.
 * 
 * @author Peiyu Wang
 * @version 2.0
 */

/*Clase que testea la clase Encuesta */
public class DriverEncuesta {
    private static CtrlDominioEncuesta ctrlDominioEncuesta;
    private static CtrlDominioUsuario ctrlUsuario; 
    private static CtrlDominioPregunta ctrlPregunta;
    private static CtrlDominioRespuesta ctrlRespuesta;
    private static Scanner scanner;

    /*
    Al principio tendrá una encuesta por defecto para que pueda interactuar con ella
    sin tener que darle siempre a la opcion de crear encuesta
    */

    public static void main (String[] args) {
        // Inicializar dependencias (capa real, no mocks)
        ctrlUsuario = new CtrlDominioUsuario();
        ctrlPregunta = new CtrlDominioPregunta();
        ctrlRespuesta = new CtrlDominioRespuesta();

        ctrlDominioEncuesta = new CtrlDominioEncuesta();
        scanner = new Scanner(System.in);

    // la contraseña debe tener al menos 6 caracteres (requisito en CtrlDominioUsuario)
        String prueba = ctrlUsuario.registrarUsuario("prueba",  "123456", "123456");
        int idE = ctrlDominioEncuesta.crearEncuesta("prueba", "E1",ctrlUsuario);


        boolean salir = false;

        System.out.println("DRIVER - CONTROLADOR DE ENCUESTA");
        System.out.println("Su nombre de usuario es: prueba");
        System.out.println("Dispone de una encuesta con id:" +idE);

        while (!salir) {
            mostrarMenu();

            int opcion = leerEntero();
            
            switch (opcion) {
                case 1:
                    testObtenerPreguntas();
                    break;
                case 2:
                    testCrearEncuesta();
                    break;
                case 3:
                    testImportarEncuesta();
                    break;
                case 4:
                    testModificarTitulo();
                    break;
                case 5:
                    testEliminarEncuesta();
                    break;
                case 6:
                    testObtenerRespuestas();
                    break;
                case 7:
                    testAnadirPregunta();
                    break;
                case 8:
                    testExisteEncuesta();
                    break;
                case 9:
                    testObtenerEncuesta();
                    break;
                case 10:
                    testObtenerAllEncuestas();
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
        System.out.println("1. Obtener preguntas de una encuesta");
        System.out.println("2. Crear encuesta");
        System.out.println("3. Importar encuesta");
        System.out.println("4. Modificar titulo de una encuesta");
        System.out.println("5. Eliminar encuesta");
        System.out.println("6. Obtener respuestas de una encuesta");
        System.out.println("7. Añadir pregunta");
        System.out.println("8. Comprobar la existencia de una encuesta");
        System.out.println("9. Obtener una encuesta");
        System.out.println("10. Obtener todas las encuestas");
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

    private static void testObtenerPreguntas() {
        System.out.println("\nTEST: OBTENER PREGUNTAS");
        
        System.out.print("Introduzca un id de encuesta: ");
        int idEncuesta= scanner.nextInt();

        try {
            List<Integer> preguntas = ctrlDominioEncuesta.getPreguntasEncuesta(idEncuesta);
            System.out.println("Ids de las preguntas de la encuesta: ");
            System.out.println(preguntas);
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
        
    }


    private static void testCrearEncuesta() {
        System.out.println("\nTEST: CREAR ENCUESTA");
        
        System.out.print("Introduzca su nombre de usuario: ");
        String username = scanner.nextLine();

        System.out.print("Introduzca un titulo de encuesta: ");
        String titulo = scanner.nextLine();

        try {
            int idE = ctrlDominioEncuesta.crearEncuesta(username,titulo,ctrlUsuario);
            System.out.println("Se ha creado una encuesta con id: " +idE);
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private static void testImportarEncuesta() {
        System.out.println("\nTEST: IMPORTAR ENCUESTA");
        
        /*
        System.out.print("Introduzca su nombre de usuario: ");
        String username = scanner.nextLine();

        System.out.print("Introduzca un titulo de encuesta: ");
        String titulo = scanner.nextLine();

        try {
            int idE = ctrlDominioEncuesta.crearEncuesta(username,titulo);
            System.out.println("Se ha creado una encuesta con id: " +idE);
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
        */
    }

    private static void testModificarTitulo() {
        System.out.println("\nTEST: MODIFICAR TITULO");
        
        System.out.print("Introduzca un id de encuesta: ");
        int idEncuesta= scanner.nextInt();
        scanner.nextLine();

        System.out.print("Introduzca un titulo de encuesta: ");
        String titulo = scanner.nextLine();

        try {
            ctrlDominioEncuesta.modificarTituloEncuesta(idEncuesta,titulo);
            System.out.println("Se ha modificado el titulo correctamente");
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private static void testEliminarEncuesta() {
        System.out.println("\nTEST: ELIMINAR ENCUESTA");
        
        System.out.print("Introduzca un id de encuesta: ");
        int idEncuesta= scanner.nextInt();
        scanner.nextLine();

        try {
            ctrlDominioEncuesta.eliminarEncuesta(idEncuesta,ctrlPregunta, ctrlRespuesta, ctrlUsuario);
            System.out.println("Se ha eliminado la encuesta correctamente");
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private static void testObtenerRespuestas() {
        System.out.println("\nTEST: OBTENER RESPUESTAS");
        
        System.out.print("Introduzca un id de encuesta: ");
        int idEncuesta= scanner.nextInt();
        scanner.nextLine();

        try {
            Map<Pregunta, Set<Respuesta>> result = ctrlDominioEncuesta.getRespuestasEncuesta(idEncuesta,ctrlRespuesta, ctrlPregunta);
            for (Map.Entry<Pregunta, Set<Respuesta>> entry : result.entrySet()) {
                System.out.println("Pregunta: " + entry.getKey());
                
                System.out.println("Respuestas:");
                for (Respuesta respuesta : entry.getValue()) {
                    System.out.println(respuesta);
                } 
                System.out.println(); 
            } //Ns si printea bien asi
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private static void testAnadirPregunta() {
        System.out.println("\nTEST: ANADIR PREGUNTA");
        
        System.out.print("Introduzca un id de encuesta: ");
        int idEncuesta= scanner.nextInt();
        scanner.nextLine();

        //QUE ES PREGUNTA IMPORT DATA?????
        /*
        try {
            int idPregunta = ctrlDominioEncuesta.anadirPregunta(idEncuesta,...); //falta el import data
            System.out.println("Se ha añadido una pregunta con id" +idPregunta);
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
        */
    }

    private static void testExisteEncuesta() {
        System.out.println("\nTEST: ANADIR PREGUNTA");
    
        System.out.print("Introduzca un id de encuesta: ");
        int idEncuesta= scanner.nextInt();
        scanner.nextLine();

        boolean b = ctrlDominioEncuesta.existeEncuesta(idEncuesta);
        if(b) {
            System.out.println("Existe una encuesta con id " +idEncuesta);
        }
        else {
            System.out.println("NO existe encuesta con id " +idEncuesta);
        }

    }

    private static void testObtenerEncuesta() {
        System.out.println("\nTEST: OBTENER INFO ENCUESTA");
    
        System.out.print("Introduzca un id de encuesta: ");
        int idEncuesta= scanner.nextInt();
        scanner.nextLine();
        try {
            Encuesta e = ctrlDominioEncuesta.getEncuesta(idEncuesta);
            System.out.println("ID de la encuesta: " +e.getIdEncuesta());
            System.out.println("Titulo de la encuesta: " +e.getTitulo());
            System.out.println("Creador de la encuesta: " +e.getIdCreador());
            System.out.println("Para las preguntas introduzca 1 ");
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        }
    }

    private static void testObtenerAllEncuestas() {
        System.out.println("\nTEST: OBTENER INFO ALL ENCUESTAS");
        TreeMap<Integer, Encuesta> encuestas = ctrlDominioEncuesta.getAllEncuestas();
        System.out.println("No se mostraran los ids de las preguntas.");
        for(Encuesta e : encuestas.values())
        {
            System.out.println("ID de la encuesta: " +e.getIdEncuesta());
            System.out.println("Titulo de la encuesta: " +e.getTitulo());
            System.out.println("Creador de la encuesta: " +e.getIdCreador());
            System.out.println("------------------------");  
        }
        
    }
}