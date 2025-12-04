package persistence;

import com.google.gson.*;
import domain.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

// pnd de hacer todo

/**
 * Controlador de persistencia de respuestas en disco.
 * 
 * Gestiona la lectura y escritura de respuestas en formato JSON, incluyendo
 * el manejo de la jerarquía polimórfica de respuestas (Libre, Numerica,
 * OpcionUnica, OpcionMultiple).
 * No mantiene cache interna - la cache es gestionada por GestorPersistencia.
 * 
 * RESPONSABILIDADES:
 * - Cargar respuestas desde archivo JSON con deserialización polimórfica
 * - Guardar respuestas a archivo JSON con información de tipo
 * - Gestionar operaciones I/O con disco
 * 
 * ATRIBUTOS PRINCIPALES:
 * - FILE: Ruta del archivo JSON
 * - gson: Serializador/deserializador JSON con custom deserializer
 * 
 * MÉTODOS PRINCIPALES:
 * - cargarRespuesta(): Lee una respuesta específica del disco
 * - cargarTodas(): Lee todas las respuestas del disco
 * - guardar(): Escribe mapa completo de respuestas a disco
 * 
 * @author Yaoyao Guo
 */
public class CtrlPersistenciaRespuesta {
    private static final String FILE = "DATA/respuestas.json";
    private final Gson gson;

    /**
     * Crea controlador de persistencia de respuestas con deserializador custom.
     */
    public CtrlPersistenciaRespuesta() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Respuesta.class, new RespuestaDeserializer());
        this.gson = builder.create();
        crearDirectorio();
    }

    /**
     * Carga una respuesta específica desde disco.
     * 
     * @param id ID de la respuesta
     * @return Respuesta encontrada, o null si no existe
     */
    public Respuesta cargarRespuesta(int id) {
        Map<Integer, Respuesta> todas = cargarTodas();
        return todas.get(id);
    }

    /**
     * Carga todas las respuestas desde disco.
     * 
     * @return Mapa de ID → Respuesta (vacío si archivo no existe)
     */
    public Map<Integer, Respuesta> cargarTodas() {
        File f = new File(FILE);
        if (!f.exists()) return new HashMap<>();
        
        try (FileReader r = new FileReader(f)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
            if (!root.has("respuestas")) return new HashMap<>();
            
            JsonObject respuestasObj = root.getAsJsonObject("respuestas");
            Map<Integer, Respuesta> result = new HashMap<>();
            
            for (Map.Entry<String, JsonElement> entry : respuestasObj.entrySet()) {
                Respuesta resp = gson.fromJson(entry.getValue(), Respuesta.class);
                result.put(Integer.parseInt(entry.getKey()), resp);
            }
            return result;
        } catch (IOException e) {
            return new HashMap<>();
        }
    }

    /**
     * Guarda el mapa completo de respuestas a disco.
     * 
     * @param respuestas Mapa de ID → Respuesta
     */
    public void guardar(Map<Integer, Respuesta> respuestas) {
        JsonObject root = new JsonObject();
        JsonObject respuestasObj = new JsonObject();
        
        for (Map.Entry<Integer, Respuesta> e : respuestas.entrySet()) {
            Respuesta r = e.getValue();
            JsonObject rObj = new JsonObject();
            rObj.addProperty("id", r.getId());
            rObj.addProperty("nomUsuario", r.getNomUsuario());
            rObj.addProperty("idPregunta", r.getIdPregunta());
            
            // Serialización polimórfica con tipo
            if (r instanceof RespuestaLibre) {
                rObj.addProperty("tipo", "libre");
                rObj.addProperty("texto", ((RespuestaLibre) r).getRespuesta());
            } else if (r instanceof RespuestaNumerica) {
                rObj.addProperty("tipo", "numerica");
                rObj.addProperty("valor", ((RespuestaNumerica) r).getRespuesta());
            } else if (r instanceof RespuestaOpcionUnica) {
                rObj.addProperty("tipo", "opcion_unica");
                rObj.addProperty("indice", ((RespuestaOpcionUnica) r).getRespuesta());
            } else if (r instanceof RespuestaOpcionMultiple) {
                rObj.addProperty("tipo", "opcion_multiple");
                rObj.add("indices", gson.toJsonTree(((RespuestaOpcionMultiple) r).getRespuesta()));
            }
            
            respuestasObj.add(String.valueOf(e.getKey()), rObj);
        }
        root.add("respuestas", respuestasObj);
        
        try (FileWriter w = new FileWriter(FILE)) {
            gson.toJson(root, w);
        } catch (IOException e) {
            throw new RuntimeException("Error guardando respuestas: " + e.getMessage());
        }
    }

    /**
     * Crea directorio DATA si no existe.
     */
    private void crearDirectorio() {
        new File("DATA").mkdirs();
    }

    /**
     * Deserializador custom para jerarquía de Respuesta.
     */
    private static class RespuestaDeserializer implements JsonDeserializer<Respuesta> {
        @Override
        public Respuesta deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            int id = obj.get("id").getAsInt();
            String nomUsuario = obj.get("nomUsuario").getAsString();
            int idPregunta = obj.get("idPregunta").getAsInt();
            String tipo = obj.get("tipo").getAsString();
            
            switch (tipo) {
                case "libre":
                    return new RespuestaLibre(id, nomUsuario, idPregunta, 
                        obj.get("texto").getAsString());
                case "numerica":
                    return new RespuestaNumerica(id, nomUsuario, idPregunta,
                        obj.get("valor").getAsDouble());
                case "opcion_unica":
                    return new RespuestaOpcionUnica(id, nomUsuario, idPregunta,
                        obj.get("indice").getAsInt());
                case "opcion_multiple":
                    JsonArray indicesArray = obj.getAsJsonArray("indices");
                    int[] indices = new int[indicesArray.size()];
                    for (int i = 0; i < indicesArray.size(); i++) {
                        indices[i] = indicesArray.get(i).getAsInt();
                    }
                    return new RespuestaOpcionMultiple(id, nomUsuario, idPregunta, indices);
                default:
                    throw new JsonParseException("Tipo respuesta desconocido: " + tipo);
            }
        }
    }
}
