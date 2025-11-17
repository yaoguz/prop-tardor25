package domain;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.*;

 /**
 * Pruebas de PreguntaLibre.
 * Comprueba creación, tipo y gestión de ids de respuesta.
 * @author Alessandro Jin
 */

public class TestPreguntaLibre {
    
   	private PreguntaLibre pregunta;
    private final int idEncuesta = 3;
	private final int idPregunta = 10;
    private final String enunciado = "test";
    //private final ArrayList<Integer> idRespuestas = new ArrayList<>(Arrays.asList(1,2,3));
    
	/**
	 * Inicializa la pregunta antes de cada test.
	 */
    @Before
    public void setUp() {
        pregunta = new PreguntaLibre(idEncuesta, idPregunta, enunciado);
        pregunta.guardarIdRespuesta(1);
        pregunta.guardarIdRespuesta(2);
        pregunta.guardarIdRespuesta(3);
    }


	/** Comprueba la constructora y getters. */
	@Test
	public void testConstructoraOk() {
	  	assertEquals("No se guardo idEncuesta bien", idEncuesta, pregunta.getIdEncuesta());
        assertEquals("No se guardo idPregunt bien", idPregunta, pregunta.getIdPregunta());
        assertEquals("No se guardo enunciado correcto", enunciado, pregunta.getEnunciado());
        assertEquals("No se creo bien idRespuesta", 3, pregunta.getIdRespuestas().size());
	}

	/** Enunciado nulo lanza excepción. */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructoraExceptionNull() {
		PreguntaLibre p = new PreguntaLibre(idEncuesta, idPregunta, null);
	}

	/** Enunciado vacío lanza excepción. */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructoraExceptionVacia() {
		PreguntaLibre p = new PreguntaLibre(idEncuesta, idPregunta, "");
	}

	/** Verifica que el tipo es "libre". */
	@Test
	public void testTipo() {
		assertEquals("No devuelve libre", "libre", pregunta.tipoPregunta());
	}

	/** Comprueba guardar id de respuesta. */
	@Test 
	public void TestGuardarIdRespuesta() {
        pregunta.guardarIdRespuesta(4);
		
        assertEquals("No se guardo correcto", Arrays.asList(1,2,3,4), pregunta.getIdRespuestas());
    }

	/** Verifica eliminación correcta de id de respuesta. */
	@Test 
	public void testEliminarIdRespuestaBueno() {
        pregunta.eliminarIdRespuesta(1);

        ArrayList<Integer> res = pregunta.getIdRespuestas();

        assertEquals("No se elimino el correcto", Arrays.asList(2,3), res);
    }

	/** Eliminar id inexistente lanza excepción. */
	@Test(expected = IllegalArgumentException.class) 
	public void testEliminarIdRespuestaExcepcion() {
        pregunta.eliminarIdRespuesta(4);
    }

	@Test
	public void testGetOpciones() {
		/** Comprueba que las opciones están vacías por defecto. */
		ArrayList<String> res = pregunta.getOpciones();

		assertTrue("La lista no es vacia", res.isEmpty());
	}

	/** Comprueba getNumMaxOpc por defecto. */
	@Test
	public void testGetNumMaxOpc () {

		assertEquals("No devuelve -1", -1, pregunta.getNumMaxOpc());
	}

	/** Comprueba getRangoInf por defecto. */
	@Test
	public void testGetRangoInf () {

		assertEquals("No devuelve -1", -1, pregunta.getRangoInf());
	}

	/** Comprueba getRangoSup por defecto. */
	@Test
	public void testGetRangoSup () {

		assertEquals("No devuelve -1", -1, pregunta.getRangoSup());
	}

	/** Verifica que no es ordenada. */
	@Test
	public void testEsOrdenada() {

		assertFalse("No devuelve False", pregunta.esOrdenada());
	}

	/** Comprueba getCantidad por defecto. */
	@Test 
	public void testGetCantidad() {
		assertEquals("No devuelve -1", -1, pregunta.getCantidad());
	}
}
