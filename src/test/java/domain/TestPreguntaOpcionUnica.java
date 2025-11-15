package domain;

import static org.junit.Assert.*;
import org.junit.Test;
import domain.Pregunta;

 /**
 * @author Alessandro Jin
 */

public class TestPreguntaOpcionUnica {
    
   	private PreguntaOpcionUnica pregunta;
    private PreguntaOpcionUnica preguntaOrdenada;
    private final int idEncuesta = 3;
	private final int idPregunta = 10;
    private final String enunciado = "test";
    private final ArrayList<Integer> idRespuestas = new ArrayList<>(Arrays.asList(1,2,3));
    
    /**
     * Configuración inicial antes de cada test.
     */
    @Before
    public void setUp() {
        pregunta = new PreguntaOpcionUnica(idEncuesta, idPregunta, enunciado, false);
        preguntaOrdenada = new new PreguntaOpcionUnica(idEncuesta, idPregunta, enunciado, true);
    }


	@Test
	public void testConstructoraOk() {
	  	assertEquals("No se guardo idEncuesta bien", idEncuesta, pregunta.getIdEncuesta());
        assertEquals("No se guardo idPregunt bien", idPregunta, pregunta.getIdPregunta());
        assertEquals("No se guardo enunciado correcto", enunciado, pregunta.getEnunciado());
        assertEquals("No se creo bien idRespuesta", 0, pregunta.getIdRespuestas().size());
        assertFalse("No es No ordenada", pregunta.esOrdenada());
        assertTrue("Deberia ser ordenada", preguntaOrdenada.esOrdenada());
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
		assertEquals("No devuelve Opcion unica", "opcion_unica", pregunta.tipoPregunta());
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

        assertEquals("No se elimino el correcto", res, Arrays.aslist(2,3));
    }

    @Test(expected = IllegalArgumentException.class) 
    public void testEliminarIdRespuestaExcepcion() {
        pregunta.eliminarIdRespuesta(4);
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
		pregunta.addOpcion("hola");
		pregunta.addOpcion("adios");
		pregunta.addOpcion("hasta luego");

		assertEquals("No devuelve cantidad bien", 3, pregunta.getRangoSup());
	}
}
