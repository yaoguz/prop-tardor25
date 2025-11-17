package domaincontrollers;

import domain.*;
import persistence.*;
import java.lang.reflect.Field;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
//import org.mockito.MockitoAnnotations; o runner o este
import org.mockito.junit.MockitoJUnitRunner;

/**
 * Pruebas del controlador CtrlDominioUsuario.
 * @author Alessandro Jin
 */

@RunWith(MockitoJUnitRunner.class)
public class TestCtrlDominioUsuario {
    @Mock
    private GestorPersistencia mockGestor;

    @Mock
    private CtrlDominioRespuesta mockCtrlDominioRespuesta;

    @Mock 
    private CtrlDominioPregunta mockCtrlDominioPregunta;
    
    private CtrlDominioUsuario ctrlDominioUsuario;   

    /**
     * Inicializa el controlador antes de cada test.
     */
    @Before
    public void setUp() throws Exception {
        // Resetear singleton
        Field instance = CtrlDominioUsuario.class.getDeclaredField("singletonInstance");
        instance.setAccessible(true);
        instance.set(null, null);
   
        ctrlDominioUsuario = CtrlDominioUsuario.getInstance(mockGestor, mockCtrlDominioRespuesta, mockCtrlDominioPregunta);
    }

    /**
     * Verifica que existeUsuario retorna true.
     */
    @Test
    public void testExisteUsuarioTrue() {
        when(mockGestor.existeUsuario("Alessandro")).thenReturn(true);

        boolean resultado = ctrlDominioUsuario.existeUsuario("Alessandro");

        assertTrue("No muestra un usuario existente", resultado);
        verify(mockGestor, times(1)).existeUsuario("Alessandro"); // verificar interacción
	}

    /**
     * Verifica que existeUsuario retorna false.
     */
    @Test 
    public void testExisteUsuarioFalse() {
        when(mockGestor.existeUsuario("Yaoyao")).thenReturn(false);

        boolean resultado = ctrlDominioUsuario.existeUsuario("Yaoyao");

        assertFalse("Muestra un usuario No existente", resultado);
        verify(mockGestor, times(1)).existeUsuario("Yaoyao"); // verificar interacción
    }

    /**
     * Comprueba obtención de usuario existente.
     */
    @Test
    public void testGetUsuarioSinError() {
        
        Usuario mockUser = mock(Usuario.class);
        when(mockGestor.getUsuario("Alessandro")).thenReturn(mockUser);
        
        Usuario resultado = ctrlDominioUsuario.getUsuario("Alessandro");

        assertEquals("Ha sacado un usuario incorrecto", mockUser, resultado);   // compara que es el mismo usuario
        verify(mockGestor, times(1)).getUsuario("Alessandro");
	}

    /**
     * Obtener usuario inexistente lanza excepción.
     */
    @Test (expected=IllegalArgumentException.class) 
    public void testGetUsuarioConError() {
            
        when(mockGestor.getUsuario("Alessandro")).thenReturn(null);

        Usuario resultado = ctrlDominioUsuario.getUsuario("Alessandro");
        // falta verificar?
    }

    /**
     * Verifica registro de usuario correctamente.
     */
    @Test
    public void testRegistrarUsuarioBueno() {
        when(mockGestor.existeUsuario("Alessandro")).thenReturn(false);
        
        String nom = ctrlDominioUsuario.registrarUsuario("Alessandro", "123456", "123456");

        assertEquals("Guarda nombre incorrecto", "Alessandro", nom);
        verify(mockGestor).existeUsuario("Alessandro");
        verify(mockGestor, times(1)).guardarUsuario(any(Usuario.class));
    }

    /** Registrar usuario con nombre vacío lanza IllegalArgumentException. */
    @Test(expected = IllegalArgumentException.class)
    public void testRegistrarUsuarioNombreVacio() {
     
        ctrlDominioUsuario.registrarUsuario("", "123456", "123456");
    }

    /** Registrar usuario con contraseña vacía lanza IllegalArgumentException. */
    @Test(expected = IllegalArgumentException.class)
    public void testRegistrarUsuarioPasswordVacia() {
      
        ctrlDominioUsuario.registrarUsuario("Alessandro", "", "123456");
    }

