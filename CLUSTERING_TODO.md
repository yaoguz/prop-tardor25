# Clustering - Métodos Pendientes de Implementar

> **Estado del módulo:** ✅ Arquitectura completa | 🔄 Implementación parcial | ⏳ Tests pendientes

---

## 📋 Resumen de Estado

| Clase | Estado | Métodos TODO | Prioridad |
|-------|--------|--------------|-----------|
| `ClusteringAlgorithm` | ✅ Completa | 0 | - |
| `DistanceCalculator` | 🔄 Funcional | 1 | 🔴 Alta |
| `CentroidCalculator` | ⚠️ Esqueleto | 4 | 🔴 Alta |
| `Cluster` | ✅ Completa | 0 | - |
| `KMeans` | 🔄 Funcional | 2 | 🟡 Media |
| `KMeansPlusPlus` | ✅ Completa | 0 | - |
| `ClusterEvaluator` | ⚠️ Esqueleto | 1 | 🟡 Media |
| `CtrlDominioPerfil` | 🔄 Funcional | 1 | 🔴 Alta |

**Total métodos TODO:** 9

---

## 🔴 PRIORIDAD ALTA (Bloquean funcionalidad básica)

---

### 1. `DistanceCalculator.java`

**Estado general:** 🔄 Funcional pero incompleto  
**Última actualización:** Corregidos getters y tipos

#### 📌 TODO #1: Implementar normalización dinámica en `distanciaNumerica()`

**Ubicación:** Línea ~28  
**Prioridad:** 🔴 Alta  
**Complejidad:** Baja  
**Tiempo estimado:** 15 min

**Descripción actual:**
```java
/**
 * Calcula la distancia entre dos respuestas numéricas.
 * Normaliza dividiendo por el rango (Vmax - Vmin).
 */
private static double distanciaNumerica(RespuestaNumerica r1, RespuestaNumerica r2) {
    double valor1 = r1.getRespuesta();
    double valor2 = r2.getRespuesta();
    
    // TODO: Obtener Vmax y Vmin de la pregunta correspondiente
    // Necesitamos acceso a PreguntaNumerica para obtener valorMaximo y valorMinimo
    double vmax = 100.0;  // Placeholder
    double vmin = 0.0;    // Placeholder
    
    if (vmax == vmin) return 0.0;
    return Math.abs(valor1 - valor2) / (vmax - vmin);
}
```

**Qué hacer:**
1. Modificar firma del método para recibir `PreguntaNumerica` o los valores min/max
2. Obtener `valorMaximo` y `valorMinimo` de la pregunta
3. Usar esos valores reales en vez de placeholders

**Ejemplo de implementación:**
```java
// Opción A: Recibir la pregunta
private static double distanciaNumerica(RespuestaNumerica r1, RespuestaNumerica r2, 
                                         PreguntaNumerica pregunta) {
    double valor1 = r1.getRespuesta();
    double valor2 = r2.getRespuesta();
    double vmax = pregunta.getValorMaximo();
    double vmin = pregunta.getValorMinimo();
    if (vmax == vmin) return 0.0;
    return Math.abs(valor1 - valor2) / (vmax - vmin);
}

// Opción B: Precalcular rangos antes de llamar
// Modificar calcularDistancia() para pasar Map<String, RangoPregunta> con info de min/max
```

**Impacto:** Sin esto, las distancias numéricas no están bien normalizadas.

---

### 2. `CentroidCalculator.java`

**Estado general:** ⚠️ Esqueleto vacío  
**Última actualización:** Creación inicial

#### 📌 TODO #2: Implementar `calcularCentroideNumerico()`

**Ubicación:** Línea ~30  
**Prioridad:** 🔴 Alta  
**Complejidad:** Baja  
**Tiempo estimado:** 10 min

**Descripción actual:**
```java
/**
 * Calcula el centroide para una pregunta numérica (media aritmética).
 * 
 * @param respuestas Lista de respuestas numéricas del cluster
 * @return Valor medio del centroide
 */
public static double calcularCentroideNumerico(List<RespuestaNumerica> respuestas) {
    // TODO: Calcular la media aritmética de todas las respuestas
    return 0.0;
}
```

