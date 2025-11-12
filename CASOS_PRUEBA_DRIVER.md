# Casos de Prueba - DriverRespuesta

## Pruebas Basicas

### Test 1: Crear respuesta numerica valida
```
Opcion: 1
Usuario: ana
Respuesta: 5

Resultado: Respuesta creada con ID: 1
```

### Test 2: Crear respuesta numerica FUERA de rango
```
Opcion: 1
Usuario: ana
Respuesta: 50

Resultado: ERROR: Valor 50.0 fuera de rango [0, 40]
```

### Test 3: Crear respuesta numerica con formato invalido
```
Opcion: 1
Usuario: ana
Respuesta: abc

Resultado: ERROR: Debes introducir un numero valido.
```

### Test 4: Crear respuesta opcion unica
```
Opcion: 2
Usuario: pol
Indice: 1

Resultado: Respuesta creada con ID: 2
```

### Test 5: Crear respuesta opcion unica con indice invalido
```
Opcion: 2
Usuario: pol
Indice: 10

Resultado: ERROR: Indice de opcion invalido
```

### Test 6: Crear respuesta opcion multiple
```
Opcion: 3
Usuario: joan
Opciones: 0,2,4

Resultado: Respuesta creada con ID: 3
```

### Test 7: Crear respuesta opcion multiple excediendo limite
```
Opcion: 3
Usuario: joan
Opciones: 0,1,2,3,4,5

Resultado: ERROR: Has seleccionado mas opciones del maximo permitido
```

### Test 8: Crear respuesta libre
```
Opcion: 4
Usuario: ana
Respuesta: Hago running por las mananas y voy al gym 3 veces por semana

Resultado: Respuesta creada con ID: 4
```

---

## Pruebas de Modificacion

### Test 9: Modificar respuesta numerica
```
Opcion: 5
ID: 1
Nuevo valor: 7.5

Resultado: Respuesta modificada.
```

### Test 10: Modificar respuesta con valor fuera de rango
```
Opcion: 5
ID: 1
Nuevo valor: 100

Resultado: ERROR: Valor 100.0 fuera de rango [0, 40]
```

### Test 11: Modificar respuesta de opcion unica
```
Opcion: 5
ID: 2
Nuevo indice: 2

Resultado: Respuesta modificada.
```

---

## Pruebas de Eliminacion

### Test 12: Eliminar respuesta confirmando
```
Opcion: 6
ID: 3
Confirmar: s

Resultado: Respuesta eliminada.
```

### Test 13: Eliminar respuesta cancelando
```
Opcion: 6
ID: 4
Confirmar: n

Resultado: Eliminacion cancelada.
```

### Test 14: Eliminar respuesta inexistente
```
Opcion: 6
ID: 999

Resultado: ERROR: Respuesta con ID 999 no existe
```

---

## Pruebas de Consulta

### Test 15: Consultar respuesta existente
```
Opcion: 7
ID: 1

Resultado:
ID: 1
Usuario: ana
ID pregunta: 1
Enunciado: Cuantas horas a la semana haces deporte?
Tipo: numerica
Valor: 7.5
```

### Test 16: Listar todas las respuestas
```
Opcion: 8

Resultado:
Total: 3
  [  1] NUM  | Usuario: ana        | Pregunta:   1 | 7.5
  [  2] OPU  | Usuario: pol        | Pregunta:   2 | opcion 2
  [  4] TXT  | Usuario: ana        | Pregunta:   4 | Hago running por las mana...
```

### Test 17: Listar respuestas de un usuario
```
Opcion: 9
Usuario: ana

Resultado:
Respuestas de ana: 2
  [  1] NUM  | Usuario: ana        | Pregunta:   1 | 7.5
  [  4] TXT  | Usuario: ana        | Pregunta:   4 | Hago running por las mana...
```

### Test 18: Listar respuestas de usuario sin respuestas
```
Opcion: 9
Usuario: joan

Resultado:
El usuario no tiene respuestas.
```

### Test 19: Listar respuestas de una pregunta
```
Opcion: 10
ID pregunta: 1

Resultado:
Pregunta: Cuantas horas a la semana haces deporte?
Respuestas: 2
  [  1] NUM  | Usuario: ana        | Pregunta:   1 | 7.5
  [  5] NUM  | Usuario: pol        | Pregunta:   1 | 3.0
```

---

## Casos Extremos

### Test 20: Respuesta numerica en el limite inferior
```
Usuario: ana
Respuesta: 0

Resultado: Respuesta creada con ID: 6
```

### Test 21: Respuesta numerica en el limite superior
```
Usuario: ana
Respuesta: 40

Resultado: Respuesta creada con ID: 7
```

### Test 22: Respuesta libre vacia
```
Usuario: ana
Respuesta: 

Resultado: ERROR: El texto no puede estar vacio
```

### Test 23: Opciones con formato incorrecto
```
Usuario: ana
Opciones: 0, 2, 4

Resultado: ERROR: Formato incorrecto. Usa numeros separados por comas.
(Nota: funciona si usas .trim() al hacer split)
```

### Test 24: Opcion multiple con indices duplicados
```
Usuario: ana
Opciones: 0,0,1,1

Resultado: Respuesta creada (pero con duplicados eliminados internamente)
```

---

## Flujo Completo de Prueba

1. Ejecutar driver: `java drivers.DriverRespuesta`
2. Crear respuestas de cada tipo (opciones 1-4)
3. Listar todas (opcion 8) para ver IDs
4. Modificar algunas (opcion 5)
5. Consultar individual (opcion 7)
6. Listar por usuario (opcion 9)
7. Listar por pregunta (opcion 10)
8. Eliminar alguna (opcion 6)
9. Verificar que se elimino (opcion 8)

---

## Notas para el Informe

### Excepciones Capturadas

1. `NumberFormatException` - cuando introduces texto en vez de numero
2. `IllegalArgumentException` - cuando:
   - Valor numerico fuera de rango
   - Indice de opcion invalido
   - Usuario no existe
   - Pregunta no existe
   - Respuesta no existe
   - Texto vacio en respuesta libre
   - Numero de opciones excede el maximo

### Integridad Referencial

- Al eliminar una respuesta, se elimina tambien de:
  - TreeMap de respuestas
  - Lista de respuestas del usuario
  - Lista de respuestas de la pregunta

Verificable con:
```
1. Crear respuesta (usuario ana, pregunta 1)
2. Listar respuestas de ana → sale
3. Listar respuestas de pregunta 1 → sale
4. Eliminar respuesta
5. Listar respuestas de ana → NO sale
6. Listar respuestas de pregunta 1 → NO sale
```
