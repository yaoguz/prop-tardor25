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
import domain.Usuario;

/**
 * @author Alessandro Jin
 */

@RunWith(MockitoJUnitRunner.class)
public class TestCtrlDominioUsuario {
    @Mock
    private TreeMap<String, Usuario> mockUsuarios;
    

    private CtrlDominioUsuario ctrlDominioUsuario;   

    @Before
    public void setUp() {
   
        ctrlDominioUsuario = new CtrlDominioUsuario();
        ctrlDominioUsuario.cargarUsuarios(mockUsuarios);
    }

    @Test
    public void testExisteUsuarioTrue() {
        when(mockUsuarios.containsKey("Alessandro")).thenReturn(true);

        boolean resultado = ctrlDominioUsuario.existeUsuario("Alessandro");

        assertTrue("No muestra un usuario existente", resultado);
        verify(mockUsuarios, times(1)).containsKey("Alessandro"); // verificar interacción
	}

    @Test 
    public void testExisteUsuarioFalse() {
        when(mockUsuarios.containsKey("Yaoyao")).thenReturn(false);

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
            

        when(mockUsuarios.get("Alessandro")).thenReturn(null);

        Usuario resultado = ctrlDominioUsuario.getUsuario("Alessandro");
    }

    @Test
    public void testRegistrarUsuarioBueno() {
    
            
        TreeMap<String, Usuario> users = new TreeMap<>();
        ctrlDominioUsuario.cargarUsuarios(users);

        String nom = ctrlDominioUsuario.registrarUsuario("Alessandro", "123456", "123456");

        assertEquals("Guarda nombre incorrecto", "Alessandro", nom);
        assertTrue(ctrlDominioUsuario.existeUsuario("Alessandro"));
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
 
        ctrlDominioUsuario.registrarUsuario("Alessandro", "123456", "654321");
    }

    @Test(expected = IllegalStateException.class)
    public void testRegistrarUsuarioUsuarioDuplicado() {

        TreeMap<String, Usuario> users = new TreeMap<>();
        ctrlDominioUsuario.cargarUsuarios(users);

      
        ctrlDominioUsuario.registrarUsuario("Alessandro", "123456", "123456");
       
        ctrlDominioUsuario.registrarUsuario("Alessandro", "123456", "123456");
    }