**Qué hacer:**
```java
public static double calcularCentroideNumerico(List<RespuestaNumerica> respuestas) {
    if (respuestas.isEmpty()) return 0.0;
    
    double suma = 0.0;
    for (RespuestaNumerica r : respuestas) {
        suma += r.getRespuesta();  // getRespuesta() devuelve Double
    }
    return suma / respuestas.size();
}
```

**Tests sugeridos:**
- Input: [5.0, 10.0, 15.0] → Output: 10.0
- Input: [] → Output: 0.0
- Input: [7.5] → Output: 7.5

---

#### 📌 TODO #3: Implementar `calcularCentroideOpcionUnica()`

**Ubicación:** Línea ~43  
**Prioridad:** 🔴 Alta  
**Complejidad:** Media  
**Tiempo estimado:** 20 min

**Descripción actual:**
```java
/**
 * Calcula el centroide para una pregunta de opción única (moda).
 * 
 * @param respuestas Lista de respuestas de opción única del cluster
 * @return Índice de la opción más frecuente
 */
public static int calcularCentroideOpcionUnica(List<RespuestaOpcionUnica> respuestas) {
    // TODO: Calcular la moda (opción más frecuente)
    // Si hay empate, elegir la de menor índice
    return 0;
}
```

**Qué hacer:**
```java
public static int calcularCentroideOpcionUnica(List<RespuestaOpcionUnica> respuestas) {
    if (respuestas.isEmpty()) return 0;
    
    // Contar frecuencias
    Map<Integer, Integer> frecuencias = new HashMap<>();
    for (RespuestaOpcionUnica r : respuestas) {
        Integer opcion = r.getRespuesta();  // devuelve Integer (índice)
        frecuencias.put(opcion, frecuencias.getOrDefault(opcion, 0) + 1);
    }
    
    // Encontrar moda (máxima frecuencia, menor índice si empate)
    int moda = 0;
    int maxFrecuencia = 0;
    for (Map.Entry<Integer, Integer> entry : frecuencias.entrySet()) {
        if (entry.getValue() > maxFrecuencia || 
            (entry.getValue() == maxFrecuencia && entry.getKey() < moda)) {
            moda = entry.getKey();
            maxFrecuencia = entry.getValue();
        }
    }
    
    return moda;
}
```

**Tests sugeridos:**
- Input: [1, 1, 2, 3] → Output: 1 (más frecuente)
- Input: [0, 1, 1, 2, 2] → Output: 1 (empate, menor índice)
- Input: [] → Output: 0

---

#### 📌 TODO #4: Implementar `calcularCentroideOpcionMultiple()`

**Ubicación:** Línea ~57  
**Prioridad:** 🔴 Alta  
**Complejidad:** Media  
**Tiempo estimado:** 25 min

**Descripción actual:**
```java
/**
 * Calcula el centroide para una pregunta de opción múltiple.
 * Devuelve las opciones que aparecen en más del 50% de las respuestas.
 * 
 * @param respuestas Lista de respuestas de opción múltiple del cluster
 * @return Array con las opciones más frecuentes
 */
public static int[] calcularCentroideOpcionMultiple(List<RespuestaOpcionMultiple> respuestas) {
    // TODO: Contar frecuencia de cada opción
    // Devolver las que aparecen en > 50% de respuestas
    return new int[0];
}
```

**Qué hacer:**
```java
public static int[] calcularCentroideOpcionMultiple(List<RespuestaOpcionMultiple> respuestas) {
    if (respuestas.isEmpty()) return new int[0];
    
    // Contar frecuencias de cada opción
    Map<Integer, Integer> frecuencias = new HashMap<>();
    for (RespuestaOpcionMultiple r : respuestas) {
        int[] opciones = r.getRespuesta();  // devuelve int[]
        for (int opcion : opciones) {
            frecuencias.put(opcion, frecuencias.getOrDefault(opcion, 0) + 1);
        }
    }
    
    // Filtrar opciones que aparecen en > 50% de respuestas
    int umbral = respuestas.size() / 2;
    List<Integer> centroide = new ArrayList<>();
    for (Map.Entry<Integer, Integer> entry : frecuencias.entrySet()) {
        if (entry.getValue() > umbral) {
            centroide.add(entry.getKey());
        }
    }
    
    // Convertir List<Integer> a int[]
    return centroide.stream().mapToInt(i -> i).toArray();
}
```

