package drivers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

import domain.*;
import domaincontrollers.*;

// pnd: documentación

/**
 * Driver interactivo para probar CtrlDominioRespuesta.
 * Prueba creación, modificación, eliminación y consulta de respuestas.
 * 
 * @author Yaoyao Guo Zhang (yaoyao.guo@estudiantat.upc.edu)
 */
public class DriverRespuesta {
    
    private static CtrlDominioRespuesta ctrlRespuesta;
    private static CtrlDominioUsuario ctrlUsuario;
    private static CtrlDominioPregunta ctrlPregunta;
    private static CtrlDominioEncuesta ctrlEncuesta;
    private static Scanner scanner;
    
    private static int idEncuesta;
    private static int idPreguntaNumerica;
    private static int idPreguntaOpcionUnica;
    private static int idPreguntaOpcionMultiple;
    private static int idPreguntaLibre;
    
    public static void main(String[] args) {
        inicializarControladores();
        crearDatosDePrueba();
        
        scanner = new Scanner(System.in);
        boolean salir = false;
        
        System.out.println("DRIVER INTERACTIVO - CONTROLADOR DE DOMINIO RESPUESTA");
        
        while (!salir) {
            mostrarMenu();
            String opcion = scanner.nextLine().trim();
            
            try {
                switch (opcion) {
                    case "1":
                        crearRespuestaNumerica();
                        break;
                    case "2":
                        crearRespuestaOpcionUnica();
                        break;
                    case "3":
                        crearRespuestaOpcionMultiple();
                        break;
                    case "4":
                        crearRespuestaLibre();
                        break;
                    case "5":
                        modificarRespuesta();
                        break;
                    case "6":
                        eliminarRespuesta();
                        break;
                    case "7":
                        consultarRespuesta();
                        break;
                    case "8":
                        listarTodasRespuestas();
                        break;
                    case "9":
                        listarRespuestasUsuario();
                        break;
                    case "10":
                        listarRespuestasPregunta();
                        break;
                    case "0":
                        salir = true;
                        break;
                    default:
                        System.out.println("Opcion no valida.");
                }
            } catch (Exception e) {
                System.out.println("\nERROR: " + e.getMessage());
                System.out.println("Tipo: " + e.getClass().getSimpleName());
            }
            
            if (!salir) {
                System.out.println("\nPresiona ENTER para continuar...");
                scanner.nextLine();
            }
        }
        scanner.close();
    }
    
    private static void inicializarControladores() {
        ctrlRespuesta = new CtrlDominioRespuesta();
        ctrlUsuario = new CtrlDominioUsuario();
        ctrlPregunta = new CtrlDominioPregunta();
        ctrlEncuesta = new CtrlDominioEncuesta();
    }
    
