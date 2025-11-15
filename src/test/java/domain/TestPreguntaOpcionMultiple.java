package domain;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import java.util.*;

 /**
 * @author Alessandro Jin
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
        pregunta = new PreguntaOpcionMultiple(idEncuesta, idPregunta, enunciado);
        pregunta.guardarIdRespuesta(1);
        pregunta.guardarIdRespuesta(2);
        pregunta.guardarIdRespuesta(3);
    }


	@Test
	public void testConstructora() {
	  	assertEquals("No se guardo idEncuesta bien", idEncuesta, pregunta.getIdEncuesta());
        assertEquals("No se guardo idPregunt bien", idPregunta, pregunta.getIdPregunta());
        assertEquals("No se guardo enunciado correcto", enunciado, pregunta.getEnunciado());
        assertEquals("No se creo bien idRespuesta", 3, pregunta.getIdRespuestas().size());
		assertEquals("EL numMaxopc no esta bien", 2, pregunta.getNumMaxOpc());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructoraExceptionNull() {
		PreguntaOpcionMultiple p = new PreguntaOpcionMultiple(idEncuesta, idPregunta, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testConstructoraExceptionVacia() {
		PreguntaOpcionMultiple p = new PreguntaOpcionMultiple(idEncuesta, idPregunta, "");
	}

	@Test
	public void testGetOpciones() {
		pregunta.addOpcion("Hola");
		pregunta.addOpcion("Adios");
		assertEquals("Deberia estar vacio", 2, pregunta.getOpciones().size());
	}

	@Test
	public void testAddOpcion() {
		pregunta.addOpcion("Hola");
		pregunta.addOpcion("Adios");
		assertEquals("No es igual", Arrays.asList("Hola", "Adios"), pregunta.getOpciones());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddOpcionNulo() {
		pregunta.addOpcion(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testAddOpcionVacio() {
		pregunta.addOpcion("");
	}

	@Test
	public void testGetNumMaxOpc() {
		assertEquals(2, pregunta.getNumMaxOpc());
	}

	@Test
	public void testSetNumMaxOpc() {
		pregunta.addOpcion("A");
		pregunta.addOpcion("B");
		pregunta.addOpcion("C");
		pregunta.setNumMaxOpc(numMaxOpc);
		assertEquals("No se setea numMaxOpc", numMaxOpc, pregunta.getNumMaxOpc());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetNumMaxOpcMenor() {
		pregunta.setNumMaxOpc(1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testSetNumMaxOpcMayor() {
		pregunta.addOpcion("hola");
		pregunta.addOpcion("adios");
		pregunta.addOpcion("hasta luego");
		pregunta.addOpcion("hasta manana");
		pregunta.addOpcion("monkey");
		pregunta.setNumMaxOpc(6);
	}

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

	@Test
	public void testTipo() {
		assertEquals("Deberia ser opcion multiple", "opcion_multiple", pregunta.tipoPregunta());
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
	public void testGetRangoInf () {

		assertEquals("No devuelve -1", -1, pregunta.getRangoInf());
	}

	@Test
	public void testGetRangoSup () {

		assertEquals("No devuelve -1", -1, pregunta.getRangoSup());
	}

	@Test 
	public void testGetCantidad() {
		pregunta.addOpcion("hola");
		pregunta.addOpcion("adios");
		pregunta.addOpcion("hasta luego");

		assertEquals("No devuelve cantidad bien", 3, pregunta.getCantidad());
	}
}
