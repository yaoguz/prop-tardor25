# 🔄 RESUMEN DE CAMBIOS EN LA ARQUITECTURA - ACTUALIZADO

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

### ✅ Cambio 3: CtrlDominio como Facade pura (solo delegación)

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
- [x] Actualizado constructor para recibir referencias a CtrlDominioXXX
- [x] Implementado Factory Pattern (delegación a `Pregunta.crearRespuesta()`)

### **✅ COMPLETADO: CtrlDominio**
- [x] Implementado `inicializarCtrlDominio()` que carga datos al inicio
- [x] Implementado `cerrarSesion()` que guarda datos al cierre
- [x] Actualizado para delegar a CtrlDominioXXX (sin referencias a Singleton viejos)
- [x] Eliminada toda lógica de negocio (solo delegación pura)
- [x] Inyección correcta de dependencias entre controladores

### **✅ COMPLETADO: CtrlDominioEncuesta**
- [x] Método `getRespuestasEncuesta()` actualizado para recibir `CtrlDominioRespuesta`
- [x] Lógica de negocio de encuestas implementada

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

### **Tarea 1: Fusionar CtrlDominioUsuario**
- [ ] Añadir `TreeMap<String, Usuario> usuarios`
- [ ] Eliminar referencias a CtrlUsuario (Singleton viejo)
- [ ] Implementar CRUD gestionando TreeMap directamente
- [ ] **NO llamar a persistencia en CRUD**
- [ ] Añadir `getAllUsuarios()` y `cargarUsuarios()`
- [ ] Implementar `getEncuestasRespondidas(String nomU)`

### **Tarea 2: Fusionar CtrlDominioEncuesta**
- [ ] Añadir `TreeMap<Integer, Encuesta> encuestas`
- [ ] Eliminar referencias a CtrlEncuesta (Singleton viejo)
- [ ] Implementar CRUD gestionando TreeMap directamente
- [ ] **NO llamar a persistencia en CRUD**
- [ ] Añadir `getAllEncuestas()` y `cargarEncuestas()`

### **Tarea 3: Fusionar CtrlDominioPregunta**
- [ ] Añadir `TreeMap<Integer, Pregunta> preguntas`
- [ ] Eliminar referencias a CtrlPregunta (Singleton viejo)
- [ ] Implementar CRUD gestionando TreeMap directamente
- [ ] **NO llamar a persistencia en CRUD**
- [ ] Añadir `getAllPreguntas()` y `cargarPreguntas()`

### **Tarea 4: Actualizar GestorPersistencia**
- [ ] Implementar `getInstance()` (corregir typo `getInstace()`)
- [ ] Implementar `cargarTodosUsuarios()` → `TreeMap<String, Usuario>`
- [ ] Implementar `cargarTodasEncuestas()` → `TreeMap<Integer, Encuesta>`
- [ ] Implementar `cargarTodasPreguntas()` → `TreeMap<Integer, Pregunta>`
- [ ] Implementar `cargarTodasRespuestas()` → `TreeMap<Integer, Respuesta>`
- [ ] Implementar `guardarTodosUsuarios(TreeMap<String, Usuario>)`
- [ ] Implementar `guardarTodasEncuestas(TreeMap<Integer, Encuesta>)`
- [ ] Implementar `guardarTodasPreguntas(TreeMap<Integer, Pregunta>)`
- [ ] Implementar `guardarTodasRespuestas(TreeMap<Integer, Respuesta>)`

### **Tarea 5: Actualizar clases de dominio**
- [ ] `Usuario`: Implementar métodos faltantes (si hay)
- [ ] `Encuesta`: Implementar `getNomCreador()`, `guardarIdPregunta()`, `eliminarIdPregunta()`
- [ ] Verificar que todas las clases tienen getters/setters necesarios

### **Tarea 6: Limpieza final**
- [ ] Eliminar carpeta `classcontroller/` (CtrlUsuario, CtrlEncuesta, CtrlPregunta, CtrlRespuesta)
- [ ] Actualizar todos los imports que referencien clases eliminadas
- [ ] Verificar que no hay errores de compilación
- [ ] Ejecutar tests

---

## ✅ CHECKLIST DE VERIFICACIÓN

### Para cada CtrlDominioXXX:
- [x] **CtrlDominioRespuesta**: Añadir `TreeMap<Key, Object> datos` como atributo ✅
- [ ] **CtrlDominioUsuario**: Añadir `TreeMap<String, Usuario> usuarios` ⚠️
- [ ] **CtrlDominioEncuesta**: Añadir `TreeMap<Integer, Encuesta> encuestas` ⚠️
- [ ] **CtrlDominioPregunta**: Añadir `TreeMap<Integer, Pregunta> preguntas` ⚠️

- [x] **CtrlDominioRespuesta**: Eliminar referencias a Singleton viejo ✅
- [ ] **CtrlDominioUsuario**: Eliminar referencias a CtrlUsuario ⚠️
- [ ] **CtrlDominioEncuesta**: Eliminar referencias a CtrlEncuesta ⚠️
- [ ] **CtrlDominioPregunta**: Eliminar referencias a CtrlPregunta ⚠️

- [x] **CtrlDominioRespuesta**: Métodos CRUD gestionan TreeMap directamente ✅
- [x] **CtrlDominioRespuesta**: NO llamar a persistencia en CRUD ✅
- [x] **CtrlDominioRespuesta**: Añadir `getAllRespuestas()` y `cargarRespuestas()` ✅

### Para GestorPersistencia:
- [ ] Implementar `getInstance()` (corregir typo) ⚠️
- [ ] Implementar `guardarTodosXXX()` para cada entidad ⚠️
- [ ] Implementar `cargarTodosXXX()` para cada entidad ⚠️

### Para CtrlDominio:
- [x] Implementar `inicializarCtrlDominio()` que carga todo ✅
- [x] Implementar `cerrarSesion()` que guarda todo ✅
- [x] Solo delegación pura (sin lógica de negocio) ✅

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

## 🤔 PREGUNTAS FRECUENTES

**P: ¿Los CtrlDominioXXX deberían ser Singleton?**
R: NO. Usan inyección de dependencias (reciben otros controladores en constructor). Singleton dificultaría testing y viola DIP.

**P: ¿Qué pasa con CtrlRespuesta (el Singleton viejo)?**
R: Se eliminará. Su funcionalidad se fusionó en CtrlDominioRespuesta.

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

---

## 📚 REFERENCIAS

- `DISTRIBUCION_METODOS_CORRECTA.md` - Guía de dónde va cada método
- `ANALISIS_ARQUITECTURA_3CAPAS.txt` - Análisis completo de la arquitectura
- `info_3capas.txt` - PDF con arquitectura PROP de 3 capas
- `notes classe.txt` - Notas de clase sobre persistencia

---

**Última actualización:** 6 de noviembre de 2025  
**Estado:** Refactorización parcialmente completada (CtrlDominioRespuesta ✅, resto pendiente ⚠️)  
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
