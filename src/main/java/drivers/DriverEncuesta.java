package drivers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import domain.*;
import domaincontrollers.*;
import persistence.GestorPersistencia;

/**
 * Driver interactivo para probar CtrlDominioEncuesta
 * 
 * @author Peiyu Wang
 */
public class DriverEncuesta {

    private static CtrlDominioEncuesta ctrlEncuesta;
    private static CtrlDominioPregunta ctrlPregunta;
    private static CtrlDominioRespuesta ctrlRespuesta;
    private static CtrlDominioUsuario ctrlUsuario;
    private static GestorPersistencia gestor;

    private static Scanner scanner;

    private static int idEncuestaPrueba;

    public static void main(String[] args) {
        inicializarControladores();
        crearDatosPrueba();

        scanner = new Scanner(System.in);
        boolean salir = false;

        System.out.println("DRIVER INTERACTIVO - CONTROLADOR DE DOMINIO ENCUESTA");

        while (!salir) {
            mostrarMenu();
            String opcion = scanner.nextLine().trim();
            try {
                switch (opcion) {
                    case "1": 
                        crearEncuesta(); 
                        break;
                    case "2": 
                        anadirPregunta(); 
                        break;
                    case "3": 
                        obtenerPreguntas(); 
                        break;
                    case "4": 
                        modificarTitulo(); 
                        break;
                    case "5": 
                        eliminarEncuesta(); 
                        break;
                    case "6": 
                        existeEncuesta(); 
                        break;
                    case "7": 
                        obtenerEncuesta(); 
                        break;
                    case "8": 
                        obtenerAllEncuestas(); 
                        break;
                    case "9": 
                        obtenerRespuestasEncuesta(); 
                        break;
                    case "10":
                        importarEncuesta();
                        break;
                    case "0": 
                        salir = true; 
                        break;
                    default: System.out.println("Opcion no valida.");
                }
            } catch (Exception e) {
                System.out.println("ERROR: " + e.getMessage());
                System.out.println("Tipo: " + e.getClass().getSimpleName());
            }

            if (!salir) {
                System.out.println("\nPulsa ENTER para continuar...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }

    private static void inicializarControladores() {
        gestor = GestorPersistencia.getInstance();

        ctrlRespuesta = CtrlDominioRespuesta.getInstance(gestor, null, null, null);
        ctrlPregunta = CtrlDominioPregunta.getInstance(gestor, ctrlRespuesta, null);
        ctrlUsuario = CtrlDominioUsuario.getInstance(gestor, ctrlRespuesta, ctrlPregunta);
        ctrlEncuesta = CtrlDominioEncuesta.getInstance(gestor, ctrlPregunta, ctrlRespuesta, ctrlUsuario);

        // actualizar referencias circulares
        ctrlRespuesta = CtrlDominioRespuesta.getInstance(gestor, ctrlUsuario, ctrlEncuesta, ctrlPregunta);
        ctrlPregunta = CtrlDominioPregunta.getInstance(gestor, ctrlRespuesta, ctrlEncuesta);
    }

    private static void crearDatosPrueba() {
        // crear usuarios
        
        ctrlUsuario.registrarUsuario("ana", "pass123", "pass123");
        ctrlUsuario.registrarUsuario("pol", "pass123", "pass123");
        ctrlUsuario.registrarUsuario("joan", "pass123", "pass123");
       

        // crear una encuesta de prueba
        idEncuestaPrueba = ctrlEncuesta.crearEncuesta("ana", "Encuesta prueba");

    // crear algunas preguntas con ctrlPregunta (guardar los ids para crear respuestas)
    List<String> ops = new ArrayList<>();
    ops.add("Si");
    ops.add("No");
    int idP_opUnica = ctrlPregunta.crearPreguntaOpcionUnica(idEncuestaPrueba, "¿Te gusta programar?", ops, false);
    int idP_numerica = ctrlPregunta.crearPreguntaNumerica(idEncuestaPrueba, "¿Cuántas horas al dia programas?", 0, 24);
    int idP_libre = ctrlPregunta.crearPreguntaLibre(idEncuestaPrueba, "Comentarios sobre la encuesta");

    // Añadir respuestas de prueba para las preguntas creadas
    // Respuestas a pregunta opción única (indices 0=Si, 1=No)
    ctrlRespuesta.darRespuesta("ana", idP_opUnica, Integer.valueOf(0));
    ctrlRespuesta.darRespuesta("pol", idP_opUnica, Integer.valueOf(1));

    // Respuestas a pregunta numérica
    ctrlRespuesta.darRespuesta("ana", idP_numerica, Double.valueOf(2.0));
    ctrlRespuesta.darRespuesta("joan", idP_numerica, Double.valueOf(5.0));

    // Respuestas a pregunta libre
    ctrlRespuesta.darRespuesta("pol", idP_libre, "Me gusta mucho");
    ctrlRespuesta.darRespuesta("joan", idP_libre, "Podría mejorar") ;
    }

    private static void mostrarMenu() {
        System.out.println("\n=== MENU ENCUESTA ===");
        System.out.println("1. Crear encuesta");
        System.out.println("2. Añadir pregunta a encuesta");
        System.out.println("3. Obtener preguntas de una encuesta");
        System.out.println("4. Modificar titulo de una encuesta");
        System.out.println("5. Eliminar encuesta");
        System.out.println("6. Comprobar existencia de encuesta");
        System.out.println("7. Obtener info de una encuesta");
        System.out.println("8. Listar todas las encuestas");
        System.out.println("9. Obtener respuestas de una encuesta");
        System.out.println("10. Importar Encuesta");
        System.out.println("0. Salir");
        System.out.print("Escoge una opción: ");
    }

    /**
     * Muestra los usuarios disponibles.
     */
    private static void mostrarUsuariosDisponibles() {
        System.out.println("Usuarios disponibles: ana, pol, joan");
    }

    private static void crearEncuesta() {
        System.out.println("\n--- Crear Encuesta ---");
        mostrarUsuariosDisponibles();
        System.out.print("Nombre usuario creador: ");
        String usuario = scanner.nextLine().trim();
        // Solo permitir crear encuestas con usuarios previamente registrados en crearDatosPrueba
        if (!ctrlUsuario.existeUsuario(usuario)) {
            System.out.println("ERROR: Usa uno de los usuarios mostrados.");
            return;
        }
        System.out.print("Titulo encuesta: ");
        String titulo = scanner.nextLine();
        int id = ctrlEncuesta.crearEncuesta(usuario, titulo);
        System.out.println("Encuesta creada con id: " + id);
    }

    private static void anadirPregunta() {
        System.out.println("\n--- Anadir Pregunta ---");
        System.out.print("ID encuesta: ");
        int idE = Integer.parseInt(scanner.nextLine().trim());
        System.out.println("Tipo de pregunta: 1=libre, 2=numerica, 3=opcion_unica, 4=opcion_multiple");
        System.out.print("Introduzca un numero: ");
        String tipo = scanner.nextLine().trim();

        try {
            switch (tipo) {
                case "1":
                    System.out.print("Enunciado: ");
                    String en = scanner.nextLine();
                    int idP1 = ctrlPregunta.crearPreguntaLibre(idE, en);
                    System.out.println("Pregunta libre creada id=" + idP1);
                    break;
                case "2":
                    System.out.print("Enunciado: ");
                    String enn = scanner.nextLine();
                    System.out.print("Rango min: ");
                    int min = Integer.parseInt(scanner.nextLine().trim());
                    System.out.print("Rango max: ");
                    int max = Integer.parseInt(scanner.nextLine().trim());
                    int idP2 = ctrlPregunta.crearPreguntaNumerica(idE, enn, min, max);
                    System.out.println("Pregunta numerica creada id=" + idP2);
                    break;
                case "3":
                    System.out.print("Enunciado: ");
                    String eun = scanner.nextLine();
                    System.out.println("Introduce opciones separadas por comas (ej: A,B,C): ");
                    String line = scanner.nextLine();
                    String[] parts = line.split(",");
                    List<String> opciones = new ArrayList<>();
                    for (String s : parts) opciones.add(s.trim());
                    int idP3 = ctrlPregunta.crearPreguntaOpcionUnica(idE, eun, opciones, false);
                    System.out.println("Pregunta opcion unica creada id=" + idP3);
                    break;
                case "4":
                    System.out.print("Enunciado: ");
                    String eum = scanner.nextLine();
                    System.out.println("Introduce opciones separadas por comas (ej: A,B,C): ");
                    String line2 = scanner.nextLine();
                    String[] parts2 = line2.split(",");
                    List<String> opciones2 = new ArrayList<>();
                    for (String s : parts2) opciones2.add(s.trim());
                    System.out.print("Numero maximo de opciones seleccionables: ");
                    int numMax = Integer.parseInt(scanner.nextLine().trim());
                    int idP4 = ctrlPregunta.crearPreguntaOpcionMultiple(idE, eum, opciones2, numMax);
                    System.out.println("Pregunta opcion multiple creada id=" + idP4);
                    break;
                default:
                    System.out.println("Tipo no valido.");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void obtenerPreguntas() {
        System.out.println("\n--- Obtener Preguntas de una Encuesta ---");
        System.out.print("ID encuesta: ");
        int idE = Integer.parseInt(scanner.nextLine().trim());
        try {
            List<Integer> ids = ctrlEncuesta.getIdPreguntasEncuesta(idE);
            if (ids.isEmpty()) {
                System.out.println("La encuesta no tiene preguntas.");
                return;
            }
            System.out.println("Preguntas (id : enunciado):");
            for (Integer idP : ids) {
                Pregunta p = ctrlPregunta.getPregunta(idP);
                System.out.println("  " + idP + " : " + p.getEnunciado());
            }
        } catch (IllegalArgumentException | IllegalStateException e) {  
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void modificarTitulo() {
        System.out.println("\n--- Modificar Titulo ---");
        System.out.print("ID encuesta: ");
        int idE = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Nuevo titulo: ");
        String nuevo = scanner.nextLine();
        try {
            ctrlEncuesta.modificarTituloEncuesta(idE, nuevo);
            System.out.println("Titulo modificado.");
        } catch (IllegalArgumentException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void eliminarEncuesta() {
        System.out.println("\n--- Eliminar Encuesta ---");
        System.out.print("ID encuesta: ");
        int idE = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Estas seguro? (s/n): ");
        String c = scanner.nextLine().trim().toLowerCase(); // a minusculas
        if (!(c.equals("s") || c.equals("si"))) { 
            System.out.println("Cancelado."); 
            return; 
        }
        try {
            ctrlEncuesta.eliminarEncuesta(idE);
            System.out.println("Encuesta eliminada.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void existeEncuesta() {
        System.out.println("\n--- Existe Encuesta ---");
        System.out.print("ID encuesta: ");
        int idE = Integer.parseInt(scanner.nextLine().trim());
        boolean b = ctrlEncuesta.existeEncuesta(idE);
        if(b) System.out.println("Existe.");
        else System.out.println("No existe.");
    }

    private static void obtenerEncuesta() {
        System.out.println("\n--- Obtener Encuesta ---");
        System.out.print("ID encuesta: ");
        int idE = Integer.parseInt(scanner.nextLine().trim());
        try {
            Encuesta e = ctrlEncuesta.getEncuesta(idE);
            System.out.println("ID: " + e.getIdEncuesta());
            System.out.println("Titulo: " + e.getTitulo());
            System.out.println("Creador: " + e.getIdCreador());
            System.out.println("Preguntas: " + e.getIdPreguntas());
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void obtenerAllEncuestas() {
        System.out.println("\n--- Todas las Encuestas ---");
        HashMap<Integer, Encuesta> all = ctrlEncuesta.getAllEncuestas();
        System.out.println("Total: " + all.size());
        for (Encuesta e : all.values()) {
            System.out.println("ID=" + e.getIdEncuesta() + " Titulo='" + e.getTitulo() + "' Creador='" + e.getIdCreador() + "'");
        }
    }

    private static void obtenerRespuestasEncuesta() {
        System.out.println("\n--- Respuestas de Encuesta ---");
        System.out.print("ID encuesta: ");
        int idE = Integer.parseInt(scanner.nextLine().trim());
        try {
            Map<Pregunta, Set<Respuesta>> mapa = ctrlEncuesta.getRespuestasEncuesta(idE);
            for (Map.Entry<Pregunta, Set<Respuesta>> entry : mapa.entrySet()) {
                Pregunta p = entry.getKey();
                System.out.println("Pregunta: " + p.getIdPregunta() + " - " + p.getEnunciado());
                for (Respuesta r : entry.getValue()) {
                    System.out.println("  Respuesta id = " + r.getId() + " usuario = " + r.getNomUsuario() + " tipo = " + r.getTipo());
                }
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    private static void importarEncuesta() {
        System.out.println("\n--- Importar encuesta ---");
        System.out.print("Usuario activo (debe iniciar sesión): ");
        String usuario = scanner.nextLine().trim();
        
        try {
            // Iniciar sesión para establecer usuario activo
            ctrlUsuario.iniciarSesion(usuario, "pass123");
            
            System.out.print("Ruta del archivo JSON: ");
            String ruta = scanner.nextLine().trim();
            java.io.File fichero = new java.io.File(ruta);
            
            if (!fichero.exists()) {
                System.out.println("ERROR: El archivo no existe");
                return;
            }
            
            int idE = ctrlEncuesta.importarEncuesta(fichero);
            System.out.println("Encuesta importada con ID: " + idE);
            
            // Mostrar información de la encuesta importada
            Encuesta e = ctrlEncuesta.getEncuesta(idE);
            System.out.println("Titulo: " + e.getTitulo());
            System.out.println("Preguntas: " + e.getIdPreguntas().size());
            
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        } finally {
            ctrlUsuario.cerrarSesionActual();
        }
    }
}
