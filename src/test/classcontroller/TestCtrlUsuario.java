package classcontroller;

import static org.junit.Assert.*;

import java.lang.reflect.Field;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;

import domain.Usuario;

/**
 * Tests unitarios para la clase CtrlUsuario (implementación singleton).
 * Las pruebas usan reflection para reiniciar el singleton entre tests y mantener
 * aislamiento.
 */
public class TestCtrlUsuario {

    private CtrlUsuario ctrlUsuario;

    @Before
    @SuppressWarnings("unchecked")
    public void setUp() throws Exception {
        // Forzar que el singleton se reinicie entre tests para evitar estado compartido
        Field singletonField = CtrlUsuario.class.getDeclaredField("singletonObject");
        singletonField.setAccessible(true);
        // Set the static field to null
        singletonField.set(null, null);

        // Obtener nueva instancia limpia
        ctrlUsuario = CtrlUsuario.getInstance();

        // Asegurar que el mapa interno 'usuarios' esté vacío
        Field usuariosField = CtrlUsuario.class.getDeclaredField("usuarios");
        usuariosField.setAccessible(true);
        Map<String, Usuario> usuarios = (Map<String, Usuario>) usuariosField.get(ctrlUsuario);
        usuarios.clear();
        // También limpiar usuarioActivo si existe
        try {
            Field activo = CtrlUsuario.class.getDeclaredField("usuarioActivo");
            activo.setAccessible(true);
            activo.set(ctrlUsuario, null);
        } catch (NoSuchFieldException e) {
            // no pasa nada si no existe
        }
    }

    @Test
    public void testCrearUsuario_Success() {
        String nombre = "testUser";
        String contrasena = "password123";

        Usuario u = ctrlUsuario.crearUsuario(nombre, contrasena);

        assertNotNull(u);
        assertEquals(nombre, u.getNomUsuario());
        // getUsuario debe devolver el mismo objeto
        Usuario fromGet = ctrlUsuario.getUsuario(nombre);
        assertSame(u, fromGet);
    }

    @Test(expected = IllegalStateException.class)
    public void testCrearUsuario_Duplicate_Throws() {
        String nombre = "testUser";
        String contrasena = "password123";
        ctrlUsuario.crearUsuario(nombre, contrasena);
        // Intentar crear de nuevo debe lanzar IllegalStateException
        ctrlUsuario.crearUsuario(nombre, contrasena);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetUsuario_NotFound_Throws() {
        ctrlUsuario.getUsuario("noExiste");
    }

    @Test
    public void testIniciarSesion_CorrectPassword_SetsUsuarioActivo() throws Exception {
        String nombre = "testUser";
        String contrasena = "password123";
        Usuario u = ctrlUsuario.crearUsuario(nombre, contrasena);

        ctrlUsuario.iniciarSesion(nombre, contrasena);

        // Comprobar usuarioActivo mediante reflection
        Field activo = CtrlUsuario.class.getDeclaredField("usuarioActivo");
        activo.setAccessible(true);
        Usuario activoObj = (Usuario) activo.get(ctrlUsuario);
        assertNotNull(activoObj);
        assertEquals(nombre, activoObj.getNomUsuario());
        assertSame(u, activoObj);
    }

    @Test
    public void test() throws Exception {
        
    }

    @Test
    public void testCerrarSesion_ClearsUsuarioActivo() throws Exception {
        String nombre = "testUser";
        String contrasena = "password123";
        ctrlUsuario.crearUsuario(nombre, contrasena);
        ctrlUsuario.iniciarSesion(nombre, contrasena);

        // Cerrar sesion
        ctrlUsuario.cerrarSesion();

        Field activo = CtrlUsuario.class.getDeclaredField("usuarioActivo");
        activo.setAccessible(true);
        assertNull(activo.get(ctrlUsuario));
    }

}
