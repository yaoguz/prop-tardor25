package domaincontroller;

import main.domini.*;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import classcontroller.CtrlUsuario;
import classcontroller.CtrlEncuesta;
import classcontroller.CtrlPregunta;
import classcontroller.CtrlRespuesta;
import persistence.ICtrlPersistence;
import domain.Usuario;
import domain.Encuesta;
import domain.Pregunta;
import domain.Respuesta;

/**
 * Tests unitarios para CtrlDominioEncuesta usando MOCKS con JUnit 4.
 * 
 * Demuestra cómo usar Mockito para simular dependencias y verificar interacciones.
 * 
 * @author Equipo PROP
 * @version 1.0 - JUnit 4
 */
@RunWith(MockitoJUnitRunner.class)
public class TestCtrlDominioEncuesta {
    
    @Mock
    private CtrlUsuario mockCtrlUsuario;
    
    @Mock
    private CtrlEncuesta mockCtrlEncuesta;
    
    @Mock
    private CtrlPregunta mockCtrlPregunta;
    
    @Mock
    private CtrlRespuesta mockCtrlRespuesta;
    
    @Mock
    private ICtrlPersistence mockPersistence;
    
    private CtrlDominioEncuesta ctrlDominioEncuesta;
    
    /**
     * Configuración inicial antes de cada test.
     * Inicializa los mocks y el controlador bajo prueba.
     */
    @Before
    public void setUp() {
        // Inicializar mocks de Mockito
        MockitoAnnotations.initMocks(this);
        
        // Crear el controlador bajo prueba con dependencias mockeadas
        ctrlDominioEncuesta = new CtrlDominioEncuesta(
            mockCtrlUsuario,
            mockCtrlEncuesta,
            mockCtrlPregunta,
            mockCtrlRespuesta,
            mockPersistence
        );
    }
    
    // ==================== TESTS DE CREAR ENCUESTA ====================
    
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
