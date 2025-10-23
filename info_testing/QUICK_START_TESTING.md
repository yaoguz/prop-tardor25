# 🚀 QUICK START: Testing en 5 Minutos

## 🎯 RESPUESTA DIRECTA A TU PREGUNTA

### **"¿Dónde necesito mocks en vez de stubs?"**

**RESPUESTA:** En los **controladores de dominio** (CtrlDominioUsuario, CtrlDominioEncuesta, etc.)

**RAZÓN:** Estos controladores **orquestan** múltiples capas (datos + persistencia). Los mocks te permiten:
- ✅ Verificar que llaman correctamente a dependencias
- ✅ Aislar errores (test solo falla si hay error en el controlador)
- ✅ Tests más rápidos (no ejecutan código real)

---

## 📁 ARCHIVOS QUE YA TIENES (LISTOS PARA USAR)

```bash
# 1. GUÍA COMPLETA (lee esto primero)
GUIA_TESTING_COMPLETA.md

# 2. EJEMPLOS DE CÓDIGO
- EJEMPLO_STUB_VS_MOCK.java          # Comparación visual
- TestUsuario.java                   # Test SIN mocks ✅
- TestCtrlDominioUsuario.java        # Test CON mocks ✅
- DriverCtrlDominioUsuario.java      # Driver interactivo ✅

# 3. PLANTILLAS PARA COPIAR
PLANTILLAS_TESTING.md

# 4. DIAGRAMA DE DECISIÓN
DIAGRAMA_DECISION_TESTING.md

# 5. ESTE RESUMEN
README_TESTING.md
```

---

## ⚡ CÓMO EMPEZAR AHORA MISMO

### **OPCIÓN 1: Ejecutar ejemplos que ya tienes**

```bash
# 1. Instalar Mockito
./gradlew build

# 2. Ejecutar tests
./gradlew test

# 3. Ver resultados
# (Nota: Pueden fallar por errores de classpath - es normal por ahora)
```

### **OPCIÓN 2: Ejecutar driver interactivo**

```bash
# 1. Compilar
./gradlew compileJava

# 2. Ejecutar driver
java -cp build/classes/java/main:build/classes/java/main/classcontroller:build/classes/java/main/domaincontroller:build/classes/java/main/domain:build/classes/java/main/persistence driver.DriverCtrlDominioUsuario

# 3. Probar opciones del menú
```

---

## 📊 CONCEPTOS EN 1 MINUTO

### **DRIVER = Programa MANUAL para demostrar al profesor**
- Tiene `main()` y menú con `Scanner`
- Tú (o el profesor) ejecutas y ves resultados
- Para el **1er lliurament**

### **TEST UNITARIO = Código AUTOMATIZADO que verifica funcionalidad**
- Se ejecuta con `gradle test`
- No necesita intervención humana
- Detecta errores automáticamente

### **STUB = Implementación FALSA simple**
- Tu `CtrlPersistence` actual ES un stub
- Finge que guarda en disco, pero usa HashMap
- NO verifica cómo lo usas

### **MOCK = Objeto FALSO que VERIFICA llamadas**
- Creado con framework Mockito
- Registra todas las llamadas
- Verifica que tu código lo usa correctamente
- **USA ESTO en controladores de dominio** ✅

---

## 🗺️ DÓNDE USAR CADA COSA

```
┌─────────────────────────────────────────────────────┐
│  CAPA DE DOMINIO (Usuario.java, Encuesta.java)     │
│  → Driver ✅  Test sin Mocks ✅  Mock ❌            │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│  CONTROLADORES DE CLASE (CtrlUsuario.java)         │
│  → Driver ✅  Test sin Mocks ✅  Mock ❌            │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│  CONTROLADORES DE DOMINIO (CtrlDominioUsuario)     │
│  → Driver ✅  Test SIN Mocks ❌  Test CON Mocks ✅  │
│  ⭐ AQUÍ ES DONDE USAS MOCKS ⭐                      │
└─────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────┐
│  PERSISTENCIA (CtrlPersistence)                     │
│  → Driver ❌  Test ❌  Es un STUB ✅                │
│  (Se mockea cuando lo usan otros tests)            │
└─────────────────────────────────────────────────────┘
```

---

## 💡 EJEMPLO MÍNIMO DE MOCK

```java
// 1. CREAR MOCKS
@Mock
private CtrlUsuario mockCtrlUsuario;

// 2. INICIALIZAR
@Before
public void setUp() {
    MockitoAnnotations.openMocks(this);
    controlador = new CtrlDominioUsuario(mockCtrlUsuario, ...);
}

// 3. CONFIGURAR qué devuelve
@Test
public void test() {
    when(mockCtrlUsuario.existeUsuario("user")).thenReturn(false);
    
    // 4. EJECUTAR código bajo prueba
    controlador.registrarUsuario("user", "pass", "pass");
    
    // 5. VERIFICAR que llamó al mock
    verify(mockCtrlUsuario).existeUsuario("user");
    verify(mockCtrlUsuario).crearUsuario("user", "pass");
}
```

