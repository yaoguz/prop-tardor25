package domaincontrollers;

import java.lang.reflect.Field;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import domain.*;
import domain.algorithm.estructuras.*;

/**
 * Pruebas del controlador CtrlDominioPerfil.
 * @author Alessandro Jin
 */
@RunWith(MockitoJUnitRunner.Silent.class)
public class TestCtrlDominioPerfil {
    
    @Mock
    private CtrlDominioEncuesta mockCtrlDominioEncuesta;

    private CtrlDominioPerfil ctrlDominioPerfil;
    
    private List<List<Respuesta>> respuestas;
    private List<Pregunta> variables;

    @Before
    public void setUp() throws Exception {
        // Resetear singleton
        Field instance = CtrlDominioPerfil.class.getDeclaredField("singletonInstance");
        instance.setAccessible(true);
        instance.set(null, null);
        
        ctrlDominioPerfil = CtrlDominioPerfil.getInstance(mockCtrlDominioEncuesta);
        
        // Crear preguntas reales (el algoritmo necesita datos reales)
        PreguntaNumerica p1 = new PreguntaNumerica(1, 1, "Edad?", 18, 99);
        PreguntaOpcionUnica p2 = new PreguntaOpcionUnica(1, 2, "Género?", false);
        p2.addOpcion("M");
        p2.addOpcion("F");
        PreguntaNumerica p3 = new PreguntaNumerica(1, 3, "Horas ejercicio?", 0, 20);
        
        variables = Arrays.asList(p1, p2, p3);
        
        // Crear respuestas reales (el algoritmo necesita datos reales)
        List<Respuesta> u1 = new ArrayList<>();
        u1.add(new RespuestaNumerica(1, "U1", 1, 22.0));
        u1.add(new RespuestaOpcionUnica(2, "U1", 2, 0));
        u1.add(new RespuestaNumerica(3, "U1", 3, 10.0));
        
        List<Respuesta> u2 = new ArrayList<>();
        u2.add(new RespuestaNumerica(4, "U2", 1, 23.0));
        u2.add(new RespuestaOpcionUnica(5, "U2", 2, 1));
        u2.add(new RespuestaNumerica(6, "U2", 3, 9.0));
        
        List<Respuesta> u3 = new ArrayList<>();
        u3.add(new RespuestaNumerica(7, "U3", 1, 25.0));
        u3.add(new RespuestaOpcionUnica(8, "U3", 2, 0));
        u3.add(new RespuestaNumerica(9, "U3", 3, 8.0));
        
        List<Respuesta> u4 = new ArrayList<>();
        u4.add(new RespuestaNumerica(10, "U4", 1, 50.0));
        u4.add(new RespuestaOpcionUnica(11, "U4", 2, 0));
        u4.add(new RespuestaNumerica(12, "U4", 3, 2.0));
        
        List<Respuesta> u5 = new ArrayList<>();
        u5.add(new RespuestaNumerica(13, "U5", 1, 52.0));
        u5.add(new RespuestaOpcionUnica(14, "U5", 2, 1));
        u5.add(new RespuestaNumerica(15, "U5", 3, 1.0));
        
        List<Respuesta> u6 = new ArrayList<>();
        u6.add(new RespuestaNumerica(16, "U6", 1, 55.0));
        u6.add(new RespuestaOpcionUnica(17, "U6", 2, 0));
        u6.add(new RespuestaNumerica(18, "U6", 3, 2.0));
        
        respuestas = Arrays.asList(u1, u2, u3, u4, u5, u6);
    }

    /** Ejecuta clustering con KMeans y verifica que el resultado sea válido. */
    @Test
    public void testEjecutarClusteringKmeans_RetornaResultadoValido() {
        when(mockCtrlDominioEncuesta.getRespuestasParaClustering(1)).thenReturn(respuestas);
        when(mockCtrlDominioEncuesta.getPreguntasEncuesta(1)).thenReturn(variables);
        
        ResultadoClustering resultado = ctrlDominioPerfil.ejecutarClustering(1, 2, "kmeans");
        
        assertNotNull(resultado);
        assertEquals(2, resultado.getPerfiles().size());
        assertTrue(resultado.getCalidad() >= -1.0 && resultado.getCalidad() <= 1.0);
        
        verify(mockCtrlDominioEncuesta, times(1)).getRespuestasParaClustering(1);
        verify(mockCtrlDominioEncuesta, times(1)).getPreguntasEncuesta(1);
    }

