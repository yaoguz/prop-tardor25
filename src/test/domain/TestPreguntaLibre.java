package domain;

import static org.junit.Assert.*;
import org.junit.Test;
import domain.Pregunta;

/**
 * Clase testeadora de PreguntaLibre
 */
 
public class TestPreguntaLibre {
    
    private PreguntaLibre pregunta;
    private final int idEncuesta = 3;
	private final int idPregunta = 10;
    private final String enunciado = "test";
    
    /**
     * Configuración inicial antes de cada test.
     */
    @Before
    public void setUp() {
        pregunta = new PreguntaLibre(idEncuesta, idPregunta, enunciado);
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
	public void testTipo() {
		assertEquals("Libre", pregunta.tipoPregunta());
	}
}