    private static void crearDatosDePrueba() {        
        // Crear usuarios
        ctrlUsuario.registrarUsuario("ana", "pass123", "pass123");
        ctrlUsuario.registrarUsuario("pol", "pass123", "pass123");
        ctrlUsuario.registrarUsuario("joan", "pass123", "pass123");
        
        // Crear encuesta
        idEncuesta = ctrlEncuesta.crearEncuesta("ana", "Encuesta Habitos Deportivos", ctrlUsuario);
        
        // pregunta numerica
        // Map<String, Object> atributosEspecificos = new HashMap<>();
        // atributosEspecificos.put("min", 0);
        // atributosEspecificos.put("max", 40);
        // ctrlPregunta.crearPregunta(idEncuesta1, 
        //             "¿Cuántas horas a la semana haces deporte?", 
        //             "numerica", atributosEspecificos, ctrlEncuesta);

        // uso directamente crearPreguntaNumerica pq se sabe que es numerica

        // Crear pregunta numerica
        idPreguntaNumerica = ctrlPregunta.crearPreguntaNumerica(idEncuesta, 
            "Cuántas horas a la semana haces deporte?", 
            0, 40, ctrlEncuesta);
        
        // pregunta opcion unica
        List<String> opcionesNivel = new ArrayList<>();
        opcionesNivel.add("Bajo");
        opcionesNivel.add("Medio");
        opcionesNivel.add("Alto");
        idPreguntaOpcionUnica = ctrlPregunta.crearPreguntaOpcionUnica(idEncuesta, 
            "Cuál es tu nivel de actividad física?",
            opcionesNivel, true, ctrlEncuesta);
        
        // pregunta opcion multiple
        List<String> opcionesDeportes = new ArrayList<>();
        opcionesDeportes.add("Futbol");
        opcionesDeportes.add("Básquet");
        opcionesDeportes.add("Natación");
        opcionesDeportes.add("Running");
        opcionesDeportes.add("Ciclismo");
        idPreguntaOpcionMultiple = ctrlPregunta.crearPreguntaOpcionMultiple(
            idEncuesta, "Qué deportes practicas? (seleccion multiple)",
            opcionesDeportes, 5, ctrlEncuesta);
        
        // pregunta libre
        idPreguntaLibre = ctrlPregunta.crearPreguntaLibre(idEncuesta,
            "Describe tu rutina de ejercicio favorita", ctrlEncuesta);
    }
    
    private static void mostrarMenu() {
        System.out.println("\n=== MENÚ PRINCIPAL ===");
        System.out.println("1. Crear respuesta numérica");
        System.out.println("2. Crear respuesta de opción única");
        System.out.println("3. Crear respuesta de opción múltiple");
        System.out.println("4. Crear respuesta libre");
        System.out.println("5. Modificar respuesta");
        System.out.println("6. Eliminar respuesta");
        System.out.println("7. Consultar respuesta por ID");
        System.out.println("8. Listar todas las respuestas");
        System.out.println("9. Listar respuestas de un usuario");
        System.out.println("10. Listar respuestas de una pregunta");
        System.out.println("0. Salir");
        System.out.print("Escoge una opción: ");
    }
    
    private static void crearRespuestaNumerica() {
        System.out.println("\n--- Crear Respuesta Numerica ---");
        mostrarUsuariosDisponibles();
        System.out.print("Usuario: ");
        String usuario = scanner.nextLine().trim();
        
        PreguntaNumerica pregunta = (PreguntaNumerica) ctrlPregunta.getPregunta(idPreguntaNumerica);
        System.out.println("\nPregunta: " + pregunta.getEnunciado());
        System.out.println("Rango valido: [" + pregunta.getRangoInf() + ", " + pregunta.getRangoSup() + "]");
        System.out.print("Respuesta: ");
        double valor = Double.parseDouble(scanner.nextLine().trim());
        
        int idRespuesta = ctrlRespuesta.darRespuesta(usuario, idPreguntaNumerica, 
            valor, ctrlUsuario, ctrlPregunta);
        
        System.out.println("\nRespuesta creada con ID: " + idRespuesta);
    }
    
    private static void crearRespuestaOpcionUnica() {
        System.out.println("\n--- Crear Respuesta Opcion Unica ---");
        mostrarUsuariosDisponibles();
        System.out.print("Usuario: ");
        String usuario = scanner.nextLine().trim();
        
        PreguntaOpcionUnica pregunta = (PreguntaOpcionUnica) ctrlPregunta.getPregunta(idPreguntaOpcionUnica);
        System.out.println("\nPregunta: " + pregunta.getEnunciado());
        System.out.println("Opciones:");
        for (int i = 0; i < pregunta.getOpciones().size(); i++) {
            System.out.println("  " + i + ". " + pregunta.getOpciones().get(i));
        }
        System.out.print("Selecciona opcion (indice): ");
        int indice = Integer.parseInt(scanner.nextLine().trim());
        
        int idRespuesta = ctrlRespuesta.darRespuesta(usuario, idPreguntaOpcionUnica, 
            indice, ctrlUsuario, ctrlPregunta);
        
        System.out.println("\nRespuesta creada con ID: " + idRespuesta);
    }
    
