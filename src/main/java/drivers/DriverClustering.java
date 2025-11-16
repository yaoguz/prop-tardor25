package drivers;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;

import domain.*;
import domain.algorithm.estructuras.Perfil;
import domain.algorithm.funciones.ClusterEvaluator;
import domaincontrollers.*;
import persistence.GestorPersistencia;

// pnd de revisar

/**
 * Driver interactivo para probar la funcionalidad de Clustering.
 * 
 * @author Denis Roca
 */
public class DriverClustering {

    private static CtrlDominioEncuesta ctrlEncuesta;
    private static CtrlDominioUsuario ctrlUsuario;
    private static CtrlDominioRespuesta ctrlRespuesta;
    private static CtrlDominioPregunta ctrlPregunta;
    private static CtrlDominioPerfil ctrlPerfil;
    private static Scanner scanner;

    // Variables para mantener estado del último clustering ejecutado
    private static int ultimoIdEncuesta = -1;
    private static List<Perfil> ultimosPerfiles = null;
    private static String nomU;

    public static void main(String[] args) {
        // Inicializar dependencias (capa real, no mocks)
        GestorPersistencia gestor = GestorPersistencia.getInstance();
        
        ctrlRespuesta = CtrlDominioRespuesta.getInstance(gestor, null, null, null);
        ctrlPregunta = CtrlDominioPregunta.getInstance(gestor, ctrlRespuesta, null);
        ctrlUsuario = CtrlDominioUsuario.getInstance(gestor, ctrlRespuesta, ctrlPregunta);
        ctrlEncuesta = CtrlDominioEncuesta.getInstance(gestor, ctrlPregunta, ctrlRespuesta, ctrlUsuario);
        ctrlPerfil = CtrlDominioPerfil.getInstance(gestor, ctrlUsuario, ctrlRespuesta, ctrlEncuesta);
        
        // Actualizar referencias circulares
        ctrlRespuesta = CtrlDominioRespuesta.getInstance(gestor, ctrlUsuario, ctrlEncuesta, ctrlPregunta);
        ctrlPregunta = CtrlDominioPregunta.getInstance(gestor, ctrlRespuesta, ctrlEncuesta);
        
        scanner = new Scanner(System.in);

        boolean salir = false;

        
        nomU = ctrlUsuario.registrarUsuario("Tester", "123456", "123456");
        ctrlUsuario.iniciarSesion(nomU, "123456");
        File fE = new File("./datos-encuestas.json");
        ultimoIdEncuesta = ctrlEncuesta.importarEncuesta(fE);
        File fR = new File("./loan-test.csv");
        ctrlRespuesta.importarRespuestas(fR);
        ctrlUsuario.cerrarSesionActual();













        // Información de inicio
        System.out.println("+---------------- DRIVER INTERACTIVO - CONTROLADOR DE: ----------------+");
        System.out.println("|  ####  #      #   #   ####  #####  #####  ####   #####  #   #   #### |");
        System.out.println("| #      #      #   #  #        #    #      #   #    #    ##  #  #     |");
        System.out.println("| #      #      #   #   ###     #    ####   ####     #    # # #  # ### |");
        System.out.println("| #      #      #   #      #    #    #      # #      #    #  ##  #   # |");
        System.out.println("|  ####  #####   ###   ####     #    #####  #  #   #####  #   #   ###  |");
        System.out.println("+----------------------------------------------------------------------+");
        System.out.println("Dispone de un usuario con nombre: " +nomU);
        System.out.println("que tiene una encuesta creada con id: " +ultimoIdEncuesta);
        System.out.println("sacada del archivo: " +fE);
        System.out.println("con las respuestas sacadas del archivo: " +fR);
        System.out.println("(ejecutar acción 9 para comprobarlo, en teoría se puede ejecutar el"); // Quitar estos
        System.out.println("clustering directamente sobre la encuesta " +ultimoIdEncuesta +")");   // comentarios.
        

        while (!salir) {
            mostrarMenu();
            
            int opcion = leerEntero();

            switch (opcion) {
                case 1:
                    importarEncuesta();
                    break;
                case 2:
                    importarRespuestas();
                    break;
                case 3:
                    ejecutarClustering();
                    break;
                case 4:
                    obtenerPerfiles();
                    break;
                case 5:
                    obtenerRepresentantes();
                    break;
                //case 6:
                //    listarIndividuosPerfil();
                //    break;
                case 7:
                    evaluarCalidad();
                    break;
                case 8:
                    compararAlgoritmos();
                    break;
                case 9:
                    mostrarEstadoSistema();
                    break;
                case 10:
                    verRespuestasEncuesta();
                    break;
                case 11:
                    verMatrizRespuestas();
                    break;
                case 0:
                    salir = true;
                    break;
                default:
                    System.out.println("\nOpción no válida. Intenta de nuevo.\n");
            }
        }
        
        scanner.close();
        System.out.println("\nSaliendo del driver...\n");
    }

