# GUÍA PARA LA PRIMERA ENTREGA - SUBGRUP PROP 11.5

## 1. PROBLEMAS DE COMPILACIÓN Y SOLUCIÓN

### Problema: Errores con Gson y Map
Los errores que ves ocurren porque estás compilando **manualmente con javac**, que no tiene acceso a las librerías definidas en Gradle (como Gson).

**SOLUCIÓN: Usa Gradle siempre**

### No necesitas javac, ni makefiles, ni .jar manuales con Gradle

Gradle gestiona automáticamente:
- Descarga de librerías (Gson, JUnit, etc.)
- Compilación de todo el proyecto
- Ejecución de tests
- Creación de executables

---

## 2. CÓMO USAR GRADLE

### 2.1. Compilar el proyecto completo
```bash
cd /root/prop/subgrup-prop11.5
./gradlew build
```
Esto compila todo y ejecuta tests automáticamente.

### 2.2. Compilar sin ejecutar tests
```bash
./gradlew compileJava
```

### 2.3. Ejecutar tests unitarios
```bash
# Todos los tests
./gradlew test

# Ver reporte HTML de tests
./gradlew test --info
# Reporte en: build/reports/tests/test/index.html
```

### 2.4. Ejecutar un driver específico
```bash
# Opción 1: Usando Gradle (RECOMENDADO)
./gradlew run --args="DriverEncuesta"

# Opción 2: Compilar y ejecutar manualmente (más complejo)
./gradlew compileJava
java -cp "build/classes/java/main:lib/*" drivers.DriverEncuesta
```

### 2.5. Limpiar el proyecto
```bash
./gradlew clean
```

### 2.6. Crear un JAR ejecutable
```bash
./gradlew jar
# El JAR estará en: build/libs/
```

---

## 3. ¿POR QUÉ NO USAR javac DIRECTAMENTE?

Cuando usas `javac drivers/DriverRespuesta.java`:
- ❌ No tiene acceso a las librerías (Gson está en el repositorio Maven, no en tu disco)
- ❌ No gestiona dependencias entre clases automáticamente
- ❌ Tienes que configurar manualmente el classpath

Gradle hace todo eso automáticamente porque:
- ✅ Lee `build.gradle` y descarga Gson de Maven Central
- ✅ Compila todas las clases en el orden correcto
- ✅ Configura el classpath automáticamente

---

## 4. ESTRUCTURA DEL PROYECTO PARA LA PRIMERA ENTREGA

### 4.1. Estructura actual (con Gradle)
```
subgrup-prop11.5/
├── build.gradle              # Configuración de Gradle (dependencias, etc.)
├── settings.gradle           # Nombre del proyecto
├── gradlew                   # Script de Gradle para Linux/Mac
├── gradlew.bat               # Script de Gradle para Windows
├── equipos.txt               # CREAR: Nombres y emails del equipo
├── index.txt                 # CREAR: Descripción del directorio raíz
│
├── DOCS/                     # CREAR: Documentación para la entrega
│   ├── index.txt
│   ├── casos_uso.pdf         # Diagrama con Rational Rose
│   ├── casos_uso_desc.pdf    # Descripción de casos de uso
│   ├── diagrama_clases.pdf   # Diagrama estático (diseño)
│   ├── descripcion_clases.pdf # Atributos y métodos de cada clase
│   ├── reparto_trabajo.pdf   # Quién implementó qué clase
│   └── estructuras_algoritmos.pdf # Descripción de K-means, HashMap, etc.
│
├── FONTS/                    # CREAR: Código fuente (todo src/)
│   ├── index.txt
│   ├── compilar.sh           # Script de compilación (./gradlew build)
│   └── src/
│       ├── main/
│       │   └── java/
│       │       ├── domain/          # Clases del modelo
│       │       ├── domaincontrollers/ # Controladores de dominio
│       │       ├── persistence/     # GestorPersistencia
│       │       └── drivers/         # Drivers interactivos
│       └── test/
│           └── java/
│               └── (tests unitarios JUnit)
│
├── EXE/                      # CREAR: Ejecutables y jocs de proves
│   ├── index.txt
│   ├── DriverEncuesta/
│   │   ├── index.txt         # Descripción del joc de proves
│   │   ├── ejecutar.sh       # ./gradlew run --args="DriverEncuesta"
│   │   └── datos_prueba.json # Datos de prueba (si aplica)
│   ├── DriverPregunta/
│   ├── DriverRespuesta/
│   ├── DriverUsuario/
│   └── DriverClustering/
│
├── src/                      # Código fuente (ya existe)
├── build/                    # Generado por Gradle (no entregar)
├── gradle/                   # Wrapper de Gradle
└── lib/                      # Librerías externas (si las hay)
```