    private static void crearRespuestaOpcionMultiple() {
        System.out.println("\n--- Crear Respuesta Opcion Multiple ---");
        mostrarUsuariosDisponibles();
        System.out.print("Usuario: ");
        String usuario = scanner.nextLine().trim();
        
        PreguntaOpcionMultiple pregunta = (PreguntaOpcionMultiple) ctrlPregunta.getPregunta(idPreguntaOpcionMultiple);
        System.out.println("\nPregunta: " + pregunta.getEnunciado());
        System.out.println("Opciones:");
        for (int i = 0; i < pregunta.getOpciones().size(); i++) {
            System.out.println("  " + i + ". " + pregunta.getOpciones().get(i));
        }
        System.out.println("Máximo de opciones: " + pregunta.getNumMaxOpc());
        System.out.print("Selecciona opciones (separadas por comas, sin espacios, ej: 0,2,4): ");
        String input = scanner.nextLine().trim();
        
        String[] partes = input.split(",");
        int[] indices = new int[partes.length];
        for (int i = 0; i < partes.length; i++) {
            indices[i] = Integer.parseInt(partes[i].trim());
        }
        
        int idRespuesta = ctrlRespuesta.darRespuesta(usuario, 
            idPreguntaOpcionMultiple, indices, ctrlUsuario, ctrlPregunta);
        
        System.out.println("\nRespuesta creada con ID: " + idRespuesta);
    }
    
    private static void crearRespuestaLibre() {
        System.out.println("\n--- Crear Respuesta Libre ---");
        mostrarUsuariosDisponibles();
        System.out.print("Usuario: ");
        String usuario = scanner.nextLine().trim();
        
        Pregunta pregunta = ctrlPregunta.getPregunta(idPreguntaLibre);
        System.out.println("\nPregunta: " + pregunta.getEnunciado());
        System.out.print("Respuesta: ");
        String texto = scanner.nextLine();
        
        int idRespuesta = ctrlRespuesta.darRespuesta(usuario, idPreguntaLibre, 
            texto, ctrlUsuario, ctrlPregunta);
        
        System.out.println("\nRespuesta creada con ID: " + idRespuesta);
    }
    
    private static void modificarRespuesta() {
        System.out.println("\n--- Modificar Respuesta ---");
        listarTodasRespuestas();
        System.out.print("\nID de la respuesta a modificar: ");
        int idRespuesta = Integer.parseInt(scanner.nextLine().trim());
        Respuesta r = ctrlRespuesta.getRespuesta(idRespuesta);
        System.out.println("\nRespuesta actual:");
        mostrarRespuestaDetallada(r);
        
        if (r instanceof RespuestaNumerica) {
            System.out.print("\nNuevo valor: ");
            double nuevoValor = Double.parseDouble(scanner.nextLine().trim());
            ctrlRespuesta.modificarRespuesta(idRespuesta, nuevoValor);
        } 
        else if (r instanceof RespuestaOpcionUnica) {
            PreguntaOpcionUnica pregunta = (PreguntaOpcionUnica) ctrlPregunta.getPregunta(r.getIdPregunta());
            System.out.println("\nOpciones:");
            for (int i = 0; i < pregunta.getOpciones().size(); i++) {
                System.out.println("  " + i + ". " + pregunta.getOpciones().get(i));
            }
            System.out.print("Nuevo indice: ");
            int nuevoIndice = Integer.parseInt(scanner.nextLine().trim());
            ctrlRespuesta.modificarRespuesta(idRespuesta, nuevoIndice);
        } 
        else if (r instanceof RespuestaOpcionMultiple) {
            PreguntaOpcionMultiple pregunta = (PreguntaOpcionMultiple) ctrlPregunta.getPregunta(r.getIdPregunta());
            System.out.println("\nOpciones:");
            for (int i = 0; i < pregunta.getOpciones().size(); i++) {
                System.out.println("  " + i + ". " + pregunta.getOpciones().get(i));
            }
            System.out.print("Nuevos indices (separados por comas, sin espacios): ");
            String input = scanner.nextLine().trim();
            String[] partes = input.split(",");
            int[] nuevosIndices = new int[partes.length];
            for (int i = 0; i < partes.length; i++) {
                nuevosIndices[i] = Integer.parseInt(partes[i].trim());
            }
            ctrlRespuesta.modificarRespuesta(idRespuesta, nuevosIndices);
        } 
        else if (r instanceof RespuestaLibre) {
            System.out.print("\nNuevo texto: ");
            String nuevoTexto = scanner.nextLine();
            ctrlRespuesta.modificarRespuesta(idRespuesta, nuevoTexto);
        }
        System.out.println("Respuesta modificada.");
    }
    
