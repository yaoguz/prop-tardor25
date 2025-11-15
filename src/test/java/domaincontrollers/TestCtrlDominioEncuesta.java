package domaincontroller;

import main.domini.*;
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
import domaincontrollers.CtrlEncuesta;

import classcontroller.CtrlEncuesta;
import persistence.CtrlPersistence;
import domain.Encuesta;
import domain.Usuario;
import domain.Pregunta;
import domain.Respuesta;
import domaincontrollers.CtrlUsuario;
import domaincontrollers.CtrlPregunta;
import domaincontrollers.CtrlRespuesta;

/**
 * @author Xiangjun Yepan
 */
@RunWith(MockitoJUnitRunner.class)
public class TestCtrlDominioEncuesta {
    
    // @Mock
    // private CtrlUsuario mockCtrlUsuario;
    
    // @Mock
    // private CtrlEncuesta mockCtrlEncuesta;
    
    // @Mock
    // private CtrlPregunta mockCtrlPregunta;
    
    // @Mock
    // private CtrlRespuesta mockCtrlRespuesta;
    @Mock
    private CtrlEncuesta mockCtrlEncuesta;
    @Mock 
    private CtrlUsuario mockCtrlUsuario;
    @Mock
    private CtrlDominioUsuario ctrlDominioUsuario;
    @Mock
    private CtrlPersistence mockPersistence;

    private CtrlDominioEncuesta ctrlDominioEncuesta;
    
    @Before
    public void setUp() {
        // Inicializar mocks de Mockito
        MockitoAnnotations.initMocks(this);
        
        // Crear el controlador bajo prueba con dependencias mockeadas
        ctrlDominioEncuesta = new CtrlDominioEncuesta();
        ctrlDominioEncuesta.cargarEncuestas(mockEncuestas);
        mockUser = mock(Usuario.class);

        when(mockUser.get("Xiangjun")).thenReturn(mockUser);
        when(mockUser.getNomUsuario()).thenReturn("Xiangjun");
    }

    @Test
    public void TestExisteEncuestaTrue() {
        when(mockEncuestas.containsKey(1)).thenReturn(true);

        boolean resultado = ctrlDominioEncuesta.existeEncuesta(1);

        assertTrue("No muestra una Encuesta existente", resultado);
        verify(mockEncuestas, times(1)).containsKey(1); // verificar interacción
	}

    @Test 
    public void TestExisteEncuestaFalse() {
        when(mockEncuestas.containsKey(1)).thenReturn(true);

        boolean resultado = ctrlDominioEncuesta.existeEncuesta(2);

        assertFalse("Muestra un Encuesta No existente", resultado);
        verify(mockEncuestas, times(1)).containsKey(2); // verificar interacción
    }

    @Test
    public void testGetEncuestaSinError() {

        Encuesta e = new Encuesta(1, "Prueba", u.getNomUsuario());
        when(mockEncuestas.get(1)).thenReturn(e);
        when

        Encuesta resultado = ctrlDominioEncuesta.getEncuesta(1);

        // Assert
        assertEquals("Ha sacado un Encuesta incorrecto", e, resultado);   // compara que es la misma Encuesta
        verify(mockEncuestas, times(1)).get(1);
	}

    @Test (expected=IllegalStateException.class) 
    public void testGetEncuestaConError() {
            
        Encuesta e = new Encuesta( 1, "Prueba", "Xiangjun");
        when(mockEncuestas.get(1)).thenReturn(null);

        Encuesta resultado = ctrlDominioEncuesta.getEncuesta(1);
    }

    @Test void testGetPreguntasEncuesta() {
        Encuesta e = new Encuesta(1, "Prueba", "Xiangjun");
        ArrayList<Integer> preguntas = new ArrayList<>(Arrays.asList(1, 2, 3));
        for (Integer p : preguntas) {
            e.guardarPregunta(p);
        }
        when(mockEncuestas.get(1)).thenReturn(e);

        ArrayList<Integer> resultado = ctrlDominioEncuesta.getIdPreguntasEncuesta(1);

        // Assert
        assertEquals("No ha sacado las preguntas correctas", preguntas, resultado);   // compara que es la misma lista
        verify(mockEncuestas, times(1)).get(1);
    }

