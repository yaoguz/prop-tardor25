# Distribución de Trabajo - Clustering

> **Recomendación:** Distribuir el trabajo entre 4 personas según expertise

---

## 👥 Asignación de Tareas

### 🧑‍💻 **Persona 1: Matemáticas y Distancias**
**Perfil:** Bueno con matemáticas, normalización, fórmulas  
**Tiempo estimado:** 25 minutos  
**Archivos:** `DistanceCalculator.java`, `CentroidCalculator.java`

#### Tareas:
- [ ] **TODO #1:** Implementar normalización dinámica en `distanciaNumerica()`
  - Obtener Vmax/Vmin de `PreguntaNumerica`
  - Aplicar fórmula: `|v1 - v2| / (Vmax - Vmin)`
  - Manejar caso `Vmax == Vmin`

- [ ] **TODO #2:** Implementar `calcularCentroideNumerico()`
  - Calcular media aritmética: `suma / n`
  - Manejar lista vacía

#### Tests a escribir:
```java
@Test
public void testDistanciaNumerica() {
    // Pregunta: rango [0, 10]
    // Usuario 1: 3, Usuario 2: 7
    // Esperado: |3-7| / 10 = 0.4
}

@Test
public void testCentroideNumerico() {
    // Respuestas: [5.0, 10.0, 15.0]
    // Esperado: 10.0
}
```

---

### 🧑‍💻 **Persona 2: Estadística y Modas**
**Perfil:** Bueno con estructuras de datos, maps, frecuencias  
**Tiempo estimado:** 75 minutos  
**Archivos:** `CentroidCalculator.java`

#### Tareas:
- [ ] **TODO #3:** Implementar `calcularCentroideOpcionUnica()`
  - Contar frecuencias con `HashMap<Integer, Integer>`
  - Encontrar moda (opción más frecuente)
  - Resolver empates (elegir índice menor)

- [ ] **TODO #4:** Implementar `calcularCentroideOpcionMultiple()`
  - Contar frecuencias de cada opción en array `int[]`
  - Filtrar las que aparecen en >50% de respuestas
  - Devolver como `int[]`

- [ ] **TODO #5:** Implementar `calcularCentroideLibre()` (opcional, puede ser simple)
  - Versión simple: Contar strings exactos, devolver el más frecuente
  - Versión avanzada (opcional): Calcular "centro" con similitud de strings

#### Tests a escribir:
```java
@Test
public void testCentroideOpcionUnica() {
    // Respuestas: [1, 1, 2, 3]
    // Esperado: 1 (más frecuente)
}

@Test
public void testCentroideOpcionMultiple() {
    // Respuestas: [{0,1}, {1,2}, {0,1}]
    // 0 aparece 2/3, 1 aparece 3/3, 2 aparece 1/3
    // Esperado: [0, 1] (aparecen >50%)
}
```

---

### 🧑‍💻 **Persona 3: Arquitectura e Integración**
**Perfil:** Conoce bien el sistema, controladores, flujo de datos  
**Tiempo estimado:** 90 minutos  
**Archivos:** `CtrlDominioPerfil.java`, `KMeans.java`

#### Tareas:
- [ ] **TODO #6:** Implementar `obtenerRespuestasPorUsuario()`
  - Obtener respuestas de la encuesta desde `CtrlDominioRespuesta`
  - Agrupar por usuario en `Map<String, List<Respuesta>>`
  - Manejar usuarios sin respuestas

- [ ] **TODO #7:** Implementar `calcularDistanciaAlCentroide()` en KMeans
  - Para cada pregunta, calcular distancia entre respuesta y valor centroide
  - Sumar todas las distancias
  - Usar `DistanceCalculator` o cálculo directo

- [ ] **TODO #8:** Implementar `calcularMediaRespuestas()` en KMeans
  - Para cada pregunta, agrupar respuestas de los miembros del cluster
  - Usar `CentroidCalculator` según tipo de respuesta
  - Convertir resultado a `List<Double>` (centroide)

