package domain;

import static org.junit.Assert.*;
import org.junit.Test;

/** Tests unitarios para RespuestaLibre 
 *  @author Peiyu Wang
*/
public class TestRespuestaLibre {

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

    @Test(expected = IllegalArgumentException.class)
    public void constructorTextoNulo() {
        new RespuestaLibre(3, "joan", 12, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorTextoVacio() {
        new RespuestaLibre(4, "joan", 12, "   ");
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorTextoMuyLargo() {
        int max = RespuestaLibre.getMaxLength();
        StringBuilder s = new StringBuilder(max + 1);
        for (int i = 0; i < max + 1; i++) s.append('a');
        new RespuestaLibre(7, "luis", 101, s.toString());
    }

    @Test
    public void modificarValido() {
        RespuestaLibre r = new RespuestaLibre(2, "pol", 11, "hola");
        r.modificar("nuevo texto");
        assertEquals("nuevo texto", r.getRespuesta());
    }

    @Test(expected = IllegalArgumentException.class)
    public void modificarMuyLargo() {
        int max = RespuestaLibre.getMaxLength();
        StringBuilder s = new StringBuilder(max + 1);
        for (int i = 0; i < max + 1; i++) s.append('a');
        RespuestaLibre r = new RespuestaLibre(6, "x", 13, "ok");
        r.modificar(s.toString());
    }

    @Test(expected = IllegalArgumentException.class)
    public void modificarVacio() {
        RespuestaLibre r = new RespuestaLibre(3, "cc", 12, "x");
        r.modificar("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void modificarNoString() {
        RespuestaLibre r = new RespuestaLibre(4, "carlos", 12, "hola");
        r.modificar(123);
    }

}