    @Test void testCrearEncuestaSinError() {
        int idEncuesta = ctrlDominioEncuesta.crearEncuesta("Xiangjun", "Prueba", ctrlDominioUsuario);
        Encuesta e = ctrlDominioEncuesta.getEncuesta(idEncuesta);
        // Assert
        assertEquals("No ha creado la encuesta con el creador correcto", "Xiangjun", e.getIdCreador());
        assertEquals("No ha creado la encuesta con el título correcto", "Prueba", e.getTitulo());
        assertEquals("No ha creado la encuesta con el id correcto", idEncuesta, e.getIdEncuesta());
        verify(mockEncuestas, times(1)).put(eq(idEncuesta), any(Encuesta.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testCrearEncuestaTituloVacio() {

        ctrlDominioEncuesta.crearEncuesta("Xiangjun", "", ctrlDominioUsuario);
    }

    @Test void testImportarEncuesta() {
        int idEncuesta = ctrlDominioEncuesta.importarEncuesta(file, ctrlDominioUsuario, ctrlDominioPregunta);

        Encuesta resultado = ctrlDominioEncuesta.getEncuesta(idEncuesta);

        // Assert
        assertEquals("No ha importado la encuesta correctamente", idEncuesta, resultado.getIdEncuesta());   // compara que es la misma Encuesta
        verify(mockEncuestas, times(1)).put(eq(idEncuesta), any(Encuesta.class));
    }

    @Test void testModificarTituloEncuestaSinError() {
        Encuesta e = new Encuesta(1, "Prueba", "Xiangjun");
        when(mockEncuestas.get(1)).thenReturn(e);

        ctrlDominioEncuesta.modificarTituloEncuesta(1, "NuevoTitulo");

        // Assert
        assertEquals("No ha modificado el título correctamente", "NuevoTitulo", e.getTitulo());   // compara que es la misma Encuesta
        verify(mockEncuestas, times(1)).get(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testModificarTituloEncuestaTituloVacio() {

        ctrlDominioEncuesta.modificarTituloEncuesta(1, "");
    }

    @Test void testEliminarEncuesta() {
        Encuesta e = new Encuesta(1, "Prueba", "Xiangjun");
        ArrayList<Integer> preguntas = new ArrayList<>(Arrays.asList(1, 2, 3));
        for (Integer p : preguntas) {
            e.guardarPregunta(p);
        }
        when(mockEncuestas.get(1)).thenReturn(e);

        ctrlDominioEncuesta.eliminarEncuesta(1, ctrlDominioPregunta);

        // Assert
        verify(mockEncuestas, times(1)).get(1);
        verify(mockEncuestas, times(1)).remove(1);
    }

    @Test void testGetRespuestasEncuesta() {
        Encuesta e = new Encuesta(1, "Prueba", "Xiangjun");
        ArrayList<Integer> preguntas = new ArrayList<>(Arrays.asList(1, 2));
        for (Integer p : preguntas) {
            e.guardarPregunta(p);
        }
        when(mockEncuestas.get(1)).thenReturn(e);

        Pregunta p1 = mock(Pregunta.class);
        Pregunta p2 = mock(Pregunta.class);
        when(ctrlDominioPregunta.getPregunta(1)).thenReturn(p1);
        when(ctrlDominioPregunta.getPregunta(2)).thenReturn(p2);

        ArrayList<Integer> respuestasP1 = new ArrayList<>(Arrays.asList(10, 11));
        ArrayList<Integer> respuestasP2 = new ArrayList<>(Arrays.asList(20));
        when(p1.getIdRespuestas()).thenReturn(respuestasP1);
        when(p2.getIdRespuestas()).thenReturn(respuestasP2);

        Respuesta r10 = mock(Respuesta.class);
        Respuesta r11 = mock(Respuesta.class);
        Respuesta r20 = mock(Respuesta.class);
        when(ctrlDominioRespuesta.getRespuesta(10)).thenReturn(r10);
        when(ctrlDominioRespuesta.getRespuesta(11)).thenReturn(r11);
        when(ctrlDominioRespuesta.getRespuesta(20)).thenReturn(r20);

        Map<Pregunta, Set<Respuesta>> resultado = 
            ctrlDominioEncuesta.getRespuestasEncuesta(1);

        // Assert
        assertNotNull("El mapa de respuestas es nulo", resultado);
        assertEquals("El mapa de respuestas tiene tamaño incorrecto", 2, resultado.size());
        assertTrue("No contiene la pregunta 1", resultado.containsKey(p1));
        assertTrue("No contiene la pregunta 2", resultado.containsKey(p2));
        
        // VERIFY
        verify(mockEncuestas).get(1);
        verify(ctrlDominioPregunta, times(2)).getPregunta(anyInt());
    }

/////////////////////////////////////////////////////////////////////////////////
    /**
     * Test: Crear encuesta con usuario válido debe crear y guardar encuesta.
     */
    @Test
    public void testCrearEncuesta_UsuarioValido_CreaYGuardaEncuesta() {
        // ARRANGE - Preparar datos y configurar mocks
        String nomUsuario = "testUser";
        String titulo = "Encuesta de Prueba";
        int idEncuestaEsperado = 1;
        
        Usuario mockUsuario = mock(Usuario.class);
        Encuesta mockEncuesta = mock(Encuesta.class);
        
        when(mockCtrlUsuario.getUsuario(nomUsuario)).thenReturn(mockUsuario);
        when(mockCtrlEncuesta.crearEncuesta(nomUsuario, titulo)).thenReturn(mockEncuesta);
        when(mockEncuesta.getId()).thenReturn(idEncuestaEsperado);
        
        // ACT - Ejecutar método bajo prueba
        int idEncuestaCreado = ctrlDominioEncuesta.crearEncuesta(nomUsuario, titulo);
        
        // ASSERT - Verificar resultado
        assertEquals(idEncuestaEsperado, idEncuestaCreado);
        
        // VERIFY - Verificar interacciones con mocks
        verify(mockCtrlUsuario, times(1)).getUsuario(nomUsuario);
        verify(mockCtrlEncuesta, times(1)).crearEncuesta(nomUsuario, titulo);
        verify(mockUsuario, times(1)).guardarIdEncuesta(idEncuestaEsperado);
        verify(mockPersistence, times(1)).guardarEncuesta(mockEncuesta);
    }
    
    /**
     * Test: Crear encuesta con usuario inexistente debe lanzar excepción.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCrearEncuesta_UsuarioInexistente_LanzaExcepcion() {
        // ARRANGE
        String nomUsuario = "nonExistentUser";
        String titulo = "Encuesta de Prueba";
        
        when(mockCtrlUsuario.getUsuario(nomUsuario)).thenReturn(null);
        
        // ACT - Debería lanzar excepción
        ctrlDominioEncuesta.crearEncuesta(nomUsuario, titulo);
        
        // VERIFY - No debería haber intentado crear encuesta
        verify(mockCtrlEncuesta, never()).crearEncuesta(anyString(), anyString());
        verify(mockPersistence, never()).guardarEncuesta(any(Encuesta.class));
    }
    
    /**
     * Test: Crear encuesta con título vacío debe lanzar excepción.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCrearEncuesta_TituloVacio_LanzaExcepcion() {
        // ARRANGE
        String nomUsuario = "testUser";
        String titulo = "";
        
        Usuario mockUsuario = mock(Usuario.class);
        when(mockCtrlUsuario.getUsuario(nomUsuario)).thenReturn(mockUsuario);
        
        // ACT - Debería lanzar excepción
        ctrlDominioEncuesta.crearEncuesta(nomUsuario, titulo);
    }
    
    // ==================== TESTS DE MODIFICAR TÍTULO ====================
    
    /**
     * Test: Modificar título de encuesta existente debe actualizar y guardar.
     */
    @Test
    public void testModificarTituloEncuesta_EncuestaExistente_ActualizaYGuarda() {
        // ARRANGE
        int idEncuesta = 1;
        String nuevoTitulo = "Nuevo Título";
        
        Encuesta mockEncuesta = mock(Encuesta.class);
        when(mockCtrlEncuesta.getEncuesta(idEncuesta)).thenReturn(mockEncuesta);
        
        // ACT
        ctrlDominioEncuesta.modificarTituloEncuesta(idEncuesta, nuevoTitulo);
        
        // VERIFY
        verify(mockCtrlEncuesta).getEncuesta(idEncuesta);
        verify(mockEncuesta).modificarTitulo(nuevoTitulo);
        verify(mockPersistence).guardarEncuesta(mockEncuesta);
    }
    
    /**
     * Test: Modificar título de encuesta inexistente debe lanzar excepción.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testModificarTituloEncuesta_EncuestaInexistente_LanzaExcepcion() {
        // ARRANGE
        int idEncuesta = 999;
        String nuevoTitulo = "Nuevo Título";
        
        when(mockCtrlEncuesta.getEncuesta(idEncuesta)).thenReturn(null);
        
        // ACT
        ctrlDominioEncuesta.modificarTituloEncuesta(idEncuesta, nuevoTitulo);
    }
    
    // ==================== TESTS DE ELIMINAR ENCUESTA ====================
    
    /**
     * Test: Eliminar encuesta debe eliminar sus preguntas y respuestas asociadas.
     */
    @Test
    public void testEliminarEncuesta_EliminaEncuestaPreguntasYRespuestas() {
        // ARRANGE
        int idEncuesta = 1;
        Encuesta mockEncuesta = mock(Encuesta.class);
        List<Integer> idPreguntas = Arrays.asList(1, 2, 3);
        
        when(mockCtrlEncuesta.getEncuesta(idEncuesta)).thenReturn(mockEncuesta);
        when(mockEncuesta.getIdPreguntas()).thenReturn(idPreguntas);
        
        // Mock de CtrlDominioPregunta (normalmente se inyectaría)
        CtrlDominioPregunta mockCtrlDominioPregunta = mock(CtrlDominioPregunta.class);
        
        // ACT
        ctrlDominioEncuesta.eliminarEncuesta(idEncuesta, mockCtrlDominioPregunta);
        
        // VERIFY
        verify(mockCtrlEncuesta).getEncuesta(idEncuesta);
        verify(mockEncuesta).getIdPreguntas();
        
        // Verificar que se eliminó cada pregunta
        for (Integer idPregunta : idPreguntas) {
            verify(mockCtrlDominioPregunta).eliminarPregunta(idPregunta);
        }
        
        verify(mockPersistence).eliminarEncuesta(idEncuesta);
        verify(mockCtrlEncuesta).eliminar(mockEncuesta);
    }
    
    // ==================== TESTS DE GET PREGUNTAS ====================
    
    /**
     * Test: Obtener preguntas de encuesta existente devuelve lista correcta.
     */
    @Test
    public void testGetPreguntasEncuesta_EncuestaExistente_RetornaPreguntas() {
        // ARRANGE
        int idEncuesta = 1;
        List<Integer> preguntasEsperadas = Arrays.asList(1, 2, 3, 4, 5);
        
        Encuesta mockEncuesta = mock(Encuesta.class);
        when(mockCtrlEncuesta.getEncuesta(idEncuesta)).thenReturn(mockEncuesta);
        when(mockEncuesta.getIdPreguntas()).thenReturn(preguntasEsperadas);
        
        // ACT
        List<Integer> preguntasObtenidas = ctrlDominioEncuesta.getPreguntasEncuesta(idEncuesta);
        
        // ASSERT
        assertNotNull(preguntasObtenidas);
        assertEquals(preguntasEsperadas.size(), preguntasObtenidas.size());
        assertEquals(preguntasEsperadas, preguntasObtenidas);
        
        // VERIFY
        verify(mockCtrlEncuesta).getEncuesta(idEncuesta);
        verify(mockEncuesta).getIdPreguntas();
    }
    
    /**
     * Test: Obtener preguntas de encuesta vacía devuelve lista vacía.
     */
    @Test
    public void testGetPreguntasEncuesta_EncuestaVacia_RetornaListaVacia() {
        // ARRANGE
        int idEncuesta = 1;
        List<Integer> preguntasVacias = new ArrayList<>();
        
        Encuesta mockEncuesta = mock(Encuesta.class);
        when(mockCtrlEncuesta.getEncuesta(idEncuesta)).thenReturn(mockEncuesta);
        when(mockEncuesta.getIdPreguntas()).thenReturn(preguntasVacias);
        
        // ACT
        List<Integer> preguntasObtenidas = ctrlDominioEncuesta.getPreguntasEncuesta(idEncuesta);
        
        // ASSERT
        assertNotNull(preguntasObtenidas);
        assertTrue(preguntasObtenidas.isEmpty());
    }
    
    // ==================== TESTS DE GET RESPUESTAS ENCUESTA ====================
    
    /**
     * Test: Obtener respuestas de encuesta devuelve mapa correcto.
     */
    @Test
    public void testGetRespuestasEncuesta_RetornaMapaDeRespuestas() {
        // ARRANGE
        int idEncuesta = 1;
        List<Integer> idPreguntas = Arrays.asList(1, 2);
        
        Encuesta mockEncuesta = mock(Encuesta.class);
        Pregunta mockPregunta1 = mock(Pregunta.class);
        Pregunta mockPregunta2 = mock(Pregunta.class);
        
        when(mockCtrlEncuesta.getEncuesta(idEncuesta)).thenReturn(mockEncuesta);
        when(mockEncuesta.getIdPreguntas()).thenReturn(idPreguntas);
        when(mockCtrlPregunta.getPregunta(1)).thenReturn(mockPregunta1);
        when(mockCtrlPregunta.getPregunta(2)).thenReturn(mockPregunta2);
        
        List<Integer> idRespuestas1 = Arrays.asList(10, 11);
        List<Integer> idRespuestas2 = Arrays.asList(20);
        when(mockPregunta1.getIdRespuestas()).thenReturn(idRespuestas1);
        when(mockPregunta2.getIdRespuestas()).thenReturn(idRespuestas2);
        
        Respuesta mockResp10 = mock(Respuesta.class);
        Respuesta mockResp11 = mock(Respuesta.class);
        Respuesta mockResp20 = mock(Respuesta.class);
        when(mockCtrlRespuesta.getRespuesta(10)).thenReturn(mockResp10);
        when(mockCtrlRespuesta.getRespuesta(11)).thenReturn(mockResp11);
        when(mockCtrlRespuesta.getRespuesta(20)).thenReturn(mockResp20);
        
        // ACT
        Map<Pregunta, Set<Respuesta>> resultado = 
            ctrlDominioEncuesta.getRespuestasEncuesta(idEncuesta);
        
        // ASSERT
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        assertTrue(resultado.containsKey(mockPregunta1));
        assertTrue(resultado.containsKey(mockPregunta2));
        
        // VERIFY
        verify(mockCtrlEncuesta).getEncuesta(idEncuesta);
        verify(mockCtrlPregunta, times(2)).getPregunta(anyInt());
    }
}
