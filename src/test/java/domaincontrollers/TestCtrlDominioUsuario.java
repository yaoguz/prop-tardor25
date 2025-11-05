package domaincontroller;

import main.domini.*;
import java.util.*;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.MockitoJUnitRunner;

import persistence.IGestorPersistencia;
import domain.Usuario;
import domaincontrollers.CtrlDominioUsuario;
import domaincontrollers.CtrlUsuario;

/**
 * Tests unitarios para CtrlDominioUsuario usando MOCKS con JUnit 4.
 * 
 * Demuestra cómo usar Mockito para simular dependencias y verificar interacciones.
 * 
 * @author Equipo PROP
 * @version 2.0 - JUnit 4
 */
@RunWith(MockitoJUnitRunner.class)
public class TestCtrlDominioUsuario {
    
    @Mock
    private CtrlUsuario mockCtrlUsuario;
    
    @Mock
    private IGestorPersistencia mockPersistence;
    
    private CtrlDominioUsuario ctrlDominioUsuario;
    
    /**
     * Configuración inicial antes de cada test.
     * Inicializa los mocks y el controlador bajo prueba.
     */
    @Before
    public void setUp() {
        // Inicializar mocks de Mockito (opcional con @RunWith(MockitoJUnitRunner.class))
        MockitoAnnotations.initMocks(this);
        
        // Crear el controlador bajo prueba con dependencias mockeadas
        ctrlDominioUsuario = new CtrlDominioUsuario(mockCtrlUsuario, mockPersistence);
    }
    
    // ==================== TESTS DE REGISTRO ====================
    
    /**
     * Test: Registrar usuario con contraseñas coincidentes debe crear usuario y guardarlo.
     */
    @Test
    public void testRegistrarUsuario_ContrasenasCoinciden_CreaYGuardaUsuario() {
        // ARRANGE (Preparar)
        String nomUsuario = "testUser";
        String contrasena = "password123";
        String confirmacion = "password123";
        
        // Configurar comportamiento del mock: cuando se llame a existeUsuario, devolver false
        when(mockCtrlUsuario.existeUsuario(nomUsuario)).thenReturn(false);
        
        // Configurar que crearUsuario devuelva un usuario específico
        Usuario usuarioEsperado = new Usuario(nomUsuario, contrasena);
        when(mockCtrlUsuario.crearUsuario(nomUsuario, contrasena)).thenReturn(usuarioEsperado);
        
        // ACT (Actuar)
        String resultado = ctrlDominioUsuario.registrarUsuario(nomUsuario, contrasena, confirmacion);
        
        // ASSERT (Verificar)
        assertEquals("Usuario registrado con éxito: " + nomUsuario, resultado);
        
        // VERIFICAR INTERACCIONES: comprobar que se llamaron los métodos correctos
        verify(mockCtrlUsuario, times(1)).existeUsuario(nomUsuario);
        verify(mockCtrlUsuario, times(1)).crearUsuario(nomUsuario, contrasena);
        verify(mockPersistence, times(1)).guardarUsuario(usuarioEsperado);
    }
    
    /**
     * Test: Registrar usuario ya existente debe lanzar excepción.
     */
    @Test
    public void testRegistrarUsuario_UsuarioExistente_LanzaExcepcion() {
        // ARRANGE
        String nomUsuario = "existingUser";
        String contrasena = "password123";
        String confirmacion = "password123";
        
        // Configurar mock: usuario ya existe
        when(mockCtrlUsuario.existeUsuario(nomUsuario)).thenReturn(true);
        
        // ACT & ASSERT
        try {
            ctrlDominioUsuario.registrarUsuario(nomUsuario, contrasena, confirmacion);
            fail("Debería haber lanzado IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Error: el nombre de usuario ya existe", e.getMessage());
        }
        
        // VERIFICAR: NO se debe haber llamado a crearUsuario ni guardarUsuario
        verify(mockCtrlUsuario, never()).crearUsuario(anyString(), anyString());
        verify(mockPersistence, never()).guardarUsuario(any(Usuario.class));
    }
    
    /**
     * Test: Registrar usuario con contraseñas diferentes debe lanzar excepción.
     */
    @Test
    public void testRegistrarUsuario_ContrasenasNoCoinciden_LanzaExcepcion() {
        // ARRANGE
        String nomUsuario = "testUser";
        String contrasena = "password123";
        String confirmacion = "differentPassword";
        
        when(mockCtrlUsuario.existeUsuario(nomUsuario)).thenReturn(false);
        
        // ACT & ASSERT
        try {
            ctrlDominioUsuario.registrarUsuario(nomUsuario, contrasena, confirmacion);
            fail("Debería haber lanzado IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Error: las contraseñas no coinciden", e.getMessage());
        }
        
        // VERIFICAR: NO se debe haber creado ni guardado usuario
        verify(mockCtrlUsuario, never()).crearUsuario(anyString(), anyString());
        verify(mockPersistence, never()).guardarUsuario(any(Usuario.class));
    }
    