    private static void eliminarRespuesta() {
        System.out.println("\n--- Eliminar Respuesta ---");
        listarTodasRespuestas();
        System.out.print("\nID de la respuesta a eliminar: ");
        int idRespuesta = Integer.parseInt(scanner.nextLine().trim());
        Respuesta r = ctrlRespuesta.getRespuesta(idRespuesta);
        
        System.out.println("\nEstás seguro? (s/n): ");
        mostrarRespuestaDetallada(r);
        System.out.print("Confirmar: ");
        String confirmacion = scanner.nextLine().trim().toLowerCase();
        if (confirmacion.equals("s") || confirmacion.equals("si")) {
            ctrlRespuesta.eliminarRespuesta(idRespuesta, ctrlUsuario, ctrlPregunta);
            System.out.println("\nRespuesta eliminada.");
        } 
        else System.out.println("Eliminacion cancelada.");
    }
    
    private static void consultarRespuesta() {
        System.out.println("\n--- Consultar respuesta ---");
        System.out.print("ID de la respuesta: ");
        int idRespuesta = Integer.parseInt(scanner.nextLine().trim());
        Respuesta r = ctrlRespuesta.getRespuesta(idRespuesta);
        mostrarRespuestaDetallada(r);
    }
    
    private static void listarTodasRespuestas() {
        System.out.println("\n--- Todas las respuestas ---");
        TreeMap<Integer, Respuesta> todas = ctrlRespuesta.getAllRespuestas();
        
        if (todas.isEmpty()) {
            System.out.println("No hay respuestas.");
            return;
        }
        
        System.out.println("Total: " + todas.size());
        for (Respuesta r : todas.values()) {
            mostrarRespuestaResumida(r);
        }
    }
    
    private static void listarRespuestasUsuario() {
        System.out.println("\n--- Respuestas por usuario ---");
        mostrarUsuariosDisponibles();
        System.out.print("Usuario: ");
        String nomUsuario = scanner.nextLine().trim();
        Usuario u = ctrlUsuario.getUsuario(nomUsuario);
        List<Integer> idsRespuestas = new ArrayList<>(u.getIdRespuestas());
        
        if (idsRespuestas.isEmpty()) {
            System.out.println("\nEl usuario no tiene respuestas.");
            return;
        }
        
        System.out.println("\nRespuestas de " + nomUsuario + ": " + idsRespuestas.size());
        for (int idResp : idsRespuestas) {
            Respuesta r = ctrlRespuesta.getRespuesta(idResp);
            mostrarRespuestaResumida(r);
        }
    }
    
