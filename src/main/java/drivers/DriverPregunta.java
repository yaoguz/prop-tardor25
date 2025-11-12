package drivers;

import java.util.Scanner;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;

import domaincontrollers.CtrlDominioUsuario;
import domaincontrollers.CtrlDominioEncuesta;
import domaincontrollers.CtrlDominioPregunta;
import domaincontrollers.CtrlDominioRespuesta;
//import domain.Encuesta;
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

/*Clase que testea la clase CtrlDominioPregunta */
public class DriverPregunta {
    private static CtrlDominioPregunta ctrlPregunta;
    private static CtrlDominioUsuario ctrlUsuario;
    private static CtrlDominioEncuesta ctrlEncuesta;
    private static CtrlDominioRespuesta ctrlRespuesta;
    private static Scanner scanner;
    private static String nomU;
    private static int idE;

    /*
    Al principio tendrá una encuesta con 5 preguntaspor defecto para que pueda
    interactuar con ella sin tener que darle siempre a la opción de crear pregunta.
    */

    public static void main (String[] args) {
        // Inicializar dependencias (capa real, no mocks)
        ctrlUsuario = new CtrlDominioUsuario();
        ctrlEncuesta = new CtrlDominioEncuesta();
        ctrlRespuesta = new CtrlDominioRespuesta();
        ctrlPregunta = new CtrlDominioPregunta();
        scanner = new Scanner(System.in);

        nomU = ctrlUsuario.registrarUsuario("Test", "1234", "1234");
        idE = ctrlEncuesta.crearEncuesta(nomU, "Título", ctrlUsuario);
        int idPL = ctrlPregunta.crearPreguntaLibre(idE,  "Enunciado libre:", ctrlEncuesta);
        int idRL = ctrlRespuesta.darRespuesta(nomU, idPL, "Respuesta Libre", ctrlUsuario, ctrlPregunta);
        int idPN = ctrlPregunta.crearPreguntaNumerica(idE,  "Enunciado numérico:", 2, 8, ctrlEncuesta);
        int idRN = ctrlRespuesta.darRespuesta(nomU, idPN, 3, ctrlUsuario, ctrlPregunta);
        ArrayList<String> ops = new ArrayList<>();
        ops.add("op1");
        ops.add("op2");
        ops.add("op3");
        int idPOM = ctrlPregunta.crearPreguntaOpcionMultiple(idE,  "Enunciado opción múltiple:", ops, 2, ctrlEncuesta);
        ArrayList<Integer> idxs = new ArrayList<>();
        idxs.add(0);
        idxs.add(2);
        int idROM = ctrlRespuesta.darRespuesta(nomU, idPOM, idxs, ctrlUsuario, ctrlPregunta);
        int idPOUN = ctrlPregunta.crearPreguntaOpcionUnica(idE,  "Enunciado opción única no ordenada:", ops, false, ctrlEncuesta);
        int idROUN = ctrlRespuesta.darRespuesta(nomU, idPOUN, 1, ctrlUsuario, ctrlPregunta);
        int idPOUO = ctrlPregunta.crearPreguntaOpcionUnica(idE,  "Enunciado opción única ordenada:", ops, false, ctrlEncuesta);
        int idROUO = ctrlRespuesta.darRespuesta(nomU, idPOUO, 1, ctrlUsuario, ctrlPregunta);


        boolean salir = false;

        System.out.println("DRIVER - CONTROLADOR DE PREGUNTA");
        System.out.println("Dispone de un usuario con nombre: " +nomU);
        System.out.println("que tiene una encuesta creada con id: " +idE);
        System.out.println("que tiene 5 preguntas:");
        System.out.println("- Una pregunta libre con id: " +idPL);
        System.out.println("  que el mismo usuario ha respuesto con la respuesta con id: " +idRL);
        System.out.println("- Una pregunta numérica con id: " +idPN);
        System.out.println("  que el mismo usuario ha respuesto con la respuesta con id: " +idRN);
        System.out.println("- Una pregunta de opción múltiple con id: " +idPOM);
        System.out.println("  que el mismo usuario ha respuesto con la respuesta con id: " +idROM);
        System.out.println("- Una pregunta de opción única no ordenada con id: " +idPOUN);
        System.out.println("  que el mismo usuario ha respuesto con la respuesta con id: " +idROUN);
        System.out.println("- Una pregunta de opción única ordenada con id: " +idPOUO);
        System.out.println("  que el mismo usuario ha respuesto con la respuesta con id: " +idROUO);
        System.out.println("Todas las acciones que necesiten una encuesta o usuario usarán los ya mencionados");

        while (!salir) {
            mostrarMenu();

            int opcion = leerEntero();
            
            switch (opcion) {
                case 1:
                    testObtenerRespuestas();
                    break;
                case 2:
                    testCrearPregunta();
                    break;
                case 3:
                    testModificarPregunta();
                case 4:
                    testEliminarPregunta();
                case 5:
                    testGetPregunta();
                case 6:
                    testGetAllPreguntas();
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
        System.out.println("1. Obtener respuestas de una pregunta");
        System.out.println("2. Crear pregunta");
        System.out.println("3. Modificar pregunta [por implementar]");
        System.out.println("4. Eliminar pregunta");
        System.out.println("5. Obtener pregunta");
        System.out.println("6. Obtener todas las preguntas");
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
        
        System.out.print("Introduzca el id de la pregunta cuyas respuestas quiere obtener: ");
        int idPregunta = scanner.nextInt();

        try {
            List<Respuesta> respuestas = ctrlPregunta.getRespuestasPregunta(idPregunta, ctrlRespuesta);
            System.out.println("Respuestas de la pregunta " +idPregunta +":");
            for(Respuesta r : respuestas){
                System.out.println("Id: " +r.getId() +". Usuario: " +r.getNomUsuario() +". Respuesta: " +r.getRespuesta().toString());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nError inesperado: " + e.getMessage());
        }
    }

    private static void testCrearPregunta(){
        System.out.println("\nTEST: CREAR UNA PREGUNTA");
        
        System.out.print("Introduzca el enunciado de la pregunta a crear: ");
        String enunciado = scanner.nextLine();

        System.out.println("Indique el tipo de pregunta que quiere crear:");
        System.out.println("[ 0 -> Libre | 1 -> Numérica | 2 -> Opción múltiple | 3 -> Opción única ]");
        int tipo = -1;
        while(tipo < 0 || tipo > 3){
            tipo = scanner.nextInt();
            if(tipo < 0 || tipo > 3) System.out.println("Entrada no válida. Introduzca un número válido (0, 1, 2 o 3).");
        }
        try {
            int idPregunta = 0;
            switch(tipo){
                case 0: //libre
                    idPregunta = ctrlPregunta.crearPreguntaLibre(idE, enunciado, ctrlEncuesta);
                    break;
                case 1: //numérica
                    System.out.print("Introduzca el número mínimo(incluido) del rango en el que puede estar la respuesta a la pregunta: ");
                    int min = scanner.nextInt();
                    System.out.print("Introduzca el número máximo(incluido) del rango en el que puede estar la respuesta a la pregunta: ");
                    int max = scanner.nextInt();
                    idPregunta = ctrlPregunta.crearPreguntaNumerica(idE, enunciado, min, max, ctrlEncuesta);
                    break;
                case 2: //múltiple
                    ArrayList<String> opsM = new ArrayList<>();
                    String sm = null;
                    while(sm != "_fin"){
                        System.out.print("Introduzca una opción de la pregunta (escribe _fin para dejar de añadir opciones): ");
                        sm = scanner.nextLine();
                        if(sm != "_fin") opsM.add(sm);
                    }
                    System.out.print("Introduzca el número máximo(incluido) de opciones seleccionables para la pregunta: ");
                    int maxOpc = scanner.nextInt();
                    idPregunta = ctrlPregunta.crearPreguntaOpcionMultiple(idE, enunciado, opsM, maxOpc, ctrlEncuesta);
                    break;
                case 3: //única
                    ArrayList<String> opsU = new ArrayList<>();
                    String su = null;
                    while(su != "_fin"){
                        System.out.print("Introduzca una opción de la pregunta (escribe _fin para dejar de añadir opciones): ");
                        su = scanner.nextLine();
                        if(su != "_fin") opsU.add(su);
                    }
                    System.out.print("Introduzca 1 si las opciones siguen un orden o 0 si no lo siguen: ");
                    int ord = scanner.nextInt();
                    idPregunta = ctrlPregunta.crearPreguntaOpcionUnica(idE, enunciado, opsU, (ord==1), ctrlEncuesta);
                    break;
            }
            System.out.println("Pregunta creada con id: " +idPregunta);
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nError inesperado: " + e.getMessage());
        }

    }

    private static void testModificarPregunta() {
        System.out.println("\nTEST: MODIFICAR UNA PREGUNTA");
        
        System.out.print("Introduzca el id de la pregunta a modificar: ");
        int idPregunta = scanner.nextInt();

        CtrlDominioPregunta.PreguntaImportData info = new CtrlDominioPregunta.PreguntaImportData();
        
        System.out.print("Introduzca el enunciado modificado: ");
        info.enunciado = scanner.nextLine();

        //por acabar

        try {
            int idNuevaRespuesta = ctrlPregunta.modificarPregunta(idPregunta, info, ctrlRespuesta, ctrlEncuesta, ctrlUsuario)
            System.out.println("Pregunta modificada. Ahora tiene el id: " +idPregunta);
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nError inesperado: " + e.getMessage());
        }
    }

    private static void testEliminarPregunta() {
        System.out.println("\nTEST: ELIMINAR UNA PREGUNTA");
        
        System.out.print("Introduzca el id de la pregunta a eliminar: ");
        int idPregunta = scanner.nextInt();

        try {
            ctrlPregunta.eliminarPregunta(idPregunta, ctrlRespuesta, ctrlEncuesta, ctrlUsuario)
            System.out.println("Eliminada la pregunta con id: " +idPregunta);
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nError inesperado: " + e.getMessage());
        }
    }

    private static void testGetPregunta() {
        System.out.println("\nTEST: OBTENER UNA PREGUNTA");
        
        System.out.print("Introduzca el id de la pregunta a obtener: ");
        int idPregunta = scanner.nextInt();

        try {
            Pregunta p = ctrlPregunta.getPregunta(idPregunta);
            System.out.println("Información de la pregunta:");
            System.out.println("Id: " +p.getIdPregunta());
            System.out.println("Id de su encuesta: " +p.getIdEncuesta());
            System.out.println("Enunciado: " +p.getEnunciado());
            System.out.println("Tipo: " +p.tipoPregunta());
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nError inesperado: " + e.getMessage());
        }
    }

    private static void testGetAllPreguntas() {
        System.out.println("\nTEST: OBTENER TODAS LAS PREGUNTAS");

        try {
            TreeMap<Integer, Pregunta> preguntas = ctrlPregunta.getAllPreguntas();
            System.out.println("Preguntas:");
            for(Pregunta p : preguntas.values()){
                System.out.println("Id: " +p.getIdPregunta());
            }
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nError inesperado: " + e.getMessage());
        }
    }
}