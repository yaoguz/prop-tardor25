package examples;

/**
 * EJEMPLO COMPARATIVO: STUB vs MOCK
 * 
 * Este archivo demuestra la diferencia entre usar stubs y mocks
 * para testing de CtrlDominioUsuario.
 * 
 * @author Equipo PROP
 * @version 1.0
 */

// ============================================================================
// ESCENARIO: Testear el método registrarUsuario() de CtrlDominioUsuario
// ============================================================================

/*
Código bajo prueba (CtrlDominioUsuario.java):

public String registrarUsuario(String nomU, String contrasena, String confirmCont) {
    // 1. Verificar que el usuario no existe
    if (ctrlUsuario.existeUsuario(nomU)) {
        throw new IllegalArgumentException("Error: el nombre de usuario ya existe");
    }
    
    // 2. Verificar que las contraseñas coinciden
    if (!contrasena.equals(confirmCont)) {
        throw new IllegalArgumentException("Error: las contraseñas no coinciden");
    }
    
    // 3. Crear el usuario
    Usuario u = ctrlUsuario.crearUsuario(nomU, contrasena);
    
    // 4. Guardar en persistencia
    ctrlPersistence.guardarUsuario(u);
    
    return "Usuario registrado con éxito: " + nomU;
}
*/

// ============================================================================
// ENFOQUE 1: USAR STUBS (implementación actual - MENOS RECOMENDADO)
// ============================================================================

/*
import org.junit.Test;
import static org.junit.Assert.*;

public class TestCtrlDominioUsuarioConStubs {
    
    @Test
    public void testRegistrarUsuario_ConStubs() {
        // PROBLEMA 1: Tienes que instanciar las dependencias REALES (stubs)
        CtrlUsuario ctrlUsuario = new CtrlUsuario();  // Stub real
        CtrlPersistence ctrlPersistence = new CtrlPersistence();  // Stub real
        
        CtrlDominioUsuario ctrl = new CtrlDominioUsuario(
            ctrlUsuario, 
            ctrlPersistence
        );
        
        // PROBLEMA 2: El test ejecuta CÓDIGO REAL de CtrlUsuario y CtrlPersistence
        // Si hay un bug en CtrlUsuario, este test fallará aunque 
        // CtrlDominioUsuario esté correcto
        
        String resultado = ctrl.registrarUsuario("testUser", "pass123", "pass123");
        
        // PROBLEMA 3: Solo puedes verificar el RESULTADO FINAL
        // No puedes verificar que:
        // - Se llamó a existeUsuario()
        // - Se llamó a crearUsuario() con los parámetros correctos
        // - Se llamó a guardarUsuario()
        
        assertEquals("Usuario registrado con éxito: testUser", resultado);
        
        // PROBLEMA 4: ¿Cómo verificas que se guardó en persistencia?
        // Tendrías que acceder al stub y comprobar su estado interno
        // (acoplamiento alto, test frágil)
    }
    
    @Test
    public void testRegistrarUsuario_UsuarioExistente_ConStubs() {
        CtrlUsuario ctrlUsuario = new CtrlUsuario();
        CtrlPersistence ctrlPersistence = new CtrlPersistence();
        
        // PROBLEMA 5: Para simular que el usuario existe, 
        // tienes que CREARLO PRIMERO de verdad
        ctrlUsuario.crearUsuario("existingUser", "oldPass");
        
        CtrlDominioUsuario ctrl = new CtrlDominioUsuario(
            ctrlUsuario, 
            ctrlPersistence
        );
        
        // Ahora sí, el test debería lanzar excepción
        try {
            ctrl.registrarUsuario("existingUser", "newPass", "newPass");
            fail("Debería haber lanzado IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Error: el nombre de usuario ya existe", e.getMessage());
        }
        
        // PROBLEMA 6: ¿Cómo verificas que NO se intentó crear otro usuario?
        // ¿Cómo verificas que NO se llamó a guardarUsuario()?
        // Con stubs, NO PUEDES - solo confías en que no falló
    }
}

DESVENTAJAS DE USAR STUBS:
❌ Test ejecuta código real de dependencias
❌ Bug en dependencia hace fallar este test (false negative)
❌ No puedes verificar CÓMO se usaron las dependencias
❌ Difícil simular casos edge (excepciones, null, etc.)
❌ Tests más lentos (ejecutan código real)
❌ Acoplamiento alto con implementación de dependencias
*/

// ============================================================================
// ENFOQUE 2: USAR MOCKS (con Mockito - RECOMENDADO)
// ============================================================================

