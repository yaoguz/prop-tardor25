# Driver de Clustering - Guía de Uso

## Descripción

El **DriverClustering** es un driver interactivo que permite probar y demostrar todas las funcionalidades de análisis de clustering del sistema de encuestas.

## Funcionalidades Implementadas

### ✅ Requisitos Mínimos Cubiertos

1. **Análisis de encuestas:**
   - Obtención de diferentes comportamientos o perfiles de usuarios
   - Obtención de representantes de cada grupo
   - Listado de todos los individuos de cada comportamiento o perfil

2. **Evaluación de calidad:**
   - Cálculo del coeficiente de silueta
   - Interpretación automática de la calidad del clustering
   - Comparación entre algoritmos (K-Means vs K-Means++)

### 🚀 Características Adicionales

- Importación de encuestas y respuestas desde archivos JSON
- Visualización clara de resultados con tablas y símbolos
- Comparación automática de algoritmos
- Estado del sistema en tiempo real

## Cómo Ejecutar

### Opción 1: Con Gradle (Recomendado)

```bash
./gradlew runDriverClustering
```

### Opción 2: Manual

```bash
cd src/main/java
java drivers.DriverClustering
```

## Flujo de Trabajo Típico

### 1. Preparar Datos

Antes de usar el driver, necesitas:
- Un archivo JSON con la encuesta (ya existe: `datos-encuestas.json`)
- Un archivo JSON con respuestas (ya existe: `datos-respuestas.json`)
- Usuarios registrados en el sistema

### 2. Usar el Driver

#### Paso 1: Importar Encuesta
```
Opción 1: Importar encuesta desde JSON
- Usuario: admin (o cualquier usuario existente)
- Archivo: datos-encuestas.json
```

#### Paso 2: Importar Respuestas
```
Opción 2: Importar respuestas desde JSON
- Archivo: datos-respuestas.json
```

#### Paso 3: Ejecutar Clustering
```
Opción 3: Ejecutar clustering
- ID encuesta: 2 (el que se importó)
- Número de clusters (k): 3 (recomendado para los datos de ejemplo)
- Algoritmo: K-Means++ (opción 2)
```

#### Paso 4: Ver Resultados
```
Opción 4: Ver perfiles obtenidos
Opción 5: Ver representantes de grupos
Opción 6: Listar individuos de un perfil específico
Opción 7: Evaluar calidad del clustering
```

### 3. Comparar Algoritmos

```
Opción 8: Comparar K-Means vs K-Means++
```

Esto ejecutará ambos algoritmos con los mismos parámetros y comparará:
- Tiempo de ejecución
- Calidad del clustering (coeficiente de silueta)

## Archivos de Datos Incluidos

### `datos-encuestas.json`

Encuesta sobre información personal y financiera con 13 preguntas:
- Preguntas de opción única (género, estado civil, educación, etc.)
- Preguntas numéricas (ingresos, monto del préstamo, etc.)
- Preguntas de opción múltiple (finalidad del préstamo)
- Pregunta libre (situación laboral)

### `datos-respuestas.json`

8 usuarios de prueba (user1 a user8) con respuestas completas a la encuesta.
Los usuarios están diseñados para formar clusters naturales:

- **Perfil 1 (Profesionales urbanos):** user1, user4, user7
  - Graduados, ingresos medios-altos, zona urbana
  
- **Perfil 2 (Emprendedores):** user2, user5, user8
  - Autoempleados, ingresos altos, graduados
  
- **Perfil 3 (Familias rurales):** user3, user6
  - Múltiples dependientes, ingresos bajos, zona rural/semiurbana

## Interpretación de Resultados

### Coeficiente de Silueta

El coeficiente de silueta mide la calidad del clustering en escala [-1, 1]:

- **0.7 - 1.0:** EXCELENTE - Estructura de clusters muy fuerte
- **0.5 - 0.7:** BUENO - Estructura de clusters razonable
- **0.25 - 0.5:** ACEPTABLE - Estructura de clusters débil
- **0.0 - 0.25:** POBRE - Estructura muy débil o artificial
- **< 0.0:** MALO - Objetos mal asignados a clusters

### Ejemplo de Salida

```
📊 RESUMEN DE PERFILES:
   Perfil 1: 3 miembros
      Representante: user1
   Perfil 2: 3 miembros
      Representante: user2
   Perfil 3: 2 miembros
      Representante: user3

📈 Calidad del clustering:
   Coeficiente de silueta: 0.6543
   Interpretación: BUENO: Estructura de clusters razonable
```

## Troubleshooting

### Error: "No hay encuestas"
**Solución:** Importa primero una encuesta (opción 1)

### Error: "La encuesta no tiene respuestas"
**Solución:** Importa respuestas (opción 2) antes de ejecutar clustering

### Error: "k debe estar entre 2 y N"
**Solución:** Elige un valor de k menor que el número de usuarios con respuestas

### Error: "El archivo no existe"
**Solución:** Asegúrate de estar en el directorio raíz del proyecto y usar rutas relativas:
- `datos-encuestas.json`
- `datos-respuestas.json`

## Implementación Técnica

### Algoritmos

- **K-Means:** Inicialización aleatoria de centroides
- **K-Means++:** Inicialización inteligente (centroides bien separados)

### Cálculo de Distancias

El sistema soporta 5 tipos de distancias según el tipo de pregunta:

1. **Numérica:** Distancia euclidiana normalizada
2. **Opción única ordenada:** Diferencia de posiciones normalizada
3. **Opción única no ordenada:** 0 si igual, 1 si diferente
4. **Opción múltiple:** Índice de Jaccard
5. **Libre:** Distancia de Levenshtein normalizada

### Evaluación

- **Coeficiente de Silueta:** Mide qué tan bien están agrupados los objetos
  - Compara distancia intra-cluster con distancia inter-cluster
  - Promedio sobre todos los individuos

## Referencias

- **Documentación completa:** Ver `CLUSTERING_EXPLICACION.md`
- **Código fuente:**
  - Algoritmos: `src/main/java/domain/algorithm/`
  - Controladores: `src/main/java/domaincontrollers/CtrlDominioPerfil.java`
  - Evaluador: `src/main/java/domain/algorithm/funciones/ClusterEvaluator.java`

## Autores

- Alessandro Jin (Algoritmos de clustering y driver)
- Equipo PROP 11.5

---

**Fecha:** Noviembre 2025  
**Versión:** 1.0
