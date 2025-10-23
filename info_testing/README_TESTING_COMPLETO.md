# 🧪 TESTING COMPLETO: Driver, Tests Unitarios, Mocks y Stubs

## 📌 RESUMEN EJECUTIVO

Has pedido ayuda para entender la diferencia entre **drivers, tests unitarios, mocks y stubs**, y cómo implementar **mocks en vez de stubs** en tu proyecto.

**✅ RESPUESTA CORTA:**
- **Drivers**: Programas manuales para demostrar casos de uso (1er lliurament)
- **Tests**: Código automatizado que verifica funcionalidad
- **Stubs**: Implementaciones falsas simples (tu `CtrlPersistence` actual)
- **Mocks**: Objetos falsos que VERIFICAN interacciones (usa en controladores de dominio)

**✅ DÓNDE USAR MOCKS:**
En los **controladores de dominio** (CtrlDominioUsuario, CtrlDominioEncuesta, etc.) porque:
- Orquestan múltiples capas
- Necesitas verificar que llaman correctamente a dependencias
- Aislas errores de cada capa

---

## 📚 DOCUMENTACIÓN COMPLETA CREADA

He creado **7 archivos de documentación** + **4 archivos de código ejemplo**:

### **1. Guías de Lectura (empezar por aquí)**

| Archivo | Descripción | Tiempo |
|---------|-------------|--------|
| **QUICK_START_TESTING.md** ⭐ | Resumen en 5 minutos - **LEER PRIMERO** | 5 min |
| **README_TESTING.md** | Resumen ejecutivo con mapas visuales | 10 min |
| **GUIA_TESTING_COMPLETA.md** | Guía detallada con todos los conceptos | 30 min |

### **2. Ejemplos y Comparaciones**

| Archivo | Descripción | Tiempo |
|---------|-------------|--------|
| **EJEMPLO_STUB_VS_MOCK.java** | Comparación lado a lado con código real | 15 min |
| **DIAGRAMA_DECISION_TESTING.md** | Árbol de decisión: ¿Qué tipo usar? | 10 min |

### **3. Plantillas para Implementar**

| Archivo | Descripción | Uso |
|---------|-------------|-----|
| **PLANTILLAS_TESTING.md** | Plantillas copy&paste para tus tests | Copy |
| **INDICE_TESTING.md** | Índice de toda la documentación | Ref. |

### **4. Código Ejemplo (listo para ejecutar)**

| Archivo | Tipo | Descripción |
|---------|------|-------------|
| `TestUsuario.java` | Test sin Mocks | Ejemplo de test de clase de dominio |
| `TestCtrlDominioUsuario.java` | Test con Mocks | Ejemplo completo con Mockito |
| `DriverCtrlDominioUsuario.java` | Driver | Menú interactivo para demostración |
| `ICtrlPersistence.java` | Interfaz | Para mockear la persistencia |

---

## 🚀 CÓMO EMPEZAR (3 PASOS)

### **Paso 1: Leer documentación básica (15 min)**

```bash
# Desde tu directorio del proyecto
cd /root/prop/subgrup-prop11.5

# Leer el quick start
cat QUICK_START_TESTING.md

# Leer el resumen ejecutivo
cat README_TESTING.md
```

### **Paso 2: Ejecutar ejemplos (10 min)**

```bash
# Compilar proyecto con Mockito
./gradlew build

# Ejecutar tests automatizados
./gradlew test

# Ver resultados (en navegador)
xdg-open build/reports/tests/test/index.html
```

### **Paso 3: Crear tu primer test (30-60 min)**

```bash
# 1. Abrir plantilla
cat PLANTILLAS_TESTING.md

# 2. Copiar plantilla según tipo:
#    - Test sin mocks → Para clases de dominio
#    - Test con mocks → Para controladores de dominio

# 3. Ver ejemplo como referencia
cat src/test/java/domaincontroller/TestCtrlDominioUsuario.java

# 4. Crear tu archivo y adaptar
```

---

## 🎯 CONCEPTOS CLAVE EXPLICADOS

### **DRIVER (Manual)**
```java
// Programa con main() y menú interactivo
public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    System.out.println("1. Registrar usuario");
    // ...
}
```
**Cuándo:** Para demostrar casos de uso al profesor (1er lliurament)

### **TEST UNITARIO (Automatizado)**
```java
@Test
public void testRegistrarUsuario_CredencialesValidas_CreaUsuario() {
    String resultado = controlador.registrarUsuario("user", "pass", "pass");
    assertEquals("Usuario registrado con éxito: user", resultado);
}
```
**Cuándo:** Para verificar funcionalidad automáticamente

### **STUB (Implementación Falsa)**
```java
// Tu CtrlPersistence actual ES un stub
public class CtrlPersistence {
    private Map<String, Usuario> usuarios = new HashMap<>();
    
    public void guardarUsuario(Usuario u) {
        usuarios.put(u.getNomUsuario(), u); // Finge disco, usa memoria
    }
}
```
**Cuándo:** Para sustituir persistencia real durante desarrollo

