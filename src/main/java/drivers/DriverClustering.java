package drivers;

import java.io.File;
import java.util.List;
import java.util.Scanner;

import domain.*;
import domain.algorithm.estructuras.Perfil;
import domain.algorithm.funciones.ClusterEvaluator;
import domaincontrollers.*;
import persistence.GestorPersistencia;

// pnd de revisar

/**
 * Driver interactivo para probar funcionalidad de Clustering.
 * 
 * Permite:
 * - Importar encuestas y respuestas
 * - Ejecutar clustering (K-Means y K-Means++)
 * - Obtener perfiles de comportamiento
 * - Obtener representantes de grupos
 * - Listar individuos de cada perfil
 * - Evaluar calidad del clustering (coeficiente de silueta)
 * 
 * @author Denis Roca
 */
public class DriverClustering {

    private static CtrlDominio ctrlDominio;
    private static CtrlDominioEncuesta ctrlEncuesta;
    private static CtrlDominioUsuario ctrlUsuario;
    private static CtrlDominioRespuesta ctrlRespuesta;
    private static CtrlDominioPregunta ctrlPregunta;
    private static Scanner scanner;

    // Variables para mantener estado del último clustering ejecutado
    private static int ultimoIdEncuesta = -1;
    private static List<Perfil> ultimosPerfiles = null;

    public static void main(String[] args) {
        inicializarControladores();
        scanner = new Scanner(System.in);
        boolean salir = false;

        System.out.println("╔════════════════════════════════════════════════════════════╗");
        System.out.println("║  DRIVER CLUSTERING - ANÁLISIS DE PERFILES DE USUARIO      ║");
        System.out.println("╚════════════════════════════════════════════════════════════╝");
        System.out.println();

        while (!salir) {
            mostrarMenu();
            String opcion = scanner.nextLine().trim();
            
            try {
                switch (opcion) {
                    case "1":
                        importarEncuesta();
                        break;
                    case "2":
                        importarRespuestas();
                        break;
                    case "3":
                        ejecutarClustering();
                        break;
                    case "4":
                        obtenerPerfiles();
                        break;
                    case "5":
                        obtenerRepresentantes();
                        break;
                    case "6":
                        listarIndividuosPerfil();
                        break;
                    case "7":
                        evaluarCalidad();
                        break;
                    case "8":
                        compararAlgoritmos();
                        break;
                    case "9":
                        mostrarEstadoSistema();
                        break;
                    case "0":
                        salir = true;
                        break;
                    default:
                        System.out.println("❌ Opción no válida.");
                }
            } catch (Exception e) {
                System.out.println("❌ ERROR: " + e.getMessage());
                System.out.println("   Tipo: " + e.getClass().getSimpleName());
                e.printStackTrace();
            }

            if (!salir) {
                System.out.println("\n⏎ Pulsa ENTER para continuar...");
                scanner.nextLine();
            }
        }
        
        scanner.close();
        System.out.println("\n✓ Saliendo del driver. ¡Hasta pronto!");
    }

    private static void inicializarControladores() {
        GestorPersistencia gestor = GestorPersistencia.getInstance();
        ctrlDominio = CtrlDominio.getInstance();
        
        // Obtener referencias individuales para operaciones específicas
        ctrlRespuesta = CtrlDominioRespuesta.getInstance(gestor, null, null, null);
        ctrlPregunta = CtrlDominioPregunta.getInstance(gestor, ctrlRespuesta, null);
        ctrlUsuario = CtrlDominioUsuario.getInstance(gestor, ctrlRespuesta, ctrlPregunta);
        ctrlEncuesta = CtrlDominioEncuesta.getInstance(gestor, ctrlPregunta, ctrlRespuesta, ctrlUsuario);
        
        System.out.println("✓ Controladores inicializados correctamente.");
    }

