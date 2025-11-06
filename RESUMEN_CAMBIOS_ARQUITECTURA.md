# 🔄 RESUMEN DE CAMBIOS EN LA ARQUITECTURA - ACTUALIZADO (v4.0)

## 📋 ¿Qué hemos cambiado y por qué?

### **Problema Original**
- ❌ Doble capa: CtrlDominio → CtrlDominioXXX → CtrlXXX (Singleton)
- ❌ Persistencia después de cada operación CRUD
- ❌ Dependencias almacenadas (alto acoplamiento)

### **Solución Implementada**
- ✅ Fusión: CtrlDominioXXX absorbe CtrlXXX (elimina capa innecesaria)
- ✅ Persistencia solo al inicio/cierre (según transparencias arquitectura 3 capas)
- ✅ Inyección por parámetros (bajo acoplamiento)
- ✅ Objetos en memoria durante toda la sesión

---

## 🔧 CAMBIOS TÉCNICOS PRINCIPALES

### 1. **Factory Pattern en Pregunta**
**Antes:** `if (p instanceof PreguntaLibre)` (violar Open/Closed)  
**Ahora:** `p.crearRespuesta()` polimórfico en cada subclase

**Beneficio:** Añadir `PreguntaBooleana` no requiere modificar `CtrlDominioRespuesta`.

---

### 2. **Persistencia Diferida**
**Antes:** `gestorPersistencia.guardar()` después de cada CRUD  
**Ahora:**
```
INICIO → cargarTodosXXX() → Memoria (TreeMap)
                              ↓
                        CRUD (sin persistencia)
                              ↓
CIERRE → guardarTodosXXX() → Disco
```

---

### 3. **Inyección por Parámetros**
**Antes:**
```java
private CtrlDominioUsuario ctrlDominioUsuario;  // Almacenado
public CtrlDominioRespuesta(CtrlDominioUsuario u) {...}
```

**Ahora:**
```java
public CtrlDominioRespuesta() {}  // Constructor limpio
public int darRespuesta(..., CtrlDominioUsuario ctrlDominioUsuario) {...}  // Parámetro
```

**Beneficios:** Testeable, bajo acoplamiento, respeta DIP.

---

### 4. **Fusión de Capas**
**Antes:** CtrlDominioUsuario → CtrlUsuario → HashMap  
**Ahora:** CtrlDominioUsuario gestiona `TreeMap<String, Usuario>` directamente

**Patrón aplicado a:** Usuario, Encuesta, Pregunta, Respuesta.

---

### 5. **CtrlDominio como Facade**
Solo coordina e inyecta dependencias:
```java
public int darRespuesta(String nomU, int idP, T resp) {
    return ctrlDominioRespuesta.darRespuesta(nomU, idP, resp,
                                               ctrlDominioUsuario,  // Inyecta
                                               ctrlDominioPregunta);
}
```

---

## 📝 LO QUE HEMOS HECHO ✅

### **Patrón aplicado a TODOS los controladores:**
1. TreeMap propio para datos en memoria
2. Sin delegación a Singletons viejos
3. Inyección por parámetros en métodos cross-controller
4. Sin persistencia en CRUD (solo inicio/cierre)
5. Métodos getAllXXX() y cargarXXX() para bulk persistence

### **Controladores Completados:**

#### **CtrlDominioUsuario** ✅
- TreeMap<String, Usuario> + usuarioActivo
- CRUD: registrarUsuario(), iniciarSesion(), modificarContrasena()
- Métodos: getAllUsuarios(), cargarUsuarios()

#### **CtrlDominioEncuesta** ✅
- TreeMap<Integer, Encuesta> + nextId
- CRUD: crearEncuesta(), modificarTituloEncuesta(), eliminarEncuesta()
- Importación: importarEncuesta() con preguntas anidadas
- Métodos: getAllEncuestas(), cargarEncuestas()

#### **CtrlDominioPregunta** ✅
- TreeMap<Integer, Pregunta> + nextId
- **FUSIONADO con CtrlPregunta** (lógica del compañero preservada)
- CRUD: crearPreguntaLibre(), crearPreguntaNumerica(), crearPreguntaOpcionUnica(), crearPreguntaOpcionMultiple()
- Wrappers: anadirPreguntaXXX() con inyección de CtrlDominioEncuesta
- Factory: crearPregunta() genérico para importación
- Métodos: getAllPreguntas(), cargarPreguntas()

#### **CtrlDominioRespuesta** ✅
- TreeMap<Integer, Respuesta> + nextId
- Factory Pattern: delega a `Pregunta.crearRespuesta()`
- CRUD: darRespuesta(), eliminarRespuesta(), modificarRespuesta()
- Métodos: getAllRespuestas(), cargarRespuestas()

