# Descripción del Joc de Proves - DriverRespuesta

## 📋 IDENTIFICACIÓN

**Executable:** `DriverRespuesta.java`  
**Package:** `drivers`  
**Autor:** Yaoyao Guo Zhang  
**Fecha:** Noviembre 2025

---

## 🎯 OBJECTE DE LA PROVA

### Controlador provat:
- **`CtrlDominioRespuesta`** - Controlador de dominio para gestión de respuestas

### Casos d'ús provats:

1. **CU-01: Crear respuesta numérica**
   - Entrada: usuario, pregunta numérica, valor numérico
   - Postcondición: Respuesta creada y vinculada a Usuario y Pregunta
   
2. **CU-02: Crear respuesta opción única**
   - Entrada: usuario, pregunta opción única, índice de opción
   - Postcondición: Respuesta creada con validación de índice

3. **CU-03: Crear respuesta opción múltiple**
   - Entrada: usuario, pregunta opción múltiple, array de índices
   - Postcondición: Respuesta creada con validación de límite de opciones

4. **CU-04: Crear respuesta libre**
   - Entrada: usuario, pregunta libre, texto
   - Postcondición: Respuesta creada sin restricciones de formato

5. **CU-05: Modificar respuesta**
   - Entrada: ID respuesta, nuevo valor
   - Precondición: Respuesta existe
   - Postcondición: Respuesta modificada manteniendo tipo

6. **CU-06: Eliminar respuesta**
   - Entrada: ID respuesta
   - Precondición: Respuesta existe
   - Postcondición: Respuesta eliminada de TreeMap, Usuario y Pregunta

7. **CU-07: Consultar respuesta por ID**
   - Entrada: ID respuesta
   - Salida: Datos completos de la respuesta

8. **CU-08: Listar todas las respuestas**
   - Salida: TreeMap completo ordenado por ID

9. **CU-09: Listar respuestas por usuario**
   - Entrada: nombre usuario
   - Salida: Todas las respuestas del usuario

10. **CU-10: Listar respuestas por pregunta**
    - Entrada: ID pregunta
    - Salida: Todas las respuestas a esa pregunta

---

## 🔗 ALTRES ELEMENTS INTEGRATS A LA PROVA

### Classes de domini (ja provades en tests unitaris):
- ✅ **`Usuario`** - Gestiona lista de IDs de respuestas del usuario
- ✅ **`Pregunta`** (abstracta) - Gestiona lista de IDs de respuestas a la pregunta
- ✅ **`PreguntaNumerica`** - Validación de rango [min, max]
- ✅ **`PreguntaOpcionUnica`** - Validación de índice de opción
- ✅ **`PreguntaOpcionMultiple`** - Validación de límite de opciones
- ✅ **`PreguntaLibre`** - Sin validaciones específicas
- ✅ **`Respuesta`** (abstracta) - Clase base con ID, usuario, pregunta
- ✅ **`RespuestaNumerica`** - Almacena `Double`
- ✅ **`RespuestaOpcionUnica`** - Almacena `Integer` (índice)
- ✅ **`RespuestaOpcionMultiple`** - Almacena `int[]` (array de índices)
- ✅ **`RespuestaLibre`** - Almacena `String`

### Controladors de domini (integrados):
- ✅ **`CtrlDominioUsuario`** - Para validar existencia de usuarios y actualizar sus respuestas
- ✅ **`CtrlDominioPregunta`** - Para validar existencia de preguntas y actualizar sus respuestas
- ✅ **`CtrlDominioEncuesta`** - Para crear encuestas que contengan preguntas

### Patrons de disseny utilitzats:
- **Factory Pattern**: `Pregunta.crearRespuesta(...)` crea polimórficamente el tipo correcto de respuesta
- **Dependency Injection**: Controladores inyectados como parámetros (no almacenados)
- **Strategy Pattern**: Polimorfismo en `Respuesta.modificar(T newResp)`

---

## 📁 FITXERS DE DADES NECESSARIS

**No se utilizan ficheros externos en esta versión del driver.**

Todos los datos se introducen por teclado durante la ejecución interactiva.

> **Nota:** El método `CtrlDominioRespuesta.importarRespuestas(File fichero, ...)` está implementado
> pero no se prueba en este driver por estar pendiente el parser de ficheros.

---

## 🧪 VALORS ESTUDIATS

### 1. Caixa negra - Particions d'equivalència:

