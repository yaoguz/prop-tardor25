# PROYECTO PROP - SUBGRUP 11.5

Sistema de gestión de encuestas con clustering de usuarios.

## Compilar

```bash
./compilar.sh
```

O manualmente:
```bash
./gradlew build
```

## Ejecutar Drivers

### Driver Usuario
```bash
./gradlew runDriverUsuario
```

### Driver Encuesta
```bash
./gradlew runDriverEncuesta
```

### Driver Pregunta
```bash
./gradlew runDriverPregunta
```

### Driver Respuesta
```bash
./gradlew runDriverRespuesta
```

### Driver Clustering
```bash
./gradlew runDriverClustering
```

## Ejecutar Tests

```bash
./gradlew test
```

Ver reporte:
```bash
build/reports/tests/test/index.html
```

## Estructura Entrega

```
DOCS/       - Documentación (UML, javadoc)
EXE/        - Drivers y datos de prueba
src/        - Código fuente
build.gradle - Configuración Gradle
compilar.sh - Script de compilación
```

## Descripción Drivers

Cada driver en EXE/ tiene:
- DESCRIPCION.txt: Casos de uso, datos, operativa
- Archivos de datos (si aplica)

Consulta EXE/DriverXXX/DESCRIPCION.txt para detalles de cada driver.
