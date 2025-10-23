# 📋 PLANTILLAS DE TESTING - COPY & PASTE

## 🎯 PLANTILLA 1: Test de Clase de Dominio (SIN mocks)

```java
package domain;

import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests unitarios para la clase NombreClase.
 * 
 * Esta clase NO tiene dependencias externas, por lo que NO necesita mocks.
 * 
 * @author Tu Nombre
 * @version 1.0
 */
public class TestNombreClase {
    
    private NombreClase objetoBajoPrueba;
    
    @Before
    public void setUp() {
        // Inicializar objeto antes de cada test
        objetoBajoPrueba = new NombreClase(/* parámetros */);
    }
    
    // ==================== TESTS DEL CONSTRUCTOR ====================
    
    @Test
    public void testConstructor_ParametrosValidos_CreaObjetoCorrectamente() {
        // ARRANGE (preparar datos)
        String param1 = "valor1";
        int param2 = 42;
        
        // ACT (ejecutar)
        NombreClase objeto = new NombreClase(param1, param2);
        
        // ASSERT (verificar)
        assertNotNull("El objeto no debe ser null", objeto);
        assertEquals(param1, objeto.getParam1());
        assertEquals(param2, objeto.getParam2());
    }
    
    @Test(expected = IllegalArgumentException.class)
    public void testConstructor_ParametroInvalido_LanzaExcepcion() {
        new NombreClase("", -1); // Debería lanzar excepción
    }
    
    // ==================== TESTS DE MÉTODOS GETTER ====================
    
    @Test
    public void testGetAlgo_RetornaValorCorrecto() {
        assertEquals("valorEsperado", objetoBajoPrueba.getAlgo());
    }
    
    // ==================== TESTS DE MÉTODOS SETTER ====================
    
    @Test
    public void testSetAlgo_ModificaValorCorrectamente() {
        String nuevoValor = "nuevo";
        objetoBajoPrueba.setAlgo(nuevoValor);
        assertEquals(nuevoValor, objetoBajoPrueba.getAlgo());
    }
    
    // ==================== TESTS DE LÓGICA DE NEGOCIO ====================
    
    @Test
    public void testMetodoDeNegocio_CasoPositivo_RetornaResultadoEsperado() {
        // ARRANGE
        objetoBajoPrueba.setEstado("activo");
        
        // ACT
        boolean resultado = objetoBajoPrueba.procesarAlgo();
        
        // ASSERT
        assertTrue("Debería procesarse correctamente", resultado);
    }
    
    @Test
    public void testMetodoDeNegocio_CasoNegativo_RetornaFalse() {
        // ARRANGE
        objetoBajoPrueba.setEstado("inactivo");
        
        // ACT
        boolean resultado = objetoBajoPrueba.procesarAlgo();
        
        // ASSERT
        assertFalse("No debería procesarse", resultado);
    }
    
    // ==================== TESTS DE COLECCIONES ====================
    
    @Test
    public void testAgregarItem_AgregaCorrectamente() {
        objetoBajoPrueba.agregarItem("item1");
        objetoBajoPrueba.agregarItem("item2");
        
        assertEquals(2, objetoBajoPrueba.getItems().size());
        assertTrue(objetoBajoPrueba.getItems().contains("item1"));
    }
    
    @Test
    public void testEliminarItem_EliminaCorrectamente() {
        objetoBajoPrueba.agregarItem("item1");
        objetoBajoPrueba.eliminarItem("item1");
        
        assertTrue(objetoBajoPrueba.getItems().isEmpty());
    }
}
```

---

## 🎯 PLANTILLA 2: Test de Controlador (CON mocks)

