package domain;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Set;

/** Tests unitarios para RespuestaOpcionMultiple (JUnit4) */
public class TestRespuestaOpcionMultiple {

    @Test
    public void constructorYGetters() {
        int[] indices = new int[] {2, 0, 4};
        RespuestaOpcionMultiple r = new RespuestaOpcionMultiple(30, "ana", 40, indices);
        assertEquals(30, r.getId());
        assertEquals("ana", r.getNomUsuario());
        assertEquals(40, r.getIdPregunta());
        assertEquals("opcion multiple", r.getTipo());
        assertEquals(3, r.getNumeroOpcionesSeleccionadas());
        int[] resp = r.getRespuesta();
        // se ordenan internamente
        assertEquals(3, resp.length);
        assertEquals(true, r.contieneOpcion(0));
        assertEquals(true, r.contieneOpcion(2));
        assertEquals(true, r.contieneOpcion(4));
        Set<Integer> set = r.getOpcionesComoSet();
        assertTrue(set.contains(Integer.valueOf(0)));
        assertTrue(set.contains(Integer.valueOf(2)));
        assertTrue(set.contains(Integer.valueOf(4)));
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorArrayNulo() {
        new RespuestaOpcionMultiple(32, "joan", 42, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorArrayVacio() {
        new RespuestaOpcionMultiple(33, "joan", 43, new int[] {});
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorConDuplicados() {
        new RespuestaOpcionMultiple(34, "joan", 44, new int[] {1,1});
    }

    @Test(expected = IllegalArgumentException.class)
    public void constructorConNegativos() {
        new RespuestaOpcionMultiple(34, "joan", 44, new int[] {-1,-2});
    }

    @Test
    public void modificarValido() {
        int[] initial = new int[] {1, 3};
        RespuestaOpcionMultiple r = new RespuestaOpcionMultiple(31, "pol", 41, initial);
        int[] nuevos = new int[] {0,2};
        r.modificar(nuevos);
        assertEquals(2, r.getNumeroOpcionesSeleccionadas());
        assertTrue(r.contieneOpcion(0));
        assertTrue(r.contieneOpcion(2));
    }


    @Test(expected = IllegalArgumentException.class)
    public void modificarTipoIncorrecto() {
        int[] init = new int[] {1};
        RespuestaOpcionMultiple r = new RespuestaOpcionMultiple(35, "ana", 45, init);
        r.modificar("hola");
    }

    @Test(expected = IllegalArgumentException.class)
    public void modificarVacio() {
        int[] init = new int[] {1};
        RespuestaOpcionMultiple r = new RespuestaOpcionMultiple(35, "ana", 45, init);
        r.modificar(new int[] {});
        
    }



}
