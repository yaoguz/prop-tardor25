package domain;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * Tests unitarios para la clase Respuesta - JUnit 4.
 * @author Peiyu Wang
 */
public class TestRespuesta {

    @Test
    public void respuestaLibre_constructoraYGetters() {
        RespuestaLibre r = new RespuestaLibre(1, "prueba", 10, "respuesta");
        assertEquals(1, r.getId());
        assertEquals("prueba", r.getNomUsuario());
        assertEquals(10, r.getIdPregunta());
        assertEquals("libre", r.getTipo());
        assertEquals("respuesta", r.getRespuesta());
        assertEquals("respuesta".length(), r.getLongitud());
    }

    @Test
    public void respuestaLibre_modificarRespuestaValido() {
        RespuestaLibre r = new RespuestaLibre(2, "prueba", 11, "hola");
        r.modificar("nuevo texto");
        assertEquals("nuevo texto", r.getRespuesta());
    }

    @Test(expected = IllegalArgumentException.class)
    public void respuestaLibre_modificarVacio() {
        RespuestaLibre r = new RespuestaLibre(3, "c", 12, "x");
        r.modificar("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void respuestaLibre_modificarMuyLargoDeberiaLanzar() {
        int max = RespuestaLibre.getMaxLength();
        StringBuilder sb = new StringBuilder(max + 1);
        for (int i = 0; i < max + 1; i++) sb.append('a');
        RespuestaLibre r = new RespuestaLibre(4, "d", 13, "ok");
        r.modificar(sb.toString());
    }

    @Test
    public void respuestaNumerica_constructoraYGetters() {
        RespuestaNumerica r = new RespuestaNumerica(5, "eve", 20, 3.5);
        assertEquals(5, r.getId());
        assertEquals("eve", r.getNomUsuario());
        assertEquals(20, r.getIdPregunta());
        assertEquals("numerica", r.getTipo());
        assertEquals(3.5, (Double) r.getRespuesta(), 0.000001);
    }

    @Test(expected = IllegalArgumentException.class)
    public void respuestaNumerica_conNaNLanza() {
        new RespuestaNumerica(6, "f", 21, Double.NaN);
    }

    @Test(expected = IllegalArgumentException.class)
    public void respuestaNumerica_conInfinitoLanza() {
        new RespuestaNumerica(7, "g", 22, Double.POSITIVE_INFINITY);
    }

    @Test
    public void respuestaNumerica_modificarValido() {
        RespuestaNumerica r = new RespuestaNumerica(8, "h", 23, 1.0);
        r.modificar(2.25);
        assertEquals(2.25, (Double) r.getRespuesta(), 0.000001);
    }

    @Test
    public void respuestaOpcionUnica_constructoraYGetters() {
        RespuestaOpcionUnica r = new RespuestaOpcionUnica(9, "ivan", 30, 2);
        assertEquals(9, r.getId());
        assertEquals("ivan", r.getNomUsuario());
        assertEquals(30, r.getIdPregunta());
        assertEquals("opcion_unica", r.getTipo());
        assertEquals(Integer.valueOf(2), r.getRespuesta());
        assertTrue(r.esOpcion(2));
        assertFalse(r.esOpcion(1));
    }

    @Test(expected = IllegalArgumentException.class)
    public void respuestaOpcionUnica_indiceNegativoConstructorLanza() {
        new RespuestaOpcionUnica(10, "j", 31, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void respuestaOpcionUnica_modificarConTipoIncorrectoLanza() {
        RespuestaOpcionUnica r = new RespuestaOpcionUnica(11, "k", 32, 0);
        r.modificar("no es numero");
    }

    @Test(expected = IllegalArgumentException.class)
    public void respuestaOpcionUnica_modificarConIndiceNegativoLanza() {
        RespuestaOpcionUnica r = new RespuestaOpcionUnica(12, "l", 33, 1);
        r.modificar(-5);
    }
}
