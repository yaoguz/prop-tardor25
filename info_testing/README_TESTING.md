# 🧪 ESTRATEGIA DE TESTING - RESUMEN EJECUTIVO

## 📊 MAPA CONCEPTUAL

```
┌─────────────────────────────────────────────────────────────────────┐
│                    TIPOS DE TESTING EN TU PROYECTO                  │
└─────────────────────────────────────────────────────────────────────┘

                                TESTING
                                   │
                ┌──────────────────┼──────────────────┐
                │                  │                  │
            MANUAL            AUTOMATIZADO        VERIFICACIÓN
                │                  │                  │
         ┌──────┴──────┐    ┌─────┴─────┐      ┌────┴────┐
         │             │    │           │      │         │
      DRIVER      JUEGO DE  TEST      TEST    STUB     MOCK
                  PRUEBA   UNITARIO  INTEGR.
```

---

## 🎯 RESPUESTA RÁPIDA A TU PREGUNTA

### **"¿Dónde necesito drivers/mocks/stubs?"**

| Componente | Driver | Test Unit. | Mock | Stub |
|------------|:------:|:----------:|:----:|:----:|
| **Usuario.java** | ✅ | ✅ | ❌ | ❌ |
| **Encuesta.java** | ✅ | ✅ | ❌ | ❌ |
| **CtrlUsuario.java** | ✅ | ✅ | ❌ | ❌ |
| **CtrlDominioUsuario.java** | ✅ | ✅ | ✅ | ❌ |
| **CtrlDominioEncuesta.java** | ✅ | ✅ | ✅ | ❌ |
| **CtrlPresentacion.java** | ✅ | ✅ | ✅ | ❌ |
| **CtrlPersistence.java** | ❌ | ❌ | ✅* | ✅ |

*Se mockea cuando lo usan otros tests

---

## 📁 ARCHIVOS CREADOS PARA TI

```
/root/prop/subgrup-prop11.5/
│
├── GUIA_TESTING_COMPLETA.md              ← Guía detallada completa
├── EJEMPLO_STUB_VS_MOCK.java             ← Comparación código real
│
├── persistence/
│   └── ICtrlPersistence.java             ← Interfaz para mockear ✅
│
├── driver/
│   └── DriverCtrlDominioUsuario.java     ← Driver interactivo ✅
│
└── src/test/java/
    ├── domain/
    │   └── TestUsuario.java              ← Test SIN mocks ✅
    │
    └── domaincontroller/
        └── TestCtrlDominioUsuario.java   ← Test CON mocks ✅
```

---

## 🚀 CÓMO EJECUTAR

### **1. Instalar dependencias (Mockito)**
```bash
./gradlew build
```

### **2. Ejecutar tests automatizados**
```bash
# Todos los tests
./gradlew test

# Ver reporte HTML
xdg-open build/reports/tests/test/index.html
```

### **3. Ejecutar driver manual**
```bash
# Compilar
./gradlew compileJava

# Ejecutar driver
java -cp build/classes/java/main driver.DriverCtrlDominioUsuario
```

---

## 💡 CONCEPTO CLAVE: CUÁNDO USAR MOCKS

```
┌─────────────────────────────────────────────────────────────┐
│  ¿LA CLASE TIENE DEPENDENCIAS EXTERNAS?                    │
│                                                             │
│  NO → NO USES MOCKS                                        │
│       Ejemplo: Usuario.java, Encuesta.java                 │
│       Test: TestUsuario.java (objetos reales)              │
│                                                             │
│  SÍ → ¿LA CLASE ORQUESTA OTRAS CAPAS?                      │
│                                                             │
│       NO → NO USES MOCKS                                   │
│            Ejemplo: CtrlUsuario.java (solo gestiona datos) │
│            Test: TestCtrlUsuario.java (objetos reales)     │
│                                                             │
│       SÍ → USA MOCKS ✅                                     │
│            Ejemplo: CtrlDominioUsuario.java                │
│            Test: TestCtrlDominioUsuario.java (con mocks)   │
└─────────────────────────────────────────────────────────────┘
```

---

## 📋 EJEMPLO PRÁCTICO: CtrlDominioUsuario