#### A) Respuestas numéricas:
| Partición | Valores | Resultado esperado |
|-----------|---------|-------------------|
| Dentro del rango | v ∈ [min, max] | ✅ Respuesta creada |
| Fuera del rango (inferior) | v < min | ❌ IllegalArgumentException |
| Fuera del rango (superior) | v > max | ❌ IllegalArgumentException |
| Valor límite inferior | v = min | ✅ Respuesta creada |
| Valor límite superior | v = max | ✅ Respuesta creada |

**Ejemplo de valores probados:**
- Pregunta: "¿Horas de deporte/semana?" [0, 20]
- Valores válidos: 0, 5.5, 10, 15, 20
- Valores inválidos: -1, 25, 100

#### B) Respuestas opción única:
| Partición | Valores | Resultado esperado |
|-----------|---------|-------------------|
| Índice válido | 0 ≤ i < numOpciones | ✅ Respuesta creada |
| Índice negativo | i < 0 | ❌ ArrayIndexOutOfBoundsException |
| Índice fuera de rango | i ≥ numOpciones | ❌ ArrayIndexOutOfBoundsException |

**Ejemplo de valores probados:**
- Pregunta: "Nivel de actividad" [Bajo, Medio, Alto]
- Valores válidos: 0 (Bajo), 1 (Medio), 2 (Alto)
- Valores inválidos: -1, 3, 10

#### C) Respuestas opción múltiple:
| Partición | Valores | Resultado esperado |
|-----------|---------|-------------------|
| Array vacío | [] | ✅ Respuesta creada |
| 1 opción | [i] | ✅ Respuesta creada |
| Varias opciones válidas | [i, j, k] donde n ≤ max | ✅ Respuesta creada |
| Excede límite | array.length > maxOpciones | ❌ IllegalArgumentException |
| Índices duplicados | [0, 1, 0] | ✅ Validado en dominio |
| Índice inválido | [99] | ❌ ArrayIndexOutOfBoundsException |

**Ejemplo de valores probados:**
- Pregunta: "¿Qué deportes practicas?" [Fútbol, Basket, Natación, Running, Ciclismo], max=5
- Valores válidos: [], [0], [0,2,4], [0,1,2,3,4]
- Valores inválidos: [0,1,2,3,4,5] (excede límite si max < 6)

#### D) Respuestas libres:
| Partición | Valores | Resultado esperado |
|-----------|---------|-------------------|
| Texto normal | "Mi rutina es correr" | ✅ Respuesta creada |
| Texto vacío | "" | ✅ Respuesta creada |
| Texto largo | 500+ caracteres | ✅ Respuesta creada |
| Caracteres especiales | "café ñoño €" | ✅ Respuesta creada |
| null | null | ❌ NullPointerException |

**Ejemplo de valores probados:**
- Pregunta: "Describe tu rutina"
- Valores: "", "Correr 5km", "Texto muy largo...", "Símbolos: @#$%"

### 2. Modificación de respuestas:

#### Tipo compatible:
| Tipo original | Nuevo valor | Resultado |
|---------------|-------------|-----------|
| RespuestaNumerica | Double | ✅ Modificada |
| RespuestaOpcionUnica | Integer | ✅ Modificada |
| RespuestaOpcionMultiple | int[] | ✅ Modificada |
| RespuestaLibre | String | ✅ Modificada |

#### Tipo incompatible:
| Tipo original | Nuevo valor | Resultado |
|---------------|-------------|-----------|
| RespuestaNumerica | String | ❌ TipoIncorrectoException |
| RespuestaOpcionUnica | Double | ❌ TipoIncorrectoException |

### 3. Eliminación y integridad referencial:

**Valores probados:**
- Eliminar respuesta existente → Se elimina de TreeMap, Usuario, Pregunta ✅
- Eliminar respuesta inexistente → IllegalArgumentException ❌
- Verificar Usuario.getIdRespuestas() antes/después → Lista actualizada ✅
- Verificar Pregunta.getIdRespuestas() antes/después → Lista actualizada ✅

---

## 🎨 EFECTES ESTUDIATS

### 1. Integridad referencial bidireccional:

**Efecto:** Al crear una respuesta, se actualiza automáticamente:
- ✅ `TreeMap<Integer, Respuesta>` en CtrlDominioRespuesta
- ✅ `List<Integer> idRespuestas` en Usuario
- ✅ `List<Integer> idRespuestas` en Pregunta

**Prueba visual:** 
1. Crear respuesta para usuario "alice"
2. Listar respuestas de "alice" → La nueva respuesta aparece
3. Listar respuestas de la pregunta → La nueva respuesta aparece

