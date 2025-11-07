package drivers;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.TreeMap;

import domain.*;
import domaincontrollers.*;

/**
 * Driver interactivo para probar CtrlDominioRespuesta.
 * 
 * OBJETO DE LA PRUEBA:
 * - Controlador de dominio: CtrlDominioRespuesta
 * - Casos de uso probados:
 *   1. Crear respuestas de todos los tipos (Numérica, Opción única, Opción múltiple, Libre)
 *   2. Modificar respuestas existentes
 *   3. Eliminar respuestas
 *   4. Consultar respuestas por ID
 *   5. Validar integridad referencial con Usuario y Pregunta
 * 
 * ELEMENTOS INTEGRADOS:
 * - CtrlDominioUsuario (gestión de usuarios)
 * - CtrlDominioPregunta (gestión de preguntas)
 * - CtrlDominioEncuesta (gestión de encuestas)
 * - Todas las clases de dominio: Usuario, Pregunta*, Respuesta*
 * 
 * FICHEROS DE DATOS:
 * - No se utilizan ficheros externos (entrada por teclado)
 * 
 * VALORES ESTUDIADOS:
 * - Caja negra: Comportamiento desde interfaz pública
 * - Conjuntos de datos:
 *   a) Respuestas numéricas: valores dentro/fuera de rango
 *   b) Respuestas opción única: índices válidos/inválidos
 *   c) Respuestas opción múltiple: arrays vacíos, completos, duplicados
 *   d) Respuestas libres: textos vacíos, largos, con caracteres especiales
 *   e) Modificaciones: cambios de tipo compatible/incompatible
 *   f) Eliminaciones: efecto en Usuario y Pregunta
 * 
 * EFECTOS ESTUDIADOS:
 * - Integridad referencial: respuestas eliminadas se quitan de Usuario/Pregunta
 * - Polimorfismo: creación y modificación según tipo de pregunta
 * - Validaciones: respuestas inválidas lanzan excepciones apropiadas
 * - IDs autoincrementales únicos
 * 
 * OPERATIVA:
 * 1. El driver crea automáticamente un entorno de prueba (usuarios, encuestas, preguntas)
 * 2. Menú principal con opciones numeradas
 * 3. Entrada de datos por teclado según el tipo de respuesta
 * 4. Visualización del estado del sistema después de cada operación
 * 5. Opción para listar todas las respuestas en cualquier momento
 * 
 * @author Yaoyao Guo Zhang (yaoyao.guo@estudiantat.upc.edu)
 */
public class DriverRespuesta {
    
    private static CtrlDominioRespuesta ctrlRespuesta;
    private static CtrlDominioUsuario ctrlUsuario;
    private static CtrlDominioPregunta ctrlPregunta;
    private static CtrlDominioEncuesta ctrlEncuesta;
    private static Scanner scanner;
    
    // IDs de ejemplo para pruebas
    private static int idEncuesta1;
    private static int idPreguntaNumerica;
    private static int idPreguntaOpcionUnica;
    private static int idPreguntaOpcionMultiple;
    private static int idPreguntaLibre;
    
