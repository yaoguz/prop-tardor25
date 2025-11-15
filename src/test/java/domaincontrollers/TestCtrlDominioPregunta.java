package domaincontrollers;

import domain.*;
import java.lang.reflect.Field;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import persistence.GestorPersistencia;


/**
 * @author Alessandro Jin
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class TestCtrlDominioPregunta {

    @Mock
    private GestorPersistencia mockGestor;
    @Mock
    private CtrlDominioRespuesta mockCtrlDominioRespuesta;
    @Mock
    private CtrlDominioEncuesta mockCtrlDominioEncuesta;

    private CtrlDominioPregunta ctrlDominioPregunta;

    @Before
    public void setUp() throws Exception {
        Field instance = CtrlDominioPregunta.class.getDeclaredField("singletonInstance");
        instance.setAccessible(true);
        instance.set(null, null);
        
        ctrlDominioPregunta = CtrlDominioPregunta.getInstance(mockGestor, mockCtrlDominioRespuesta, mockCtrlDominioEncuesta);
    }

    @Test
    public void testExistePregunta() {
        when(mockGestor.existePregunta(1)).thenReturn(true);
        when(mockGestor.existePregunta(2)).thenReturn(false);
        
        boolean resultado1 = ctrlDominioPregunta.existePregunta(1);
        boolean resultado2 = ctrlDominioPregunta.existePregunta(2);

        assertTrue("Deberia ser true", resultado1);
        assertFalse("Deberia ser false", resultado2);
        verify(mockGestor).existePregunta(1);
        verify(mockGestor).existePregunta(2);
    }

    @Test
    public void testGetPregunta() {
        Pregunta mockPregunta = mock(PreguntaLibre.class);
        when(mockGestor.getPregunta(1)).thenReturn(mockPregunta);

        Pregunta resultado = ctrlDominioPregunta.getPregunta(1);

        assertEquals("No ha sacado la pregunta correcta", mockPregunta, resultado);
        verify(mockGestor).getPregunta(1);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetPreguntaNoExiste() {
        when(mockGestor.getPregunta(1)).thenReturn(null);

        ctrlDominioPregunta.getPregunta(1);
    }

    @Test
    public void testCrearPreguntaLibre() {
        Encuesta mockEncuesta = new Encuesta(1, "Test", "autor");
        when(mockCtrlDominioEncuesta.getEncuesta(1)).thenReturn(mockEncuesta);
        when(mockGestor.getNextIdPregunta()).thenReturn(10);

        int idP = ctrlDominioPregunta.crearPreguntaLibre(1, "¿Pregunta libre?");

        assertEquals("No ha retornado el id correcto", 10, idP);
        verify(mockGestor).guardarPregunta(any(PreguntaLibre.class));
        verify(mockGestor).actualizarEncuesta(mockEncuesta);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrearPreguntaLibreEnunciadoVacio() {
        ctrlDominioPregunta.crearPreguntaLibre(1, "");
    }

    @Test
    public void testCrearPreguntaNumerica() {
        Encuesta mockEncuesta = new Encuesta(1, "Test", "autor");
        when(mockCtrlDominioEncuesta.getEncuesta(1)).thenReturn(mockEncuesta);
        when(mockGestor.getNextIdPregunta()).thenReturn(10);

        int idP = ctrlDominioPregunta.crearPreguntaNumerica(1, "¿Edad?", 0, 100);

        assertEquals("No ha retornado el id correcto", 10, idP);
        verify(mockGestor).guardarPregunta(any(PreguntaNumerica.class));
        verify(mockGestor).actualizarEncuesta(mockEncuesta);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrearPreguntaNumerica_RangoInvalido() {
        Encuesta mockEncuesta = new Encuesta(1, "Test", "autor");
        when(mockCtrlDominioEncuesta.getEncuesta(1)).thenReturn(mockEncuesta);

        ctrlDominioPregunta.crearPreguntaNumerica(1, "¿Pregunta?", 100, 0);
    }

    @Test
    public void testCrearPreguntaOpcionUnica() {
        Encuesta mockEncuesta = new Encuesta(1, "Test", "autor");
        when(mockCtrlDominioEncuesta.getEncuesta(1)).thenReturn(mockEncuesta);
        when(mockGestor.getNextIdPregunta()).thenReturn(10);
        List<String> opciones = Arrays.asList("Si", "No");

        int idP = ctrlDominioPregunta.crearPreguntaOpcionUnica(1, "¿Pregunta?", opciones, false);

        assertEquals("No ha retornado el id correcto", 10, idP);
        verify(mockGestor).guardarPregunta(any(PreguntaOpcionUnica.class));
        verify(mockGestor).actualizarEncuesta(mockEncuesta);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrearPreguntaOpcionUnicaSinOpciones() {
        ctrlDominioPregunta.crearPreguntaOpcionUnica(1, "¿Pregunta?", Arrays.asList("Una"), false);
    }

    @Test
    public void testCrearPreguntaOpcionMultiple() {
        Encuesta mockEncuesta = new Encuesta(1, "Test", "autor");
        when(mockCtrlDominioEncuesta.getEncuesta(1)).thenReturn(mockEncuesta);
        when(mockGestor.getNextIdPregunta()).thenReturn(10);
        List<String> opciones = Arrays.asList("A", "B", "C");

        int idP = ctrlDominioPregunta.crearPreguntaOpcionMultiple(1, "¿Pregunta?", opciones, 2);

        assertEquals("No ha retornado el id correcto", 10, idP);
        verify(mockGestor).guardarPregunta(any(PreguntaOpcionMultiple.class));
        verify(mockGestor).actualizarEncuesta(mockEncuesta);
    }

    @Test
    public void testAnadirRespuesta() {
        Pregunta mockPregunta = mock(PreguntaLibre.class);
        when(mockGestor.getPregunta(1)).thenReturn(mockPregunta);

        ctrlDominioPregunta.anadirRespuesta(1, 100);

        verify(mockPregunta).guardarIdRespuesta(100);
        verify(mockGestor).actualizarPregunta(mockPregunta);
    }

    @Test
    public void testEliminarRespuestaReferencia() {
        Pregunta mockPregunta = mock(PreguntaLibre.class);
        when(mockGestor.getPregunta(1)).thenReturn(mockPregunta);

        ctrlDominioPregunta.eliminarRespuestaReferencia(1, 100);

        verify(mockPregunta).eliminarIdRespuesta(100);
        verify(mockGestor).actualizarPregunta(mockPregunta);
    }

    @Test
    public void testEliminarPregunta() {
        PreguntaLibre p = new PreguntaLibre(1, 10, "¿Pregunta?");
        p.guardarIdRespuesta(100);
        p.guardarIdRespuesta(101);
        when(mockGestor.getPregunta(10)).thenReturn(p);
        
        Encuesta mockEncuesta = new Encuesta(1, "Test", "autor");
        when(mockCtrlDominioEncuesta.getEncuesta(1)).thenReturn(mockEncuesta);

        ctrlDominioPregunta.eliminarPregunta(10);

        verify(mockCtrlDominioRespuesta, times(2)).eliminarRespuesta(anyInt());
        verify(mockGestor).eliminarPregunta(10);
    }

    @Test
    public void testGetAllPreguntas() {
        ArrayList<Pregunta> lista = new ArrayList<>();
        Pregunta p = mock(PreguntaLibre.class);
        when(p.getIdPregunta()).thenReturn(1);
        lista.add(p);
        when(mockGestor.getPreguntas()).thenReturn(lista);

        HashMap<Integer, Pregunta> resultado = ctrlDominioPregunta.getAllPreguntas();

        assertEquals("No ha sacado todas las preguntas", 1, resultado.size());
        assertTrue(resultado.containsKey(1));
        verify(mockGestor).getPreguntas();
    }

    @Test
    public void testGetRespuestasPregunta() {
        PreguntaLibre p = new PreguntaLibre(1, 10, "¿Pregunta?");
        p.guardarIdRespuesta(100);
        p.guardarIdRespuesta(101);
        when(mockGestor.getPregunta(10)).thenReturn(p);
        
        Respuesta r1 = mock(Respuesta.class);
        Respuesta r2 = mock(Respuesta.class);
        when(mockCtrlDominioRespuesta.getRespuesta(100)).thenReturn(r1);
        when(mockCtrlDominioRespuesta.getRespuesta(101)).thenReturn(r2);

        List<Respuesta> resultado = ctrlDominioPregunta.getRespuestasPregunta(10);

        assertEquals("No ha sacado todas las respuestas", 2, resultado.size());
        verify(mockCtrlDominioRespuesta, times(2)).getRespuesta(anyInt());
    }

    @Test
    public void testCrearPreguntaGenerica() {
        Encuesta mockEncuesta = new Encuesta(1, "Test", "autor");
        when(mockCtrlDominioEncuesta.getEncuesta(1)).thenReturn(mockEncuesta);
        when(mockGestor.getNextIdPregunta()).thenReturn(10);
        
        Map<String, Object> atributos = new HashMap<>();
        atributos.put("min", 0);
        atributos.put("max", 100);

        int idP = ctrlDominioPregunta.crearPregunta(1, "¿Edad?", "numerica", atributos);

        assertEquals("No ha retornado el id correcto", 10, idP);
        verify(mockGestor).guardarPregunta(any(PreguntaNumerica.class));
    }
}
