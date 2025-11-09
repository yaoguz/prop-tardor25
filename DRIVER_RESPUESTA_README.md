# Driver Respuesta - Guía Rápida de Uso

## 🚀 Compilar y Ejecutar

```bash
# Compilar
cd /root/prop/subgrup-prop11.5
javac -d build/classes \
  src/main/java/drivers/DriverRespuesta.java \
  src/main/java/domaincontrollers/CtrlDominioRespuesta.java \
  src/main/java/domaincontrollers/CtrlDominioUsuario.java \
  src/main/java/domaincontrollers/CtrlDominioPregunta.java \
  src/main/java/domaincontrollers/CtrlDominioEncuesta.java \
  src/main/java/domain/*.java

# Ejecutar
java -cp build/classes drivers.DriverRespuesta
```

## 📋 Datos de Prueba Precargados

El driver crea automáticamente:

### Usuarios:
- **alice** (contraseña: pass123)
- **bob** (contraseña: pass123)
- **charlie** (contraseña: pass123)

### Encuesta:
**ID:** Variable (se muestra al inicio)  
**Título:** "Encuesta Hábitos Deportivos"  
**Autor:** alice

### Preguntas:

1. **Pregunta Numérica** (ID variable)
   - Enunciado: "¿Cuántas horas a la semana haces deporte?"
   - Rango: [0, 20]
   - Tipo de respuesta: `Double`

2. **Pregunta Opción Única** (ID variable)
   - Enunciado: "¿Cuál es tu nivel de actividad física?"
   - Opciones:
     - 0 = Bajo
     - 1 = Medio
     - 2 = Alto
   - Tipo de respuesta: `Integer` (índice)

3. **Pregunta Opción Múltiple** (ID variable)
   - Enunciado: "¿Qué deportes practicas? (selección múltiple)"
   - Opciones:
     - 0 = Fútbol
     - 1 = Baloncesto
     - 2 = Natación
     - 3 = Running
     - 4 = Ciclismo
   - Máximo de opciones: 5
   - Tipo de respuesta: `int[]` (array de índices)

4. **Pregunta Libre** (ID variable)
   - Enunciado: "Describe tu rutina de ejercicio favorita"
   - Tipo de respuesta: `String`

## 🎯 Ejemplos de Uso

### Ejemplo 1: Crear Respuesta Numérica

```
Seleccione una opción: 1

═══ CREAR RESPUESTA NUMÉRICA ═══
Usuarios disponibles: alice, bob, charlie
Usuario (nombre): alice

Pregunta: ¿Cuántas horas a la semana haces deporte?
Rango válido: [0, 20]
Respuesta (número): 10.5

✅ Respuesta numérica creada con ID: 1
   Usuario: alice
   Valor: 10.5
```

### Ejemplo 2: Crear Respuesta Opción Única

```
Seleccione una opción: 2

═══ CREAR RESPUESTA OPCIÓN ÚNICA ═══
Usuarios disponibles: alice, bob, charlie
Usuario (nombre): bob

Pregunta: ¿Cuál es tu nivel de actividad física?
Opciones disponibles:
  0. Bajo
  1. Medio
  2. Alto
Seleccione opción (índice): 2

✅ Respuesta opción única creada con ID: 2
   Usuario: bob
   Opción seleccionada: 2 (Alto)
```

### Ejemplo 3: Crear Respuesta Opción Múltiple

```
Seleccione una opción: 3

═══ CREAR RESPUESTA OPCIÓN MÚLTIPLE ═══
Usuarios disponibles: alice, bob, charlie
Usuario (nombre): charlie

Pregunta: ¿Qué deportes practicas? (selección múltiple)
Opciones disponibles:
  0. Fútbol
  1. Baloncesto
  2. Natación
  3. Running
  4. Ciclismo
Máximo de opciones: 5
Seleccione opciones (separadas por comas, ej: 0,2,4): 0,3,4

✅ Respuesta opción múltiple creada con ID: 3
   Usuario: charlie
   Opciones seleccionadas: Fútbol Running Ciclismo
```

### Ejemplo 4: Listar Todas las Respuestas

```
Seleccione una opción: 8

═══ TODAS LAS RESPUESTAS ═══
Total de respuestas: 3
─────────────────────────────────────────────────────────────
  [  1] NUM  | Usuario: alice      | Pregunta:   1 | 10.5
  [  2] OPU  | Usuario: bob        | Pregunta:   2 | opción 2
  [  3] OPM  | Usuario: charlie    | Pregunta:   3 | 3 opciones
```

