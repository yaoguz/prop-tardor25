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
    }

    /**
     * Objeto de la prueba: existeEncuesta(idEncuesta)
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Caso en que la encuesta existe
     * Operativa: Mock de gestor retorna true, se verifica que el método retorna true y se llama al gestor
     */
    @Test
    public void TestExisteEncuestaIdEncuestaTrue() {
        when(mockGestor.existeEncuesta(1)).thenReturn(true);

        boolean resultado = ctrlDominioEncuesta.existeEncuesta(1);

        assertTrue("No muestra una Encuesta existente", resultado);
        verify(mockGestor, times(1)).existeEncuesta(1); 
	}

    /**
     * Objeto de la prueba: existeEncuesta(idEncuesta)
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Caso en que la encuesta no existe
     * Operativa: Mock de gestor retorna false, se verifica que el método retorna false
     */
    @Test 
    public void TestExisteEncuestaIdEncuestaFalse() {
        when(mockGestor.existeEncuesta(1)).thenReturn(false);

        boolean resultado = ctrlDominioEncuesta.existeEncuesta(1);

        assertFalse("Muestra un Encuesta No existente", resultado);
        verify(mockGestor, times(1)).existeEncuesta(1); 
    }

    /**
     * Objeto de la prueba: existeEncuesta(autor, titulo)
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Caso en que la encuesta del autor con ese título existe
     * Operativa: Mocks de usuario y encuesta con título coincidente, se verifica que retorna true
     */
    @Test
    public void TestExisteEncuestaAutorTituloTrue() {
        Usuario mockUsuario = mock(Usuario.class);
        Encuesta mockEncuesta = mock(Encuesta.class);
        String nomAutor = "user1";
        String titulo = "Prueba";
        ArrayList<Integer> idEncuestas = new ArrayList<>(Arrays.asList(1));
        when(mockCtrlDominioUsuario.getUsuario(nomAutor)).thenReturn(mockUsuario);
        when(mockUsuario.getIdEncuestasCreadas()).thenReturn(idEncuestas);
        when(mockGestor.getEncuesta(1)).thenReturn(mockEncuesta);
        when(mockEncuesta.getTitulo()).thenReturn(titulo);

        boolean resultado = ctrlDominioEncuesta.existeEncuesta(nomAutor, titulo);

        assertTrue("No muestra una Encuesta existente", resultado);
        verify(mockCtrlDominioUsuario, times(1)).getUsuario(nomAutor); 
        verify(mockUsuario, times(1)).getIdEncuestasCreadas(); 
        verify(mockGestor, times(1)).getEncuesta(1); 
        verify(mockEncuesta, times(1)).getTitulo(); 
	}

    /**
     * Objeto de la prueba: existeEncuesta(autor, titulo)
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Caso en que la encuesta del autor no tiene ese título
     * Operativa: Mocks configurados, se busca título diferente y se verifica que retorna false
     */
    @Test 
    public void TestExisteEncuestaAutorTituloFalse() {
        Usuario mockUsuario = mock(Usuario.class);
        Encuesta mockEncuesta = mock(Encuesta.class);
        String nomAutor = "user1";
        String titulo = "Prueba";
        ArrayList<Integer> idEncuestas = new ArrayList<>(Arrays.asList(1));
        when(mockCtrlDominioUsuario.getUsuario(nomAutor)).thenReturn(mockUsuario);
        when(mockUsuario.getIdEncuestasCreadas()).thenReturn(idEncuestas);
        when(mockGestor.getEncuesta(1)).thenReturn(mockEncuesta);
        when(mockEncuesta.getTitulo()).thenReturn(titulo);

        boolean resultado = ctrlDominioEncuesta.existeEncuesta(nomAutor, "NoExiste");

        assertFalse("Muestra un Encuesta No existente", resultado);
        verify(mockCtrlDominioUsuario, times(1)).getUsuario(nomAutor); 
        verify(mockUsuario, times(1)).getIdEncuestasCreadas(); 
        verify(mockGestor, times(1)).getEncuesta(1); 
        verify(mockEncuesta, times(1)).getTitulo();     
    }

    /**
     * Objeto de la prueba: getEncuesta(idEncuesta)
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Obtención correcta de encuesta por ID
     * Operativa: Mock de gestor retorna encuesta, se verifica que se obtiene la correcta
     */
    @Test
    public void testGetEncuestaIdEncuestaSinError() {
        Encuesta mockEncuesta = mock(Encuesta.class);
        when(mockGestor.getEncuesta(1)).thenReturn(mockEncuesta);

        Encuesta resultado = ctrlDominioEncuesta.getEncuesta(1);

        assertEquals("Ha sacado un Encuesta incorrecto", mockEncuesta, resultado);   
        verify(mockGestor, times(1)).getEncuesta(1);
	}

    /**
     * Objeto de la prueba: getEncuesta(idEncuesta)
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Excepción cuando la encuesta no existe
     * Operativa: Mock retorna null, se espera IllegalStateException
     */
    @Test (expected=IllegalStateException.class) 
    public void testGetEncuestaIdEncuestaConError() {
        when(mockGestor.getEncuesta(1)).thenReturn(null);

        ctrlDominioEncuesta.getEncuesta(1);
    }

    /**
     * Objeto de la prueba: getEncuesta(titulo, autor)
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Obtención correcta de encuesta por título y autor
     * Operativa: Mocks configurados con encuesta existente, se verifica obtención correcta
     */
    @Test
    public void testGetEncuestaTituloAutorSinError() {
        Usuario mockUsuario = mock(Usuario.class);
        Encuesta mockEncuesta = mock(Encuesta.class);
        String nomAutor = "user1";
        String titulo = "Prueba";
        ArrayList<Integer> idEncuestas = new ArrayList<>(Arrays.asList(1));
        when(mockCtrlDominioUsuario.getUsuario(nomAutor)).thenReturn(mockUsuario);
        when(mockUsuario.getIdEncuestasCreadas()).thenReturn(idEncuestas);
        when(mockGestor.getEncuesta(1)).thenReturn(mockEncuesta);
        when(mockEncuesta.getTitulo()).thenReturn(titulo);

        Encuesta resultado = ctrlDominioEncuesta.getEncuesta(titulo, nomAutor);

        assertEquals("Ha sacado un Encuesta incorrecto", mockEncuesta, resultado);   
        verify(mockCtrlDominioUsuario, times(1)).getUsuario(nomAutor); 
        verify(mockUsuario, times(1)).getIdEncuestasCreadas(); 
        verify(mockGestor, times(1)).getEncuesta(1); 
        verify(mockEncuesta, times(1)).getTitulo(); 
	}

    /**
     * Objeto de la prueba: getEncuesta(titulo, autor)
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Excepción cuando el título no existe para el autor
     * Operativa: Mocks configurados, se busca título no existente, se espera IllegalStateException
     */
    @Test (expected=IllegalStateException.class) 
    public void testGetEncuestaTituloAutorConError() {
        Usuario mockUsuario = mock(Usuario.class);
        Encuesta mockEncuesta = mock(Encuesta.class);
        String nomAutor = "user1";
        String titulo = "Prueba";
        ArrayList<Integer> idEncuestas = new ArrayList<>(Arrays.asList(1));
        when(mockCtrlDominioUsuario.getUsuario(nomAutor)).thenReturn(mockUsuario);
        when(mockUsuario.getIdEncuestasCreadas()).thenReturn(idEncuestas);
        when(mockGestor.getEncuesta(1)).thenReturn(mockEncuesta);
        when(mockEncuesta.getTitulo()).thenReturn(titulo);

        Encuesta resultado = ctrlDominioEncuesta.getEncuesta("NoExiste", nomAutor);
        // falta verificar?
    }

    /**
     * Objeto de la prueba: getAllEncuestas()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Obtención de todas las encuestas como HashMap
     * Operativa: Mock retorna lista de encuestas, se verifica conversión a HashMap correcta
     */
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
        verify(mockEncuesta, times(1)).getIdEncuesta();
    }

    /**
     * Objeto de la prueba: getIdPreguntasEncuesta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Obtención de IDs de preguntas de una encuesta
     * Operativa: Mock de encuesta retorna lista de IDs, se verifica obtención correcta
     */
    @Test 
    public void testGetIdPreguntasEncuesta() {
        Encuesta mockEncuesta = mock(Encuesta.class);
        ArrayList<Integer> idPreguntas = new ArrayList<>(Arrays.asList(1, 2, 3));
        when(mockGestor.getEncuesta(1)).thenReturn(mockEncuesta);
        when(mockEncuesta.getIdPreguntas()).thenReturn(idPreguntas);

        List<Integer> resultado = ctrlDominioEncuesta.getIdPreguntasEncuesta(1);

        assertEquals("No ha sacado las preguntas correctas", idPreguntas, resultado);  
        verify(mockGestor, times(1)).getEncuesta(1);
        verify(mockEncuesta, times(1)).getIdPreguntas();
    }

    /**
     * Objeto de la prueba: getPreguntasEncuesta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Obtención de objetos Pregunta de una encuesta
     * Operativa: Mocks retornan preguntas por IDs, se verifica obtención correcta
     */
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
        verify(mockEncuesta, times(1)).getIdPreguntas();
        verify(mockCtrlDominioPregunta, times(1)).getPregunta(1);
    }

    /**
     * Objeto de la prueba: crearEncuesta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Creación correcta de una encuesta
     * Operativa: Mocks configurados, se crea encuesta y se verifica ID y persistencia
     */
    @Test 
    public void testCrearEncuestaSinError() {
        Usuario mockUsuario = mock(Usuario.class);
        when(mockCtrlDominioUsuario.getUsuario("user1")).thenReturn(mockUsuario);
        when(mockGestor.getNextIdEncuesta()).thenReturn(1);
        
        int idEncuesta = ctrlDominioEncuesta.crearEncuesta("user1", "Prueba");
        
        assertEquals("No ha creado la encuesta con el id correcto", 1, idEncuesta);
        verify(mockGestor).guardarEncuesta(any(Encuesta.class));
        verify(mockGestor).actualizarUsuario(mockUsuario);
    }

    /**
     * Objeto de la prueba: crearEncuesta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Excepción cuando el título es vacío
     * Operativa: Se intenta crear encuesta con título vacío, se espera IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCrearEncuestaTituloVacio() {
        ctrlDominioEncuesta.crearEncuesta("user1", "");
    }

    /**
     * Objeto de la prueba: crearEncuesta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Excepción cuando ya existe encuesta con mismo título y autor
     * Operativa: Mocks configurados con encuesta existente, se intenta crear duplicado
     */
    @Test(expected = IllegalArgumentException.class)
    public void testCrearEncuestaDuplicado() {
        Usuario mockUsuario = mock(Usuario.class);
        Encuesta mockEncuesta = mock(Encuesta.class);
        String nomAutor = "user1";
        String titulo = "Prueba";
        ArrayList<Integer> idEncuestas = new ArrayList<>(Arrays.asList(1));
        when(mockCtrlDominioUsuario.getUsuario(nomAutor)).thenReturn(mockUsuario);
        when(mockUsuario.getIdEncuestasCreadas()).thenReturn(idEncuestas);
        when(mockGestor.getEncuesta(1)).thenReturn(mockEncuesta);
        when(mockEncuesta.getTitulo()).thenReturn(titulo);

        ctrlDominioEncuesta.crearEncuesta(nomAutor, titulo);
    }

    // TODO: Create test resource files
    /*
    @Test
    public void testParsearFicheroEncuestaBueno() {
        when(mockCtrlDominioUsuario.getUsuarioActivo()).thenReturn("user1");
        Usuario mockUsuario = mock(Usuario.class);
        when(mockCtrlDominioUsuario.getUsuario("user1")).thenReturn(mockUsuario);
        when(mockGestor.getNextIdEncuesta()).thenReturn(1);
        when(mockCtrlDominioPregunta.crearPregunta(anyInt(), anyString(), anyString(), anyMap())).thenReturn(1);
        
        File file = new File("src/test/resources/EjemploBueno.json");
        int idEncuesta = ctrlDominioEncuesta.importarEncuesta(file);

        assertEquals("No tiene mismo id", 1, idEncuesta);
    }
    */

    // TODO: Create test resource files
    /*
    @Test(expected = IllegalArgumentException.class)
    public void testParsearFicheroEncuestaError() {
        when(mockCtrlDominioUsuario.getUsuarioActivo()).thenReturn("user1");
        File file = new File("src/test/resources/EjemploError.json");
        ctrlDominioEncuesta.importarEncuesta(file);
    }
    */

    // TODO: Create test resource files
    /*
    @Test(expected = IllegalArgumentException.class)
    public void testParsearFicheroEncuestaTipoMal() {
        when(mockCtrlDominioUsuario.getUsuarioActivo()).thenReturn("user1");
        File file = new File("src/test/resources/EjemploTipoPreguntaMal.json");
        ctrlDominioEncuesta.importarEncuesta(file);
    }
    */

    /**
     * Objeto de la prueba: modificarTituloEncuesta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Modificación correcta del título
     * Operativa: Se modifica título y se verifica cambio y persistencia
     */
    @Test //?
    public void testModificarTituloEncuestaSinError() {
        Encuesta e = new Encuesta(1, "Prueba", "user1");
        when(mockGestor.getEncuesta(1)).thenReturn(e);

        ctrlDominioEncuesta.modificarTituloEncuesta(1, "NuevoTitulo");

        assertEquals("No ha modificado el título correctamente", "NuevoTitulo", e.getTitulo());
        verify(mockGestor, times(1)).getEncuesta(1);
        verify(mockGestor, times(1)).actualizarEncuesta(e);
    }

    /**
     * Objeto de la prueba: modificarTituloEncuesta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Excepción cuando el nuevo título es vacío
     * Operativa: Se intenta modificar a título vacío, se espera IllegalArgumentException
     */
    @Test(expected = IllegalArgumentException.class)
    public void testModificarTituloEncuestaTituloVacio() {
        ctrlDominioEncuesta.modificarTituloEncuesta(1, "");
    }

    /**
     * Objeto de la prueba: eliminarEncuesta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Eliminación correcta de encuesta y sus preguntas
     * Operativa: Mocks configurados, se elimina encuesta y se verifican llamadas a gestor
     */
    @Test 
    public void testEliminarEncuesta() {
        Usuario mockUsuario = mock(Usuario.class);
        Encuesta mockEncuesta= mock(Encuesta.class);
        ArrayList<Integer> idPreguntas = new ArrayList<>(Arrays.asList(1, 2, 3));
        when(mockGestor.getEncuesta(1)).thenReturn(mockEncuesta);
        when(mockEncuesta.getIdPreguntas()).thenReturn(idPreguntas);
        when(mockEncuesta.getIdCreador()).thenReturn("user1");
        when(mockCtrlDominioUsuario.getUsuario("user1")).thenReturn(mockUsuario);

        ctrlDominioEncuesta.eliminarEncuesta(1);

        verify(mockGestor).getEncuesta(1);
        verify(mockCtrlDominioPregunta, times(3)).eliminarPregunta(anyInt());
        verify(mockCtrlDominioUsuario).getUsuario("user1");
        verify(mockGestor).eliminarEncuesta(1);
    }

    /**
     * Objeto de la prueba: getRespuestasEncuesta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Obtención de respuestas agrupadas por pregunta
     * Operativa: Mocks de preguntas y respuestas, se verifica estructura Map<Pregunta, Set<Respuesta>>
     */
    @Test 
    public void testGetRespuestasEncuesta() {
        Encuesta mockEncuesta = mock(Encuesta.class);
        ArrayList<Integer> preguntas = new ArrayList<>(Arrays.asList(1, 2));
        when(mockGestor.getEncuesta(1)).thenReturn(mockEncuesta);
        when(mockEncuesta.getIdPreguntas()).thenReturn(preguntas);

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


    /**
     * Objeto de la prueba: anadirPregunta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Añadir pregunta a encuesta existente
     * Operativa: Mocks configurados, se añade pregunta y se verifica creación
     */
    @Test
    public void testAnadirPregunta() {
        Encuesta mockEncuesta = mock(Encuesta.class);
        when(mockGestor.getEncuesta(1)).thenReturn(mockEncuesta);
        when(mockCtrlDominioPregunta.crearPregunta(eq(1), anyString(), anyString(), anyMap())).thenReturn(10);

        CtrlDominioEncuesta.PreguntaImportData infoP = new CtrlDominioEncuesta.PreguntaImportData();
        infoP.enunciado = "¿Pregunta?";
        infoP.tipo = "libre";
        infoP.atributosEspecificos = new HashMap<>();

        int idP = ctrlDominioEncuesta.anadirPregunta(1, infoP);

        assertEquals("No ha retornado el id correcto", 10, idP);
        verify(mockCtrlDominioPregunta).crearPregunta(1, "¿Pregunta?", "libre", infoP.atributosEspecificos);
    }

    /**
     * Objeto de la prueba: getRespuestasParaClustering()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Obtención de matriz de respuestas organizada por usuarios
     * Operativa: Mocks de preguntas y respuestas de usuarios, se verifica estructura matricial
     */
    @Test
    public void testGetRespuestasParaClustering() {
        Encuesta mockEncuesta = mock(Encuesta.class);
        ArrayList<Integer> idPreguntas = new ArrayList<>(Arrays.asList(1, 2));
        when(mockEncuesta.getIdPreguntas()).thenReturn(idPreguntas);
        when(mockGestor.getEncuesta(1)).thenReturn(mockEncuesta);

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

    // TODO: Create test resource files
    /*
    @Test
    public void testImportarEncuesta() {
        when(mockCtrlDominioUsuario.getUsuarioActivo()).thenReturn("user1");
        Usuario mockUsuario = mock(Usuario.class);
        when(mockCtrlDominioUsuario.getUsuario("user1")).thenReturn(mockUsuario);
        when(mockGestor.getNextIdEncuesta()).thenReturn(1);
        when(mockCtrlDominioPregunta.crearPregunta(anyInt(), anyString(), anyString(), anyMap())).thenReturn(1);
        
        File file = new File("src/test/resources/EjemploBueno.json");
        int idEncuesta = ctrlDominioEncuesta.importarEncuesta(file);

        assertEquals("No ha creado la encuesta con el id correcto", 1, idEncuesta);
        verify(mockGestor).guardarEncuesta(any(Encuesta.class));
    }
    */


    /**
     * Objeto de la prueba: importarEncuesta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Excepción cuando no hay usuario activo
     * Operativa: Mock retorna null para usuario activo, se espera IllegalStateException
     */
    @Test(expected = IllegalStateException.class)
    public void testImportarEncuestaSinUsuarioActivo() {
        when(mockCtrlDominioUsuario.getUsuarioActivo()).thenReturn(null);
        File mockFile = mock(File.class);

        ctrlDominioEncuesta.importarEncuesta(mockFile);
    }
}
