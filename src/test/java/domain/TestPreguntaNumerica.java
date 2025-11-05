package domain;

import static org.junit.Assert.*;
import org.junit.Test;
import domain.Pregunta;

/**
 * Clase testeadora de PreguntaNumerica
 */
 
public class TestPreguntaNumerica {
    
    private PreguntaNumerica pregunta;
    private final int idEncuesta = 3;
	private final int idPregunta = 10;
    private final String enunciado = "test";
	private final int rangoInf = 18;
	private final int rangoSup = 99;
    
    /**
     * Configuración inicial antes de cada test.
     */
    @Before
    public void setUp() {
        pregunta = new PreguntaNumerica(idEncuesta, idPregunta, enunciado, rangoInf, rangoSup);
    }


	@Test
	public void testConstructoraOk() {
	  	assertEquals(3, pregunta.getIdEncuesta());
	  	assertEquals(10, pregunta.getIdPregunta());
	  	assertEquals("test", pregunta.getEnunciado());
		assertEquals(0, pregunta.getIdRespuestas().size());
		assertEquals(18, pregunta.getRangoInf());
		assertEquals(99, pregunta.getRangoSup());
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
	public void testGetRangoInf() {
		assertEquals(18, pregunta.getRangoInf());
	}

	@Test
	public void testGetRangoSup() {
		assertEquals(99, pregunta.getRangoSup());
	}

	@Test
	public void testTipo() {
		assertEquals("Numerica", pregunta.tipoPregunta());
	}
}
