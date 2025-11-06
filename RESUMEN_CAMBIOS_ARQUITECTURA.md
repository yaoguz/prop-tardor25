# 🔄 RESUMEN DE CAMBIOS EN LA ARQUITECTURA - ACTUALIZADO (v3.0)

## 📋 ¿Qué hemos cambiado y por qué?

### 1. **Problema Original: Doble capa de controladores**

**Antes teníamos:**
```
CtrlDominio
    ↓
CtrlDominioRespuesta → CtrlRespuesta (Singleton con HashMap)
                            ↓
                       persistence (guardar después de cada operación)
```

**Problemas:**
- ❌ CtrlDominioRespuesta solo delegaba a CtrlRespuesta (capa innecesaria)
- ❌ Llamadas a persistencia después de cada operación (ineficiente)
- ❌ Violaba principio de responsabilidad única
- ❌ Dependencias almacenadas (alto acoplamiento)

### 2. **Solución: Fusionar capas + Patrón Correcto**

**Ahora tenemos:**
```
PRESENTACIÓN (CtrlPresentacion)
    ↓
CtrlDominio (Facade MINIMALISTA - solo delega)
    ↓
CtrlDominioUsuario (TreeMap<String, Usuario> + lógica)
CtrlDominioEncuesta (TreeMap<Integer, Encuesta> + lógica)
CtrlDominioPregunta (TreeMap<Integer, Pregunta> + lógica)
CtrlDominioRespuesta (TreeMap<Integer, Respuesta> + lógica)
CtrlDominioPerfil (solo algoritmos, no datos)
    ↓
GestorPersistencia (STUB - solo cargar/guardar al inicio/cierre)
```

**Ventajas:**
- ✅ Objetos en memoria durante toda la sesión (más rápido)
- ✅ Persistencia solo al abrir/cerrar aplicación (arquitectura correcta según PDF)
- ✅ Cada CtrlDominioXXX gestiona sus datos directamente
- ✅ CtrlDominio es una Facade pura (solo delega, no tiene lógica)

---

## 🔧 CAMBIOS TÉCNICOS PRINCIPALES

### ✅ Cambio 1: Factory Pattern en Pregunta (Principio Abierto/Cerrado)

**Antes (MALO):**
```java
// En CtrlDominioRespuesta
if (p instanceof PreguntaLibre) {
    nuevaRespuesta = new RespuestaLibre(...);
} else if (p instanceof PreguntaNumerica) {
    nuevaRespuesta = new RespuestaNumerica(...);
}
// Si añades nuevo tipo, hay que modificar este código ❌
```

**Ahora (BUENO):**
```java
// En CtrlDominioRespuesta
Respuesta nuevaRespuesta = p.crearRespuesta(idR, nomUsuario, respuesta);
// Polimórfico! Cada pregunta sabe crear su respuesta ✅

// En cada subclase de Pregunta
public class PreguntaLibre extends Pregunta {
    @Override
    public <T> Respuesta crearRespuesta(int idR, String nomU, T resp) {
        return new RespuestaLibre(idR, nomU, getIdPregunta(), (String) resp);
    }
}
```

**Por qué es mejor:**
- ✅ Si añades `PreguntaBooleana`, solo implementas `crearRespuesta()` en ella
- ✅ No hay que tocar `CtrlDominioRespuesta` (abierto/cerrado)
- ✅ Más elegante y mantenible

---

### ✅ Cambio 2: Persistencia solo al abrir/cerrar aplicación

**Antes (MALO):**
```java
public int darRespuesta(...) {
    // Crear respuesta
    respuestas.put(idR, nuevaRespuesta);
    
    // GUARDAR EN PERSISTENCIA INMEDIATAMENTE ❌
    gestorPersistencia.guardarRespuesta(nuevaRespuesta);
    
    return idR;
}
```

**Ahora (BUENO):**
```java
// AL INICIAR LA APLICACIÓN (solo una vez)
public void inicializarCtrlDominio() {
    // 1. Crear controladores
    ctrlDominioUsuario = new CtrlDominioUsuario(gestorPersistencia);
    // ...
    
    // 2. CARGAR TODO desde persistencia
    ctrlDominioUsuario.cargarUsuarios(gestorPersistencia.cargarTodosUsuarios());
    ctrlDominioEncuesta.cargarEncuestas(gestorPersistencia.cargarTodasEncuestas());
    ctrlDominioPregunta.cargarPreguntas(gestorPersistencia.cargarTodasPreguntas());
    ctrlDominioRespuesta.cargarRespuestas(gestorPersistencia.cargarTodasRespuestas());
}

// DURANTE LA SESIÓN - NO hay persistencia
public int darRespuesta(...) {
    respuestas.put(idR, nuevaRespuesta);
    // NO se guarda en persistencia aquí ✅
    return idR;
}

// AL CERRAR LA APLICACIÓN (solo una vez)
public void cerrarSesion() {
    // GUARDAR TODO de golpe
    gestorPersistencia.guardarTodosUsuarios(ctrlDominioUsuario.getAllUsuarios());
    gestorPersistencia.guardarTodasEncuestas(ctrlDominioEncuesta.getAllEncuestas());
    gestorPersistencia.guardarTodasPreguntas(ctrlDominioPregunta.getAllPreguntas());
    gestorPersistencia.guardarTodasRespuestas(ctrlDominioRespuesta.getAllRespuestas());
}
```

