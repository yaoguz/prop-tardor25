# 📚 GUÍA COMPLETA: TESTING, DRIVERS, MOCKS Y STUBS

## 🎯 CONCEPTOS FUNDAMENTALES

### 1️⃣ DRIVER (Programa de Prueba Manual)

**¿Qué es?**
- Programa con `main()` que permite ejecutar funcionalidades MANUALMENTE
- Tiene un menú interactivo (con Scanner)
- El usuario (o profesor) elige opciones y ve resultados en consola

**¿Para qué sirve?**
- **1er lliurament**: Demostrar casos de uso funcionando
- Permitir al profesor ejecutar y verificar funcionalidades
- NO es automatizado - requiere intervención humana

**¿Cuándo se usa?**
- Para demostrar que tu código funciona
- Para "juegos de prueba" (jocs de prova) documentados
- Cada controlador o caso de uso tiene su driver

**Ejemplo de uso:**
```bash
$ java driver.DriverCtrlDominioUsuario
╔═══════════════════════════════════╗
║  DRIVER - GESTIÓN DE USUARIOS     ║
╚═══════════════════════════════════╝
1. Registrar usuario
2. Iniciar sesión
3. Modificar contraseña
0. Salir

Selecciona: 1
Nombre: testUser
Contraseña: 12345
Confirmar: 12345
✅ Usuario registrado con éxito
```

**📁 Archivo:** `driver/DriverCtrlDominioUsuario.java`

---

### 2️⃣ TEST UNITARIO (JUnit - Automatizado)

**¿Qué es?**
- Código que **verifica automáticamente** que tu código funciona
- Usa anotaciones como `@Test`
- Ejecuta con `gradle test` o `./gradlew test`

**¿Para qué sirve?**
- Verificar que cada método funciona correctamente
- Detectar errores automáticamente (regression testing)
- Cobertura de código (code coverage)

**¿Cuándo se usa?**
- Durante desarrollo (TDD - Test Driven Development)
- Antes de hacer commits
- En integración continua (CI/CD)

**Características:**
- **Automatizado** - se ejecuta sin intervención
- **Repetible** - siempre mismo resultado
- **Rápido** - miles de tests en segundos
- **Isolado** - cada test es independiente

**Ejemplo:**
```java
@Test
public void testRegistrarUsuario_CredencialesValidas_CreaUsuario() {
    String resultado = ctrlDominio.registrarUsuario("user", "pass", "pass");
    assertEquals("Usuario registrado con éxito: user", resultado);
}
```

**📁 Archivo:** `src/test/java/domaincontroller/TestCtrlDominioUsuario.java`

---

### 3️⃣ STUB (Implementación Falsa)

**¿Qué es?**
- Implementación **simplificada** que sustituye componentes reales
- Devuelve datos **hardcodeados** o simulados
- NO verifica comportamiento, solo provee datos

**¿Para qué sirve?**
- Sustituir capas que no están listas (ej: base de datos)
- Acelerar tests (no esperar disco/red)
- Simplificar desarrollo

**Tu caso actual:**
- `CtrlPersistence` es un **STUB**
- Finge que guarda en disco, pero usa HashMap en memoria

**Ejemplo:**
```java
// STUB de persistencia - NO guarda en disco real
public class CtrlPersistence {
    private Map<String, Usuario> usuarios = new HashMap<>();
    
    public void guardarUsuario(Usuario u) {
        // NO escribe a disco - solo guarda en memoria
        usuarios.put(u.getNomUsuario(), u);
    }
}
```

**Ventajas:**
- Simple de implementar
- Rápido (no hay I/O)
- Suficiente para desarrollo inicial

**Desventajas:**
- No verifica que usas correctamente la API
- No detecta errores de integración

---

### 4️⃣ MOCK (Objeto Simulado Verificable)

**¿Qué es?**
- Objeto **falso** creado con framework (Mockito)
- **Registra** todas las llamadas que recibe
- **Verifica** que tu código interactúa correctamente

**¿Para qué sirve?**
- Verificar que tu código llama a métodos correctos
- Controlar qué devuelve cada método
- Detectar llamadas incorrectas o faltantes

**Diferencia con STUB:**
| STUB | MOCK |
|------|------|
| Solo devuelve datos | Devuelve datos Y verifica llamadas |
| No sabe si lo usaste | Verifica que lo llamaste correctamente |
| Implementación manual | Framework automático (Mockito) |
| Para datos simples | Para verificar comportamiento |

