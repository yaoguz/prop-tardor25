package driver;

import java.util.Scanner;
import java.util.Set;
import java.util.HashSet;
import domain.*;
import classcontroller.*;
import domaincontroller.CtrlDominioRespuesta;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;

/**
 * Driver interactivo para probar CtrlDominioRespuesta de forma aislada.
 * 
 * Usa mocks de Mockito para simular las dependencias y probar
 * solo la funcionalidad de CtrlDominioRespuesta.
 * 
 * @author Yaoyao
 * @version 2.0
 */
public class DriverRespuesta {
    
    private static CtrlDominioRespuesta ctrlDominioRespuesta;
    private static CtrlUsuario mockCtrlUsuario;
    private static CtrlPregunta mockCtrlPregunta;
    private static CtrlRespuesta mockCtrlRespuesta;
    private static Scanner scanner;
    
    public static void main(String[] args) {
        scanner = new Scanner(System.in);
        
        System.out.println("DRIVER - CONTROLADOR DE DOMINIO DE RESPUESTA");
        
        mockCtrlUsuario = mock(CtrlUsuario.class);
        mockCtrlPregunta = mock(CtrlPregunta.class);
        mockCtrlRespuesta = mock(CtrlRespuesta.class);
        
        ctrlDominioRespuesta = new CtrlDominioRespuesta(mockCtrlUsuario, 
                               mockCtrlPregunta, mockCtrlRespuesta);
        
        boolean salir = false;
        while (!salir) {
            mostrarMenu();
            int opcion = leerEntero();
            
            switch (opcion) {
                case 1:
                    testDarRespuesta();
                    break;
                case 2:
                    testModificarRespuesta();
                    break;
                case 3:
                    testEliminarRespuesta();
                    break;
                case 4:
                    testConsultarRespuesta();
                    break;
                case 0:
                    salir = true;
                    System.out.println("\nSaliendo del driver...\n");
                    break;
                default:
                    System.out.println("\nOpción no válida. Intenta de nuevo:\n");
            }
        }
        scanner.close();
    }
    
    private static void mostrarMenu() {
        System.out.println("\nMENU PRINCIPAL:");
        System.out.println("1. Dar respuesta");
        System.out.println("2. Modificar respuesta");
        System.out.println("3. Eliminar respuesta");
        System.out.println("4. Consultar respuesta");
        System.out.println("5. Ver listado de las encuestas respondidas por un usuario"); // pnd
        System.out.println("6. Importar respuestas"); // pnd
        System.out.println("0. Salir");        
        System.out.print("\nSelecciona una opcion: ");
    }
    
    private static void testDarRespuesta() {
        System.out.println("\nTEST DAR RESPUESTA:");
        System.out.println("1. Pregunta libre");
        System.out.println("2. Pregunta numerica");
        System.out.println("3. Pregunta de opcion unica");
        System.out.println("4. Pregunta de opcion multiple");
        System.out.print("\nSelecciona una opcion: ");
        
        int opcion = leerEntero();
        switch (opcion) {
            case 1:
                testDarRespuestaLibre();
                break;
            case 2:
                testDarRespuestaNumerica();
                break;
            case 3:
                testDarRespuestaOpcionUnica();
                break;
            case 4:
                testDarRespuestaOpcionMultiple();
                break;
            default:
                System.out.println("\nOpcion no valida.\n");
        }
    }

