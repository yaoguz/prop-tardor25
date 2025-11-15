package domain;

import static org.junit.Assert.*;
import org.junit.Test;

/** Tests unitarios para RespuestaOpcionUnica (JUnit4) */
public class RespuestaOpcionUnicaTest {

    @Test
    public void constructorYGetters() {
        RespuestaOpcionUnica r = new RespuestaOpcionUnica(20, "ana", 30, 1);
        assertEquals(Integer.valueOf(1), (Integer) r.getRespuesta());
        assertEquals("opcion_unica", r.getTipo());
        assertTrue(r.esOpcion(1));
    }

    @Test
    public void modificarValido() {
        RespuestaOpcionUnica r = new RespuestaOpcionUnica(21, "pol", 31, 0);
        r.modificar(Integer.valueOf(2));
        assertEquals(Integer.valueOf(2), (Integer) r.getRespuesta());
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorIndiceNegativoLanza() {
        new RespuestaOpcionUnica(22, "joan", 32, -1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void modificarTipoIncorrectoLanza() {
        RespuestaOpcionUnica r = new RespuestaOpcionUnica(23, "ana", 33, 0);
        r.modificar("no es un numero");
    }
}
