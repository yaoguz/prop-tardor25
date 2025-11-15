package domaincontrollers;

import java.io.File;
import java.lang.reflect.Field;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import domaincontrollers.CtrlDominioEncuesta;
import domaincontrollers.CtrlDominioPregunta;
import domaincontrollers.CtrlDominioRespuesta;
import domaincontrollers.CtrlDominioUsuario;
import persistence.GestorPersistencia;
import domain.Encuesta;
import domain.Usuario;
import domain.Pregunta;
import domain.Respuesta;

/**
 * @author Xiangjun Yepan
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class TestCtrlDominioEncuesta {
    
    @Mock
    private GestorPersistencia mockGestor;
    @Mock
    private CtrlDominioUsuario mockCtrlDominioUsuario;
    @Mock
    private CtrlDominioPregunta mockCtrlDominioPregunta;
    @Mock
    private CtrlDominioRespuesta mockCtrlDominioRespuesta;

    private CtrlDominioEncuesta ctrlDominioEncuesta;
    
    @Before
    public void setUp() throws Exception {
        // Resetear singleton
        Field instance = CtrlDominioEncuesta.class.getDeclaredField("singletonInstance");
        instance.setAccessible(true);
        instance.set(null, null);
        
        ctrlDominioEncuesta = CtrlDominioEncuesta.getInstance(mockGestor, mockCtrlDominioPregunta, mockCtrlDominioRespuesta, mockCtrlDominioUsuario);
        Usuario mockUser = mock(Usuario.class);
        when(mockUser.getNomUsuario()).thenReturn("Xiangjun");
        when(mockCtrlDominioUsuario.getUsuario("Xiangjun")).thenReturn(mockUser);
    }

    @Test
    public void TestExisteEncuestaTrue() {
        when(mockGestor.existeEncuesta(1)).thenReturn(true);

        boolean resultado = ctrlDominioEncuesta.existeEncuesta(1);

        assertTrue("No muestra una Encuesta existente", resultado);
        verify(mockGestor, times(1)).existeEncuesta(1); 
	}

    @Test 
    public void TestExisteEncuestaFalse() {
        when(mockGestor.existeEncuesta(1)).thenReturn(false);

        boolean resultado = ctrlDominioEncuesta.existeEncuesta(1);

        assertFalse("Muestra un Encuesta No existente", resultado);
        verify(mockGestor, times(1)).existeEncuesta(1); 
    }

    @Test
    public void testGetEncuestaSinError() {
        Encuesta mockEncuesta = mock(Encuesta.class);
        when(mockGestor.getEncuesta(1)).thenReturn(mockEncuesta);

        Encuesta resultado = ctrlDominioEncuesta.getEncuesta(1);

        assertEquals("Ha sacado un Encuesta incorrecto", mockEncuesta, resultado);   
        verify(mockGestor, times(1)).getEncuesta(1);
	}

    @Test (expected=IllegalStateException.class) 
    public void testGetEncuestaConError() {
        when(mockGestor.getEncuesta(1)).thenReturn(null);

        ctrlDominioEncuesta.getEncuesta(1);
    }

    @Test 
    public void testGetAllEncuestas() {
        ArrayList<Encuesta> lista = new ArrayList<>();
        Encuesta mockEncuesta = mock(Encuesta.class);
        lista.add(mockEncuesta);
        when(mockGestor.getEncuestas()).thenReturn(lista);
        when(mockEncuesta.getIdEncuesta()).thenReturn(1);

        HashMap<Integer, Encuesta> resultado = ctrlDominioEncuesta.getAllEncuestas();

        assertEquals("No ha sacado todas las encuestas correctamente", 1, resultado.size());
        assertTrue(resultado.containsKey(1));
        verify(mockGestor, times(1)).getEncuestas();
    }

    @Test 
    public void testGetIdPreguntasEncuesta() {
        Encuesta mockEncuesta = mock(Encuesta.class);
        ArrayList<Integer> idPreguntas = new ArrayList<>(Arrays.asList(1, 2, 3));
        when(mockGestor.getEncuesta(1)).thenReturn(mockEncuesta);
        when(mockEncuesta.getIdPreguntas()).thenReturn(idPreguntas);

        List<Integer> resultado = ctrlDominioEncuesta.getIdPreguntasEncuesta(1);

        assertEquals("No ha sacado las preguntas correctas", idPreguntas, resultado);  
        verify(mockGestor, times(1)).getEncuesta(1);
    }

    @Test 
    public void testGetPreguntasEncuesta() {
        Encuesta mockEncuesta = mock(Encuesta.class);
        Pregunta mockPregunta = mock(Pregunta.class);
        ArrayList<Integer> idPreguntas = new ArrayList<>(Arrays.asList(1));
        when(mockGestor.getEncuesta(1)).thenReturn(mockEncuesta);
        when(mockEncuesta.getIdPreguntas()).thenReturn(idPreguntas);
        when(mockCtrlDominioPregunta.getPregunta(1)).thenReturn(mockPregunta);

        List<Pregunta> resultado = ctrlDominioEncuesta.getPreguntasEncuesta(1);

        assertEquals("No ha sacado las preguntas correctas", 1, resultado.size());  
        verify(mockGestor, times(1)).getEncuesta(1);
        verify(mockCtrlDominioPregunta, times(1)).getPregunta(1);
    }

    @Test 
    public void testCrearEncuestaSinError() {
        Usuario mockUsuario = mock(Usuario.class);
        when(mockUsuario.getNomUsuario()).thenReturn("Xiangjun");
        when(mockCtrlDominioUsuario.getUsuario("Xiangjun")).thenReturn(mockUsuario);
        when(mockGestor.getNextIdEncuesta()).thenReturn(1);
        
        int idEncuesta = ctrlDominioEncuesta.crearEncuesta("Xiangjun", "Prueba");
        
        assertEquals("No ha creado la encuesta con el id correcto", 1, idEncuesta);
        verify(mockGestor).guardarEncuesta(any(Encuesta.class));
        verify(mockGestor).actualizarUsuario(mockUsuario);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrearEncuestaTituloVacio() {
        ctrlDominioEncuesta.crearEncuesta("Xiangjun", "");
    }


    @Test 
    public void testModificarTituloEncuestaSinError() {
        Encuesta e = new Encuesta(1, "Prueba", "Xiangjun");
        when(mockGestor.getEncuesta(1)).thenReturn(e);

        ctrlDominioEncuesta.modificarTituloEncuesta(1, "NuevoTitulo");

        assertEquals("No ha modificado el título correctamente", "NuevoTitulo", e.getTitulo());
        verify(mockGestor, times(1)).getEncuesta(1);
        verify(mockGestor, times(1)).actualizarEncuesta(e);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testModificarTituloEncuestaTituloVacio() {
        ctrlDominioEncuesta.modificarTituloEncuesta(1, "");
    }

    @Test 
    public void testEliminarEncuesta() {
        Usuario mockUsuario = mock(Usuario.class);
        Encuesta e = new Encuesta(1, "Prueba", "Xiangjun");
        ArrayList<Integer> preguntas = new ArrayList<>(Arrays.asList(1, 2, 3));
        for (Integer p : preguntas) {
            e.guardarPregunta(p);
        }
        when(mockGestor.getEncuesta(1)).thenReturn(e);
        when(mockCtrlDominioUsuario.getUsuario("Xiangjun")).thenReturn(mockUsuario);

        ctrlDominioEncuesta.eliminarEncuesta(1);

        verify(mockGestor).getEncuesta(1);
        verify(mockCtrlDominioPregunta, times(3)).eliminarPregunta(anyInt());
        verify(mockCtrlDominioUsuario).getUsuario("Xiangjun");
        verify(mockGestor).eliminarEncuesta(1);
    }

    @Test 
    public void testGetRespuestasEncuesta() {
        Encuesta e = new Encuesta(1, "Prueba", "Xiangjun");
        ArrayList<Integer> preguntas = new ArrayList<>(Arrays.asList(1, 2));
        for (Integer p : preguntas) {
            e.guardarPregunta(p);
        }
        when(mockGestor.getEncuesta(1)).thenReturn(e);

        Pregunta p1 = mock(Pregunta.class);
        Pregunta p2 = mock(Pregunta.class);
        when(mockCtrlDominioPregunta.getPregunta(1)).thenReturn(p1);
        when(mockCtrlDominioPregunta.getPregunta(2)).thenReturn(p2);

        ArrayList<Integer> respuestasP1 = new ArrayList<>(Arrays.asList(10, 11));
        ArrayList<Integer> respuestasP2 = new ArrayList<>(Arrays.asList(20));
        when(p1.getIdRespuestas()).thenReturn(respuestasP1);
        when(p2.getIdRespuestas()).thenReturn(respuestasP2);

        Respuesta r10 = mock(Respuesta.class);
        Respuesta r11 = mock(Respuesta.class);
        Respuesta r20 = mock(Respuesta.class);
        when(mockCtrlDominioRespuesta.getRespuesta(10)).thenReturn(r10);
        when(mockCtrlDominioRespuesta.getRespuesta(11)).thenReturn(r11);
        when(mockCtrlDominioRespuesta.getRespuesta(20)).thenReturn(r20);

        Map<Pregunta, Set<Respuesta>> resultado = 
            ctrlDominioEncuesta.getRespuestasEncuesta(1);

        assertNotNull("El mapa de respuestas es nulo", resultado);
        assertEquals("El mapa de respuestas tiene tamaño incorrecto", 2, resultado.size());
        assertTrue("No contiene la pregunta 1", resultado.containsKey(p1));
        assertTrue("No contiene la pregunta 2", resultado.containsKey(p2));
        
        verify(mockGestor).getEncuesta(1);
        verify(mockCtrlDominioPregunta, times(2)).getPregunta(anyInt());
        verify(mockCtrlDominioRespuesta, times(3)).getRespuesta(anyInt());
    }

    @Test
    public void testGetEncuestaPorTituloYAutor() {
        Usuario mockUsuario = mock(Usuario.class);
        Encuesta e = new Encuesta(1, "Prueba", "Xiangjun");
        ArrayList<Integer> idEncuestas = new ArrayList<>(Arrays.asList(1));
        when(mockCtrlDominioUsuario.getUsuario("Xiangjun")).thenReturn(mockUsuario);
        when(mockUsuario.getIdEncuestasCreadas()).thenReturn(idEncuestas);
        when(mockGestor.getEncuesta(1)).thenReturn(e);

        Encuesta resultado = ctrlDominioEncuesta.getEncuesta("Prueba", "Xiangjun");

        assertEquals("No ha sacado la encuesta correcta", e, resultado);
        verify(mockCtrlDominioUsuario).getUsuario("Xiangjun");
        verify(mockGestor).getEncuesta(1);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetEncuestaPorTituloYAutorNoExiste() {
        Usuario mockUsuario = mock(Usuario.class);
        ArrayList<Integer> idEncuestas = new ArrayList<>(Arrays.asList(1));
        Encuesta e = new Encuesta(1, "Otra", "Xiangjun");
        when(mockCtrlDominioUsuario.getUsuario("Xiangjun")).thenReturn(mockUsuario);
        when(mockUsuario.getIdEncuestasCreadas()).thenReturn(idEncuestas);
        when(mockGestor.getEncuesta(1)).thenReturn(e);

        ctrlDominioEncuesta.getEncuesta("NoExiste", "Xiangjun");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrearEncuestaTituloDuplicado() {
        Usuario mockUsuario = mock(Usuario.class);
        ArrayList<Integer> idEncuestas = new ArrayList<>(Arrays.asList(1));
        Encuesta e = new Encuesta(1, "Prueba", "Xiangjun");
        when(mockCtrlDominioUsuario.getUsuario("Xiangjun")).thenReturn(mockUsuario);
        when(mockUsuario.getIdEncuestasCreadas()).thenReturn(idEncuestas);
        when(mockGestor.getEncuesta(1)).thenReturn(e);

        ctrlDominioEncuesta.crearEncuesta("Xiangjun", "Prueba");
    }

    @Test
    public void testAnadirPregunta() {
        Encuesta e = new Encuesta(1, "Prueba", "Xiangjun");
        when(mockGestor.getEncuesta(1)).thenReturn(e);
        when(mockCtrlDominioPregunta.crearPregunta(eq(1), anyString(), anyString(), anyMap())).thenReturn(10);

        CtrlDominioEncuesta.PreguntaImportData infoP = new CtrlDominioEncuesta.PreguntaImportData();
        infoP.enunciado = "¿Pregunta?";
        infoP.tipo = "libre";
        infoP.atributosEspecificos = new HashMap<>();

        int idP = ctrlDominioEncuesta.anadirPregunta(1, infoP);

        assertEquals("No ha retornado el id correcto", 10, idP);
        verify(mockCtrlDominioPregunta).crearPregunta(1, "¿Pregunta?", "libre", infoP.atributosEspecificos);
        verify(mockGestor).actualizarEncuesta(e);
    }

    @Test
    public void testGetRespuestasParaClustering() {
        Encuesta e = new Encuesta(1, "Prueba", "Xiangjun");
        e.guardarPregunta(1);
        e.guardarPregunta(2);
        when(mockGestor.getEncuesta(1)).thenReturn(e);

        Pregunta p1 = mock(Pregunta.class);
        Pregunta p2 = mock(Pregunta.class);
        when(mockCtrlDominioPregunta.getPregunta(1)).thenReturn(p1);
        when(mockCtrlDominioPregunta.getPregunta(2)).thenReturn(p2);

        ArrayList<Integer> respuestasP1 = new ArrayList<>(Arrays.asList(10, 11));
        ArrayList<Integer> respuestasP2 = new ArrayList<>(Arrays.asList(20));
        when(p1.getIdRespuestas()).thenReturn(respuestasP1);
        when(p2.getIdRespuestas()).thenReturn(respuestasP2);

        Respuesta r10 = mock(Respuesta.class);
        Respuesta r11 = mock(Respuesta.class);
        Respuesta r20 = mock(Respuesta.class);
        when(r10.getNomUsuario()).thenReturn("user1");
        when(r11.getNomUsuario()).thenReturn("user2");
        when(r20.getNomUsuario()).thenReturn("user1");
        when(mockCtrlDominioRespuesta.getRespuesta(10)).thenReturn(r10);
        when(mockCtrlDominioRespuesta.getRespuesta(11)).thenReturn(r11);
        when(mockCtrlDominioRespuesta.getRespuesta(20)).thenReturn(r20);

        List<List<Respuesta>> resultado = ctrlDominioEncuesta.getRespuestasParaClustering(1);

        assertNotNull("La matriz es nula", resultado);
        assertEquals("La matriz tiene tamaño incorrecto", 2, resultado.size());
        verify(mockGestor).getEncuesta(1);
        verify(mockCtrlDominioPregunta, times(2)).getPregunta(anyInt());
        verify(mockCtrlDominioRespuesta, times(3)).getRespuesta(anyInt());
    }

    @Test(expected = IllegalStateException.class)
    public void testImportarEncuestaSinUsuarioActivo() {
        when(mockCtrlDominioUsuario.getUsuarioActivo()).thenReturn(null);
        File mockFile = mock(File.class);

        ctrlDominioEncuesta.importarEncuesta(mockFile);
    }
}