### Ejemplo 5: Modificar Respuesta

```
Seleccione una opción: 5

═══ MODIFICAR RESPUESTA ═══
[Muestra listado de todas las respuestas]

ID de la respuesta a modificar: 1

Respuesta actual:
┌────────────────────────────────────────────────────────┐
│ ID: 1
│ Usuario: alice
│ Pregunta ID: 1
│ Enunciado: ¿Cuántas horas a la semana haces deporte?
│ Tipo: NUMÉRICA
│ Valor: 10.5
└────────────────────────────────────────────────────────┘

Nuevo valor numérico: 15

✅ Respuesta numérica modificada a: 15.0
```

### Ejemplo 6: Error de Validación

```
Seleccione una opción: 1

Usuario (nombre): alice
Respuesta (número): 25

❌ ERROR: El valor 25.0 está fuera del rango permitido [0, 20]
Tipo: IllegalArgumentException
```

## 🔍 Funcionalidades del Menú

| Opción | Acción | Entrada Requerida |
|--------|--------|-------------------|
| 1 | Crear respuesta numérica | Usuario, valor numérico |
| 2 | Crear respuesta opción única | Usuario, índice de opción |
| 3 | Crear respuesta opción múltiple | Usuario, índices separados por comas |
| 4 | Crear respuesta libre | Usuario, texto |
| 5 | Modificar respuesta | ID respuesta, nuevo valor |
| 6 | Eliminar respuesta | ID respuesta, confirmación |
| 7 | Consultar respuesta | ID respuesta |
| 8 | Listar TODAS las respuestas | - |
| 9 | Listar respuestas de usuario | Nombre usuario |
| 10 | Listar respuestas de pregunta | ID pregunta |
| 11 | Mostrar datos de prueba | - |
| 0 | Salir | - |

## ✅ Casos de Prueba Recomendados

### Prueba Básica (5 minutos)
1. Opción 11 → Ver IDs disponibles
2. Opción 1 → Crear respuesta numérica (alice, valor: 10)
3. Opción 2 → Crear respuesta opción única (bob, opción: 1)
4. Opción 8 → Listar todas (verificar las 2 creadas)
5. Opción 0 → Salir

### Prueba de Validación (10 minutos)
1. Opción 1 → Crear respuesta numérica con valor fuera de rango (ej: 25)
   - **Resultado esperado:** ❌ ERROR IllegalArgumentException
2. Opción 2 → Crear respuesta opción única con índice inválido (ej: 5)
   - **Resultado esperado:** ❌ ERROR ArrayIndexOutOfBoundsException
3. Opción 3 → Crear respuesta opción múltiple con más de 5 opciones
   - **Resultado esperado:** ❌ ERROR IllegalArgumentException

### Prueba de Integridad (15 minutos)
1. Crear respuestas para alice en las 4 preguntas
2. Opción 9 → Listar respuestas de alice (debe mostrar 4)
3. Opción 6 → Eliminar una respuesta de alice
4. Opción 9 → Listar respuestas de alice (debe mostrar 3)
5. Opción 10 → Listar respuestas de la pregunta eliminada (debe tener 1 menos)

### Prueba de Modificación (10 minutos)
1. Crear respuesta numérica (alice, valor: 5)
2. Opción 7 → Consultar la respuesta (ver ID)
3. Opción 5 → Modificar con nuevo valor (10)
4. Opción 7 → Consultar de nuevo (verificar cambio)
5. Opción 5 → Intentar modificar con tipo incorrecto (texto en vez de número)
   - **Resultado esperado:** ❌ ERROR TipoIncorrectoException

## 🐛 Solución de Problemas

### Error de compilación
```bash
# Asegúrate de que el directorio build/classes existe
mkdir -p build/classes

# Compila de nuevo
javac -d build/classes src/main/java/drivers/DriverRespuesta.java ...
```

### ClassNotFoundException al ejecutar
```bash
# Verifica que estás usando el classpath correcto
java -cp build/classes drivers.DriverRespuesta
```

### No aparecen las preguntas
```bash
# Usa opción 11 en el menú para ver los IDs de preguntas creados
# Los IDs pueden variar en cada ejecución
```

## 📚 Documentación Adicional

Ver `DRIVER_RESPUESTA_PRUEBAS.md` para:
- Descripción detallada de las pruebas
- Valores estudiados y particiones de equivalencia
- Efectos probados (polimorfismo, integridad referencial, etc.)
- Cobertura de métodos
- Criterios de éxito

---

**¡Listo para probar!** 🎉