    private static void listarRespuestasPregunta() {
        System.out.println("\n--- Respuestas de una pregunta ---");
        System.out.println("Preguntas:");
        System.out.println("  " + idPreguntaNumerica + ". Cuantas horas a la semana haces deporte?");
        System.out.println("  " + idPreguntaOpcionUnica + ". Cual es tu nivel de actividad fisica?");
        System.out.println("  " + idPreguntaOpcionMultiple + ". Que deportes practicas?");
        System.out.println("  " + idPreguntaLibre + ". Describe tu rutina de ejercicio favorita");
        
        System.out.print("\nID de la pregunta: ");
        int idPregunta = Integer.parseInt(scanner.nextLine().trim());
        Pregunta p = ctrlPregunta.getPregunta(idPregunta);
        List<Integer> idsRespuestas = new ArrayList<>(p.getIdRespuestas());
        
        if (idsRespuestas.isEmpty()) {
            System.out.println("\nLa pregunta no tiene respuestas.");
            return;
        }        
        System.out.println("\nPregunta: " + p.getEnunciado());
        System.out.println("Respuestas: " + idsRespuestas.size());
        for (int idResp : idsRespuestas) {
            Respuesta r = ctrlRespuesta.getRespuesta(idResp);
            mostrarRespuestaResumida(r);
        }
    }
    
    private static void mostrarRespuestaDetallada(Respuesta r) {
        System.out.println("\nID: " + r.getId());
        System.out.println("Usuario: " + r.getNomUsuario());
        System.out.println("ID pregunta: " + r.getIdPregunta());
        
        Pregunta p = ctrlPregunta.getPregunta(r.getIdPregunta());
        System.out.println("Enunciado: " + p.getEnunciado());
        
        if (r instanceof RespuestaNumerica) {
            RespuestaNumerica rn = (RespuestaNumerica) r;
            System.out.println("Tipo: numérica");
            System.out.println("Valor: " + rn.getRespuesta());
        } 
        else if (r instanceof RespuestaOpcionUnica) {
            RespuestaOpcionUnica ro = (RespuestaOpcionUnica) r;
            PreguntaOpcionUnica po = (PreguntaOpcionUnica) p;
            System.out.println("Tipo: opción única");
            System.out.println("Indice: " + ro.getRespuesta());
            System.out.println("Opcion: " + po.getOpciones().get(ro.getRespuesta()));
        } 
        else if (r instanceof RespuestaOpcionMultiple) {
            RespuestaOpcionMultiple rm = (RespuestaOpcionMultiple) r;
            PreguntaOpcionMultiple pm = (PreguntaOpcionMultiple) p;
            System.out.println("Tipo: opción múltiple");
            System.out.print("Opciones: ");
            int[] indices = rm.getRespuesta();
            for (int i = 0; i < indices.length; i++) {
                System.out.print(pm.getOpciones().get(indices[i]));
                if (i < indices.length - 1) System.out.print(", ");
            }
            System.out.println();
        } 
        else if (r instanceof RespuestaLibre) {
            RespuestaLibre rl = (RespuestaLibre) r;
            System.out.println("Tipo: libre");
            System.out.println("Texto: " + rl.getRespuesta());
        }
    }
    
    private static void mostrarRespuestaResumida(Respuesta r) {
        String tipo = "";
        String valor = "";
        
        if (r instanceof RespuestaNumerica) {
            tipo = "NUM";
            valor = String.valueOf(((RespuestaNumerica) r).getRespuesta());
        } else if (r instanceof RespuestaOpcionUnica) {
            tipo = "OPU";
            valor = "opcion " + ((RespuestaOpcionUnica) r).getRespuesta();
        } else if (r instanceof RespuestaOpcionMultiple) {
            tipo = "OPM";
            int[] indices = ((RespuestaOpcionMultiple) r).getRespuesta();
            valor = indices.length + " opciones";
        } else if (r instanceof RespuestaLibre) {
            tipo = "TXT";
            String texto = ((RespuestaLibre) r).getRespuesta();
            valor = texto.length() > 30 ? texto.substring(0, 27) + "..." : texto;
        }
        
        System.out.printf("  [%3d] %-4s | Usuario: %-10s | Pregunta: %3d | %s%n",
            r.getId(), tipo, r.getNomUsuario(), r.getIdPregunta(), valor);
    }
    
    private static void mostrarUsuariosDisponibles() {
        System.out.println("Usuarios disponibles: ana, pol, joan");
    }
}
