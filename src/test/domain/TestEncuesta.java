package domain;

import main.domini.*;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests unitarios para la clase Encuesta (SIN MOCKS) - JUnit 4.
 * 
 * Las clases de dominio puras no necesitan mocks porque no tienen dependencias externas.
 * Solo prueban lógica interna.
 * 
 * @author Equipo PROP
 * @version 1.0 - JUnit 4
 */
public class TestEncuesta {
    
    private Encuesta encuesta;
    private final int ID_ENCUESTA = 1;
    private final String NOMBRE_CREADOR = "testUser";
    private final String TITULO_ENCUESTA = "Encuesta de Prueba";
    
    /**
     * Configuración inicial antes de cada test.
     */
    @Before
    public void setUp() {
        encuesta = new Encuesta(ID_ENCUESTA, NOMBRE_CREADOR, TITULO_ENCUESTA);
    }
    
    // ==================== TESTS DEL CONSTRUCTOR ====================
    
    @Test
    public void testConstructor_CreaEncuestaCorrectamente() {
        // Verificar que el ID se asignó correctamente
        assertEquals(ID_ENCUESTA, encuesta.getId());
        
        // Verificar que el creador se asignó correctamente
        assertEquals(NOMBRE_CREADOR, encuesta.getNombreCreador());
        
        // Verificar que el título se asignó correctamente
        assertEquals(TITULO_ENCUESTA, encuesta.getTitulo());
        
        // Verificar que las listas están inicializadas vacías
        assertNotNull(encuesta.getIdPreguntas());
        assertTrue(encuesta.getIdPreguntas().isEmpty());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_IdNegativo_LanzaExcepcion() {
        new Encuesta(-1, NOMBRE_CREADOR, TITULO_ENCUESTA);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_NombreCreadorVacio_LanzaExcepcion() {
        new Encuesta(ID_ENCUESTA, "", TITULO_ENCUESTA);
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_TituloVacio_LanzaExcepcion() {
        new Encuesta(ID_ENCUESTA, NOMBRE_CREADOR, "");
    }
    
    // ==================== TESTS DE PREGUNTAS ====================
    
    @Test
    public void testGuardarIdPregunta_AgregaPregunta() {
        encuesta.guardarIdPregunta(1);
        encuesta.guardarIdPregunta(2);
        encuesta.guardarIdPregunta(3);
        
        List<Integer> preguntas = encuesta.getIdPreguntas();
        assertEquals(3, preguntas.size());
        assertTrue(preguntas.contains(1));
        assertTrue(preguntas.contains(2));
        assertTrue(preguntas.contains(3));
    }
    
    @Test
    public void testGuardarIdPregunta_MantieneOrden() {
        encuesta.guardarIdPregunta(10);
        encuesta.guardarIdPregunta(20);
        encuesta.guardarIdPregunta(30);
        
        List<Integer> preguntas = encuesta.getIdPreguntas();
        assertEquals(Integer.valueOf(10), preguntas.get(0));
        assertEquals(Integer.valueOf(20), preguntas.get(1));
        assertEquals(Integer.valueOf(30), preguntas.get(2));
    }
    
    @Test
    public void testEliminarIdPregunta_EliminaCorrectamente() {
        encuesta.guardarIdPregunta(1);
        encuesta.guardarIdPregunta(2);
        encuesta.guardarIdPregunta(3);
        
        // Eliminar pregunta 2
        encuesta.eliminarIdPregunta(2);
        
        List<Integer> preguntas = encuesta.getIdPreguntas();
        assertEquals(2, preguntas.size());
        assertTrue(preguntas.contains(1));
        assertFalse(preguntas.contains(2));
        assertTrue(preguntas.contains(3));
    }
    
    @Test
    public void testEliminarIdPregunta_IdInexistente_NoHaceNada() {
        encuesta.guardarIdPregunta(1);
        
        // Intentar eliminar ID que no existe
        encuesta.eliminarIdPregunta(999);
        
        List<Integer> preguntas = encuesta.getIdPreguntas();
        assertEquals(1, preguntas.size());
        assertTrue(preguntas.contains(1));
    }
    
    // ==================== TESTS DE TÍTULO ====================
    
    @Test
    public void testModificarTitulo_CambiaTituloCorrectamente() {
        String nuevoTitulo = "Nuevo Título de Encuesta";
        
        encuesta.modificarTitulo(nuevoTitulo);
        
        assertEquals(nuevoTitulo, encuesta.getTitulo());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testModificarTitulo_TituloVacio_LanzaExcepcion() {
        encuesta.modificarTitulo("");
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testModificarTitulo_TituloNull_LanzaExcepcion() {
        encuesta.modificarTitulo(null);
    }
    
    // ==================== TESTS DE GETTERS ====================
    
    @Test
    public void testGetId_RetornaIdCorrecto() {
        assertEquals(ID_ENCUESTA, encuesta.getId());
    }
    
    @Test
    public void testGetNombreCreador_RetornaNombreCorrecto() {
        assertEquals(NOMBRE_CREADOR, encuesta.getNombreCreador());
    }
    
    @Test
    public void testGetTitulo_RetornaTituloCorrecto() {
        assertEquals(TITULO_ENCUESTA, encuesta.getTitulo());
    }
    
    @Test
    public void testGetIdPreguntas_RetornaListaNoNull() {
        assertNotNull(encuesta.getIdPreguntas());
    }
    
    // ==================== TESTS DE OPERACIONES MÚLTIPLES ====================
    
    @Test
    public void testAgregarYEliminarMultiplesPreguntas() {
        // Agregar varias preguntas
        for (int i = 1; i <= 10; i++) {
            encuesta.guardarIdPregunta(i);
        }
        
        assertEquals(10, encuesta.getIdPreguntas().size());
        
        // Eliminar preguntas pares
        for (int i = 2; i <= 10; i += 2) {
            encuesta.eliminarIdPregunta(i);
        }
        
        // Deberían quedar solo las impares
        List<Integer> preguntas = encuesta.getIdPreguntas();
        assertEquals(5, preguntas.size());
        assertTrue(preguntas.contains(1));
        assertTrue(preguntas.contains(3));
        assertTrue(preguntas.contains(5));
        assertTrue(preguntas.contains(7));
        assertTrue(preguntas.contains(9));
    }
    
    // ==================== TESTS DE CASOS EXTREMOS ====================
    
    @Test
    public void testEncuestaVacia_NoTienePreguntas() {
        assertTrue(encuesta.getIdPreguntas().isEmpty());
        assertEquals(0, encuesta.getIdPreguntas().size());
    }
    
    @Test
    public void testToString_ContieneInformacionBasica() {
        String resultado = encuesta.toString();
        assertNotNull(resultado);
        assertTrue("Debería contener el ID", resultado.contains(String.valueOf(ID_ENCUESTA)));
        assertTrue("Debería contener el título", resultado.contains(TITULO_ENCUESTA));
    }
}