### 4.2. Qué CREAR para la entrega

#### A. Directorio raíz
- ✅ `equipos.txt`: Nombres, emails @estudiantat.upc.edu
- ✅ `index.txt`: Descripción del proyecto y estructura

#### B. DOCS/
- ⬜ Diagrama de casos de uso (Rational Rose .mdl + PDF)
- ⬜ Descripción de casos de uso (Word/PDF)
- ⬜ Diagrama de clases completo diseño (Rational Rose .mdl + PDF)
- ⬜ Descripción de clases (atributos, métodos, responsabilidades)
- ⬜ Reparto de trabajo por miembro
- ⬜ Descripción de estructuras de datos (HashMap, ArrayList, TreeMap) y algoritmos (K-means, K-means++)

#### C. FONTS/
- ✅ Copiar todo `src/` actual
- ✅ Crear `compilar.sh` con: `./gradlew build`
- ✅ Incluir `build.gradle`, `settings.gradle`, `gradlew`, `gradlew.bat`

#### D. EXE/
- ⬜ Subdirectorio por cada driver
- ⬜ Scripts ejecutables (ej: `./gradlew run --args="DriverUsuario"`)
- ⬜ Descripción del joc de proves (ver sección 6)

---

## 5. CHECKLIST COMPLETO PRIMERA ENTREGA

### DOCUMENTACIÓN (DOCS/)
- [ ] **Casos de uso**: Diagrama (.mdl de Rational Rose + PDF exportado)
- [ ] **Descripción casos de uso**: Documento Word/PDF con cada caso
- [ ] **Diagrama de clases**: Diseño completo (.mdl + PDF)
  - Incluir: domain, domaincontrollers, persistence, algorithm
  - Mostrar: atributos, métodos (sin getters/setters simples), relaciones
- [ ] **Descripción de clases**: Documento con cada clase
  - Responsabilidades
  - Atributos principales (tipo, significado)
  - Métodos principales (parámetros, retorno, qué hace)
- [ ] **Reparto de trabajo**: Tabla con:
  - Miembro del equipo | Clases implementadas
- [ ] **Estructuras de datos y algoritmos**:
  - HashMap: para almacenar usuarios, encuestas, preguntas, respuestas (O(1) acceso)
  - ArrayList: para listas de IDs (idPreguntas, idRespuestas, etc.)
  - K-means: algoritmo de clustering con inicialización aleatoria
  - K-means++: mejora de K-means con inicialización inteligente
  - Descripción de cálculo de distancias, centroides, etc.