    private static void mostrarMenu() {
        System.out.println("\nMENÚ DRIVER CLUSTERING");
        System.out.println("1. Importar encuesta desde archivo JSON");
        System.out.println("2. Importar respuestas desde archivo CSV");
        System.out.println("3. Ejecutar clustering");
        System.out.println("4. Obtener perfiles del último clustering");
        System.out.println("5. Obtener representantes de los perfiles del último clustering");
        //System.out.println("6. Listar individuos de cada perfil");
        System.out.println("7. Evaluar calidad del último clustering");
        System.out.println("8. Comparar algoritmos (K-Means vs K-Means++)");
        System.out.println("9. Mostrar estado del sistema");
        System.out.println("10. Ver respuestas de una encuesta");
        System.out.println("11. Ver matriz de respuestas (clustering format)");
        System.out.println("0. Salir");
        System.out.println("\nIntroduzca el número correspondiente a la acción a ejecutar: ");
    }

	/**
	 * Devuelve un entero leído del terminal.
	 * 
     * @throws NumberFormatException si la entrada no era un número.
	 * @return entero leído del terminal.
	 */
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

    private static void importarEncuesta() {
        System.out.println("\n--{ IMPORTAR UNA ENCUESTA DE UN FICHERO }--");
        
        System.out.println("Introduzca el nombre del usuario propietario de la encuesta: ");
        String usr = scanner.nextLine();
        ctrlUsuario.registrarUsuario(usr, "123456", "123456");

        System.out.print("Introduzca la ruta del archivo JSON de la encuesta: ");
        String ruta = scanner.nextLine().trim();
        File fichero = new File(ruta);

        if (!fichero.exists()) {
            System.out.println("El archivo especificado no existe: " + ruta);
            return;
        }

        try {
            // Iniciar sesión para establecer usuario activo
            ctrlUsuario.iniciarSesion(usr, "123456");
            
            int idEncuesta = ctrlEncuesta.importarEncuesta(fichero);
            System.out.println("Se ha importado la encuesta exitosamente. Se le ha asignado el identificador " + idEncuesta);
            
            // Mostrar información
            Encuesta e = ctrlEncuesta.getEncuesta(idEncuesta);
            System.out.println("Su título es: " + e.getTitulo());
            System.out.println("tiene " + e.getIdPreguntas().size() + " preguntas");
            System.out.println("y su creador es: " + e.getIdCreador());
            
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
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
            ctrlRespuesta.importarRespuestas(fichero);
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

		if (respuestas.size() < 2) {
			System.out.println("❌ No se puede ejecutar clustering. Se necesitan al menos 2 usuarios con respuestas.");
			System.out.println("   Actualmente solo hay " + respuestas.size() + " usuario(s).");
			return;
		}

		System.out.print("\n🔢 Número de clusters (k): ");
		int k = Integer.parseInt(scanner.nextLine().trim());

		if (k < 2 || k > respuestas.size()) {
			System.out.println("❌ k debe estar entre 2 y " + respuestas.size());
			return;
		}        System.out.println("\n🤖 Algoritmos disponibles:");
        System.out.println("   1. K-Means (estándar)");
        System.out.println("   2. K-Means++ (mejor inicialización)");
        System.out.print("Selecciona algoritmo (1/2): ");
        String opcionAlg = scanner.nextLine().trim();

        String algoritmo = opcionAlg.equals("2") ? "kmeans++" : "kmeans";
        String nombreAlg = algoritmo.equals("kmeans++") ? "K-Means++" : "K-Means";

        System.out.println("\n⏳ Ejecutando " + nombreAlg + " con k=" + k + "...");
        long inicio = System.currentTimeMillis();

        List<Perfil> perfiles = ctrlPerfil.ejecutarClustering(idEncuesta, k, algoritmo);
        
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

        // // Evaluar calidad automáticamente
        // double silueta = ctrlPerfil.evaluarCalidadClustering(perfiles, idEncuesta);
        // System.out.println("\n📈 Calidad del clustering:");
        // System.out.println("   Coeficiente de silueta: " + String.format("%.4f", silueta));
        // System.out.println("   Interpretación: " + ClusterEvaluator.interpretarSilueta(silueta));
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
            String representante = ctrlPerfil.obtenerRepresentantePerfil(p);
            System.out.println("   Perfil " + (i + 1) + ": " + representante + 
                             " (representa a " + p.miembros().size() + " usuarios)");
        }
    }
/*
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
        List<String> miembros = ctrlPerfil.obtenerMiembrosPerfil(perfil);

        System.out.println("\n👥 MIEMBROS DEL PERFIL " + numPerfil + ":");
        System.out.println("   Representante: ★ " + perfil.getRepresentante());
        System.out.println("   Total: " + miembros.size() + " individuos");
        System.out.println();
        for (int i = 0; i < miembros.size(); i++) {
            System.out.println("   " + (i + 1) + ". " + miembros.get(i));
        }
    }
*/
    private static void evaluarCalidad() {
        System.out.println("\n═══ EVALUAR CALIDAD DEL CLUSTERING ═══");
        
        if (ultimosPerfiles == null || ultimoIdEncuesta == -1) {
            System.out.println("❌ No hay clustering ejecutado. Ejecuta primero (opción 3).");
            return;
        }

        System.out.println("\n⏳ Calculando coeficiente de silueta...");
        double silueta = ctrlPerfil.evaluarCalidadClustering(ultimosPerfiles, ultimoIdEncuesta);

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
        List<Perfil> perfilesKmeans = ctrlPerfil.ejecutarClustering(idEncuesta, k, "kmeans");
        long fin1 = System.currentTimeMillis();
        double siluetaKmeans = ctrlPerfil.evaluarCalidadClustering(perfilesKmeans, idEncuesta);
        System.out.println("   Tiempo: " + (fin1 - inicio1) + " ms");
        System.out.println("   Silueta: " + String.format("%.4f", siluetaKmeans));

        // Ejecutar K-Means++
        System.out.println("\n🤖 K-Means++:");
        long inicio2 = System.currentTimeMillis();
        List<Perfil> perfilesKmeansPP = ctrlPerfil.ejecutarClustering(idEncuesta, k, "kmeans++");
        long fin2 = System.currentTimeMillis();
        double siluetaKmeansPP = ctrlPerfil.evaluarCalidadClustering(perfilesKmeansPP, idEncuesta);
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

    private static void verRespuestasEncuesta() {
        System.out.println("\n═══ VER RESPUESTAS DE ENCUESTA ===");
        
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

        try {
            Map<Pregunta, Set<Respuesta>> mapa = ctrlEncuesta.getRespuestasEncuesta(idEncuesta);
            
            if (mapa.isEmpty()) {
                System.out.println("\n⚠ La encuesta no tiene respuestas todavía.");
                return;
            }
            
            System.out.println("\n📝 RESPUESTAS DE LA ENCUESTA:");
            for (Map.Entry<Pregunta, Set<Respuesta>> entry : mapa.entrySet()) {
                Pregunta p = entry.getKey();
                System.out.println("\n┌─ Pregunta ID " + p.getIdPregunta() + " ───────────────────");
                System.out.println("│ " + p.getEnunciado());
                System.out.println("│ Tipo: " + p.tipoPregunta());
                System.out.println("├─ Respuestas:");
                
                if (entry.getValue().isEmpty()) {
                    System.out.println("│   (sin respuestas)");
                } else {
                    for (Respuesta r : entry.getValue()) {
                        System.out.println("│   • Usuario: " + r.getNomUsuario() + 
                                         " | ID: " + r.getId() + 
                                         " | Tipo: " + r.getTipo());
                    }
                }
                System.out.println("└────────────────────────────────────────");
            }
            
            // Mostrar resumen de usuarios únicos
            Set<String> usuariosUnicos = new HashSet<>();
            for (Set<Respuesta> respuestas : mapa.values()) {
                for (Respuesta r : respuestas) {
                    usuariosUnicos.add(r.getNomUsuario());
                }
            }
            
            System.out.println("\n📊 RESUMEN:");
            System.out.println("   Total de preguntas: " + mapa.size());
            System.out.println("   Usuarios que respondieron: " + usuariosUnicos.size());
            System.out.println("   Usuarios: " + String.join(", ", usuariosUnicos));
            
        } catch (Exception e) {
            System.out.println("❌ Error al obtener respuestas: " + e.getMessage());
        }
    }

    private static void verMatrizRespuestas() {
        System.out.println("\n═══ VER MATRIZ DE RESPUESTAS (FORMATO CLUSTERING) ===");
        
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

        try {
            // Obtener preguntas de la encuesta
            List<Pregunta> preguntas = ctrlEncuesta.getPreguntasEncuesta(idEncuesta);
            System.out.println("\n📋 PREGUNTAS DE LA ENCUESTA:");
            for (int i = 0; i < preguntas.size(); i++) {
                Pregunta p = preguntas.get(i);
                System.out.println("   P" + p.getIdPregunta() + ": " + p.getEnunciado() + " (" + p.tipoPregunta() + ")");
            }
            
            // Obtener matriz de respuestas para clustering
            List<List<Respuesta>> matriz = ctrlEncuesta.getRespuestasParaClustering(idEncuesta);
            
            if (matriz.isEmpty()) {
                System.out.println("\n⚠ La encuesta no tiene respuestas todavía.");
                return;
            }
            
            System.out.println("\n📊 MATRIZ DE RESPUESTAS (Formato para clustering):");
            System.out.println("   Total usuarios: " + matriz.size());
            System.out.println("   Mostrando primeros 20 usuarios...");
            System.out.println();
            
            // Encabezado con IDs de preguntas
            System.out.print("   Usuario          ");
            for (Pregunta p : preguntas) {
                System.out.print(String.format("  P%-4d", p.getIdPregunta()));
            }
            System.out.println();
            System.out.print("   ────────────────");
            for (int i = 0; i < preguntas.size(); i++) {
                System.out.print("───────");
            }
            System.out.println();
            
            // Filas de usuarios (máximo 20)
            int maxUsuarios = Math.min(20, matriz.size());
            for (int u = 0; u < maxUsuarios; u++) {
                List<Respuesta> respuestasUsuario = matriz.get(u);
                
                // Obtener nombre de usuario de la primera respuesta no nula
                String nombreUsuario = "Usuario" + (u + 1);
                for (Respuesta r : respuestasUsuario) {
                    if (r != null) {
                        nombreUsuario = r.getNomUsuario();
                        break;
                    }
                }
                
                System.out.print(String.format("   %-16s", 
                    nombreUsuario.length() > 16 ? nombreUsuario.substring(0, 16) : nombreUsuario));
                
                // Mostrar ID de respuesta o "null" si no respondió
                for (Respuesta r : respuestasUsuario) {
                    if (r == null) {
                        System.out.print("  ----  ");
                    } else {
                        System.out.print(String.format("  R%-4d", r.getId()));
                    }
                }
                System.out.println();
            }
            
            if (matriz.size() > 20) {
                System.out.println("\n   ... (" + (matriz.size() - 20) + " usuarios más no mostrados)");
            }
            
            System.out.println("\n📈 ESTADÍSTICAS:");
            System.out.println("   Total usuarios con respuestas: " + matriz.size());
            System.out.println("   Total preguntas: " + preguntas.size());
            
            // Contar respuestas nulas
            int totalRespuestas = 0;
            int respuestasNulas = 0;
            for (List<Respuesta> fila : matriz) {
                for (Respuesta r : fila) {
                    totalRespuestas++;
                    if (r == null) respuestasNulas++;
                }
            }
            System.out.println("   Respuestas totales: " + totalRespuestas);
            System.out.println("   Respuestas válidas: " + (totalRespuestas - respuestasNulas));
            System.out.println("   Respuestas faltantes: " + respuestasNulas + 
                             " (" + String.format("%.1f%%", 100.0 * respuestasNulas / totalRespuestas) + ")");
            
        } catch (Exception e) {
            System.out.println("❌ Error al obtener matriz: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
