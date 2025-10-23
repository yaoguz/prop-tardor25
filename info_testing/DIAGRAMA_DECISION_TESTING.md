# 🎯 DIAGRAMA DE DECISIÓN: ¿QUÉ TIPO DE TEST USAR?

```
┌─────────────────────────────────────────────────────────────────────┐
│          INICIO: Necesitas probar una clase/método                  │
└─────────────────────────────────────────────────────────────────────┘
                                  │
                                  ▼
              ┌───────────────────────────────────────┐
              │ ¿Es para el 1er LLIURAMENT?           │
              │ (demostración manual al profesor)     │
              └───────────────────────────────────────┘
                   │                            │
                   │ SÍ                         │ NO
                   ▼                            ▼
     ┌──────────────────────────┐    ┌──────────────────────────┐
     │   CREAR DRIVER           │    │   CREAR TEST UNITARIO    │
     │                          │    │   (automatizado)         │
     │  ✅ Menú con Scanner     │    │                          │
     │  ✅ main()               │    │  ✅ Anotación @Test      │
     │  ✅ Interactivo          │    │  ✅ Assertions           │
     │  ✅ try/catch con print  │    │  ✅ Automatizado         │
     │                          │    │                          │
     │  📁 driver/Driver*.java  │    │  📁 src/test/java/       │
     └──────────────────────────┘    └──────────────────────────┘
                                                │
                                                ▼
                        ┌────────────────────────────────────────┐
                        │ ¿La clase tiene DEPENDENCIAS externas? │
                        │ (otros controladores, persistencia)    │
                        └────────────────────────────────────────┘
                            │                              │
                            │ NO                           │ SÍ
                            ▼                              ▼
          ┌─────────────────────────────┐   ┌──────────────────────────────┐
          │  TEST SIN MOCKS             │   │  ¿La clase ORQUESTA capas?   │
          │                             │   │  (controlador de dominio)    │
          │  ✅ Objetos reales          │   └──────────────────────────────┘
          │  ✅ No usa Mockito          │              │              │
          │  ✅ Solo lógica interna     │              │ NO           │ SÍ
          │                             │              │              │
          │  Ejemplo:                   │              ▼              ▼
          │  - Usuario.java             │   ┌──────────────┐   ┌──────────────┐
          │  - Encuesta.java            │   │  TEST SIN    │   │  TEST CON    │
          │  - CtrlUsuario.java         │   │  MOCKS       │   │  MOCKS ✅    │
          │                             │   │              │   │              │
          │  @Test                      │   │  Objetos     │   │  @Mock       │
          │  public void test() {       │   │  reales      │   │  verify()    │
          │    Usuario u = new...       │   │              │   │  when()      │
          │    assertEquals(...)        │   │  Ejemplo:    │   │              │
          │  }                          │   │  CtrlUsuario │   │  Ejemplo:    │
          └─────────────────────────────┘   └──────────────┘   │  CtrlDominio │
                                                               │  CtrlPresent.│
                                                               └──────────────┘
```

---

## 📊 TABLA DE DECISIÓN RÁPIDA

| Pregunta | Respuesta | Acción |
|----------|-----------|--------|
| ¿Es para demostración manual? | SÍ | **DRIVER** con menú |
| ¿Es para demostración manual? | NO | **TEST** automatizado |
| ¿La clase tiene dependencias? | NO | **TEST SIN MOCKS** |
| ¿La clase tiene dependencias? | SÍ | → Siguiente pregunta |
| ¿Orquesta múltiples capas? | SÍ | **TEST CON MOCKS** ✅ |
| ¿Orquesta múltiples capas? | NO | **TEST SIN MOCKS** |

---

## 🗺️ MAPA DE TU PROYECTO

