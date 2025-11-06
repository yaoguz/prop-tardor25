package test;

import src.domini.*;
import org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.Test;


/**
 * Clase testeadora de Usuario
 */
public Class TestUsuario() {

    private Usuario usuario;
    private final Stirng userName = "tester";
    private final String contrasena = "123456";

    /**
     * Configuraciones iniciales
     */
    @Before
    public void inicialConf() {
        usuario = new Usuario (userName, contrasena);
    }
    

    @Test
    public void testConstructor() {
        assertEquals("Nombre creado incorrecto",userName, usuario.getNomUsuario());
        
        assertTrue("Contrasena creada incorrecta", usuario.comprobarContrasena(contrasena));

        assertNotNull("idRespuestas null", usuario.getIdRespuestas());
        assertTrue("idRespuestas no vacia",usuario.getIdRespuestas().isEmpty());
        
        assertNotNull("idEncuestasCreadas null", usuario.getIdEncuestasCreadas());
        assertTrue("idEncuestasCreadas no vacia",usuario.getIdEncuestasCreadas().isEmpty());
        
        assertNotNull("idPerfiles null", usuario.getIdPerfiles());
        assertTrue("idPerfiles no vacia", usuario.getIdPerfiles().isEmpty());
    }

    @Test (expected = IllegalArgumentException.class)
    public void testConstructo_NombreVacio() {
        new Usuario("", contrasena);
    }

    @Test (expected = IllegalArgumentException.class) 
    public void testConstructor_ContrasenaVacia() {
        new Usuario(userName, "");
    }

    @Test
    public void testGetNomUsuario() {
        assertEquals("Nombre obtenido incorrecto", NOMBRE_USUARIO, usuario.getNomUsuario());
    }

    @test
    public void testComprobarContrasenaCorrecta() {
        assertTrue("Contrasena incorrecta", usuario.comprobarContrasena(contrasena));
    }

    @test
    public void testComprobarContrasenaIncorrecta() {
        assertFalse("Contrasena incorrecta pasa", usuario.comprobarContrasena("otraContrasena"));
    }

    @Test
    public void testNewContrasena() {
        String nuevaContrasena = "newContrasena";
        
        // Cambiar contraseña
        usuario.newContrasena(nuevaContrasena);
        
        // Verificar que la nueva funciona
        assertTrue("Contrasena no cambiada", usuario.comprobarContrasena(nuevaContrasena));
        
    }

    @Test(expected = IllegalArgumentException.class)
    public void testNewContrasenaVacia () {
        usuario.newContrasena("");
    }


    @Test
    public void testGuardarIdRespuesta() {
        usuario.guardarIdRespuesta(1);
        usuario.guardarIdRespuesta(2);
        usuario.guardarIdRespuesta(3);
        
        Set<Integer> respuestas = usuario.getIdRespuestas();
        assertEquals("No se guardo todo idR", 3, respuestas.size());
        assertTrue("No contiene lo guardado de idR", respuestas.contains(1));
        assertTrue("No contiene lo guardado de idR",respuestas.contains(2));
        assertTrue("No contiene lo guardado de idR", respuestas.contains(3));
    }



    @Test
    public void testEliminarIdRespuesta() {
        usuario.guardarIdRespuesta(1);
        usuario.guardarIdRespuesta(2);
        usuario.guardarIdRespuesta(3);
        
        // Eliminar respuesta 2
        usuario.eliminarIdRespuesta(2);
        
        Set<Integer> respuestas = usuario.getIdRespuestas();
        assertEquals("IdR no eliminado", 2, respuestas.size());
        assertTrue("IdR borrado incorrecto", respuestas.contains(1));
        assertFalse("IdR no borrado", respuestas.contains(2));
        assertTrue("idR borrado incorrect", respuestas.contains(3));
    }

    @Test
    public void testEliminarIdRespuesta_NoExistente() {
        usuario.guardarIdRespuesta(1);
        
        // Intentar eliminar ID que no existe
        usuario.eliminarIdRespuesta(999);
        
        Set<Integer> respuestas = usuario.getIdRespuestas();
        assertEquals("IdR borrado", 1, respuestas.size());
        assertTrue("IdR borrado", respuestas.contains(1));
    }


    @Test
    public void testGuardarIdEncuesta() {
        usuario.guardarIdEncuesta(10);
        usuario.guardarIdEncuesta(20);
        
        Set<Integer> encuestas = usuario.getIdEncuestasCreadas();
        assertEquals("No se guardo todo idE",2, encuestas.size());
        assertTrue("No contiene lo guardado de idE",encuestas.contains(10));
        assertTrue("No contiene lo guardado de idE", encuestas.contains(20));
    }

    @Test
    public void testEliminarIdEncuesta() {
        usuario.guardarIdEncuesta(10);
        usuario.guardarIdEncuesta(20);

        
        usuario.eliminarIdEncuesta(20);
        
        Set<Integer> encuestas = usuario.getIdEncuestasCreadas();
        assertEquals("IdE no borrado", 2, encuestas.size());
        assertTrue("IdE borrado incorrecto", encuesta.contains(10));
        assertFalse("IdE no borrado",encuestas.contains(20));
    }
}   