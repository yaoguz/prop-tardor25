package persistence;

import com.google.gson.*;
import domain.*;
import java.io.*;
import java.lang.reflect.Type;
import java.util.*;

/**
 * Controlador de persistencia de preguntas en disco.
 * 
 * Gestiona la lectura y escritura de respuestas en formato JSON.
 * Cada pregunta se almacena en data/encuestas/{idEncuesta}/preguntas/{idPregunta}.json
 * 
 * RESPONSABILIDADES:
 * - Cargar preguntas desde archivo JSON con deserialización polimórfica
 * - Guardar preguntas a archivo JSON con información de tipo
 * - Gestionar operaciones I/O con disco
 * 
 * ATRIBUTOS PRINCIPALES:
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
    	File carpetaRaiz = new File("data/encuestas");
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
    	File carpetaPreguntas = new File("data/encuestas/" + idEncuesta + "/preguntas/" + id + ".json");
    	
    	//-------------------------//
		// Falta generar el objeto //
		//-------------------------//
		//Si se hace indexPreguntas hay que cambiarlo
    }

    /**
     * Carga todas las preguntas desde disco.
     * 
     * @return Mapa de <ID,Pregunta> (vacío si archivo no existe)
     */
    public Map<Integer, Pregunta> getAllPreguntas() { 
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
        for (Map.Entry<Integer, Pregunta> e : preguntas.entrySet()) {
            Pregunta p = e.getValue();
            JsonObject pObj = new JsonObject();
            pObj.addProperty("idPregunta", p.getIdPregunta());
            pObj.addProperty("tipo", p.tipoPregunta());
//            pObj.addProperty("idEncuesta", p.getIdEncuesta());
            pObj.addProperty("enunciado", p.getEnunciado());
            
            // Serialización polimórfica con tipo
            switch(p.tipoPregunta()) {
            	case "numerica":
    	            pObj.addProperty("rangoInf", p.getRangoInf());
    	            pObj.addProperty("rangoSup", p.getRangoSup());
    	            break;
    	        case "opcion_unica":
            		JsonArray arrU = new JsonArray();
            		for (int iU : p.getOpciones()) {
            		    arrU.add(iU);
            		}
            		pObj.add("opciones", arrU);
                	pObj.addProperty("ordenada", p.esOrdenada());
                	break;
				case "opcion_multiple":
            		JsonArray arrM = new JsonArray();
            		for (int iM : p.getOpciones()) {
            		    arrM.add(iM);
            		}
            		pObj.add("opciones", arrM);
    	            pObj.addProperty("numMaxOpc", p.getNumMaxOpc());
            }
    		JsonArray resps = new JsonArray();
    		for (int iR : p.getRespuestas()) {
    		    resps.add(iR);
    		}
    		pObj.add("idRespuestas", resps);
			try (FileWriter w = new FileWriter("data/encuestas/" + p.getIdEncuesta() + "/preguntas/" + p.getIdPregunta() + ".json")) {
		        gson.toJson(pObj, w);
		    } catch (IOException e) {
		        throw new RuntimeException("Error guardando preguntas: " + e.getMessage());
		    }
        }
    }

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