**Efecto:** Al eliminar una respuesta, se elimina de los 3 lugares:
- ✅ Eliminada del TreeMap
- ✅ Eliminada de Usuario
- ✅ Eliminada de Pregunta

### 2. Polimorfismo en creación:

**Efecto:** El método `darRespuesta(...)` es genérico pero crea el tipo correcto:
```java
// Mismo método, diferentes tipos:
darRespuesta("alice", idPreguntaNumerica, 5.0, ...)        → RespuestaNumerica
darRespuesta("alice", idPreguntaOpcionUnica, 2, ...)       → RespuestaOpcionUnica
darRespuesta("alice", idPreguntaOpcionMultiple, [0,2], ...)→ RespuestaOpcionMultiple
darRespuesta("alice", idPreguntaLibre, "texto", ...)       → RespuestaLibre
```

**Prueba visual:** Crear 4 respuestas de tipos diferentes y listarlas todas → Cada una muestra su tipo correctamente.

### 3. IDs autoincrementales únicos:

**Efecto:** Cada respuesta creada obtiene un ID único incremental.

**Prueba visual:**
1. Crear respuesta → ID = 1
2. Crear otra → ID = 2
3. Eliminar ID 1
4. Crear otra → ID = 3 (no reutiliza el 1)

### 4. Validaciones en cascada:

**Efecto:** Las validaciones se propagan desde dominio hasta controlador.

**Ejemplo:**
- Usuario introduce valor 25 para pregunta con rango [0, 20]
- `PreguntaNumerica.crearRespuesta(25)` → Validación en RespuestaNumerica
- Lanza `IllegalArgumentException`
- Driver captura excepción y muestra mensaje de error ❌

### 5. Visualización diferenciada por tipo:

**Efecto:** El driver muestra cada tipo de respuesta de forma apropiada:
- Numérica: Muestra el valor decimal
- Opción única: Muestra el índice y el texto de la opción
- Opción múltiple: Muestra la lista de opciones seleccionadas
- Libre: Muestra el texto completo

**Prueba visual:** Listar todas las respuestas → Cada una se visualiza según su tipo.

---

## 📖 OPERATIVA

### Inicio del driver:

```bash
# Compilar
javac -d build/classes src/main/java/drivers/DriverRespuesta.java \
               src/main/java/domaincontrollers/*.java \
               src/main/java/domain/*.java

# Ejecutar
java -cp build/classes drivers.DriverRespuesta
```

### Flujo de ejecución:

1. **Inicialización automática:**
   - Se crean 3 usuarios: alice, bob, charlie
   - Se crea 1 encuesta: "Encuesta Hábitos Deportivos"
   - Se crean 4 preguntas (1 de cada tipo)
   - Mensaje: "✅ Datos de prueba creados correctamente."

2. **Menú principal:**
   - 11 opciones numeradas + opción 0 para salir
   - Usuario selecciona opción escribiendo el número

3. **Entrada de datos:**
   - Según la opción, el driver solicita datos por teclado
   - Entrada natural: nombres de usuario, números, índices, texto libre
   - Validación inmediata con mensajes de error descriptivos

4. **Visualización de resultados:**
   - Cada operación muestra confirmación visual con ✅
   - Listados formateados en tabla con códigos de tipo (NUM, OPU, OPM, TXT)
   - Detalles de respuesta en recuadro con caracteres Unicode

5. **Manejo de errores:**
   - Excepciones capturadas y mostradas con ❌
   - Tipo de excepción indicado (IllegalArgumentException, etc.)
   - Programa continúa ejecutándose (no crash)

6. **Opción de ayuda:**
   - Opción 11: Muestra todos los IDs de preguntas y usuarios disponibles
   - Útil cuando el usuario no recuerda los IDs

### Ejemplo de sesión:

```
═══ CREAR RESPUESTA NUMÉRICA ═══
Usuarios disponibles: alice, bob, charlie
Usuario (nombre): alice

Pregunta: ¿Cuántas horas a la semana haces deporte?
Rango válido: [0, 20]
Respuesta (número): 10.5

✅ Respuesta numérica creada con ID: 1
   Usuario: alice
   Valor: 10.5

Presione ENTER para continuar...
```

### Casos de prueba sugeridos:

#### Caso 1: Crear respuesta de cada tipo
1. Opción 1: Crear respuesta numérica para alice (valor: 10)
2. Opción 2: Crear respuesta opción única para bob (índice: 1)
3. Opción 3: Crear respuesta opción múltiple para charlie (índices: 0,2,4)
4. Opción 4: Crear respuesta libre para alice (texto: "Corro 30 minutos diarios")
5. Opción 8: Listar todas las respuestas → Debe mostrar las 4