**Ejemplo:**
```java
// Crear MOCK de CtrlUsuario
@Mock
private CtrlUsuario mockCtrlUsuario;

@Test
public void test() {
    // CONFIGURAR comportamiento
    when(mockCtrlUsuario.existeUsuario("user")).thenReturn(false);
    
    // EJECUTAR código bajo prueba
    ctrlDominio.registrarUsuario("user", "pass", "pass");
    
    // VERIFICAR que se llamó al mock correctamente
    verify(mockCtrlUsuario).existeUsuario("user");
    verify(mockCtrlUsuario).crearUsuario("user", "pass");
}
```

---

## 🏗️ ARQUITECTURA DE TESTING EN TU PROYECTO

```
┌─────────────────────────────────────────────────────────────┐
│                   CAPA DE PRESENTACIÓN                      │
│                  (CtrlPresentacion, Vistas)                 │
│                                                             │
│  Driver: DriverPresentacion.java (menú principal)           │
│  Test: TestCtrlPresentacion.java (con mocks de dominio)    │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│              CAPA DE DOMINIO (Controladores)                │
│         CtrlDominioUsuario, CtrlDominioEncuesta, etc.       │
│                                                             │
│  Driver: DriverCtrlDominioUsuario.java                      │
│  Test: TestCtrlDominioUsuario.java                          │
│  ✅ USAR MOCKS AQUÍ para CtrlUsuario y CtrlPersistence      │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│         CAPA DE DATOS (Controladores de Clase)              │
│           CtrlUsuario, CtrlEncuesta, etc.                   │
│                                                             │
│  Driver: DriverCtrlUsuario.java                             │
│  Test: TestCtrlUsuario.java                                 │
│  ❌ NO necesitan mocks - usan objetos de dominio reales     │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                  CAPA DE DOMINIO (Clases)                   │
│               Usuario, Encuesta, Pregunta, etc.             │
│                                                             │
│  Driver: DriverUsuario.java (ya existe)                     │
│  Test: TestUsuario.java                                     │
│  ❌ NO necesitan mocks - lógica pura sin dependencias       │
└─────────────────────────────────────────────────────────────┘
                            ↓
┌─────────────────────────────────────────────────────────────┐
│                  CAPA DE PERSISTENCIA                       │
│                   CtrlPersistence (STUB)                    │
│                                                             │
│  Implementación actual: STUB (HashMap en memoria)           │
│  Para testing: crear ICtrlPersistence (interfaz)            │
│  ✅ MOCKEAR en tests de capas superiores                    │
└─────────────────────────────────────────────────────────────┘
```

---

## 📋 QUÉ NECESITA CADA CAPA

### **Clases de Dominio** (Usuario, Encuesta, Pregunta, etc.)

```
✅ Driver: DriverUsuario.java (ya existe)
   - Prueba métodos individualmente
   - Menú: 1. Constructor, 2. newContrasena, 3. guardarIdRespuesta, etc.

✅ Tests: TestUsuario.java
   - SIN mocks (no tiene dependencias)
   - Prueba lógica interna pura
   - Ejemplo: testComprobarContrasena_CredencialesCorrectas_RetornaTrue()
```

---

### **Controladores de Clase** (CtrlUsuario, CtrlEncuesta, etc.)

```
✅ Driver: DriverCtrlUsuario.java
   - Prueba CRUD de usuarios
   - Menú: 1. Crear usuario, 2. Buscar usuario, 3. Eliminar usuario

✅ Tests: TestCtrlUsuario.java
   - Pueden usar objetos Usuario reales (no mocks)
   - Prueba gestión de colecciones
   - Ejemplo: testCrearUsuario_NuevoUsuario_AgregaAColeccion()
```

---

### **Controladores de Dominio** ⭐ (CtrlDominioUsuario, etc.)

```
✅ Driver: DriverCtrlDominioUsuario.java (CREADO ARRIBA)
   - Prueba casos de uso completos
   - Menú interactivo con flujos reales
   - Simula experiencia de usuario

✅ Tests: TestCtrlDominioUsuario.java (CREADO ARRIBA)
   - ⭐ AQUÍ SÍ USAR MOCKS
   - Mock de CtrlUsuario (capa de datos)
   - Mock de CtrlPersistence (persistencia)
   - Verifica que orquesta correctamente
```

