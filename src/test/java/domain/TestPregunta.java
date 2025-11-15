package domain;

import static org.junit.Assert.*;
import org.junit.Test;
import domain.Pregunta;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * @author Alessandro Jin
 */

public class TestPregunta {
    private Pregunta pregunta;
    private final int idEncuesta = 3;
	private final int idPregunta = 10;
    private final String enunciado = "test";
    private final ArrayList<Integer> idRespuestas = new ArrayList<>(Arrays.asList(1,2,3));
    
    /**
     * Configuración inicial antes de cada test.
     */
    @Before
    public void setUp() {
        assertEquals("No se guardo idEncuesta bien", idEncuesta, pregunta.getIdEncuesta());
        assertEquals("No se guardo idPregunt bien", idPregunta, pregunta.getIdPregunta());
        assertEquals("No se guardo enunciado correcto", enunciado, pregunta.getEnunciado());
        assertEquals("No se guardo idRespuesta", idRespuestas, pregunta.getIdRespuestas());
        pregunta = new Pregunta(idEncuesta, idPregunta, enunciado);
    }


    @Test(expected = IllegalArgumentException.class)
    public void testConstructorExcepcionNulo() {
        pregunta = new Pregunta(idEncuesta, idPregunta, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testConstructorExcepcionNulo() {
        pregunta = new Pregunta(idEncuesta, idPregunta, null);
    }

    @Test(expected = IllegalArgumentException.class) 
    public void testConstructorExcepcionVacio() {
        pregunta = new Pregunta(idEncuesta, idPregunta, "");
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

    
}
