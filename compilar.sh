#!/bin/bash
# Compilación proyecto PROP 11.5

echo "Compilando..."
./gradlew build

if [ $? -eq 0 ]; then
    echo ""
    echo "COMPILACION EXITOSA"
    echo ""
    echo "Comandos disponibles:"
    echo "  ./gradlew test                  - Ejecutar tests"
    echo "  ./gradlew runDriverUsuario      - Driver Usuario"
    echo "  ./gradlew runDriverEncuesta     - Driver Encuesta"
    echo "  ./gradlew runDriverPregunta     - Driver Pregunta"
    echo "  ./gradlew runDriverRespuesta    - Driver Respuesta"
    echo "  ./gradlew runDriverClustering   - Driver Clustering"
    echo ""
else
    echo ""
    echo "ERROR EN COMPILACION"
    exit 1
fi