**Tests sugeridos:**
- Input: [{0,1}, {1,2}, {0,1}] → Output: [0,1] (aparecen en 2/3 > 50%)
- Input: [{0}, {1}, {2}] → Output: [] (ninguna > 50%)
- Input: [] → Output: []

---

#### 📌 TODO #5: Implementar `calcularCentroideLibre()`

**Ubicación:** Línea ~72  
**Prioridad:** 🟡 Media (menos crítico)  
**Complejidad:** Alta  
**Tiempo estimado:** 30-60 min

**Descripción actual:**
```java
/**
 * Calcula el centroide para una pregunta de texto libre.
 * Devuelve la respuesta más común, o una representación "central".
 * 
 * @param respuestas Lista de respuestas libres del cluster
 * @return String representativo del centroide
 */
public static String calcularCentroideLibre(List<RespuestaLibre> respuestas) {
    // TODO: Opción simple: Devolver la respuesta más frecuente
    // Opción avanzada: Calcular "centro" usando similitud de strings
    return "";
}
```

**Qué hacer (versión simple):**
```java
public static String calcularCentroideLibre(List<RespuestaLibre> respuestas) {
    if (respuestas.isEmpty()) return "";
    
    // Contar frecuencias de textos exactos
    Map<String, Integer> frecuencias = new HashMap<>();
    for (RespuestaLibre r : respuestas) {
        String texto = r.getRespuesta();  // devuelve String
        frecuencias.put(texto, frecuencias.getOrDefault(texto, 0) + 1);
    }
    
    // Devolver el más frecuente
    String moda = "";
    int maxFrecuencia = 0;
    for (Map.Entry<String, Integer> entry : frecuencias.entrySet()) {
        if (entry.getValue() > maxFrecuencia) {
            moda = entry.getKey();
            maxFrecuencia = entry.getValue();
        }
    }
    
    return moda;
}
```

**Qué hacer (versión avanzada - opcional):**
- Calcular el texto que minimiza la suma de distancias Levenshtein a todos los demás
- O devolver el texto mediano por longitud

**Tests sugeridos:**
- Input: ["hola", "hola", "adios"] → Output: "hola"
- Input: ["a", "b", "c"] → Output: cualquiera (o "a" si se elige el primero)

---

### 3. `CtrlDominioPerfil.java`

**Estado general:** 🔄 Funcional pero incompleto  
**Última actualización:** Corregido método duplicado

#### 📌 TODO #6: Implementar `obtenerRespuestasPorUsuario()`

**Ubicación:** Línea ~95  
**Prioridad:** 🔴 Alta (crítico para que funcione el clustering)  
**Complejidad:** Media  
**Tiempo estimado:** 30 min

**Descripción actual:**
```java
private Map<String, List<Respuesta>> obtenerRespuestasPorUsuario(
        int idEncuesta, CtrlDominioRespuesta ctrlDominioRespuesta) {
    // TODO: Obtener todas las respuestas de la encuesta
    // y agruparlas por usuario
    return new HashMap<>();
}
```

**Qué hacer:**
```java
private Map<String, List<Respuesta>> obtenerRespuestasPorUsuario(
        int idEncuesta, CtrlDominioRespuesta ctrlDominioRespuesta) {
    
    Map<String, List<Respuesta>> resultado = new HashMap<>();
    
    // Opción A: Si CtrlDominioRespuesta tiene getRespuestasPorEncuesta()
    List<Respuesta> todasRespuestas = ctrlDominioRespuesta.getRespuestasPorEncuesta(idEncuesta);
    for (Respuesta r : todasRespuestas) {
        String usuario = r.getNomUsuario();  // Asumiendo que Respuesta tiene este método
        resultado.computeIfAbsent(usuario, k -> new ArrayList<>()).add(r);
    }
    
    // Opción B: Si necesitas iterar por usuarios
    // List<String> usuarios = ctrlDominioUsuario.getTodosUsuarios();
    // for (String usuario : usuarios) {
    //     List<Respuesta> respuestas = ctrlDominioRespuesta.getRespuestasUsuario(usuario, idEncuesta);
    //     if (!respuestas.isEmpty()) {
    //         resultado.put(usuario, respuestas);
    //     }
    // }
    
    return resultado;
}
```

**Dependencias:**
- Necesitas un método en `CtrlDominioRespuesta` para obtener respuestas de una encuesta
- O bien obtener lista de usuarios y sus respuestas

