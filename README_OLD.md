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

Ver reporte:
```bash
build/reports/tests/test/index.html
```

## Limpiar

```bash
./gradlew clean
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
- DESCRIPCION.txt: Casos de uso, datos, operativa...
- Archivos de datos (si aplica)