    private static void mostrarMenu() {
        System.out.println("\n┌─────────────────────────────────────────────────────────┐");
        System.out.println("│                    MENÚ PRINCIPAL                       │");
        System.out.println("├─────────────────────────────────────────────────────────┤");
        System.out.println("│  1. Importar encuesta desde JSON                       │");
        System.out.println("│  2. Importar respuestas desde JSON                     │");
        System.out.println("│  3. Ejecutar clustering (K-Means / K-Means++)          │");
        System.out.println("│  4. Obtener diferentes perfiles/comportamientos        │");
        System.out.println("│  5. Obtener representantes de grupos                   │");
        System.out.println("│  6. Listar individuos de cada perfil                   │");
        System.out.println("│  7. Evaluar calidad del clustering (silueta)           │");
        System.out.println("│  8. Comparar algoritmos (K-Means vs K-Means++)         │");
        System.out.println("│  9. Mostrar estado del sistema                         │");
        System.out.println("│  0. Salir                                               │");
        System.out.println("└─────────────────────────────────────────────────────────┘");
        System.out.print("Selecciona opción: ");
    }

    private static void importarEncuesta() {
        System.out.println("\n═══ IMPORTAR ENCUESTA ═══");
        
        // Mostrar usuarios disponibles
        System.out.println("\n📋 Usuarios disponibles:");
        var usuarios = ctrlUsuario.getAllUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("   ⚠ No hay usuarios. Creando usuario de prueba...");
            ctrlDominio.registrarUsuario("ana", "ana123", "ana123");
            System.out.println("   ✓ Usuario 'ana' creado.");
        } else {
            for (String nombre : usuarios.keySet()) {
                System.out.println("   • " + nombre);
            }
        }

        System.out.print("\n👤 Nombre usuario (para asignar como creador): ");
        String usuario = scanner.nextLine().trim();

        if (!ctrlUsuario.existeUsuario(usuario)) {
            System.out.println("❌ El usuario no existe. Usa uno de los listados.");
            return;
        }

        System.out.print("📁 Ruta del archivo JSON de la encuesta: ");
        String ruta = scanner.nextLine().trim();
        File fichero = new File(ruta);

        if (!fichero.exists()) {
            System.out.println("❌ El archivo no existe: " + ruta);
            return;
        }