### **MOCK (Objeto Verificable)**
```java
@Mock
private CtrlUsuario mockCtrlUsuario;  // Mockito crea objeto falso

@Test
public void test() {
    when(mockCtrlUsuario.existeUsuario("user")).thenReturn(false);
    controlador.registrarUsuario("user", "pass", "pass");
    
    verify(mockCtrlUsuario).existeUsuario("user");  // ✅ Verificar llamada
    verify(mockCtrlUsuario).crearUsuario("user", "pass");
}
```
**Cuándo:** Para verificar que tu código llama correctamente a dependencias

---

## 🗺️ ARQUITECTURA: DÓNDE USAR CADA COSA

```
┌──────────────────────────────────────────────────────────┐
│  CLASES DE DOMINIO (Usuario.java, Encuesta.java)        │
│  ✅ Driver: DriverUsuario.java (ya existe)               │
│  ✅ Test SIN mocks: TestUsuario.java (creado)            │
│  ❌ Mocks: NO necesario                                  │
└──────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────┐
│  CONTROLADORES DE CLASE (CtrlUsuario.java)              │
│  ✅ Driver: DriverCtrlUsuario.java                       │
│  ✅ Test SIN mocks: TestCtrlUsuario.java (por hacer)     │
│  ❌ Mocks: NO necesario                                  │
└──────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────┐
│  CONTROLADORES DE DOMINIO (CtrlDominioUsuario.java)     │
│  ✅ Driver: DriverCtrlDominioUsuario.java (creado)       │
│  ✅ Test CON mocks: TestCtrlDominioUsuario.java (creado) │
│  ⭐ AQUÍ SÍ USAR MOCKS ⭐                                 │
└──────────────────────────────────────────────────────────┘

┌──────────────────────────────────────────────────────────┐
│  PERSISTENCIA (CtrlPersistence.java)                    │
│  ❌ Driver: NO necesario (es infraestructura)            │
│  ❌ Test directo: NO necesario (es un stub)              │
│  ✅ Se mockea cuando lo usan otros tests                 │
└──────────────────────────────────────────────────────────┘
```

---

## 💡 DIFERENCIA CLAVE: STUB vs MOCK

### **Problema con STUB:**
```java
@Test
public void test() {
    CtrlUsuario stub = new CtrlUsuario();  // Objeto REAL
    CtrlPersistence persistence = new CtrlPersistence();  // Objeto REAL
    
    CtrlDominioUsuario ctrl = new CtrlDominioUsuario(stub, persistence);
    ctrl.registrarUsuario("user", "pass", "pass");
    
    // ❌ NO puedes verificar que se llamó a existeUsuario()
    // ❌ NO puedes verificar que se llamó a crearUsuario()
    // ❌ Si hay bug en CtrlUsuario, este test falla
    // ❌ Test ejecuta MUCHO código que no estás testeando
}
```

### **Solución con MOCK:**
```java
@Test
public void test() {
    @Mock CtrlUsuario mock;  // Objeto FALSO
    @Mock ICtrlPersistence mockPersist;  // Objeto FALSO
    
    // CONTROLAS qué devuelve
    when(mock.existeUsuario("user")).thenReturn(false);
    when(mock.crearUsuario("user", "pass")).thenReturn(new Usuario("user", "pass"));
    
    CtrlDominioUsuario ctrl = new CtrlDominioUsuario(mock, mockPersist);
    ctrl.registrarUsuario("user", "pass", "pass");
    
    // ✅ VERIFICAS que se llamaron los métodos correctos
    verify(mock).existeUsuario("user");
    verify(mock).crearUsuario("user", "pass");
    verify(mockPersist).guardarUsuario(any(Usuario.class));
    
    // ✅ Test AISLADO - solo falla si hay error en CtrlDominioUsuario
    // ✅ Test RÁPIDO - no ejecuta código real de dependencias
}
```

---

## 📋 ARCHIVOS CREADOS - RESUMEN

### **Documentación (7 archivos - 90KB total)**
```
QUICK_START_TESTING.md           8.3 KB  ⭐ Empezar aquí
README_TESTING.md               11.0 KB  Resumen visual
GUIA_TESTING_COMPLETA.md        17.0 KB  Referencia completa
EJEMPLO_STUB_VS_MOCK.java       16.0 KB  Comparación código
DIAGRAMA_DECISION_TESTING.md    15.0 KB  Árbol de decisión
PLANTILLAS_TESTING.md           22.0 KB  Plantillas copy&paste
INDICE_TESTING.md                8.2 KB  Índice general
```

### **Código Ejemplo (4 archivos)**
```
src/test/java/domain/TestUsuario.java                      Test sin mocks
src/test/java/domaincontroller/TestCtrlDominioUsuario.java Test con mocks ⭐
driver/DriverCtrlDominioUsuario.java                       Driver interactivo
persistence/ICtrlPersistence.java                          Interfaz para mocks
```

