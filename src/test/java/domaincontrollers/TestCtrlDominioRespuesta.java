package domaincontrollers;

import domain.*;
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

    /**
     * Objeto de la prueba: existeRespuesta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Verificación de existencia de respuestas
     * Operativa: Mocks retornan true/false, se verifica comportamiento correcto
     */
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

    /**
     * Objeto de la prueba: getRespuesta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Obtención correcta de respuesta por ID
     * Operativa: Mock retorna respuesta, se verifica obtención
     */
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

    /**
     * Objeto de la prueba: darRespuesta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Creación correcta de respuesta a pregunta
     * Operativa: Mocks de pregunta y usuario, se crea respuesta y se verifican actualizaciones
     */
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

    /**
     * Objeto de la prueba: modificarRespuesta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Modificación correcta del contenido de respuesta
     * Operativa: Mock de respuesta, se modifica contenido y se verifica actualización
     */
    @Test
    public void testModificarRespuesta() {
        RespuestaLibre mockRespuesta = mock(RespuestaLibre.class);
        when(mockGestor.getRespuesta(1)).thenReturn(mockRespuesta);

        ctrlDominioRespuesta.modificarRespuesta(1, "Nueva respuesta");

        verify(mockRespuesta).modificar("Nueva respuesta");
        verify(mockGestor).actualizarRespuesta(mockRespuesta);
    }

    /**
     * Objeto de la prueba: eliminarRespuesta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Eliminación correcta de respuesta y sus referencias
     * Operativa: Mocks configurados, se elimina respuesta y se verifican eliminaciones en usuario y pregunta
     */
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

    /**
     * Objeto de la prueba: importarRespuestas()
     * Ficheros de datos necesarios: src/test/resources/test_respuestas.csv
     * Valores estudiados: Importación correcta de respuestas desde CSV
     * Operativa: Mocks de encuesta, pregunta y usuario, se importan respuestas y se verifican creaciones
     */
    @Test 
    public void testImportarRespuestas() {
        File file = new File("src/test/resources/test_respuestas.csv");
        PreguntaLibre mockPregunta = mock(PreguntaLibre.class);
        Encuesta mockEncuesta = mock(Encuesta.class);
        Usuario mockUsuario = mock(Usuario.class);
        Respuesta mockRespuesta = mock(Respuesta.class);
        
        when(mockCtrlDominioEncuesta.getEncuesta("Test Survey", "test")).thenReturn(mockEncuesta);
        when(mockEncuesta.getIdEncuesta()).thenReturn(1);
        when(mockCtrlDominioEncuesta.getIdPreguntasEncuesta(1)).thenReturn(List.of(1));
        when(mockCtrlDominioEncuesta.existeEncuesta(1)).thenReturn(true);
        when(mockCtrlDominioPregunta.existePregunta(1)).thenReturn(true);
        when(mockCtrlDominioUsuario.existeUsuario("user1")).thenReturn(true);
        when(mockCtrlDominioPregunta.getPregunta(1)).thenReturn(mockPregunta);
        when(mockPregunta.getIdEncuesta()).thenReturn(1);
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

    /**
     * Objeto de la prueba: importarRespuestas()
     * Ficheros de datos necesarios: src/test/resources/invalid_respuestas.csv
     * Valores estudiados: Excepción cuando la encuesta referenciada no existe
     * Operativa: Mock lanza excepción al buscar encuesta inexistente
     */
    @Test(expected = IllegalArgumentException.class) 
    public void testImportarRespuestasEncuestaNoExiste() {
        File file = new File("src/test/resources/invalid_respuestas.csv");
        when(mockCtrlDominioEncuesta.getEncuesta("Invalid Survey", "test")).thenThrow(new IllegalArgumentException("Encuesta no encontrada"));
        ctrlDominioRespuesta.importarRespuestas(file);
    }

    /**
     * Objeto de la prueba: importarRespuestas()
     * Ficheros de datos necesarios: src/test/resources/invalid_respuestas.csv
     * Valores estudiados: Excepción cuando la pregunta referenciada no existe
     * Operativa: Mocks configurados para validación fallida de pregunta inexistente
     */
    @Test
    public void testImportarRespuestasPreguntaNoExiste() {
        File file = new File("src/test/resources/invalid_respuestas.csv");
        Encuesta mockEncuesta = mock(Encuesta.class);
        when(mockCtrlDominioEncuesta.getEncuesta("Invalid Survey", "test")).thenReturn(mockEncuesta);
        when(mockEncuesta.getIdEncuesta()).thenReturn(1);
        when(mockCtrlDominioEncuesta.getIdPreguntasEncuesta(1)).thenReturn(List.of(1));
        when(mockCtrlDominioUsuario.existeUsuario("user1")).thenReturn(false);
        when(mockCtrlDominioUsuario.registrarUsuario("user1", "123456", "123456")).thenReturn("user1");
        
        // Mock pregunta for parsing
        PreguntaLibre mockPregunta = mock(PreguntaLibre.class);
        when(mockCtrlDominioPregunta.getPregunta(1)).thenReturn(mockPregunta);
        
        // After parsing, the validation will fail
        when(mockCtrlDominioEncuesta.existeEncuesta(1)).thenReturn(true);
        when(mockCtrlDominioPregunta.existePregunta(1)).thenReturn(false);
        
        try {
            ctrlDominioRespuesta.importarRespuestas(file);
            fail("Se esperaba IllegalStateException");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("Pregunta con ID 1 no existe"));
        }
    }

    /**
     * Objeto de la prueba: importarRespuestas()
     * Ficheros de datos necesarios: src/test/resources/test_respuestas.csv
     * Valores estudiados: Excepción cuando la pregunta no pertenece a la encuesta
     * Operativa: Mocks configurados con pregunta de encuesta diferente
     */
    @Test
    public void testImportarRespuestasEncuestaNoTienePregunta() {
        File file = new File("src/test/resources/test_respuestas.csv");
        PreguntaLibre mockPregunta = mock(PreguntaLibre.class);
        Encuesta mockEncuesta = mock(Encuesta.class);
        when(mockCtrlDominioEncuesta.getEncuesta("Test Survey", "test")).thenReturn(mockEncuesta);
        when(mockEncuesta.getIdEncuesta()).thenReturn(1);
        when(mockCtrlDominioEncuesta.getIdPreguntasEncuesta(1)).thenReturn(List.of(1));
        when(mockCtrlDominioEncuesta.existeEncuesta(1)).thenReturn(true);
        when(mockCtrlDominioPregunta.existePregunta(1)).thenReturn(true);
        when(mockCtrlDominioPregunta.getPregunta(1)).thenReturn(mockPregunta);
        when(mockPregunta.getIdEncuesta()).thenReturn(2);
        when(mockCtrlDominioUsuario.existeUsuario("user1")).thenReturn(false);
        when(mockCtrlDominioUsuario.registrarUsuario("user1", "123456", "123456")).thenReturn("user1");
        
        try {
            ctrlDominioRespuesta.importarRespuestas(file);
            fail("Se esperaba IllegalStateException");
        } catch (IllegalStateException e) {
            assertTrue(e.getMessage().contains("no pertenece a la encuesta"));
        }
    }

    /**
     * Objeto de la prueba: getAllRespuestas()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Obtención de todas las respuestas como HashMap
     * Operativa: Mock retorna lista de respuestas, se verifica conversión a HashMap
     */
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
