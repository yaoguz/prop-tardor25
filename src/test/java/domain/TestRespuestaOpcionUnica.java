package domain;

import static org.junit.Assert.*;
import org.junit.Test;

/** Tests unitarios para RespuestaOpcionUnica.
 *  Verifica creación, getters y validaciones simples.
 *  @author Peiyu Wang
 */
public class TestRespuestaOpcionUnica {

    /** Comprueba constructor y getters básicos. */
    @Test
    public void constructorYGetters() {
        RespuestaOpcionUnica r = new RespuestaOpcionUnica(9, "ivan", 30, 2);
        assertEquals(9, r.getId());
        assertEquals("ivan", r.getNomUsuario());
        assertEquals(30, r.getIdPregunta());
        assertEquals("opcion_unica", r.getTipo());
        assertEquals((Integer)2, r.getRespuesta());
        assertTrue(r.esOpcion(2));
        assertFalse(r.esOpcion(1));
    }

    /** Índice negativo en constructor lanza excepción. */
    @Test(expected = IllegalArgumentException.class)
    public void constructorIndiceNegativo() {
        new RespuestaOpcionUnica(22, "joan", 32, -1);
    }

    /** Modificar con índice válido actualiza la respuesta. */
    @Test
    public void modificarValido() {
        RespuestaOpcionUnica r = new RespuestaOpcionUnica(21, "pol", 31, 0);
        assertEquals((Integer)0, r.getRespuesta());
        r.modificar(2);
        assertEquals((Integer)2, r.getRespuesta());
    }

    /** Modificar con tipo incorrecto lanza excepción. */
    @Test(expected = IllegalArgumentException.class)
    public void modificarTipoIncorrecto() {
        RespuestaOpcionUnica r = new RespuestaOpcionUnica(23, "ana", 33, 0);
        r.modificar("hola");
    }

    /** Modificar con índice negativo lanza excepción. */
    @Test(expected = IllegalArgumentException.class)
    public void modificarNegativo() {
        RespuestaOpcionUnica r = new RespuestaOpcionUnica(23, "ana", 33, 0);
        r.modificar(-1);
    }
}
