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

    @Before
    public void setUp() throws Exception {
        // Resetear singleton
        Field instance = CtrlDominioUsuario.class.getDeclaredField("singletonInstance");
        instance.setAccessible(true);
        instance.set(null, null);
   
        ctrlDominioUsuario = CtrlDominioUsuario.getInstance(mockGestor, mockCtrlDominioRespuesta, mockCtrlDominioPregunta);
    }

    /**
     * Objeto de la prueba: existeUsuario()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Verificación de existencia de usuario
     * Operativa: Mock retorna true, se verifica que el método retorna true
     */
    @Test
    public void testExisteUsuarioTrue() {
        when(mockGestor.existeUsuario("Alessandro")).thenReturn(true);

        boolean resultado = ctrlDominioUsuario.existeUsuario("Alessandro");

        assertTrue("No muestra un usuario existente", resultado);
        verify(mockGestor, times(1)).existeUsuario("Alessandro"); // verificar interacción
	}

    /**
     * Objeto de la prueba: existeUsuario()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Caso en que el usuario no existe
     * Operativa: Mock retorna false, se verifica comportamiento correcto
     */
    @Test 
    public void testExisteUsuarioFalse() {
        when(mockGestor.existeUsuario("Yaoyao")).thenReturn(false);

        boolean resultado = ctrlDominioUsuario.existeUsuario("Yaoyao");

        assertFalse("Muestra un usuario No existente", resultado);
        verify(mockGestor, times(1)).existeUsuario("Yaoyao"); // verificar interacción
    }

    /**
     * Objeto de la prueba: getUsuario()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Obtención correcta de usuario por nombre
     * Operativa: Mock retorna usuario, se verifica obtención
     */
    @Test
    public void testGetUsuarioSinError() {
        
        Usuario mockUser = mock(Usuario.class);
        when(mockGestor.getUsuario("Alessandro")).thenReturn(mockUser);
        
        Usuario resultado = ctrlDominioUsuario.getUsuario("Alessandro");

        assertEquals("Ha sacado un usuario incorrecto", mockUser, resultado);   // compara que es el mismo usuario
        verify(mockGestor, times(1)).getUsuario("Alessandro");
	}

    @Test (expected=IllegalArgumentException.class) 
    public void testGetUsuarioConError() {
            
        when(mockGestor.getUsuario("Alessandro")).thenReturn(null);

        Usuario resultado = ctrlDominioUsuario.getUsuario("Alessandro");
        // falta verificar?
    }

    /**
     * Objeto de la prueba: registrarUsuario()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Registro correcto de nuevo usuario
     * Operativa: Mocks configurados, se registra usuario y se verifica persistencia
     */
    @Test
    public void testRegistrarUsuarioBueno() {
        when(mockGestor.existeUsuario("Alessandro")).thenReturn(false);
        
        String nom = ctrlDominioUsuario.registrarUsuario("Alessandro", "123456", "123456");

        assertEquals("Guarda nombre incorrecto", "Alessandro", nom);
        verify(mockGestor).existeUsuario("Alessandro");
        verify(mockGestor, times(1)).guardarUsuario(any(Usuario.class));
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

        when(mockGestor.existeUsuario("Alessandro")).thenReturn(true);
      
        ctrlDominioUsuario.registrarUsuario("Alessandro", "123456", "123456");
       
    }


    /**
     * Objeto de la prueba: iniciarSesion()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Inicio de sesión correcto con credenciales válidas
     * Operativa: Mocks de usuario y contraseña, se inicia sesión y se verifica usuario activo
     */
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


    @Test(expected = IllegalStateException.class)
    public void testIniciarSesionContrasenaIncorrecta() {
        
        Usuario mockUser = mock(Usuario.class);
        when(mockGestor.getUsuario("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("654321")).thenReturn(false);

     
        ctrlDominioUsuario.iniciarSesion("Alessandro", "654321");

   
    }

    @Test(expected = IllegalArgumentException.class)
    public void testIniciarSesionUsuarioNoExiste() {
        // usar mockUsuarios -> devuelve null por defecto para get()
        when(mockGestor.getUsuario("Alessandro")).thenReturn(null);
        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");
    }


    /**
     * Objeto de la prueba: getUsuarioActivo()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Caso en que no hay usuario con sesión activa
     * Operativa: Sin iniciar sesión, se verifica que retorna null
     */
    @Test
    public void testGetUsuarioActivoSinSesion() {
        
        assertNull("Debe devolver null si no hay usuario activo", ctrlDominioUsuario.getUsuarioActivo());
    }

    /**
     * Objeto de la prueba: getUsuarioActivo()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Obtención del usuario activo con sesión iniciada
     * Operativa: Se inicia sesión, se verifica que getUsuarioActivo retorna el nombre correcto
     */
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

    /**
     * Objeto de la prueba: cerrarSesionActual()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Cierre correcto de sesión activa
     * Operativa: Se inicia sesión, se cierra y se verifica que usuario activo queda null
     */
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

    /**
     * Objeto de la prueba: modificarContrasena()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Modificación correcta de contraseña
     * Operativa: Usuario con sesión activa modifica contraseña, se verifican llamadas
     */
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

    @Test(expected = IllegalArgumentException.class)
    public void testModificarContrasena_nula() {
        Usuario mockUser = mock(Usuario.class);
        when(mockGestor.getUsuario("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("123456")).thenReturn(true);
        when(mockUser.getNomUsuario()).thenReturn("Alessandro");
        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");
        
        ctrlDominioUsuario.modificarContrasena(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testModificarContrasena_vacia() {
        Usuario mockUser = mock(Usuario.class);
        when(mockGestor.getUsuario("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("123456")).thenReturn(true);
        when(mockUser.getNomUsuario()).thenReturn("Alessandro");
        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");
        
        ctrlDominioUsuario.modificarContrasena("");
    }

    @Test(expected = IllegalArgumentException.class)
    public void testModificarContrasena_corta() {
        Usuario mockUser = mock(Usuario.class);
        when(mockGestor.getUsuario("Alessandro")).thenReturn(mockUser);
        when(mockUser.comprobarContrasena("123456")).thenReturn(true);
        when(mockUser.getNomUsuario()).thenReturn("Alessandro");
        ctrlDominioUsuario.iniciarSesion("Alessandro", "123456");
        
        ctrlDominioUsuario.modificarContrasena("123");
    }

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


    /**
     * Objeto de la prueba: getEncuestasRespondidas()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Obtención de IDs de encuestas respondidas por usuario activo
     * Operativa: Mocks de respuestas y preguntas, se obtienen encuestas y se verifica lista
     */
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


    /**
     * Objeto de la prueba: getRespuestasEncuesta()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Obtención de respuestas de un usuario para una encuesta específica
     * Operativa: Mocks de respuestas de usuario a 2 encuestas, se filtran por encuesta
     */
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

    /**
     * Objeto de la prueba: getAllUsuarios()
     * Ficheros de datos necesarios: Ninguno
     * Valores estudiados: Obtención de todos los usuarios como copia
     * Operativa: Mock retorna lista de usuarios, se verifica que devuelve copia independiente
     */
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
