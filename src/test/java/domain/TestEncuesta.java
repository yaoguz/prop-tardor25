package domain;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

/**
 * Tests unitarios para la clase Encuesta.
 * 
 * Las clases de dominio puras no necesitan mocks porque no tienen dependencias externas.
 * Solo prueban lógica interna.
 * 
 * @author Peiyu Wang
 */
public class TestEncuesta {
    
    private Encuesta encuesta;
    private final int ID_ENCUESTA = 1;
    private final String NOMBRE_CREADOR = "testUser";
    private final String TITULO_ENCUESTA = "Encuesta de Prueba";
    
    /**
     * Inicializa la encuesta antes de cada test.
     */
    @Before
    public void setUp() {
        encuesta = new Encuesta(ID_ENCUESTA, TITULO_ENCUESTA, NOMBRE_CREADOR);
    }
    
    /**
     * Comprueba que la constructora y los getters funcionan.
     */
    @Test
    public void testConstructor_CreaEncuestaCorrectamente() {
        // Verificar ID asignado correctamente
        assertEquals(ID_ENCUESTA, encuesta.getIdEncuesta());
        
        // Verificar creador asignado correctamente
        assertEquals(NOMBRE_CREADOR, encuesta.getIdCreador());
        
        // Verificar titulo asignado correctamente
        assertEquals(TITULO_ENCUESTA, encuesta.getTitulo());
        
        // Verificar listas vacias
        assertNotNull(encuesta.getIdPreguntas());
        assertTrue(encuesta.getIdPreguntas().isEmpty());
    }
    
    /**
     * Verifica que se guardan correctamente múltiples preguntas.
     */
    @Test
    public void testGuardarPregunta_AgregaCorrectamente() {
        encuesta.guardarPregunta(10);
        encuesta.guardarPregunta(20);
        encuesta.guardarPregunta(30);

        ArrayList<Integer> lista = encuesta.getIdPreguntas();
        assertEquals(3, lista.size());
        assertTrue(lista.contains(Integer.valueOf(10)));
        assertTrue(lista.contains(Integer.valueOf(20)));
        assertTrue(lista.contains(Integer.valueOf(30)));
    }
    
    /**
     * Verifica que se elimina una pregunta correctamente.
     */
    @Test
    public void testEliminarPregunta_EliminaCorrectamente() {
        encuesta.guardarPregunta(1);
        encuesta.guardarPregunta(2);
        encuesta.guardarPregunta(3);

        encuesta.eliminarPregunta(2);

        ArrayList<Integer> lista = encuesta.getIdPreguntas();
        assertEquals(2, lista.size());
        assertTrue(lista.contains(Integer.valueOf(1)));
        assertFalse(lista.contains(Integer.valueOf(2)));
        assertTrue(lista.contains(Integer.valueOf(3)));
    }
    
    /**
     * Comprueba que eliminar una pregunta inexistente no causa error.
     */
    @Test
    public void testEliminarPregunta_SiNoExiste_NoFalla() {
        encuesta.guardarPregunta(5);
        encuesta.eliminarPregunta(999); // no existe
        ArrayList<Integer> lista = encuesta.getIdPreguntas();
        assertEquals(1, lista.size());
        assertTrue(lista.contains(Integer.valueOf(5)));
        assertFalse(lista.contains(Integer.valueOf(999)));
    }
    
    /**
     * Verifica que se cambia el título correctamente.
     */
    @Test
    public void testCambiarTitulo() {
        String nuevo = "Nuevo título";
        encuesta.cambiarTitulo(nuevo);
        assertEquals(nuevo, encuesta.getTitulo());
    }
    
    /**
     * Prueba agregar y eliminar múltiples preguntas en conjunto.
     */
    @Test
    public void testAgregarYEliminarMultiplesPreguntas() {
        // Agregar varias preguntas
        for (int i = 1; i <= 10; i++) {
            encuesta.guardarPregunta(i);
        }
        
        assertEquals(10, encuesta.getIdPreguntas().size());
        
        // Eliminar preguntas pares
        for (int i = 2; i <= 10; i += 2) {
            encuesta.eliminarPregunta(i);
        }
        
        // Deberian quedar solo las impares
        ArrayList<Integer> preguntas = encuesta.getIdPreguntas();
        assertEquals(5, preguntas.size());
        assertTrue(preguntas.contains(1));
        assertTrue(preguntas.contains(3));
        assertTrue(preguntas.contains(5));
        assertTrue(preguntas.contains(7));
        assertTrue(preguntas.contains(9));
    }
    
    
}