#### Tests a escribir:
```java
@Test
public void testObtenerRespuestasPorUsuario() {
    // Encuesta 1 con usuarios: alice, bob
    // alice: [resp1, resp2], bob: [resp3, resp4]
    // Esperado: Map con 2 entradas
}

@Test
public void testCalcularMediaRespuestas() {
    // Cluster con 3 usuarios, 2 preguntas numéricas
    // Usuario1: [5.0, 10.0], Usuario2: [3.0, 8.0], Usuario3: [4.0, 9.0]
    // Esperado: [4.0, 9.0] (medias)
}
```

---

### 🧑‍💻 **Persona 4: Evaluación y Testing**
**Perfil:** Le gustan los algoritmos, testing, métricas  
**Tiempo estimado:** 120 minutos  
**Archivos:** `ClusterEvaluator.java`, tests

#### Tareas:
- [ ] **TODO #9:** Implementar `calcularCoeficienteSilueta()`
  - Para cada usuario, calcular `a(i)` (distancia media intra-cluster)
  - Para cada usuario, calcular `b(i)` (distancia al cluster más cercano)
  - Calcular `s(i) = (b - a) / max(a, b)`
  - Promedio de todos los `s(i)`

- [ ] Escribir tests para **todas** las clases del módulo clustering:
  - `DistanceCalculator` (5 tipos de distancia)
  - `CentroidCalculator` (4 tipos de centroide)
  - `KMeans` (clustering básico)
  - `KMeansPlusPlus` (clustering mejorado)
  - `ClusterEvaluator` (silueta)

#### Tests a escribir:
```java
@Test
public void testSiluetaClusteringPerfecto() {
    // Clusters muy separados
    // Esperado: ~1.0
}

@Test
public void testSiluetaClusteringMalo() {
    // Clusters mezclados
    // Esperado: ~0 o negativo
}

@Test
public void testKMeansConvergencia() {
    // 10 usuarios, k=2
    // Verificar que converge en <20 iteraciones
}
```

---

## 📋 Checklist por Persona

### ✅ Persona 1
- [ ] Modificar `distanciaNumerica()` para obtener Vmax/Vmin dinámicamente
- [ ] Implementar `calcularCentroideNumerico()` con media aritmética
- [ ] Test: distancia numérica con diferentes rangos
- [ ] Test: centroide numérico con lista vacía
- [ ] Test: centroide numérico con valores negativos

### ✅ Persona 2
- [ ] Implementar `calcularCentroideOpcionUnica()` con HashMap
- [ ] Implementar `calcularCentroideOpcionMultiple()` con frecuencias
- [ ] Implementar `calcularCentroideLibre()` (versión simple)
- [ ] Test: moda con empates
- [ ] Test: opción múltiple sin opciones >50%
- [ ] Test: texto libre con empates

### ✅ Persona 3
- [ ] Implementar `obtenerRespuestasPorUsuario()` consultando controlador respuestas
- [ ] Implementar `calcularDistanciaAlCentroide()` sumando distancias
- [ ] Implementar `calcularMediaRespuestas()` usando CentroidCalculator
- [ ] Test: obtener respuestas de encuesta vacía
- [ ] Test: distancia de usuario a centroide
- [ ] Test: recalcular centroide después de reasignación

### ✅ Persona 4
- [ ] Implementar `calcularCoeficienteSilueta()` con fórmula completa
- [ ] Implementar método auxiliar `calcularDistanciaMediaIntraCluster()`
- [ ] Implementar método auxiliar `calcularDistanciaMediaClusterMasCercano()`
- [ ] Test: silueta con 1 solo cluster (debe ser 0)
- [ ] Test: silueta con clustering aleatorio
- [ ] Test: comparar silueta de k-means vs k-means++
- [ ] Test de integración: clustering completo end-to-end

---

## 🔄 Workflow Sugerido

### Día 1 (2-3 horas)
1. **Todos:** Leer `CLUSTERING_EXPLICACION.md` juntos (30 min)
2. **Todos:** Revisar `CLUSTERING_TODO.md` y aclarar dudas (15 min)
3. **Persona 1 y 2:** Implementar centroides (en paralelo)
4. **Persona 3:** Implementar integración en controlador

