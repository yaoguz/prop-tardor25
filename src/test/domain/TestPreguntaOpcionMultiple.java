package domain;

import static org.junit.Assert.*;
import org.junit.Test;
import domain.Pregunta;

/**
 * Clase testeadora de PreguntaOpcionMultiple
 */
 
public class TestPreguntaOpcionMultiple {
    
    private PreguntaOpcionMultiple pregunta;
    private final int idEncuesta = 3;
	private final int idPregunta = 10;
    private final String enunciado = "test";
	private final int numMaxOpc = 3;
    
    /**
     * Configuración inicial antes de cada test.
     */
    @Before
    public void setUp() {
        pregunta = new PreguntaNumerica(idEncuesta, idPregunta, enunciado);
    }


	@Test
	public void testConstructoraOk() {
	  	assertEquals(3, pregunta.getIdEncuesta());
	  	assertEquals(10, pregunta.getIdPregunta());
	  	assertEquals("test", pregunta.getEnunciado());
		assertEquals(0, pregunta.getIdRespuestas().size());
		assertEquals(2, pregunta.getNumMaxOpc());
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
	public void testGetNumMaxOpc() {
		assertEquals(2, pregunta.getNumMaxOpc());
	}

	@Test
	public void testSetNumMaxOpc() {
		pregunta.setNumMaxOpc(numMaxOpc);
		assertEquals(numMaxOpc, pregunta.setNumMaxOpc());
	}

	@Test
	public void testGetOpciones() {
		assertEquals(0, pregunta.getOpciones().size());
	}

	@Test
	public void testAgregarOpcione() {
		pregunta.addOpcion("opcion1");
		pregunta.addOpcion("opcion2");
		ArrayList<String> l = pregunta.getOpciones();
		assertTrue(l.contains("opcion1"));
		assertTrue(l.contains("opcion2"));
	}

	@Test
	public void testTipo() {
		assertEquals("OpcionMultiple", pregunta.tipoPregunta());
	}
}
