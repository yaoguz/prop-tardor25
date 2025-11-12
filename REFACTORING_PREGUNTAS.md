# Refactorización: Eliminación de Métodos Redundantes en CtrlDominioPregunta

## 📋 Cambios Realizados

### 1. Eliminación de Métodos Redundantes ✅

**Métodos eliminados:**
- ❌ `anadirPreguntaLibre()`
- ❌ `anadirPreguntaNumerica()`
- ❌ `anadirPreguntaOpcionUnica()`
- ❌ `anadirPreguntaOpcionMultiple()`

**Razón:** Eran wrappers innecesarios que solo llamaban a `crear*()` sin añadir lógica adicional.

---

### 2. Actualización de Firmas de Métodos `crear*()` ✅

#### **PreguntaOpcionUnica:**
```java
// ANTES:
crearPreguntaOpcionUnica(int idEncuesta, String enunciado, boolean ordenada, ...)

// DESPUÉS:
crearPreguntaOpcionUnica(int idEncuesta, String enunciado, 
                         List<String> opciones, boolean ordenada, ...)
```

#### **PreguntaOpcionMultiple:**
```java
// ANTES:
crearPreguntaOpcionMultiple(int idEncuesta, String enunciado, ...)

// DESPUÉS:
crearPreguntaOpcionMultiple(int idEncuesta, String enunciado, 
                            List<String> opciones, int numMaxOpc, ...)
```

---

### 3. Implementación con Loop `addOpcion()` ✅

**Decisión de diseño:** Usar loop + `addOpcion()` en vez de modificar constructoras.

**Justificación:**
1. ✅ **Mantiene constructoras simples:** No sobrecargamos con múltiples parámetros
2. ✅ **Reutiliza validación existente:** `addOpcion()` ya valida cada opción individualmente
3. ✅ **Flexible:** Permite crear pregunta vacía y añadir opciones progresivamente
4. ✅ **Consistente con patrón Builder:** Separación entre construcción y configuración

**Código implementado:**
```java
public int crearPreguntaOpcionUnica(int idEncuesta, String enunciado, 
                                     List<String> opciones, boolean ordenada,
                                     CtrlDominioEncuesta ctrlDominioEncuesta) {
    int idPregunta = nextId++;
    PreguntaOpcionUnica p = new PreguntaOpcionUnica(idEncuesta, idPregunta, enunciado, ordenada);
    
    // Loop para añadir opciones (aprovecha validación existente)
    if (opciones != null) {
        for (String opcion : opciones) {
            p.addOpcion(opcion);
        }
    }
    
    preguntas.put(idPregunta, p);
    // ... resto del código
}
```

---

### 4. Actualización de `crearPregunta()` Genérico ✅

Ahora soporta `opciones` y `numMaxOpc` en `atributosEspecificos`:

```java
case "opcion_unica":
    List<String> opcionesOU = (List<String>) atributosEspecificos.get("opciones");
    boolean ordenada = (boolean) atributosEspecificos.getOrDefault("ordenada", false);
    return crearPreguntaOpcionUnica(idEncuesta, enunciado, opcionesOU, ordenada, ...);

case "opcion_multiple":
    List<String> opcionesOM = (List<String>) atributosEspecificos.get("opciones");
    int numMaxOpc = (int) atributosEspecificos.getOrDefault("numMaxOpc", 10);
    return crearPreguntaOpcionMultiple(idEncuesta, enunciado, opcionesOM, numMaxOpc, ...);
```

---

### 5. Actualización de `CtrlDominio` ✅

Los métodos `anadir*()` en CtrlDominio ahora llaman directamente a `crear*()`:

```java
// ANTES:
public int anadirPreguntaOpcionUnica(int idEncuesta, String enunciado, List<String> opciones) {
    return ctrlDominioPregunta.anadirPreguntaOpcionUnica(idEncuesta, enunciado, opciones, ...);
}

// DESPUÉS:
public int anadirPreguntaOpcionUnica(int idEncuesta, String enunciado, List<String> opciones) {
    return ctrlDominioPregunta.crearPreguntaOpcionUnica(idEncuesta, enunciado, opciones, false, ...);
}
```

**Nota:** En CtrlDominio **mantenemos** los métodos `anadir*()` porque:
- Son parte de la API pública de CtrlDominio
- Simplifican la interfaz para el usuario final (menos parámetros)
- Proporcionan valores por defecto razonables (ej: `ordenada=false`, `numMaxOpc=size(opciones)`)

---

## 🎯 Resultado Final

### Beneficios:
1. ✅ **Menos código redundante:** -60 líneas eliminadas
2. ✅ **API más consistente:** Todos los `crear*()` reciben parámetros completos
3. ✅ **Más flexible:** Opciones pueden ser null (pregunta vacía) o pobladas
4. ✅ **Mejor mantenibilidad:** Un solo lugar para crear cada tipo de pregunta

### Métodos Públicos en CtrlDominioPregunta:
- ✅ `crearPreguntaLibre(idEncuesta, enunciado, ...)`
- ✅ `crearPreguntaNumerica(idEncuesta, enunciado, min, max, ...)`
- ✅ `crearPreguntaOpcionUnica(idEncuesta, enunciado, opciones, ordenada, ...)`
- ✅ `crearPreguntaOpcionMultiple(idEncuesta, enunciado, opciones, numMaxOpc, ...)`
- ✅ `crearPregunta(idEncuesta, enunciado, tipo, atributos, ...)` - Genérico

