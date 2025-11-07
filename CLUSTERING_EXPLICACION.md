# Explicación del Clustering - Análisis de Perfiles de Usuario

## 📚 ¿Qué es el Clustering?

El **clustering** (agrupamiento) es una técnica de aprendizaje automático NO supervisado que agrupa elementos similares. En vuestro proyecto, se usa para **encontrar perfiles de usuario** basados en cómo responden las encuestas.

**Ejemplo visual:**
```
Usuarios dispersos → Clustering → 3 grupos (perfiles)

  U1      U5           [Perfil A]    [Perfil B]    [Perfil C]
    U2  U4               U1 U2         U4 U5         U6 U7
  U6    U3               U3            U8            U9
    U7  U8
  U9
```

---

## 🎯 ¿Para qué lo usáis?

**Objetivo**: Dado que varios usuarios han respondido la misma encuesta, queremos:
1. Agruparlos en **k perfiles** (clusters)
2. Cada perfil representa un "tipo de usuario" con respuestas similares
3. Poder decir: "Este usuario es del perfil 2 (deportistas activos)" o "Este usuario es del perfil 1 (sedentarios)"

---

## 🔢 Algoritmo K-Means (Básico)

### Pasos del algoritmo:

**Entrada:**
- Lista de usuarios con sus respuestas: `{U1: [resp1, resp2, ...], U2: [resp1, resp2, ...], ...}`
- Número de clusters: `k = 3` (por ejemplo)

**Proceso:**

#### **Paso 1: Inicialización Random**
Elegir **k usuarios al azar** como "centroides" (representantes) iniciales:
```
k=3 → Elijo U1, U5, U9 como centroides iniciales
Centroide C1 = respuestas de U1
Centroide C2 = respuestas de U5
Centroide C3 = respuestas de U9
```

#### **Paso 2: Asignación**
Para cada usuario, calcular la **distancia** a cada centroide y asignarlo al más cercano:
```
U2 → distancia a C1=0.2, C2=0.8, C3=0.5 → asignar a C1
U3 → distancia a C1=0.3, C2=0.7, C3=0.4 → asignar a C1
U4 → distancia a C1=0.9, C2=0.1, C3=0.6 → asignar a C2
...

Resultado: Cluster 1 = {U1, U2, U3}
           Cluster 2 = {U4, U5}
           Cluster 3 = {U9, U6}
```

#### **Paso 3: Recalcular Centroides**
Para cada cluster, calcular el **nuevo centroide** (media/moda de las respuestas):
```
Cluster 1 = {U1, U2, U3}
Nuevo C1 = promedio de respuestas de U1, U2, U3
```

#### **Paso 4: Repetir**
Volver al Paso 2 hasta que **los clusters no cambien** (convergencia).

---

## 🚀 Algoritmo K-Means++ (Mejorado)

**Problema de K-Means básico:**
Si eliges mal los centroides iniciales, puedes acabar en un mal resultado (óptimo local).

**Solución K-Means++:**
Elegir centroides iniciales de forma **inteligente**:

1. Elegir 1er centroide **al azar**
2. Para elegir el siguiente:
   - Calcular distancia de cada usuario al centroide más cercano
   - Elegir el siguiente centroide con **probabilidad proporcional al cuadrado de la distancia** (D²-weighted)
   - Esto favorece elegir usuarios **lejanos** a los centroides ya elegidos

**Resultado:** Centroides bien espaciados → Mejor clustering → K-Means++ da mejores resultados que K-Means clásico.

---

## 📏 Cálculo de Distancias (5 tipos)

Cada pregunta puede ser de diferente tipo, así que calculamos distancias de forma distinta:

### **1. Pregunta Numérica**
**Ejemplo:** "¿Cuántas horas haces deporte a la semana?"
- U1 responde: 5
- U2 responde: 8

**Fórmula:**
```
distancia = |valor1 - valor2| / (Vmax - Vmin)
          = |5 - 8| / (10 - 0)
          = 3 / 10 = 0.3
```

**¿Por qué normalizar?**
Para que todas las distancias estén entre 0 y 1 (comparable con otros tipos).

---

### **2. Pregunta Categórica Ordenada**
**Ejemplo:** "Nivel de satisfacción: Bajo, Medio, Alto"
- U1 responde: Medio (índice 1)
- U2 responde: Alto (índice 2)

**Fórmula:**
```
distancia = |índice1 - índice2| / (numOpciones - 1)
          = |1 - 2| / (3 - 1)
          = 1 / 2 = 0.5
```

---

### **3. Pregunta Categórica NO Ordenada (Opción Única)**
**Ejemplo:** "Color favorito: Rojo, Azul, Verde"
- U1 responde: Rojo
- U2 responde: Rojo

**Fórmula:**
```
distancia = 0 si igual, 1 si diferente
          = 0 (ambos Rojo)
```

