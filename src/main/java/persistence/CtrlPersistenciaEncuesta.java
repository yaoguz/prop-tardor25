package persistence;

import com.google.gson.*;
import domain.Encuesta;
import domain.Respuesta;
import java.io.*;
import java.util.*;
import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

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

    private static CtrlPersistenciaRespuesta ctrlRespuesta;

    private final Gson gson;

    /**
     * Obtiene instancia única (patrón Singleton).
     * 
     * @return Instancia única de CtrlPersistenciaEncuesta
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

        ctrlRespuesta = CtrlPersistenciaRespuesta.getInstance();
    }

    /**
     * Obtiene el siguiente ID disponible para una nueva encuesta.
     * 
     * @return Siguiente ID de encuesta
     */
    public int getNextId () {
        
        String FILE = "data/nextId.json";

        try (FileReader reader = new FileReader(FILE)) {

            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            return jsonObject.get("nextIdEncuesta").getAsInt();
        }

        catch (Exception exc) {
            throw new RuntimeException("Error leyendo el archivo JSON 1", exc);
        }
    }

    /**
     * Obtiene una encuesta desde disco.
     * 
     * @param id ID de la encuesta
     * @return Encuesta encontrada
     * @throws RuntimeException si hay error leyendo el archivo
     */
    public Encuesta getEncuesta(int id) {

        String FILE = "data/encuestas/" + id + ".json";

        try (FileReader reader = new FileReader(FILE)) {

            JsonObject encuesta = JsonParser.parseReader(reader).getAsJsonObject();
            /* 
            int idEncuesta = encuesta.get("id").getAsInt();
            String titulo = encuesta.get("titulo").getAsString();
            String creador = encuesta.get("creador").getAsString();

            JsonArray listaPreguntas = encuesta.getAsJsonArray("preguntas");
            
            
            Encuesta e = new Encuesta(idEncuesta, titulo, creador);

           
            for (JsonElement elemento : listaPreguntas) {
                                
                int idP = elemento.getAsInt();
                
                e.guardarPregunta(idP);
            }*/

            Encuesta e = new Gson().fromJson(encuesta, Encuesta.class);

            return e;

        }

        catch(Exception exc) {
            throw new RuntimeException("Error leyendo el archivo JSON 2", exc);
        }
    }

    /**
     * Obtiene todas las encuestas del sistema.
     * 
     * @return Lista con todas las encuestas, o null si no hay encuestas
     */
    public ArrayList<Encuesta> getAllEncuestas() {

        File carpetaRaiz = new File("data/encuestas");

        File[] jsonFiles = carpetaRaiz.listFiles(
            file -> file.isFile() && file.getName().endsWith(".json")
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
                    throw new RuntimeException("Error el nombre de la carpeta no es un número válido", exc);
                }
            }

            return encuestas;
        }

        else return null;

    }

    /**
     * Guarda una nueva encuesta en disco.
     * 
     * @param e Encuesta a guardar
     * @throws IllegalArgumentException si la encuesta ya existe
     */
    public void guardarEncuesta(Encuesta e) {
        
        int newid = e.getIdEncuesta();

        File archivoJson = new File("data/encuestas", newid + ".json");

        if (archivoJson.exists()) throw new IllegalArgumentException("La encuesta Ya existe");

        try (FileWriter writer = new FileWriter(archivoJson)) {
        
            gson.toJson(e, writer);

        } 
        catch (IOException exc) {
            throw new RuntimeException("Error al guardar el archivo JSON", exc);
        }
    }

    /**
     * Verifica si existe una encuesta.
     * 
     * @param id ID de la encuesta
     * @return true si existe
     */
    public boolean existeEncuesta(int id) {

        File archivo = new File("data/encuestas", id + ".json");
        return archivo.exists();
    }

    /**
     * Actualiza una encuesta existente.
     * 
     * @param e Encuesta a actualizar
     * @throws IllegalArgumentException si la encuesta no existe
     */
    public void actualizarEncuesta (Encuesta e) {

        int id = e.getIdEncuesta();

        File archivoJson = new File("data/encuestas", id + ".json");

        if (!archivoJson.exists()) throw new IllegalArgumentException("La encuesta No existe");

        //si existe devuelve la ruta sino la crea
        try (FileWriter writer = new FileWriter(archivoJson)) {
            
            //sobre escribe pq append es 0
            gson.toJson(e, writer);

        } 
        catch (IOException exc) {
            throw new RuntimeException("Error al guardar el archivo JSON", exc);
        }

    }

    /**
     * Elimina una encuesta del disco.
     * 
     * @param id ID de la encuesta
     * @throws IllegalArgumentException si la encuesta no existe
     */
    public void eliminarEncuesta (int id) {

        File archivo = new File("data/encuestas", id + ".json"); //solo es  una ruta

        boolean eliminado = archivo.delete();
        
        if (!eliminado) {
            throw new IllegalArgumentException("No existe la encuesta a eliminar");
        }

        Path ruta = Paths.get("data/encuestas", String.valueOf(id));
        if (!Files.exists(ruta)) {
            //throw new IllegalArgumentException("No existe la encuesta/dir: " + ruta);
            return;
        }

        try {
            Files.walkFileTree(ruta, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    Files.delete(file);
                    return FileVisitResult.CONTINUE;
                }

                @Override
                public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                    if (exc != null) throw exc; // re-lanzar errores anteriores
                    Files.delete(dir);
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            throw new RuntimeException("No se pudo eliminar " + ruta + ": " + e.getMessage(), e);
        }
    }

    /**
     * Obtiene todas las respuestas de una encuesta.
     * 
     * @param id ID de la encuesta
     * @return Lista con todas las respuestas, o null si no hay respuestas
     */
    public ArrayList<Respuesta> getRespuestasEncuesta (int id) {

        File carpetaRaiz = new File("data/encuestas", id + "/respuestas");

        File[] jsonFiles = carpetaRaiz.listFiles(
            file -> file.isFile() && file.getName().endsWith(".json")
        );

        if (jsonFiles != null) {

            ArrayList<Respuesta> respuestas = new ArrayList<>();

            for (File jsonFile: jsonFiles) {
                
                try{

                    String fileName = jsonFile.getName(); // "1.json"
                    String idStr = fileName.replace(".json", ""); // "1"

                    int idR = Integer.parseInt(idStr); // 1

                    respuestas.add (ctrlRespuesta.getRespuesta(idR, id));
                }

                catch(Exception exc) {
                    throw new RuntimeException("Error el nombre de la carpeta no es un número válido", exc);
                }
            }

            return respuestas;
        }

        else return null;
    }

    /**
     * Guarda el siguiente ID disponible en disco.
     * 
     * @param newid Nuevo ID de encuesta
     */
    public void guardarNextId (int newid) {
        String FILE = "data/nextId.json";

        try (FileReader reader = new FileReader(FILE)) {

            JsonObject jsonObject = JsonParser.parseReader(reader).getAsJsonObject();

            
            // .addProperty reemplaza el valor si la clave ya existe, o la crea si no
            jsonObject.addProperty("nextIdEncuesta", newid);

         
            try (FileWriter writer = new FileWriter(FILE)) {
                
                // Esta línea vuelca el objeto modificado al archivo
                gson.toJson(jsonObject, writer);
                
            } catch (IOException e) {
                throw new RuntimeException("Error escribiendo el archivo JSON", e);
            }

        }

        catch (Exception exc) {
            throw new RuntimeException("Error leyendo el archivo JSON 3", exc);
        }
    }

}
