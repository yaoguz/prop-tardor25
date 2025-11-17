package domain;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import java.util.*;

/**
 * Pruebas de PreguntaNumerica.
 * @author Alessandro Jin
 */
 
public class TestPreguntaNumerica {
    
    private PreguntaNumerica pregunta;
    private final int idEncuesta = 3;
	private final int idPregunta = 10;
    private final String enunciado = "test";
	private final int rangoInf = 18;
	private final int rangoSup = 99;
    
    /**
     * Inicializa la pregunta antes de cada test.
     */
    @Before
    public void setUp() {
        pregunta = new PreguntaNumerica(idEncuesta, idPregunta, enunciado, rangoInf, rangoSup);
        pregunta.guardarIdRespuesta(1);
        pregunta.guardarIdRespuesta(2);
        pregunta.guardarIdRespuesta(3);
    }


	/**
	 * Comprueba la constructora y getters.
	 */
	@Test
	public void testConstructoraOk() {
	  	assertEquals("No se guardo idEncuesta bien", idEncuesta, pregunta.getIdEncuesta());
	  	assertEquals("No se guardo idPregunt bien", idPregunta, pregunta.getIdPregunta());
	  	assertEquals("No se guardo enunciado correcto", enunciado, pregunta.getEnunciado());
		assertEquals("No se creo bien idRespuesta",3, pregunta.getIdRespuestas().size());
		assertEquals("No se guardo rangoInf bien",rangoInf, pregunta.getRangoInf());
		assertEquals("No se guardo rangoSup bien",rangoSup, pregunta.getRangoSup());
	}

	/**
	 * Enunciado nulo lanza excepción.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructoraExceptionNull() {
		PreguntaNumerica p = new PreguntaNumerica(idEncuesta, idPregunta, null, rangoInf, rangoSup);
	}

	/**
	 * Enunciado vacío lanza excepción.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructoraExceptionVacia() {
		PreguntaNumerica p = new PreguntaNumerica(idEncuesta, idPregunta, "", rangoInf, rangoSup);
	}

	/**
	 * Verifica getRangoInf.
	 */
	@Test
	public void testGetRangoInf() {
		assertEquals("El rangoInf no coincide", rangoInf, pregunta.getRangoInf());
	}

	/**
	 * Verifica getRangoSup.
	 */
	@Test
	public void testGetRangoSup() {
		assertEquals("El rangoSup no coincide", rangoSup, pregunta.getRangoSup());
	}

	/**
	 * Verifica que el tipo es "numerica".
	 */
	@Test
	public void testTipo() {
		assertEquals("No devuelve numerica", "numerica", pregunta.tipoPregunta());
	}

	/**
	 * Valor fuera del rango inferior lanza excepción.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCrearRespuestaRangoInf() {
		pregunta.crearRespuesta(2, "Alessandro", 1.0);
	}

	/**
	 * Valor fuera del rango superior lanza excepción.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testCrearRespuestaRangoSup() {
		pregunta.crearRespuesta(2, "Alessadro", 100.0);
	}

	/**
	 * Comprueba que se guarda el id de respuesta.
	 */
	@Test 
    public void TestGuardarIdRespuesta() {
        pregunta.guardarIdRespuesta(4);
		
        assertEquals("No se guardo correcto", Arrays.asList(1,2,3,4), pregunta.getIdRespuestas());
    }

    /**
     * Verifica que se elimina correctamente un id de respuesta.
     */
    @Test 
    public void testEliminarIdRespuestaBueno() {
        pregunta.eliminarIdRespuesta(1);

        ArrayList<Integer> res = pregunta.getIdRespuestas();

        assertEquals("No se elimino el correcto", Arrays.asList(2,3), res);
    }

    /**
     * Eliminar id inexistente lanza excepción.
     */
    @Test(expected = IllegalArgumentException.class) 
    public void testEliminarIdRespuestaExcepcion() {
        pregunta.eliminarIdRespuesta(4);
    }

	/**
	 * Verifica que la lista de opciones está vacía.
	 */
	@Test
	public void testGetOpciones() {
		ArrayList<String> res = pregunta.getOpciones();

		assertTrue("La lista no es vacia", res.isEmpty());
	}


	/**
	 * Comprueba que getNumMaxOpc retorna -1.
	 */
	@Test
	public void testGetNumMaxOpc () {

		assertEquals("No devuelve -1", -1, pregunta.getNumMaxOpc());
	}

	/**
	 * Verifica que esOrdenada retorna false.
	 */
	@Test
	public void testEsOrdenada() {

		assertFalse("No devuelve False", pregunta.esOrdenada());
	}

	/**
	 * Comprueba que getCantidad retorna -1.
	 */
	@Test 
	public void testGetCantidad() {
		assertEquals("No devuelve -1", -1, pregunta.getCantidad());
	}

}