**Por qué es mejor:**
- ✅ Más eficiente (dos operaciones I/O en vez de cientos)
- ✅ Sigue la arquitectura del PDF (objetos en memoria, persistencia al inicio/cierre)
- ✅ Más simple de implementar y testear

---

### ✅ Cambio 3: **INYECCIÓN DE DEPENDENCIAS POR PARÁMETROS** (Nueva arquitectura mejorada)

**EVOLUCIÓN ARQUITECTÓNICA:**

**Versión 1 (inicial):**
```java
// Dependencias en constructor - almacenadas
public class CtrlDominioRespuesta {
    private CtrlDominioUsuario ctrlDominioUsuario;  // ❌ Almacenada
    
    public CtrlDominioRespuesta(CtrlDominioUsuario u, ...) {
        this.ctrlDominioUsuario = u;  // Se guarda
    }
    
    public int darRespuesta(...) {
        Usuario u = ctrlDominioUsuario.getUsuario(nomU);  // Usa referencia almacenada
    }
}
```
**Problemas:**
- Alto acoplamiento (referencias almacenadas)
- Difícil de testear (mock complicado)
- Violación de Dependency Inversion Principle

**Versión 2 (ACTUAL - MEJORADA):**
```java
// Inyección por parámetros - NO almacenadas
public class CtrlDominioRespuesta {
    // NO hay referencias almacenadas ✅
    
    public CtrlDominioRespuesta() {  // Constructor sin dependencias
        this.respuestas = new TreeMap<>();
        this.nextId = 1;
    }
    
    public int darRespuesta(..., 
                             CtrlDominioUsuario ctrlDominioUsuario,  // Inyectado como parámetro
                             CtrlDominioPregunta ctrlDominioPregunta) {
        Usuario u = ctrlDominioUsuario.getUsuario(nomU);  // Usa parámetro
    }
}
```
**Ventajas:**
- ✅ Bajo acoplamiento (solo cuando se necesita)
- ✅ Más fácil de testear (inyectar mocks por parámetro)
- ✅ Sigue DIP (Dependency Inversion Principle)
- ✅ Más flexible y mantenible

**Aplicación en CtrlDominio (Facade):**
```java
public class CtrlDominio {
    // CtrlDominio coordina e inyecta dependencias cuando es necesario
    
    public int darRespuesta(String nomU, int idP, T resp) {
        return ctrlDominioRespuesta.darRespuesta(nomU, idP, resp,
                                                   ctrlDominioUsuario,  // Inyecta
                                                   ctrlDominioPregunta); // Inyecta
    }
    
    public void eliminarEncuesta(int idE) {
        return ctrlDominioEncuesta.eliminarEncuesta(idE,
                                                     ctrlDominioPregunta,  // Inyecta
                                                     ctrlDominioUsuario);  // Inyecta
    }
}
```

---

### ✅ Cambio 4: Fusión completa de CtrlDominioXXX con Singleton CtrlXXX

**Antes (doble capa innecesaria):**
```
CtrlDominioUsuario
    ↓ delega todo
CtrlUsuario (Singleton)
    ↓ gestiona HashMap<String, Usuario>
```

**Ahora (capa única optimizada):**
```
CtrlDominioUsuario
    ↓ gestiona directamente TreeMap<String, Usuario>
```

**Cambios específicos:**

**CtrlDominioUsuario:**
```java
// ANTES:
public class CtrlDominioUsuario {
    private CtrlUsuario ctrlUsuario;  // Delegación
    private GestorPersistencia gestorPersistencia;
    
    public String registrarUsuario(...) {
        String nom = ctrlUsuario.crearUsuario(...);  // Delega
        gestorPersistencia.guardarUsuario(...);  // Persistencia inmediata ❌
        return nom;
    }
}

// AHORA:
public class CtrlDominioUsuario {
    private TreeMap<String, Usuario> usuarios;  // Gestión directa
    private String usuarioActivo;  // Gestión de sesión
    
    public String registrarUsuario(...) {
        Usuario u = new Usuario(nomU, contrasena);  // Crea directamente
        usuarios.put(nomU, u);  // TreeMap management
        // NO persistencia! ✅
        return u.getNomUsuario();
    }
}
```

**CtrlDominioEncuesta:**
```java
// ANTES:
public class CtrlDominioEncuesta {
    private CtrlEncuesta ctrlEncuesta;  // Delegación
    private GestorPersistencia gestorPersistencia;
    
    public int crearEncuesta(...) {
        int id = ctrlEncuesta.crearEncuesta(...);  // Delega
        gestorPersistencia.guardarEncuesta(...);  // Persistencia inmediata ❌
        return id;
    }
}

// AHORA:
public class CtrlDominioEncuesta {
    private TreeMap<Integer, Encuesta> encuestas;  // Gestión directa
    private int nextId;
    
    public int crearEncuesta(..., CtrlDominioUsuario ctrlDominioUsuario) {
        int idE = nextId++;
        Encuesta e = new Encuesta(idE, titulo, nomUsuario);  // Crea directamente
        encuestas.put(idE, e);  // TreeMap management
        // NO persistencia! ✅
        return idE;
    }
}
```