/*
import org.junit.Test;
import org.junit.Before;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class TestCtrlDominioUsuarioConMocks {
    
    @Mock
    private CtrlUsuario mockCtrlUsuario;  // Mock automático de Mockito
    
    @Mock
    private ICtrlPersistence mockPersistence;  // Mock automático de Mockito
    
    private CtrlDominioUsuario ctrlDominioUsuario;
    
    @Before
    public void setUp() {
        // Inicializar los mocks
        MockitoAnnotations.openMocks(this);
        
        // Inyectar los mocks en el objeto bajo prueba
        ctrlDominioUsuario = new CtrlDominioUsuario(
            mockCtrlUsuario, 
            mockPersistence
        );
    }
    
    @Test
    public void testRegistrarUsuario_ConMocks() {
        // VENTAJA 1: CONTROLAS exactamente qué devuelve cada método
        when(mockCtrlUsuario.existeUsuario("testUser")).thenReturn(false);
        
        Usuario usuarioMock = new Usuario("testUser", "pass123");
        when(mockCtrlUsuario.crearUsuario("testUser", "pass123"))
            .thenReturn(usuarioMock);
        
        // VENTAJA 2: Ejecutar el método bajo prueba
        // Los mocks NO ejecutan código real, solo devuelven lo que configuraste
        String resultado = ctrlDominioUsuario.registrarUsuario(
            "testUser", "pass123", "pass123"
        );
        
        // VENTAJA 3: Verificar el resultado
        assertEquals("Usuario registrado con éxito: testUser", resultado);
        
        // VENTAJA 4: VERIFICAR que se llamaron los métodos CORRECTOS
        // en el ORDEN CORRECTO con los PARÁMETROS CORRECTOS
        verify(mockCtrlUsuario, times(1)).existeUsuario("testUser");
        verify(mockCtrlUsuario, times(1)).crearUsuario("testUser", "pass123");
        verify(mockPersistence, times(1)).guardarUsuario(usuarioMock);
        
        // VENTAJA 5: Verificar que NO se llamaron otros métodos
        verifyNoMoreInteractions(mockCtrlUsuario);
        verifyNoMoreInteractions(mockPersistence);
    }
    
    @Test
    public void testRegistrarUsuario_UsuarioExistente_ConMocks() {
        // VENTAJA 6: Simular caso edge es TRIVIAL - solo configurar el mock
        when(mockCtrlUsuario.existeUsuario("existingUser")).thenReturn(true);
        
        // No necesitas crear usuarios de verdad
        
        try {
            ctrlDominioUsuario.registrarUsuario(
                "existingUser", "pass", "pass"
            );
            fail("Debería haber lanzado IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Error: el nombre de usuario ya existe", e.getMessage());
        }
        
        // VENTAJA 7: Verificar que NO se intentó crear ni guardar
        verify(mockCtrlUsuario, times(1)).existeUsuario("existingUser");
        verify(mockCtrlUsuario, never()).crearUsuario(anyString(), anyString());
        verify(mockPersistence, never()).guardarUsuario(any(Usuario.class));
    }
    
    @Test
    public void testRegistrarUsuario_ContrasenasNoCoinciden_ConMocks() {
        when(mockCtrlUsuario.existeUsuario("user")).thenReturn(false);
        
        try {
            ctrlDominioUsuario.registrarUsuario("user", "pass1", "pass2");
            fail("Debería haber lanzado IllegalArgumentException");
        } catch (IllegalArgumentException e) {
            assertEquals("Error: las contraseñas no coinciden", e.getMessage());
        }
        
        // VENTAJA 8: Verificar que verificó existencia pero NO creó usuario
        verify(mockCtrlUsuario).existeUsuario("user");
        verify(mockCtrlUsuario, never()).crearUsuario(anyString(), anyString());
        verify(mockPersistence, never()).guardarUsuario(any());
    }
    
    @Test
    public void testRegistrarUsuario_ExcepcionEnCrearUsuario_ConMocks() {
        // VENTAJA 9: Simular excepciones es FÁCIL
        when(mockCtrlUsuario.existeUsuario("user")).thenReturn(false);
        when(mockCtrlUsuario.crearUsuario("user", "pass"))
            .thenThrow(new RuntimeException("Error en base de datos"));
        
        try {
            ctrlDominioUsuario.registrarUsuario("user", "pass", "pass");
            fail("Debería propagar la excepción");
        } catch (RuntimeException e) {
            assertEquals("Error en base de datos", e.getMessage());
        }
        
        // Verificar que NO se intentó guardar si falló la creación
        verify(mockPersistence, never()).guardarUsuario(any());
    }
}

VENTAJAS DE USAR MOCKS:
✅ Test AISLADO - solo falla si hay error en la clase bajo prueba
✅ CONTROLAS exactamente qué devuelve cada dependencia
✅ VERIFICAS que se llaman los métodos correctos
✅ VERIFICAS el orden de llamadas
✅ VERIFICAS que NO se llaman métodos incorrectos
✅ Simular excepciones/casos edge es trivial
✅ Tests RÁPIDOS (no ejecutan código real)
✅ Documentan CÓMO debe usarse la dependencia
*/