### Día 2 (2 horas)
1. **Persona 3:** Implementar métodos de KMeans usando centroides de P1/P2
2. **Persona 4:** Implementar evaluación
3. **Todos:** Code review cruzado

### Día 3 (1-2 horas)
1. **Persona 4:** Tests unitarios
2. **Todos:** Test de integración conjunto
3. **Todos:** Probar con datos reales de encuesta

---

## 📞 Coordinación

### Dependencias entre tareas:
```
Persona 1 (Centroides numéricos)
    ↓
Persona 3 (KMeans - calcular centroides) ← Persona 2 (Centroides categóricos)
    ↓
Persona 4 (Tests)
```

**Importante:**
- Persona 3 necesita que Persona 1 y 2 terminen `CentroidCalculator` primero
- Persona 4 puede empezar tests unitarios en paralelo, pero tests de integración requieren TODO #6, #7, #8

### Comunicación sugerida:
- **Daily standup** de 5 min: "¿Qué hice? ¿Qué haré hoy? ¿Bloqueadores?"
- **Pull Requests:** Cada persona hace PR de su parte para revisión
- **Tests:** Cada persona escribe tests de su código antes de PR

---

## 🎯 Definición de "Hecho"

Una tarea está completa cuando:
- [ ] Código implementado siguiendo sugerencia de `CLUSTERING_TODO.md`
- [ ] Sin errores de compilación
- [ ] Tests unitarios escritos
- [ ] Tests pasan (verde)
- [ ] Javadoc actualizado si es necesario
- [ ] Code review aprobado por al menos 1 compañero

---

## 🚨 Si te atascas

**Persona 1/2 (Centroides):**
- Revisar ejemplos en `CLUSTERING_EXPLICACION.md`
- Usar `System.out.println()` para debuggear frecuencias
- Empezar con casos simples (lista con 1 elemento)

**Persona 3 (Integración):**
- Revisar qué métodos existen en `CtrlDominioRespuesta`
- Hacer un método auxiliar temporal con datos mock para probar el flujo
- Preguntar a Persona 1/2 si CentroidCalculator está listo

**Persona 4 (Evaluación):**
- Implementar primero los métodos auxiliares (`calcularDistanciaMediaIntraCluster`)
- Probar con clusters "dummy" (clusters perfectos hechos a mano)
- Verificar fórmula de silueta en `CLUSTERING_EXPLICACION.md`

---

## 📦 Entregables Finales

Cuando terminen, deberíais tener:

1. ✅ **Código funcionando:**
   - 7 clases de clustering completamente implementadas
   - 1 controlador de dominio funcional
   - 0 errores de compilación

2. ✅ **Tests:**
   - Tests unitarios para cada clase
   - Al menos 1 test de integración end-to-end
   - Cobertura >80% en módulo clustering

3. ✅ **Documentación:**
   - Javadoc actualizado en métodos implementados
   - `CLUSTERING_EXPLICACION.md` (ya hecho)
   - `CLUSTERING_TODO.md` (ya hecho)
   - Opcional: `CLUSTERING_RESULTADOS.md` con ejemplos de uso

4. ✅ **Demo:**
   - Ejecutar clustering sobre una encuesta real
   - Mostrar los clusters resultantes
   - Mostrar coeficiente de silueta

---

## 🎓 Aprendizajes Esperados

Al terminar, cada persona debería poder:

**Todos:**
- ✅ Explicar qué es clustering y para qué sirve
- ✅ Explicar la diferencia entre k-means y k-means++
- ✅ Interpretar un coeficiente de silueta

**Persona 1:**
- ✅ Normalizar datos numéricos
- ✅ Calcular centroides de variables continuas

**Persona 2:**
- ✅ Calcular modas y frecuencias
- ✅ Trabajar con variables categóricas

**Persona 3:**
- ✅ Orquestar flujo de datos entre controladores
- ✅ Implementar algoritmos iterativos (k-means)

**Persona 4:**
- ✅ Calcular métricas de evaluación (silueta)
- ✅ Diseñar y ejecutar tests comprehensivos

---

**¡Éxito con la implementación!** 🚀