---

### ✅ Cambio 5: CtrlDominio como Facade pura (solo delegación)

**Estructura de CtrlDominio:**
```java
public class CtrlDominio {
    // SOLO referencias a controladores (no datos propios)
    private CtrlDominioUsuario ctrlDominioUsuario;
    private CtrlDominioEncuesta ctrlDominioEncuesta;
    private CtrlDominioPregunta ctrlDominioPregunta;
    private CtrlDominioRespuesta ctrlDominioRespuesta;
    
    // DELEGACIÓN PURA (sin lógica de negocio)
    public int darRespuesta(String nomU, int idP, T resp) {
        return ctrlDominioRespuesta.darRespuesta(nomU, idP, resp); // solo delega
    }
    
    public int crearEncuesta(String nomU, String titulo) {
        return ctrlDominioEncuesta.crearEncuesta(nomU, titulo); // solo delega
    }
    
    // COORDINACIÓN (múltiples controladores, pero sin lógica compleja)
    public void cerrarSesion() {
        // Guarda TODO (coordina múltiples controladores)
        gestorPersistencia.guardarTodosUsuarios(ctrlDominioUsuario.getAllUsuarios());
        gestorPersistencia.guardarTodasEncuestas(ctrlDominioEncuesta.getAllEncuestas());
        // ...
    }
}
```

---

### ✅ Cambio 4: CtrlDominioRespuesta gestiona TreeMap directamente

**Estructura de CtrlDominioRespuesta (y similar para otros):**
```java
public class CtrlDominioRespuesta {
    // DATOS EN MEMORIA (no delegado a otro controlador)
    private TreeMap<Integer, Respuesta> respuestas;
    private int nextId;
    
    // Referencias a otros controladores de dominio
    private CtrlDominioUsuario ctrlDominioUsuario;
    private CtrlDominioPregunta ctrlDominioPregunta;
    private GestorPersistencia gestorPersistencia;  // SOLO para inicio/cierre
    
    // Métodos CRUD (gestionan TreeMap directamente)
    public int darRespuesta(...) { 
        int idR = nextId++;
        Respuesta nuevaRespuesta = p.crearRespuesta(idR, nomUsuario, respuesta);
        respuestas.put(Integer.valueOf(idR), nuevaRespuesta);
        // NO persistencia!
        return idR;
    }
    
    // Métodos para persistencia (SOLO llamados al abrir/cerrar)
    public TreeMap<Integer, Respuesta> getAllRespuestas() { 
        return new TreeMap<>(respuestas); 
    }
    
    public void cargarRespuestas(TreeMap<Integer, Respuesta> datos) {
        this.respuestas = datos;
        if (!respuestas.isEmpty()) {
            this.nextId = respuestas.lastKey() + 1;
        }
    }
}
```

---

### ✅ Cambio 5: Distribución correcta de métodos según Aggregate Root

**Regla: "¿De qué entidad es la operación?" → Va en su CtrlDominioXXX**

```java
// CtrlDominioEncuesta - operaciones SOBRE encuestas
getRespuestasEncuesta(int idEncuesta) {
    // "Todas las respuestas DE esta encuesta"
    // Aggregate root: Encuesta
}

// CtrlDominioPregunta - operaciones SOBRE preguntas
getRespuestasPregunta(int idPregunta) {
    // "Todas las respuestas DE esta pregunta"
    // Aggregate root: Pregunta
}

// CtrlDominioUsuario - operaciones SOBRE usuarios
getEncuestasRespondidas(String nomUsuario) {
    // "Qué encuestas respondió este usuario"
    // Aggregate root: Usuario
}
```

---

## 📝 LO QUE HEMOS HECHO (COMPLETADO ✅)

### **✅ COMPLETADO: CtrlDominioRespuesta**
- [x] Añadido `TreeMap<Integer, Respuesta> respuestas`
- [x] Eliminadas referencias a CtrlRespuesta (Singleton viejo)
- [x] Añadidos métodos `existeRespuesta()` y `getRespuesta()` (públicos)
- [x] Refactorizados todos los métodos CRUD para gestionar TreeMap directamente
- [x] **Eliminadas todas las llamadas a persistencia** de los métodos CRUD
- [x] Añadidos `getAllRespuestas()` y `cargarRespuestas()` para persistencia
- [x] **MIGRADO A INYECCIÓN POR PARÁMETROS** (constructor sin dependencias)
- [x] Implementado Factory Pattern (delegación a `Pregunta.crearRespuesta()`)

### **✅ COMPLETADO: CtrlDominioUsuario** ⭐ NUEVO
- [x] Añadido `TreeMap<String, Usuario> usuarios`
- [x] Añadido `String usuarioActivo` para gestión de sesión
- [x] **FUSIONADO con CtrlUsuario (eliminada capa intermedia)**
- [x] Eliminadas todas las referencias a CtrlUsuario (Singleton viejo)
- [x] Eliminadas todas las referencias a GestorPersistencia (excepto persistencia inicio/cierre)
- [x] Añadidos métodos helper: `existeUsuario()`, `getUsuario()`
- [x] Refactorizados todos los métodos CRUD para gestionar TreeMap directamente:
  - `registrarUsuario()` - crea Usuario directamente
  - `iniciarSesion()` - gestiona usuarioActivo directamente
  - `modificarContrasena()` - actualiza en memoria
  - `getEncuestasRespondidas()` - con **inyección por parámetros**
