# Arquitectura de Persistencia de Preguntas

Este documento describe la nueva arquitectura de persistencia para las preguntas, siguiendo el mismo patrón que se implementó para las respuestas.

## Estructura de Datos

```
data/
├── encuestas/
│   ├── 1.json (metadata: {idEncuesta, titulo, creador, IdPreguntas:[1,2,3,4]})
│   ├── 1/
│   │   ├── preguntas/
│   │   │   ├── 1.json (pregunta completa)
│   │   │   ├── 2.json
│   │   │   ├── 3.json
│   │   │   └── 4.json
│   │   └── respuestas/
│   │       └── ... (archivos de respuestas)
│   └── 2/, 3/, 4/, 5/ (similar)
└── indexPreguntas.json (mapeo idPregunta → idEncuesta)
```

## indexPreguntas.json

```json
{
  "1": 1,
  "2": 1,
  "3": 1,
  "4": 1,
  "5": 2,
  "6": 2,
  "7": 2,
  "8": 3,
  ...
}
```

---

## CtrlPersistenciaPregunta.java

```java
package persistence;

import com.google.gson.*;
import domain.*;
import java.io.*;
import java.util.*;

/**
 * Controlador de persistencia de preguntas en disco.
 * 
 * Gestiona la lectura y escritura de preguntas en formato JSON.
 * Cada pregunta se almacena en data/encuestas/{idEncuesta}/preguntas/{idPregunta}.json
 * 
 * RESPONSABILIDADES:
 * - Cargar preguntas desde archivo JSON
 * - Guardar preguntas a archivo JSON
 * - Gestionar operaciones I/O con disco
 * - Serializar según tipo de pregunta
 * 
 * ATRIBUTOS PRINCIPALES:
 * - gson: serializador/deserializador JSON
 * 
 * METODOS PRINCIPALES:
 * - getPregunta(): lee una pregunta específica del disco
 * - getAllPreguntas(): lee todas las preguntas del sistema
 * - guardarPregunta(): escribe una nueva pregunta a disco
 * - actualizarPregunta(): actualiza una pregunta existente
 * - eliminarPregunta(): elimina una pregunta del disco
 * 
 * @author PROP Cluster XX
 */
public class CtrlPersistenciaPregunta {

    private static CtrlPersistenciaPregunta singletonInstance;

    private final Gson gson;

    /**
     * Obtiene instancia única (patrón Singleton).
     * 
     * @return Instancia única de CtrlPersistenciaPregunta
     */
    public static CtrlPersistenciaPregunta getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new CtrlPersistenciaPregunta();
        }
        return singletonInstance;
    }

    /**
     * Crea controlador de persistencia de preguntas.
     */
    public CtrlPersistenciaPregunta() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Obtiene el siguiente ID disponible para una nueva pregunta.
     * 
     * @return Siguiente ID de pregunta
     */
    public int getNextId() {
        String FILE = "data/nextId.json";

        try (FileReader reader = new FileReader(FILE)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            return jsonObject.get("nextIdPregunta").getAsInt();
        }
        catch (Exception exc) {
            throw new RuntimeException("Error leyendo el archivo JSON", exc);
        }
    }

    /**
     * Obtiene el idEncuesta para una pregunta desde el índice.
     * 
     * @param idPregunta ID de la pregunta
     * @return ID de la encuesta
     */
    private int getIdEncuesta(int idPregunta) {
        String path = "data/indexPreguntas.json";
        try (FileReader reader = new FileReader(path)) {
            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
            return obj.get(String.valueOf(idPregunta)).getAsInt();
        }
        catch (Exception e) {
            throw new RuntimeException("Error leyendo índice para pregunta " + idPregunta, e);
        }
    }

    /**
     * Obtiene una pregunta desde disco.
     * 
     * @param idPregunta ID de la pregunta
     * @return Pregunta encontrada
     * @throws IllegalArgumentException si la pregunta no existe
     */
    public Pregunta getPregunta(int idPregunta) {
        int idEncuesta = getIdEncuesta(idPregunta);
        return getPregunta(idPregunta, idEncuesta);
    }

    /**
     * Obtiene una pregunta desde disco.
     * 
     * @param idPregunta ID de la pregunta
     * @param idEncuesta ID de la encuesta
     * @return Pregunta encontrada
     * @throws IllegalArgumentException si la pregunta no existe
     */
    public Pregunta getPregunta(int idPregunta, int idEncuesta) {
        String FILE = "data/encuestas/" + idEncuesta + "/preguntas/" + idPregunta + ".json";
        
        try (FileReader reader = new FileReader(FILE)) {
            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
            return deserializarPregunta(idPregunta, obj);
        }
        catch (FileNotFoundException e) {
            throw new IllegalArgumentException("La pregunta " + idPregunta + " no existe");
        }
        catch (Exception e) {
            throw new RuntimeException("Error leyendo pregunta " + idPregunta, e);
        }
    }

    /**
     * Deserializa un JsonObject a Pregunta según su tipo.
     * 
     * @param idPregunta ID de la pregunta
     * @param obj JsonObject con los datos
     * @return Pregunta deserializada
     */
    private Pregunta deserializarPregunta(int idPregunta, JsonObject obj) {
        String tipo = obj.get("tipo").getAsString();
        String enunciado = obj.get("enunciado").getAsString();
        int idEncuesta = obj.get("idEncuesta").getAsInt();
        
        JsonArray idRespuestas = obj.getAsJsonArray("idRespuestas");
        
        Pregunta p;
        switch (tipo) {
            case "libre":
                p = new PreguntaLibre(idEncuesta, idPregunta, enunciado);
                break;
            
            case "numerica":
                int rangoInf = obj.get("rangoInf").getAsInt();
                int rangoSup = obj.get("rangoSup").getAsInt();
                p = new PreguntaNumerica(idEncuesta, idPregunta, enunciado, rangoInf, rangoSup);
                break;
            
            case "opcion_unica":
                boolean ordenada = obj.get("ordenada").getAsBoolean();
                p = new PreguntaOpcionUnica(idPregunta, idEncuesta, enunciado, ordenada);
                JsonArray opcionesUnica = obj.getAsJsonArray("opciones");
                for (JsonElement elem : opcionesUnica) {
                    ((PreguntaOpcionUnica) p).addOpcion(elem.getAsString());
                }
                break;
            
            case "opcion_multiple":
                p = new PreguntaOpcionMultiple(idEncuesta, idPregunta, enunciado);
                JsonArray opcionesMultiple = obj.getAsJsonArray("opciones");
                for (JsonElement elem : opcionesMultiple) {
                    ((PreguntaOpcionMultiple) p).addOpcion(elem.getAsString());
                }
                int numMaxOpc = obj.get("numMaxOpc").getAsInt();
                ((PreguntaOpcionMultiple) p).setNumMaxOpc(numMaxOpc);
                break;
            
            default:
                throw new IllegalArgumentException("Tipo de pregunta desconocido: " + tipo);
        }
        
        // Restaurar idRespuestas
        for (JsonElement elem : idRespuestas) {
            p.guardarIdRespuesta(elem.getAsInt());
        }
        
        return p;
    }

    /**
     * Obtiene todas las preguntas del sistema.
     * 
     * @return Lista con todas las preguntas
     */
    public ArrayList<Pregunta> getAllPreguntas() {
        ArrayList<Pregunta> preguntas = new ArrayList<>();
        File carpetaEncuestas = new File("data/encuestas");
        
        File[] directoriosEncuestas = carpetaEncuestas.listFiles(File::isDirectory);
        if (directoriosEncuestas == null) return preguntas;
        
        for (File dirEncuesta : directoriosEncuestas) {
            try {
                int idEncuesta = Integer.parseInt(dirEncuesta.getName());
                File carpetaPreguntas = new File(dirEncuesta, "preguntas");
                
                if (!carpetaPreguntas.exists()) continue;
                
                File[] archivos = carpetaPreguntas.listFiles((dir, name) -> name.endsWith(".json"));
                if (archivos == null) continue;
                
                for (File archivo : archivos) {
                    int idPregunta = Integer.parseInt(archivo.getName().replace(".json", ""));
                    preguntas.add(getPregunta(idPregunta, idEncuesta));
                }
            }
            catch (NumberFormatException e) {
                continue;
            }
        }
        
        return preguntas;
    }

    /**
     * Verifica si existe una pregunta.
     * 
     * @param idPregunta ID de la pregunta
     * @return true si existe
     */
    public boolean existePregunta(int idPregunta) {
        int idEncuesta = getIdEncuesta(idPregunta);
        return existePregunta(idPregunta, idEncuesta);
    }

    /**
     * Verifica si existe una pregunta.
     * 
     * @param idPregunta ID de la pregunta
     * @param idEncuesta ID de la encuesta
     * @return true si existe
     */
    public boolean existePregunta(int idPregunta, int idEncuesta) {
        File archivo = new File("data/encuestas/" + idEncuesta + "/preguntas", idPregunta + ".json");
        return archivo.exists();
    }

    /**
     * Convierte una pregunta a JsonObject.
     * 
     * @param p Pregunta a convertir
     * @return JsonObject con la estructura correcta
     */
    private JsonObject preguntaToJson(Pregunta p) {
        JsonObject obj = new JsonObject();
        obj.addProperty("idPregunta", p.getIdPregunta());
        obj.addProperty("idEncuesta", p.getIdEncuesta());
        obj.addProperty("enunciado", p.getEnunciado());
        
        // Serializar idRespuestas
        JsonArray idRespuestas = new JsonArray();
        for (int idR : p.getIdRespuestas()) {
            idRespuestas.add(idR);
        }
        obj.add("idRespuestas", idRespuestas);
        
        // Serialización polimórfica según tipo
        if (p instanceof PreguntaLibre) {
            obj.addProperty("tipo", "libre");
        }
        else if (p instanceof PreguntaNumerica) {
            PreguntaNumerica pn = (PreguntaNumerica) p;
            obj.addProperty("tipo", "numerica");
            obj.addProperty("rangoInf", pn.getRangoInf());
            obj.addProperty("rangoSup", pn.getRangoSup());
        }
        else if (p instanceof PreguntaOpcionUnica) {
            PreguntaOpcionUnica pu = (PreguntaOpcionUnica) p;
            obj.addProperty("tipo", "opcion_unica");
            JsonArray opciones = new JsonArray();
            for (String op : pu.getOpciones()) {
                opciones.add(op);
            }
            obj.add("opciones", opciones);
            obj.addProperty("ordenada", pu.esOrdenada());
        }
        else if (p instanceof PreguntaOpcionMultiple) {
            PreguntaOpcionMultiple pm = (PreguntaOpcionMultiple) p;
            obj.addProperty("tipo", "opcion_multiple");
            JsonArray opciones = new JsonArray();
            for (String op : pm.getOpciones()) {
                opciones.add(op);
            }
            obj.add("opciones", opciones);
            obj.addProperty("numMaxOpc", pm.getNumMaxOpc());
        }
        
        return obj;
    }

    /**
     * Guarda una nueva pregunta en disco.
     * 
     * @param p Pregunta a guardar
     * @throws IllegalArgumentException si la pregunta ya existe
     */
    public void guardarPregunta(Pregunta p) {
        int idEncuesta = p.getIdEncuesta();
        guardarPregunta(p, idEncuesta);
    }

    /**
     * Guarda una nueva pregunta en disco.
     * 
     * @param p Pregunta a guardar
     * @param idEncuesta ID de la encuesta
     * @throws IllegalArgumentException si la pregunta ya existe
     */
    public void guardarPregunta(Pregunta p, int idEncuesta) {
        int id = p.getIdPregunta();
        File carpeta = new File("data/encuestas/" + idEncuesta + "/preguntas");
        
        if (!carpeta.exists()) carpeta.mkdirs();
        
        File archivo = new File(carpeta, id + ".json");
        if (archivo.exists()) {
            throw new IllegalArgumentException("La pregunta " + id + " ya existe");
        }

        try (FileWriter writer = new FileWriter(archivo)) {
            gson.toJson(preguntaToJson(p), writer);
            actualizarIndice(id, idEncuesta);
        }
        catch (IOException e) {
            throw new RuntimeException("Error guardando pregunta " + id, e);
        }
    }

    /**
     * Actualiza una pregunta existente.
     * 
     * @param p Pregunta a actualizar
     * @throws IllegalArgumentException si la pregunta no existe
     */
    public void actualizarPregunta(Pregunta p) {
        int idEncuesta = getIdEncuesta(p.getIdPregunta());
        actualizarPregunta(p, idEncuesta);
    }

    /**
     * Actualiza una pregunta existente.
     * 
     * @param p Pregunta a actualizar
     * @param idEncuesta ID de la encuesta
     * @throws IllegalArgumentException si la pregunta no existe
     */
    public void actualizarPregunta(Pregunta p, int idEncuesta) {
        int id = p.getIdPregunta();
        File archivo = new File("data/encuestas/" + idEncuesta + "/preguntas", id + ".json");
        
        if (!archivo.exists()) {
            throw new IllegalArgumentException("La pregunta " + id + " no existe");
        }

        try (FileWriter writer = new FileWriter(archivo)) {
            gson.toJson(preguntaToJson(p), writer);
        }
        catch (IOException e) {
            throw new RuntimeException("Error actualizando pregunta " + id, e);
        }
    }

    /**
     * Elimina una pregunta del disco.
     * 
     * @param idPregunta ID de la pregunta
     * @throws IllegalArgumentException si la pregunta no existe
     */
    public void eliminarPregunta(int idPregunta) {
        int idEncuesta = getIdEncuesta(idPregunta);
        eliminarPregunta(idPregunta, idEncuesta);
    }

    /**
     * Elimina una pregunta del disco.
     * 
     * @param idPregunta ID de la pregunta
     * @param idEncuesta ID de la encuesta
     * @throws IllegalArgumentException si la pregunta no existe
     */
    public void eliminarPregunta(int idPregunta, int idEncuesta) {
        File archivo = new File("data/encuestas/" + idEncuesta + "/preguntas", idPregunta + ".json");
        
        if (!archivo.exists()) {
            throw new IllegalArgumentException("La pregunta " + idPregunta + " no existe");
        }
        
        boolean eliminado = archivo.delete();
        if (!eliminado) {
            throw new RuntimeException("No se pudo eliminar pregunta " + idPregunta);
        }
        
        eliminarDeIndice(idPregunta);
    }

    /**
     * Actualiza el índice con un nuevo mapeo idPregunta → idEncuesta.
     * 
     * @param idPregunta ID de la pregunta
     * @param idEncuesta ID de la encuesta
     */
    private void actualizarIndice(int idPregunta, int idEncuesta) {
        String path = "data/indexPreguntas.json";
        JsonObject indice;
        
        try (FileReader reader = new FileReader(path)) {
            indice = JsonParser.parseReader(reader).getAsJsonObject();
        }
        catch (FileNotFoundException e) {
            indice = new JsonObject();
        }
        catch (IOException e) {
            throw new RuntimeException("Error leyendo índice de preguntas", e);
        }
        
        indice.addProperty(String.valueOf(idPregunta), idEncuesta);
        
        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(indice, writer);
        }
        catch (IOException e) {
            throw new RuntimeException("Error actualizando índice de preguntas", e);
        }
    }

    /**
     * Elimina un mapeo del índice.
     * 
     * @param idPregunta ID de la pregunta
     */
    private void eliminarDeIndice(int idPregunta) {
        String path = "data/indexPreguntas.json";
        JsonObject indice;
        
        try (FileReader reader = new FileReader(path)) {
            indice = JsonParser.parseReader(reader).getAsJsonObject();
        }
        catch (IOException e) {
            return; // índice no existe
        }
        
        indice.remove(String.valueOf(idPregunta));
        
        try (FileWriter writer = new FileWriter(path)) {
            gson.toJson(indice, writer);
        }
        catch (IOException e) {
            throw new RuntimeException("Error actualizando índice de preguntas", e);
        }
    }

    /**
     * Guarda el siguiente ID disponible.
     * 
     * @param newid Nuevo ID
     */
    public void guardarNextId(int newid) {
        String FILE = "data/nextId.json";

        try (FileReader reader = new FileReader(FILE)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            jsonObject.addProperty("nextIdPregunta", newid);

            try (FileWriter writer = new FileWriter(FILE)) {
                gson.toJson(jsonObject, writer);
            }
            catch (IOException e) {
                throw new RuntimeException("Error escribiendo el archivo JSON", e);
            }
        }
        catch (Exception exc) {
            throw new RuntimeException("Error leyendo el archivo JSON", exc);
        }
    }
}
```

