#!/bin/bash
# Script de compilacion para el proyecto PROP 11.5
# Usa Gradle para compilar todo el proyecto

echo "=========================================="
echo "  COMPILACION PROYECTO PROP 11.5"
echo "=========================================="
echo ""

# Verificar que estamos en el directorio correcto
if [ ! -f "build.gradle" ]; then
    echo "ERROR: No se encuentra build.gradle"
    echo "Ejecuta este script desde el directorio raiz del proyecto"
    exit 1
fi

echo "Compilando con Gradle..."
./gradlew build

if [ $? -eq 0 ]; then
    echo ""
    echo "=========================================="
    echo "  COMPILACION EXITOSA"
    echo "=========================================="
    echo ""
    echo "COMANDOS DISPONIBLES:"
    echo "  - Ejecutar tests:        ./gradlew test"
    echo "  - Ejecutar un driver:    ./gradlew run --args=\"NombreDriver\""
    echo "  - Ver tests en HTML:     build/reports/tests/test/index.html"
    echo "  - Limpiar proyecto:      ./gradlew clean"
    echo ""
else
    echo ""
    echo "=========================================="
    echo "  ERROR EN LA COMPILACION"
    echo "=========================================="
    echo ""
    echo "Revisa los errores arriba y corrigelos."
    exit 1
fi