- [x] **Eliminadas todas las llamadas a persistencia** de los métodos CRUD
- [x] Añadidos `getAllUsuarios()` y `cargarUsuarios()` para persistencia

### **✅ COMPLETADO: CtrlDominioEncuesta** ⭐ NUEVO
- [x] Añadido `TreeMap<Integer, Encuesta> encuestas`
- [x] Añadido `int nextId` para IDs autoincrementales
- [x] **FUSIONADO con CtrlEncuesta (eliminada capa intermedia)**
- [x] Eliminadas todas las referencias a CtrlEncuesta (Singleton viejo)
- [x] Eliminadas todas las referencias a GestorPersistencia (excepto persistencia inicio/cierre)
- [x] Eliminadas referencias innecesarias a CtrlUsuario, CtrlPregunta, CtrlRespuesta
- [x] Añadidos métodos helper: `existeEncuesta()`, `getEncuesta()`
- [x] Refactorizados todos los métodos CRUD para gestionar TreeMap directamente:
  - `crearEncuesta()` - con **inyección por parámetros** (CtrlDominioUsuario)
  - `getPreguntasEncuesta()` - sin dependencias externas
  - `modificarTituloEncuesta()` - actualiza en memoria
  - `eliminarEncuesta()` - con **inyección por parámetros** (CtrlDominioPregunta, CtrlDominioUsuario)
  - `getRespuestasEncuesta()` - con **inyección por parámetros** (CtrlDominioRespuesta, CtrlDominioPregunta)
  - `importarEncuesta()` - con **inyección por parámetros** (CtrlDominioUsuario, CtrlDominioPregunta)
- [x] **Eliminadas todas las llamadas a persistencia** de los métodos CRUD
- [x] Añadidos `getAllEncuestas()` y `cargarEncuestas()` para persistencia

### **✅ COMPLETADO: CtrlDominio**
- [x] Implementado `inicializarCtrlDominio()` que carga datos al inicio
- [x] Implementado `cerrarSesion()` que guarda datos al cierre
- [x] Actualizado para delegar a CtrlDominioXXX (sin referencias a Singleton viejos)
- [x] Eliminada toda lógica de negocio (solo delegación pura)
- [x] **ACTUALIZADO para inyección correcta de dependencias por parámetros:**
  - `darRespuesta()` - pasa ctrlDominioUsuario + ctrlDominioPregunta
  - `eliminarRespuesta()` - pasa ctrlDominioUsuario + ctrlDominioPregunta
  - `importarRespuestas()` - pasa ctrlDominioUsuario + ctrlDominioEncuesta + ctrlDominioPregunta
  - `crearEncuesta()` - pasa ctrlDominioUsuario
  - `importarEncuesta()` - pasa ctrlDominioUsuario + ctrlDominioPregunta
  - `eliminarEncuesta()` - pasa ctrlDominioPregunta + ctrlDominioUsuario
  - `getRespuestasEncuesta()` - pasa ctrlDominioRespuesta + ctrlDominioPregunta
  - `getEncuestasRespondidas()` - pasa ctrlDominioRespuesta + ctrlDominioPregunta
- [x] **Constructores actualizados** (sin parámetros para controladores fusionados)

### **✅ COMPLETADO: CtrlDominioPregunta**
- [x] Método `getRespuestasPregunta()` implementado
- [x] Lógica de negocio de preguntas implementada

### **✅ COMPLETADO: Pregunta (Factory Pattern)**
- [x] Método abstracto `crearRespuesta()` añadido en `Pregunta`
- [x] Implementado en `PreguntaLibre`
- [x] Implementado en `PreguntaNumerica`
- [x] Implementado en `PreguntaOpcionUnica`
- [x] Implementado en `PreguntaOpcionMultiple`
- [x] Métodos `guardarIdRespuesta()` y `eliminarIdRespuesta()` actualizados

---

## ⚠️ LO QUE FALTA POR HACER (PENDIENTE)

### **Tarea 1: Fusionar CtrlDominioPregunta** (Según criterio del usuario)
- [ ] Añadir `TreeMap<Integer, Pregunta> preguntas`
- [ ] Eliminar referencias a CtrlPregunta (Singleton viejo)
- [ ] Implementar CRUD gestionando TreeMap directamente
- [ ] **NO llamar a persistencia en CRUD**
- [ ] Añadir `getAllPreguntas()` y `cargarPreguntas()`
- [ ] Añadir `existePregunta()` y `getPregunta()`
- [ ] Actualizar para inyección por parámetros (sin dependencias almacenadas)

