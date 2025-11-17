package domain;

import static org.junit.Assert.*;
import org.junit.Test;

/** Tests unitarios para RespuestaNumerica (JUnit4) 
 * @author Peiyu Wang
*/
public class TestRespuestaNumerica {

    /**
     * Comprueba constructor y getters.
     */
    @Test
    public void constructorYGetters() {
        RespuestaNumerica r = new RespuestaNumerica(5, "eve", 20, 3.5);
        assertEquals(5, r.getId());
        assertEquals("eve", r.getNomUsuario());
        assertEquals(20, r.getIdPregunta());
        assertEquals("numerica", r.getTipo());
        assertEquals(Double.valueOf(3.5), (Double) r.getRespuesta());
    }

    /**
     * NaN lanza excepción.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorNaN() {
        new RespuestaNumerica(12, "joan", 22, Double.NaN);
    }

    /**
     * Infinito positivo lanza excepción.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorInfinitoPositivo() {
        new RespuestaNumerica(14, "joan", 24, Double.POSITIVE_INFINITY);
    }

    /**
     * Infinito negativo lanza excepción.
     */
    @Test(expected = IllegalArgumentException.class)
    public void constructorInfinitoNegativo() {
        new RespuestaNumerica(14, "joan", 24, Double.NEGATIVE_INFINITY);
    }

    /**
     * Modifica con valores válidos.
     */
    @Test
    public void modificarValido() {
        RespuestaNumerica r = new RespuestaNumerica(11, "pol", 21, 1.0);
        r.modificar(Integer.valueOf(5));
        assertEquals(Double.valueOf(5.0), (Double) r.getRespuesta());
        r.modificar(Double.valueOf(2.25));
        assertEquals(Double.valueOf(2.25), (Double) r.getRespuesta());
    }

    /**
     * Modificar con string lanza excepción.
     */
    @Test(expected = IllegalArgumentException.class)
    public void modificarString() {
        RespuestaNumerica r = new RespuestaNumerica(13, "ana", 23, 0.0);
        r.modificar("hola");
    }

    /**
     * Modificar con infinito positivo lanza excepción.
     */
    @Test(expected = IllegalArgumentException.class)
    public void modificarInfinitoPositivo() {
        RespuestaNumerica r = new RespuestaNumerica(13, "ana", 23, 0.0);
        r.modificar(Double.POSITIVE_INFINITY);
    }

    /**
     * Modificar con infinito negativo lanza excepción.
     */
    @Test(expected = IllegalArgumentException.class)
    public void modificarInfinitoNegativo() {
        RespuestaNumerica r = new RespuestaNumerica(13, "ana", 23, 0.0);
        r.modificar(Double.NEGATIVE_INFINITY);
    }

    /**
     * Modificar con NaN lanza excepción.
     */
    @Test(expected = IllegalArgumentException.class)
    public void modificarNaN() {
        RespuestaNumerica r = new RespuestaNumerica(13, "ana", 23, 0.0);
        r.modificar(Double.NaN);
    }
}