    private static void testDarRespuestaLibre() {
        System.out.println("\n--- DAR RESPUESTA LIBRE ---");
        
        System.out.print("Nombre usuario: ");
        String nomUsuario = scanner.nextLine();
        
        System.out.print("ID pregunta: ");
        int idPregunta = leerEntero();
        
        System.out.print("Texto respuesta: ");
        String textoRespuesta = scanner.nextLine();
        
        // Setup mocks
        Usuario mockUsuario = mock(Usuario.class);
        when(mockCtrlUsuario.getUsuario(nomUsuario)).thenReturn(mockUsuario);
        
        PreguntaLibre mockPregunta = mock(PreguntaLibre.class);
        when(mockCtrlPregunta.getPregunta(idPregunta)).thenReturn(mockPregunta);
        
        try {
            int idR = ctrlDominioRespuesta.darRespuesta(nomUsuario, idPregunta, textoRespuesta);

            System.out.println("\nRespuesta creada con ID: " + idRespuesta);
            
            // pnd de revisar a partir de aqui
            verify(mockCtrlUsuario).getUsuario(nomUsuario);
            verify(mockCtrlPregunta).getPregunta(idPregunta);
            verify(mockCtrlRespuesta).guardar(any(RespuestaLibre.class));
            verify(mockUsuario).guardarIdRespuesta(idRespuesta);
            
            System.out.println("Verificaciones correctas.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // OJO nombres de los métodos ********

    private static void testDarRespuestaNumerica() {
        System.out.println("\n--- DAR RESPUESTA NUMERICA ---");
        
        System.out.print("Nombre usuario: ");
        String nomUsuario = scanner.nextLine();
        
        System.out.print("ID pregunta: ");
        int idPregunta = leerEntero();
        
        System.out.print("Valor numerico: ");
        double valor = Double.parseDouble(scanner.nextLine());
        
        // Setup mocks
        Usuario mockUsuario = mock(Usuario.class);
        when(mockUsuario.getNombre()).thenReturn(nomUsuario);
        when(mockCtrlUsuario.obtener(nomUsuario)).thenReturn(mockUsuario);
        
        PreguntaNumerica mockPregunta = mock(PreguntaNumerica.class);
        when(mockPregunta.getId()).thenReturn(idPregunta);
        when(mockPregunta.getRangoMin()).thenReturn(0.0);
        when(mockPregunta.getRangoMax()).thenReturn(10.0);
        when(mockCtrlPregunta.obtener(idPregunta)).thenReturn(mockPregunta);
        
        try {
            int idRespuesta = ctrlDominioRespuesta.darRespuestaNumerica(
                nomUsuario, idPregunta, valor
            );
            System.out.println("\nRespuesta creada con ID: " + idRespuesta);
            
            verify(mockCtrlUsuario).obtener(nomUsuario);
            verify(mockCtrlPregunta).obtener(idPregunta);
            verify(mockCtrlRespuesta).guardar(any(RespuestaNumerica.class));
            verify(mockUsuario).guardarIdRespuesta(idRespuesta);
            
            System.out.println("Verificaciones correctas.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void testDarRespuestaOpcionUnica() {
        System.out.println("\n--- DAR RESPUESTA OPCION UNICA ---");
        
        System.out.print("Nombre usuario: ");
        String nomUsuario = scanner.nextLine();
        
        System.out.print("ID pregunta: ");
        int idPregunta = leerEntero();
        
        System.out.print("Indice opcion seleccionada: ");
        int opcion = leerEntero();
        
        // Setup mocks
        Usuario mockUsuario = mock(Usuario.class);
        when(mockUsuario.getNombre()).thenReturn(nomUsuario);
        when(mockCtrlUsuario.obtener(nomUsuario)).thenReturn(mockUsuario);
        
        PreguntaOpcionUnica mockPregunta = mock(PreguntaOpcionUnica.class);
        when(mockPregunta.getId()).thenReturn(idPregunta);
        String[] opciones = {"Opcion 1", "Opcion 2", "Opcion 3"};
        when(mockPregunta.getOpciones()).thenReturn(opciones);
        when(mockCtrlPregunta.obtener(idPregunta)).thenReturn(mockPregunta);
        
        try {
            int idRespuesta = ctrlDominioRespuesta.darRespuestaOpcionUnica(
                nomUsuario, idPregunta, opcion
            );
            System.out.println("\nRespuesta creada con ID: " + idRespuesta);
            
            verify(mockCtrlUsuario).obtener(nomUsuario);
            verify(mockCtrlPregunta).obtener(idPregunta);
            verify(mockCtrlRespuesta).guardar(any(RespuestaOpcionUnica.class));
            verify(mockUsuario).guardarIdRespuesta(idRespuesta);
            
            System.out.println("Verificaciones correctas.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void testDarRespuestaOpcionMultiple() {
        System.out.println("\n--- DAR RESPUESTA OPCION MULTIPLE ---");
        
        System.out.print("Nombre usuario: ");
        String nomUsuario = scanner.nextLine();
        
        System.out.print("ID pregunta: ");
        int idPregunta = leerEntero();
        
        System.out.print("Indices seleccionados (separados por comas): ");
        String input = scanner.nextLine();
        String[] partes = input.split(",");
        int[] opciones = new int[partes.length];
        for (int i = 0; i < partes.length; i++) {
            opciones[i] = Integer.parseInt(partes[i].trim());
        }
        
        // Setup mocks
        Usuario mockUsuario = mock(Usuario.class);
        when(mockUsuario.getNombre()).thenReturn(nomUsuario);
        when(mockCtrlUsuario.obtener(nomUsuario)).thenReturn(mockUsuario);
        
        PreguntaOpcionMultiple mockPregunta = mock(PreguntaOpcionMultiple.class);
        when(mockPregunta.getId()).thenReturn(idPregunta);
        String[] opcionesDisponibles = {"Opcion 1", "Opcion 2", "Opcion 3", "Opcion 4"};
        when(mockPregunta.getOpciones()).thenReturn(opcionesDisponibles);
        when(mockCtrlPregunta.obtener(idPregunta)).thenReturn(mockPregunta);
        
        try {
            int idRespuesta = ctrlDominioRespuesta.darRespuestaOpcionMultiple(
                nomUsuario, idPregunta, opciones
            );
            System.out.println("\nRespuesta creada con ID: " + idRespuesta);
            
            verify(mockCtrlUsuario).obtener(nomUsuario);
            verify(mockCtrlPregunta).obtener(idPregunta);
            verify(mockCtrlRespuesta).guardar(any(RespuestaOpcionMultiple.class));
            verify(mockUsuario).guardarIdRespuesta(idRespuesta);
            
            System.out.println("Verificaciones correctas.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }


    
    private static void testModificarRespuesta() {
        System.out.println("\n--- MODIFICAR RESPUESTA ---");
        
        System.out.print("ID respuesta a modificar: ");
        int idRespuesta = leerEntero();
        
        // Setup mock respuesta existente
        RespuestaLibre mockRespuesta = mock(RespuestaLibre.class);
        when(mockRespuesta.getId()).thenReturn(idRespuesta);
        when(mockRespuesta.getNomUsuario()).thenReturn("usuario1");
        when(mockRespuesta.getIdPregunta()).thenReturn(1);
        when(mockCtrlRespuesta.obtener(idRespuesta)).thenReturn(mockRespuesta);
        
        System.out.print("Nuevo texto: ");
        String nuevoTexto = scanner.nextLine();
        
        try {
            ctrlDominioRespuesta.modificarRespuestaLibre(idRespuesta, nuevoTexto);
            System.out.println("\nRespuesta modificada correctamente.");
            
            verify(mockCtrlRespuesta).obtener(idRespuesta);
            verify(mockRespuesta).setRespuesta(nuevoTexto);
            
            System.out.println("Verificaciones correctas.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    private static void testEliminarRespuesta() {
        System.out.println("\n--- ELIMINAR RESPUESTA ---");
        
        System.out.print("ID respuesta a eliminar: ");
        int idRespuesta = leerEntero();
        
        // Setup mocks
        RespuestaLibre mockRespuesta = mock(RespuestaLibre.class);
        when(mockRespuesta.getId()).thenReturn(idRespuesta);
        when(mockRespuesta.getNomUsuario()).thenReturn("usuario1");
        when(mockRespuesta.getIdPregunta()).thenReturn(1);
        when(mockCtrlRespuesta.obtener(idRespuesta)).thenReturn(mockRespuesta);
        
        Usuario mockUsuario = mock(Usuario.class);
        when(mockCtrlUsuario.obtener("usuario1")).thenReturn(mockUsuario);
        
        PreguntaLibre mockPregunta = mock(PreguntaLibre.class);
        when(mockCtrlPregunta.obtener(1)).thenReturn(mockPregunta);
        
        try {
            ctrlDominioRespuesta.eliminarRespuesta(idRespuesta);
            System.out.println("\nRespuesta eliminada correctamente.");
            
            verify(mockCtrlRespuesta).obtener(idRespuesta);
            verify(mockCtrlRespuesta).eliminar(idRespuesta);
            verify(mockUsuario).eliminarIdRespuesta(idRespuesta);
            verify(mockPregunta).eliminarIdRespuesta(idRespuesta);
            
            System.out.println("Verificaciones correctas.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    private static void testConsultarRespuesta() {
        System.out.println("\n--- CONSULTAR RESPUESTAS ---");
        
        System.out.print("Nombre usuario: ");
        String nomUsuario = scanner.nextLine();
        
        // Setup mocks
        Usuario mockUsuario = mock(Usuario.class);
        Set<Integer> idsRespuestas = new HashSet<>();
        idsRespuestas.add(1);
        idsRespuestas.add(2);
        when(mockUsuario.getIdsRespuestas()).thenReturn(idsRespuestas);
        when(mockCtrlUsuario.obtener(nomUsuario)).thenReturn(mockUsuario);
        
        try {
            Set<Integer> respuestas = ctrlDominioRespuesta.consultarRespuestasUsuario(nomUsuario);
            System.out.println("\nRespuestas del usuario:");
            for (int id : respuestas) {
                System.out.println("- ID: " + id);
            }
            
            verify(mockCtrlUsuario).obtener(nomUsuario);
            verify(mockUsuario).getIdsRespuestas();
            
            System.out.println("\nVerificaciones correctas.");
        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
    
    /**
     * Lee una opción numérica del menú.
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
}