### **Tarea 2: Actualizar GestorPersistencia**
- [ ] Implementar `getInstance()` (corregir typo `getInstace()`)
- [ ] Implementar `cargarTodosUsuarios()` → `TreeMap<String, Usuario>`
- [ ] Implementar `cargarTodasEncuestas()` → `TreeMap<Integer, Encuesta>`
- [ ] Implementar `cargarTodasPreguntas()` → `TreeMap<Integer, Pregunta>`
- [ ] Implementar `cargarTodasRespuestas()` → `TreeMap<Integer, Respuesta>`
- [ ] Implementar `guardarTodosUsuarios(TreeMap<String, Usuario>)`
- [ ] Implementar `guardarTodasEncuestas(TreeMap<Integer, Encuesta>)`
- [ ] Implementar `guardarTodasPreguntas(TreeMap<Integer, Pregunta>)`
- [ ] Implementar `guardarTodasRespuestas(TreeMap<Integer, Respuesta>)`

### **Tarea 3: Actualizar clases de dominio**
- [ ] `Encuesta`: Verificar que `getIdCreador()` y `guardarPregunta()` funcionan correctamente
- [ ] `Usuario`: Verificar que `getIdRespuestas()` devuelve `ArrayList` correctamente
- [ ] Verificar que todas las clases tienen getters/setters necesarios

### **Tarea 4: Limpieza final**
- [ ] Eliminar CtrlUsuario.java (Singleton viejo ya fusionado)
- [ ] Eliminar CtrlEncuesta.java (Singleton viejo ya fusionado)
- [ ] Eliminar CtrlRespuesta.java (si existe - Singleton viejo ya fusionado)
- [ ] Actualizar todos los imports que referencien clases eliminadas
- [ ] Verificar que no hay errores de compilación (excepto los esperados de CtrlDominioPregunta)
- [ ] Ejecutar tests

---

## ✅ CHECKLIST DE VERIFICACIÓN

### Para cada CtrlDominioXXX:
- [x] **CtrlDominioRespuesta**: Añadir `TreeMap<Key, Object> datos` como atributo ✅
- [x] **CtrlDominioUsuario**: Añadir `TreeMap<String, Usuario> usuarios` ✅
- [x] **CtrlDominioEncuesta**: Añadir `TreeMap<Integer, Encuesta> encuestas` ✅
- [ ] **CtrlDominioPregunta**: Añadir `TreeMap<Integer, Pregunta> preguntas` ⚠️

- [x] **CtrlDominioRespuesta**: Eliminar referencias a Singleton viejo ✅
- [x] **CtrlDominioUsuario**: Eliminar referencias a CtrlUsuario ✅
- [x] **CtrlDominioEncuesta**: Eliminar referencias a CtrlEncuesta ✅
- [ ] **CtrlDominioPregunta**: Eliminar referencias a CtrlPregunta ⚠️

- [x] **CtrlDominioRespuesta**: Métodos CRUD gestionan TreeMap directamente ✅
- [x] **CtrlDominioRespuesta**: NO llamar a persistencia en CRUD ✅
- [x] **CtrlDominioRespuesta**: Añadir `getAllRespuestas()` y `cargarRespuestas()` ✅
- [x] **CtrlDominioRespuesta**: MIGRADO A INYECCIÓN POR PARÁMETROS ✅

- [x] **CtrlDominioUsuario**: Métodos CRUD gestionan TreeMap directamente ✅
- [x] **CtrlDominioUsuario**: NO llamar a persistencia en CRUD ✅
- [x] **CtrlDominioUsuario**: Añadir `getAllUsuarios()` y `cargarUsuarios()` ✅
- [x] **CtrlDominioUsuario**: Inyección por parámetros en métodos que cruzan controladores ✅

- [x] **CtrlDominioEncuesta**: Métodos CRUD gestionan TreeMap directamente ✅
- [x] **CtrlDominioEncuesta**: NO llamar a persistencia en CRUD ✅
- [x] **CtrlDominioEncuesta**: Añadir `getAllEncuestas()` y `cargarEncuestas()` ✅
- [x] **CtrlDominioEncuesta**: Inyección por parámetros en métodos que cruzan controladores ✅

### Para GestorPersistencia:
- [ ] Implementar `getInstance()` (corregir typo) ⚠️
- [ ] Implementar `guardarTodosXXX()` para cada entidad ⚠️
- [ ] Implementar `cargarTodosXXX()` para cada entidad ⚠️

### Para CtrlDominio:
- [x] Implementar `inicializarCtrlDominio()` que carga todo ✅
- [x] Implementar `cerrarSesion()` que guarda todo ✅
- [x] Solo delegación pura (sin lógica de negocio) ✅
- [x] Inyección por parámetros en todos los métodos que cruzan controladores ✅

### Patrón de Inyección por Parámetros:
- [x] **CtrlDominioRespuesta**: Constructor sin dependencias ✅
- [x] **CtrlDominioUsuario**: Constructor sin dependencias ✅
- [x] **CtrlDominioEncuesta**: Constructor sin dependencias ✅
- [x] **CtrlDominio**: Pasa dependencias como parámetros en métodos cross-controller ✅

---

## 🎯 CONCEPTO CLAVE A RECORDAR

**"Los objetos viven en memoria (TreeMap) durante toda la aplicación"**

