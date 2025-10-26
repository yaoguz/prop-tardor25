package driver;

import java.util.Scanner;
import classcontroller.CtrlUsuario;
import classcontroller.CtrlEncuesta;
import classcontroller.CtrlPregunta;
import classcontroller.CtrlRespuesta;
import persistence.CtrlPersistence;
import domaincontroller.CtrlDominioUsuario;

/**
 * Driver interactivo para probar CtrlDominioEncuesta.
 * 
 * Este es un programa MANUAL con menú para el 1er lliurament.
 * El profesor lo ejecuta y prueba casos de uso por consola.
 * 
 * @author Peiyu Wang
 * @version 1.0
 */

/*Clase que testea la clase Encuesta */
public class DriverEncuesta {
    private static CtrlDominioEncuesta ctrlDominioEncuesta;
    private static Scanner scanner;
    //private static String u = null; // Usuario con sesión iniciada
    

    public static void main (String[] args) {
        // Inicializar dependencias (capa real, no mocks)
        CtrlEncuesta ctrlEncuesta = new CtrlEncuesta();
        CtrlUsuario ctrlUsuario = new CtrlUsuario();
        CtrlPregunta ctrlPregunta = new CtrlPregunta();
        CtrlRespuesta ctrlRespuesta = new CtrlRespuesta();
        CtrlPersistence ctrlPersistence = new CtrlPersistence();

        ctrlDominioEncuesta = new CtrlDominioEncuesta(ctrlUsuario,ctrlEncuesta,
	                             ctrlPregunta,ctrlRespuesta, ctrlPersistence);
        scanner = new Scanner(System.in);

        boolean salir = false;

        System.out.println("    DRIVER - CONTROLADOR DE ENCUESTA          ");
        System.out.println("----------------------------------------------");

        while (!salir) {
            inicio();
    }
}