    /** Registrar usuario con confirmación vacía lanza IllegalArgumentException. */
    @Test(expected = IllegalArgumentException.class)
    public void testRegistrarUsuarioConfirmVacia() {
       
        ctrlDominioUsuario.registrarUsuario("Alessandro", "123456", "");
    }

    /** Registrar usuario con nombre corto lanza IllegalArgumentException. */
    @Test(expected = IllegalArgumentException.class)
    public void testRegistrarUsuarioNombreCorto() {
     
        ctrlDominioUsuario.registrarUsuario("al", "123456", "123456");
    }

    /** Registrar usuario con contraseña corta lanza IllegalArgumentException. */
    @Test(expected = IllegalArgumentException.class)
    public void testRegistrarUsuarioPasswordCorta() {
        ctrlDominioUsuario.registrarUsuario("Alessandro", "12345", "12345");
    }

    /** Registrar usuario con contraseñas que no coinciden lanza IllegalStateException. */
    @Test(expected = IllegalStateException.class)
    public void testRegistrarUsuarioPasswordNoCoincide() {
 
        ctrlDominioUsuario.registrarUsuario("Alessandro", "123456", "654321");
    }

    /** Registrar usuario duplicado lanza IllegalStateException. */
    @Test(expected = IllegalStateException.class)
    public void testRegistrarUsuarioUsuarioDuplicado() {

        when(mockGestor.existeUsuario("Alessandro")).thenReturn(true);
      
        ctrlDominioUsuario.registrarUsuario("Alessandro", "123456", "123456");
       
    }