### **ARQUITECTURA**

```
TestCtrlDominioUsuario.java (TEST)
         │
         ├── Mock: CtrlUsuario          ← Simula capa de datos
         ├── Mock: CtrlPersistence      ← Simula persistencia
         │
         └── Real: CtrlDominioUsuario   ← CLASE BAJO PRUEBA
```

### **CÓDIGO DEL TEST**

```java
@Mock
private CtrlUsuario mockCtrlUsuario;      // Mockito crea esto

@Mock  
private ICtrlPersistence mockPersistence; // Mockito crea esto

@Before
public void setUp() {
    MockitoAnnotations.openMocks(this);
    
    ctrlDominioUsuario = new CtrlDominioUsuario(
        mockCtrlUsuario,     // Inyectar mock
        mockPersistence      // Inyectar mock
    );
}

@Test
public void testRegistrarUsuario() {
    // 1. CONFIGURAR mocks
    when(mockCtrlUsuario.existeUsuario("user")).thenReturn(false);
    when(mockCtrlUsuario.crearUsuario("user", "pass"))
        .thenReturn(new Usuario("user", "pass"));
    
    // 2. EJECUTAR
    String resultado = ctrlDominioUsuario.registrarUsuario(
        "user", "pass", "pass"
    );
    
    // 3. VERIFICAR resultado
    assertEquals("Usuario registrado con éxito: user", resultado);
    
    // 4. VERIFICAR que llamó a los mocks correctamente
    verify(mockCtrlUsuario).existeUsuario("user");
    verify(mockCtrlUsuario).crearUsuario("user", "pass");
    verify(mockPersistence).guardarUsuario(any(Usuario.class));
}
```

---

## 🎓 DIFERENCIA STUB vs MOCK

### **STUB (tu implementación actual de CtrlPersistence)**

```java
// STUB = Implementación simple que FINGE funcionar
public class CtrlPersistence {
    private Map<String, Usuario> usuarios = new HashMap<>();
    
    public void guardarUsuario(Usuario u) {
        // FINGE que guarda en disco, pero usa memoria
        usuarios.put(u.getNomUsuario(), u);
    }
}

// En test:
CtrlPersistence stub = new CtrlPersistence(); // Objeto real
// El stub NO sabe si lo usaste correctamente
```

**Características:**
- ✅ Implementación manual simple
- ✅ Devuelve datos hardcodeados/predefinidos
- ❌ NO verifica cómo lo usas
- ❌ NO detecta llamadas incorrectas
- ❌ Ejecuta código real (más lento)

### **MOCK (con Mockito - recomendado)**

```java
// MOCK = Objeto falso que REGISTRA y VERIFICA llamadas
@Mock
private CtrlUsuario mockCtrlUsuario; // Mockito lo crea

// Configurar comportamiento
when(mockCtrlUsuario.existeUsuario("user")).thenReturn(false);

// Ejecutar código bajo prueba
ctrlDominioUsuario.registrarUsuario("user", "pass", "pass");

// VERIFICAR que se usó correctamente
verify(mockCtrlUsuario).existeUsuario("user");
verify(mockCtrlUsuario).crearUsuario("user", "pass");
```

**Características:**
- ✅ Framework automático (Mockito)
- ✅ Devuelve lo que TÚ configuras
- ✅ **VERIFICA** que lo llamaste correctamente
- ✅ Detecta llamadas faltantes o incorrectas
- ✅ NO ejecuta código real (más rápido)
- ✅ Simula excepciones fácilmente

---

## 🏗️ PLAN DE IMPLEMENTACIÓN

### **Fase 1: Tests de Dominio (SIN mocks) - SIMPLE**

```bash
✅ src/test/java/domain/TestUsuario.java       (CREADO)
📝 src/test/java/domain/TestEncuesta.java      (TODO)
📝 src/test/java/domain/TestPregunta.java      (TODO)
📝 src/test/java/domain/TestRespuesta.java     (TODO)
```

**Complejidad**: ⭐ Fácil - Solo lógica interna, sin dependencias

### **Fase 2: Tests de Controladores de Clase (SIN mocks) - MEDIO**

