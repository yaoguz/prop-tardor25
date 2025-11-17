package domaincontrollers;

import domain.*;
import java.lang.reflect.Field;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
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
@RunWith(MockitoJUnitRunner.class)
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
        // Resetear singleton
        Field instance = CtrlDominioPregunta.class.getDeclaredField("singletonInstance");
        instance.setAccessible(true);
        instance.set(null, null);
        
        ctrlDominioPregunta = CtrlDominioPregunta.getInstance(mockGestor, mockCtrlDominioRespuesta, mockCtrlDominioEncuesta);
    }

    /**
     * Objeto de la prueba: existePregunta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Verificación de existencia de preguntas
     * Operativa: Mocks retornan true/false, se verifica comportamiento correcto
     */
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

    /**
     * Objeto de la prueba: getPregunta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Obtención correcta de pregunta por ID
     * Operativa: Mock retorna pregunta, se verifica obtención
     */
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

    /**
     * Objeto de la prueba: crearPreguntaLibre()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Creación correcta de pregunta de tipo libre
     * Operativa: Mocks configurados, se crea pregunta y se verifican persistencia y actualización
     */
    @Test
    public void testCrearPreguntaLibre() {
        Encuesta mockEncuesta = mock(Encuesta.class);

        when(mockCtrlDominioEncuesta.getEncuesta(1)).thenReturn(mockEncuesta);
        when(mockGestor.getNextIdPregunta()).thenReturn(10);

        int idP = ctrlDominioPregunta.crearPreguntaLibre(1, "¿Edad?");

        assertEquals("No ha retornado el id correcto", 10, idP);
        verify(mockGestor).guardarPregunta(any(PreguntaLibre.class));
        verify(mockGestor).actualizarEncuesta(mockEncuesta);
        verify(mockEncuesta).guardarPregunta(10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrearPreguntaLibreEnunciadoVacio() {
        ctrlDominioPregunta.crearPreguntaLibre(1, "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrearPreguntaLibreEnunciadoNulo() {
        ctrlDominioPregunta.crearPreguntaLibre(1, null);
    }

    /**
     * Objeto de la prueba: crearPreguntaNumerica()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Creación correcta de pregunta numérica con rango
     * Operativa: Mocks configurados, se crea pregunta con rango 0-100 y se verifica
     */
    @Test
    public void testCrearPreguntaNumerica() {
        Encuesta mockEncuesta = mock(Encuesta.class);
        when(mockCtrlDominioEncuesta.getEncuesta(1)).thenReturn(mockEncuesta);
        when(mockGestor.getNextIdPregunta()).thenReturn(10);

        int idP = ctrlDominioPregunta.crearPreguntaNumerica(1, "¿Edad?", 0, 100);

        assertEquals("No ha retornado el id correcto", 10, idP);
        verify(mockGestor).guardarPregunta(any(PreguntaNumerica.class));
        verify(mockGestor).actualizarEncuesta(mockEncuesta);
        verify(mockEncuesta).guardarPregunta(10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrearPreguntaNumerica_RangoInvalido() {

        ctrlDominioPregunta.crearPreguntaNumerica(1, "¿Edad?", 100, 0);
    }

    /**
     * Objeto de la prueba: crearPreguntaOpcionUnica()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Creación correcta de pregunta de opción única
     * Operativa: Mocks configurados, se crea pregunta con opciones Si/No y se verifica
     */
    @Test
    public void testCrearPreguntaOpcionUnica() {
        Encuesta mockEncuesta = mock(Encuesta.class);
        when(mockCtrlDominioEncuesta.getEncuesta(1)).thenReturn(mockEncuesta);
        when(mockGestor.getNextIdPregunta()).thenReturn(10);
        List<String> opciones = Arrays.asList("Si", "No");

        int idP = ctrlDominioPregunta.crearPreguntaOpcionUnica(1, "¿Pregunta?", opciones, false);

        assertEquals("No ha retornado el id correcto", 10, idP);
        verify(mockGestor).guardarPregunta(any(PreguntaOpcionUnica.class));
        verify(mockGestor).actualizarEncuesta(mockEncuesta);
        verify(mockEncuesta).guardarPregunta(10);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrearPreguntaOpcionUnicaSinOpciones() {
        ctrlDominioPregunta.crearPreguntaOpcionUnica(1, "¿Pregunta?", Arrays.asList("Una"), false);
    }

    /**
     * Objeto de la prueba: crearPreguntaOpcionMultiple()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Creación correcta de pregunta de opción múltiple
     * Operativa: Mocks configurados, se crea pregunta con 3 opciones y máximo 2 selecciones
     */
    @Test
    public void testCrearPreguntaOpcionMultiple() {
        Encuesta mockEncuesta = mock(Encuesta.class);
        when(mockCtrlDominioEncuesta.getEncuesta(1)).thenReturn(mockEncuesta);
        when(mockGestor.getNextIdPregunta()).thenReturn(10);
        List<String> opciones = Arrays.asList("A", "B", "C");

        int idP = ctrlDominioPregunta.crearPreguntaOpcionMultiple(1, "¿Pregunta?", opciones, 2);

        assertEquals("No ha retornado el id correcto", 10, idP);
        verify(mockGestor).guardarPregunta(any(PreguntaOpcionMultiple.class));
        verify(mockGestor).actualizarEncuesta(mockEncuesta);
        verify(mockEncuesta).guardarPregunta(10);
    }

    /**
     * Objeto de la prueba: anadirRespuesta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Añadir referencia de respuesta a pregunta
     * Operativa: Mock de pregunta, se añade respuesta y se verifica actualización
     */
    @Test
    public void testAnadirRespuesta() {
        Pregunta mockPregunta = mock(PreguntaLibre.class);
        when(mockGestor.getPregunta(1)).thenReturn(mockPregunta);

        ctrlDominioPregunta.anadirRespuesta(1, 100);

        verify(mockPregunta).guardarIdRespuesta(100);
        verify(mockGestor).actualizarPregunta(mockPregunta);
    }

    @Test(expected = IllegalStateException.class)
    public void testAnadirRespuestaNulo () {

        when(mockGestor.getPregunta(1)).thenReturn(null);

        ctrlDominioPregunta.anadirRespuesta(1, 100);
    }

    /**
     * Objeto de la prueba: eliminarRespuestaReferencia()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Eliminación de referencia de respuesta de pregunta
     * Operativa: Mock de pregunta, se elimina referencia y se verifica actualización
     */
    @Test
    public void testEliminarRespuestaReferencia() {
        Pregunta mockPregunta = mock(PreguntaLibre.class);
        when(mockGestor.getPregunta(1)).thenReturn(mockPregunta);

        ctrlDominioPregunta.eliminarRespuestaReferencia(1, 100);

        verify(mockPregunta).eliminarIdRespuesta(100);
        verify(mockGestor).actualizarPregunta(mockPregunta);
    }

    @Test(expected = IllegalStateException.class)
    public void testEliminarRespuestaReferenciaConPreguntaNull() {
        when(mockGestor.getPregunta(1)).thenReturn(null);

        ctrlDominioPregunta.anadirRespuesta(1, 100);
    }


    /**
     * Objeto de la prueba: eliminarPregunta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Eliminación de pregunta y sus respuestas asociadas
     * Operativa: Mocks configurados, se elimina pregunta y se verifica eliminación en cascada
     */
    @Test
    public void testEliminarPregunta() {
        Pregunta mockPregunta = mock(PreguntaLibre.class);

        when(mockGestor.getPregunta(10)).thenReturn(mockPregunta);
        when(mockPregunta.getIdRespuestas()).thenReturn(new ArrayList<> (Arrays.asList(100,200)));
        when(mockPregunta.getIdEncuesta()).thenReturn(1);

        Encuesta mockEncuesta = mock(Encuesta.class);
        when(mockCtrlDominioEncuesta.getEncuesta(1)).thenReturn(mockEncuesta);

        ctrlDominioPregunta.eliminarPregunta(10);

        verify(mockCtrlDominioRespuesta, times(2)).eliminarRespuesta(anyInt());
        verify(mockGestor).eliminarPregunta(10);
        verify(mockPregunta).getIdEncuesta();
        verify(mockEncuesta).eliminarPregunta(10);
    }

    /**
     * Objeto de la prueba: getAllPreguntas()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Obtención de todas las preguntas como HashMap
     * Operativa: Mock retorna lista de preguntas, se verifica conversión a HashMap
     */
    @Test
    public void testGetAllPreguntas() {
        ArrayList<Pregunta> lista = new ArrayList<>();
        Pregunta p = mock(PreguntaLibre.class);
        when(p.getIdPregunta()).thenReturn(1);
        lista.add(p);
        when(mockGestor.getPreguntas()).thenReturn(lista);

        HashMap<Integer, Pregunta> resultado = new HashMap<>(ctrlDominioPregunta.getAllPreguntas());

        assertEquals("No ha sacado todas las preguntas", 1, resultado.size());
        assertTrue(resultado.containsKey(1));
        verify(mockGestor).getPreguntas();
    }

    /**
     * Objeto de la prueba: getRespuestasPregunta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Obtención de respuestas asociadas a una pregunta
     * Operativa: Mocks retornan respuestas por IDs, se verifica obtención correcta
     */
    @Test
    public void testGetRespuestasPregunta() {
        Pregunta p = mock(PreguntaLibre.class);
     
        when(mockGestor.getPregunta(10)).thenReturn(p);
        when(p.getIdRespuestas()).thenReturn(new ArrayList<> (Arrays.asList(100,200)));
        
        Respuesta r1 = mock(Respuesta.class);
        Respuesta r2 = mock(Respuesta.class);
        when(mockCtrlDominioRespuesta.getRespuesta(100)).thenReturn(r1);
        when(mockCtrlDominioRespuesta.getRespuesta(200)).thenReturn(r2);

        List<Respuesta> resultado = ctrlDominioPregunta.getRespuestasPregunta(10);

        assertEquals("No ha sacado todas las respuestas", 2, resultado.size());
        verify(mockCtrlDominioRespuesta, times(2)).getRespuesta(anyInt());
    }
}
