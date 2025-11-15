# Instrucciones de Compilación y Ejecución

## Compilar el proyecto

```bash
./gradlew build
```

## Ejecutar los drivers interactivos

### Driver de Encuestas
```bash
./gradlew runDriverEncuesta
```

### Driver de Preguntas
```bash
./gradlew runDriverPregunta
```

### Driver de Respuestas
```bash
./gradlew runDriverRespuesta
```

### Driver de Usuarios
```bash
./gradlew runDriverUsuario
```

## Ejecutar los tests

```bash
./gradlew test
```

Ver reporte de tests:
```bash
open build/reports/tests/test/index.html
```

## Limpiar el proyecto

```bash
./gradlew clean
```

## Notas

- Los drivers son interactivos y permiten probar todas las funcionalidades del sistema
- Cada driver crea datos de prueba iniciales para facilitar las pruebas
- Los tests unitarios están en `src/test/java` y usan JUnit 4 + Mockito
- El código fuente principal está en `src/main/java`