### Backwards Compatibility:
- ✅ CtrlDominio mantiene su API (`anadir*()`)
- ✅ Código cliente que usa CtrlDominio **no necesita cambios**
- ⚠️ Código que llamaba directamente a `CtrlDominioPregunta.anadir*()` debe cambiarse a `crear*()`

---

## 📚 Sobre tu Pregunta: ¿Hace falta testear CtrlDominio en drivers?

### Respuesta Corta:
**Sí, pero NO en los drivers actuales.** Los drivers actuales prueban controladores de dominio específicos (CtrlDominioRespuesta, etc.). CtrlDominio se testearía en un **driver de integración end-to-end**.

### Explicación Detallada:

#### **Arquitectura Actual:**
```
CtrlDominio (Facade/Coordinator)
    ├─> CtrlDominioUsuario
    ├─> CtrlDominioEncuesta
    ├─> CtrlDominioPregunta
    └─> CtrlDominioRespuesta
```

#### **Tipos de Drivers:**

1. **Drivers de Controlador Específico** (actuales):
   - **DriverRespuesta** → Prueba `CtrlDominioRespuesta`
   - **DriverUsuario** → Prueba `CtrlDominioUsuario`
   - **DriverEncuesta** → Prueba `CtrlDominioEncuesta`
   - **DriverPregunta** → Prueba `CtrlDominioPregunta`
   
   **Objetivo:** Probar funcionalidad específica con máximo control.
   **Dependencias:** Usan otros `CtrlDominio*` directamente (inyección de dependencias).

2. **Driver de CtrlDominio** (pendiente de crear):
   - **DriverCtrlDominio** → Prueba `CtrlDominio` completo
   
   **Objetivo:** Probar flujo completo end-to-end como lo usaría CtrlPresentacion.
   **Escenarios:**
   ```java
   // Ejemplo: Crear encuesta completa con preguntas y respuestas
   1. inicializarSesion()
   2. registrarUsuario("alice", ...)
   3. iniciarSesion("alice", ...)
   4. crearEncuesta("Mi Encuesta")
   5. anadirPreguntaNumerica(idEncuesta, "Edad", 0, 120)
   6. anadirPreguntaOpcionUnica(idEncuesta, "Género", ["M", "F", "Otro"])
   7. darRespuesta("bob", idPregunta1, 25)
   8. consultarEncuesta(idEncuesta)
   9. cerrarSesion()
   ```

#### **Diferencias Clave:**

| Aspecto | Driver Específico | Driver CtrlDominio |
|---------|-------------------|-------------------|
| **Scope** | Un controlador | Todos integrados |
| **Dependencias** | Usa otros Ctrl directamente | Solo usa CtrlDominio |
| **Setup** | Manual (crea objetos necesarios) | Automático (CtrlDominio gestiona) |
| **Objetivo** | Probar lógica específica | Probar coordinación |
| **Ejemplo** | DriverRespuesta crea usuarios/preguntas para testear respuestas | DriverCtrlDominio usa flujo natural de la app |

#### **¿Cuándo Crear DriverCtrlDominio?**

**Momento ideal:** Después de tener todos los drivers específicos funcionando.

**Propósito:**
1. ✅ Validar que la **coordinación** entre controladores funciona
2. ✅ Probar **sesiones** (inicializar, cerrar, persistencia)
3. ✅ Simular **flujo real de usuario** desde presentación
4. ✅ Detectar **problemas de integración** que drivers individuales no ven

**Ejemplo de Test en DriverCtrlDominio:**
```java
// Test: Eliminar encuesta elimina todas sus preguntas y respuestas
1. Crear encuesta con 3 preguntas
2. 2 usuarios responden (6 respuestas totales)
3. Eliminar encuesta
4. Verificar:
   - Preguntas eliminadas ✓
   - Respuestas eliminadas ✓
   - Usuarios actualizados (respuestas eliminadas de su lista) ✓
```

---

## 🔧 Próximos Pasos Recomendados

1. ✅ **Completado:** Refactorizar CtrlDominioPregunta
2. ⏳ **Pendiente:** Actualizar tests unitarios que usen `anadir*()` (si existen)
3. ⏳ **Pendiente:** Crear `DriverCtrlDominio` para pruebas end-to-end
4. ⏳ **Pendiente:** Documentar flujos de uso en `EjemplosUso.java`

---

## ✅ Checklist de Verificación

- [x] Métodos `anadir*()` eliminados de CtrlDominioPregunta
- [x] Métodos `crear*()` actualizados con parámetros `opciones`
- [x] Implementación con loop `addOpcion()`
- [x] `crearPregunta()` genérico actualizado
- [x] CtrlDominio actualizado para llamar a `crear*()` directamente
- [x] Código compila sin errores en CtrlDominioPregunta
- [ ] Actualizar DriverRespuesta si usa métodos obsoletos
- [ ] Crear DriverCtrlDominio (futuro)
- [ ] Actualizar documentación de API

---

**Cambios aplicados correctamente. El diseño es ahora más limpio y consistente.** ✅
