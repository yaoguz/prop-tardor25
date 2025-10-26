# 🧪 GUÍA COMPLETA: TESTING CON JUNIT 4 Y MOCKITO

## 📚 CONFIGURACIÓN DEL PROYECTO

### **Dependencias en build.gradle**

```gradle
dependencies {
    implementation 'com.google.code.gson:gson:2.8.9'
    
    // Testing dependencies - JUnit 4 y Mockito
    testImplementation 'junit:junit:4.12'
    testImplementation 'org.hamcrest:hamcrest-core:1.3'
    testImplementation 'org.mockito:mockito-core:4.9.0'
    testImplementation 'net.bytebuddy:byte-buddy:1.12.16'
    testImplementation 'net.bytebuddy:byte-buddy-agent:1.12.16'
    testImplementation 'org.objenesis:objenesis:3.3'
}
```

### **Archivos JAR necesarios (si no usas Gradle):**

```
junit-4.12.jar
hamcrest-core-1.3.jar
mockito-core-4.9.0.jar
byte-buddy-1.12.16.jar
byte-buddy-agent-1.12.16.jar
objenesis-3.3.jar
```

---

## 📋 IMPORTS ESTÁNDAR PARA JUNIT 4

### **Para tests SIN mocks:**

```java
package domain;

import main.domini.*;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;
```

### **Para tests CON mocks:**

```java
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
```

### **Para tests PARAMETRIZADOS:**

```java
package domain;

import main.domini.*;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
```

---

## 🎯 DIFERENCIAS CLAVE: JUNIT 4 vs JUNIT 5

| Aspecto | JUnit 4 | JUnit 5 |
|---------|---------|---------|
| **Anotación test** | `@Test` | `@Test` |
| **Setup** | `@Before` | `@BeforeEach` |
| **Teardown** | `@After` | `@AfterEach` |
| **Assertions** | `Assert.assertEquals()` | `Assertions.assertEquals()` |
| **Mocks init** | `MockitoAnnotations.initMocks(this)` | `MockitoAnnotations.openMocks(this)` |
| **Runner** | `@RunWith(MockitoJUnitRunner.class)` | `@ExtendWith(MockitoExtension.class)` |
| **Parametrizados** | `@RunWith(Parameterized.class)` | `@ParameterizedTest` |

---

## 🔧 PLANTILLA 1: TEST SIMPLE SIN MOCKS (JUNIT 4)

```java
package domain;

import main.domini.*;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.Test;

/**
 * Tests unitarios para la clase NombreClase - JUnit 4.
 */
public class TestNombreClase {
    
    private NombreClase objetoBajoPrueba;
    
    /**
     * Setup - Se ejecuta ANTES de cada test.
     */
    @Before
    public void setUp() {
        objetoBajoPrueba = new NombreClase();
    }
    
    /**
     * Test simple.
     */
    @Test
    public void testMetodo_Condicion_ResultadoEsperado() {
        // ARRANGE - Preparar datos
        String input = "valor";
        
        // ACT - Ejecutar
        String resultado = objetoBajoPrueba.metodo(input);
        
        // ASSERT - Verificar
        assertEquals("valorEsperado", resultado);
    }
    
    /**
     * Test que espera excepción.
     */
    @Test(expected = IllegalArgumentException.class)
    public void testMetodo_ParametroInvalido_LanzaExcepcion() {
        objetoBajoPrueba.metodo(null);
    }
}
```

---

## 🔧 PLANTILLA 2: TEST CON MOCKS (JUNIT 4)