#### Caso 2: Validación de rango numérico
1. Opción 1: Crear respuesta numérica
2. Introducir valor fuera de rango (ej: 25 cuando max=20)
3. Debe mostrar: ❌ ERROR: IllegalArgumentException

#### Caso 3: Modificación
1. Opción 7: Consultar respuesta ID 1
2. Opción 5: Modificar respuesta ID 1 con nuevo valor
3. Opción 7: Consultar respuesta ID 1 de nuevo → Verificar cambio

#### Caso 4: Integridad referencial
1. Opción 9: Listar respuestas de alice → Anotar cantidad
2. Opción 6: Eliminar una respuesta de alice
3. Opción 9: Listar respuestas de alice → Verificar que cantidad disminuyó

#### Caso 5: Listados por pregunta
1. Crear varias respuestas a la misma pregunta (diferentes usuarios)
2. Opción 10: Listar respuestas de esa pregunta
3. Verificar que aparecen todas las respuestas de usuarios diferentes

---

## ✅ CRITERIS D'ÈXIT

El driver considera la prueba exitosa si:

1. ✅ **Creación correcta:**
   - Se pueden crear respuestas de los 4 tipos sin errores
   - IDs autoincrementales únicos
   - Respuestas aparecen en listados

2. ✅ **Validaciones funcionan:**
   - Valores fuera de rango son rechazados
   - Índices inválidos lanzan excepciones
   - Tipos incompatibles en modificación son rechazados

3. ✅ **Integridad referencial:**
   - Respuestas creadas aparecen en Usuario y Pregunta
   - Respuestas eliminadas desaparecen de Usuario y Pregunta
   - No hay IDs huérfanos

4. ✅ **Polimorfismo:**
   - `darRespuesta()` crea el tipo correcto según la pregunta
   - `modificar()` valida el tipo del nuevo valor
   - Visualización diferenciada por tipo

5. ✅ **Robustez:**
   - Excepciones no rompen el programa
   - Mensajes de error claros
   - Programa se puede usar continuamente sin reiniciar

---

## 🐛 DEFECTES CONEGUTS / LIMITACIONS

1. **Importación de ficheros no implementada:**
   - El método `importarRespuestas(File, ...)` tiene parser pendiente
   - No se puede probar carga masiva desde fichero

2. **Validación de opciones duplicadas:**
   - En RespuestaOpcionMultiple, no se valida que los índices sean únicos
   - Ej: [0, 1, 0] es aceptado (puede ser comportamiento deseado)

3. **Sin persistencia:**
   - Las respuestas solo existen en memoria durante la ejecución
   - Al cerrar el driver, todos los datos se pierden

4. **Sin validación de límite de texto:**
   - RespuestaLibre acepta textos de cualquier longitud
   - No hay límite de caracteres

---

## 📊 COBERTURA DE PROVES

### Mètodes de CtrlDominioRespuesta provats:

| Método | Cobertura | Observaciones |
|--------|-----------|---------------|
| `darRespuesta(...)` | ✅ 100% | Probado con 4 tipos de respuesta |
| `modificarRespuesta(...)` | ✅ 100% | Probado con modificaciones válidas e inválidas |
| `eliminarRespuesta(...)` | ✅ 100% | Probado con verificación de integridad |
| `getRespuesta(...)` | ✅ 100% | Usado en todos los listados |
| `getAllRespuestas()` | ✅ 100% | Usado en opción 8 (listar todas) |
| `existeRespuesta(...)` | ⚠️ Indirecta | Usado internamente en otros métodos |
| `importarRespuestas(...)` | ❌ 0% | Parser de ficheros no implementado |
| `cargarRespuestas(...)` | ❌ 0% | Método para persistencia, fuera del scope del driver |

**Cobertura total:** 75% (6/8 métodos)

---

## 🎯 CONCLUSIÓ

El **DriverRespuesta** proporciona una prova d'integració completa del `CtrlDominioRespuesta` amb els seus controladors dependents i les classes de domini. Permet validar:

- ✅ Creació polimòrfica de respuestas
- ✅ Modificació amb validació de tipus
- ✅ Eliminació amb integritat referencial
- ✅ Consultes i llistats
- ✅ Validacions de domini en cascada
- ✅ Gestió d'IDs autoincrementals

El driver és completament **interactiu**, no requereix recompilació per provar nous datos, i maneja errors de forma robusta sense trencar l'execució.

---

**Versió:** 1.0  
**Data:** Novembre 2025  
**Estat:** ✅ Completament funcional
