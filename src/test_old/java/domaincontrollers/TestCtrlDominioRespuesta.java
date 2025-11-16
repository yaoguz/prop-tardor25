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
 * @author Xiangjun Yepan
 */
@RunWith(MockitoJUnitRunner.class)
public class TestCtrlDominioRespuesta {

    @Mock
    private GestorPersistencia mockGestor;
    @Mock
    private CtrlDominioUsuario mockCtrlDominioUsuario;
    @Mock
    private CtrlDominioEncuesta mockCtrlDominioEncuesta;
    @Mock
    private CtrlDominioPregunta mockCtrlDominioPregunta;

    private CtrlDominioRespuesta ctrlDominioRespuesta;

    @Before
    public void setUp() throws Exception {
        Field instance = CtrlDominioRespuesta.class.getDeclaredField("singletonInstance");
        instance.setAccessible(true);
        instance.set(null, null);
        
        ctrlDominioRespuesta = CtrlDominioRespuesta.getInstance(mockGestor, mockCtrlDominioUsuario, mockCtrlDominioEncuesta, mockCtrlDominioPregunta);
    }

    @Test
    public void testExisteRespuesta() {
        when(mockGestor.existeRespuesta(1)).thenReturn(true);
        when(mockGestor.existeRespuesta(2)).thenReturn(false);

        boolean resultado1 = ctrlDominioRespuesta.existeRespuesta(1);
        boolean resultado2 = ctrlDominioRespuesta.existeRespuesta(2);

        assertTrue("Deberia ser true", resultado1);
        assertFalse("Deberia ser false", resultado2);
        verify(mockGestor).existeRespuesta(1);
        verify(mockGestor).existeRespuesta(2);
    }

