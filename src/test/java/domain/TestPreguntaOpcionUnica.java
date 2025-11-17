package domain;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import java.util.*;

/**
 * Pruebas de PreguntaOpcionUnica.
 * @author Alessandro Jin
 */

public class TestPreguntaOpcionUnica {
    
   	private PreguntaOpcionUnica pregunta;
    private PreguntaOpcionUnica preguntaOrdenada;
    private final int idEncuesta = 3;
	private final int idPregunta = 10;
    private final String enunciado = "test";
    //private final ArrayList<Integer> idRespuestas = new ArrayList<>(Arrays.asList(1,2,3));
    
    /**
     * Inicializa la pregunta antes de cada test.
     */
    @Before
    public void setUp() {
        pregunta = new PreguntaOpcionUnica(idEncuesta, idPregunta, enunciado, false);
        preguntaOrdenada = new PreguntaOpcionUnica(idEncuesta, idPregunta, enunciado, true);
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
        assertEquals("No se creo bien idRespuesta", 3, pregunta.getIdRespuestas().size());
        assertFalse("No es No ordenada", pregunta.esOrdenada());
        assertTrue("Deberia ser ordenada", preguntaOrdenada.esOrdenada());
	}

	/**
	 * Enunciado nulo lanza excepción.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructoraExceptionNull() {
		PreguntaOpcionUnica p = new PreguntaOpcionUnica(idEncuesta, idPregunta, null, false);
	}

	/**
	 * Enunciado vacío lanza excepción.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructoraExceptionVacia() {
		PreguntaOpcionUnica p = new PreguntaOpcionUnica(idEncuesta, idPregunta, "", false);
	}

	/**
	 * Verifica que el tipo es "opcion_unica".
	 */
	@Test
	public void testTipo() {
		assertEquals("No devuelve Opcion unica", "opcion_unica", pregunta.tipoPregunta());
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
     * Comprueba que se retornan las opciones.
     */
    @Test
	public void testGetOpciones() {
		pregunta.addOpcion("Hola");
		pregunta.addOpcion("Adios");
		assertEquals("Deberia estar vacio", 2, pregunta.getOpciones().size());
	}

	/**
	 * Verifica que se añade opción correctamente.
	 */
	@Test
	public void testAddOpcion() {
		pregunta.addOpcion("Hola");
		pregunta.addOpcion("Adios");
		assertEquals("No es igual", Arrays.asList("Hola", "Adios"), pregunta.getOpciones());
	}

	/**
	 * Comprueba que getNumMaxOpc retorna -1.
	 */
	@Test
	public void testGetNumMaxOpc () {

		assertEquals("No devuelve -1", -1, pregunta.getNumMaxOpc());
	}

	/**
	 * Comprueba que getRangoInf retorna -1.
	 */
	@Test
	public void testGetRangoInf () {

		assertEquals("No devuelve -1", -1, pregunta.getRangoInf());
	}

	/**
	 * Comprueba que getRangoSup retorna -1.
	 */
	@Test
	public void testGetRangoSup () {

		assertEquals("No devuelve -1", -1, pregunta.getRangoSup());
	}

	/**
	 * Verifica que esOrdenada retorna false.
	 */
	@Test
	public void testEsOrdenada() {

		assertFalse("No devuelve False", pregunta.esOrdenada());
	}

	/**
	 * Verifica que getCantidad retorna el número correcto de opciones.
	 */
	@Test 
	public void testGetCantidad() {
		pregunta.addOpcion("hola");
		pregunta.addOpcion("adios");
		pregunta.addOpcion("hasta luego");

		assertEquals("No devuelve cantidad bien", 3, pregunta.getCantidad());
	}
}