```
┌─────────────────────────────────────────────────────────────────┐
│                         TU PROYECTO                             │
└─────────────────────────────────────────────────────────────────┘

domain/
├── Usuario.java                    → Driver ✅ + Test sin Mocks ✅
├── Encuesta.java                   → Driver ✅ + Test sin Mocks ❌
├── Pregunta.java                   → Driver ✅ + Test sin Mocks ❌
├── PreguntaLibre.java             → Driver ❌ + Test sin Mocks ❌
├── PreguntaNumerica.java          → Driver ❌ + Test sin Mocks ❌
├── PreguntaOpcionUnica.java       → Driver ❌ + Test sin Mocks ❌
├── PreguntaOpcionMultiple.java    → Driver ❌ + Test sin Mocks ❌
├── Respuesta.java                 → Driver ✅ + Test sin Mocks ❌
├── RespuestaLibre.java            → Driver ❌ + Test sin Mocks ❌
├── RespuestaNumerica.java         → Driver ❌ + Test sin Mocks ❌
├── RespuestaOpcionUnica.java      → Driver ❌ + Test sin Mocks ❌
├── RespuestaOpcionMultiple.java   → Driver ❌ + Test sin Mocks ❌
└── Perfil.java                    → Driver ❌ + Test sin Mocks ❌

classcontroller/
├── CtrlUsuario.java               → Driver ✅ + Test sin Mocks ❌
├── CtrlEncuesta.java              → Driver ✅ + Test sin Mocks ❌
├── CtrlPregunta.java              → Driver ❌ + Test sin Mocks ❌
└── CtrlRespuesta.java             → Driver ❌ + Test sin Mocks ❌

domaincontroller/
├── CtrlDominio.java               → Driver ❌ + Test con Mocks ❌
├── CtrlDominioUsuario.java        → Driver ✅ + Test con Mocks ✅
├── CtrlDominioEncuesta.java       → Driver ❌ + Test con Mocks ❌
├── CtrlDominioPregunta.java       → Driver ❌ + Test con Mocks ❌
├── CtrlDominioRespuesta.java      → Driver ❌ + Test con Mocks ❌
└── CtrlDominioPerfil.java         → Driver ❌ + Test con Mocks ❌

presentation/
├── CtrlPresentacion.java          → Driver ❌ + Test con Mocks ❌
└── VistaPrincipal.java            → Driver ❌ + Test ❌ (GUI)

persistence/
└── CtrlPersistence.java           → Driver ❌ + Test ❌ (es STUB)

LEYENDA:
✅ Ya existe o ya creado
❌ Por hacer
```

---

## 🎯 EJEMPLO COMPLETO DE FLUJO

### **Caso: Quiero probar CtrlDominioEncuesta**

```
1. ¿Es para demostración manual?
   → NO (quiero test automatizado)
   
2. ¿Tiene dependencias externas?
   → SÍ (usa CtrlEncuesta, CtrlPregunta, CtrlRespuesta, CtrlPersistence)
   
3. ¿Orquesta múltiples capas?
   → SÍ (coordina capa de datos + persistencia)
   
4. CONCLUSIÓN: Usar TEST CON MOCKS ✅

5. IMPLEMENTACIÓN:
   a) Crear archivo: src/test/java/domaincontroller/TestCtrlDominioEncuesta.java
   b) Agregar mocks:
      @Mock CtrlEncuesta mockCtrlEncuesta;
      @Mock CtrlPregunta mockCtrlPregunta;
      @Mock CtrlRespuesta mockCtrlRespuesta;
      @Mock ICtrlPersistence mockPersistence;
   
   c) Configurar mocks:
      when(mockCtrlEncuesta.existe(1)).thenReturn(true);
   
   d) Verificar llamadas:
      verify(mockCtrlEncuesta).crearEncuesta(...);
      verify(mockPersistence).guardarEncuesta(...);
```

### **Caso: Quiero probar clase Encuesta**

```
1. ¿Es para demostración manual?
   → NO (quiero test automatizado)
   
2. ¿Tiene dependencias externas?
   → NO (solo atributos internos: String titulo, List<Integer> preguntas)
   
3. CONCLUSIÓN: Usar TEST SIN MOCKS ✅

4. IMPLEMENTACIÓN:
   a) Crear archivo: src/test/java/domain/TestEncuesta.java
   b) Usar objetos reales:
      Encuesta e = new Encuesta(1, "testUser", "Título");
   
   c) Probar métodos:
      e.guardarIdPregunta(1);
      assertEquals(1, e.getIdPreguntas().size());
```

### **Caso: Quiero demostrar registro de usuario al profesor**