```java
package domaincontroller;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import classcontroller.CtrlDependencia1;
import classcontroller.CtrlDependencia2;
import persistence.ICtrlPersistence;
import domain.*;

/**
 * Tests unitarios para CtrlNombreControlador usando MOCKS.
 * 
 * Este controlador orquesta múltiples dependencias, por lo que usa mocks
 * para aislar la lógica bajo prueba.
 * 
 * @author Tu Nombre
 * @version 1.0
 */
public class TestCtrlNombreControlador {
    
    // ==================== MOCKS ====================
    
    @Mock
    private CtrlDependencia1 mockDependencia1;
    
    @Mock
    private CtrlDependencia2 mockDependencia2;
    
    @Mock
    private ICtrlPersistence mockPersistence;
    
    // ==================== OBJETO BAJO PRUEBA ====================
    
    private CtrlNombreControlador controlador;
    
    // ==================== SETUP ====================
    
    @Before
    public void setUp() {
        // Inicializar mocks
        MockitoAnnotations.openMocks(this);
        
        // Inyectar mocks en el controlador
        controlador = new CtrlNombreControlador(
            mockDependencia1,
            mockDependencia2,
            mockPersistence
        );
    }
    
    // ==================== TESTS CASO POSITIVO ====================
    
    @Test
    public void testMetodo_CasoExitoso_RetornaResultadoYGuarda() {
        // 1. ARRANGE - Configurar mocks
        String parametro = "valor";
        
        when(mockDependencia1.validar(parametro)).thenReturn(true);
        when(mockDependencia2.procesar(parametro)).thenReturn("resultado");
        
        // 2. ACT - Ejecutar método bajo prueba
        String resultado = controlador.metodo(parametro);
        
        // 3. ASSERT - Verificar resultado
        assertEquals("resultado", resultado);
        
        // 4. VERIFY - Verificar interacciones con mocks
        verify(mockDependencia1, times(1)).validar(parametro);
        verify(mockDependencia2, times(1)).procesar(parametro);
        verify(mockPersistence, times(1)).guardar(any());
    }
    
    // ==================== TESTS CASO NEGATIVO ====================
    
    @Test(expected = IllegalArgumentException.class)
    public void testMetodo_ParametroInvalido_LanzaExcepcion() {
        // ARRANGE
        String parametroInvalido = "";
        
        when(mockDependencia1.validar(parametroInvalido)).thenReturn(false);
        
        // ACT - Debería lanzar excepción
        controlador.metodo(parametroInvalido);
        
        // VERIFY - No debería haber intentado procesar ni guardar
        verify(mockDependencia2, never()).procesar(anyString());
        verify(mockPersistence, never()).guardar(any());
    }
    
    // ==================== TESTS DE EXCEPCIONES ====================
    
    @Test
    public void testMetodo_ExcepcionEnDependencia_PropagaExcepcion() {
        // ARRANGE - Configurar mock para lanzar excepción
        when(mockDependencia1.validar("error"))
            .thenThrow(new RuntimeException("Error de prueba"));
        
        // ACT & ASSERT
        try {
            controlador.metodo("error");
            fail("Debería haber lanzado RuntimeException");
        } catch (RuntimeException e) {
            assertEquals("Error de prueba", e.getMessage());
        }
        
        // VERIFY - No debería haber intentado guardar
        verify(mockPersistence, never()).guardar(any());
    }
    
    // ==================== TESTS DE ORDEN DE LLAMADAS ====================
    
    @Test
    public void testMetodo_LlamaEnOrdenCorrecto() {
        // ARRANGE
        String param = "valor";
        when(mockDependencia1.validar(param)).thenReturn(true);
        when(mockDependencia2.procesar(param)).thenReturn("resultado");
        
        // ACT
        controlador.metodo(param);
        
        // VERIFY - Verificar orden
        InOrder inOrder = inOrder(mockDependencia1, mockDependencia2, mockPersistence);
        inOrder.verify(mockDependencia1).validar(param);
        inOrder.verify(mockDependencia2).procesar(param);
        inOrder.verify(mockPersistence).guardar(any());
    }
    
    // ==================== TESTS CON CAPTURA DE ARGUMENTOS ====================
    
    @Test
    public void testMetodo_GuardaObjetoConDatosCorrectos() {
        // ARRANGE
        when(mockDependencia1.validar("test")).thenReturn(true);
        when(mockDependencia2.procesar("test")).thenReturn("resultado");
        
        // ACT
        controlador.metodo("test");
        
        // VERIFY - Capturar argumento pasado a guardar
        ArgumentCaptor<ObjetoDominio> captor = 
            ArgumentCaptor.forClass(ObjetoDominio.class);
        verify(mockPersistence).guardar(captor.capture());
        
        // Verificar datos del objeto capturado
        ObjetoDominio objetoGuardado = captor.getValue();
        assertEquals("test", objetoGuardado.getNombre());
        assertEquals("resultado", objetoGuardado.getValor());
    }
    
    // ==================== TESTS DE MÚLTIPLES ITERACIONES ====================
    
    @Test
    public void testMetodoConLista_ProcesaCadaElemento() {
        // ARRANGE
        List<String> elementos = Arrays.asList("a", "b", "c");
        
        for (String elem : elementos) {
            when(mockDependencia1.validar(elem)).thenReturn(true);
        }
        
        // ACT
        controlador.procesarLista(elementos);
        
        // VERIFY - Cada elemento se validó
        verify(mockDependencia1, times(3)).validar(anyString());
        verify(mockPersistence, times(3)).guardar(any());
    }
}
```