No hay orden entre colores, así que solo importa si coinciden.

---

### **4. Pregunta Categórica NO Ordenada (Opción Múltiple)**
**Ejemplo:** "¿Qué deportes practicas? (varios): Fútbol, Tenis, Natación, Running"
- U1 responde: {Fútbol, Tenis}
- U2 responde: {Tenis, Natación}

**Fórmula (Jaccard):**
```
Jaccard = |A ∩ B| / |A ∪ B|
        = |{Tenis}| / |{Fútbol, Tenis, Natación}|
        = 1 / 3 ≈ 0.333

distancia = 1 - Jaccard = 1 - 0.333 = 0.667
```

**Interpretación:**
- Jaccard alto → muy similares → distancia baja
- Jaccard bajo → muy diferentes → distancia alta

---

### **5. Pregunta de Texto Libre**
**Ejemplo:** "Describe tu hobbie favorito"
- U1 responde: "programar"
- U2 responde: "programación"

**Fórmula (Levenshtein normalizada):**
```
Levenshtein("programar", "programación") = 4 ediciones
distancia = 4 / max(9, 12) = 4 / 12 ≈ 0.333
```

**¿Qué es Levenshtein?**
Número de inserciones, borrados o sustituciones para convertir una cadena en otra.

---

## 📍 Cálculo de Centroides

Un **centroide** es el "representante" de un cluster. Se calcula de forma diferente según el tipo de pregunta:

### **Pregunta Numérica → Media**
```
Cluster = {U1: 5, U2: 8, U3: 6}
Centroide = (5 + 8 + 6) / 3 = 6.33
```

### **Pregunta Categórica Ordenada → Mediana o Moda**
```
Cluster = {U1: "Bajo", U2: "Alto", U3: "Alto"}
Centroide = "Alto" (más frecuente)
```

### **Pregunta Categórica NO Ordenada (Única) → Moda**
```
Cluster = {U1: "Rojo", U2: "Azul", U3: "Rojo"}
Centroide = "Rojo" (más frecuente)
```

### **Pregunta Categórica NO Ordenada (Múltiple) → Unión o Intersección**
```
Cluster = {U1: {Fútbol, Tenis}, U2: {Tenis, Running}, U3: {Fútbol}}
Centroide = {Fútbol, Tenis} (opciones más frecuentes)
```

### **Pregunta Texto Libre → String más común o centroide sintético**
Esto es complicado. Opciones:
- Elegir la respuesta más común
- Calcular "centro" usando embeddings (avanzado)

---

## 📊 Evaluación: Coeficiente de Silueta

**¿Cómo sabemos si el clustering es bueno?**

El **coeficiente de silueta** mide:
- **Cohesión**: ¿Los usuarios de un cluster están cerca entre sí?
- **Separación**: ¿Los clusters están bien diferenciados?

### Fórmula para cada usuario:

```
a(i) = distancia media de i a otros usuarios del MISMO cluster
b(i) = distancia media de i al cluster MÁS CERCANO (no el suyo)

s(i) = (b(i) - a(i)) / max(a(i), b(i))
```

**Interpretación:**
- `s(i) ≈ 1`: Usuario bien asignado (cerca de su cluster, lejos de otros)
- `s(i) ≈ 0`: Usuario en la frontera entre clusters
- `s(i) < 0`: Usuario mal asignado (más cerca de otro cluster)

**Silueta global = promedio de s(i) para todos los usuarios**

---

## 🔄 Ejemplo Completo Paso a Paso

**Encuesta:** "Hábitos deportivos"
- P1 (numérica): ¿Horas de deporte/semana?
- P2 (categórica): Nivel: Bajo/Medio/Alto

**Usuarios:**
```
U1: [3 horas, Bajo]
U2: [2 horas, Bajo]
U3: [8 horas, Alto]
U4: [9 horas, Alto]
```

**Ejecutar K-Means con k=2:**

### **Iteración 1:**

1. **Inicialización:** Elegir U1 y U3 como centroides
   - C1 = [3, Bajo]
   - C2 = [8, Alto]

2. **Asignación:**
   - U1 → dist(U1, C1) = 0, dist(U1, C2) = grande → Cluster 1
   - U2 → dist(U2, C1) = pequeña, dist(U2, C2) = grande → Cluster 1
   - U3 → dist(U3, C1) = grande, dist(U3, C2) = 0 → Cluster 2
   - U4 → dist(U4, C1) = grande, dist(U4, C2) = pequeña → Cluster 2

   **Resultado:** 
   - Cluster 1 = {U1, U2}
   - Cluster 2 = {U3, U4}

3. **Recalcular centroides:**
   - C1 = media de {U1, U2} = [(3+2)/2, Bajo] = [2.5, Bajo]
   - C2 = media de {U3, U4} = [(8+9)/2, Alto] = [8.5, Alto]

