package persistence;

import com.google.gson.*;
import domain.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Controlador de persistencia de preguntas en disco.
 * 
 * Gestiona la lectura y escritura de preguntas en formato JSON, incluyendo
 * el manejo de la jerarquía polimórfica de preguntas (Libre, Numerica,
 * OpcionUnica, OpcionMultiple).
 * No mantiene cache interna - la cache es gestionada por GestorPersistencia.
 * 
 * RESPONSABILIDADES:
 * - Cargar preguntas desde archivo JSON con deserialización polimórfica
 * - Guardar preguntas a archivo JSON con información de tipo
 * - Gestionar operaciones I/O con disco
 * 
 * ATRIBUTOS PRINCIPALES:
 * - FILE: Ruta del archivo JSON
 * - gson: Serializador/deserializador JSON con custom deserializer
 * 
 * MÉTODOS PRINCIPALES:
 * - cargarPregunta(): Lee una pregunta específica del disco
 * - cargarTodas(): Lee todas las preguntas del disco
 * - guardar(): Escribe mapa completo de preguntas a disco
 * 
 * @author Denis Roca
 */
public class CtrlPersistenciaPregunta {
    private static final carpeta = "data/encuestas";
    private final Gson gson;
    
    private final CtrlPersistenciaPreguta singletonInstance;

    /**
     * Crea controlador de persistencia de preguntas con deserializador custom.
     */
    public CtrlPersistenciaPregunta() {
        GsonBuilder builder = new GsonBuilder();
        builder.registerTypeAdapter(Pregunta.class, new PreguntaDeserializer());
        this.gson = builder.create();
    }

    /**
     * Obtiene la instancia única del controlador (patrón singleton).
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
     * Carga una pregunta específica desde disco.
     * 
     * @param id ID de la pregunta
     * @return Pregunta encontrada, o null si no existe
     */
    public Pregunta getPregunta(int id) {
    	File carpetaRaiz = new File(carpeta);
    	File[] jsonFiles = carpetaRaiz.listFiles(file -> file.isFile() && file.getName()..endsWith(".json"));
    	int idEncuesta = -1;
    	try{
    		for(File f : jsonFiles){
    			FileReader reader = new FileReader(f);
    			JsonObject encuesta = JsonParser.parseReader(reader).getAsJsonObject();
    			JsonArray listaPreguntas = encuesta.getAsJsonArray("preguntas");
    			for (JsonElement pregunta : listaPreguntas){
    				if(pregunta.getAsInt() == id) idEncuesta = encuesta.get("id").getAsInt();
    				break;
    			}
    			if(idEncuesta != -1) break;
            }
        } catch(Exception e) {
            throw new RuntimeException("Fichero no válido", e);
        }
    	if(idEncuesta == -1) throw new IllegalArgumentException("No existe la pregunta");
    	File carpetaPreguntas = new File(carpeta + "/" + idEncuesta + "/preguntas/" + id + ".json");
		// por acabar <---------------------------------------------------------------------------
    }

    /**
     * Carga todas las preguntas desde disco.
     * 
     * @return Mapa de <ID,Pregunta> (vacío si archivo no existe)
     */
    public Map<Integer, Pregunta> cargarTodas() {
        File f = new File(FILE);
        if (!f.exists()) return new HashMap<>();
        
        try (FileReader r = new FileReader(f)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
            if (!root.has("preguntas")) return new HashMap<>();
            
            JsonObject preguntasObj = root.getAsJsonObject("preguntas");
            Map<Integer, Pregunta> result = new HashMap<>();
            
            for (Map.Entry<String, JsonElement> entry : preguntasObj.entrySet()) {
                Pregunta p = gson.fromJson(entry.getValue(), Pregunta.class);
                result.put(Integer.parseInt(entry.getKey()), p);
            }
            return result;
        } catch (IOException e) {
            return new HashMap<>();
        }
    }

