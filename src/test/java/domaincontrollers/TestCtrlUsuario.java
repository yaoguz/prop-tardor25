package domaincontrollers;

import main.domini.*;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.Map;


import domain.Usuario;
import domaincontrollers.CtrlUsuario;

/**
 * Tests unitarios para la clase CtrlUsuario (implementación singleton).
 */
public class TestCtrlUsuario {

    private CtrlUsuario ctrlUsuario;

    @Before
    public void setUp() throws Exception {
        // Obtener nueva instancia limpia
        ctrlUsuario = CtrlUsuario.getInstance();

        // Asegurar que el mapa interno 'usuarios' esté vacío
        assertTrue("Mapa de usuarios no esta vacia", ctrlUsuario.mapaUsuariosVacio());
        // También limpiar usuarioActivo si existe
        String activo = ctrlUsuario.getUsuarioActivo();
        if (activo != null) {
            ctrlUsuario.cerrarSesion();
        }
    }

    /**
     * Test: Comprueba que crearUsuario crea un usuario correctamente.
     */
    @Test
    public void testCreaUsuario_CreaUsuarioCorrectamente() {
        String nombre = "testUser";
        String contrasena = "password123";

        Usuario u = ctrlUsuario.crearUsuario(nombre, contrasena);

        assertNotNull("usuario null", u);
        assertEquals("nombre usuario diferentes", nombre, u.getNomUsuario());
        // getUsuario debe devolver el mismo objeto
        Usuario aux = ctrlUsuario.getUsuario(nombre);
        assertSame("los dos objetos de usuario no son los mismos", u, aux);
    }

    /**
     * Test: Comprueba que no se pueden crear dos usuarios con el mismo nombre,
     * sino lanza una excepcion.
     */
    @Test(expected = IllegalStateException.class)
    public void testCreaUsuario_UsuarioDuplicado_LanzaExcepcion() {
        String nombre = "testUser";
        String contrasena = "password123";
        ctrlUsuario.crearUsuario(nombre, contrasena);
        // Intentar crear de nuevo debe lanzar IllegalStateException
        ctrlUsuario.crearUsuario(nombre, contrasena);
    }

    /**
     * Test: Comprueba que getUsuario devuelve un usuario con el mismo nombre 
     * que se le pasa por parámetro.
     */
    @Test
    public void testGetUsuario_GatUsuarioCorrectamente() {
        String nombre = "testUser";
        String contrasena = "password123";
        ctrlUsuario.crearUsuario(nombre, contrasena);
        Usuario u = ctrlUsuario.getUsuario(nombre);
        assertNotNull("usuario null", u);
        assertEquals("nombre de usuario diferentes", nombre, u.getNomUsuario());
    }

    /**
     * Test: Comprueba que getUsuario no puede obtener un usuario si no existe,
     * en este caso lanza una excepción.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testGetUsuario_UsuarioNoExistente_LanzaExcepcion() {
        ctrlUsuario.getUsuario("noExiste");
    }

    /**
     * Test: Comprueba que iniciarSesion pone en usuarioActivo al usuario que iniciaSesion.
     */
    @Test
    public void testIniciarSesion_IniciaSesionCorrectamente() {
        String nombre = "testUser";
        String contrasena = "password123";
        Usuario u = ctrlUsuario.crearUsuario(nombre, contrasena);

        ctrlUsuario.iniciarSesion(nombre, contrasena);

        // Comprobar usuarioActivo 
        String activo = ctrlUsuario.getUsuarioActivo();
        assertNotNull("usuarioActivo null", activo);
        assertEquals("nombre de usuario diferentes", nombre, activo);
    }

    /**
     * Test: Comprueba que no se puede iniciar sesion con una contrasena incorrecta,
     * en este caso no se cambia el usuarioActivo.
     */
    @Test
    public void testIniciarSesion_ContrasenaIncorrecta() throws Exception {
        String nombre = "testUser";
        String contrasena = "password123";
        Usuario u = ctrlUsuario.crearUsuario(nombre, contrasena);

        ctrlUsuario.iniciarSesion(nombre, "passwordIncorrecta");

        // Comprobar usuarioActivo 
        String activo = ctrlUsuario.getUsuarioActivo();
        assertNull("usuarioActivo no null", activo);
    }

    /**
     * Test: Comprueba que no se puede iniciar sesion si ya hay alguien logueado,
     * en este caso no se cambia el usuarioActivo.
     */
    @Test
    public void testIniciarSesion_IniciarSesionSinCerrarSesion(){
        String nombre1 = "testUser";
        String contrasena1 = "password123";
        Usuario u = ctrlUsuario.crearUsuario(nombre1, contrasena1);
        ctrlUsuario.iniciarSesion(nombre1, contrasena1);

        String nombre2 = "testUser";
        String contrasena2 = "password123";
        Usuario u = ctrlUsuario.crearUsuario(nombre2, contrasena2);
        ctrlUsuario.iniciarSesion(nombre2, contrasena2);

        String activo = ctrlUsuario.getUsuarioActivo();
        assertNull("usuarioActivo no null", activo);
        assertEquals("nombre de usuario diferentes", nombre1, activo);
    }

    /**
     * Test: Comprueba que cerrarSesion deja en null el usuarioActivo.
     */
    @Test
    public void testCerrarSesion_CierraSesionCorrectamente(){
        String nombre = "testUser";
        String contrasena = "password123";
        ctrlUsuario.crearUsuario(nombre, contrasena);
        ctrlUsuario.iniciarSesion(nombre, contrasena);

        // Cerrar sesion
        ctrlUsuario.cerrarSesion();

        String activo = ctrlUsuario.getUsuarioActivo();
        assertNull("usuarioActivo no null", activo);
    }

    /**
     * Test: Comprueba que getUsuarioActivo devuelve el nombre del usuario activo.
     */
    @Test
    public void testGetUsuarioActivo_HayUsuarioActivo(){
        String nombre = "testUser";
        String contrasena = "password123";
        ctrlUsuario.crearUsuario(nombre, contrasena);
        ctrlUsuario.iniciarSesion(nombre, contrasena);

        String activo = ctrlUsuario.getUsuarioActivo();
        assertNotNull("usuarioActivo null", activo);
        assertEquals("nombre de usuario diferentes", nombre, activo);
    }

    /**
     * Test: Comprueba que no getUsuarioActivo devuelve null si no hay ningun usuario que haya 
     * iniciado sesion.
     */
    @Test
    public void testGetUsuarioActivo_NoHayUsuarioActivo(){
        String activo = ctrlUsuario.getUsuarioActivo();
        assertNull("usuarioActivo no null", activo);
    }

}