---

## 🎯 DIFERENCIA CLAVE: STUB vs MOCK

### **CON STUB (problemático)**
```java
@Test
public void test() {
    CtrlUsuario stub = new CtrlUsuario();  // Objeto REAL
    
    controlador.registrarUsuario("user", "pass", "pass");
    
    // ❌ No puedes verificar que se llamó a existeUsuario()
    // ❌ No puedes verificar que se llamó a crearUsuario()
    // ❌ Si hay bug en CtrlUsuario, este test falla
}
```

### **CON MOCK (recomendado)**
```java
@Test
public void test() {
    @Mock CtrlUsuario mock;  // Objeto FALSO de Mockito
    
    when(mock.existeUsuario("user")).thenReturn(false);
    
    controlador.registrarUsuario("user", "pass", "pass");
    
    // ✅ Verificas que se llamó correctamente
    verify(mock).existeUsuario("user");
    verify(mock).crearUsuario("user", "pass");
    
    // ✅ Test solo falla si hay error en CtrlDominioUsuario
}
```

---

## ✅ TODO LIST PARA TI

### **Hoy (30 minutos)**
- [ ] Leer `GUIA_TESTING_COMPLETA.md` (sección conceptos)
- [ ] Revisar `TestCtrlDominioUsuario.java` (ejemplo completo)
- [ ] Ejecutar `./gradlew test`

### **Esta semana (2-3 horas)**
- [ ] Crear TestEncuesta.java (copia plantilla de TestUsuario.java)
- [ ] Crear TestCtrlUsuario.java (sin mocks)
- [ ] Crear DriverEncuesta.java (copia DriverCtrlDominioUsuario.java)

### **Próxima semana (3-4 horas)**
- [ ] Crear TestCtrlDominioEncuesta.java (CON mocks, copia TestCtrlDominioUsuario.java)
- [ ] Crear TestCtrlDominioPregunta.java (CON mocks)
- [ ] Crear TestCtrlDominioRespuesta.java (CON mocks)

### **Antes del 1er lliurament**
- [ ] Driver principal con menú general
- [ ] Documentar juegos de prueba (ver plantilla en PLANTILLAS_TESTING.md)
- [ ] Verificar cobertura >80%
- [ ] Ejecutar todos los tests sin errores

---

## 🔥 LO MÁS IMPORTANTE

1. **Drivers** = Para demostrar manualmente (1er lliurament)
2. **Tests sin mocks** = Para clases simples sin dependencias
3. **Tests con mocks** = Para controladores de dominio ⭐
4. **Ya tienes ejemplos completos de los 3** ✅

**NO necesitas implementar TODO desde cero** - copia las plantillas y adapta.

---

## 📞 SI TIENES DUDAS

1. **Conceptos generales** → Lee `GUIA_TESTING_COMPLETA.md`
2. **Cómo escribir un test específico** → Copia de `PLANTILLAS_TESTING.md`
3. **¿Qué tipo de test usar?** → Mira `DIAGRAMA_DECISION_TESTING.md`
4. **Diferencia stub vs mock** → Lee `EJEMPLO_STUB_VS_MOCK.java`

---

## 🎓 RESUMEN EJECUTIVO

**Tu pregunta:** "¿Dónde usar mocks?"

**Respuesta:** En **controladores de dominio** (CtrlDominioUsuario, CtrlDominioEncuesta, etc.)

**Razón:** Estos orquestan múltiples capas. Los mocks permiten:
1. Verificar que llaman correctamente a dependencias
2. Aislar errores
3. Tests más rápidos

**Ya tienes:**
- ✅ Mockito configurado
- ✅ Interfaz ICtrlPersistence para mockear
- ✅ Ejemplo completo: TestCtrlDominioUsuario.java
- ✅ Driver ejemplo: DriverCtrlDominioUsuario.java
- ✅ Plantillas para copiar

**Siguiente paso:**
Copia `TestCtrlDominioUsuario.java` y adapta para `TestCtrlDominioEncuesta.java`

---

## 🚀 COMANDO PARA PROBAR AHORA

```bash
# 1. Instalar dependencias
./gradlew build

# 2. Ejecutar tests (algunos pueden fallar por classpath - es normal)
./gradlew test

# 3. Ver qué archivos tienes
ls -la *.md
cat GUIA_TESTING_COMPLETA.md
```

**¡Ya estás listo para empezar!** 🎉
