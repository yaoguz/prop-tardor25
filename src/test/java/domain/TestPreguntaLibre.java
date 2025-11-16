package domain;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

 /**
 * @author Alessandro Jin
 */

public class TestPreguntaLibre {
    
   	private PreguntaLibre pregunta;
    private final int idEncuesta = 3;
	private final int idPregunta = 10;
    private final String enunciado = "test";
    //private final ArrayList<Integer> idRespuestas = new ArrayList<>(Arrays.asList(1,2,3));
    
    /**
     * Configuración inicial antes de cada test.
     */
    @Before
    public void setUp() {
        pregunta = new PreguntaLibre(idEncuesta, idPregunta, enunciado);
        pregunta.guardarIdRespuesta(1);
        pregunta.guardarIdRespuesta(2);
        pregunta.guardarIdRespuesta(3);
    }


	@Test
	public void testConstructoraOk() {
	  	assertEquals("No se guardo idEncuesta bien", idEncuesta, pregunta.getIdEncuesta());
        assertEquals("No se guardo idPregunt bien", idPregunta, pregunta.getIdPregunta());
        assertEquals("No se guardo enunciado correcto", enunciado, pregunta.getEnunciado());
        assertEquals("No se creo bien idRespuesta", 3, pregunta.getIdRespuestas().size());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructoraExceptionNull() {
		PreguntaLibre p = new PreguntaLibre(idEncuesta, idPregunta, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructoraExceptionVacia() {
		PreguntaLibre p = new PreguntaLibre(idEncuesta, idPregunta, "");
	}

	@Test
	public void testTipo() {
		assertEquals("No devuelve libre", "libre", pregunta.tipoPregunta());
	}

	@Test 
    public void TestGuardarIdRespuesta() {
        pregunta.guardarIdRespuesta(4);
		
        assertEquals("No se guardo correcto", Arrays.asList(1,2,3,4), pregunta.getIdRespuestas());
    }

    @Test 
    public void testEliminarIdRespuestaBueno() {
        pregunta.eliminarIdRespuesta(1);

        ArrayList<Integer> res = pregunta.getIdRespuestas();

        assertEquals("No se elimino el correcto", Arrays.asList(2,3), res);
    }

    @Test(expected = IllegalArgumentException.class) 
    public void testEliminarIdRespuestaExcepcion() {
        pregunta.eliminarIdRespuesta(4);
    }

	@Test
	public void testGetOpciones() {
		ArrayList<String> res = pregunta.getOpciones();

		assertTrue("La lista no es vacia", res.isEmpty());
	}

	@Test
	public void testGetNumMaxOpc () {

		assertEquals("No devuelve -1", -1, pregunta.getNumMaxOpc());
	}

	@Test
	public void testGetRangoInf () {

		assertEquals("No devuelve -1", -1, pregunta.getRangoInf());
	}

	@Test
	public void testGetRangoSup () {

		assertEquals("No devuelve -1", -1, pregunta.getRangoSup());
	}

	@Test
	public void testEsOrdenada() {

		assertFalse("No devuelve False", pregunta.esOrdenada());
	}

	@Test 
	public void testGetCantidad() {
		assertEquals("No devuelve -1", -1, pregunta.getCantidad());
	}
}