---

## GestorPersistenciaPregunta.java

```java
package persistence;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import domain.Pregunta;

/**
 * Cache de Pregunta
 * 
 * @author Denis Roca
 */
public class GestorPersistenciaPregunta {

    private static GestorPersistenciaPregunta singletonInstance;

    // Límites de cache
    private static final int CACHE_LIMIT = 64;

    // Cache con LinkedHashMap (accessOrder=true para LRU tracking)
    private final LinkedHashMap<Integer, Pregunta> preguntas;

    // Controlador de persistencia pregunta en disco
    private final CtrlPersistenciaPregunta ctrlFicheroPregunta;

    /** ID para la próxima pregunta a crear */
    private int nextIdPregunta;

    /**
     * Obtiene instancia única (patrón Singleton).
     * 
     * @return Instancia única de GestorPersistenciaPregunta
     */
    public static GestorPersistenciaPregunta getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new GestorPersistenciaPregunta();
        }
        return singletonInstance;
    }

    private GestorPersistenciaPregunta() {
        // Inicializar LinkedHashMap con accessOrder=true para tracking LRU
        preguntas = new LinkedHashMap<>(CACHE_LIMIT, 2.0f, true) {
            protected boolean removeEldestEntry(Map.Entry<Integer, Pregunta> eldest) {
                return size() > CACHE_LIMIT;
            }
        };

        ctrlFicheroPregunta = CtrlPersistenciaPregunta.getInstance();
        nextIdPregunta = ctrlFicheroPregunta.getNextId();
    }

    /**
     * Busca la Pregunta en los ficheros y la añade a la cache.
     * 
     * @param idP ID de la pregunta
     * @return Pregunta encontrada
     * @throws IllegalArgumentException si no existe
     */
    private Pregunta sacarPreguntaFichero(int idP) {
        Pregunta p = ctrlFicheroPregunta.getPregunta(idP);
        preguntas.put(idP, p);
        return p;
    }

    /**
     * Busca la Pregunta en los ficheros y la añade a la cache.
     * 
     * @param idP ID de la pregunta
     * @param idEncuesta ID de la encuesta
     * @return Pregunta encontrada
     * @throws IllegalArgumentException si no existe
     */
    private Pregunta sacarPreguntaFichero(int idP, int idEncuesta) {
        Pregunta p = ctrlFicheroPregunta.getPregunta(idP, idEncuesta);
        preguntas.put(idP, p);
        return p;
    }

    /**
     * Guarda una nueva pregunta en el sistema.
     * 
     * @param p Pregunta a guardar
     * @throws IllegalArgumentException si ya existe
     */
    public void guardarPregunta(Pregunta p) {
        int id = p.getIdPregunta();
        int idEncuesta = p.getIdEncuesta();
        
        if (preguntas.containsKey(id)) {
            throw new IllegalArgumentException("La pregunta " + id + " ya existe en cache");
        }
        
        ctrlFicheroPregunta.guardarPregunta(p, idEncuesta);
        preguntas.put(id, p);
    }

    /**
     * Guarda una nueva pregunta en el sistema.
     * 
     * @param p Pregunta a guardar
     * @param idEncuesta ID de la encuesta
     * @throws IllegalArgumentException si ya existe
     */
    public void guardarPregunta(Pregunta p, int idEncuesta) {
        int id = p.getIdPregunta();
        
        if (preguntas.containsKey(id)) {
            throw new IllegalArgumentException("La pregunta " + id + " ya existe en cache");
        }
        
        ctrlFicheroPregunta.guardarPregunta(p, idEncuesta);
        preguntas.put(id, p);
    }

    /**
     * Obtiene el siguiente ID disponible para una nueva pregunta.
     * 
     * @return Siguiente ID disponible
     */
    public int getNextIdPregunta() {
        int id = nextIdPregunta;
        nextIdPregunta++;
        ctrlFicheroPregunta.guardarNextId(nextIdPregunta);
        return id;
    }

    /**
     * Obtiene una pregunta por su ID.
     * 
     * @param id ID de la pregunta
     * @return Pregunta correspondiente
     * @throws IllegalArgumentException si no existe
     */
    public Pregunta getPregunta(int id) {
        Pregunta p = preguntas.get(id);
        
        if (p == null) {
            p = sacarPreguntaFichero(id);
        }
        
        return p;
    }

    /**
     * Obtiene una pregunta por su ID.
     * 
     * @param id ID de la pregunta
     * @param idEncuesta ID de la encuesta
     * @return Pregunta correspondiente
     * @throws IllegalArgumentException si no existe
     */
    public Pregunta getPregunta(int id, int idEncuesta) {
        Pregunta p = preguntas.get(id);
        
        if (p == null) {
            p = sacarPreguntaFichero(id, idEncuesta);
        }
        
        return p;
    }

    /**
     * Verifica si existe una pregunta.
     * 
     * @param id ID de la pregunta
     * @return true si existe
     */
    public boolean existePregunta(int id) {
        if (preguntas.containsKey(id)) {
            return true;
        }
        
        boolean existe = ctrlFicheroPregunta.existePregunta(id);
        if (existe) {
            sacarPreguntaFichero(id);
        }
        
        return existe;
    }

    /**
     * Verifica si existe una pregunta.
     * 
     * @param id ID de la pregunta
     * @param idEncuesta ID de la encuesta
     * @return true si existe
     */
    public boolean existePregunta(int id, int idEncuesta) {
        if (preguntas.containsKey(id)) {
            return true;
        }
        
        boolean existe = ctrlFicheroPregunta.existePregunta(id, idEncuesta);
        if (existe) {
            sacarPreguntaFichero(id, idEncuesta);
        }
        
        return existe;
    }

    /**
     * Obtiene todas las preguntas.
     * 
     * @return Lista con todas las preguntas
     */
    public ArrayList<Pregunta> getPreguntas() {
        return ctrlFicheroPregunta.getAllPreguntas();
    }

    /**
     * Actualiza una pregunta existente.
     * 
     * @param p Pregunta a actualizar
     */
    public void actualizarPregunta(Pregunta p) {
        preguntas.put(p.getIdPregunta(), p);
        ctrlFicheroPregunta.actualizarPregunta(p);
    }

    /**
     * Actualiza una pregunta existente.
     * 
     * @param p Pregunta a actualizar
     * @param idEncuesta ID de la encuesta
     */
    public void actualizarPregunta(Pregunta p, int idEncuesta) {
        preguntas.put(p.getIdPregunta(), p);
        ctrlFicheroPregunta.actualizarPregunta(p, idEncuesta);
    }

    /**
     * Elimina una pregunta.
     * 
     * @param id ID de la pregunta
     * @throws IllegalArgumentException si no existe
     */
    public void eliminarPregunta(int id) {
        preguntas.remove(id);
        ctrlFicheroPregunta.eliminarPregunta(id);
    }

    /**
     * Elimina una pregunta.
     * 
     * @param id ID de la pregunta
     * @param idEncuesta ID de la encuesta
     * @throws IllegalArgumentException si no existe
     */
    public void eliminarPregunta(int id, int idEncuesta) {
        preguntas.remove(id);
        ctrlFicheroPregunta.eliminarPregunta(id, idEncuesta);
    }
}
```