**¿Por qué mocks aquí?**
- Estos controladores **orquestan** múltiples capas
- Queremos verificar que **llaman correctamente** a dependencias
- No queremos que errores en capas inferiores oculten errores aquí
- Tests más **rápidos** (no hay I/O simulado)

---

### **Capa de Presentación** (CtrlPresentacion)

```
✅ Driver: DriverPrincipal.java
   - Menú general del sistema
   - Navega entre todos los casos de uso

✅ Tests: TestCtrlPresentacion.java
   - Mock de CtrlDominio
   - Verifica flujos de interfaz
```

---

## 🛠️ IMPLEMENTACIÓN CON MOCKITO

### **Paso 1: Agregar Mockito al proyecto**

Ya hecho - ver `build.gradle`:
```gradle
dependencies {
    testImplementation('org.mockito:mockito-core:5.5.0')
}
```

### **Paso 2: Crear interfaz para dependencias mockables**

Ya creado - ver `persistence/ICtrlPersistence.java`

**¿Por qué interfaz?**
- Mockito puede mockear interfaces fácilmente
- Permite cambiar implementación (stub → real)
- Dependency Inversion Principle (SOLID)

### **Paso 3: Escribir test con mocks**

```java
public class TestCtrlDominioUsuario {
    @Mock
    private CtrlUsuario mockCtrlUsuario;  // Mock automático
    
    @Mock
    private ICtrlPersistence mockPersistence;  // Mock automático
    
    private CtrlDominioUsuario ctrlDominioUsuario;
    
    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);  // Inicializar mocks
        
        // Inyectar mocks en el objeto bajo prueba
        ctrlDominioUsuario = new CtrlDominioUsuario(
            mockCtrlUsuario, 
            mockPersistence
        );
    }
    
    @Test
    public void testRegistrarUsuario() {
        // 1. CONFIGURAR mocks (qué deben devolver)
        when(mockCtrlUsuario.existeUsuario("user")).thenReturn(false);
        when(mockCtrlUsuario.crearUsuario("user", "pass"))
            .thenReturn(new Usuario("user", "pass"));
        
        // 2. EJECUTAR código bajo prueba
        String resultado = ctrlDominioUsuario.registrarUsuario(
            "user", "pass", "pass"
        );
        
        // 3. VERIFICAR resultado
        assertEquals("Usuario registrado con éxito: user", resultado);
        
        // 4. VERIFICAR que se llamaron los mocks correctamente
        verify(mockCtrlUsuario).existeUsuario("user");
        verify(mockCtrlUsuario).crearUsuario("user", "pass");
        verify(mockPersistence).guardarUsuario(any(Usuario.class));
    }
}
```

---

## 📊 TABLA RESUMEN: CUÁNDO USAR CADA COSA

| Componente | Driver | Test sin Mock | Test con Mock |
|------------|--------|---------------|---------------|
| **Usuario.java** | ✅ Sí | ✅ Sí | ❌ No |
| **CtrlUsuario.java** | ✅ Sí | ✅ Sí | ❌ No |
| **CtrlDominioUsuario.java** | ✅ Sí | ❌ No | ✅ SÍ |
| **CtrlPresentacion.java** | ✅ Sí | ❌ No | ✅ SÍ |
| **CtrlPersistence.java** | ❌ No* | ❌ No | ✅ Mock en otros tests |

*CtrlPersistence es un stub, no necesita driver propio

---

## 🎯 PLAN DE ACCIÓN PARA TU PROYECTO

### **Para el 1er Lliurament (Drivers):**

1. **Crear driver para cada controlador de dominio:**
   ```
   ✅ driver/DriverCtrlDominioUsuario.java (CREADO)
   📝 driver/DriverCtrlDominioEncuesta.java (TODO)
   📝 driver/DriverCtrlDominioPregunta.java (TODO)
   📝 driver/DriverCtrlDominioRespuesta.java (TODO)
   📝 driver/DriverPrincipal.java (menú general) (TODO)
   ```

2. **Documentar "juegos de prueba":**
   - Para cada driver, documentar:
     - Caso de prueba (ej: "Registrar usuario válido")
     - Pasos a seguir
     - Resultado esperado
     - Resultado obtenido

### **Para Tests Unitarios:**