```
INICIO APLICACIÓN
    ↓
inicializarCtrlDominio()
    → GestorPersistencia.cargarTodosXXX() 
    → CtrlDominioXXX.cargarXXX(datos)
    → TreeMaps poblados en memoria
    ↓
OPERACIONES CRUD (crear, modificar, eliminar)
    → Todo se hace en memoria (TreeMaps)
    → NO se llama a persistencia
    ↓
CERRAR APLICACIÓN
    ↓
cerrarSesion()
    → CtrlDominioXXX.getAllXXX()
    → GestorPersistencia.guardarTodosXXX(datos)
```

---

## � PATRÓN: INYECCIÓN POR PARÁMETROS vs CONSTRUCTOR

### ¿Por qué cambiamos de constructor a parámetros?

**EVOLUCIÓN:**

**❌ Versión Inicial (Constructor Injection - Stored References):**
```java
public class CtrlDominioRespuesta {
    private CtrlDominioUsuario ctrlDominioUsuario;  // Almacenado
    private CtrlDominioPregunta ctrlDominioPregunta;  // Almacenado
    
    public CtrlDominioRespuesta(CtrlDominioUsuario u, CtrlDominioPregunta p) {
        this.ctrlDominioUsuario = u;
        this.ctrlDominioPregunta = p;
    }
    
    public int darRespuesta(...) {
        Usuario u = ctrlDominioUsuario.getUsuario(nomU);  // Usa almacenado
        Pregunta p = ctrlDominioPregunta.getPregunta(idP);  // Usa almacenado
    }
}
```
**Problemas:**
- Alto acoplamiento (dependencias permanentes)
- Difícil de testear (mocks complicados)
- Viola DIP (Dependency Inversion Principle)
- Dificulta reutilización

**✅ Versión Actual (Parameter Injection - No Stored References):**
```java
public class CtrlDominioRespuesta {
    // NO dependencias almacenadas ✅
    
    public CtrlDominioRespuesta() {  // Constructor limpio
        this.respuestas = new TreeMap<>();
        this.nextId = 1;
    }
    
    public int darRespuesta(...,
                             CtrlDominioUsuario ctrlDominioUsuario,  // Inyectado
                             CtrlDominioPregunta ctrlDominioPregunta) {  // Inyectado
        Usuario u = ctrlDominioUsuario.getUsuario(nomU);  // Usa parámetro
        Pregunta p = ctrlDominioPregunta.getPregunta(idP);  // Usa parámetro
    }
}
```
**Ventajas:**
- ✅ Bajo acoplamiento (solo cuando se necesita)
- ✅ Fácil de testear (mocks por parámetro)
- ✅ Respeta DIP
- ✅ Más flexible y reutilizable

### Aplicación consistente en toda la arquitectura

**En CtrlDominioUsuario:**
```java
// Antes: dependencias almacenadas
private CtrlDominioRespuesta ctrlDominioRespuesta;
private CtrlDominioPregunta ctrlDominioPregunta;

// Ahora: inyección por parámetros
public List<Integer> getEncuestasRespondidas(String nomU,
                                               CtrlDominioRespuesta ctrlDominioRespuesta,
                                               CtrlDominioPregunta ctrlDominioPregunta) {
    // Usa parámetros, no campos almacenados
}
```

**En CtrlDominioEncuesta:**
```java
// Constructor sin dependencias
public CtrlDominioEncuesta() {
    this.encuestas = new TreeMap<>();
    this.nextId = 1;
}

// Métodos reciben dependencias
public int crearEncuesta(..., CtrlDominioUsuario ctrlDominioUsuario) { ... }
public void eliminarEncuesta(int idE, 
                              CtrlDominioPregunta ctrlDominioPregunta,
                              CtrlDominioUsuario ctrlDominioUsuario) { ... }
public Map<Pregunta, Set<Respuesta>> getRespuestasEncuesta(int idE,
                              CtrlDominioRespuesta ctrlDominioRespuesta,
                              CtrlDominioPregunta ctrlDominioPregunta) { ... }
```

**En CtrlDominio (Facade - Coordinador):**
```java
// CtrlDominio mantiene referencias (es el único que debe hacerlo)
private CtrlDominioUsuario ctrlDominioUsuario;
private CtrlDominioEncuesta ctrlDominioEncuesta;
private CtrlDominioPregunta ctrlDominioPregunta;
private CtrlDominioRespuesta ctrlDominioRespuesta;

// Y las inyecta como parámetros cuando es necesario
public int darRespuesta(String nomU, int idP, T resp) {
    return ctrlDominioRespuesta.darRespuesta(nomU, idP, resp,
                                               ctrlDominioUsuario,  // Inyecta
                                               ctrlDominioPregunta); // Inyecta
}
```

---

## 🤔 PREGUNTAS FRECUENTES

**P: ¿Los CtrlDominioXXX deberían ser Singleton?**
R: NO. Antes usaban constructor injection (dependencias almacenadas), ahora usan parameter injection. Singleton dificultaría testing y viola DIP.

**P: ¿Por qué inyección por parámetros en vez de constructor?**
R: Porque reduce acoplamiento. Las dependencias solo se necesitan en métodos específicos que cruzan controladores, no en todo momento. Es más testeable y flexible.

**P: ¿Qué pasa con CtrlRespuesta, CtrlUsuario, CtrlEncuesta (Singletons viejos)?**
R: Se eliminaron. Su funcionalidad se fusionó en CtrlDominioXXX correspondientes. Ya no hay doble capa.