---

## 🎯 PLANTILLA 3: Driver Interactivo

```java
package driver;

import java.util.Scanner;
import classcontroller.*;
import domaincontroller.*;
import persistence.*;

/**
 * Driver interactivo para probar CtrlNombreControlador.
 * 
 * Programa MANUAL con menú para demostrar casos de uso.
 * Usado para el 1er lliurament y "juegos de prueba".
 * 
 * @author Tu Nombre
 * @version 1.0
 */
public class DriverCtrlNombreControlador {
    
    private static CtrlNombreControlador controlador;
    private static Scanner scanner;
    
    public static void main(String[] args) {
        // Inicializar dependencias REALES (no mocks)
        CtrlDependencia1 dep1 = new CtrlDependencia1();
        CtrlDependencia2 dep2 = new CtrlDependencia2();
        CtrlPersistence persistence = new CtrlPersistence();
        
        controlador = new CtrlNombreControlador(dep1, dep2, persistence);
        scanner = new Scanner(System.in);
        
        boolean salir = false;
        
        System.out.println("╔═══════════════════════════════════════════════╗");
        System.out.println("║   DRIVER - NOMBRE DEL CONTROLADOR            ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        
        while (!salir) {
            mostrarMenu();
            int opcion = leerEntero();
            
            switch (opcion) {
                case 1:
                    testMetodo1();
                    break;
                case 2:
                    testMetodo2();
                    break;
                case 3:
                    testMetodo3();
                    break;
                case 0:
                    salir = true;
                    System.out.println("\n👋 Saliendo...\n");
                    break;
                default:
                    System.out.println("\n❌ Opción no válida.\n");
            }
        }
        
        scanner.close();
    }
    
    private static void mostrarMenu() {
        System.out.println("\n┌───────────────────────────────────────┐");
        System.out.println("│         MENÚ PRINCIPAL                │");
        System.out.println("├───────────────────────────────────────┤");
        System.out.println("│  1. Probar Método 1                   │");
        System.out.println("│  2. Probar Método 2                   │");
        System.out.println("│  3. Probar Método 3                   │");
        System.out.println("│  0. Salir                             │");
        System.out.println("└───────────────────────────────────────┘");
        System.out.print("\nSelecciona opción: ");
    }
    
    private static void testMetodo1() {
        System.out.println("\n╔═══════════════════════════════════════════════╗");
        System.out.println("║          TEST: NOMBRE DEL MÉTODO              ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        
        System.out.print("Parámetro 1: ");
        String param1 = scanner.nextLine();
        
        System.out.print("Parámetro 2: ");
        String param2 = scanner.nextLine();
        
        try {
            String resultado = controlador.metodo1(param1, param2);
            System.out.println("\n✅ Éxito: " + resultado);
        } catch (IllegalArgumentException e) {
            System.out.println("\n❌ Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n💥 Error inesperado: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void testMetodo2() {
        System.out.println("\n╔═══════════════════════════════════════════════╗");
        System.out.println("║          TEST: OTRO MÉTODO                    ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        
        // Implementar similar a testMetodo1
    }
    
    private static void testMetodo3() {
        System.out.println("\n╔═══════════════════════════════════════════════╗");
        System.out.println("║          TEST: TERCER MÉTODO                  ║");
        System.out.println("╚═══════════════════════════════════════════════╝");
        
        // Implementar similar a testMetodo1
    }
    
    private static int leerEntero() {
        while (true) {
            try {
                String input = scanner.nextLine();
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.print("❌ Entrada inválida. Introduce un número: ");
            }
        }
    }
    
    private static String leerString(String prompt) {
        System.out.print(prompt);
        return scanner.nextLine();
    }
    
    private static double leerDouble(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                return Double.parseDouble(input);
            } catch (NumberFormatException e) {
                System.out.println("❌ Entrada inválida. Introduce un número decimal.");
            }
        }
    }
}
```