    public static void main(String[] args) {
        inicializarControladores();
        crearDatosDePrueba();
        
        scanner = new Scanner(System.in);
        boolean salir = false;
        
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║    DRIVER INTERACTIVO - CONTROLADOR DE DOMINIO RESPUESTA    ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println();
        
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
                    case "11":
                        mostrarDatosPrueba();
                        break;
                    case "0":
                        salir = true;
                        System.out.println("\n¡Hasta pronto!");
                        break;
                    default:
                        System.out.println("❌ Opción no válida. Intente de nuevo.");
                }
            } catch (Exception e) {
                System.out.println("\n❌ ERROR: " + e.getMessage());
                System.out.println("Tipo: " + e.getClass().getSimpleName());
            }
            
            if (!salir) {
                System.out.println("\nPresione ENTER para continuar...");
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
        System.out.println("Creando datos de prueba...");
        
        // Crear usuarios
        ctrlUsuario.registrarUsuario("alice", "pass123", "pass123");
        ctrlUsuario.registrarUsuario("bob", "pass123", "pass123");
        ctrlUsuario.registrarUsuario("charlie", "pass123", "pass123");
        
        // Crear encuesta
        idEncuesta1 = ctrlEncuesta.crearEncuesta("alice", "Encuesta Hábitos Deportivos", ctrlUsuario);
        
        // Crear preguntas de diferentes tipos
        idPreguntaNumerica = ctrlPregunta.crearPreguntaNumerica(
            idEncuesta1, 
            "¿Cuántas horas a la semana haces deporte?", 
            0, 
            20,
            ctrlEncuesta
        );
        
        idPreguntaOpcionUnica = ctrlPregunta.crearPreguntaOpcionUnica(
            idEncuesta1,
            "¿Cuál es tu nivel de actividad física?",
            true,  // ordenada
            ctrlEncuesta
        );
        // Añadir opciones manualmente
        PreguntaOpcionUnica pou = (PreguntaOpcionUnica) ctrlPregunta.getPregunta(idPreguntaOpcionUnica);
        pou.addOpcion("Bajo");
        pou.addOpcion("Medio");
        pou.addOpcion("Alto");
        
        idPreguntaOpcionMultiple = ctrlPregunta.crearPreguntaOpcionMultiple(
            idEncuesta1,
            "¿Qué deportes practicas? (selección múltiple)",
            ctrlEncuesta
        );
        // Añadir opciones y límite manualmente
        PreguntaOpcionMultiple pom = (PreguntaOpcionMultiple) ctrlPregunta.getPregunta(idPreguntaOpcionMultiple);
        pom.addOpcion("Fútbol");
        pom.addOpcion("Baloncesto");
        pom.addOpcion("Natación");
        pom.addOpcion("Running");
        pom.addOpcion("Ciclismo");
        pom.setNumMaxOpc(5);
        
        idPreguntaLibre = ctrlPregunta.crearPreguntaLibre(
            idEncuesta1,
            "Describe tu rutina de ejercicio favorita",
            ctrlEncuesta
        );
        
        System.out.println("✅ Datos de prueba creados correctamente.\n");
    }
    
    private static void mostrarMenu() {
        System.out.println("\n╔════════════════════════════════════════════════════════════╗");
        System.out.println("║                      MENÚ PRINCIPAL                        ║");
        System.out.println("╠════════════════════════════════════════════════════════════╣");
        System.out.println("║  CREAR RESPUESTAS:                                         ║");
        System.out.println("║    1. Crear respuesta NUMÉRICA                             ║");
        System.out.println("║    2. Crear respuesta OPCIÓN ÚNICA                         ║");
        System.out.println("║    3. Crear respuesta OPCIÓN MÚLTIPLE                      ║");
        System.out.println("║    4. Crear respuesta LIBRE (texto)                        ║");
        System.out.println("║                                                            ║");
        System.out.println("║  GESTIÓN:                                                  ║");
        System.out.println("║    5. Modificar respuesta                                  ║");
        System.out.println("║    6. Eliminar respuesta                                   ║");
        System.out.println("║                                                            ║");
        System.out.println("║  CONSULTAS:                                                ║");
        System.out.println("║    7. Consultar respuesta por ID                           ║");
        System.out.println("║    8. Listar TODAS las respuestas                          ║");
        System.out.println("║    9. Listar respuestas de un USUARIO                      ║");
        System.out.println("║   10. Listar respuestas de una PREGUNTA                    ║");
        System.out.println("║                                                            ║");
        System.out.println("║  AYUDA:                                                    ║");
        System.out.println("║   11. Mostrar datos de prueba disponibles                  ║");
        System.out.println("║                                                            ║");
        System.out.println("║    0. Salir                                                ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.print("Seleccione una opción: ");
    }
    
    private static void crearRespuestaNumerica() {
        System.out.println("\n═══ CREAR RESPUESTA NUMÉRICA ═══");
        
        mostrarUsuariosDisponibles();
        System.out.print("Usuario (nombre): ");
        String usuario = scanner.nextLine().trim();
        
        PreguntaNumerica pregunta = (PreguntaNumerica) ctrlPregunta.getPregunta(idPreguntaNumerica);
        System.out.println("\nPregunta: " + pregunta.getEnunciado());
        System.out.println("Rango válido: [" + pregunta.getRangoInf() + ", " + pregunta.getRangoSup() + "]");
        System.out.print("Respuesta (número): ");
        double valor = Double.parseDouble(scanner.nextLine().trim());
        
        int idRespuesta = ctrlRespuesta.darRespuesta(
            usuario, 
            idPreguntaNumerica, 
            valor,
            ctrlUsuario,
            ctrlPregunta
        );
        
        System.out.println("\n✅ Respuesta numérica creada con ID: " + idRespuesta);
        System.out.println("   Usuario: " + usuario);
        System.out.println("   Valor: " + valor);
    }
    
    private static void crearRespuestaOpcionUnica() {
        System.out.println("\n═══ CREAR RESPUESTA OPCIÓN ÚNICA ═══");
        
        mostrarUsuariosDisponibles();
        System.out.print("Usuario (nombre): ");
        String usuario = scanner.nextLine().trim();
        
        PreguntaOpcionUnica pregunta = (PreguntaOpcionUnica) ctrlPregunta.getPregunta(idPreguntaOpcionUnica);
        System.out.println("\nPregunta: " + pregunta.getEnunciado());
        System.out.println("Opciones disponibles:");
        for (int i = 0; i < pregunta.getOpciones().size(); i++) {
            System.out.println("  " + i + ". " + pregunta.getOpciones().get(i));
        }
        System.out.print("Seleccione opción (índice): ");
        int indice = Integer.parseInt(scanner.nextLine().trim());
        
        int idRespuesta = ctrlRespuesta.darRespuesta(
            usuario, 
            idPreguntaOpcionUnica, 
            indice,
            ctrlUsuario,
            ctrlPregunta
        );
        
        System.out.println("\n✅ Respuesta opción única creada con ID: " + idRespuesta);
        System.out.println("   Usuario: " + usuario);
        System.out.println("   Opción seleccionada: " + indice + " (" + pregunta.getOpciones().get(indice) + ")");
    }
    
    private static void crearRespuestaOpcionMultiple() {
        System.out.println("\n═══ CREAR RESPUESTA OPCIÓN MÚLTIPLE ═══");
        
        mostrarUsuariosDisponibles();
        System.out.print("Usuario (nombre): ");
        String usuario = scanner.nextLine().trim();
        
        PreguntaOpcionMultiple pregunta = (PreguntaOpcionMultiple) ctrlPregunta.getPregunta(idPreguntaOpcionMultiple);
        System.out.println("\nPregunta: " + pregunta.getEnunciado());
        System.out.println("Opciones disponibles:");
        for (int i = 0; i < pregunta.getOpciones().size(); i++) {
            System.out.println("  " + i + ". " + pregunta.getOpciones().get(i));
        }
        System.out.println("Máximo de opciones: " + pregunta.getNumMaxOpc());
        System.out.print("Seleccione opciones (separadas por comas, ej: 0,2,4): ");
        String input = scanner.nextLine().trim();
        
        String[] partes = input.split(",");
        int[] indices = new int[partes.length];
        for (int i = 0; i < partes.length; i++) {
            indices[i] = Integer.parseInt(partes[i].trim());
        }
        
        int idRespuesta = ctrlRespuesta.darRespuesta(
            usuario, 
            idPreguntaOpcionMultiple, 
            indices,
            ctrlUsuario,
            ctrlPregunta
        );
        
        System.out.println("\n✅ Respuesta opción múltiple creada con ID: " + idRespuesta);
        System.out.println("   Usuario: " + usuario);
        System.out.print("   Opciones seleccionadas: ");
        for (int idx : indices) {
            System.out.print(pregunta.getOpciones().get(idx) + " ");
        }
        System.out.println();
    }
    
    private static void crearRespuestaLibre() {
        System.out.println("\n═══ CREAR RESPUESTA LIBRE ═══");
        
        mostrarUsuariosDisponibles();
        System.out.print("Usuario (nombre): ");
        String usuario = scanner.nextLine().trim();
        
        Pregunta pregunta = ctrlPregunta.getPregunta(idPreguntaLibre);
        System.out.println("\nPregunta: " + pregunta.getEnunciado());
        System.out.print("Respuesta (texto libre): ");
        String texto = scanner.nextLine();
        
        int idRespuesta = ctrlRespuesta.darRespuesta(
            usuario, 
            idPreguntaLibre, 
            texto,
            ctrlUsuario,
            ctrlPregunta
        );
        
        System.out.println("\n✅ Respuesta libre creada con ID: " + idRespuesta);
        System.out.println("   Usuario: " + usuario);
        System.out.println("   Texto: \"" + texto + "\"");
    }
    
    private static void modificarRespuesta() {
        System.out.println("\n═══ MODIFICAR RESPUESTA ═══");
        
        listarTodasRespuestas();
        
        System.out.print("\nID de la respuesta a modificar: ");
        int idRespuesta = Integer.parseInt(scanner.nextLine().trim());
        
        Respuesta r = ctrlRespuesta.getRespuesta(idRespuesta);
        System.out.println("\nRespuesta actual:");
        mostrarRespuestaDetallada(r);
        
        if (r instanceof RespuestaNumerica) {
            System.out.print("\nNuevo valor numérico: ");
            double nuevoValor = Double.parseDouble(scanner.nextLine().trim());
            ctrlRespuesta.modificarRespuesta(idRespuesta, nuevoValor);
            System.out.println("✅ Respuesta numérica modificada a: " + nuevoValor);
            
        } else if (r instanceof RespuestaOpcionUnica) {
            PreguntaOpcionUnica pregunta = (PreguntaOpcionUnica) ctrlPregunta.getPregunta(r.getIdPregunta());
            System.out.println("\nOpciones disponibles:");
            for (int i = 0; i < pregunta.getOpciones().size(); i++) {
                System.out.println("  " + i + ". " + pregunta.getOpciones().get(i));
            }
            System.out.print("Nuevo índice: ");
            int nuevoIndice = Integer.parseInt(scanner.nextLine().trim());
            ctrlRespuesta.modificarRespuesta(idRespuesta, nuevoIndice);
            System.out.println("✅ Respuesta opción única modificada a: " + pregunta.getOpciones().get(nuevoIndice));
            
        } else if (r instanceof RespuestaOpcionMultiple) {
            PreguntaOpcionMultiple pregunta = (PreguntaOpcionMultiple) ctrlPregunta.getPregunta(r.getIdPregunta());
            System.out.println("\nOpciones disponibles:");
            for (int i = 0; i < pregunta.getOpciones().size(); i++) {
                System.out.println("  " + i + ". " + pregunta.getOpciones().get(i));
            }
            System.out.print("Nuevos índices (separados por comas): ");
            String input = scanner.nextLine().trim();
            String[] partes = input.split(",");
            int[] nuevosIndices = new int[partes.length];
            for (int i = 0; i < partes.length; i++) {
                nuevosIndices[i] = Integer.parseInt(partes[i].trim());
            }
            ctrlRespuesta.modificarRespuesta(idRespuesta, nuevosIndices);
            System.out.println("✅ Respuesta opción múltiple modificada.");
            
        } else if (r instanceof RespuestaLibre) {
            System.out.print("\nNuevo texto: ");
            String nuevoTexto = scanner.nextLine();
            ctrlRespuesta.modificarRespuesta(idRespuesta, nuevoTexto);
            System.out.println("✅ Respuesta libre modificada.");
        }
    }
    
    private static void eliminarRespuesta() {
        System.out.println("\n═══ ELIMINAR RESPUESTA ═══");
        
        listarTodasRespuestas();
        
        System.out.print("\nID de la respuesta a eliminar: ");
        int idRespuesta = Integer.parseInt(scanner.nextLine().trim());
        
        Respuesta r = ctrlRespuesta.getRespuesta(idRespuesta);
        String usuario = r.getNomUsuario();
        int idPregunta = r.getIdPregunta();
        
        System.out.println("\n¿Está seguro de eliminar esta respuesta?");
        mostrarRespuestaDetallada(r);
        System.out.print("Confirmar (s/n): ");
        String confirmacion = scanner.nextLine().trim().toLowerCase();
        
        if (confirmacion.equals("s") || confirmacion.equals("si")) {
            ctrlRespuesta.eliminarRespuesta(idRespuesta, ctrlUsuario, ctrlPregunta);
            System.out.println("\n✅ Respuesta eliminada correctamente.");
            System.out.println("   - Eliminada del usuario: " + usuario);
            System.out.println("   - Eliminada de la pregunta ID: " + idPregunta);
        } else {
            System.out.println("❌ Eliminación cancelada.");
        }
    }
    
    private static void consultarRespuesta() {
        System.out.println("\n═══ CONSULTAR RESPUESTA ═══");
        
        System.out.print("ID de la respuesta: ");
        int idRespuesta = Integer.parseInt(scanner.nextLine().trim());
        
        Respuesta r = ctrlRespuesta.getRespuesta(idRespuesta);
        mostrarRespuestaDetallada(r);
    }
    
    private static void listarTodasRespuestas() {
        System.out.println("\n═══ TODAS LAS RESPUESTAS ═══");
        
        TreeMap<Integer, Respuesta> todas = ctrlRespuesta.getAllRespuestas();
        
        if (todas.isEmpty()) {
            System.out.println("No hay respuestas en el sistema.");
            return;
        }
        
        System.out.println("Total de respuestas: " + todas.size());
        System.out.println("─────────────────────────────────────────────────────────────");
        
        for (Respuesta r : todas.values()) {
            mostrarRespuestaResumida(r);
        }
    }
    
    private static void listarRespuestasUsuario() {
        System.out.println("\n═══ RESPUESTAS POR USUARIO ═══");
        
        mostrarUsuariosDisponibles();
        System.out.print("Usuario: ");
        String nomUsuario = scanner.nextLine().trim();
        
        Usuario u = ctrlUsuario.getUsuario(nomUsuario);
        List<Integer> idsRespuestas = new ArrayList<>(u.getIdRespuestas());
        
        if (idsRespuestas.isEmpty()) {
            System.out.println("\nEl usuario '" + nomUsuario + "' no tiene respuestas.");
            return;
        }
        
        System.out.println("\nRespuestas del usuario '" + nomUsuario + "': " + idsRespuestas.size());
        System.out.println("─────────────────────────────────────────────────────────────");
        
        for (int idResp : idsRespuestas) {
            Respuesta r = ctrlRespuesta.getRespuesta(idResp);
            mostrarRespuestaResumida(r);
        }
    }
    
    private static void listarRespuestasPregunta() {
        System.out.println("\n═══ RESPUESTAS POR PREGUNTA ═══");
        
        System.out.println("Preguntas disponibles:");
        System.out.println("  " + idPreguntaNumerica + ". ¿Cuántas horas a la semana haces deporte?");
        System.out.println("  " + idPreguntaOpcionUnica + ". ¿Cuál es tu nivel de actividad física?");
        System.out.println("  " + idPreguntaOpcionMultiple + ". ¿Qué deportes practicas?");
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
        System.out.println("─────────────────────────────────────────────────────────────");
        
        for (int idResp : idsRespuestas) {
            Respuesta r = ctrlRespuesta.getRespuesta(idResp);
            mostrarRespuestaResumida(r);
        }
    }
    
    private static void mostrarDatosPrueba() {
        System.out.println("\n═══ DATOS DE PRUEBA DISPONIBLES ═══");
        
        System.out.println("\n📋 USUARIOS:");
        System.out.println("  - alice");
        System.out.println("  - bob");
        System.out.println("  - charlie");
        
        System.out.println("\n📝 ENCUESTA:");
        System.out.println("  ID: " + idEncuesta1);
        System.out.println("  Título: Encuesta Hábitos Deportivos");
        
        System.out.println("\n❓ PREGUNTAS:");
        System.out.println("  [" + idPreguntaNumerica + "] NUMÉRICA:");
        System.out.println("      ¿Cuántas horas a la semana haces deporte? [0.0 - 20.0]");
        
        System.out.println("\n  [" + idPreguntaOpcionUnica + "] OPCIÓN ÚNICA:");
        System.out.println("      ¿Cuál es tu nivel de actividad física?");
        System.out.println("      Opciones: 0=Bajo, 1=Medio, 2=Alto");
        
        System.out.println("\n  [" + idPreguntaOpcionMultiple + "] OPCIÓN MÚLTIPLE:");
        System.out.println("      ¿Qué deportes practicas?");
        System.out.println("      Opciones: 0=Fútbol, 1=Baloncesto, 2=Natación, 3=Running, 4=Ciclismo");
        
        System.out.println("\n  [" + idPreguntaLibre + "] TEXTO LIBRE:");
        System.out.println("      Describe tu rutina de ejercicio favorita");
    }
    
    private static void mostrarRespuestaDetallada(Respuesta r) {
        System.out.println("\n┌────────────────────────────────────────────────────────┐");
        System.out.println("│ ID: " + r.getId());
        System.out.println("│ Usuario: " + r.getNomUsuario());
        System.out.println("│ Pregunta ID: " + r.getIdPregunta());
        
        Pregunta p = ctrlPregunta.getPregunta(r.getIdPregunta());
        System.out.println("│ Enunciado: " + p.getEnunciado());
        
        if (r instanceof RespuestaNumerica) {
            RespuestaNumerica rn = (RespuestaNumerica) r;
            System.out.println("│ Tipo: NUMÉRICA");
            System.out.println("│ Valor: " + rn.getRespuesta());
            
        } else if (r instanceof RespuestaOpcionUnica) {
            RespuestaOpcionUnica ro = (RespuestaOpcionUnica) r;
            PreguntaOpcionUnica po = (PreguntaOpcionUnica) p;
            System.out.println("│ Tipo: OPCIÓN ÚNICA");
            System.out.println("│ Índice: " + ro.getRespuesta());
            System.out.println("│ Opción: " + po.getOpciones().get(ro.getRespuesta()));
            
        } else if (r instanceof RespuestaOpcionMultiple) {
            RespuestaOpcionMultiple rm = (RespuestaOpcionMultiple) r;
            PreguntaOpcionMultiple pm = (PreguntaOpcionMultiple) p;
            System.out.println("│ Tipo: OPCIÓN MÚLTIPLE");
            System.out.print("│ Opciones seleccionadas: ");
            int[] indices = rm.getRespuesta();
            for (int i = 0; i < indices.length; i++) {
                System.out.print(pm.getOpciones().get(indices[i]));
                if (i < indices.length - 1) System.out.print(", ");
            }
            System.out.println();
            
        } else if (r instanceof RespuestaLibre) {
            RespuestaLibre rl = (RespuestaLibre) r;
            System.out.println("│ Tipo: TEXTO LIBRE");
            System.out.println("│ Texto: \"" + rl.getRespuesta() + "\"");
        }
        
        System.out.println("└────────────────────────────────────────────────────────┘");
    }
    
    private static void mostrarRespuestaResumida(Respuesta r) {
        String tipo = "";
        String valor = "";
        
        if (r instanceof RespuestaNumerica) {
            tipo = "NUM";
            valor = String.valueOf(((RespuestaNumerica) r).getRespuesta());
        } else if (r instanceof RespuestaOpcionUnica) {
            tipo = "OPU";
            valor = "opción " + ((RespuestaOpcionUnica) r).getRespuesta();
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
        System.out.println("Usuarios disponibles: alice, bob, charlie");
    }
}