    @Test
    public void testGetRespuesta() {
        Respuesta mockRespuesta = mock(RespuestaLibre.class);
        when(mockGestor.getRespuesta(1)).thenReturn(mockRespuesta);

        Respuesta resultado = ctrlDominioRespuesta.getRespuesta(1);

        assertEquals("No ha sacado la respuesta correcta", mockRespuesta, resultado);
        verify(mockGestor).getRespuesta(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetRespuestaNoExiste() {
        when(mockGestor.getRespuesta(1)).thenReturn(null);

        ctrlDominioRespuesta.getRespuesta(1);
    }

    @Test
    public void testDarRespuesta() {
        PreguntaLibre mockPregunta = mock(PreguntaLibre.class);
        Usuario mockUsuario = mock(Usuario.class);
        RespuestaLibre mockRespuesta = mock(RespuestaLibre.class);
        
        when(mockCtrlDominioPregunta.getPregunta(1)).thenReturn(mockPregunta);
        when(mockCtrlDominioUsuario.getUsuario("user1")).thenReturn(mockUsuario);
        when(mockGestor.getNextIdRespuesta()).thenReturn(100);
        when(mockPregunta.crearRespuesta(eq(100), eq("user1"), anyString())).thenReturn(mockRespuesta);

        int idR = ctrlDominioRespuesta.darRespuesta("user1", 1, "Respuesta libre");

        assertEquals("No ha retornado el id correcto", 100, idR);
        verify(mockGestor).guardarRespuesta(mockRespuesta);
        verify(mockGestor).actualizarUsuario(mockUsuario);
        verify(mockGestor).actualizarPregunta(mockPregunta);
        verify(mockUsuario).guardarIdRespuesta(100);
        verify(mockPregunta).guardarIdRespuesta(100);
    }

    @Test
    public void testModificarRespuesta() {
        RespuestaLibre mockRespuesta = mock(RespuestaLibre.class);
        when(mockGestor.getRespuesta(1)).thenReturn(mockRespuesta);

        ctrlDominioRespuesta.modificarRespuesta(1, "Nueva respuesta");

        verify(mockRespuesta).modificar("Nueva respuesta");
        verify(mockGestor).actualizarRespuesta(mockRespuesta);
    }

    @Test
    public void testEliminarRespuesta() {
        RespuestaLibre r = mock(RespuestaLibre.class);
        Usuario mockUsuario = mock(Usuario.class);
        Pregunta mockPregunta = mock(PreguntaLibre.class);
        
        when(mockGestor.getRespuesta(100)).thenReturn(r);
        when(r.getIdPregunta()).thenReturn(1);
        when(r.getNomUsuario()).thenReturn("user1");
        when(mockCtrlDominioUsuario.getUsuario("user1")).thenReturn(mockUsuario);
        when(mockCtrlDominioPregunta.getPregunta(1)).thenReturn(mockPregunta);

        ctrlDominioRespuesta.eliminarRespuesta(100);

        verify(mockUsuario).eliminarIdRespuesta(100);
        verify(mockPregunta).eliminarIdRespuesta(100);
        verify(mockGestor).eliminarRespuesta(100);
    }

    @Test 
    public void testImportarRespuestas() {
        File file = new File("src/test/resources/test_respuestas.csv");
        PreguntaLibre mockPregunta = mock(PreguntaLibre.class);
        Usuario mockUsuario = mock(Usuario.class);
        Respuesta mockRespuesta = mock(Respuesta.class);
        when(mockCtrlDominioEncuesta.existeEncuesta(1)).thenReturn(true);
        when(mockCtrlDominioPregunta.existePregunta(1)).thenReturn(true);
        when(mockCtrlDominioUsuario.existeUsuario("user1")).thenReturn(true);
        when(mockCtrlDominioPregunta.getPregunta(1)).thenReturn(mockPregunta);
        when(mockPregunta.getIdEncuesta()).thenReturn(1);

        //when(mockCtrlDominioPregunta.getPregunta(1)).thenReturn(mockPregunta);
        when(mockCtrlDominioUsuario.getUsuario("user1")).thenReturn(mockUsuario);
        when(mockGestor.getNextIdRespuesta()).thenReturn(10);
        when(mockPregunta.crearRespuesta(10, "user1", "Respuesta libre")).thenReturn(mockRespuesta);
        
        ctrlDominioRespuesta.importarRespuestas(file);

        verify(mockGestor).guardarRespuesta(mockRespuesta);
        verify(mockGestor).actualizarUsuario(mockUsuario);
        verify(mockGestor).actualizarPregunta(mockPregunta);
        verify(mockUsuario).guardarIdRespuesta(10);
        verify(mockPregunta).guardarIdRespuesta(10);
    }

    @Test(expected = IllegalArgumentException.class) 
    public void testImportarRespuestasEncuestaNoExiste() {
        File file = new File("src/test/resources/invalid_respuestas.csv");
        when(mockCtrlDominioEncuesta.existeEncuesta(1)).thenReturn(false);
        ctrlDominioRespuesta.importarRespuestas(file);
    }

    @Test(expected = IllegalArgumentException.class) 
    public void testImportarRespuestasPreguntaNoExiste() {
        File file = new File("src/test/resources/invalid_respuestas.csv");
        when(mockCtrlDominioEncuesta.existeEncuesta(1)).thenReturn(true);
        when(mockCtrlDominioPregunta.existePregunta(1)).thenReturn(false);
        ctrlDominioRespuesta.importarRespuestas(file);
    }

    @Test(expected = IllegalArgumentException.class) 
    public void testImportarRespuestasUsuarioNoExiste() {
        File file = new File("src/test/resources/invalid_respuestas.csv");
        when(mockCtrlDominioEncuesta.existeEncuesta(1)).thenReturn(true);
        when(mockCtrlDominioPregunta.existePregunta(1)).thenReturn(true);
        when(mockCtrlDominioUsuario.existeUsuario("user1")).thenReturn(false);
        ctrlDominioRespuesta.importarRespuestas(file);
    }

    @Test(expected = IllegalArgumentException.class) 
    public void testImportarRespuestasEncuestaNoTienePregunta() {
        File file = new File("src/test/resources/test_respuestas.csv");
        PreguntaLibre mockPregunta = mock(PreguntaLibre.class);
        Usuario mockUsuario = mock(Usuario.class);
        Respuesta mockRespuesta = mock(Respuesta.class);
        when(mockCtrlDominioEncuesta.existeEncuesta(1)).thenReturn(true);
        when(mockCtrlDominioPregunta.existePregunta(1)).thenReturn(true);
        when(mockCtrlDominioUsuario.existeUsuario("user1")).thenReturn(true);
        when(mockCtrlDominioPregunta.getPregunta(1)).thenReturn(mockPregunta);
        when(mockPregunta.getIdEncuesta()).thenReturn(2);
        
        ctrlDominioRespuesta.importarRespuestas(file);
    }

    @Test
    public void testGetAllRespuestas() {
        ArrayList<Respuesta> lista = new ArrayList<>();
        Respuesta r = mock(RespuestaLibre.class);
        when(r.getId()).thenReturn(1);
        lista.add(r);
        when(mockGestor.getRespuestas()).thenReturn(lista);

        HashMap<Integer, Respuesta> resultado = ctrlDominioRespuesta.getAllRespuestas();

        assertEquals("No ha sacado todas las respuestas", 1, resultado.size());
        assertTrue(resultado.containsKey(1));
        verify(mockGestor).getRespuestas();
    }
}
