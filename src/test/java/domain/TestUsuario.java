package domain;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

/**
 * Tests unitarios para la clase Usuario (JUnit4).
 * @author Peiyu Wang
 */
public class TestUsuario {

    private Usuario usuario;
    private final String userName = "tester";
    private final String contrasena = "123456";

    @Before
    public void setUp() {
        usuario = new Usuario(userName, contrasena);
    }

    @Test
    public void testConstructor_Getters() {
        assertEquals(userName, usuario.getNomUsuario());
        assertTrue(usuario.comprobarContrasena(contrasena));

        assertNotNull(usuario.getIdRespuestas());
        assertTrue(usuario.getIdRespuestas().isEmpty());

        assertNotNull(usuario.getIdEncuestasCreadas());
        assertTrue(usuario.getIdEncuestasCreadas().isEmpty());

        assertNotNull(usuario.getIdPerfiles());
        assertTrue(usuario.getIdPerfiles().isEmpty());
    }

    @Test
    public void testComprobarContrasenaIncorrecta() {
        assertFalse(usuario.comprobarContrasena("otra"));
    }

    @Test
    public void testNewContrasena() {
        String nueva = "newPass";
        usuario.newContrasena(nueva);
        assertTrue(usuario.comprobarContrasena(nueva));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewContrasena_SamePassword() {
        usuario.newContrasena(contrasena); // misma que la actual 
    }

    @Test
    public void testGuardar_EliminarIdRespuesta() {
        usuario.guardarIdRespuesta(1);
        usuario.guardarIdRespuesta(2);
        usuario.guardarIdRespuesta(3);

        ArrayList<Integer> respuestas = usuario.getIdRespuestas();
        assertEquals(3, respuestas.size());
        assertTrue(respuestas.contains(Integer.valueOf(1)));
        assertTrue(respuestas.contains(Integer.valueOf(2)));
        assertTrue(respuestas.contains(Integer.valueOf(3)));

        usuario.eliminarIdRespuesta(2);
        respuestas = usuario.getIdRespuestas();
        assertEquals(2, respuestas.size());
        assertFalse(respuestas.contains(Integer.valueOf(2)));
        assertTrue(respuestas.contains(Integer.valueOf(1)));
        assertTrue(respuestas.contains(Integer.valueOf(3)));

        
        usuario.eliminarIdRespuesta(999); // eliminar idRespuesta no existente
        respuestas = usuario.getIdRespuestas();
        assertEquals(2, usuario.getIdRespuestas().size());
    }

    @Test
    public void testGuardar_EliminarIdEncuesta() {
        usuario.guardarIdEncuesta(10);
        usuario.guardarIdEncuesta(20);

        ArrayList<Integer> encuestas = usuario.getIdEncuestasCreadas();
        assertEquals(2, encuestas.size());
        assertTrue(encuestas.contains(Integer.valueOf(10)));
        assertTrue(encuestas.contains(Integer.valueOf(20)));

        usuario.eliminarIdEncuestaCreada(20);
        encuestas = usuario.getIdEncuestasCreadas();
        assertEquals(1, encuestas.size());
        assertTrue(encuestas.contains(Integer.valueOf(10)));
        assertFalse(encuestas.contains(Integer.valueOf(20)));

        usuario.eliminarIdEncuestaCreada(999);// eliminar idEncuesta no existente
        encuestas = usuario.getIdEncuestasCreadas();
        assertEquals(1, encuestas.size());
    }
}
