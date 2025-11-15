package domain;

import static org.junit.Assert.*;
import org.junit.Test;
import domain.Pregunta;

/**
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
     * Configuración inicial antes de cada test.
     */
    @Before
    public void setUp() {
        pregunta = new PreguntaNumerica(idEncuesta, idPregunta, enunciado, rangoInf, rangoSup);
    }


	@Test
	public void testConstructoraOk() {
	  	assertEquals("No se guardo idEncuesta bien", idEncuesta, pregunta.getIdEncuesta());
	  	assertEquals("No se guardo idPregunt bien", idPregunta, pregunta.getIdPregunta());
	  	assertEquals("No se guardo enunciado correcto", enunciado, pregunta.getEnunciado());
		assertEquals("No se creo bien idRespuesta",0, pregunta.getIdRespuestas().size());
		assertEquals("No se guardo rangoInf bien",rangoInf, pregunta.getRangoInf());
		assertEquals("No se guardo rangoSup bien",rangoSup, pregunta.getRangoSup());
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
		assertEquals("El rangoInf no coincide", rangoInf, pregunta.getRangoInf());
	}

	@Test
	public void testGetRangoSup() {
		assertEquals("El rangoSup no coincide", rangoSup, pregunta.getRangoSup());
	}

	@Test
	public void testTipo() {
		assertEquals("No devuelve numerica", "Numerica", pregunta.tipoPregunta());
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCrearRespuestaRangoInf() {
		pregunta.crearRespuesta(2, "Alessandro", 1);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testCrearRespuestaRangoSup() {
		pregunta.crearRespuesta(2, "Alessadro", 100);
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
		ArrayList<String> res = pregunta.getOpciones();

		assertTrue("La lista no es vacia", res.isEmpty());
	}


	@Test
	public void testGetNumMaxOpc () {

		assertEquals("No devuelve -1", -1, pregunta.getNumMaxOpc());
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
