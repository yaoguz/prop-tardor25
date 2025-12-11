package drivers;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;
import java.util.HashMap;

import domain.*;
import domain.algorithm.estructuras.Perfil;
import domain.algorithm.estructuras.ResultadoClustering;
import domain.algorithm.funciones.EvaluarCluster;
import domaincontrollers.*;
import persistence.GestorPersistencia;

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
    private static ResultadoClustering ultimoResultado = null;

    public static void main(String[] args) {
        // Inicializar dependencias (capa real, no mocks)
        GestorPersistencia gestor = GestorPersistencia.getInstance();
        
        ctrlRespuesta = CtrlDominioRespuesta.getInstance(gestor, null, null, null);
        ctrlPregunta = CtrlDominioPregunta.getInstance(gestor, ctrlRespuesta, null);
        ctrlUsuario = CtrlDominioUsuario.getInstance(gestor, ctrlRespuesta, ctrlPregunta);
        ctrlEncuesta = CtrlDominioEncuesta.getInstance(gestor, ctrlPregunta, ctrlRespuesta, ctrlUsuario);
        ctrlPerfil = CtrlDominioPerfil.getInstance(ctrlEncuesta);
        
        // Actualizar referencias circulares
        ctrlRespuesta = CtrlDominioRespuesta.getInstance(gestor, ctrlUsuario, ctrlEncuesta, ctrlPregunta);
        ctrlPregunta = CtrlDominioPregunta.getInstance(gestor, ctrlRespuesta, ctrlEncuesta);
        
        scanner = new Scanner(System.in);

        boolean salir = false;

        // Información de inicio
        System.out.println("+---------------- DRIVER INTERACTIVO - CONTROLADOR DE: ----------------+");
        System.out.println("|  ####  #      #   #   ####  #####  #####  ####   #####  #   #   #### |");
        System.out.println("| #      #      #   #  #        #    #      #   #    #    ##  #  #     |");
        System.out.println("| #      #      #   #   ###     #    ####   ####     #    # # #  # ### |");
        System.out.println("| #      #      #   #      #    #    #      # #      #    #  ##  #   # |");
        System.out.println("|  ####  #####   ###   ####     #    #####  #  #   #####  #   #   ###  |");
        System.out.println("+----------------------------------------------------------------------+");
        System.out.println("Cuando se importe una encuesta e introduzca un nombre de usuario, se creará");
        System.out.println("para asignarle esa encuesta (si no existía ya).");        

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
                    mostrarEstadoSistema();
                    break;
                case 6:
                    verRespuestasEncuesta();
                    break;
                case 0:
                    salir = true;
                    break;
                default:
                    System.out.println("\nOpción no válida. Intenta de nuevo.\n");
            }

            System.out.println("Presione enter para continuar.");
            scanner.nextLine();

        }
        
        scanner.close();
        System.out.println("\nSaliendo del driver...\n");
    }
    /**
     * Punto de entrada del driver en modo interactivo.
     * Inicializa controladores y procesa el menú de usuario.
     * @param args argumentos de línea de comandos (no usados)
     */
    
    private static void mostrarMenu() {
        System.out.println("\nMENÚ DRIVER CLUSTERING");
        System.out.println("1. Importar encuesta desde archivo JSON");
        System.out.println("2. Importar respuestas desde archivo CSV");
        System.out.println("3. Ejecutar clustering");
        System.out.println("4. Obtener información de los perfiles del último clustering ejecutado");
        System.out.println("5. Mostrar información de los datos actuales");
        System.out.println("6. Ver respuestas de una encuesta");
        System.out.println("0. Salir");
        System.out.println("\nIntroduzca el número correspondiente a la acción a ejecutar: ");
    }

    /** Muestra el menú principal del driver por consola. */

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

    /** Lee un entero desde la entrada estándar, repite hasta obtener uno válido. */

    /** Importa una encuesta desde un archivo JSON y muestra información básica. */
    private static void importarEncuesta() {
        System.out.println("\n--{ IMPORTAR UNA ENCUESTA DE UN FICHERO }--");
        
        System.out.println("Introduzca el usuario propietario de la encuesta: ");
        String usr = scanner.nextLine().trim();
        if(!ctrlUsuario.existeUsuario(usr)) ctrlUsuario.registrarUsuario(usr, "123456", "123456");


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

    /** Importa respuestas desde un archivo CSV. */
    private static void importarRespuestas() {
        System.out.println("\n--{ IMPORTAR RESPUESTAS DE UN FICHERO }--");
        
        System.out.print("Introduzca la ruta del archivo CSV de respuestas: ");
        String ruta = scanner.nextLine().trim();
        File fichero = new File(ruta);

        if (!fichero.exists()) {
            System.out.println("El archivo especificado no existe: " + ruta);
            return;
        }

        try {
            ctrlRespuesta.importarRespuestas(fichero);
            System.out.println("Se han importado las respuestas exitosamente.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    /** Ejecuta el algoritmo de clustering para una encuesta seleccionada. */
    private static void ejecutarClustering() {
        System.out.println("\n--{ EJECUTAR ALGORITMO DE CLUSTERING }--");
        
        HashMap<Integer, Encuesta> encuestas = ctrlEncuesta.getAllEncuestas();
        if (encuestas.isEmpty()) {
            System.out.println("No hay encuestas. Importe primero una encuesta.");
            return;
        }

        System.out.println("Encuestas disponibles:");
        for (Encuesta e : encuestas.values()) {
            System.out.println("Id: " + e.getIdEncuesta() + " - " + e.getTitulo() + " (" + e.getIdPreguntas().size() + " preguntas)");
        }
        System.out.println("Indique el identificador de la encuesta a analizar: ");
        int idEncuesta = Integer.parseInt(scanner.nextLine().trim());

        if (!ctrlEncuesta.existeEncuesta(idEncuesta)) {
            System.out.println("La encuesta indicada no existe.");
            return;
        }

        // Verificar que hay respuestas
        var respuestas = ctrlEncuesta.getRespuestasParaClustering(idEncuesta);
        if (respuestas.isEmpty()) {
            System.out.println("La encuesta indicada no tiene respuestas. Importe respuestas primero.");
            return;
        }
		System.out.println("La encuesta tiene respuestas de " + respuestas.size() + " usuarios.");
		if (respuestas.size() < 2) {
			System.out.println("No son suficientes usuarios para ejecutar el clustering. Se necesitan al menos 2 usuarios con respuestas.");
			return;
		}

		System.out.print("Introduzca el número de clústers que se quieren generar (entre 2 y " + respuestas.size() + "): ");
		int k = Integer.parseInt(scanner.nextLine().trim());

		if (k < 2 || k > respuestas.size()) {
			System.out.println("El número de clústers ha de estar entre 2 y " + respuestas.size() + "(ambos incluidos).");
			return;
		}
        System.out.println("Indique el algoritmo a usar [ 1: K-Means | 2: K-Means++ | 3: K-Medoids ]: ");
        int opcionAlg = scanner.nextInt();
        
        String algoritmo = null;
        switch(opcionAlg){
            case 1:
                algoritmo = "kmeans";
                break;
            case 2:
                algoritmo = "kmeans++";
                break;
            case 3:
                algoritmo = "kmedoids";
                break;
            default:
                System.out.println("Número no válido.");
        }

        System.out.println("Ejecutando...");
        long inicio = System.currentTimeMillis();

        ResultadoClustering resultado = ctrlPerfil.ejecutarClustering(idEncuesta, k, algoritmo);
        
        long fin = System.currentTimeMillis();
        System.out.println("Clustering completado en " + (fin - inicio) + " milisegundos.");

        // Guardar para futuras operaciones
        ultimoIdEncuesta = idEncuesta;
        ultimoResultado = resultado;

        // Mostrar resumen
        List<Perfil> perfiles = resultado.getPerfiles();
        System.out.println("Información del clustering:");
        for (int i = 0; i < perfiles.size(); i++) {
            Perfil p = perfiles.get(i);
            System.out.println("El perfil " + (i + 1) + " tiene " + p.getMiembros().size() + " miembros. Su representante es: " + p.getRepresentante() + ".");
        }

        // Mostrar calidad
        double silueta = resultado.getCalidad();
        System.out.println("El coeficiente de silueta del clustering es: " + String.format("%.4f", silueta));
        System.out.println("Calidad "+EvaluarCluster.interpretarSilueta(silueta));
    }

    /** Muestra los perfiles resultantes del último clustering ejecutado. */
    private static void obtenerPerfiles() {
        System.out.println("\n--{ OBTENER PERFILES DEL ÚLTIMO CLUSTERING }--");
        
        if (ultimoResultado == null) {
            System.out.println("No se ha ejecutado ningún clustering.");
            return;
        }

        List<Perfil> perfiles = ultimoResultado.getPerfiles();
        System.out.println("La última ejecución del algoritmo generó los siguientes " + perfiles.size() + " perfiles:");

        for (int i = 0; i < perfiles.size(); i++) {
            Perfil p = perfiles.get(i);
            System.out.println("Perfil " + (i + 1) + ":");
            System.out.println("-Tamaño: " + p.getMiembros().size() + " usuarios.");
            System.out.println("-Representante: " + p.getRepresentante() + ".");
            System.out.println("-Miembros: " + String.join(", ", p.getMiembros()) + ".");
        }
    }

    /** Muestra información resumida del estado actual del sistema (usuarios, encuestas, último clustering). */
    private static void mostrarEstadoSistema() {
        System.out.println("\n--{ INFORMACIÓN ACTUAL DE LOS DATOS DEL SISTEMA }--");
        
        Map<String,Usuario> usuarios = ctrlUsuario.getAllUsuarios();
        Map<Integer, Encuesta> encuestas = ctrlEncuesta.getAllEncuestas();
        
        System.out.println("Usuarios registrados: " + usuarios.size());
        System.out.println("Encuestas registradas: " + encuestas.size());
        
        if (!encuestas.isEmpty()) {
            System.out.println("Información de las encuestas:");
            for (Encuesta e : encuestas.values()) {
                var respuestas = ctrlEncuesta.getRespuestasParaClustering(e.getIdEncuesta());
                System.out.println("\nId: " + e.getIdEncuesta() + " - " + e.getTitulo());
                System.out.println("-Preguntas: " + e.getIdPreguntas().size());
                System.out.println("-Usuarios que han respondido: " + respuestas.size());
            }
        }
        
        if (ultimoResultado != null) {
            System.out.println("\nÚltimo clustering ejecutado:");
            System.out.println("Encuesta con id: " + ultimoIdEncuesta);
            System.out.println("Perfiles generados: " + ultimoResultado.getPerfiles().size());
            System.out.println("Calidad (coeficiente de silueta): " + String.format("%.4f", ultimoResultado.getCalidad()));
        }
    }

    /** Visualiza las respuestas de una encuesta seleccionada por consola. */
    private static void verRespuestasEncuesta() {
        System.out.println("\n--{ VISUALIZAR RESPUESTAS DE UNA ENCUESTA }--");
        
        HashMap<Integer, Encuesta> encuestas = ctrlEncuesta.getAllEncuestas();
        if (encuestas.isEmpty()) {
            System.out.println("No hay encuestas. Importe primero una encuesta.");
            return;
        }

        System.out.println("Encuestas disponibles:");
        for (Encuesta e : encuestas.values()) {
            System.out.println("Id: " + e.getIdEncuesta() + " - " + e.getTitulo() + " (" + e.getIdPreguntas().size() + " preguntas)");
        }
        System.out.println("Indique el identificador de la encuesta a analizar: ");
        int idEncuesta = Integer.parseInt(scanner.nextLine().trim());

        if (!ctrlEncuesta.existeEncuesta(idEncuesta)) {
            System.out.println("La encuesta indicada no existe.");
            return;
        }

        try {
            Map<Pregunta, Set<Respuesta>> mapa = ctrlEncuesta.getRespuestasEncuesta(idEncuesta);
            
            if (mapa.isEmpty()) {
                System.out.println("La encuesta indicada no tiene respuestas.");
                return;
            }
            
            System.out.println("Respuestas de la encuesta " + idEncuesta + ":");
            for (Map.Entry<Pregunta, Set<Respuesta>> entry : mapa.entrySet()) {
                Pregunta p = entry.getKey();
                System.out.println("\nId de pregunta: " + p.getIdPregunta());
                System.out.println("Enunciado: " + p.getEnunciado());
                System.out.println("Tipo: " + p.tipoPregunta());
                
                if (entry.getValue().isEmpty()) {
                    System.out.println("No tiene respuestas.");
                } else {
                    System.out.println("Respuestas:");
                    for (Respuesta r : entry.getValue()) {
                        System.out.println("-Id de la respuesta: " + r.getId() + ". Usuario: " + r.getNomUsuario());
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
