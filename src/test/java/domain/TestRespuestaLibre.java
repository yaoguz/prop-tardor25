package domain;

import static org.junit.Assert.*;
import org.junit.Test;

/** Tests unitarios para RespuestaLibre.
 *  Comprueba constructores y modificación de texto.
 *  @author Peiyu Wang
*/
public class TestRespuestaLibre {

    /** Comprueba constructor y getters. */
    @Test
    public void constructorYGetters() {
        RespuestaLibre r = new RespuestaLibre(1, "prueba", 10, "respuesta");
        assertEquals(1, r.getId());
        assertEquals("prueba", r.getNomUsuario());
        assertEquals(10, r.getIdPregunta());
        assertEquals("libre", r.getTipo());
        assertEquals("respuesta", r.getRespuesta());
        assertEquals("respuesta".length(), r.getLongitud());
    }

    /** Texto nulo lanza excepción. */
    @Test(expected = IllegalArgumentException.class)
    public void constructorTextoNulo() {
        new RespuestaLibre(3, "joan", 12, null);
    }

    /** Texto vacío lanza excepción. */
    @Test(expected = IllegalArgumentException.class)
    public void constructorTextoVacio() {
        new RespuestaLibre(4, "joan", 12, "   ");
    }

    /** Texto demasiado largo lanza excepción. */
    @Test(expected = IllegalArgumentException.class)
    public void constructorTextoMuyLargo() {
        int max = RespuestaLibre.getMaxLength();
        StringBuilder s = new StringBuilder(max + 1);
        for (int i = 0; i < max + 1; i++) s.append('a');
        new RespuestaLibre(7, "luis", 101, s.toString());
    }

    /** Modificar con texto válido actualiza la respuesta. */
    @Test
    public void modificarValido() {
        RespuestaLibre r = new RespuestaLibre(2, "pol", 11, "hola");
        r.modificar("nuevo texto");
        assertEquals("nuevo texto", r.getRespuesta());
    }

    /** Modificar con texto demasiado largo lanza excepción. */
    @Test(expected = IllegalArgumentException.class)
    public void modificarMuyLargo() {
        int max = RespuestaLibre.getMaxLength();
        StringBuilder s = new StringBuilder(max + 1);
        for (int i = 0; i < max + 1; i++) s.append('a');
        RespuestaLibre r = new RespuestaLibre(6, "x", 13, "ok");
        r.modificar(s.toString());
    }

    /** Modificar con texto vacío lanza excepción. */
    @Test(expected = IllegalArgumentException.class)
    public void modificarVacio() {
        RespuestaLibre r = new RespuestaLibre(3, "cc", 12, "x");
        r.modificar("");
    }

    /** Modificar con tipo no String lanza excepción. */
    @Test(expected = IllegalArgumentException.class)
    public void modificarNoString() {
        RespuestaLibre r = new RespuestaLibre(4, "carlos", 12, "hola");
        r.modificar(123);
    }

}



