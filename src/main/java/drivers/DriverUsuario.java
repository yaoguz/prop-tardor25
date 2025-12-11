package drivers;

import java.util.Map;
import java.util.Scanner;
import java.util.List;

import domaincontrollers.CtrlDominioUsuario;
import domaincontrollers.CtrlDominioPregunta;
import domaincontrollers.CtrlDominioRespuesta;
import domaincontrollers.CtrlDominioEncuesta;
import persistence.GestorPersistencia;
import domain.Respuesta;
import domain.Usuario;

/**
 * Driver interactivo para probar CtrlDominioUsuario.
 * 
 * Este es un programa MANUAL con menú para el 1er lliurament.
 * El profesor lo ejecuta y prueba casos de uso por consola.
 * 
 * @author Xiangjun Yepan
 */
public class DriverUsuario {
    private static CtrlDominioUsuario ctrlDominioUsuario;
    private static CtrlDominioPregunta ctrlDominioPregunta;
    private static CtrlDominioRespuesta ctrlDominioRespuesta;
    private static CtrlDominioEncuesta ctrlDominioEncuesta;
    private static Scanner scanner;
    private static String usuarioActual = null; // Usuario con sesión iniciada
    
    /** Punto de entrada del driver para interactuar con CtrlDominioUsuario. */
    public static void main(String[] args) {
        // Inicializar dependencias (capa real, no mocks)
        GestorPersistencia gestor = GestorPersistencia.getInstance();
        
        ctrlDominioUsuario = CtrlDominioUsuario.getInstance(gestor, null, null);
        ctrlDominioRespuesta = CtrlDominioRespuesta.getInstance(gestor, ctrlDominioUsuario, null, null);
        ctrlDominioPregunta = CtrlDominioPregunta.getInstance(gestor, ctrlDominioRespuesta, null);
        ctrlDominioEncuesta = CtrlDominioEncuesta.getInstance(gestor, ctrlDominioPregunta, ctrlDominioUsuario);

        ctrlDominioUsuario = CtrlDominioUsuario.getInstance(gestor, ctrlDominioRespuesta, ctrlDominioPregunta);
        ctrlDominioRespuesta = CtrlDominioRespuesta.getInstance(gestor, ctrlDominioUsuario, ctrlDominioEncuesta, ctrlDominioPregunta);
        ctrlDominioPregunta = CtrlDominioPregunta.getInstance(gestor, ctrlDominioRespuesta, ctrlDominioEncuesta);

        crearDatosDePrueba();

        scanner = new Scanner(System.in);
        
        boolean salir = false;
        
        System.out.println("DRIVER - CONTROLADOR DE DOMINIO DE USUARIO");
        
        while (!salir) {
            mostrarMenu();
            int opcion = leerEntero();
            
            switch (opcion) {
                case 1:
                    testExisteUsuario();
                    break;
                case 2:
                    testGetUsuario();
                    break;
                case 3:
                    testRegistrarUsuario();
                    break;
                case 4:
                    testIniciarSesion();
                    break;
                case 5:
                    testCerrarSesionActual();
                    break;
                case 6:
                    testGetUsuarioActivo();
                    break;
                case 7:
                    testModificarContrasena();
                    break;
                case 8:
                    testGetEncuestasRespondidas();
                    break;
                case 9:
                    testGetRespuestasEncuesta();
                    break;
                case 10:
                    testGetAllUsuarios();
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

    /** Crea datos mínimos (usuario, encuesta, pregunta y respuesta) para pruebas manuales. */
    private static void crearDatosDePrueba() {
        String nomUsuario = ctrlDominioUsuario.registrarUsuario("user1", "123456", "123456");
        int idEncuesta = ctrlDominioEncuesta.crearEncuesta("user1", "Encuesta1");
        int idPreguntaLibre = ctrlDominioPregunta.crearPreguntaLibre(idEncuesta,
            "Quien eres?");
        String texto = "Soy user1";
        int idRespuesta = ctrlDominioRespuesta.darRespuesta(nomUsuario, idPreguntaLibre, 
                texto);
        System.out.println("Datos de prueba creados. ID respuesta: " + idRespuesta);
    }
    
    /** Muestra el menú principal y las opciones disponibles por consola. */
    private static void mostrarMenu() {
        System.out.println("\nMENÚ PRINCIPAL");
        System.out.println("1. Existe Usuario");
        System.out.println("2. Get Usuario");
        System.out.println("3. Registrar Usuario");
        System.out.println("4. Iniciar Sesión");
        System.out.println("5. Cerrar Sesión Actual");
        System.out.println("6. Get Usuario Activo");
        System.out.println("7. Modificar Contraseña");
        System.out.println("8. Get Encuestas Respondidas");
        System.out.println("9. Get Respuestas Encuesta");
        System.out.println("10. Get All Usuarios");
        System.out.println("0. Salir");
        
        if (usuarioActual != null) {
            System.out.println("Sesión activa: " + usuarioActual);
        }
        
        System.out.print("\nSelecciona una opción: ");
    }
    /** Consulta interactivamente si un usuario existe. */
    private static void testExisteUsuario() {
        System.out.println("\nTEST: EXISTE USUARIO");
        
        System.out.print("Nombre de usuario: ");
        String nomUsuario = scanner.nextLine();
        
        try {
            boolean existe = ctrlDominioUsuario.existeUsuario(nomUsuario);
            if (existe) {
                System.out.println("\nEl usuario " + nomUsuario + " existe.");
            } else {
                System.out.println("\nEl usuario " + nomUsuario + " no existe.");
            }
        } catch (Exception e) {
            System.out.println("\nError inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Obtiene y muestra información de un usuario por nombre. */
    private static void testGetUsuario() {
        System.out.println("\nTEST: GET USUARIO");
        
        System.out.print("Nombre de usuario: ");
        String nomUsuario = scanner.nextLine();
        
        try {
            String usuario = ctrlDominioUsuario.getUsuario(nomUsuario).getNomUsuario();
            System.out.println("\nUsuario " + usuario + " obtenido correctamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nError inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Registra un usuario usando datos introducidos por consola. */
    private static void testRegistrarUsuario() {
        System.out.println("\nTEST: REGISTRAR USUARIO");
        
        System.out.print("Nombre de usuario: ");
        String nomUsuario = scanner.nextLine();
        
        System.out.print("Contraseña: ");
        String contrasena = scanner.nextLine();
        
        System.out.print("Confirmar contraseña: ");
        String confirmacion = scanner.nextLine();
        
        try {
            String resultado = ctrlDominioUsuario.registrarUsuario(nomUsuario, contrasena, confirmacion);
            System.out.println("\nUsuario " + resultado + " registrado correctamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nError inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Inicia sesión para un usuario con las credenciales dadas. */
    private static void testIniciarSesion() {
        System.out.println("TEST: INICIAR SESIÓN");
        
        System.out.print("Nombre de usuario: ");
        String nomUsuario = scanner.nextLine();
        
        System.out.print("Contraseña: ");
        String contrasena = scanner.nextLine();
        
        try {
            ctrlDominioUsuario.iniciarSesion(nomUsuario, contrasena);
            usuarioActual = nomUsuario;
            System.out.println("\nSesión iniciada correctamente. Bienvenido, " + nomUsuario + "!");
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nError inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Cierra la sesión actual tras confirmación por consola. */
    private static void testCerrarSesionActual() {
        System.out.println("TEST: CERRAR SESIÓN ACTUAL");
        
        if (usuarioActual == null) {
            System.out.println("\nNo hay ninguna sesión activa.");
            return;
        }
        System.out.println("Quieres cerrar la sesión de " + usuarioActual + "? (s/n): ");
        String respuesta = scanner.nextLine();
        if (!respuesta.equalsIgnoreCase("s")) {
            System.out.println("\nOperación cancelada. La sesión sigue activa.");
            return;
        }
        try {
            ctrlDominioUsuario.cerrarSesionActual();
            System.out.println("\nSesión cerrada correctamente. Hasta pronto, " + usuarioActual + "!");
            usuarioActual = null;
        } catch (Exception e) {
            System.out.println("\nError: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Muestra el usuario actualmente activo (si existe). */
    private static void testGetUsuarioActivo() {
        System.out.println("TEST: GET USUARIO ACTIVO");
        
        try {
            String activo = ctrlDominioUsuario.getUsuarioActivo();
            if (activo != null) {
                System.out.println("\nUsuario activo: " + activo);
            } else {
                System.out.println("\nNo hay ningún usuario activo.");
            }
        } catch (Exception e) {
            System.out.println("\nError inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /** Modifica la contraseña del usuario actualmente con sesión iniciada. */
    private static void testModificarContrasena() {
        System.out.println("TEST: MODIFICAR CONTRASEÑA");
        
        if (usuarioActual == null) {
            System.out.println("\nDebes iniciar sesión primero.");
            return;
        }
        
        System.out.println("Usuario: " + usuarioActual);
        System.out.print("Nueva contraseña: ");
        String nuevaContrasena = scanner.nextLine();
        
        try {
            ctrlDominioUsuario.modificarContrasena(nuevaContrasena);
            System.out.println("\nContraseña modificada correctamente.");
        } catch (IllegalArgumentException e) {
            System.out.println("\nError: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\nError inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Muestra las encuestas respondidas por el usuario con sesión iniciada. */
    private static void testGetEncuestasRespondidas() {
        System.out.println("TEST: GET ENCUESTAS RESPONDIDAS");
        
        if (usuarioActual == null) {
            System.out.println("\nDebes iniciar sesión primero.");
            return;
        }
        
        try {
            List<Integer> encuestas = ctrlDominioUsuario.getEncuestasRespondidas();
            System.out.println("\nEncuestas respondidas por " + usuarioActual + ": " + encuestas);
        } catch (Exception e) {
            System.out.println("\nError inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /** Muestra las respuestas de una encuesta para un usuario dado. */
    private static void testGetRespuestasEncuesta() {
        System.out.println("TEST: GET RESPUESTAS ENCUESTA");
        
        System.out.print("Nombre de usuario: ");
        String nomUsuario = scanner.nextLine();
        System.out.print("ID de la encuesta: ");
        int idEncuesta = leerEntero();
        
        try {
            List<Respuesta> respuestas = ctrlDominioUsuario.getRespuestasEncuesta(nomUsuario, idEncuesta);
            System.out.println("\nRespuestas de la encuesta " + idEncuesta + " del usuario " + nomUsuario + ": " );
            for (Respuesta r : respuestas) {
                System.out.println("ID Respuesta: " + r.getId() + ", ID Pregunta: " + r.getIdPregunta());
            }
        } catch (Exception e) {
            System.out.println("\nError inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /** Lista por consola todos los usuarios registrados. */
    private static void testGetAllUsuarios() {
        System.out.println("TEST: GET ALL USUARIOS");
        try {
            Map<String, Usuario> usuarios = ctrlDominioUsuario.getAllUsuarios();
            System.out.println("Total de usuarios: " + usuarios.size());
            for (String nomU : usuarios.keySet()) {
                System.out.println("Usuario: " + nomU);
            }
        } catch (Exception e) {
            System.out.println("\nError inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /* 
    Esta funcionalidad ya no es necesaria.");
    Los datos ya están cargados en memoria en GestorPersistencia
    Los usuarios ya están cargados en memoria desde el inicio
    
    private static void testCargarUsuarios() {
        System.out.println("TEST: CARGAR USUARIOS");
        
        // Aquí se debería implementar la lógica para cargar usuarios desde persistencia.
        // Por simplicidad, este test solo simula la carga.
        System.out.print("Cuantos usuarios deseas cargar?: ");
        int cantidadUsuarios = leerEntero();
        TreeMap<String, Usuario> usuariosImportados = new TreeMap<>();

        for (int i = 0; i < cantidadUsuarios; i++) {
            System.out.print("Nombre de usuario " + (i + 1) + ": ");
            String nomUsuario = scanner.nextLine();
            System.out.print("Contraseña: ");
            String contrasena = scanner.nextLine();
            String nombre = ctrlDominioUsuario.registrarUsuario(nomUsuario, contrasena, contrasena);
            Usuario usuario = ctrlDominioUsuario.getUsuario(nombre);
            usuariosImportados.put(nomUsuario, usuario);
        }

        try {
            ctrlDominioUsuario.cargarUsuarios(usuariosImportados);
            System.out.println("\nUsuarios cargados correctamente.");
        } catch (Exception e) {
            System.out.println("\nError inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
        */

    /** Lee un entero por consola, repitiendo hasta recibir entrada válida. */
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
}
