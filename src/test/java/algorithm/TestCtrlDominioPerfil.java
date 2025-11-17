package domaincontrollers;

import java.io.File;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import domain.algorithm.*;
import domain.Encuesta;
import domain.Usuario;
import domain.Pregunta;
import domain.Respuesta;
import domain.RespuestaLibre;
import domain.RespuestaNumerica;
import domain.RespuestaOpcionMultiple;
import domain.RespuestaOpcionUnica;

/**
 * @author Alessandro Jin
 */

@RunWith(MockitoJUnitRunner.Silent.class)
public class TestCtrlDominioPerfil {
    @Mock
    private CtrlDominioEncuesta mockCtrlDominioEncuesta;

    private CtrlDominioPerfil ctrlDominioPerfil;

    List<List<Respuesta>> respuestas;
    List<Pregunta> variables;

    @Before
    public void setUp() throws Exception {
        // Resetear singleton
        Field instance = ctrlDominioPerfil.class.getDeclaredField("singletonInstance");
        instance.setAccessible(true);
        instance.set(null, null);
        
        ctrlDominioPerfil = ctrlDominioPerfil.getInstance(mockCtrlDominioEncuesta);

        Respuesta mockU1R1 = mock(RespuestaLibre.class);
        Respuesta mockU1R2 = mock(RespuestaNumerica.class);
        Respuesta mockU1R3 = mock(RespuestaOpcionMultiple.class);
        Respuesta mockU1R4 = mock(RespuestaOpcionUnica.class);
        Respuesta mockU1R5 = mock(RespuestaOpcionUnica.class);

        Respuesta mockU2R1 = mock(RespuestaLibre.class);
        Respuesta mockU2R2 = mock(RespuestaNumerica.class);
        Respuesta mockU2R3 = mock(RespuestaOpcionMultiple.class);
        Respuesta mockU2R4 = mock(RespuestaOpcionUnica.class);
        Respuesta mockU2R5 = mock(RespuestaOpcionUnica.class);

        Respuesta mockU3R1 = mock(RespuestaLibre.class);
        Respuesta mockU3R2 = mock(RespuestaNumerica.class);
        Respuesta mockU3R3 = mock(RespuestaOpcionMultiple.class);
        Respuesta mockU3R4 = mock(RespuestaOpcionUnica.class);
        Respuesta mockU3R5 = mock(RespuestaOpcionUnica.class);

        Respuesta mockU4R1 = mock(RespuestaLibre.class);
        Respuesta mockU4R2 = mock(RespuestaNumerica.class);
        Respuesta mockU4R3 = mock(RespuestaOpcionMultiple.class);
        Respuesta mockU4R4 = mock(RespuestaOpcionUnica.class);
        Respuesta mockU4R5 = mock(RespuestaOpcionUnica.class);

                
        // Creamos listas individuales
        List<Respuesta> lista1 = Arrays.asList(mockU1R1, mockU1R2, mockU1R3, mockU1R4, mockU1R5);
        List<Respuesta> lista2 = Arrays.asList(mockU2R1, mockU2R2, mockU2R3, mockU2R4, mockU2R5);
        List<Respuesta> lista3 = Arrays.asList(mockU3R1, mockU3R2, mockU3R3, mockU3R4, mockU3R5);
        List<Respuesta> lista4 = Arrays.asList(mockU4R1, mockU4R2, mockU4R3, mockU4R4, mockU4R5);
        List<Respuesta> lista5 = Arrays.asList(mockU5R1, mockU5R2, mockU5R3, mockU5R4, mockU5R5);

        // Lista de listas
        res = Arrays.asList(lista1, lista2, lista3, lista4, lista5); 

        when(mockU1R1.getRespuesta()).thenReturn("Buenos dias");
        when(mockU2R1.getRespuesta()).thenReturn("Buenos tardes");
        when(mockU3R1.getRespuesta()).thenReturn("Buenos tardes");
        when(mockU4R1.getRespuesta()).thenReturn("Buenos dias");
        when(mockU5R1.getRespuesta()).thenReturn("Buenos noches");

        when(mockU1R2.getRespuesta()).thenReturn(5);
        when(mockU2R2.getRespuesta()).thenReturn(7);
        when(mockU3R2.getRespuesta()).thenReturn(8);
        when(mockU4R2.getRespuesta()).thenReturn(5);
        when(mockU5R2.getRespuesta()).thenReturn(7);

        when(mockU1R3.getRespuesta()).thenReturn(Arrays.asList(1,2,3));
        when(mockU1R3.getRespuesta()).thenReturn(Arrays.asList(1,2,3));


    }

    @Test
    public void testEjecutarClusteringKmeans() {
     
        when(mockCtrlDominioEncuesta.getRespuestasParaClustering(1)).thenReturn(ArrayList<>())
    }

    @Test
    public void testEjecutarClusteringKmeansPlusPlus() {
        
    }

    @Test
    public void testEjecutarClusteringKmeansMedoids() {
        
    }

}