**P: ¿Cuándo se guarda en persistencia?**
R: Solo al cerrar la aplicación, en `CtrlDominio.cerrarSesion()`.

**P: ¿Cuándo se carga desde persistencia?**
R: Solo al iniciar la aplicación, en `CtrlDominio.inicializarCtrlDominio()`.

**P: ¿Qué pasa si el sistema se cierra inesperadamente?**
R: Se pierden los cambios de la sesión (limitación aceptable para el 1er lliurament). En producción se implementaría auto-guardado periódico.

**P: ¿Por qué Factory Pattern en Pregunta?**
R: Respeta el principio Abierto/Cerrado. Si añades un nuevo tipo de pregunta, solo implementas `crearRespuesta()` en la nueva subclase, sin tocar `CtrlDominioRespuesta`.

**P: ¿Dónde va el método getXXXDeY(idY)?**
R: En CtrlDominioY (el aggregate root). Ejemplo: `getRespuestasEncuesta(idE)` → `CtrlDominioEncuesta`.

**P: ¿Cómo sé si una dependencia debe ser parámetro o campo?**
R: Regla simple:
- **Campo almacenado**: Solo datos propios (TreeMap de tu entidad)
- **Parámetro**: Cualquier otro CtrlDominioXXX que necesites consultar

---

## 📚 REFERENCIAS

- `ANALISIS_ARQUITECTURA_3CAPAS.txt` - Análisis completo de la arquitectura
- `DISTRIBUCION_METODOS_CORRECTA.md` - Guía de dónde poner cada método
- `info_3capas.txt` - PDF con arquitectura PROP de 3 capas
- `notes classe.txt` - Notas de clase sobre persistencia

---

**Última actualización:** [Fecha actualizada]  
**Estado:** CtrlDominioRespuesta ✅ | CtrlDominioUsuario ✅ | CtrlDominioEncuesta ✅ | CtrlDominioPregunta ⚠️  
**Patrón actual:** Inyección por parámetros (parameter injection) sin dependencias almacenadas  
**Responsable:** Refactorización coordinada con GitHub Copilot

## 📝 ***************** TAREAS PENDIENTES DE HACER *****************

### **Tarea 1: Aplicar el mismo patrón a los otros controladores**

Necesitan refactorizar:
- ✅ `CtrlDominioRespuesta` - **YA HECHO** ✨
- ⚠️ `CtrlDominioUsuario` - **PENDIENTE**
- ⚠️ `CtrlDominioEncuesta` - **PENDIENTE**
- ⚠️ `CtrlDominioPregunta` - **PENDIENTE**

**Pasos para cada controlador:**

#### Ejemplo: Refactorizar CtrlDominioUsuario

**Paso 1: Añadir TreeMap y eliminar referencias a CtrlUsuario (Singleton viejo)**
```java
public class CtrlDominioUsuario {
    // ANTES:
    // private CtrlUsuario ctrlUsuario;
    
    // AHORA:
    private TreeMap<String, Usuario> usuarios;  // DATOS EN MEMORIA
    private GestorPersistencia gestorPersistencia;  // Solo para abrir/cerrar sesión
    
    public CtrlDominioUsuario(GestorPersistencia gestor) {
        this.usuarios = new TreeMap<>();
        this.gestorPersistencia = gestor;
    }
}
```

**Paso 2: Añadir métodos privados auxiliares**
```java
private boolean existeUsuario(String nomU) {
    return usuarios.containsKey(nomU);
}

public Usuario getUsuario(String nomU) {
    Usuario u = usuarios.get(nomU);
    if (u == null) throw new IllegalArgumentException("Usuario no existe");
    return u;
}
```

**Paso 3: Implementar CRUD gestionando TreeMap directamente**
```java
public String registrarUsuario(String nomU, String pass, String confirmPass) {
    // Validaciones
    if (existeUsuario(nomU)) throw new UsuarioYaExisteException();
    
    // Crear y guardar EN MEMORIA
    Usuario u = new Usuario(nomU, pass);
    usuarios.put(nomU, u);
    
    // NO llamar a persistencia aquí! ✅
    return nomU;
}

public void modificarContrasena(String nomU, String newPass) {
    Usuario u = getUsuario(nomU);
    u.setContrasena(newPass);
    // NO llamar a persistencia aquí! ✅
}
```

**Paso 4: Añadir métodos para persistencia**
```java
// Para guardar al cerrar sesión
public TreeMap<String, Usuario> getAllUsuarios() {
    return new TreeMap<>(usuarios); // copia
}

// Para cargar al abrir sesión
public void cargarUsuarios(TreeMap<String, Usuario> datosImportados) {
    this.usuarios = datosImportados;
}
```

**Paso 5: Actualizar constructor de CtrlDominio**
```java
// En CtrlDominio.inicializarCtrlDominio()

// ANTES:
// ctrlDominioUsuario = new CtrlDominioUsuario(ctrlUsuario, gestorPersistencia);

// AHORA:
ctrlDominioUsuario = new CtrlDominioUsuario(gestorPersistencia);
```

---

### **Tarea 2: Implementar métodos getAllXXX() en cada controlador**

