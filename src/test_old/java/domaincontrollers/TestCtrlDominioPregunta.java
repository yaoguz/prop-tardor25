package domaincontrollers;

import domain.*;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;

import org.mockito.junit.MockitoJUnitRunner;

import persistence.CtrlPersistence;
import persistence.GestorPersistencia;
import domain.Usuario;


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
    public void setUp() {
        ctrlDominioPregunta = new CtrlDominioPregunta(mockGestor,mockCtrlDominioRespuesta, mockCtrlDominioEncuesta);
    }

    @Test

    public void testExistePregunta() {
        when(mockGestor.existePregunta(1)).thenReturn(true);
        when(mockGestor.existePregunta(2)).thenReturn(false);
        boolean resultado1 = ctrlDominioPregunta.existePregunta(1);
        boolean resultado2 = ctrlDominioPregunta.existePregunta(2);

        assertFalse("Deberia ser false", resultado2);
        assertTrue("Deberia ser true", resultado1);

        verify(mockGestor).existePregunta(1);
        

    }
}