// ============================================================================
// COMPARACIÓN VISUAL
// ============================================================================

/*
┌─────────────────────────────────────────────────────────────────────────┐
│                            CON STUBS                                    │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  CtrlDominioUsuario ──calls──> CtrlUsuario (STUB REAL)                 │
│         │                            │                                  │
│         │                            │ Ejecuta código real              │
│         │                            │ Accede a HashMap real            │
│         │                            │                                  │
│         └──calls──> CtrlPersistence (STUB REAL)                        │
│                            │                                            │
│                            │ Ejecuta código real                        │
│                            │ Accede a HashMap real                      │
│                                                                         │
│  RESULTADO: Test ejecuta MUCHO código que no estamos testeando        │
│  PROBLEMA: Bug en stub hace fallar este test                          │
└─────────────────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                           CON MOCKS                                     │
├─────────────────────────────────────────────────────────────────────────┤
│                                                                         │
│  CtrlDominioUsuario ──calls──> MockCtrlUsuario (MOCKITO)              │
│         │                            │                                  │
│         │                            │ NO ejecuta código real           │
│         │                            │ Devuelve lo que configuraste     │
│         │                            │ REGISTRA la llamada              │
│         │                            │                                  │
│         └──calls──> MockPersistence (MOCKITO)                          │
│                            │                                            │
│                            │ NO ejecuta código real                     │
│                            │ Devuelve lo que configuraste               │
│                            │ REGISTRA la llamada                        │
│                                                                         │
│  Luego verificas:                                                      │
│    verify(mockCtrlUsuario).existeUsuario("user");                      │
│    verify(mockCtrlUsuario).crearUsuario("user", "pass");               │
│    verify(mockPersistence).guardarUsuario(any());                      │
│                                                                         │
│  RESULTADO: Test solo ejecuta código de CtrlDominioUsuario            │
│  VENTAJA: Bug en dependencias NO afecta este test                     │
└─────────────────────────────────────────────────────────────────────────┘
*/

// ============================================================================
// CUÁNDO USAR CADA UNO
// ============================================================================

/*
USA STUBS cuando:
- Clase NO tiene dependencias externas (ej: Usuario.java, Encuesta.java)
- Dependencia es muy simple y estable
- Quieres test de integración (probar varias capas juntas)

USA MOCKS cuando:
- Clase ORQUESTA múltiples dependencias (ej: CtrlDominioUsuario)
- Quieres verificar CÓMO se usa la dependencia
- Quieres tests AISLADOS (solo fallan si hay error en la clase)
- Necesitas simular excepciones o casos edge
- Dependencia es compleja o inestable
- Quieres tests RÁPIDOS

REGLA GENERAL:
- Clases de dominio puras (Usuario, Encuesta): SIN mocks
- Controladores de clase (CtrlUsuario): SIN mocks (usan objetos reales)
- Controladores de dominio (CtrlDominioUsuario): CON mocks ✅
- Capa de presentación (CtrlPresentacion): CON mocks ✅
*/

// ============================================================================
// CÓDIGO MOCKITO MÁS USADO
// ============================================================================

/*
// 1. CREAR MOCKS
@Mock
private CtrlUsuario mockCtrlUsuario;

// 2. INICIALIZAR MOCKS
@Before
public void setUp() {
    MockitoAnnotations.openMocks(this);
}

// 3. CONFIGURAR QUÉ DEVUELVE UN MOCK
when(mockCtrlUsuario.existeUsuario("user")).thenReturn(false);
when(mockCtrlUsuario.getUsuario("user")).thenReturn(new Usuario("user", "pass"));

// 4. CONFIGURAR EXCEPCIÓN
when(mockCtrlUsuario.crearUsuario("user", "pass"))
    .thenThrow(new RuntimeException("Error"));

// 5. VERIFICAR QUE SE LLAMÓ
verify(mockCtrlUsuario).existeUsuario("user");

// 6. VERIFICAR QUE SE LLAMÓ N VECES
verify(mockCtrlUsuario, times(2)).existeUsuario("user");

// 7. VERIFICAR QUE NUNCA SE LLAMÓ
verify(mockCtrlUsuario, never()).crearUsuario(anyString(), anyString());

// 8. VERIFICAR CON CUALQUIER PARÁMETRO
verify(mockPersistence).guardarUsuario(any(Usuario.class));

// 9. VERIFICAR ORDEN DE LLAMADAS
InOrder inOrder = inOrder(mockCtrlUsuario, mockPersistence);
inOrder.verify(mockCtrlUsuario).crearUsuario("user", "pass");
inOrder.verify(mockPersistence).guardarUsuario(any());

// 10. CAPTURAR ARGUMENTO PARA VERIFICARLO
ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
verify(mockPersistence).guardarUsuario(captor.capture());
assertEquals("testUser", captor.getValue().getNomUsuario());
*/