Cada CtrlDominioXXX necesita:
```java
public TreeMap<Key, Object> getAllXXX() {
    return new TreeMap<>(datosInternos); // devolver copia
}

public void cargarXXX(TreeMap<Key, Object> datos) {
    this.datosInternos = datos;
    // Actualizar nextId si es necesario
}
```

---

### **Tarea 3: Implementar métodos en GestorPersistencia**

```java
public class GestorPersistencia {
    // Ya existe como STUB con HashMaps
    
    // Añadir métodos:
    public void guardarTodosUsuarios(TreeMap<String, Usuario> usuarios) { ... }
    public void guardarTodasEncuestas(TreeMap<Integer, Encuesta> encuestas) { ... }
    public void guardarTodasPreguntas(TreeMap<Integer, Pregunta> preguntas) { ... }
    public void guardarTodasRespuestas(TreeMap<Integer, Respuesta> respuestas) { ... }
    
    public TreeMap<String, Usuario> cargarTodosUsuarios() { ... }
    // etc.
}
```

---

### **Tarea 4: Implementar iniciarSesion() en CtrlDominio**

```java
public void iniciarSesion(String nomU, String pass) {
    // 1. Validar credenciales
    ctrlDominioUsuario.iniciarSesion(nomU, pass);
    
    // 2. CARGAR TODO desde persistencia (solo aquí!)
    ctrlDominioUsuario.cargarUsuarios(gestorPersistencia.cargarTodosUsuarios());
    ctrlDominioEncuesta.cargarEncuestas(gestorPersistencia.cargarTodasEncuestas());
    ctrlDominioPregunta.cargarPreguntas(gestorPersistencia.cargarTodasPreguntas());
    ctrlDominioRespuesta.cargarRespuestas(gestorPersistencia.cargarTodasRespuestas());
}
```

---

## ✅ CHECKLIST DE TAREAS

### Para cada CtrlDominioXXX (Usuario, Encuesta, Pregunta):
- [ ] Añadir `TreeMap<Key, Object> datos` como atributo
- [ ] Eliminar referencias a CtrlXXX (Singleton viejo)
- [ ] Añadir métodos privados `existeXXX()` y `getXXX()`
- [ ] Refactorizar todos los métodos CRUD para gestionar TreeMap directamente
- [ ] **Eliminar todas las llamadas a persistencia** de los métodos CRUD
- [ ] Añadir `getAllXXX()` para exportar datos
- [ ] Añadir `cargarXXX()` para importar datos
- [ ] Actualizar constructor para recibir solo referencias necesarias

### Para GestorPersistencia:
- [ ] Implementar `guardarTodosXXX()` para cada entidad
- [ ] Implementar `cargarTodosXXX()` para cada entidad

### Para CtrlDominio:
- [x] Implementar `cerrarSesion()` que guarda todo **YA HECHO** ✅
- [ ] Implementar `iniciarSesion()` que carga todo

### Limpieza final:
- [ ] Eliminar carpeta `classcontroller/` (CtrlUsuario, CtrlEncuesta, etc.)
- [ ] Actualizar todos los imports
- [ ] Verificar que no hay errores de compilación
- [ ] Ejecutar tests

---

## 🎯 CONCEPTO CLAVE A RECORDAR

**"Los objetos viven en memoria (TreeMap) durante toda la sesión"**

```
INICIO SESIÓN
    ↓
GestorPersistencia.cargar() → CtrlDominioXXX (TreeMaps en memoria)
    ↓
OPERACIONES CRUD (crear, modificar, eliminar)
    → Todo se hace en memoria (TreeMaps)
    → NO se llama a persistencia
    ↓
CERRAR SESIÓN
    ↓
CtrlDominioXXX.getAllXXX() → GestorPersistencia.guardar()
```

---

## 🤔 PREGUNTAS FRECUENTES

**P: ¿Los CtrlDominioXXX deberían ser Singleton?**
R: NO. Usan inyección de dependencias (reciben otros controladores en constructor). Singleton dificultaría testing y viola DIP.

**P: ¿Qué pasa con CtrlRespuesta (el Singleton viejo)?**
R: Se elimina. Su funcionalidad se fusionó en CtrlDominioRespuesta.

**P: ¿Cuándo se guarda en persistencia?**
R: Solo al cerrar sesión, en `CtrlDominio.cerrarSesion()`.

**P: ¿Qué pasa si el sistema se cierra inesperadamente?**
R: Se pierden los cambios de la sesión (es una limitación aceptable para el 1er lliurament). En producción se implementaría auto-guardado periódico.

**P: ¿Por qué Factory Pattern en Pregunta?**
R: Respeta el principio Abierto/Cerrado. Si añades un nuevo tipo de pregunta, solo implementas `crearRespuesta()` en la nueva subclase, sin tocar `CtrlDominioRespuesta`.

---

## 📚 REFERENCIAS

- `ANALISIS_ARQUITECTURA_3CAPAS.txt` - Análisis completo de la arquitectura
- `info_3capas.txt` - PDF con arquitectura PROP de 3 capas
- `notes classe.txt` - Notas de clase sobre persistencia

---

**Última actualización:** 6 de noviembre de 2025  
**Responsable:** Refactorización coordinada con GitHub Copilot