---

## 🎯 PLANTILLA 4: Juego de Prueba (Documentación)

```markdown
# JUEGO DE PRUEBA: [Nombre del Caso de Uso]

## Información General
- **Caso de Uso**: CU-01 Registrar Usuario
- **Actor**: Usuario no registrado
- **Precondiciones**: Sistema iniciado
- **Driver**: DriverCtrlDominioUsuario.java

---

## PRUEBA 1: Registro exitoso con datos válidos

### Objetivo
Verificar que un usuario puede registrarse con credenciales válidas.

### Pasos a Seguir
1. Ejecutar driver: `java driver.DriverCtrlDominioUsuario`
2. Seleccionar opción: `1. Registrar usuario`
3. Introducir nombre: `testUser`
4. Introducir contraseña: `password123`
5. Confirmar contraseña: `password123`

### Resultado Esperado
```
✅ Usuario registrado con éxito: testUser
```

### Resultado Obtenido
```
[El profesor lo completa al ejecutar]
```

### Estado
- [ ] Pasa ✅
- [ ] Falla ❌
- [ ] No ejecutado

---

## PRUEBA 2: Registro fallido - Usuario ya existente

### Objetivo
Verificar que el sistema rechaza registrar un usuario ya existente.

### Pasos a Seguir
1. Ejecutar driver
2. Registrar usuario `existingUser` (como en PRUEBA 1)
3. Intentar registrar de nuevo el mismo usuario
4. Seleccionar opción: `1. Registrar usuario`
5. Introducir nombre: `existingUser`
6. Introducir contraseña: `newPassword`
7. Confirmar contraseña: `newPassword`

### Resultado Esperado
```
❌ Error: el nombre de usuario ya existe
```

### Resultado Obtenido
```
[El profesor lo completa al ejecutar]
```

### Estado
- [ ] Pasa ✅
- [ ] Falla ❌
- [ ] No ejecutado

---

## PRUEBA 3: Registro fallido - Contraseñas no coinciden

### Objetivo
Verificar validación de confirmación de contraseña.

### Pasos a Seguir
1. Ejecutar driver
2. Seleccionar opción: `1. Registrar usuario`
3. Introducir nombre: `newUser`
4. Introducir contraseña: `password123`
5. Confirmar contraseña: `differentPassword`

### Resultado Esperado
```
❌ Error: las contraseñas no coinciden
```

### Resultado Obtenido
```
[El profesor lo completa al ejecutar]
```

### Estado
- [ ] Pasa ✅
- [ ] Falla ❌
- [ ] No ejecutado

---

## Observaciones
[Cualquier nota adicional sobre las pruebas]
```

---

## 🎯 PLANTILLA 5: Configuración de Mockito