    /** Iniciar sesión con credenciales válidas activa al usuario. */
    @Test
    public void testIniciarSesionBueno() {
        
        Usuario mockUser = mock(Usuario.class);
        when(mockGestor.getUsuario("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("123456")).thenReturn(true);
        when(mockUser.getNomUsuario()).thenReturn("Alessandro");

        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");

      
        assertEquals("No se activo el usuario", "Alessandro", ctrlDominioUsuario.getUsuarioActivo());

        verify(mockGestor, times(1)).getUsuario("Alessandro");
        verify(mockUser, times(1)).comprobarContrasena("123456");
    }

    /** Intentar iniciar sesión cuando ya hay usuario activo lanza IllegalStateException. */
    @Test(expected = IllegalStateException.class)
    public void testIniciarSesionYaHayUsuarioActivo() {

        Usuario mockUser = mock(Usuario.class);
        when(mockGestor.getUsuario("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("123456")).thenReturn(true);
        when(mockUser.getNomUsuario()).thenReturn("Alessandro");

        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");
    
        assertEquals("No se activo el usuario","Alessandro", ctrlDominioUsuario.getUsuarioActivo());

        // intentar iniciar sesión con otro nombre 
        ctrlDominioUsuario.iniciarSesion("Yaoyao", "123456");
    }


    /** Iniciar sesión con contraseña incorrecta lanza IllegalStateException. */
    @Test(expected = IllegalStateException.class)
    public void testIniciarSesionContrasenaIncorrecta() {
        
        Usuario mockUser = mock(Usuario.class);
        when(mockGestor.getUsuario("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("654321")).thenReturn(false);

     
        ctrlDominioUsuario.iniciarSesion("Alessandro", "654321");

   
    }

    /** Iniciar sesión con usuario inexistente lanza IllegalArgumentException. */
    @Test(expected = IllegalArgumentException.class)
    public void testIniciarSesionUsuarioNoExiste() {
        // usar mockUsuarios -> devuelve null por defecto para get()
        when(mockGestor.getUsuario("Alessandro")).thenReturn(null);
        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");
    }


    /** Devuelve null si no hay usuario activo. */
    @Test
    public void testGetUsuarioActivoSinSesion() {
        
        assertNull("Debe devolver null si no hay usuario activo", ctrlDominioUsuario.getUsuarioActivo());
    }

    /** Devuelve el usuario activo tras iniciar sesión. */
    @Test
    public void testGetUsuarioActivoConSesion() {

        Usuario mockUser = mock(Usuario.class);  
        when(mockGestor.getUsuario("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("123456")).thenReturn(true);
        when(mockUser.getNomUsuario()).thenReturn("Alessandro");

        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");

        String activo = ctrlDominioUsuario.getUsuarioActivo();
        assertEquals("Debe devolver el usuario activo", "Alessandro", activo);

        
    }

    /** Cierra la sesión actual y deja usuario activo a null. */
    @Test
    public void testCerrarSesionActual() {
        
        Usuario mockUser = mock(Usuario.class);
        when(mockGestor.getUsuario("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("123456")).thenReturn(true);
        when(mockUser.getNomUsuario()).thenReturn("Alessandro");

        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");
        assertEquals("Debe estar logueado antes de cerrar sesión", "Alessandro", ctrlDominioUsuario.getUsuarioActivo());

        ctrlDominioUsuario.cerrarSesionActual();

        assertNull("Debe quedar null después de cerrar sesión", ctrlDominioUsuario.getUsuarioActivo());
    }

    /** Modifica la contraseña cuando la nueva es válida. */
    @Test
    public void testModificarContrasenaValida() {

        Usuario mockUser = mock(Usuario.class);
        when(mockGestor.getUsuario("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("123456")).thenReturn(true);
        when(mockUser.getNomUsuario()).thenReturn("Alessandro");

        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");
        ctrlDominioUsuario.modificarContrasena("654321");

        verify(mockUser, times(1)).newContrasena("654321");
        verify(mockGestor, times(1)).actualizarUsuario(mockUser);
    }

    /** Modificar contraseña nula lanza IllegalArgumentException. */
    @Test(expected = IllegalArgumentException.class)
    public void testModificarContrasena_nula() {
        Usuario mockUser = mock(Usuario.class);
        when(mockGestor.getUsuario("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("123456")).thenReturn(true);
        when(mockUser.getNomUsuario()).thenReturn("Alessandro");
        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");
        
        ctrlDominioUsuario.modificarContrasena(null);
    }

    /** Modificar contraseña vacía lanza IllegalArgumentException. */
    @Test(expected = IllegalArgumentException.class)
    public void testModificarContrasena_vacia() {
        Usuario mockUser = mock(Usuario.class);
        when(mockGestor.getUsuario("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("123456")).thenReturn(true);
        when(mockUser.getNomUsuario()).thenReturn("Alessandro");
        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");
        
        ctrlDominioUsuario.modificarContrasena("");
    }

    /** Modificar contraseña corta lanza IllegalArgumentException. */
    @Test(expected = IllegalArgumentException.class)
    public void testModificarContrasena_corta() {
        Usuario mockUser = mock(Usuario.class);
        when(mockGestor.getUsuario("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("123456")).thenReturn(true);
        when(mockUser.getNomUsuario()).thenReturn("Alessandro");
        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");
        
        ctrlDominioUsuario.modificarContrasena("123");
    }

    /** Modificar contraseña idéntica a la actual lanza IllegalStateException. */
    @Test(expected = IllegalStateException.class)
    public void testModificarContrasenaIgualActual() {
        
        Usuario mockUser = mock(Usuario.class);
        when(mockGestor.getUsuario("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("123456")).thenReturn(true);
        when(mockUser.getNomUsuario()).thenReturn("Alessandro");

        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");

        doThrow(new IllegalStateException("La nueva contraseña es igual a la actual")).when(mockUser).newContrasena("123456");

        ctrlDominioUsuario.modificarContrasena("123456");
    }


    /** Devuelve las encuestas respondidas por el usuario activo. */
    @Test
    public void testGetEncuestasRespondidas() {
        // Mocks 
        Usuario mockUser = mock(Usuario.class);
        Respuesta mockR1 = mock(Respuesta.class);
        Respuesta mockR2 = mock(Respuesta.class);
        Pregunta mockP1 = mock(Pregunta.class);
        Pregunta mockP2 = mock(Pregunta.class);

        
        when(mockGestor.getUsuario("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("123456")).thenReturn(true);
        when(mockUser.getNomUsuario()).thenReturn("Alessandro");
        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");

        // Simular IDs de respuestas del usuario
        when(mockUser.getIdRespuestas()).thenReturn(new ArrayList<>(Arrays.asList(1, 2)));

        // Simular las respuestas y preguntas
        when(mockCtrlDominioRespuesta.getRespuesta(1)).thenReturn(mockR1);
        when(mockCtrlDominioRespuesta.getRespuesta(2)).thenReturn(mockR2);
        when(mockR1.getIdPregunta()).thenReturn(10);
        when(mockR2.getIdPregunta()).thenReturn(20);
        when(mockCtrlDominioPregunta.getPregunta(10)).thenReturn(mockP1);
        when(mockCtrlDominioPregunta.getPregunta(20)).thenReturn(mockP2);
        when(mockP1.getIdEncuesta()).thenReturn(100);
        when(mockP2.getIdEncuesta()).thenReturn(200);

      
        List<Integer> resultado = ctrlDominioUsuario.getEncuestasRespondidas();

        
        assertEquals("Size no coincide", 2, resultado.size());
        assertTrue("No da id bueno", resultado.contains(100));
        assertTrue("No da id bueno", resultado.contains(200));

        // Verificaciones opcionales
        verify(mockUser).getIdRespuestas(); //times 1
        verify(mockCtrlDominioRespuesta, times(2)).getRespuesta(anyInt());
        verify(mockCtrlDominioPregunta, times(2)).getPregunta(anyInt());
    }


    /** Devuelve las respuestas del usuario filtradas por encuesta. */
    @Test
    public void testGetRespuestasEncuesta() {
     
        Usuario mockUser = mock(Usuario.class);
        Respuesta mockR1 = mock(Respuesta.class);
        Respuesta mockR2 = mock(Respuesta.class);
        Respuesta mockR3 = mock(Respuesta.class);
        Pregunta mockP1 = mock(Pregunta.class);
        Pregunta mockP2 = mock(Pregunta.class);
        Pregunta mockP3 = mock(Pregunta.class);

        when(mockGestor.getUsuario("Alessandro")).thenReturn(mockUser);
        when(mockUser.getIdRespuestas()).thenReturn(new ArrayList<>(Arrays.asList(1, 2, 3)));

        when(mockCtrlDominioRespuesta.getRespuesta(1)).thenReturn(mockR1);
        when(mockCtrlDominioRespuesta.getRespuesta(2)).thenReturn(mockR2);
        when(mockCtrlDominioRespuesta.getRespuesta(3)).thenReturn(mockR3);

        when(mockR1.getIdPregunta()).thenReturn(10);
        when(mockR2.getIdPregunta()).thenReturn(20);
        when(mockR3.getIdPregunta()).thenReturn(30);

        when(mockCtrlDominioPregunta.getPregunta(10)).thenReturn(mockP1);
        when(mockCtrlDominioPregunta.getPregunta(20)).thenReturn(mockP2);
        when(mockCtrlDominioPregunta.getPregunta(30)).thenReturn(mockP3);

        when(mockP1.getIdEncuesta()).thenReturn(100);
        when(mockP2.getIdEncuesta()).thenReturn(200);
        when(mockP3.getIdEncuesta()).thenReturn(100);

        List<Respuesta> resultado = ctrlDominioUsuario.getRespuestasEncuesta("Alessandro", 100);

        assertEquals("Size no coincide", 2, resultado.size());
        assertEquals("no devuelve en el lugar", mockR1, resultado.get(0));
        assertEquals("no devuelve en el lugar", mockR3, resultado.get(1));

        verify(mockUser).getIdRespuestas();
        verify(mockCtrlDominioRespuesta, times(3)).getRespuesta(anyInt());
        verify(mockCtrlDominioPregunta, times(3)).getPregunta(anyInt());
    }

    @Test
    public void testGetAllUsuariosDevuelveCopia() {
        Usuario mockUser1 = mock(Usuario.class);
        Usuario mockUser2 = mock(Usuario.class);
        when(mockUser1.getNomUsuario()).thenReturn("user1");
        when(mockUser2.getNomUsuario()).thenReturn("user2");
        ArrayList<Usuario> users = new ArrayList<>(Arrays.asList(mockUser1, mockUser2));
        when(mockGestor.getUsuarios()).thenReturn(users);

        Map<String, Usuario> copia = ctrlDominioUsuario.getAllUsuarios();

        assertEquals("Size no coinciden", 2, copia.size());
        assertNotSame("No son iguales", users, copia);
    }
  
}