**Tests sugeridos:**
- Encuesta con 3 usuarios, 2 preguntas cada uno → Map con 3 entradas, cada una con 2 respuestas
- Encuesta sin respuestas → Map vacío

---

## 🟡 PRIORIDAD MEDIA (Mejoran funcionalidad)

---

### 4. `KMeans.java`

**Estado general:** 🔄 Funcional, métodos auxiliares incompletos  
**Última actualización:** Corregida visibilidad de inicializarClusters

#### 📌 TODO #7: Implementar `calcularDistanciaAlCentroide()`

**Ubicación:** Línea ~120  
**Prioridad:** 🟡 Media (ya hay código provisional)  
**Complejidad:** Media  
**Tiempo estimado:** 20 min

**Descripción actual:**
```java
/**
 * Calcula la distancia de un usuario al centroide de un cluster.
 * 
 * @param respuestas Respuestas del usuario
 * @param centroide Centroide del cluster (valores representativos por pregunta)
 * @return Distancia total (suma de distancias por pregunta)
 */
private double calcularDistanciaAlCentroide(List<Respuesta> respuestas, List<Double> centroide) {
    // TODO: Por cada respuesta, calcular distancia a su valor centroide correspondiente
    // Suma todas las distancias
    return 0.0;
}
```

**Qué hacer:**
```java
private double calcularDistanciaAlCentroide(List<Respuesta> respuestas, List<Double> centroide) {
    if (respuestas.size() != centroide.size()) {
        throw new IllegalArgumentException("Tamaños no coinciden");
    }
    
    double distanciaTotal = 0.0;
    
    for (int i = 0; i < respuestas.size(); i++) {
        Respuesta r = respuestas.get(i);
        double valorCentroide = centroide.get(i);
        
        // Calcular distancia según tipo de respuesta
        // (esto es simplificado, idealmente usar DistanceCalculator)
        if (r instanceof RespuestaNumerica) {
            double valorResp = ((RespuestaNumerica) r).getRespuesta();
            distanciaTotal += Math.abs(valorResp - valorCentroide);
        } else if (r instanceof RespuestaOpcionUnica) {
            int valorResp = ((RespuestaOpcionUnica) r).getRespuesta();
            distanciaTotal += (valorResp == (int)valorCentroide) ? 0.0 : 1.0;
        }
        // ... otros tipos
    }
    
    return distanciaTotal;
}
```

**Nota:** Este método es provisional. Idealmente debería usar `DistanceCalculator` de forma más robusta.

---

#### 📌 TODO #8: Implementar `calcularMediaRespuestas()`

**Ubicación:** Línea ~137  
**Prioridad:** 🟡 Media  
**Complejidad:** Alta  
**Tiempo estimado:** 40 min

**Descripción actual:**
```java
/**
 * Calcula el nuevo centroide de un cluster (media de respuestas).
 * 
 * @param miembros Nombres de usuarios en el cluster
 * @param respuestas Mapa de respuestas por usuario
 * @return Lista de valores representativos (centroide)
 */
private List<Double> calcularMediaRespuestas(List<String> miembros, 
                                              Map<String, List<Respuesta>> respuestas) {
    // TODO: Para cada pregunta (índice en las respuestas):
    // - Obtener todas las respuestas de los miembros para esa pregunta
    // - Calcular centroide usando CentroidCalculator según tipo
    // - Convertir a Double y añadir a la lista
    return new ArrayList<>();
}
```

**Qué hacer:**
```java
private List<Double> calcularMediaRespuestas(List<String> miembros, 
                                              Map<String, List<Respuesta>> respuestas) {
    if (miembros.isEmpty()) return new ArrayList<>();
    
    // Determinar número de preguntas
    int numPreguntas = respuestas.get(miembros.get(0)).size();
    List<Double> centroide = new ArrayList<>();
    
    // Para cada pregunta
    for (int i = 0; i < numPreguntas; i++) {
        // Recolectar respuestas de todos los miembros para esta pregunta
        List<Respuesta> respuestasPregunta = new ArrayList<>();
        for (String usuario : miembros) {
            respuestasPregunta.add(respuestas.get(usuario).get(i));
        }
        
        // Calcular centroide según tipo
        Respuesta primera = respuestasPregunta.get(0);
        double valorCentroide;
        
        if (primera instanceof RespuestaNumerica) {
            List<RespuestaNumerica> rNumericas = respuestasPregunta.stream()
                .map(r -> (RespuestaNumerica) r)
                .collect(Collectors.toList());
            valorCentroide = CentroidCalculator.calcularCentroideNumerico(rNumericas);
            
        } else if (primera instanceof RespuestaOpcionUnica) {
            List<RespuestaOpcionUnica> rOpciones = respuestasPregunta.stream()
                .map(r -> (RespuestaOpcionUnica) r)
                .collect(Collectors.toList());
            valorCentroide = CentroidCalculator.calcularCentroideOpcionUnica(rOpciones);
            
        } else {
            // Otros tipos...
            valorCentroide = 0.0;
        }
        
        centroide.add(valorCentroide);
    }
    
    return centroide;
}
```