```java
// ==================== CONFIGURACIÓN BÁSICA ====================

// En build.gradle (YA HECHO)
dependencies {
    testImplementation('org.mockito:mockito-core:5.5.0')
}

// ==================== IMPORTS NECESARIOS ====================

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.ArgumentCaptor;
import org.mockito.InOrder;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

// ==================== CREAR MOCKS ====================

// Opción 1: Con anotación @Mock
@Mock
private CtrlUsuario mockCtrlUsuario;

@Before
public void setUp() {
    MockitoAnnotations.openMocks(this);
}

// Opción 2: Manualmente
CtrlUsuario mockCtrlUsuario = mock(CtrlUsuario.class);

// ==================== CONFIGURAR COMPORTAMIENTO ====================

// Devolver valor
when(mock.metodo()).thenReturn(valor);
when(mock.metodo("param")).thenReturn(valor);

// Devolver múltiples valores (sucesivos)
when(mock.metodo()).thenReturn(valor1, valor2, valor3);

// Lanzar excepción
when(mock.metodo()).thenThrow(new RuntimeException("error"));

// Hacer algo (custom)
when(mock.metodo()).thenAnswer(invocation -> {
    String arg = invocation.getArgument(0);
    return "procesado: " + arg;
});

// ==================== VERIFICAR LLAMADAS ====================

// Verificar que se llamó
verify(mock).metodo();
verify(mock).metodo("parametro");

// Verificar número de veces
verify(mock, times(3)).metodo();
verify(mock, atLeast(2)).metodo();
verify(mock, atMost(5)).metodo();

// Verificar que NUNCA se llamó
verify(mock, never()).metodo();

// Verificar con cualquier parámetro
verify(mock).metodo(anyString());
verify(mock).metodo(any(Usuario.class));
verify(mock).metodo(eq("exacto"), anyInt());

// ==================== VERIFICAR ORDEN ====================

InOrder inOrder = inOrder(mock1, mock2);
inOrder.verify(mock1).metodo1();
inOrder.verify(mock2).metodo2();

// ==================== CAPTURAR ARGUMENTOS ====================

ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
verify(mock).guardarUsuario(captor.capture());

Usuario usuarioCapturado = captor.getValue();
assertEquals("esperado", usuarioCapturado.getNombre());

// ==================== MATCHERS ÚTILES ====================

any()                    // Cualquier objeto
any(Clase.class)        // Cualquier objeto de ese tipo
anyString()             // Cualquier String
anyInt()                // Cualquier int
anyList()               // Cualquier lista
eq(valor)               // Valor exacto
isNull()                // null
isNotNull()             // no null
contains("texto")       // String que contiene
startsWith("pre")       // String que empieza con
matches("regex")        // String que coincide con regex
```

---

## ✅ CHECKLIST AL CREAR UN TEST

```
Antes de escribir el test:
□ ¿La clase tiene dependencias externas? → Necesita mocks
□ ¿Qué escenarios voy a probar? (positivos, negativos, edge cases)
□ ¿Qué resultado espero en cada caso?

Al escribir el test:
□ Nombre descriptivo: testMetodo_Condicion_ResultadoEsperado
□ Sección ARRANGE: Preparar datos y configurar mocks
□ Sección ACT: Ejecutar método bajo prueba
□ Sección ASSERT: Verificar resultado
□ Sección VERIFY: Verificar interacciones (si usa mocks)

Después de escribir el test:
□ Ejecutar el test: ¿Pasa? ✅
□ Romper el código a propósito: ¿El test detecta el error? ✅
□ Código legible y comentado
□ Cobertura de casos positivos Y negativos
```

---

## 📚 REFERENCIAS RÁPIDAS

| Concepto | Cuándo Usar | Ejemplo |
|----------|-------------|---------|
| **@Test** | Marcar método como test | `@Test public void test()` |
| **@Before** | Ejecutar antes de cada test | Inicializar objetos |
| **@After** | Ejecutar después de cada test | Limpiar recursos |
| **@Mock** | Crear mock automáticamente | `@Mock CtrlUsuario mock;` |
| **assertEquals** | Verificar igualdad | `assertEquals(5, resultado);` |
| **assertTrue** | Verificar condición verdadera | `assertTrue(activo);` |
| **assertFalse** | Verificar condición falsa | `assertFalse(inactivo);` |
| **assertNull** | Verificar que es null | `assertNull(objeto);` |
| **assertNotNull** | Verificar que no es null | `assertNotNull(objeto);` |
| **when** | Configurar mock | `when(mock.get()).thenReturn(5);` |
| **verify** | Verificar llamada | `verify(mock).metodo();` |
| **times** | Verificar N llamadas | `verify(mock, times(3)).metodo();` |
| **never** | Verificar NUNCA se llamó | `verify(mock, never()).metodo();` |
