# 🔄 RESUMEN DE CAMBIOS EN LA ARQUITECTURA

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

### 2. **Solución: Fusionar capas**

**Ahora tenemos:**
```
CtrlDominio (Facade)
    ↓
CtrlDominioRespuesta (gestiona TreeMap directamente)
    - TreeMap<Integer, Respuesta> respuestas
    - CRUD operations (crear, modificar, eliminar)
    - NO llama a persistencia durante operaciones
    ↓
GestorPersistencia (solo al cerrar/abrir sesión)
```

**Ventajas:**
- ✅ Objetos en memoria durante toda la sesión (más rápido)
- ✅ Persistencia solo al cerrar sesión (arquitectura correcta según PDF)
- ✅ Cada CtrlDominioXXX gestiona sus datos directamente

---

## 🔧 CAMBIOS TÉCNICOS PRINCIPALES

### Cambio 1: Factory Pattern en Pregunta (Principio Abierto/Cerrado)

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

### Cambio 2: Persistencia solo al cerrar sesión

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
public int darRespuesta(...) {
    // Crear respuesta
    respuestas.put(idR, nuevaRespuesta);
    
    // NO se guarda en persistencia aquí ✅
    // Solo se guarda cuando se cierra sesión en CtrlDominio.cerrarSesion()
    
    return idR;
}

// En CtrlDominio
public void cerrarSesion() {
    // AQUÍ sí se guarda TODO de golpe
    gestorPersistencia.guardarTodasRespuestas(
        ctrlDominioRespuesta.getAllRespuestas()
    );
}
```

**Por qué es mejor:**
- ✅ Más eficiente (una sola escritura en vez de muchas)
- ✅ Sigue la arquitectura del PDF (objetos en memoria, persistencia al cerrar)
- ✅ Más simple de implementar y testear

### Cambio 3: CtrlDominioRespuesta gestiona TreeMap directamente

**Estructura de CtrlDominioRespuesta:**
```java
public class CtrlDominioRespuesta {
    // DATOS EN MEMORIA (no en CtrlRespuesta)
    private TreeMap<Integer, Respuesta> respuestas;
    private int nextId;
    
    // Referencias a otros controladores de dominio
    private CtrlDominioUsuario ctrlDominioUsuario;
    private CtrlDominioPregunta ctrlDominioPregunta;
    
    // Métodos CRUD
    public int darRespuesta(...) { /* gestiona TreeMap directamente */ }
    public void modificarRespuesta(...) { /* gestiona TreeMap directamente */ }
    public void eliminarRespuesta(...) { /* gestiona TreeMap directamente */ }
    
    // Métodos para persistencia (solo llamados al abrir/cerrar sesión)
    public TreeMap<Integer, Respuesta> getAllRespuestas() { /* para guardar */ }
    public void cargarRespuestas(TreeMap<...> datos) { /* para cargar */ }
}
```

---

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
