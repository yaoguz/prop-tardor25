package driver;


import domain.Usuario;
import java.util.ArrayList;

/**
 * Clase testeadora de Usuario
 */
public class DriverUsuario {

    public void testConstructor() {
        Usuario u = new Usuario("Alessandro", "123456");
        System.out.println("Constructor probado: " + u);
    }

    public void testNewContrasena() {
        Usuario u = new Usuario("Alessandro", "123456");
        System.out.println("Contrasena original: 123456 ");
        u.comprobarContrasena("123456");

        u.newContrasena("654321");
        
        System.out.println("newContrasena(654321): ");
        System.out.println("Contrasena cambiado a 654321 : " + u.comprobarContrasena("654321"));

        u.newContrasena("654321");
        System.out.println("Contrasena nueva igual a 654321 : ");
    }

    public void testComprobarContrasena() {
        Usuario u = new Usuario("Alessandro", "123456");
        System.out.println("Contrasena original: 123456 ");

        System.out.println("comprobarContrasena(123456): " + u.comprobarContrasena("123456"));

    }

    public void testGetnomUsuario() {
        Usuario u = new Usuario("Alessandro", "123456");
        System.out.println("getNombre(): " + u.getNomUsuario()); 
    }

    public void testGetIdRespuestas() {
        Usuario u = new Usuario("Alessandro", "123456");
        u.guardarIdRespuesta(1);
        u.guardarIdRespuesta(2);
        System.out.println("Guardamos 1, 2 como ids");
        System.out.println("getIdRespuestas(): " + u.getIdRespuestas()); 
    }

    public void testGuardarIdRespuesta() {
        Usuario u = new Usuario("Alessandro", "123456");
        

        u.guardarIdRespuesta(1);
        System.out.println("guardarIdRespuesta(1): " + u.getIdRespuestas());
        
        
        u.guardarIdRespuesta(2);
        System.out.println("guardarIdRespuesta(2): " + u.getIdRespuestas());
       
    }

    public void testEliminarIdRespuesta() {
        Usuario u = new Usuario("Alessandro", "123456");
        u.guardarIdRespuesta(1);
        u.guardarIdRespuesta(2);
        System.out.println("getIdRespuestas(): " + u.getIdRespuestas()); 

        u.eliminarIdRespuesta(1);
        System.out.println("eliminarIdRespuesta(1): ");
        System.out.println("getIdRespuestas(): " + u.getIdRespuestas());

        u.eliminarIdRespuesta(2);
        System.out.println("eliminarIdRespuesta(2): ");
        System.out.println("getIdRespuestas(): " + u.getIdRespuestas());
    }

    public void testGetIdEncuestasCreadas() {
        Usuario u = new Usuario("Alessandro", "123456");
        u.guardarIdEncuesta(1);
        u.guardarIdEncuesta(2);

        System.out.println("Guardamos 1, 2 como ids");
        System.out.println("getIdEncuestasCreadas(): " + u.getIdEncuestasCreadas());
    }

    public void testGuardarIdEncuesta() {
        Usuario u = new Usuario("Alessandro", "123456");
    

        u.guardarIdEncuesta(1);
        System.out.println("guardarIdEncuesta(1): " + u.getIdEncuestasCreadas());
        
        
        u.guardarIdEncuesta(2);
        System.out.println("guardarIdEncuesta(2): " + u.getIdEncuestasCreadas());
    }

    public void testEliminarIdEncuesta() {
        Usuario u = new Usuario("Alessandro", "123456");
        u.guardarIdEncuesta(1);
        u.guardarIdEncuesta(2);
        System.out.println("getIdEncuestasCreadas(): " + u.getIdEncuestasCreadas()); 

        u.eliminarEncuestaCreada(1);
        System.out.println("eliminarEncuestaCreada(1): ");
        System.out.println("getIdEncuestasCreadas(): " + u.getIdEncuestasCreadas());

        u.eliminarEncuestaCreada(2);
        System.out.println("eliminarEncuestaCreada(2): ");
        System.out.println("getIdEncuestasCreadas(): " + u.getIdEncuestasCreadas());
    }

    public static void main(String[] args) {
        DriverUsuario driver = new DriverUsuario();

        // Test Constructor
        System.out.println("===== Test Constructor =====");
        driver.testConstructor();
        System.out.println();

        // Test getNomUsuario
        System.out.println("===== Test getNomUsuario =====");
        driver.testGetnomUsuario();
        System.out.println();

        // Test comprobarContrasena
        System.out.println("===== Test comprobarContrasena =====");
        driver.testComprobarContrasena();
        System.out.println();

        // Test newContrasena
        System.out.println("===== Test newContrasena =====");
        driver.testNewContrasena();
        System.out.println();

        // Test guardarIdRespuesta
        System.out.println("===== Test guardarIdRespuesta =====");
        driver.testGuardarIdRespuesta();
        System.out.println();

        // Test getIdRespuestas
        System.out.println("===== Test getIdRespuestas =====");
        driver.testGetIdRespuestas();
        System.out.println();

        // Test eliminarIdRespuesta
        System.out.println("===== Test eliminarIdRespuesta =====");
        driver.testEliminarIdRespuesta();
        System.out.println();

        // Test guardarIdEncuesta
        System.out.println("===== Test guardarIdEncuesta =====");
        driver.testGuardarIdEncuesta();
        System.out.println();

        // Test getIdEncuestasCreadas
        System.out.println("===== Test getIdEncuestasCreadas =====");
        driver.testGetIdEncuestasCreadas();
        System.out.println();

        // Test eliminarIdEncuesta
        System.out.println("===== Test eliminarIdEncuesta =====");
        driver.testEliminarIdEncuesta();
        System.out.println();
    }

}
