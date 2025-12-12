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
 * - Cargar preguntas desde archivo JSON con deserialización polimórfica
 * - Guardar preguntas a archivo JSON con información de tipo
 * - Gestionar operaciones I/O con disco
 * 
 * ATRIBUTOS PRINCIPALES:
 * - gson: Serializador/deserializador JSON con custom deserializer
 * 
 * MÉTODOS PRINCIPALES:
 * - getNextId()
 * - guardarNextId()
 * - getPregunta()
 * - sobreescribe()
 * - eliminarPregunta()
 * - guardar()
 * 
 * - cargarPregunta(): Lee una pregunta específica del disco
 * - cargarTodas(): Lee todas las preguntas del disco
 * - guardar(): Escribe mapa completo de preguntas a disco
 * 
 * @author Denis Roca
 */
public class CtrlPersistenciaPregunta {
    private final Gson gson;
    private static CtrlPersistenciaPregunta singletonInstance;

    /**
     * Crea controlador de persistencia de preguntas con deserializador custom.
     */
    public CtrlPersistenciaPregunta() {
        GsonBuilder builder = new GsonBuilder();
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
     * Obtiene el siguiente ID disponible para una nueva pregunta.
     * 
     * @return Siguiente ID de pregunta
     */
    public int getNextId () {
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
     * Guarda el siguiente ID disponible en disco.
     * 
     * @param newid Nuevo ID de pregunta
     */
    public void guardarNextId (int newid) {
        String FILE = "data/nextId.json";

        try{
            FileReader reader = new FileReader(FILE);
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

    /**
     * Convierte un JsonObject a Pregunta según su tipo.
     * 
     * @param pregunta JsonObject con los datos de la pregunta
     * @param idEncuesta ID de la encuesta a la que pertenece
     * @return Pregunta del tipo correspondiente
     * @throws IllegalArgumentException si el tipo es desconocido
     */
    private Pregunta jsonToPregunta(JsonObject pregunta, int idEncuesta){
        try{
            // Intentar leer idEncuesta del JSON, si no existe usar el parámetro
            if (pregunta.has("idEncuesta")) {
                idEncuesta = pregunta.get("idEncuesta").getAsInt();
            }
            int idPregunta = pregunta.get("idPregunta").getAsInt();
            String tipo = pregunta.get("tipo").getAsString();
            String enunciado = pregunta.get("enunciado").getAsString();
            JsonArray idRs = pregunta.get("idRespuestas").getAsJsonArray();
            int[] idRespuestas = new int[idRs.size()];
            for (int i = 0; i < idRs.size(); i++) {
                idRespuestas[i] = idRs.get(i).getAsInt();
            }

            switch (tipo) {
                case "numerica":
                    int rangoInf = pregunta.get("rangoInf").getAsInt();
                    int rangoSup = pregunta.get("rangoSup").getAsInt();
                    Pregunta pNum = new PreguntaNumerica(idEncuesta, idPregunta, enunciado, rangoInf, rangoSup);
                    for (int idR : idRespuestas) {
                        pNum.guardarIdRespuesta(idR);
                    }
                    return pNum;
                case "libre":
                    Pregunta pLib = new PreguntaLibre(idEncuesta, idPregunta, enunciado);
                    for (int idR : idRespuestas) {
                        pLib.guardarIdRespuesta(idR);
                    }
                    return pLib;
                
                case "opcion_unica":
                    boolean ordenada = pregunta.get("ordenada").getAsBoolean();
                    Pregunta pU = new PreguntaOpcionUnica(idEncuesta, idPregunta, enunciado, ordenada);
                    JsonArray opsU = pregunta.get("opciones").getAsJsonArray();
                    for (int i = 0; i < opsU.size(); i++) {
                        pU.addOpcion(opsU.get(i).getAsString());
                    }
                    for (int idR : idRespuestas) {
                        pU.guardarIdRespuesta(idR);
                    }
                    return pU;
                case "opcion_multiple":
                    Pregunta pM = new PreguntaOpcionMultiple(idEncuesta, idPregunta, enunciado);
                    JsonArray opsM = pregunta.get("opciones").getAsJsonArray();
                    for (int i = 0; i < opsM.size(); i++) {
                        pM.addOpcion(opsM.get(i).getAsString());
                    }
                    pM.setNumMaxOpc(pregunta.get("numMaxOpc").getAsInt());
                    for (int idR : idRespuestas) {
                        pM.guardarIdRespuesta(idR);
                    }
                    return pM;
                default:
                    throw new IllegalArgumentException("Tipo de pregunta desconocido: " + tipo);
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error traduciendo el json a objeto java");
        }

    }

    /**
     * Convierte una Pregunta a JsonObject.
     * 
     * @param p Pregunta a convertir
     * @return JsonObject con la estructura correcta
     */
    private JsonObject preguntaToJson(Pregunta p){
        JsonObject pObj = new JsonObject();
        pObj.addProperty("idEncuesta", p.getIdEncuesta());
        pObj.addProperty("idPregunta", p.getIdPregunta());
        pObj.addProperty("tipo", p.tipoPregunta());
        pObj.addProperty("enunciado", p.getEnunciado());

        switch(p.tipoPregunta()) {
            case "numerica":
                pObj.addProperty("rangoInf", p.getRangoInf());
                pObj.addProperty("rangoSup", p.getRangoSup());
                break;
            case "opcion_unica":
                JsonArray arrU = new JsonArray();
                for (String sU : p.getOpciones()) {
                    arrU.add(sU);
                }
                pObj.add("opciones", arrU);
                pObj.addProperty("ordenada", p.esOrdenada());
                break;
            case "opcion_multiple":
                JsonArray arrM = new JsonArray();
                for (String sM : p.getOpciones()) {
                    arrM.add(sM);
                }
                pObj.add("opciones", arrM);
                pObj.addProperty("numMaxOpc", p.getNumMaxOpc());
        }
        JsonArray resps = new JsonArray();
        for (int iR : p.getIdRespuestas()) {
            resps.add(iR);
        }
        pObj.add("idRespuestas", resps);
        return pObj;
    }

    /**
     * Busca el ID de encuesta que contiene una pregunta.
     * 
     * @param idPregunta ID de la pregunta
     * @return ID de la encuesta, o -1 si no se encuentra
     */
    private int getEncuestaPregunta(int idPregunta){
    	File carpetaRaiz = new File("data/encuestas");
    	File[] jsonFiles = carpetaRaiz.listFiles(file -> file.isFile() && file.getName().endsWith(".json"));
    	int idEncuesta = -1;
    	try{
    		for(File f : jsonFiles){
    			FileReader reader = new FileReader(f);
    			JsonObject encuesta = JsonParser.parseReader(reader).getAsJsonObject();
    			JsonArray listaPreguntas = encuesta.getAsJsonArray("IdPreguntas");
    			for (JsonElement pregunta : listaPreguntas){
    				if(pregunta.getAsInt() == idPregunta) {
    					idEncuesta = encuesta.get("idEncuesta").getAsInt();
    					break;
    				}
    			}
    			reader.close();
    			if(idEncuesta != -1) break;
            }
        } catch(Exception e) {
            throw new RuntimeException("Fichero no válido", e);
        }
        return idEncuesta;
    }

    /**
     * Carga una pregunta específica desde disco.
     * 
     * @param id ID de la pregunta
     * @return Objeto Pregunta con identificador id
     * @throws IllegalArgumentException si la pregunta no existe
     */
    public Pregunta getPregunta(int id) throws IllegalArgumentException {
        int idEncuesta = getEncuestaPregunta(id);
    	if(idEncuesta == -1) throw new IllegalArgumentException("La pregunta " + id + " no existe");
    	File jsonPregunta = new File("data/encuestas/" + idEncuesta + "/preguntas/" + id + ".json");
        try{
            FileReader r = new FileReader(jsonPregunta);
            Pregunta p = jsonToPregunta(JsonParser.parseReader(r).getAsJsonObject(), idEncuesta);
            return p;
        }
        catch(FileNotFoundException eF){
            throw new IllegalArgumentException("La pregunta " + id + " no existe");
        }
        catch (Exception e) {
            throw new RuntimeException("Error leyendo la pregunta " + id, e);
        }
    }

    /**
     * Carga todas las preguntas desde disco.
     * 
     * @return Mapa de <ID,Pregunta> (vacío si archivo no existe)
     */
    public ArrayList<Pregunta> getAllPreguntas() { 

    	//--------------------------//
		// Falta revisar (está mal) //
		//---------------------//---//
        //Parece que no se usa //
        //---------------------//

        File f = new File("data/encuestas");
        File[] encuestas = f.listFiles(File::isDirectory);
        if (encuestas == null) return new ArrayList<>();
        
        try (FileReader r = new FileReader(f)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
            if (!root.has("preguntas")) return new ArrayList<>();
            
            JsonObject preguntasObj = root.getAsJsonObject("preguntas");
            ArrayList<Pregunta> result = new ArrayList<>();
            
            for (Map.Entry<String, JsonElement> entry : preguntasObj.entrySet()) {
                Pregunta p = gson.fromJson(entry.getValue(), Pregunta.class);
                result.add(p);
            }
            return result;
        } catch (IOException e) {
            return new ArrayList<>();
        }
    }

    /**
     * Actualiza una pregunta existente.
     * 
     * @param p pregunta a actualizar
     * @throws IllegalArgumentException si la pregunta no existe
     */
    public void sobreescribe(Pregunta p) {
        int id = p.getIdPregunta();
        File archivo = new File("data/encuestas/" + p.getIdEncuesta() + "/preguntas", id + ".json");
        
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
     * Elimina una pregunta del disco y la desregistra de la encuesta.
     * 
     * @param idPregunta Identificador de la pregunta
     * @throws IllegalArgumentException si la pregunta no existe
     */
    public void eliminarPregunta(int idPregunta) {
        int idEncuesta = getEncuestaPregunta(idPregunta);
        if (idEncuesta == -1) {
            throw new IllegalArgumentException("No existe la pregunta " + idPregunta);
        }
        
        File archivo = new File("data/encuestas/" + idEncuesta + "/preguntas", idPregunta + ".json");
        if (!archivo.delete()) {
            throw new IllegalArgumentException("No existe la pregunta " + idPregunta);
        }
        
        // Actualizar la lista de preguntas en la encuesta
        try {
            File encuestaFile = new File("data/encuestas/" + idEncuesta + ".json");
            if (encuestaFile.exists()) {
                FileReader reader = new FileReader(encuestaFile);
                JsonObject encuesta = JsonParser.parseReader(reader).getAsJsonObject();
                reader.close();
                
                if (encuesta.has("IdPreguntas")) {
                    JsonArray preguntas = encuesta.getAsJsonArray("IdPreguntas");
                    JsonArray nuevasPreguntas = new JsonArray();
                    for (JsonElement elem : preguntas) {
                        if (elem.getAsInt() != idPregunta) {
                            nuevasPreguntas.add(elem);
                        }
                    }
                    encuesta.add("IdPreguntas", nuevasPreguntas);
                    
                    FileWriter writer = new FileWriter(encuestaFile);
                    gson.toJson(encuesta, writer);
                    writer.close();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("Error actualizando encuesta: " + e.getMessage());
        }
    }

    /**
     * Guarda una pregunta a disco y la registra en la encuesta.
     * 
     * @param p Pregunta a guardar
     */
    public void guardar(Pregunta p) {
        int idEncuesta = p.getIdEncuesta();
        int idPregunta = p.getIdPregunta();
        
        // Guardar archivo de pregunta
        try (FileWriter w = new FileWriter("data/encuestas/" + idEncuesta + "/preguntas/" + idPregunta + ".json")) {
            gson.toJson(preguntaToJson(p), w);
        } catch (IOException e) {
            throw new RuntimeException("Error guardando pregunta: " + e.getMessage());
        }
        
        // Actualizar la lista de preguntas en la encuesta
        try {
            File encuestaFile = new File("data/encuestas/" + idEncuesta + ".json");
            JsonObject encuesta;
            
            if (encuestaFile.exists()) {
                FileReader reader = new FileReader(encuestaFile);
                encuesta = JsonParser.parseReader(reader).getAsJsonObject();
                reader.close();
                
                // Obtener o crear el array de preguntas
                JsonArray preguntas;
                if (encuesta.has("IdPreguntas")) {
                    preguntas = encuesta.getAsJsonArray("IdPreguntas");
                    // Verificar si ya está registrada
                    boolean yaExiste = false;
                    for (JsonElement elem : preguntas) {
                        if (elem.getAsInt() == idPregunta) {
                            yaExiste = true;
                            break;
                        }
                    }
                    if (!yaExiste) {
                        preguntas.add(idPregunta);
                    }
                } else {
                    preguntas = new JsonArray();
                    preguntas.add(idPregunta);
                    encuesta.add("IdPreguntas", preguntas);
                }
                
                // Guardar encuesta actualizada
                FileWriter writer = new FileWriter(encuestaFile);
                gson.toJson(encuesta, writer);
                writer.close();
            }
        } catch (IOException e) {
            throw new RuntimeException("Error actualizando encuesta: " + e.getMessage());
        }
    }
}