---

## Optimizaciones en Capas Superiores

### CtrlDominioPregunta.java

Al igual que con las respuestas, se deben usar las versiones optimizadas donde se tenga acceso al `idEncuesta`:

```java
// En crearPregunta() - ya se conoce idEncuesta
public int crearPregunta(int idEncuesta, String enunciado, TipoPregunta tipo, ...) {
    // ... crear pregunta p
    gestor.guardarPregunta(p, idEncuesta); // versión optimizada
    // ...
}

// En modificarPregunta() - obtener pregunta primero
public void modificarPregunta(int idPregunta, ...) {
    Pregunta p = getPregunta(idPregunta);
    int idEncuesta = p.getIdEncuesta();
    // ... modificar p
    gestor.actualizarPregunta(p, idEncuesta); // versión optimizada
}

// En eliminarPregunta() - obtener pregunta primero
public void eliminarPregunta(int idPregunta) {
    Pregunta p = getPregunta(idPregunta);
    int idEncuesta = p.getIdEncuesta();
    // ... operaciones
    gestor.eliminarPregunta(idPregunta, idEncuesta); // versión optimizada
}
```

---

## Ventajas de esta Arquitectura

1. **Acceso directo cuando es posible**: Evita lookups innecesarios en el índice
2. **Compatibilidad**: Mantiene versiones sin `idEncuesta` para casos donde no esté disponible
3. **Consistencia**: Sigue el mismo patrón que las respuestas
4. **Escalabilidad**: Archivos individuales permiten operaciones paralelas
5. **Mantenimiento**: Índice se actualiza automáticamente

---

## Tareas de Migración

1. Crear `data/indexPreguntas.json` con mapeos existentes
2. Mover preguntas de `DATA/preguntas.json` a estructura individual
3. Implementar `CtrlPersistenciaPregunta` con sobrecargas
4. Actualizar `GestorPersistenciaPregunta` con sobrecargas
5. Optimizar llamadas en `CtrlDominioPregunta`
6. Actualizar `GestorPersistencia` para exponer ambas versiones
