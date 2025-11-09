package domaincontroller;

import main.domin.*;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
//import org.mockito.MockitoAnnotations; o runner o este
import org.mockito.junit.MockitoJUnitRunner;

import classcontroller.CtrlUsuario;
import persistence.CtrlPersistence;
import domain.Usuario;

@RunWith(MockitoJUnitRunner.class)
public class TestCtrlDominioUsuario {
    @Mock
    private CtrlUsuario mockCtrlUsuario;
    
    @Mock
    private CtrlPersistence mockPersistence;
    
    private CtrlDominioUsuario ctrlDominioUsuario;

    @Before
    public void setUp() {
   
        ctrlDominioUsuario = new CtrlDominioUsuario(mockCtrlUsuario, mockPersistence);
    }

    @Test
    public void testRegistrarUsuario() {
        
        String nomUsuario = "tester";
        String contrasena = "123456";
        String confirmacion = "123456";
        
        
        Usuario usuario = new Usuario(nomUsuario, contrasena);
        when(mockCtrlUsuario.crearUsuario(nomUsuario, contrasena)).thenReturn(usuario);
        
        
        String nomUser = ctrlDominioUsuario.registrarUsuario(nomUsuario, contrasena, confirmacion);
        
        assertEquals("Usuario registrado mal", nomUsuario, nomUser);

      
        verify(mockCtrlUsuario, times(1)).crearUsuario(nomUsuario, contrasena);
        verify(mockPersistence, times(1)).guardarUsuario(usuario);
    }



    @Test (expected = IllegalArgumentException.class)
    public void testRegistrarUsuario_ContrasenasNoCoinciden() {
        // ARRANGE
        String nomUsuario = "tester";
        String contrasena = "123456";
        String confirmacion = "654321";
        
        
        Usuario usuario = new Usuario(nomUsuario, contrasena);
        when(mockCtrlUsuario.crearUsuario(nomUsuario, contrasena)).thenReturn(usuario);
        ctrlDominioUsuario.registrarUsuario(nomUsuario, contrasena, confirmacion);
        
        
    }

    @Test
    public void testIniciarSesion() {
        
        String nomUsuario = "tester";
        String contrasena = "123456";
        
        Usuario mockUsuario = mock(Usuario.class);

        when(mockPersistence.cargarUsuario(nomUsuario)).thenReturn(mockUsuario);
        when(mockUsuario.comprobarContrasena(contrasena)).thenReturn(true);
        when(mockCtrlUsuario.iniciarSesion(nomUsuario, contrasena)).thenReturn(true);
        
  
        boolean resultado = ctrlDominioUsuario.iniciarSesion(nomUsuario, contrasena);
        
        
        assertTrue("No inicio correctamente", resultado);
        
        
        verify(mockPersistence, times(1)).cargarUsuario(nomUsuario);
        verify(mockUsuario, times(1)).comprobarContrasena(contrasena);
        verify(mockCtrlUsuario, times(1)).iniciarSesion(nomUsuario, contrasena);
    }

    @Test
    public void testIniciarSesion_ContrasenaIncorrecta() {
        
        String nomUsuario = "tester";
        String contrasena = "123456";
        
        Usuario mockUsuario = mock(Usuario.class);
        
        when(mockPersistence.cargarUsuario(nomUsuario)).thenReturn(mockUsuario);
        when(mockUsuario.comprobarContrasena(contrasena)).thenReturn(false);
        
       
        boolean resultado = ctrlDominioUsuario.iniciarSesion(nomUsuario, contrasena);
        
        
        assertFalse("Ha entrado con contraseña incorrecta", resultado);

        verify(mockPersistence, times(1)).cargarUsuario(nomUsuario);
        verify(mockUsuario, times(1)).comprobarContrasena(contrasena);
        verify(mockCtrlUsuario, never()).iniciarSesion(nomUsuario, contrasena);
    }

     @Test
    public void testModificarContrasena() {
        
        String nomUsuario = "testUser";
        String nuevaContrasena = "nuevaContrasena";
        
        Usuario mockUsuario = mock(Usuario.class);
        when(mockCtrlUsuario.getUsuario(nomUsuario)).thenReturn(mockUsuario);
        
      
        ctrlDominioUsuario.modificarContrasena(nomUsuario, nuevaContrasena);
        
        
        verify(mockCtrlUsuario, times(1)).getUsuario(nomUsuario);
        verify(mockUsuario, times(1)).newContrasena(nuevaContrasena);
        verify(mockPersistence, times(1)).actualizarUsuario(mockUsuario);
    }

    @Test
    public void testCerrarSesion() {

        ctrlDominioUsuario.cerrarSesion();
        
        verify(mockCtrlUsuario, times(1)).cerrarSesion();
    }
}