1. **Tests de clases de dominio (SIN mocks):**
   ```
   ✅ src/test/java/domain/TestUsuario.java (CREADO)
   📝 src/test/java/domain/TestEncuesta.java (TODO)
   📝 src/test/java/domain/TestPregunta.java (TODO)
   📝 src/test/java/domain/TestRespuesta.java (TODO)
   ```

2. **Tests de controladores de clase (SIN mocks):**
   ```
   📝 src/test/java/classcontroller/TestCtrlUsuario.java (TODO)
   📝 src/test/java/classcontroller/TestCtrlEncuesta.java (TODO)
   ```

3. **Tests de controladores de dominio (CON mocks):**
   ```
   ✅ src/test/java/domaincontroller/TestCtrlDominioUsuario.java (CREADO)
   📝 src/test/java/domaincontroller/TestCtrlDominioEncuesta.java (TODO)
   📝 src/test/java/domaincontroller/TestCtrlDominioPregunta.java (TODO)
   ```

---

## 🔧 COMANDOS ÚTILES

```bash
# Ejecutar todos los tests
./gradlew test

# Ejecutar tests con reporte de cobertura
./gradlew test jacocoTestReport

# Ejecutar un driver específico
java -cp build/classes/java/main driver.DriverCtrlDominioUsuario

# Compilar todo
./gradlew build

# Ver resultados de tests
cat build/reports/tests/test/index.html
```

---

## 💡 VENTAJAS DE USAR MOCKS vs STUBS

### **Con STUB (tu implementación actual):**
```java
@Test
public void test() {
    CtrlUsuario ctrlUsuario = new CtrlUsuario();  // Stub real
    CtrlPersistence persistence = new CtrlPersistence();  // Stub real
    
    CtrlDominioUsuario ctrl = new CtrlDominioUsuario(ctrlUsuario, persistence);
    
    // Si hay bug en CtrlUsuario, este test falla
    // No sabes si el error está en CtrlDominioUsuario o en CtrlUsuario
    ctrl.registrarUsuario("user", "pass", "pass");
}
```

### **Con MOCK (implementación recomendada):**
```java
@Test
public void test() {
    @Mock CtrlUsuario mockCtrlUsuario;  // Mock controlado
    @Mock ICtrlPersistence mockPersistence;  // Mock controlado
    
    CtrlDominioUsuario ctrl = new CtrlDominioUsuario(
        mockCtrlUsuario, mockPersistence
    );
    
    // Controlas EXACTAMENTE qué devuelve cada mock
    when(mockCtrlUsuario.existeUsuario("user")).thenReturn(false);
    
    // Test solo falla si HAY ERROR EN CtrlDominioUsuario
    ctrl.registrarUsuario("user", "pass", "pass");
    
    // VERIFICAS que llamó correctamente a las dependencias
    verify(mockCtrlUsuario).existeUsuario("user");
    verify(mockPersistence).guardarUsuario(any());
}
```

**Ventajas:**
- ✅ **Aislamiento** - test solo falla si hay error en la clase bajo prueba
- ✅ **Velocidad** - no ejecuta código real de dependencias
- ✅ **Control** - decides qué devuelve cada método
- ✅ **Verificación** - compruebas que llama a métodos correctos
- ✅ **Casos edge** - puedes simular errores/excepciones fácilmente

---

## 📚 GLOSARIO FINAL

- **Driver**: Programa MANUAL con menú para demostrar funcionalidad
- **Test Unitario**: Código AUTOMATIZADO que verifica funcionalidad
- **Stub**: Implementación FALSA que devuelve datos hardcodeados
- **Mock**: Objeto FALSO que verifica interacciones (con Mockito)
- **Joc de prova**: Documentación de casos de prueba manual (con drivers)
- **Coverage**: % de código ejecutado por tests
- **Assertion**: Verificación en test (assertEquals, assertTrue, etc.)
- **Verify**: Comprobación de que se llamó a un mock correctamente

---

## ✅ CHECKLIST PARA EL 1ER LLIURAMENT

- [ ] Drivers de todos los controladores de dominio
- [ ] Driver principal con menú general
- [ ] Documentación de juegos de prueba
- [ ] Tests unitarios con >80% cobertura
- [ ] Tests usan mocks para controladores de dominio
- [ ] Ejecutar `./gradlew test` sin errores
- [ ] Javadoc completo en todas las clases