### CÓDIGO (FONTS/)
- [x] **domain/**: Todas las clases del modelo
  - [x] Usuario, Encuesta, Pregunta (jerárquica), Respuesta (jerárquica)
  - [x] Perfil, Cluster, Centroide, ComponenteCentroide (jerárquico)
- [x] **domaincontrollers/**: Controladores de dominio
  - [x] CtrlDominioUsuario
  - [x] CtrlDominioEncuesta
  - [x] CtrlDominioPregunta
  - [x] CtrlDominioRespuesta
  - [x] CtrlDominio (facade)
- [x] **persistence/**: GestorPersistencia (HashMap storage)
- [x] **algorithm/**: Algoritmos de clustering
  - [x] ClusteringAlgorithm (interfaz)
  - [x] Kalgorithm (clase abstracta)
  - [x] Kmeans, KmeansPlusPlus
  - [x] Funciones: CalculoCentroide, CalculoDistanciaCentroide, calculoDistanciaGlobal
- [ ] **drivers/**: Drivers interactivos (¿cuántos hay actualmente?)
  - [ ] DriverUsuario (registro, login, logout)
  - [ ] DriverEncuesta (crear, importar JSON)
  - [ ] DriverPregunta (añadir preguntas)
  - [ ] DriverRespuesta (dar respuestas, importar CSV)
  - [ ] DriverClustering (ejecutar K-means/K-means++)
- [ ] **compilar.sh**: Script con `./gradlew build`

### TESTS UNITARIOS (FONTS/src/test/)
- [ ] **JUnit tests** para cada clase del modelo:
  - [ ] TestUsuario
  - [ ] TestEncuesta
  - [ ] TestPreguntaNumerica, TestPreguntaOpcionUnica, etc.
  - [ ] TestRespuestaNumerica, TestRespuestaOpcionUnica, etc.
  - [ ] TestKmeans, TestKmeansPlusPlus
  - [ ] TestCalculoCentroide, TestCalculoDistancia
  - [ ] (Usar stubs/mocks si es necesario)
- [ ] **Cobertura alta**: casos convencionales + casos extremos
  - Valores límite (min/max)
  - Entradas vacías/nulas
  - Excepciones esperadas

### EJECUTABLES (EXE/)
- [ ] **Subdirectorio por driver** con:
  - [ ] `index.txt`: Descripción del joc de proves (ver plantilla abajo)
  - [ ] `ejecutar.sh` o `ejecutar.bat`: Script para ejecutar
  - [ ] Datos de prueba (JSON, CSV, etc.) si aplica
- [ ] **Funcionalidades principales a demostrar**:
  1. Gestión de usuarios (registro, login)
  2. Creación/importación de encuestas
  3. Añadir preguntas a encuestas
  4. Responder encuestas / importar respuestas
  5. Ejecutar clustering (K-means, K-means++)

### EXTRAS
- [x] **build.gradle**: Con dependencias (Gson, JUnit)
- [x] **settings.gradle**: Nombre del proyecto
- [x] **gradlew, gradlew.bat**: Wrappers de Gradle
- [ ] **equipos.txt**: En el raíz
- [ ] **index.txt**: En raíz y cada directorio

---

## 6. PLANTILLA DESCRIPCIÓN JOCS DE PROVES (index.txt en EXE/DriverXXX/)

```
DRIVER: DriverEncuesta
=====================

OBJETO DE LA PRUEBA:
- Casos de uso: Crear encuesta, importar encuesta desde JSON
- Clases probadas: CtrlDominioEncuesta, Encuesta, Pregunta (jerarquía completa)

OTROS ELEMENTOS INTEGRADOS:
- Usuario (ya probado en DriverUsuario)
- GestorPersistencia (probado indirectamente)
- CtrlDominioPregunta (integrado para añadir preguntas)

FICHEROS DE DATOS NECESARIOS:
- datos-encuestas.json: Encuesta con 13 preguntas (5 opcion_unica, 4 numerica, 
  1 opcion_multiple, 1 libre, 1 ordenada)

VALORES ESTUDIADOS:
- Caja negra: JSON válido vs JSON con formato incorrecto
- Títulos duplicados (mismo autor no puede tener 2 encuestas con igual título)
- Usuario activo vs sin usuario activo (debe dar excepción)
- Preguntas con distintos tipos y atributos

EFECTOS ESTUDIADOS:
- Creación correcta de encuesta con todas las preguntas
- Validación de restricción de título único por autor
- Excepción cuando no hay usuario activo
- Mensajes de error claros

OPERATIVA:
1. Ejecutar: ./ejecutar.sh (o ./gradlew run --args="DriverEncuesta")
2. El driver solicita login (usar usuario de prueba)
3. Menú interactivo:
   - Opción 1: Crear encuesta manualmente
   - Opción 2: Importar encuesta desde JSON
   - Opción 3: Listar encuestas del usuario
   - Opción 4: Salir
4. Probar importación con datos-encuestas.json
5. Verificar que no permite crear encuesta con título duplicado
```

---

## 7. SCRIPTS DE EJECUCIÓN PARA EXE/

### EXE/DriverUsuario/ejecutar.sh
```bash
#!/bin/bash
cd ../../
./gradlew run --args="DriverUsuario"
```

### EXE/DriverEncuesta/ejecutar.sh
```bash
#!/bin/bash
cd ../../
./gradlew run --args="DriverEncuesta"
```

### FONTS/compilar.sh
```bash
#!/bin/bash
# Script de compilación para el proyecto PROP 11.5
echo "Compilando proyecto con Gradle..."
./gradlew build

echo ""
echo "Compilación completada."
echo "Para ejecutar tests: ./gradlew test"
echo "Para ejecutar drivers: ./gradlew run --args=\"NombreDriver\""
```

---

## 8. CÓMO EJECUTAR TODO

### Compilar
```bash
cd /root/prop/subgrup-prop11.5
./gradlew build
```

### Ejecutar tests
```bash
./gradlew test
# Ver reporte: build/reports/tests/test/index.html
```

### Ejecutar un driver
```bash
./gradlew run --args="DriverUsuario"
```

### Ver qué drivers hay
```bash
ls src/main/java/drivers/
```

---

## 9. NOTAS IMPORTANTES

1. **NO entregar build/**: Es código generado, pesa mucho y no es necesario
2. **NO usar javac manualmente**: Gradle gestiona todo
3. **NO necesitas makefiles**: Gradle reemplaza make
4. **NO añadas .jar manualmente**: Gradle descarga librerías automáticamente
5. **SÍ incluye gradlew y gradlew.bat**: Son necesarios para compilar sin instalar Gradle
6. **SÍ incluye build.gradle**: Define las dependencias del proyecto
7. **Ortografía**: Revisa bien todos los documentos
8. **index.txt**: Sin acentos, formato texto plano
9. **Paths relativos**: Nunca uses /root/prop/... en scripts, usa ./

---

## 10. ESTRUCTURA FINAL PARA COMPRIMIR Y ENTREGAR

```
subgrup-prop11.5.zip
├── equipos.txt
├── index.txt
├── DOCS/
│   ├── index.txt
│   ├── (todos los PDFs y .mdl)
├── FONTS/
│   ├── index.txt
│   ├── compilar.sh
│   ├── build.gradle
│   ├── settings.gradle
│   ├── gradlew
│   ├── gradlew.bat
│   ├── gradle/
│   └── src/
│       ├── main/java/...
│       └── test/java/...
└── EXE/
    ├── index.txt
    ├── DriverUsuario/
    ├── DriverEncuesta/
    ├── DriverPregunta/
    ├── DriverRespuesta/
    └── DriverClustering/
```

**NO incluir**: build/, .git/, .gradle/, archivos temporales

---

## 11. SIGUIENTE PASO INMEDIATO

1. ✅ **Arreglar imports** (ya hecho)
2. ⬜ **Crear drivers interactivos** si faltan
3. ⬜ **Crear tests JUnit** para todas las clases
4. ⬜ **Crear documentación** (DOCS/)
5. ⬜ **Organizar ejecutables** (EXE/)
6. ⬜ **Crear equipos.txt e index.txt**

---

## 12. CONTACTO CON TUTOR

Antes de entregar, consulta con el tutor sobre:
- ¿Puedo usar Gson? (ya viene con Java estándar moderno, pero mejor confirmar)
- ¿Formato de documentos: PDF o DOC?
- ¿Diagramas: entregar .mdl de Rational Rose o solo PDF?
- ¿Algún requisito específico para vuestro grupo?
