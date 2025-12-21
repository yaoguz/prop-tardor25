package persistence;

import com.google.gson.*;
import domain.*;
import java.io.*;
import java.util.*;

/**
 * Controlador de persistencia de respuestas en disco.
 * 
 * Gestiona la lectura y escritura de respuestas en formato JSON.
 * Cada respuesta se almacena en data/encuestas/{idEncuesta}/respuestas/{idRespuesta}.json
 * 
 * RESPONSABILIDADES:
 * - Cargar respuestas desde archivo JSON
 * - Guardar respuestas a archivo JSON
 * - Gestionar operaciones I/O con disco
 * - Serializar según tipo de respuesta
 * 
 * ATRIBUTOS PRINCIPALES:
 * - gson: serializador/deserializador JSON
 * 
 * METODOS PRINCIPALES:
 * - getRespuesta(): lee una respuesta específica del disco
 * - getAllRespuestas(): lee todas las respuestas del sistema
 * - guardarRespuesta(): escribe una nueva respuesta a disco
 * - actualizarRespuesta(): actualiza una respuesta existente
 * - eliminarRespuesta(): elimina una respuesta del disco
 * 
 * @author Yaoyao Guo
 */
public class CtrlPersistenciaRespuesta {

    private static CtrlPersistenciaRespuesta singletonInstance;

    private final Gson gson;

