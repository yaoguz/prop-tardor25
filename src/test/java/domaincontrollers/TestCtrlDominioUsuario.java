package domaincontrollers;

import main.domini.*;
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

/**
 * @author Alessandro Jin
 */

@RunWith(MockitoJUnitRunner.class)
public class TestCtrlDominioUsuario {
    @Mock
    private TreeMap<String, Usuario> mockUsuarios;
    
    @Mock
    private String usuarioActivo;
   
    private CtrlDominioUsuario ctrlDominioUsuario;   

    @Before
    public void setUp() {
   
        ctrlDominioUsuario = new CtrlDominioUsuario();
        ctrlDominioUsuario.cargarUsuarios(mockUsuarios);
    }

    @Test
    public void TestExisteUsuarioTrue() {
        when(mockUsuarios.containsKey("Alessandro")).thenReturn(true);

        boolean resultado = ctrlDominioUsuario.existeUsuario("Alessandro");

        assertTrue("No muestra un usuario existente", resultado);
        verify(mockUsuarios, times(1)).containsKey("Alessandro"); // verificar interacción
	}

    @Test 
    public void TestExisteUsuanrioFalse() {
        when(mockUsuarios.containsKey("Alessandro")).thenReturn(true);

        boolean resultado = ctrlDominioUsuario.existeUsuario("Yaoyao");

        assertFalse("Muestra un usuario No existente", resultado);
        verify(mockUsuarios, times(1)).containsKey("Yaoyao"); // verificar interacción
    }

    @Test
    public void testGetUsuarioSinError() {
        
        Usuario u = new Usuario("Alessandro", "123456");
        when(mockUsuarios.get("Alessandro")).thenReturn(u);

        
        Usuario resultado = ctrlDominioUsuario.getUsuario("Alessandro");

        // Assert
        assertEquals("Ha sacado un usuario incorrecto", u, resultado);   // compara que es el mismo usuario
        verify(mockUsuarios, times(1)).get("Alessandro");
	}

    @Test (expected=IllegalArgumentException.class) 
    public void testGetUsuarioConError() {
            
        Usuario u = new Usuario("Alessandro", "123456");
        when(mockUsuarios.get("Alessandro")).thenReturn(null);

        Usuario resultado = ctrlDominioUsuario.getUsuario("Alessandro");
    }

    @Test
    public void testRegistrarUsuarioBueno() {
    
        String nom = ctrlDominioUsuario.registrarUsuario("Alessandro", "123456", "123456");
        
        assertEquals("Guarda nombre incorrecto", "Alessandro", nom);  
        assertTrue(ctrlDominioUsuario.existeUsuario("Alessandro"));

        verify(mockUsuarios, times(1)).put(eq("Alessandro"), any(Usuario.class));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegistrarUsuarioNombreVacio() {
     
        ctrlDominioUsuario.registrarUsuario("", "123456", "123456");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegistrarUsuarioPasswordVacia() {
      
        ctrlDominioUsuario.registrarUsuario("Alessandro", "", "123456");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegistrarUsuarioConfirmVacia() {
       
        ctrlDominioUsuario.registrarUsuario("Alessandro", "123456", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegistrarUsuarioNombreCorto() {
     
        ctrlDominioUsuario.registrarUsuario("al", "123456", "123456");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testRegistrarUsuarioPasswordCorta() {
        ctrlDominioUsuario.registrarUsuario("Alessandro", "12345", "12345");
    }

    @Test(expected = IllegalStateException.class)
    public void testRegistrarUsuarioPasswordNoCoincide() {
 
        ctrlDominioUsuario.registrarUsuario("Alessandro", "123456", "123456");
    }
    
    //ARRIBA BIEN ABAJO NO

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
        
        
        verify(mockCtrlUsuario, time(1)).getUsuario(nomUsuario);
        verify(mockUsuario, time(1)).newContrasena(nuevaContrasena);
        verify(mockPersistence, time(1)).actualizarUsuario(mockUsuario);
    }

    @Test
    public void testCerrarSesion() {

        ctrlDominioUsuario.cerrarSesion();
        
        verify(mockCtrlUsuario, time(1)).cerrarSesion();
    }
}