    /**
     * Guarda el mapa completo de preguntas a disco.
     * 
     * @param preguntas Mapa de <ID,Pregunta>
     */
    public void guardar(Map<Integer, Pregunta> preguntas) {
        JsonObject root = new JsonObject();
        JsonObject preguntasObj = new JsonObject();
        
        for (Map.Entry<Integer, Pregunta> e : preguntas.entrySet()) {
            Pregunta p = e.getValue();
            JsonObject pObj = new JsonObject();
            pObj.addProperty("idPregunta", p.getIdPregunta());
            pObj.addProperty("idEncuesta", p.getIdEncuesta());
            pObj.addProperty("enunciado", p.getEnunciado());
            
            // Serialización polimórfica con tipo
            if (p instanceof PreguntaLibre) {
                pObj.addProperty("tipo", "libre");
            } else if (p instanceof PreguntaNumerica) {
                PreguntaNumerica pn = (PreguntaNumerica) p;
                pObj.addProperty("tipo", "numerica");
                pObj.addProperty("rangoInf", pn.getRangoInf());
                pObj.addProperty("rangoSup", pn.getRangoSup());
            } else if (p instanceof PreguntaOpcionUnica) {
                pObj.addProperty("tipo", "opcionUnica");
                PreguntaOpcionUnica pu = (PreguntaOpcionUnica) p;
                pObj.add("opciones", gson.toJsonTree(pu.getOpciones()));
                pObj.addProperty("ordenada", pu.esOrdenada());
            } else if (p instanceof PreguntaOpcionMultiple) {
                pObj.addProperty("tipo", "opcionMultiple");
                PreguntaOpcionMultiple pm = (PreguntaOpcionMultiple) p;
                pObj.add("opciones", gson.toJsonTree(pm.getOpciones()));
                pObj.addProperty("numMaxOpc", pm.getNumMaxOpc());
            }
            
            preguntasObj.add(String.valueOf(e.getKey()), pObj);
        }
        root.add("preguntas", preguntasObj);
        
        try (FileWriter w = new FileWriter(FILE)) {
            gson.toJson(root, w);
        } catch (IOException e) {
            throw new RuntimeException("Error guardando preguntas: " + e.getMessage());
        }
    }

    /**
     * Crea directorio DATA si no existe.
     */
    //private void crearDirectorio() {
    //    new File("DATA").mkdirs();
    //}

    /**
     * Deserializador custom para jerarquía de Pregunta.
     */
    private static class PreguntaDeserializer implements JsonDeserializer<Pregunta> {
        @Override
        public Pregunta deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            JsonObject obj = json.getAsJsonObject();
            int idPregunta = obj.get("idPregunta").getAsInt();
            int idEncuesta = obj.get("idEncuesta").getAsInt();
            String enunciado = obj.get("enunciado").getAsString();
            String tipo = obj.get("tipo").getAsString();
            
            switch (tipo) {
                case "libre":
                    return new PreguntaLibre(idEncuesta, idPregunta, enunciado);
                case "numerica":
                    return new PreguntaNumerica(idEncuesta, idPregunta, enunciado,
                        obj.get("rangoInf").getAsInt(), obj.get("rangoSup").getAsInt());
                case "opcionUnica":
                    PreguntaOpcionUnica pu = new PreguntaOpcionUnica(idPregunta, idEncuesta, 
                        enunciado, obj.get("ordenada").getAsBoolean());
                    for (JsonElement op : obj.getAsJsonArray("opciones")) {
                        pu.addOpcion(op.getAsString());
                    }
                    return pu;
                case "opcionMultiple":
                    PreguntaOpcionMultiple pm = new PreguntaOpcionMultiple(idEncuesta, idPregunta, enunciado);
                    for (JsonElement op : obj.getAsJsonArray("opciones")) {
                        pm.addOpcion(op.getAsString());
                    }
                    pm.setNumMaxOpc(obj.get("numMaxOpc").getAsInt());
                    return pm;
                default:
                    throw new JsonParseException("Tipo pregunta desconocido: " + tipo);
            }
        }
    }
}