**Dependencias:**
- Requiere que `CentroidCalculator` esté implementado (TODO #2-5)

---

### 5. `ClusterEvaluator.java`

**Estado general:** ⚠️ Esqueleto vacío  
**Última actualización:** Creación inicial

#### 📌 TODO #9: Implementar `calcularCoeficienteSilueta()`

**Ubicación:** Línea ~25  
**Prioridad:** 🟡 Media (para evaluación, no bloquea clustering básico)  
**Complejidad:** Alta  
**Tiempo estimado:** 60 min

**Descripción actual:**
```java
/**
 * Calcula el coeficiente de silueta global para evaluar la calidad del clustering.
 * 
 * @param clusters Lista de clusters resultantes
 * @param respuestas Mapa de respuestas por usuario
 * @return Coeficiente de silueta [-1, 1], donde valores cercanos a 1 indican buen clustering
 */
public static double calcularCoeficienteSilueta(List<Cluster> clusters, 
                                                 Map<String, List<Respuesta>> respuestas) {
    // TODO: Implementar cálculo del coeficiente de silueta
    // Ver CLUSTERING_EXPLICACION.md para la fórmula
    return 0.0;
}
```

**Qué hacer:**
```java
public static double calcularCoeficienteSilueta(List<Cluster> clusters, 
                                                 Map<String, List<Respuesta>> respuestas) {
    double sumaSiluetas = 0.0;
    int totalUsuarios = 0;
    
    for (Cluster cluster : clusters) {
        for (String usuario : cluster.getMiembros()) {
            // a(i): distancia media a usuarios del mismo cluster
            double a = calcularDistanciaMediaIntraCluster(usuario, cluster, respuestas);
            
            // b(i): distancia media al cluster más cercano
            double b = calcularDistanciaMediaClusterMasCercano(usuario, cluster, clusters, respuestas);
            
            // s(i) = (b - a) / max(a, b)
            double s = (b - a) / Math.max(a, b);
            sumaSiluetas += s;
            totalUsuarios++;
        }
    }
    
    return totalUsuarios > 0 ? sumaSiluetas / totalUsuarios : 0.0;
}

private static double calcularDistanciaMediaIntraCluster(String usuario, Cluster cluster,
                                                          Map<String, List<Respuesta>> respuestas) {
    List<Respuesta> respUsuario = respuestas.get(usuario);
    double sumaDistancias = 0.0;
    int count = 0;
    
    for (String otroUsuario : cluster.getMiembros()) {
        if (!otroUsuario.equals(usuario)) {
            List<Respuesta> respOtro = respuestas.get(otroUsuario);
            sumaDistancias += DistanceCalculator.calcularDistancia(respUsuario, respOtro);
            count++;
        }
    }
    
    return count > 0 ? sumaDistancias / count : 0.0;
}

private static double calcularDistanciaMediaClusterMasCercano(String usuario, Cluster clusterActual,
                                                                List<Cluster> todosLosClusters,
                                                                Map<String, List<Respuesta>> respuestas) {
    List<Respuesta> respUsuario = respuestas.get(usuario);
    double minDistanciaMedia = Double.MAX_VALUE;
    
    for (Cluster otroCluster : todosLosClusters) {
        if (otroCluster == clusterActual) continue;
        
        double sumaDistancias = 0.0;
        for (String otroUsuario : otroCluster.getMiembros()) {
            List<Respuesta> respOtro = respuestas.get(otroUsuario);
            sumaDistancias += DistanceCalculator.calcularDistancia(respUsuario, respOtro);
        }
        
        double distanciaMedia = sumaDistancias / otroCluster.getMiembros().size();
        minDistanciaMedia = Math.min(minDistanciaMedia, distanciaMedia);
    }
    
    return minDistanciaMedia;
}
```

**Tests sugeridos:**
- Clustering perfecto (usuarios muy separados) → ~1.0
- Clustering malo (usuarios mezclados) → ~0 o negativo
- Un solo cluster → 0.0 (no hay cluster externo)

---

## 📊 Distribución Sugerida del Trabajo

### **Persona 1: Distancias y Centroides Numéricos**
- ✅ TODO #1: Normalización en `DistanceCalculator`
- ✅ TODO #2: `CentroidCalculator.calcularCentroideNumerico()`
- ⏱️ Tiempo estimado: 25 min
- 📚 Conocimientos: Matemáticas básicas, normalización

### **Persona 2: Centroides Categóricos**
- ✅ TODO #3: `CentroidCalculator.calcularCentroideOpcionUnica()`
- ✅ TODO #4: `CentroidCalculator.calcularCentroideOpcionMultiple()`
- ✅ TODO #5: `CentroidCalculator.calcularCentroideLibre()` (opcional)
- ⏱️ Tiempo estimado: 45-75 min
- 📚 Conocimientos: Maps, frecuencias, estadística básica

### **Persona 3: Integración y K-Means**
- ✅ TODO #6: `CtrlDominioPerfil.obtenerRespuestasPorUsuario()`
- ✅ TODO #7: `KMeans.calcularDistanciaAlCentroide()`
- ✅ TODO #8: `KMeans.calcularMediaRespuestas()`
- ⏱️ Tiempo estimado: 90 min
- 📚 Conocimientos: Arquitectura del sistema, controladores

### **Persona 4: Evaluación y Tests**
- ✅ TODO #9: `ClusterEvaluator.calcularCoeficienteSilueta()`
- ✅ Escribir tests para todas las clases
- ⏱️ Tiempo estimado: 90-120 min
- 📚 Conocimientos: Testing, JUnit, evaluación de algoritmos

---

## ✅ Checklist de Implementación

### Fase 1: Básico (funcionalidad mínima)
- [ ] TODO #1: Normalización distancias numéricas
- [ ] TODO #2: Centroide numérico
- [ ] TODO #3: Centroide opción única
- [ ] TODO #6: Obtener respuestas por usuario
- [ ] TODO #7: Distancia al centroide en K-Means
- [ ] TODO #8: Recalcular centroides en K-Means

### Fase 2: Completo
- [ ] TODO #4: Centroide opción múltiple
- [ ] TODO #5: Centroide texto libre
- [ ] TODO #9: Coeficiente de silueta

### Fase 3: Validación
- [ ] Tests unitarios para `DistanceCalculator`
- [ ] Tests unitarios para `CentroidCalculator`
- [ ] Tests de integración para `KMeans`
- [ ] Tests de integración para `KMeansPlusPlus`
- [ ] Test end-to-end: ejecutar clustering completo

---

## 📝 Notas Adicionales

### Imports necesarios
Aseguraos de tener estos imports donde corresponda:
```java
import java.util.*;
import java.util.stream.Collectors;
import domain.*;
```

### Gestión de errores
Considerar casos especiales:
- Listas vacías
- Clusters con 1 solo usuario
- Divisiones por cero (rangos con Vmax = Vmin)
- Usuarios sin respuestas

### Optimizaciones futuras (no prioritarias)
- Cachear cálculos de distancia (matriz de distancias)
- Paralelizar iteraciones de K-Means
- Early stopping si convergencia es rápida

---

## 🎯 Meta Final

Una vez completados todos los TODOs, deberíais poder ejecutar:

```java
CtrlDominioPerfil ctrlPerfil = new CtrlDominioPerfil();
List<Cluster> clusters = ctrlPerfil.ejecutarClustering(
    idEncuesta, 
    k=4, 
    "kmeans++", 
    ctrlUsuario, 
    ctrlRespuesta
);

// Output esperado:
// Coeficiente de Silueta: 0.73
// Cluster 0: 15 usuarios
// Cluster 1: 23 usuarios
// Cluster 2: 42 usuarios
// Cluster 3: 8 usuarios
```

**¡Buena suerte con la implementación!** 🚀