    @Test
    public void testIniciarSesionBueno() {
        
        Usuario mockUser = mock(Usuario.class);
        when(mockUsuarios.get("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("123456")).thenReturn(true);
        when(mockUser.getNomUsuario()).thenReturn("Alessandro");

        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");

      
        assertEquals("No se activo el usuario", "Alessandro", ctrlDominioUsuario.getUsuarioActivo());

        verify(mockUsuarios, times(1)).get("Alessandro");
        verify(mockUser, times(1)).comprobarContrasena("123456");
    }

    @Test(expected = IllegalStateException.class)
    public void testIniciarSesionYaHayUsuarioActivo() {

        Usuario mockUser = mock(Usuario.class);
        when(mockUsuarios.get("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("123456")).thenReturn(true);
        when(mockUser.getNomUsuario()).thenReturn("Alessandro");

        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");
    
        assertEquals("No se activo el usuario","Alessandro", ctrlDominioUsuario.getUsuarioActivo());

        // intentar iniciar sesión con otro nombre 
        ctrlDominioUsuario.iniciarSesion("Yaoyao", "123456");
    }


    @Test(expected = IllegalStateException.class)
    public void testIniciarSesionContrasenaIncorrecta() {
        
        Usuario mockUser = mock(Usuario.class);
        when(mockUsuarios.get("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("654321")).thenReturn(false);

     
        ctrlDominioUsuario.iniciarSesion("Alessandro", "654321");

   
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIniciarSesionUsuarioNoExiste() {
        // usar mockUsuarios -> devuelve null por defecto para get()
        when(mockUsuarios.get("Alessandro")).thenReturn(null);
        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");
    }


    @Test
    public void testGetUsuarioActivoSinSesion() {
        
        assertNull("Debe devolver null si no hay usuario activo", ctrlDominioUsuario.getUsuarioActivo());
    }

    @Test
    public void testGetUsuarioActivoConSesion() {

        Usuario mockUser = mock(Usuario.class);  
        when(mockUsuarios.get("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("123456")).thenReturn(true);
        when(mockUser.getNomUsuario()).thenReturn("Alessandro");

        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");

        String activo = ctrlDominioUsuario.getUsuarioActivo();
        assertEquals("Debe devolver el usuario activo", "Alessandro", activo);
    }

    @Test
    public void testCerrarSesionActual() {
        
        Usuario mockUser = mock(Usuario.class);
        when(mockUsuarios.get("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("123456")).thenReturn(true);
        when(mockUser.getNomUsuario()).thenReturn("Alessandro");

        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");
        assertEquals("Debe estar logueado antes de cerrar sesión", "Alessandro", ctrlDominioUsuario.getUsuarioActivo());

        ctrlDominioUsuario.cerrarSesionActual();

        assertNull("Debe quedar null después de cerrar sesión", ctrlDominioUsuario.getUsuarioActivo());
    }

    @Test
    public void testModificarContrasenaValida() {

        Usuario mockUser = mock(Usuario.class);
        when(mockUsuarios.get("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("123456")).thenReturn(true);
        when(mockUser.getNomUsuario()).thenReturn("Alessandro");

        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");
        ctrlDominioUsuario.modificarContrasena("654321");

        verify(mockUser, times(1)).newContrasena("654321");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testModificarContrasena_nula() {
        ctrlDominioUsuario.modificarContrasena(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testModificarContrasena_vacia() {
        ctrlDominioUsuario.modificarContrasena("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testModificarContrasena_corta() {
        ctrlDominioUsuario.modificarContrasena("123");
    }

    @Test(expected = IllegalStateException.class)
    public void testModificarContrasenaIgualActual() {
        
        Usuario mockUser = mock(Usuario.class);
        when(mockUsuarios.get("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("123456")).thenReturn(true);
        when(mockUser.getNomUsuario()).thenReturn("Alessandro");

        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");

        doThrow(new IllegalStateException("La nueva contraseña es igual a la actual"))
            .when(mockUser).newContrasena("123456");

        ctrlDominioUsuario.modificarContrasena("123456");
    }


    @Test
    public void testGetEncuestasRespondidas_OK() {
        // Mocks 
        CtrlDominioRespuesta mockCtrlResp = mock(CtrlDominioRespuesta.class);
        CtrlDominioPregunta mockCtrlPreg = mock(CtrlDominioPregunta.class);
        Usuario mockUser = mock(Usuario.class);
        Respuesta mockR1 = mock(Respuesta.class);
        Respuesta mockR2 = mock(Respuesta.class);
        Pregunta mockP1 = mock(Pregunta.class);
        Pregunta mockP2 = mock(Pregunta.class);

        
        when(mockUsuarios.get("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("123456")).thenReturn(true);
        when(mockUser.getNomUsuario()).thenReturn("Alessandro");
        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");

        // Simular IDs de respuestas del usuario
        when(mockUser.getIdRespuestas()).thenReturn(new ArrayList<>(Arrays.asList(1, 2)));

        // Simular las respuestas y preguntas
        when(mockCtrlResp.getRespuesta(1)).thenReturn(mockR1);
        when(mockCtrlResp.getRespuesta(2)).thenReturn(mockR2);
        when(mockR1.getIdPregunta()).thenReturn(10);
        when(mockR2.getIdPregunta()).thenReturn(20);
        when(mockCtrlPreg.getPregunta(10)).thenReturn(mockP1);
        when(mockCtrlPreg.getPregunta(20)).thenReturn(mockP2);
        when(mockP1.getIdEncuesta()).thenReturn(100);
        when(mockP2.getIdEncuesta()).thenReturn(200);

      
        List<Integer> resultado = ctrlDominioUsuario.getEncuestasRespondidas(mockCtrlResp, mockCtrlPreg);

        
        assertEquals("Size no coincide", 2, resultado.size());
        assertTrue("No da id bueno", resultado.contains(100));
        assertTrue("No da id bueno", resultado.contains(200));

        // Verificaciones opcionales
        verify(mockUser).getIdRespuestas(); //times 1
        verify(mockCtrlResp, times(2)).getRespuesta(anyInt());
        verify(mockCtrlPreg, times(2)).getPregunta(anyInt());
    }


    @Test
    public void testGetRespuestasEncuesta() {
        CtrlDominioRespuesta mockCtrlResp = mock(CtrlDominioRespuesta.class);
        CtrlDominioPregunta mockCtrlPreg = mock(CtrlDominioPregunta.class);
        Usuario mockUser = mock(Usuario.class);
        Respuesta mockR1 = mock(Respuesta.class);
        Respuesta mockR2 = mock(Respuesta.class);
        Respuesta mockR3 = mock(Respuesta.class);
        Pregunta mockP1 = mock(Pregunta.class);
        Pregunta mockP2 = mock(Pregunta.class);
        Pregunta mockP3 = mock(Pregunta.class);

        when(mockUsuarios.get("Alessandro")).thenReturn(mockUser);
        when(mockUser.getIdRespuestas()).thenReturn(new ArrayList<>(Arrays.asList(1, 2, 3)));

        when(mockCtrlResp.getRespuesta(1)).thenReturn(mockR1);
        when(mockCtrlResp.getRespuesta(2)).thenReturn(mockR2);
        when(mockCtrlResp.getRespuesta(3)).thenReturn(mockR3);

        when(mockR1.getIdPregunta()).thenReturn(10);
        when(mockR2.getIdPregunta()).thenReturn(20);
        when(mockR3.getIdPregunta()).thenReturn(30);

        when(mockCtrlPreg.getPregunta(10)).thenReturn(mockP1);
        when(mockCtrlPreg.getPregunta(20)).thenReturn(mockP2);
        when(mockCtrlPreg.getPregunta(30)).thenReturn(mockP3);

        when(mockP1.getIdEncuesta()).thenReturn(100);
        when(mockP2.getIdEncuesta()).thenReturn(200);
        when(mockP3.getIdEncuesta()).thenReturn(100);

        List<Respuesta> resultado = ctrlDominioUsuario.getRespuestasEncuesta("Alessandro", 100, mockCtrlResp, mockCtrlPreg);

        assertEquals("Size no coincide", 2, resultado.size());
        assertEquals("no devuelve en el lugar", mockR1, resultado.get(0));
        assertEquals("no devuelve en el lugar", mockR3, resultado.get(1));

        verify(mockUser).getIdRespuestas();
        verify(mockCtrlResp, times(3)).getRespuesta(anyInt());
        verify(mockCtrlPreg, times(3)).getPregunta(anyInt());
    }

    @Test
    public void testGetAllUsuariosDevuelveCopia() {
        TreeMap<String, Usuario> users = new TreeMap<>();
        users.put("Alessandro", new Usuario("Alessandro", "123456"));
        users.put("Denis", new Usuario("Denis", "654321"));

        ctrlDominioUsuario.cargarUsuarios(users);

        TreeMap<String, Usuario> copia = ctrlDominioUsuario.getAllUsuarios();

        assertEquals("Size no coinciden", 2, copia.size());
        assertNotSame("No son iguales", users, copia);
    }

    @Test
    public void testCargarUsuarios() {
        TreeMap<String, Usuario> users = new TreeMap<>();
        users.put("Alessandro", new Usuario("Alessandro", "123456"));
        users.put("Denis", new Usuario("Denis", "654321"));

        ctrlDominioUsuario.cargarUsuarios(users);

        TreeMap<String, Usuario> copia = ctrlDominioUsuario.getAllUsuarios();

        assertEquals("Size no coinciden", 2, copia.size());
        assertNotSame("No son iguales", users, copia);
    }
  
}