```bash
📝 src/test/java/classcontroller/TestCtrlUsuario.java
📝 src/test/java/classcontroller/TestCtrlEncuesta.java
```

**Complejidad**: ⭐⭐ Medio - Usan objetos de dominio reales

### **Fase 3: Tests de Controladores de Dominio (CON mocks) - COMPLEJO**

```bash
✅ src/test/java/domaincontroller/TestCtrlDominioUsuario.java  (CREADO)
📝 src/test/java/domaincontroller/TestCtrlDominioEncuesta.java
📝 src/test/java/domaincontroller/TestCtrlDominioPregunta.java
📝 src/test/java/domaincontroller/TestCtrlDominioRespuesta.java
```

**Complejidad**: ⭐⭐⭐ Avanzado - Requiere mocks y verificación

### **Fase 4: Drivers (para 1er lliurament) - SIMPLE**

```bash
✅ driver/DriverCtrlDominioUsuario.java        (CREADO)
📝 driver/DriverCtrlDominioEncuesta.java
📝 driver/DriverPrincipal.java                 (menú general)
```

**Complejidad**: ⭐ Fácil - Solo Scanner y menús

---

## 🔧 COMANDOS MOCKITO MÁS USADOS

```java
// CREAR MOCK
@Mock
private CtrlUsuario mockCtrlUsuario;

// CONFIGURAR qué devuelve
when(mock.metodo("param")).thenReturn(valor);

// CONFIGURAR excepción
when(mock.metodo("param")).thenThrow(new RuntimeException());

// VERIFICAR que se llamó
verify(mock).metodo("param");

// VERIFICAR N veces
verify(mock, times(2)).metodo("param");

// VERIFICAR NUNCA se llamó
verify(mock, never()).metodo(anyString());

// VERIFICAR con cualquier parámetro
verify(mock).metodo(any(Usuario.class));
```

---

## ✅ CHECKLIST PARA EL 1ER LLIURAMENT

### **Testing Automatizado**
- [ ] Tests unitarios de clases de dominio (Usuario, Encuesta, etc.)
- [ ] Tests de controladores de clase (CtrlUsuario, etc.)
- [ ] Tests CON MOCKS de controladores de dominio ✅
- [ ] Cobertura de tests > 80%
- [ ] `./gradlew test` sin errores

### **Testing Manual (Drivers)**
- [ ] Driver para cada controlador de dominio
- [ ] Driver principal con menú general
- [ ] Documentación de juegos de prueba

### **Documentación**
- [ ] JavaDoc completo en todas las clases
- [ ] README con instrucciones de ejecución
- [ ] Casos de prueba documentados

---

## 📚 RECURSOS

- **Guía completa**: `GUIA_TESTING_COMPLETA.md`
- **Ejemplo comparativo**: `EJEMPLO_STUB_VS_MOCK.java`
- **Test ejemplo SIN mocks**: `src/test/java/domain/TestUsuario.java`
- **Test ejemplo CON mocks**: `src/test/java/domaincontroller/TestCtrlDominioUsuario.java`
- **Driver ejemplo**: `driver/DriverCtrlDominioUsuario.java`

---

## 🎯 CONCLUSIÓN

**Para tu proyecto:**

1. **Clases de Dominio** (Usuario, Encuesta) → Tests SIN mocks
2. **Controladores de Clase** (CtrlUsuario) → Tests SIN mocks  
3. **Controladores de Dominio** (CtrlDominioUsuario) → Tests CON mocks ✅
4. **Todos los controladores** → Drivers para el 1er lliurament

**¿Por qué usar mocks en controladores de dominio?**
- ✅ Aíslan la clase bajo prueba
- ✅ Verifican que orquestan correctamente
- ✅ Tests más rápidos y confiables
- ✅ Detectan errores de integración

**Tu proyecto ya tiene:**
- ✅ Mockito configurado en build.gradle
- ✅ ICtrlPersistence.java (interfaz para mockear)
- ✅ TestCtrlDominioUsuario.java (ejemplo completo con mocks)
- ✅ TestUsuario.java (ejemplo sin mocks)
- ✅ DriverCtrlDominioUsuario.java (driver interactivo)
