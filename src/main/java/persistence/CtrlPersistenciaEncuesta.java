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

    private static CtrlPersistenciaEncuesta singletonInstance;

    private final Gson gson;
    /**
     * Obtiene instancia única (patrón Singleton).
     * 
     * @return Instancia única de GestorPersistenciaEncuesta
     */
    public static CtrlPersistenciaEncuesta getInstance() {
        if (singletonInstance == null) {
            singletonInstance = new CtrlPersistenciaEncuesta();
        }
        return singletonInstance;
    }


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

        catch (Exception exc) {
            throw new RuntimeException("Error leyendo el archivo JSON", e);
        }
    }

    public Encuesta getEncuesta(int id) {

        String FILE = "data/encuestas/" + id + ".json";

        try (FileReader reader = new FileReader(FILE)) {

            JsonObject encuesta = JsonParser.parseReader(reader).getAsJsonObject();

            int idEncuesta = encuesta.get("id").getAsInt();
            String titulo = encuesta.get("titulo").getAsString();
            String creador = encuesta.get("creador").getAsString();

            JsonArray listaPreguntas = encuesta.getAsJsonArray("preguntas");
            
            
            Encuesta e = new Encuesta(idEncuesta, titulo, creador);

           
            for (JsonElement elemento : listaPreguntas) {
                                
                int idP = elemento.getAsInt();
                
                e.guardarPregunta(idP);
            }

            return e;

        }

        catch(Exception exc) {
            throw new RuntimeException("Error leyendo el archivo JSON", e);
        }
    }

    public ArrayList<Encuesta> getAllEncuestas() {

        File carpetaRaiz = new File("data/encuestas");

        File[] jsonFiles = carpetaRaiz.listFiles(
            file -> file.isFile() && file.getName()..endsWith(".json")
        );

        if (jsonFiles != null) {

            ArrayList<Encuesta> encuestas = new ArrayList<>();

            for (File jsonFile: jsonFiles) {
                
                try{
                    String fileName = jsonFile.getName(); // "1.json"
                    String idStr = fileName.replace(".json", ""); // "1"

                    int id = Integer.parseInt(idStr); // 1
                    encuestas.add(getEncuesta(id));
                }

                catch(Exception exc) {
                    throw new RuntimeException("Error el nombre de la carpeta no es un número válido", e);
                }
            }

            return encuestas;
        }

        else return null;

    }


    public void guardarEncuesta(Encuesta e) {
        
        int newid = e.getIdEncuesta();

        File archivoJson = new File("data/encuestas/", newid + ".json");

        if (archivoJson.exists()) throw new IllegalArgumentException("La encuesta Ya existe");

        try (FileWriter writer = new FileWriter(archivoJson)) {
        
            gson.toJson(e, writer);

        } 
        catch (IOException exc) {
            throw new RuntimeException("Error al guardar el archivo JSON", e);
        }
    }

    public boolean existeEncuesta(int id) {

        File archivo = new File("data/encuestas", id + ".json");
        return archivo.exists();
    }

    public void actualizarEncuesta (Encuesta e) {

        int id = e.getIdEncuesta();

        File archivoJson = new File("data/encuestas/", id + ".json");

        if (!archivoJson.exists()) throw new IllegalArgumentException("La encuesta No existe");

        //si existe devuelve la ruta sino la crea
        try (FileWriter writer = new FileWriter(archivoJson)) {
            
            //sobre escribe pq append es 0
            gson.toJson(e, writer);

        } 
        catch (IOException exc) {
            throw new RuntimeException("Error al guardar el archivo JSON", e);
        }

    }

    public void eliminarEncuesta (int id) {
        String rutaCarpeta = "data/encuestas/";

        File archivo = new File(rutaCarpeta, id + ".json"); //solo es  una ruta

        boolean eliminado = archivo.delete();
        
        if (!eliminado) {
            throw new IllegalArgumentException("No existe la encuesta a eliminar");
        }
    }

}