```java
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

/**
 * Tests unitarios con MOCKS - JUnit 4.
 */
@RunWith(MockitoJUnitRunner.class)
public class TestControlador {
    
    @Mock
    private Dependencia1 mockDependencia1;
    
    @Mock
    private Dependencia2 mockDependencia2;
    
    private Controlador controlador;
    
    /**
     * Setup - Inicializa mocks.
     */
    @Before
    public void setUp() {
        // Inicializar mocks (opcional con @RunWith)
        MockitoAnnotations.initMocks(this);
        
        // Crear objeto bajo prueba con mocks
        controlador = new Controlador(mockDependencia1, mockDependencia2);
    }
    
    /**
     * Test con verificación de mocks.
     */
    @Test
    public void testMetodo_ConMocks_VerificaLlamadas() {
        // ARRANGE - Configurar comportamiento de mocks
        when(mockDependencia1.metodo("param")).thenReturn("valor");
        
        // ACT - Ejecutar
        String resultado = controlador.procesarAlgo("param");
        
        // ASSERT - Verificar resultado
        assertEquals("valorEsperado", resultado);
        
        // VERIFY - Verificar llamadas a mocks
        verify(mockDependencia1, times(1)).metodo("param");
        verify(mockDependencia2, never()).otroMetodo();
    }
}
```

---

## 🔧 PLANTILLA 3: TESTS PARAMETRIZADOS (JUNIT 4)

```java
package domain;

import main.domini.*;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests parametrizados - JUnit 4.
 */
@RunWith(Parameterized.class)
public class TestParametrizado {
    
    // Parámetros del test
    private String input;
    private String expectedOutput;
    
    /**
     * Constructor con parámetros.
     */
    public TestParametrizado(String input, String expectedOutput) {
        this.input = input;
        this.expectedOutput = expectedOutput;
    }
    
    /**
     * Define los parámetros para cada ejecución.
     */
    @Parameters(name = "Test {index}: input={0}, expected={1}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {"input1", "output1"},
            {"input2", "output2"},
            {"input3", "output3"}
        });
    }
    
    /**
     * Test que se ejecuta con cada conjunto de parámetros.
     */
    @Test
    public void testConParametros() {
        String resultado = procesarInput(input);
        assertEquals(expectedOutput, resultado);
    }
}
```

---

## 📊 ASSERTIONS COMUNES EN JUNIT 4

```java
// Igualdad
assertEquals(expected, actual);
assertEquals("mensaje", expected, actual);

// Valores booleanos
assertTrue(condicion);
assertTrue("mensaje", condicion);
assertFalse(condicion);

// Null
assertNull(objeto);
assertNotNull(objeto);

// Referencia de objeto
assertSame(expected, actual);       // Mismo objeto
assertNotSame(expected, actual);    // Objetos diferentes

// Arrays
assertArrayEquals(expectedArray, actualArray);

// Fallo manual
fail("Este test debería fallar");

// Con delta (para doubles)
assertEquals(3.14, resultado, 0.01);
```

---

## 🎭 MOCKITO CON JUNIT 4

### **Inicialización de Mocks (3 formas):**

```java
// Opción 1: Con @RunWith (recomendado)
@RunWith(MockitoJUnitRunner.class)
public class TestClase {
    @Mock private Dependencia mock;
}

// Opción 2: Con @Before
public class TestClase {
    @Mock private Dependencia mock;
    
    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }
}

// Opción 3: Manual
public class TestClase {
    private Dependencia mock;
    
    @Before
    public void setUp() {
        mock = mock(Dependencia.class);
    }
}
```

### **Configurar comportamiento (when-thenReturn):**

```java
// Devolver valor
when(mock.metodo()).thenReturn(valor);
when(mock.metodo("param")).thenReturn(valor);

// Devolver múltiples valores
when(mock.metodo()).thenReturn(valor1, valor2, valor3);

// Lanzar excepción
when(mock.metodo()).thenThrow(new RuntimeException("error"));

// Para métodos void
doNothing().when(mock).metodoVoid();
doThrow(new RuntimeException()).when(mock).metodoVoid();
```

### **Verificar llamadas (verify):**

```java
// Verificar que se llamó
verify(mock).metodo();
verify(mock).metodo("param");

// Verificar número de veces
verify(mock, times(3)).metodo();
verify(mock, atLeast(2)).metodo();
verify(mock, atMost(5)).metodo();
verify(mock, never()).metodo();

// Verificar con cualquier parámetro
verify(mock).metodo(anyString());
verify(mock).metodo(any(Usuario.class));
verify(mock).metodo(eq("exacto"), anyInt());
```