```
1. ¿Es para demostración manual?
   → SÍ (1er lliurament, demostración)
   
2. CONCLUSIÓN: Usar DRIVER ✅

3. IMPLEMENTACIÓN:
   a) Crear archivo: driver/DriverCtrlDominioUsuario.java (YA EXISTE ✅)
   b) Menú interactivo:
      1. Registrar usuario
      2. Iniciar sesión
      3. Modificar contraseña
      0. Salir
   
   c) Usar Scanner para leer entradas
   d) Mostrar resultados con System.out.println
   e) Manejo de excepciones con try/catch y mensajes claros
```

---

## 🔍 IDENTIFICAR DEPENDENCIAS

### **¿Cómo saber si una clase tiene dependencias externas?**

```java
// SIN DEPENDENCIAS (usar objetos reales)
public class Usuario {
    private String nomUsuario;      // ✅ Tipo primitivo
    private String contrasena;      // ✅ String
    private List<Integer> ids;      // ✅ Colección de primitivos
    
    // Solo lógica interna
}

// CON DEPENDENCIAS (considerar mocks)
public class CtrlDominioUsuario {
    private CtrlUsuario ctrlUsuario;          // ❌ Dependencia externa
    private CtrlPersistence ctrlPersistence;  // ❌ Dependencia externa
    
    public String registrarUsuario(...) {
        // Llama a métodos de ctrlUsuario
        // Llama a métodos de ctrlPersistence
    }
}
```

### **Regla práctica:**

Si el constructor tiene **parámetros de otras clases** del proyecto:
→ Tiene dependencias
→ Considera usar mocks (si orquesta capas)

---

## 📈 NIVEL DE COMPLEJIDAD

```
┌─────────────────────────────────────────────────────────┐
│  NIVEL 1: FÁCIL ⭐                                      │
│  - Tests de clases de dominio (Usuario, Encuesta)      │
│  - Drivers simples con menú                            │
│  - Sin mocks, sin dependencias                         │
│                                                         │
│  Tiempo estimado: 30 min por clase                     │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│  NIVEL 2: MEDIO ⭐⭐                                     │
│  - Tests de controladores de clase                     │
│  - Drivers con múltiples opciones                      │
│  - Usa objetos de dominio reales                       │
│                                                         │
│  Tiempo estimado: 1 hora por controlador               │
└─────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────┐
│  NIVEL 3: AVANZADO ⭐⭐⭐                                │
│  - Tests de controladores de dominio CON MOCKS         │
│  - Configurar when(), verify()                         │
│  - Casos edge, excepciones                             │
│                                                         │
│  Tiempo estimado: 2 horas por controlador              │
│  → YA TIENES UN EJEMPLO COMPLETO ✅                     │
└─────────────────────────────────────────────────────────┘
```

---

## ✅ PLAN DE ACCIÓN RECOMENDADO

### **Semana 1: Nivel Fácil**
```
□ Tests de Usuario.java (SIN mocks)
□ Tests de Encuesta.java (SIN mocks)
□ Driver de Usuario.java
□ Driver de Encuesta.java
```

### **Semana 2: Nivel Medio**
```
□ Tests de CtrlUsuario.java (SIN mocks)
□ Tests de CtrlEncuesta.java (SIN mocks)
□ Driver de CtrlUsuario.java
□ Driver de CtrlEncuesta.java
```

### **Semana 3: Nivel Avanzado**
```
□ Tests de CtrlDominioUsuario.java (CON mocks) ← YA HECHO ✅
□ Tests de CtrlDominioEncuesta.java (CON mocks)
□ Driver de CtrlDominioUsuario.java ← YA HECHO ✅
□ Driver de CtrlDominioEncuesta.java
```

### **Semana 4: Integración y Documentación**
```
□ Driver principal (menú general)
□ Documentar juegos de prueba
□ Ejecutar todos los tests
□ Verificar cobertura >80%
```

---

## 🎯 RESUMEN ULTRA-RÁPIDO

| Tipo | Cuándo | Cómo | Ejemplo |
|------|--------|------|---------|
| **Driver** | Demo manual | Scanner + menú | DriverCtrlDominioUsuario.java ✅ |
| **Test sin Mock** | Clase pura | @Test + assertEquals | TestUsuario.java ✅ |
| **Test con Mock** | Orquesta capas | @Mock + verify | TestCtrlDominioUsuario.java ✅ |

**TU PROYECTO YA TIENE EJEMPLOS DE LOS 3 TIPOS** ✅

Solo copia las plantillas y adapta para otras clases.