#### **CtrlDominioPerfil** ✅
- Constructor sin parámetros
- Inyección por parámetros: getPerfilesUsuario(ctrlDominioUsuario)
- Conversión: Set<Integer> → List<Integer>

#### **CtrlDominio** ✅
- Facade pura (solo delegación + coordinación)
- Inyección en TODOS los métodos cross-controller
- Persistencia: inicializarCtrlDominio(), cerrarSesion()

#### **GestorPersistencia** ✅
- Singleton: getInstance()
- Bulk operations (marcadas "Implementado por el copilot, pendiente de revisar"):
  - cargarTodosUsuarios() / guardarTodosUsuarios()
  - cargarTodasEncuestas() / guardarTodasEncuestas()
  - cargarTodasPreguntas() / guardarTodasPreguntas()
  - cargarTodasRespuestas() / guardarTodasRespuestas()

#### **Pregunta (Factory Pattern)** ✅
- Método abstracto crearRespuesta() en:
  - PreguntaLibre, PreguntaNumerica, PreguntaOpcionUnica, PreguntaOpcionMultiple

---

## ⚠️ LO QUE FALTA (MÍNIMO)

### **Tarea 1: Actualizar Tests y Drivers**
- [ ] TestCtrlDominioUsuario.java - actualizar para nueva arquitectura
- [ ] TestCtrlDominioEncuesta.java - actualizar para nueva arquitectura  
- [ ] TestPregunta.java - revisar deprecation warnings
- [ ] Todos los drivers (sin mocks)

### **Tarea 2: Limpieza**
- [ ] Eliminar CtrlUsuario.java, CtrlEncuesta.java, CtrlPregunta.java

### **Tarea 3: Revisión del Compañero**
- [ ] **GestorPersistencia**: Revisar métodos marcados "Implementado por el copilot, pendiente de revisar"

---

## 🎯 ARQUITECTURA FINAL

```
PRESENTACIÓN (CtrlPresentacion)
        ↓
CtrlDominio (Facade - coordina e inyecta)
        ↓
┌───────────────┬────────────────┬─────────────────┬────────────────┬────────────┐
Usuario         Encuesta         Pregunta          Respuesta        Perfil
TreeMap<Str,U>  TreeMap<Int,E>  TreeMap<Int,P>   TreeMap<Int,R>   (algoritmos)
└───────────────┴────────────────┴─────────────────┴────────────────┘
        ↓
GestorPersistencia (Singleton)
- Solo carga/guarda al inicio/cierre
```

**Flujo:**
```
INICIO → cargar → TreeMaps → CRUD (memoria) → CIERRE → guardar
```

---

## 🤔 PREGUNTAS FRECUENTES

**P: ¿Por qué inyección por parámetros?**  
R: Reduce acoplamiento. Dependencias solo cuando se necesitan.

**P: ¿Cuándo persistencia?**  
R: Al iniciar (`inicializarCtrlDominio()`) y al cerrar (`cerrarSesion()`).

**P: ¿Qué pasa si cierre inesperado?**  
R: Se pierden cambios de la sesión (aceptable para 1er lliurament).

**P: ¿Por qué Factory Pattern?**  
R: Open/Closed. Nuevos tipos de pregunta no modifican `CtrlDominioRespuesta`.

**P: ¿Dónde va getXXXDeY(idY)?**  
R: En CtrlDominioY (aggregate root). Ejemplo: `getRespuestasEncuesta()` → `CtrlDominioEncuesta`.

**P: ¿Los CtrlDominioXXX son Singleton?**  
R: NO. Usan parameter injection, no constructor injection.

**P: ¿Dependencia como parámetro o campo?**  
R: **Campo**: Solo TreeMap propio. **Parámetro**: Otros CtrlDominioXXX.

---

## 📚 REFERENCIAS

- `ANALISIS_ARQUITECTURA_3CAPAS.txt` - Análisis arquitectura completo
- `info_3capas.txt` - Transparencias arquitectura PROP 3 capas
- `notes classe.txt` - Notas sobre persistencia

---

**Última actualización:** 6 de noviembre de 2025  
**Estado:** ✅ **ARQUITECTURA COMPLETA**  
Usuario ✅ | Encuesta ✅ | Pregunta ✅ | Respuesta ✅ | Perfil ✅ | GestorPersistencia ✅  
**Patrón:** Inyección por parámetros (parameter injection)  
**Responsable:** Refactorización coordinada con GitHub Copilot
