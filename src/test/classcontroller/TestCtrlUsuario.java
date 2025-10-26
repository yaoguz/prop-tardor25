package classcontroller;

import main.domini.*;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
import java.util.Map;


import domain.Usuario;

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
        assert ctrlUsuario.mapaUsuariosVacio();
        // También limpiar usuarioActivo si existe
        String activo = ctrlUsuario.getUsuarioActivo();
        if (activo != null) {
            ctrlUsuario.cerrarSesion();
        }
    }

    @Test
    public void testCreaUsuario_CreaUsuarioCorrectamente() {
        String nombre = "testUser";
        String contrasena = "password123";

        Usuario u = ctrlUsuario.crearUsuario(nombre, contrasena);

        assertNotNull(u);
        assertEquals(nombre, u.getNomUsuario());
        // getUsuario debe devolver el mismo objeto
        Usuario aux = ctrlUsuario.getUsuario(nombre);
        assertSame(u, aux);
    }

    @Test(expected = IllegalStateException.class)
    public void testCreaUsuario_UsuarioDuplicado_LanzaExcepcion() {
        String nombre = "testUser";
        String contrasena = "password123";
        ctrlUsuario.crearUsuario(nombre, contrasena);
        // Intentar crear de nuevo debe lanzar IllegalStateException
        ctrlUsuario.crearUsuario(nombre, contrasena);
    }

    @Test
    public void testGetUsuario_GatUsuarioCorrectamente() {
        String nombre = "testUser";
        String contrasena = "password123";
        ctrlUsuario.crearUsuario(nombre, contrasena);
        Usuario u = ctrlUsuario.getUsuario(nombre);
        assertNotNull(u);
        assertEquals(nombre, u.getNomUsuario());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUsuario_UsuarioNoExistente_LanzaExcepcion() {
        ctrlUsuario.getUsuario("noExiste");
    }

    @Test
    public void testIniciarSesion_IniciaSesionCorrectamente() {
        String nombre = "testUser";
        String contrasena = "password123";
        Usuario u = ctrlUsuario.crearUsuario(nombre, contrasena);

        ctrlUsuario.iniciarSesion(nombre, contrasena);

        // Comprobar usuarioActivo 
        String activo = ctrlUsuario.getUsuarioActivo();
        assertNotNull(activo);
        assertEquals(nombre, activo);
    }

    @Test
    public void testIniciarSesion_ContrasenaIncorrecta() throws Exception {
        String nombre = "testUser";
        String contrasena = "password123";
        Usuario u = ctrlUsuario.crearUsuario(nombre, contrasena);

        ctrlUsuario.iniciarSesion(nombre, "passwordIncorrecta");

        // Comprobar usuarioActivo 
        String activo = ctrlUsuario.getUsuarioActivo();
        assertNull(activo);
    }

    @Test
    public void testCerrarSesion_CierraSesionCorrectamente() throws Exception {
        String nombre = "testUser";
        String contrasena = "password123";
        ctrlUsuario.crearUsuario(nombre, contrasena);
        ctrlUsuario.iniciarSesion(nombre, contrasena);

        // Cerrar sesion
        ctrlUsuario.cerrarSesion();

        String activo = CtrlUsuario.getUsuarioActivo();
        assertNull(activo);
    }

    @Test
    public void testGetUsuarioActivo_HayUsuarioActivo() throws Exception {
        String nombre = "testUser";
        String contrasena = "password123";
        ctrlUsuario.crearUsuario(nombre, contrasena);
        ctrlUsuario.iniciarSesion(nombre, contrasena);

        String activo = CtrlUsuario.getUsuarioActivo();
        assertNotNull(activo);
        assertEquals(nombre, activo);
    }

    @Test
    public void testGetUsuarioActivo_NoHayUsuarioActivo() throws Exception {
        String activo = CtrlUsuario.getUsuarioActivo();
        assertNull(activo);
    }

}
