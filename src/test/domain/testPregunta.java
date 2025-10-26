package domain;

import static org.junit.Assert.*;
import org.junit.Test;
import domain.Pregunta;

/**
 * Clase testeadora de Pregunta
 */
 
public class TestPregunta {
    
    private Pregunta pregunta;
    private final int idEncuesta = 3;
	private final int idPregunta = 10;
    private final String enunciado = "test";
    
    /**
     * Configuración inicial antes de cada test.
     */
    @Before
    public void setUp() {
        pregunta = new Pregunta(idEncuesta, idPregunta, enunciado);
    }


	@Test
	public void testConstructoraOk() {
	  	assertEquals(3, pregunta.getIdEncuesta());
	  	assertEquals(10, pregunta.getIdPregunta());
	  	assertEquals("test", pregunta.getEnunciado());
		assertEquals(0, pregunta.getIdRespuestas().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructoraExceptionNull() {
		Pregunta p = new Pregunta(idEncuesta, idPregunta, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructoraExceptionVacia() {
		Pregunta p = new Pregunta(idEncuesta, idPregunta, "");
	}

	@Test
	public void testGetIdEncuesta() {
		assertEquals(3, pregunta.getIdEncuesta());
	}

	@Test
	public void testGetIdPregunta() {
		assertEquals(10, pregunta.getIdPregunta());
	}

	@Test
	public void testGetEnunciado() {
		assertEquals("test", pregunta.getEnunciado());
	}

	@Test
	public void testGetIdRespuestas() {
		assertEquals(0, pregunta.getIdRespuestas().size());
	}

	@Test
	public void testAgregarRespuesta() {
		pregunta.addRespuesta(22);
		pregunta.addRespuesta(23);
		pregunta.addRespuesta(42);
		pregunta.addRespuesta(99);
		ArrayList<Integer> l = pregunta.getIdRespuestas();
		assertTrue(l.contains(22));
		assertTrue(l.contains(23));
		assertTrue(l.contains(42));
		assertTrue(l.contains(99));
	}

	@Test
	public void testBorrarRespuesta() {
		pregunta.addRespuesta(33);
		pregunta.eliminarRespuesta(33);
		assertEquals(0, pregunta.getIdRespuestas().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testBorrarRespuesetaNoExiste() {
		pregunta.eliminarRespuesta(2);
	}

	//Tests de subclases (funciones genéricas)

	@Test
	public void testGetOpciones() {
		assertEquals(0, pregunta.getOpciones().size());
	}

	@Test
	public void testNumMax() {
		assertEquals(-1, pregunta.getNumMaxOpc());
	}

	@Test
	public void testRangoInf() {
		assertEquals(-1, pregunta.getRangoInf());
	}

	@Test
	public void testRangoSup() {
		assertEquals(-1, pregunta.getRangoSup());
	}

	@Test
	public void testOrdenada() {
		assertNull(pregunta.esOrdenada());
	}
}