        try {
            // Iniciar sesión para establecer usuario activo
            ctrlUsuario.iniciarSesion(usuario, obtenerPasswordUsuario(usuario));
            
            int idEncuesta = ctrlDominio.importarEncuesta(fichero);
            System.out.println("✓ Encuesta importada exitosamente!");
            System.out.println("   ID asignado: " + idEncuesta);
            
            // Mostrar información
            Encuesta e = ctrlEncuesta.getEncuesta(idEncuesta);
            System.out.println("   Título: " + e.getTitulo());
            System.out.println("   Preguntas: " + e.getIdPreguntas().size());
            System.out.println("   Creador: " + e.getIdCreador());
            
        } catch (Exception e) {
            System.out.println("❌ Error al importar: " + e.getMessage());
        } finally {
            ctrlUsuario.cerrarSesionActual();
        }
    }

    private static void importarRespuestas() {
        System.out.println("\n═══ IMPORTAR RESPUESTAS ═══");
        
        System.out.print("📁 Ruta del archivo JSON de respuestas: ");
        String ruta = scanner.nextLine().trim();
        File fichero = new File(ruta);

        if (!fichero.exists()) {
            System.out.println("❌ El archivo no existe: " + ruta);
            return;
        }

        try {
            ctrlDominio.importarRespuestas(fichero);
            System.out.println("✓ Respuestas importadas exitosamente!");
        } catch (Exception e) {
            System.out.println("❌ Error al importar: " + e.getMessage());
        }
    }

    private static void ejecutarClustering() {
        System.out.println("\n═══ EJECUTAR CLUSTERING ═══");
        
        // Mostrar encuestas disponibles
        var encuestas = ctrlEncuesta.getAllEncuestas();
        if (encuestas.isEmpty()) {
            System.out.println("❌ No hay encuestas. Importa una encuesta primero (opción 1).");
            return;
        }

        System.out.println("\n📋 Encuestas disponibles:");
        for (Encuesta e : encuestas.values()) {
            System.out.println("   ID " + e.getIdEncuesta() + ": " + e.getTitulo() + 
                             " (" + e.getIdPreguntas().size() + " preguntas)");
        }

        System.out.print("\n🔢 ID de la encuesta a analizar: ");
        int idEncuesta = Integer.parseInt(scanner.nextLine().trim());

        if (!ctrlEncuesta.existeEncuesta(idEncuesta)) {
            System.out.println("❌ La encuesta no existe.");
            return;
        }

        // Verificar que hay respuestas
        var respuestas = ctrlEncuesta.getRespuestasParaClustering(idEncuesta);
        if (respuestas.isEmpty()) {
            System.out.println("❌ La encuesta no tiene respuestas. Importa respuestas primero (opción 2).");
            return;
        }

        System.out.println("   ✓ Respuestas disponibles: " + respuestas.size() + " usuarios");

        System.out.print("\n🔢 Número de clusters (k): ");
        int k = Integer.parseInt(scanner.nextLine().trim());

        if (k < 2 || k > respuestas.size()) {
            System.out.println("❌ k debe estar entre 2 y " + respuestas.size());
            return;
        }

        System.out.println("\n🤖 Algoritmos disponibles:");
        System.out.println("   1. K-Means (estándar)");
        System.out.println("   2. K-Means++ (mejor inicialización)");
        System.out.print("Selecciona algoritmo (1/2): ");
        String opcionAlg = scanner.nextLine().trim();

        String algoritmo = opcionAlg.equals("2") ? "kmeans++" : "kmeans";
        String nombreAlg = algoritmo.equals("kmeans++") ? "K-Means++" : "K-Means";

        System.out.println("\n⏳ Ejecutando " + nombreAlg + " con k=" + k + "...");
        long inicio = System.currentTimeMillis();

        List<Perfil> perfiles = ctrlDominio.ejecutarClustering(idEncuesta, k, algoritmo);
        
        long fin = System.currentTimeMillis();
        System.out.println("✓ Clustering completado en " + (fin - inicio) + " ms");

        // Guardar para futuras operaciones
        ultimoIdEncuesta = idEncuesta;
        ultimosPerfiles = perfiles;

        // Mostrar resumen
        System.out.println("\n📊 RESUMEN DE PERFILES:");
        for (int i = 0; i < perfiles.size(); i++) {
            Perfil p = perfiles.get(i);
            System.out.println("   Perfil " + (i + 1) + ": " + p.miembros().size() + " miembros");
            System.out.println("      Representante: " + p.getRepresentante());
        }

        // Evaluar calidad automáticamente
        double silueta = ctrlDominio.evaluarCalidadClustering(perfiles, idEncuesta);
        System.out.println("\n📈 Calidad del clustering:");
        System.out.println("   Coeficiente de silueta: " + String.format("%.4f", silueta));
        System.out.println("   Interpretación: " + ClusterEvaluator.interpretarSilueta(silueta));
    }

    private static void obtenerPerfiles() {
        System.out.println("\n═══ OBTENER PERFILES ═══");
        
        if (ultimosPerfiles == null) {
            System.out.println("❌ No hay clustering ejecutado. Ejecuta primero (opción 3).");
            return;
        }

        System.out.println("\n📊 DIFERENTES COMPORTAMIENTOS/PERFILES DETECTADOS:");
        System.out.println("   Total de perfiles: " + ultimosPerfiles.size());
        System.out.println();

        for (int i = 0; i < ultimosPerfiles.size(); i++) {
            Perfil p = ultimosPerfiles.get(i);
            System.out.println("┌─ PERFIL " + (i + 1) + " ─────────────────────────────");
            System.out.println("│ Tamaño: " + p.miembros().size() + " usuarios");
            System.out.println("│ Representante: " + p.getRepresentante());
            System.out.println("│ Miembros: " + String.join(", ", p.miembros()));
            System.out.println("└────────────────────────────────────────────────");
        }
    }

    private static void obtenerRepresentantes() {
        System.out.println("\n═══ REPRESENTANTES DE GRUPOS ═══");
        
        if (ultimosPerfiles == null) {
            System.out.println("❌ No hay clustering ejecutado. Ejecuta primero (opción 3).");
            return;
        }

        System.out.println("\n👥 REPRESENTANTES DE CADA PERFIL:");
        for (int i = 0; i < ultimosPerfiles.size(); i++) {
            Perfil p = ultimosPerfiles.get(i);
            String representante = ctrlDominio.obtenerRepresentantePerfil(p);
            System.out.println("   Perfil " + (i + 1) + ": " + representante + 
                             " (representa a " + p.miembros().size() + " usuarios)");
        }
    }

    private static void listarIndividuosPerfil() {
        System.out.println("\n═══ LISTAR INDIVIDUOS POR PERFIL ═══");
        
        if (ultimosPerfiles == null) {
            System.out.println("❌ No hay clustering ejecutado. Ejecuta primero (opción 3).");
            return;
        }

        System.out.print("\n🔢 Número de perfil (1-" + ultimosPerfiles.size() + "): ");
        int numPerfil = Integer.parseInt(scanner.nextLine().trim());

        if (numPerfil < 1 || numPerfil > ultimosPerfiles.size()) {
            System.out.println("❌ Perfil no válido.");
            return;
        }

        Perfil perfil = ultimosPerfiles.get(numPerfil - 1);
        List<String> miembros = ctrlDominio.obtenerMiembrosPerfil(perfil);

        System.out.println("\n👥 MIEMBROS DEL PERFIL " + numPerfil + ":");
        System.out.println("   Representante: ★ " + perfil.getRepresentante());
        System.out.println("   Total: " + miembros.size() + " individuos");
        System.out.println();
        for (int i = 0; i < miembros.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + miembros.get(i));
        }
    }

    private static void evaluarCalidad() {
        System.out.println("\n═══ EVALUAR CALIDAD DEL CLUSTERING ═══");
        
        if (ultimosPerfiles == null || ultimoIdEncuesta == -1) {
            System.out.println("❌ No hay clustering ejecutado. Ejecuta primero (opción 3).");
            return;
        }

        System.out.println("\n⏳ Calculando coeficiente de silueta...");
        double silueta = ctrlDominio.evaluarCalidadClustering(ultimosPerfiles, ultimoIdEncuesta);

        System.out.println("\n📈 EVALUACIÓN DE CALIDAD:");
        System.out.println("   ┌─────────────────────────────────────────┐");
        System.out.println("   │ Coeficiente de silueta: " + String.format("%7.4f", silueta) + "       │");
        System.out.println("   └─────────────────────────────────────────┘");
        System.out.println("\n   Interpretación: " + ClusterEvaluator.interpretarSilueta(silueta));
        System.out.println("\n   Escala de valores:");
        System.out.println("   • 0.7 - 1.0: Excelente clustering");
        System.out.println("   • 0.5 - 0.7: Buen clustering");
        System.out.println("   • 0.25 - 0.5: Clustering aceptable");
        System.out.println("   • 0.0 - 0.25: Clustering pobre");
        System.out.println("   • < 0.0: Clustering malo (objetos mal asignados)");
    }

    private static void compararAlgoritmos() {
        System.out.println("\n═══ COMPARAR ALGORITMOS ═══");
        
        // Mostrar encuestas disponibles
        var encuestas = ctrlEncuesta.getAllEncuestas();
        if (encuestas.isEmpty()) {
            System.out.println("❌ No hay encuestas. Importa una encuesta primero (opción 1).");
            return;
        }

        System.out.println("\n📋 Encuestas disponibles:");
        for (Encuesta e : encuestas.values()) {
            System.out.println("   ID " + e.getIdEncuesta() + ": " + e.getTitulo());
        }

        System.out.print("\n🔢 ID de la encuesta: ");
        int idEncuesta = Integer.parseInt(scanner.nextLine().trim());

        if (!ctrlEncuesta.existeEncuesta(idEncuesta)) {
            System.out.println("❌ La encuesta no existe.");
            return;
        }

        var respuestas = ctrlEncuesta.getRespuestasParaClustering(idEncuesta);
        if (respuestas.isEmpty()) {
            System.out.println("❌ La encuesta no tiene respuestas.");
            return;
        }

        System.out.print("\n🔢 Número de clusters (k): ");
        int k = Integer.parseInt(scanner.nextLine().trim());

        if (k < 2 || k > respuestas.size()) {
            System.out.println("❌ k debe estar entre 2 y " + respuestas.size());
            return;
        }

        System.out.println("\n⏳ Ejecutando comparación...\n");

        // Ejecutar K-Means
        System.out.println("🤖 K-Means estándar:");
        long inicio1 = System.currentTimeMillis();
        List<Perfil> perfilesKmeans = ctrlDominio.ejecutarClustering(idEncuesta, k, "kmeans");
        long fin1 = System.currentTimeMillis();
        double siluetaKmeans = ctrlDominio.evaluarCalidadClustering(perfilesKmeans, idEncuesta);
        System.out.println("   Tiempo: " + (fin1 - inicio1) + " ms");
        System.out.println("   Silueta: " + String.format("%.4f", siluetaKmeans));

        // Ejecutar K-Means++
        System.out.println("\n🤖 K-Means++:");
        long inicio2 = System.currentTimeMillis();
        List<Perfil> perfilesKmeansPP = ctrlDominio.ejecutarClustering(idEncuesta, k, "kmeans++");
        long fin2 = System.currentTimeMillis();
        double siluetaKmeansPP = ctrlDominio.evaluarCalidadClustering(perfilesKmeansPP, idEncuesta);
        System.out.println("   Tiempo: " + (fin2 - inicio2) + " ms");
        System.out.println("   Silueta: " + String.format("%.4f", siluetaKmeansPP));

        // Comparación
        System.out.println("\n📊 COMPARACIÓN:");
        System.out.println("   ┌──────────────┬──────────┬──────────┐");
        System.out.println("   │ Algoritmo    │  Tiempo  │ Silueta  │");
        System.out.println("   ├──────────────┼──────────┼──────────┤");
        System.out.printf("   │ K-Means      │ %6d ms│  %.4f  │\n", (fin1 - inicio1), siluetaKmeans);
        System.out.printf("   │ K-Means++    │ %6d ms│  %.4f  │\n", (fin2 - inicio2), siluetaKmeansPP);
        System.out.println("   └──────────────┴──────────┴──────────┘");

        if (siluetaKmeansPP > siluetaKmeans) {
            System.out.println("\n   ✓ K-Means++ produce mejor clustering");
        } else if (siluetaKmeans > siluetaKmeansPP) {
            System.out.println("\n   ✓ K-Means produce mejor clustering");
        } else {
            System.out.println("\n   ⚖ Ambos algoritmos producen clustering similar");
        }

        // Guardar el mejor resultado
        if (siluetaKmeansPP >= siluetaKmeans) {
            ultimosPerfiles = perfilesKmeansPP;
        } else {
            ultimosPerfiles = perfilesKmeans;
        }
        ultimoIdEncuesta = idEncuesta;
    }

    private static void mostrarEstadoSistema() {
        System.out.println("\n═══ ESTADO DEL SISTEMA ═══");
        
        var usuarios = ctrlUsuario.getAllUsuarios();
        var encuestas = ctrlEncuesta.getAllEncuestas();
        
        System.out.println("\n📊 Estadísticas:");
        System.out.println("   • Usuarios registrados: " + usuarios.size());
        System.out.println("   • Encuestas importadas: " + encuestas.size());
        
        if (!encuestas.isEmpty()) {
            System.out.println("\n📋 Detalle de encuestas:");
            for (Encuesta e : encuestas.values()) {
                var respuestas = ctrlEncuesta.getRespuestasParaClustering(e.getIdEncuesta());
                System.out.println("   • ID " + e.getIdEncuesta() + ": " + e.getTitulo());
                System.out.println("     - Preguntas: " + e.getIdPreguntas().size());
                System.out.println("     - Respuestas: " + respuestas.size() + " usuarios");
            }
        }
        
        if (ultimosPerfiles != null) {
            System.out.println("\n🎯 Último clustering ejecutado:");
            System.out.println("   • Encuesta ID: " + ultimoIdEncuesta);
            System.out.println("   • Perfiles generados: " + ultimosPerfiles.size());
        } else {
            System.out.println("\n⚠ No se ha ejecutado ningún clustering todavía.");
        }
    }

    /**
     * Obtiene la contraseña de un usuario (simplificado para el driver).
     * En un sistema real, esto vendría de la base de datos.
     */
    private static String obtenerPasswordUsuario(String usuario) {
        // Para simplificar, usamos contraseñas por defecto
        if (usuario.equals("ana")) return "ana123";
        return "pass123"; // Contraseña por defecto
    }
}