### **Configuración**
```
build.gradle                     Actualizado con Mockito
```

---

## ✅ TODO LIST PARA TI

### **Hoy (30 min - 1 hora)**
- [ ] Leer `QUICK_START_TESTING.md`
- [ ] Leer `README_TESTING.md`
- [ ] Revisar código de `TestCtrlDominioUsuario.java`
- [ ] Ejecutar `./gradlew test`

### **Esta semana (3-4 horas)**
- [ ] Crear `TestEncuesta.java` (sin mocks)
- [ ] Crear `TestCtrlUsuario.java` (sin mocks)
- [ ] Crear `DriverEncuesta.java`
- [ ] Leer `GUIA_TESTING_COMPLETA.md` completa

### **Próxima semana (4-5 horas)**
- [ ] Crear `TestCtrlDominioEncuesta.java` (con mocks)
- [ ] Crear `TestCtrlDominioPregunta.java` (con mocks)
- [ ] Crear `TestCtrlDominioRespuesta.java` (con mocks)
- [ ] Crear drivers para cada controlador

### **Antes del 1er lliurament**
- [ ] Driver principal con menú general
- [ ] Documentar juegos de prueba (ver plantilla)
- [ ] Cobertura de tests >80%
- [ ] Ejecutar todos los tests sin errores
- [ ] JavaDoc completo

---

## 🔧 COMANDOS ÚTILES

```bash
# Compilar con Mockito
./gradlew build

# Ejecutar todos los tests
./gradlew test

# Ejecutar tests con reporte de cobertura
./gradlew test jacocoTestReport

# Ver reporte HTML de tests
xdg-open build/reports/tests/test/index.html

# Ver reporte de cobertura
xdg-open build/reports/jacoco/test/html/index.html

# Ejecutar un driver específico (después de compilar)
java -cp build/classes/java/main:... driver.DriverCtrlDominioUsuario

# Compilar solo
./gradlew compileJava
```

---

## 📞 AYUDA RÁPIDA

| Si necesitas... | Consulta... |
|-----------------|-------------|
| Entender conceptos básicos | `QUICK_START_TESTING.md` |
| Ver arquitectura del proyecto | `README_TESTING.md` |
| Guía paso a paso completa | `GUIA_TESTING_COMPLETA.md` |
| Comparar stub vs mock | `EJEMPLO_STUB_VS_MOCK.java` |
| Decidir qué tipo usar | `DIAGRAMA_DECISION_TESTING.md` |
| Plantilla para copiar | `PLANTILLAS_TESTING.md` |
| Índice de todo | `INDICE_TESTING.md` |
| Ejemplo test sin mocks | `TestUsuario.java` |
| Ejemplo test con mocks | `TestCtrlDominioUsuario.java` |
| Ejemplo driver | `DriverCtrlDominioUsuario.java` |

---

## 🎓 RESUMEN FINAL

### **Tu pregunta original:**
> "Tenemos un lío de conceptos de driver, juego de pruebas, test unitarios, mocks, stubs... Nos gustaría implementar mocks en vez de stubs, pero no sé como ni donde."

### **Respuesta:**

**1. Conceptos aclarados:**
- **Driver**: Programa manual para demostrar (1er lliurament)
- **Test**: Código automatizado para verificar
- **Stub**: Implementación falsa simple (tu CtrlPersistence)
- **Mock**: Objeto falso que verifica llamadas (Mockito)

**2. Dónde usar mocks:**
- ✅ **Controladores de dominio** (CtrlDominioUsuario, CtrlDominioEncuesta, etc.)
- ❌ Clases de dominio (Usuario, Encuesta)
- ❌ Controladores de clase (CtrlUsuario)

**3. Cómo implementar:**
- Ya tienes Mockito configurado en `build.gradle`
- Ya tienes interfaz `ICtrlPersistence` para mockear
- Ya tienes ejemplo completo en `TestCtrlDominioUsuario.java`
- Solo copia la plantilla de `PLANTILLAS_TESTING.md` y adapta

**4. Archivos clave:**
- Leer primero: `QUICK_START_TESTING.md`
- Ejemplo completo: `TestCtrlDominioUsuario.java`
- Plantilla: `PLANTILLAS_TESTING.md`

---

## 🎉 ¡ESTÁS LISTO!

Tienes **toda la documentación**, **ejemplos completos** y **plantillas** necesarias para:
- ✅ Entender diferencia entre driver/test/mock/stub
- ✅ Saber dónde usar cada uno
- ✅ Implementar tests con mocks
- ✅ Crear drivers para demostración
- ✅ Aprobar el 1er lliurament

**Siguiente paso:** Leer `QUICK_START_TESTING.md` (5 minutos)

---

**Última actualización:** 23 de octubre de 2025
**Archivos creados:** 11 (7 documentación + 4 código)
**Total documentación:** ~90 KB
**Nivel de detalle:** Completo - desde conceptos básicos hasta implementación avanzada
