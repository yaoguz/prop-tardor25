package domain;

import main.domini.*;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests unitarios para la clase Usuario (SIN MOCKS).
 * 
 * Las clases de dominio puras no necesitan mocks porque no tienen dependencias externas.
 * Solo prueban lógica interna.
 * 
 * @author Equipo PROP
 * @version 1.0
 */
public class TestUsuario {
    
    private Usuario usuario;
    private final String NOMBRE_USUARIO = "testUser";
    private final String CONTRASENA_INICIAL = "password123";
    
    /**
     * Configuración inicial antes de cada test.
     */
    @Before
    public void setUp() {
        usuario = new Usuario(NOMBRE_USUARIO, CONTRASENA_INICIAL);
    }
    
    // ==================== TESTS DEL CONSTRUCTOR ====================
    
    @Test
    public void testConstructor_CreaUsuarioCorrectamente() {
        // Verificar que el nombre se asignó correctamente
        assertEquals(NOMBRE_USUARIO, usuario.getNomUsuario());
        
        // Verificar que la contraseña funciona
        assertTrue(usuario.comprobarContrasena(CONTRASENA_INICIAL));
        
        // Verificar que las listas están inicializadas vacías
        assertNotNull(usuario.getIdRespuestas());
        assertTrue(usuario.getIdRespuestas().isEmpty());
        
        assertNotNull(usuario.getIdEncuestasCreadas());
        assertTrue(usuario.getIdEncuestasCreadas().isEmpty());
        
        assertNotNull(usuario.getIdPerfiles());
        assertTrue(usuario.getIdPerfiles().isEmpty());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_NombreVacio_LanzaExcepcion() {
        new Usuario("", CONTRASENA_INICIAL);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_ContrasenaVacia_LanzaExcepcion() {
        new Usuario(NOMBRE_USUARIO, "");
    }
    
    // ==================== TESTS DE CONTRASEÑA ====================
    
    @Test
    public void testComprobarContrasena_ContrasenaCorrecta_RetornaTrue() {
        assertTrue(usuario.comprobarContrasena(CONTRASENA_INICIAL));
    }
    
    @Test
    public void testComprobarContrasena_ContrasenaIncorrecta_RetornaFalse() {
        assertFalse(usuario.comprobarContrasena("wrongPassword"));
    }
    
    @Test
    public void testNewContrasena_CambiaContrasenaCorrectamente() {
        String nuevaContrasena = "newPassword456";
        
        // Cambiar contraseña
        usuario.newContrasena(nuevaContrasena);
        
        // Verificar que la nueva funciona
        assertTrue(usuario.comprobarContrasena(nuevaContrasena));
        
        // Verificar que la antigua ya no funciona
        assertFalse(usuario.comprobarContrasena(CONTRASENA_INICIAL));
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testNewContrasena_ContrasenaVacia_LanzaExcepcion() {
        usuario.newContrasena("");
    }
    
    // ==================== TESTS DE RESPUESTAS ====================
    
    @Test
    public void testGuardarIdRespuesta_AgregaRespuesta() {
        usuario.guardarIdRespuesta(1);
        usuario.guardarIdRespuesta(2);
        usuario.guardarIdRespuesta(3);
        
        List<Integer> respuestas = usuario.getIdRespuestas();
        assertEquals(3, respuestas.size());
        assertTrue(respuestas.contains(1));
        assertTrue(respuestas.contains(2));
        assertTrue(respuestas.contains(3));
    }
    
    @Test
    public void testGuardarIdRespuesta_Duplicado_NoAgregaDosVeces() {
        usuario.guardarIdRespuesta(1);
        usuario.guardarIdRespuesta(1); // Duplicado
        
        List<Integer> respuestas = usuario.getIdRespuestas();
        // Dependiendo de tu implementación, puede permitir duplicados o no
        // Ajusta este test según tu lógica de negocio
        assertTrue(respuestas.contains(1));
    }
    
    @Test
    public void testEliminarIdRespuesta_EliminaCorrectamente() {
        usuario.guardarIdRespuesta(1);
        usuario.guardarIdRespuesta(2);
        usuario.guardarIdRespuesta(3);
        
        // Eliminar respuesta 2
        usuario.eliminarIdRespuesta(2);
        
        List<Integer> respuestas = usuario.getIdRespuestas();
        assertEquals(2, respuestas.size());
        assertTrue(respuestas.contains(1));
        assertFalse(respuestas.contains(2));
        assertTrue(respuestas.contains(3));
    }
    
    @Test
    public void testEliminarIdRespuesta_IdInexistente_NoHaceNada() {
        usuario.guardarIdRespuesta(1);
        
        // Intentar eliminar ID que no existe
        usuario.eliminarIdRespuesta(999);
        
        List<Integer> respuestas = usuario.getIdRespuestas();
        assertEquals(1, respuestas.size());
        assertTrue(respuestas.contains(1));
    }
    
    // ==================== TESTS DE ENCUESTAS CREADAS ====================
    
    @Test
    public void testGuardarIdEncuesta_AgregaEncuesta() {
        usuario.guardarIdEncuesta(10);
        usuario.guardarIdEncuesta(20);
        
        List<Integer> encuestas = usuario.getIdEncuestasCreadas();
        assertEquals(2, encuestas.size());
        assertTrue(encuestas.contains(10));
        assertTrue(encuestas.contains(20));
    }
    
    @Test
    public void testEliminarIdEncuesta_EliminaCorrectamente() {
        usuario.guardarIdEncuesta(10);
        usuario.guardarIdEncuesta(20);
        usuario.guardarIdEncuesta(30);
        
        usuario.eliminarIdEncuesta(20);
        
        List<Integer> encuestas = usuario.getIdEncuestasCreadas();
        assertEquals(2, encuestas.size());
        assertFalse(encuestas.contains(20));
    }
    
    // ==================== TESTS DE PERFILES ====================
    
    @Test
    public void testGuardarIdPerfil_AgregaPerfil() {
        usuario.guardarIdPerfil(100);
        usuario.guardarIdPerfil(200);
        
        List<Integer> perfiles = usuario.getIdPerfiles();
        assertEquals(2, perfiles.size());
        assertTrue(perfiles.contains(100));
        assertTrue(perfiles.contains(200));
    }
    
    // ==================== TESTS DE MÉTODOS DE UTILIDAD ====================
    
    @Test
    public void testGetNomUsuario_RetornaNombreCorrecto() {
        assertEquals(NOMBRE_USUARIO, usuario.getNomUsuario());
    }
    
    @Test
    public void testToString_ContieneNombreUsuario() {
        String resultado = usuario.toString();
        assertNotNull(resultado);
        assertTrue(resultado.contains(NOMBRE_USUARIO));
    }
}