    /**
     * Obtiene instancia única (patrón Singleton).
     * 
     * @return Instancia única de CtrlPersistenciaRespuesta
     */
    public static CtrlPersistenciaRespuesta getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new CtrlPersistenciaRespuesta();
        }
        return singletonInstance;
    }

    /**
     * Crea controlador de persistencia de respuestas.
     */
    public CtrlPersistenciaRespuesta() {
        // lo pone bonito cuando escribe (no todo en una linea)
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Obtiene el siguiente ID disponible para una nueva respuesta.
     * 
     * @return Siguiente ID de respuesta
     */
    public int getNextId () {
        String FILE = "data/nextId.json";

        try (FileReader reader = new FileReader(FILE)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();
            return jsonObject.get("nextIdRespuesta").getAsInt();
        }
        catch (Exception exc) {
            throw new RuntimeException("Error leyendo el archivo JSON 6", exc);
        }
    }

    private int getIdEncuesta(int idRespuesta) {
        String path = "data/indexRespuestas.json";
        try (FileReader reader = new FileReader(path)) {
            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
            return obj.get(String.valueOf(idRespuesta)).getAsInt();
        }
        catch (Exception e) {
            throw new RuntimeException("Error leyendo índice para respuesta " + idRespuesta, e);
        }
    }

    /**
     * Obtiene una respuesta desde disco.
     * 
     * @param idRespuesta ID de la respuesta
     * @return Respuesta encontrada
     * @throws IllegalArgumentException si la respuesta no existe
     */
    public Respuesta getRespuesta(int idRespuesta) {
        int idEncuesta = getIdEncuesta(idRespuesta);
        return getRespuesta(idRespuesta, idEncuesta);
    }

    /**
     * Obtiene una respuesta desde disco.
     * 
     * @param idRespuesta ID de la respuesta
     * @param idEncuesta ID de la encuesta
     * @return Respuesta encontrada
     * @throws IllegalArgumentException si la respuesta no existe
     */
    public Respuesta getRespuesta(int idRespuesta, int idEncuesta) {
        String FILE = "data/encuestas/" + idEncuesta + "/respuestas/" + idRespuesta + ".json";
        
        try (FileReader reader = new FileReader(FILE)) {
            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
            int idPregunta = obj.get("idPregunta").getAsInt();
            String nomUsuario = obj.get("nomUsuario").getAsString();
            String tipo = obj.get("tipo").getAsString();
            JsonElement respuestaElem = obj.get("respuesta");

            switch (tipo) {
                case "numerica":
                    double valor = respuestaElem.getAsDouble();
                    return new RespuestaNumerica(idRespuesta, nomUsuario, idPregunta, valor);
                
                case "libre":
                    String texto = respuestaElem.getAsString();
                    return new RespuestaLibre(idRespuesta, nomUsuario, idPregunta, texto);
                
                case "opcion_unica":
                    int indice = respuestaElem.getAsInt();
                    return new RespuestaOpcionUnica(idRespuesta, nomUsuario, idPregunta, indice);
                
                case "opcion_multiple":
                    JsonArray arr = respuestaElem.getAsJsonArray();
                    int[] indices = new int[arr.size()];
                    for (int i = 0; i < arr.size(); i++) {
                        indices[i] = arr.get(i).getAsInt();
                    }
                    return new RespuestaOpcionMultiple(idRespuesta, nomUsuario, idPregunta, indices);
                
                default:
                    throw new IllegalArgumentException("Tipo de respuesta desconocido: " + tipo);
            }
        }
        catch (FileNotFoundException e) {
            throw new IllegalArgumentException("La respuesta " + idRespuesta + " no existe");
        }
        catch (Exception e) {
            throw new RuntimeException("Error leyendo respuesta " + idRespuesta, e);
        }
    }

    /**
     * Obtiene todas las respuestas del sistema.
     * 
     * @return Lista con todas las respuestas
     */
    public ArrayList<Respuesta> getAllRespuestas() {
        ArrayList<Respuesta> respuestas = new ArrayList<>();
        File carpetaEncuestas = new File("data/encuestas");
        
        File[] directoriosEncuestas = carpetaEncuestas.listFiles(File::isDirectory);
        if (directoriosEncuestas == null) return respuestas; // ninguna encuesta
        
        for (File dirEncuesta : directoriosEncuestas) {
            try {
                int idEncuesta = Integer.parseInt(dirEncuesta.getName());
                File carpetaRespuestas = new File(dirEncuesta, "respuestas");
                
                if (!carpetaRespuestas.exists()) continue; // ninguna respuesta
                
                File[] archivos = carpetaRespuestas.listFiles((dir, name) -> name.endsWith(".json"));
                if (archivos == null) continue;
                
                for (File archivo : archivos) {
                    int idRespuesta = Integer.parseInt(archivo.getName().replace(".json", ""));
                    respuestas.add(getRespuesta(idRespuesta, idEncuesta));
                }
            }
            catch (NumberFormatException e) {
                continue;
            }
        }
        
        return respuestas;
    }

    /**
     * Verifica si existe una respuesta.
     * 
     * @param idRespuesta ID de la respuesta
     * @return true si existe
     */
    public boolean existeRespuesta(int idRespuesta) {
        int idEncuesta = getIdEncuesta(idRespuesta);
        return existeRespuesta(idRespuesta, idEncuesta);
    }

    /**
     * Verifica si existe una respuesta.
     * 
     * @param idRespuesta ID de la respuesta
     * @param idEncuesta ID de la encuesta
     * @return true si existe
     */
    public boolean existeRespuesta(int idRespuesta, int idEncuesta) {
        File archivo = new File("data/encuestas/" + idEncuesta + "/respuestas", idRespuesta + ".json");
        return archivo.exists();
    }

    /**
     * Convierte una respuesta a JsonObject.
     * 
     * @param r Respuesta a convertir
     * @return JsonObject con la estructura correcta
     */
    private JsonObject respuestaToJson(Respuesta r) {
        JsonObject obj = new JsonObject();
        obj.addProperty("idRespuesta", r.getId());
        obj.addProperty("idPregunta", r.getIdPregunta());
        obj.addProperty("nomUsuario", r.getNomUsuario());
        obj.addProperty("tipo", r.getTipo());
        
        Object respuesta = r.getRespuesta();
        if (respuesta instanceof int[]) {
            JsonArray arr = new JsonArray();
            for (int i : (int[]) respuesta) {
                arr.add(i);
            }
            obj.add("respuesta", arr);
        } 
        else if (respuesta instanceof Double) {
            obj.addProperty("respuesta", (Double) respuesta);
        } 
        else if (respuesta instanceof Integer) {
            obj.addProperty("respuesta", (Integer) respuesta);
        } 
        else if (respuesta instanceof String) {
            obj.addProperty("respuesta", (String) respuesta);
        }
        
        return obj;
    }

    /**
     * Guarda una nueva respuesta en disco.
     * 
     * @param r Respuesta a guardar
     * @throws IllegalArgumentException si la respuesta ya existe
     */
    public void guardarRespuesta(Respuesta r) {
        int idEncuesta = getIdEncuesta(r.getId());
        guardarRespuesta(r, idEncuesta);
    }

    /**
     * Guarda una nueva respuesta en disco.
     * 
     * @param r Respuesta a guardar
     * @param idEncuesta ID de la encuesta a la que pertenece
     * @throws IllegalArgumentException si la respuesta ya existe
     */
    public void guardarRespuesta(Respuesta r, int idEncuesta) {
        int id = r.getId();
        File carpeta = new File("data/encuestas/" + idEncuesta + "/respuestas");
        
        if (!carpeta.exists()) carpeta.mkdirs(); // primera respuesta de esta encuesta
        
        File archivo = new File(carpeta, id + ".json");
        if (archivo.exists()) {
            throw new IllegalArgumentException("La respuesta " + id + " ya existe");
        }

        try (FileWriter writer = new FileWriter(archivo)) {
            gson.toJson(respuestaToJson(r), writer);
            actualizarIndice(id, idEncuesta);
        }
        catch (IOException e) {
            throw new RuntimeException("Error guardando respuesta " + id, e);
        }
    }

    /**
     * Actualiza una respuesta existente.
     * 
     * @param r Respuesta a actualizar
     * @throws IllegalArgumentException si la respuesta no existe
     */
    public void actualizarRespuesta(Respuesta r) {
        int idEncuesta = getIdEncuesta(r.getId());
        actualizarRespuesta(r, idEncuesta);
    }

    /**
     * Actualiza una respuesta existente.
     * 
     * @param r Respuesta a actualizar
     * @param idEncuesta ID de la encuesta
     * @throws IllegalArgumentException si la respuesta no existe
     */
    public void actualizarRespuesta(Respuesta r, int idEncuesta) {
        int id = r.getId();
        File archivo = new File("data/encuestas/" + idEncuesta + "/respuestas/", id + ".json");
        
        if (!archivo.exists()) {
            throw new IllegalArgumentException("La respuesta " + id + " no existe");
        }

        try (FileWriter writer = new FileWriter(archivo)) {
            gson.toJson(respuestaToJson(r), writer);
        }
        catch (IOException e) {
            throw new RuntimeException("Error actualizando respuesta " + id, e);
        }
    }

    /**
     * Elimina una respuesta del disco.
     * 
     * @param idRespuesta ID de la respuesta
     * @throws IllegalArgumentException si la respuesta no existe
     */
    public void eliminarRespuesta(int idRespuesta) {
        int idEncuesta = getIdEncuesta(idRespuesta);
        eliminarRespuesta(idRespuesta, idEncuesta);
    }

    /**
     * Elimina una respuesta del disco.
     * 
     * @param idRespuesta ID de la respuesta
     * @param idEncuesta ID de la encuesta
     * @throws IllegalArgumentException si la respuesta no existe
     */
    public void eliminarRespuesta(int idRespuesta, int idEncuesta) {
        File archivo = new File("data/encuestas/" + idEncuesta + "/respuestas/", idRespuesta + ".json");
        
        if (!archivo.delete()) {
            throw new IllegalArgumentException("No existe la respuesta " + idRespuesta);
        }
        
        eliminarDeIndice(idRespuesta);
    }

    /**
     * Actualiza el índice de respuestas añadiendo un nuevo mapeo.
     * 
     * @param idRespuesta ID de la respuesta
     * @param idEncuesta ID de la encuesta
     */
    private void actualizarIndice(int idRespuesta, int idEncuesta) {
        String path = "data/indexRespuestas.json";
        try (FileReader reader = new FileReader(path)) {
            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
            obj.addProperty(String.valueOf(idRespuesta), idEncuesta);
            
            try (FileWriter writer = new FileWriter(path)) {
                gson.toJson(obj, writer);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error actualizando índice", e);
        }
    }

    /**
     * Elimina un mapeo del índice de respuestas.
     * 
     * @param idRespuesta ID de la respuesta
     */
    private void eliminarDeIndice(int idRespuesta) {
        String path = "data/indexRespuestas.json";
        try (FileReader reader = new FileReader(path)) {
            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
            obj.remove(String.valueOf(idRespuesta));
            
            try (FileWriter writer = new FileWriter(path)) {
                gson.toJson(obj, writer);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error actualizando índice", e);
        }
    }

    /**
     * Guarda el siguiente ID disponible en disco.
     * 
     * @param newid Nuevo ID de respuesta
     */
    public void guardarNextId (int newid) {
        String FILE = "data/nextId.json";

        try (FileReader reader = new FileReader(FILE)) {
            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            // .addProperty reemplaza el valor si la clave ya existe, o la crea si no
            jsonObject.addProperty("nextIdRespuesta", newid);

            try (FileWriter writer = new FileWriter(FILE)) {
                gson.toJson(jsonObject, writer); // vuelca el objeto modificado al archivo
            } 
            catch (IOException e) {
                throw new RuntimeException("Error escribiendo el archivo JSON", e);
            }
        }
        catch (Exception exc) {
            throw new RuntimeException("Error leyendo el archivo JSON 7", exc);
        }
    }
}
