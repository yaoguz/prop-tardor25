package domain;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import java.util.*;

/**
 * Pruebas de PreguntaOpcionMultiple.
 * @author Alessandro Jin
 */
 
public class TestPreguntaOpcionMultiple {
    
    private PreguntaOpcionMultiple pregunta;
    private final int idEncuesta = 3;
	private final int idPregunta = 10;
    private final String enunciado = "test";
	private final int numMaxOpc = 3;
    
    /**
     * Inicializa la pregunta antes de cada test.
     */
    @Before
    public void setUp() {
        pregunta = new PreguntaOpcionMultiple(idEncuesta, idPregunta, enunciado);
        pregunta.guardarIdRespuesta(1);
        pregunta.guardarIdRespuesta(2);
        pregunta.guardarIdRespuesta(3);
    }


	/**
	 * Comprueba la constructora y getters.
	 */
	@Test
	public void testConstructora() {
	  	assertEquals("No se guardo idEncuesta bien", idEncuesta, pregunta.getIdEncuesta());
        assertEquals("No se guardo idPregunt bien", idPregunta, pregunta.getIdPregunta());
        assertEquals("No se guardo enunciado correcto", enunciado, pregunta.getEnunciado());
        assertEquals("No se creo bien idRespuesta", 3, pregunta.getIdRespuestas().size());
		assertEquals("EL numMaxopc no esta bien", 2, pregunta.getNumMaxOpc());
	}

	/**
	 * Enunciado nulo lanza excepción.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructoraExceptionNull() {
		PreguntaOpcionMultiple p = new PreguntaOpcionMultiple(idEncuesta, idPregunta, null);
	}

	/**
	 * Enunciado vacío lanza excepción.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testConstructoraExceptionVacia() {
		PreguntaOpcionMultiple p = new PreguntaOpcionMultiple(idEncuesta, idPregunta, "");
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
	 * Opción nula lanza excepción.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddOpcionNulo() {
		pregunta.addOpcion(null);
	}

	/**
	 * Opción vacía lanza excepción.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testAddOpcionVacio() {
		pregunta.addOpcion("");
	}

	/**
	 * Verifica getNumMaxOpc.
	 */
	@Test
	public void testGetNumMaxOpc() {
		assertEquals(2, pregunta.getNumMaxOpc());
	}

	/**
	 * Verifica setNumMaxOpc.
	 */
	@Test
	public void testSetNumMaxOpc() {
		pregunta.addOpcion("A");
		pregunta.addOpcion("B");
		pregunta.addOpcion("C");
		pregunta.setNumMaxOpc(numMaxOpc);
		assertEquals("No se setea numMaxOpc", numMaxOpc, pregunta.getNumMaxOpc());
	}

	/**
	 * setNumMaxOpc menor a 2 lanza excepción.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetNumMaxOpcMenor() {
		pregunta.setNumMaxOpc(1);
	}

	/**
	 * setNumMaxOpc mayor al número de opciones lanza excepción.
	 */
	@Test(expected = IllegalArgumentException.class)
	public void testSetNumMaxOpcMayor() {
		pregunta.addOpcion("hola");
		pregunta.addOpcion("adios");
		pregunta.addOpcion("hasta luego");
		pregunta.addOpcion("hasta manana");
		pregunta.addOpcion("monkey");
		pregunta.setNumMaxOpc(6);
	}

	/**
	 * setNumMaxOpc con valor válido funciona correctamente.
	 */
	@Test
	public void testSetNumMaxOpcAdd() {
		pregunta.addOpcion("hola");
		pregunta.addOpcion("adios");
		pregunta.addOpcion("hasta luego");
		pregunta.addOpcion("hasta manana");
		pregunta.addOpcion("monkey");
		pregunta.setNumMaxOpc(4);
		assertEquals("No se setea numMaxOpc", 4, pregunta.getNumMaxOpc());
	}

	/**
	 * Verifica que el tipo es "opcion_multiple".
	 */
	@Test
	public void testTipo() {
		assertEquals("Deberia ser opcion multiple", "opcion_multiple", pregunta.tipoPregunta());
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