    // ==================== TESTS DE INICIO DE SESIÓN ====================
    
    /**
     * Test: Iniciar sesión con credenciales correctas debe retornar true.
     */
    @Test
    public void testIniciarSesion_CredencialesCorrectas_RetornaTrue() {
        // ARRANGE
        String nomUsuario = "testUser";
        String contrasena = "password123";
        
        Usuario mockUsuario = mock(Usuario.class);
        when(mockCtrlUsuario.existeUsuario(nomUsuario)).thenReturn(true);
        when(mockCtrlUsuario.getUsuario(nomUsuario)).thenReturn(mockUsuario);
        when(mockUsuario.comprobarContrasena(contrasena)).thenReturn(true);
        
        // ACT
        boolean resultado = ctrlDominioUsuario.iniciarSesion(nomUsuario, contrasena);
        
        // ASSERT
        assertTrue("El inicio de sesión debería ser exitoso", resultado);
        
        // VERIFICAR INTERACCIONES
        verify(mockCtrlUsuario).existeUsuario(nomUsuario);
        verify(mockCtrlUsuario).getUsuario(nomUsuario);
        verify(mockUsuario).comprobarContrasena(contrasena);
    }
    
    /**
     * Test: Iniciar sesión con usuario inexistente debe retornar false.
     */
    @Test
    public void testIniciarSesion_UsuarioInexistente_RetornaFalse() {
        // ARRANGE
        String nomUsuario = "nonExistentUser";
        String contrasena = "password123";
        
        when(mockCtrlUsuario.existeUsuario(nomUsuario)).thenReturn(false);
        
        // ACT
        boolean resultado = ctrlDominioUsuario.iniciarSesion(nomUsuario, contrasena);
        
        // ASSERT
        assertFalse("El inicio de sesión debería fallar", resultado);
        
        // VERIFICAR: NO se debe haber llamado a getUsuario
        verify(mockCtrlUsuario, never()).getUsuario(anyString());
    }
    
    /**
     * Test: Iniciar sesión con contraseña incorrecta debe retornar false.
     */
    @Test
    public void testIniciarSesion_ContrasenaIncorrecta_RetornaFalse() {
        // ARRANGE
        String nomUsuario = "testUser";
        String contrasena = "wrongPassword";
        
        Usuario mockUsuario = mock(Usuario.class);
        when(mockCtrlUsuario.existeUsuario(nomUsuario)).thenReturn(true);
        when(mockCtrlUsuario.getUsuario(nomUsuario)).thenReturn(mockUsuario);
        when(mockUsuario.comprobarContrasena(contrasena)).thenReturn(false);
        
        // ACT
        boolean resultado = ctrlDominioUsuario.iniciarSesion(nomUsuario, contrasena);
        
        // ASSERT
        assertFalse("El inicio de sesión debería fallar con contraseña incorrecta", resultado);
    }
    
    // ==================== TESTS DE MODIFICAR CONTRASEÑA ====================
    
    /**
     * Test: Modificar contraseña debe actualizar el usuario y persistirlo.
     */
    @Test
    public void testModificarContrasena_ActualizaYPersiste() {
        // ARRANGE
        String nomUsuario = "testUser";
        String nuevaContrasena = "newPassword456";
        
        Usuario mockUsuario = mock(Usuario.class);
        when(mockCtrlUsuario.getUsuario(nomUsuario)).thenReturn(mockUsuario);
        
        // ACT
        ctrlDominioUsuario.modificarContrasena(nomUsuario, nuevaContrasena);
        
        // ASSERT - VERIFICAR INTERACCIONES
        verify(mockCtrlUsuario).getUsuario(nomUsuario);
        verify(mockUsuario).newContrasena(nuevaContrasena);
        verify(mockPersistence).guardarUsuario(mockUsuario);
    }
    
    // ==================== TESTS DE CERRAR SESIÓN ====================
    
    /**
     * Test: Cerrar sesión debe guardar el estado del usuario.
     */
    @Test
    public void testCerrarSesion_GuardaEstadoUsuario() {
        // ARRANGE
        String nomUsuario = "testUser";
        
        Usuario mockUsuario = mock(Usuario.class);
        when(mockCtrlUsuario.getUsuario(nomUsuario)).thenReturn(mockUsuario);
        
        // ACT
        ctrlDominioUsuario.cerrarSesion(nomUsuario);
        
        // ASSERT - VERIFICAR INTERACCIONES
        verify(mockCtrlUsuario).getUsuario(nomUsuario);
        verify(mockPersistence).guardarUsuario(mockUsuario);
    }
}