    /** Ejecuta clustering con KMeans++ y verifica resultado válido. */
    @Test
    public void testEjecutarClusteringKmeansPlusPlus_RetornaResultadoValido() {
        when(mockCtrlDominioEncuesta.getRespuestasParaClustering(1)).thenReturn(respuestas);
        when(mockCtrlDominioEncuesta.getPreguntasEncuesta(1)).thenReturn(variables);
        
        ResultadoClustering resultado = ctrlDominioPerfil.ejecutarClustering(1, 2, "kmeans++");
        
        assertNotNull(resultado);
        assertEquals(2, resultado.getPerfiles().size());
        assertTrue(resultado.getCalidad() >= -1.0 && resultado.getCalidad() <= 1.0);
        
        verify(mockCtrlDominioEncuesta, times(1)).getRespuestasParaClustering(1);
        verify(mockCtrlDominioEncuesta, times(1)).getPreguntasEncuesta(1);
    }

    /** Ejecuta clustering con KMedoids y comprueba el resultado. */
    @Test
    public void testEjecutarClusteringKmedoids_RetornaResultadoValido() {
        when(mockCtrlDominioEncuesta.getRespuestasParaClustering(1)).thenReturn(respuestas);
        when(mockCtrlDominioEncuesta.getPreguntasEncuesta(1)).thenReturn(variables);
        
        ResultadoClustering resultado = ctrlDominioPerfil.ejecutarClustering(1, 2, "kmedoids");
        
        assertNotNull(resultado);
        assertEquals(2, resultado.getPerfiles().size());
        assertTrue(resultado.getCalidad() >= -1.0 && resultado.getCalidad() <= 1.0);
        
        verify(mockCtrlDominioEncuesta, times(1)).getRespuestasParaClustering(1);
        verify(mockCtrlDominioEncuesta, times(1)).getPreguntasEncuesta(1);
    }

    /** Verifica que se obtienen correctamente los miembros de un perfil. */
    @Test
    public void testObtenerMiembrosPerfil_RetornaListaCorrecta() {
        List<String> miembros = Arrays.asList("U1", "U2", "U3");
        Centroide mockCentroide = mock(Centroide.class);
        Perfil perfil = new Perfil("U1", mockCentroide, miembros);
        
        List<String> resultado = ctrlDominioPerfil.obtenerMiembrosPerfil(perfil);
        
        assertNotNull(resultado);
        assertEquals(3, resultado.size());
        assertTrue(resultado.contains("U1"));
        assertTrue(resultado.contains("U2"));
        assertTrue(resultado.contains("U3"));
    }

    /** Verifica que se obtiene el representante esperado de un perfil. */
    @Test
    public void testObtenerRepresentantePerfil_RetornaRepresentanteCorrecto() {
        List<String> miembros = Arrays.asList("U1", "U2", "U3");
        Centroide mockCentroide = mock(Centroide.class);
        Perfil perfil = new Perfil("U1", mockCentroide, miembros);
        
        String resultado = ctrlDominioPerfil.obtenerRepresentantePerfil(perfil);
        
        assertNotNull(resultado);
        assertEquals("U1", resultado);
    }

    /** Ejecuta clustering con k=3 y comprueba que genera tres perfiles. */
    @Test
    public void testEjecutarClustering_K3_GeneraTresPerfiles() {
        when(mockCtrlDominioEncuesta.getRespuestasParaClustering(1)).thenReturn(respuestas);
        when(mockCtrlDominioEncuesta.getPreguntasEncuesta(1)).thenReturn(variables);
        
        ResultadoClustering resultado = ctrlDominioPerfil.ejecutarClustering(1, 3, "kmeans");
        
        assertNotNull(resultado);
        assertEquals(3, resultado.getPerfiles().size());
    }

    /** Si se pasa un algoritmo desconocido, usa KMeans por defecto. */
    @Test
    public void testEjecutarClustering_AlgoritmoDesconocido_UsaKmeansPorDefecto() {
        when(mockCtrlDominioEncuesta.getRespuestasParaClustering(1)).thenReturn(respuestas);
        when(mockCtrlDominioEncuesta.getPreguntasEncuesta(1)).thenReturn(variables);
        
        ResultadoClustering resultado = ctrlDominioPerfil.ejecutarClustering(1, 2, "algoritmo_invalido");
        
        assertNotNull(resultado);
        assertEquals(2, resultado.getPerfiles().size());
    }
}
