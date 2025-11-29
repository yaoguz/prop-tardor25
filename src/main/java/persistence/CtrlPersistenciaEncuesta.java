package persistence;

import com.google.gson.*;
import domain.Encuesta;
import java.io.*;
import java.util.*;

/**
 * Controlador de persistencia de encuestas en disco.
 * 
 * Gestiona la lectura y escritura de encuestas en formato JSON.
 * No mantiene cache interna - la cache es gestionada por GestorPersistencia.
 * 
 * RESPONSABILIDADES:
 * - Cargar encuestas desde archivo JSON
 * - Guardar encuestas a archivo JSON
 * - Gestionar operaciones I/O con disco
 * 
 * ATRIBUTOS PRINCIPALES:
 * - FILE: Ruta del archivo JSON
 * - gson: Serializador/deserializador JSON
 * 
 * MÉTODOS PRINCIPALES:
 * - cargarEncuesta(): Lee una encuesta específica del disco
 * - cargarTodas(): Lee todas las encuestas del disco
 * - guardar(): Escribe mapa completo de encuestas a disco
 * 
 * @author Alessandro Jin
 */
public class CtrlPersistenciaEncuesta {

    private final Gson gson;

    /**
     * Crea controlador de persistencia de encuestas.
     */
    public CtrlPersistenciaEncuesta() {

        //lo pone bonito cuando escribe
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    public int getNextId () {
        
        String FILE = "data/nextId.json";

        try (FileReader reader = new FileReader(FILE)) {

            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            return jsonObject.get("nextIdEncuesta").getAsInt();
        }

        catch (Exception e) {
            throw new RuntimeException("Error leyendo el archivo JSON", e);
        }
    }

    public Encuesta getEncuesta(int id) {

        String FILE = "data/encuestas/" + id + "/encuesta.json";

        try (FileReader reader = new FileReader(FILE)) {

            JsonObject encuesta = JsonParser.parseReader(reader).getAsJsonObject();

            int idEncuesta = encuesta.get("id").getAsInt();
            String titulo = encuesta.get("titulo").getAsString();
            String creador = encuesta.get("creador").getAsString();

            JsonArray listaPreguntas = encuesta.getAsJsonArray("preguntas");
            
            
            Encuesta e = new Encuesta(idEncuesta, titulo, creador);

           
            for (JsonElement elemento : listaPreguntas) {
                
                JsonObject preguntaObj = elemento.getAsJsonObject();
                
                int idP = preguntaObj.get("idPregunta").getAsInt();
                
                e.guardarPregunta(idP);
            }

            return e;

        }

        catch(Exception e) {
            throw new RuntimeException("Error leyendo el archivo JSON", e);
        }
    }

    public ArrayList<Encuesta> getEncuestas() {

        File carpetaRaiz = new File("data/encuestas");

        File[] subcarpetas = carpetaRaiz.listFiles(File::isDirectory);

        if (subcarpetas != null) {

            ArrayList<Encuesta> encuestas = new ArrayList<>();

            for (File carpeta: subcarpetas) {
                
                try{
                    String carpetaName = carpeta.getName();

                    int id = Integer.parseInt(carpetaName);

                    encuestas.add(getEncuesta(id));
                }

                catch(Exception e) {
                    throw new RuntimeException("Error el nombre de la carpeta no es un número válido", e);
                }
            }

            return encuestas;
        }

        else return null;

    }


    public void guardarEncuesta(Encuesta e) {

        int newid = e.getIdEncuesta();

        File carpetaRaiz = new File("data/encuestas");

        File[] subcarpetas = carpetaRaiz.listFiles(File::isDirectory);

        if (subcarpetas != null) {

            for (File carpeta: subcarpetas) {
                
                try{
                    String carpetaName = carpeta.getName();

                    int id = Integer.parseInt(carpetaName);

                    if (newid == id) throw new IllegalArgumentException("La encuesta con id: "+ id + " ya existe");
                }

                catch(Exception e) {
                    throw new RuntimeException("Error el nombre de la carpeta no es un número válido", e);
                }
            }

        }

        String rutaCarpeta = "data/encuestas/" + newid;

        File directorio = new File(rutaCarpeta);

        File archivoJson = new File(directorio, "encuesta.json");

        try (FileWriter writer = new FileWriter(archivoJson)) {
        
            gson.toJson(e, writer); // ?????

        } 
        catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo JSON", e);
        }
    }

    /**
     * Carga una encuesta específica desde disco.
     * 
     * @param id ID de la encuesta
     * @return Encuesta encontrada, o null si no existe
     */
    public Encuesta cargarEncuesta(int id) {
        Map<Integer, Encuesta> todas = cargarTodas();
        return todas.get(id);
    }

    /**
     * Carga todas las encuestas desde disco.
     * 
     * @return Mapa de ID → Encuesta (vacío si archivo no existe)
     */
    public Map<Integer, Encuesta> cargarTodas() {
        File f = new File(FILE);
        if (!f.exists()) return new HashMap<>();
        
        try (FileReader r = new FileReader(f)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
            if (!root.has("encuestas")) return new HashMap<>();
            
            JsonObject encuestasObj = root.getAsJsonObject("encuestas");
            Map<Integer, Encuesta> result = new HashMap<>();
            
            for (Map.Entry<String, JsonElement> entry : encuestasObj.entrySet()) {
                Encuesta e = gson.fromJson(entry.getValue(), Encuesta.class);
                result.put(Integer.parseInt(entry.getKey()), e);
            }
            return result;
        } catch (IOException e) {
            return new HashMap<>();
        }
    }

    /**
     * Guarda el mapa completo de encuestas a disco.
     * 
     * @param encuestas Mapa de ID → Encuesta
     */
    public void guardar(Map<Integer, Encuesta> encuestas) {
        JsonObject root = new JsonObject();
        JsonObject encuestasObj = new JsonObject();
        
        for (Map.Entry<Integer, Encuesta> e : encuestas.entrySet()) {
            encuestasObj.add(String.valueOf(e.getKey()), gson.toJsonTree(e.getValue()));
        }
        root.add("encuestas", encuestasObj);
        
        try (FileWriter w = new FileWriter(FILE)) {
            gson.toJson(root, w);
        } catch (IOException e) {
            throw new RuntimeException("Error guardando encuestas: " + e.getMessage());
        }
    }


}
