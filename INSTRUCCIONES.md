# Instrucciones Compilación y Ejecución

## Compilar

```bash
./compilar.sh
```

## Ejecutar Drivers

```bash
./gradlew runDriverUsuario
./gradlew runDriverEncuesta
./gradlew runDriverPregunta
./gradlew runDriverRespuesta
./gradlew runDriverClustering
```

## Ejecutar Tests

```bash
./gradlew test
```

Ver reporte: `build/reports/tests/test/index.html`

## Limpiar

```bash
./gradlew clean
```

## Notas

- Los drivers son interactivos (menú por consola)
- Consulta EXE/DriverXXX/DESCRIPCION.txt para detalles de cada driver
- Los paths de archivos deben ser relativos (ej: EXE/DriverClustering/datos.json)
- Cada driver crea datos de prueba iniciales para facilitar las pruebas
- Los tests unitarios están en `src/test/java` y usan JUnit 4 + Mockito
- El código fuente principal está en `src/main/java`