### **Matchers comunes:**

```java
any()                    // Cualquier objeto
any(Clase.class)        // Cualquier objeto de ese tipo
anyString()             // Cualquier String
anyInt()                // Cualquier int
anyList()               // Cualquier lista
eq(valor)               // Valor exacto
isNull()                // null
isNotNull()             // no null
```

---

## 📁 ESTRUCTURA DE ARCHIVOS CREADOS

```
src/test/
├── domain/
│   ├── TestUsuario.java                    ✅ Test simple sin mocks
│   ├── TestUsuarioParametrizado.java       ✅ Test parametrizado
│   └── TestEncuesta.java                   ✅ Test completo sin mocks
│
└── domaincontroller/
    ├── TestCtrlDominioUsuario.java         ✅ Test con mocks
    └── TestCtrlDominioEncuesta.java        ✅ Test avanzado con mocks
```

---

## 🚀 COMANDOS PARA EJECUTAR TESTS

```bash
# Compilar proyecto
./gradlew build

# Ejecutar todos los tests
./gradlew test

# Ejecutar tests de un paquete específico
./gradlew test --tests domain.*

# Ejecutar un test específico
./gradlew test --tests domain.TestUsuario

# Ver reporte HTML
xdg-open build/reports/tests/test/index.html

# Limpiar y ejecutar tests
./gradlew clean test
```

---

## ✅ CHECKLIST DE MIGRACIÓN A JUNIT 4

- [x] Actualizar `build.gradle` con dependencias JUnit 4
- [x] Cambiar imports:
  - [x] `@BeforeEach` → `@Before`
  - [x] `@AfterEach` → `@After`
  - [x] `MockitoAnnotations.openMocks()` → `MockitoAnnotations.initMocks()`
  - [x] `@ExtendWith` → `@RunWith`
- [x] Usar `import static org.junit.Assert.*;`
- [x] Usar `import main.domini.*;` en lugar de imports específicos
- [x] Agregar `@RunWith(MockitoJUnitRunner.class)` en tests con mocks
- [x] Tests parametrizados con `@RunWith(Parameterized.class)`

---

## 🎯 DIFERENCIAS IMPORTANTES JUNIT 4

### **@Before vs @BeforeEach:**
```java
// JUnit 4
@Before
public void setUp() { }

// JUnit 5
@BeforeEach
void setUp() { }
```

### **@Test con expected:**
```java
// JUnit 4
@Test(expected = IllegalArgumentException.class)
public void test() { }

// JUnit 5
@Test
void test() {
    assertThrows(IllegalArgumentException.class, () -> { });
}
```

### **Inicialización de Mocks:**
```java
// JUnit 4
MockitoAnnotations.initMocks(this);

// JUnit 5
MockitoAnnotations.openMocks(this);
```

---

## 📚 RESUMEN

**Para tu proyecto con JUnit 4:**

1. **Tests de clases de dominio** (Usuario, Encuesta):
   - Sin mocks
   - Usa `@Before`, `@Test`
   - Import: `import main.domini.*;`

2. **Tests de controladores de dominio** (CtrlDominioUsuario):
   - Con mocks
   - Usa `@RunWith(MockitoJUnitRunner.class)`
   - Usa `MockitoAnnotations.initMocks(this)` en `@Before`
   - `when()`, `verify()` para mocks

3. **Tests parametrizados**:
   - Usa `@RunWith(Parameterized.class)`
   - Define `@Parameters`
   - Constructor con parámetros

**Ya tienes 5 ejemplos completos listos para usar:** ✅
- TestUsuario.java (simple)
- TestUsuarioParametrizado.java (parametrizado)
- TestEncuesta.java (completo sin mocks)
- TestCtrlDominioUsuario.java (con mocks)
- TestCtrlDominioEncuesta.java (avanzado con mocks)

¡Solo copia y adapta según necesites!