### **Iteración 2:**

4. **Reasignación:** (calcular distancias de nuevo)
   - Las asignaciones no cambian

5. **Convergencia alcanzada** ✅

**Resultado final:**
- **Perfil A (sedentarios):** U1, U2 → ~2.5h/semana, nivel Bajo
- **Perfil B (deportistas):** U3, U4 → ~8.5h/semana, nivel Alto

---

## 🛠️ Flujo en Vuestro Código

```java
// 1. Usuario pide clustering
CtrlDominioPerfil.ejecutarClustering(idEncuesta=123, k=3, algoritmo="kmeans++", ...)

// 2. Obtener respuestas de todos los usuarios
Map<String, List<Respuesta>> respuestas = obtenerRespuestasPorUsuario(123);
// {
//   "user1": [RespuestaNumerica(5), RespuestaOpcionUnica(2), ...],
//   "user2": [RespuestaNumerica(8), RespuestaOpcionUnica(1), ...],
//   ...
// }

// 3. Crear algoritmo
ClusteringAlgorithm alg = new KMeansPlusPlus();

// 4. Ejecutar
List<Cluster> clusters = alg.ejecutarClustering(respuestas, 3);

// 5. Dentro del algoritmo:
// 5a. Inicializar centroides (k-means++: inteligente)
// 5b. BUCLE hasta convergencia:
//     - Para cada usuario: calcular distancia a centroides (DistanceCalculator)
//     - Asignar al centroide más cercano
//     - Recalcular centroides (CentroidCalculator)
// 5c. Retornar clusters

// 6. Evaluar calidad
double silueta = ClusterEvaluator.calcularCoeficienteSilueta(clusters, respuestas);
System.out.println("Calidad: " + silueta); // 0.7 → Buen clustering
```

---

## 🎓 Conceptos Clave - Resumen

| Concepto | Definición | En vuestro código |
|----------|-----------|-------------------|
| **Clustering** | Agrupar elementos similares | `ClusteringAlgorithm.ejecutarClustering()` |
| **Centroide** | Representante de un cluster | `CentroidCalculator.calcularCentroide()` |
| **Distancia** | Medida de similitud entre usuarios | `DistanceCalculator.calcularDistancia()` |
| **K-Means** | Algoritmo iterativo de clustering | `KMeans.java` |
| **K-Means++** | K-Means con mejor inicialización | `KMeansPlusPlus.java` |
| **Silueta** | Métrica de calidad del clustering | `ClusterEvaluator.calcularCoeficienteSilueta()` |
| **Convergencia** | Estado donde clusters no cambian | Condición de parada en `KMeans` |

---

## 🧪 Ejemplo de Uso Final

```java
// En la presentación:
int k = 4; // Quiero 4 perfiles
List<Cluster> perfiles = ctrlPresentacion.ejecutarClustering(idEncuesta, k, "kmeans++");

// Resultado:
// Cluster 0: 15 usuarios → Perfil "Deportistas intensivos"
// Cluster 1: 23 usuarios → Perfil "Deportistas ocasionales"
// Cluster 2: 42 usuarios → Perfil "Sedentarios"
// Cluster 3: 8 usuarios → Perfil "Profesionales del deporte"

// Ahora puedo:
// 1. Etiquetar usuarios con su perfil
// 2. Hacer recomendaciones basadas en perfil
// 3. Analizar qué respuestas caracterizan cada perfil
```

---

## ❓ Preguntas Frecuentes

**P: ¿Cómo elijo k (número de clusters)?**
R: Prueba varios valores (2, 3, 4, 5...) y elige el que tenga mejor coeficiente de silueta. También puedes usar el "método del codo" graficando distorsión vs k.

**P: ¿Qué pasa si dos usuarios tienen distancia 0?**
R: Son idénticos en cuanto a respuestas. Estarán en el mismo cluster.

**P: ¿K-Means siempre converge?**
R: Sí, pero puede ser a un óptimo local (mala solución). Por eso K-Means++ ayuda.

**P: ¿Por qué normalizar las distancias?**
R: Para que todas estén en la escala [0, 1] y sean comparables entre diferentes tipos de preguntas.

---

## 📚 Referencias

- [K-Means Wikipedia](https://en.wikipedia.org/wiki/K-means_clustering)
- [K-Means++ Paper](http://ilpubs.stanford.edu:8090/778/1/2006-13.pdf)
- [Silhouette Coefficient](https://en.wikipedia.org/wiki/Silhouette_(clustering))
- [Jaccard Index](https://en.wikipedia.org/wiki/Jaccard_index)
- [Levenshtein Distance](https://en.wikipedia.org/wiki/Levenshtein_distance)

---

**¡Listo para implementar!** 🚀
