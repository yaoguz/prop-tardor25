package persistence;

import com.google.gson.*;
import domain.*;
import java.io.*;
import java.util.*;


// pnd revisar

/**
 * Controlador de persistencia de respuestas en disco.
 * 
 * Gestiona la lectura y escritura de respuestas en formato JSON.
 * Cada respuesta se almacena en data/encuestas/{idEncuesta}/respuestas/{idRespuesta}.json
 * 
 * @author Yaoyao Guo
 */
public class CtrlPersistenciaRespuesta {

    private final Gson gson;

    public CtrlPersistenciaRespuesta() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Obtiene una respuesta desde disco.
     * 
     * @param idRespuesta ID de la respuesta
     * @param idEncuesta ID de la encuesta a la que pertenece
     * @return Respuesta encontrada
     * @throws IllegalArgumentException si la respuesta no existe
     */
    public Respuesta getRespuesta(int idRespuesta, int idEncuesta) {
        String FILE = "data/encuestas/" + idEncuesta + "/respuestas/" + idRespuesta + ".json";

        try (FileReader reader = new FileReader(FILE)) {
            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();

            int id = obj.get("idRespuesta").getAsInt();
            int idPregunta = obj.get("idPregunta").getAsInt();
            String nomUsuario = obj.get("nomUsuario").getAsString();
            String tipo = obj.get("tipo").getAsString();
            JsonElement respuestaElem = obj.get("respuesta");

            switch (tipo) {
                case "numerica":
                    double valor = respuestaElem.getAsDouble();
                    return new RespuestaNumerica(id, nomUsuario, idPregunta, valor);
                
                case "libre":
                    String texto = respuestaElem.getAsString();
                    return new RespuestaLibre(id, nomUsuario, idPregunta, texto);
                
                case "opcion_unica":
                    int indice = respuestaElem.getAsInt();
                    return new RespuestaOpcionUnica(id, nomUsuario, idPregunta, indice);
                
                case "opcion_multiple":
                    JsonArray arr = respuestaElem.getAsJsonArray();
                    int[] indices = new int[arr.size()];
                    for (int i = 0; i < arr.size(); i++) {
                        indices[i] = arr.get(i).getAsInt();
                    }
                    return new RespuestaOpcionMultiple(id, nomUsuario, idPregunta, indices);
                
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
        if (directoriosEncuestas == null) return respuestas;
        
        for (File dirEncuesta : directoriosEncuestas) {
            try {
                int idEncuesta = Integer.parseInt(dirEncuesta.getName());
                File carpetaRespuestas = new File(dirEncuesta, "respuestas");
                
                if (!carpetaRespuestas.exists()) continue;
                
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
     * Guarda una nueva respuesta en disco.
     * 
     * @param r Respuesta a guardar
     * @param idEncuesta ID de la encuesta a la que pertenece
     * @throws IllegalArgumentException si la respuesta ya existe
     */
    public void guardarRespuesta(Respuesta r, int idEncuesta) {
        int id = r.getId();
        String rutaCarpeta = "data/encuestas/" + idEncuesta + "/respuestas";
        File carpeta = new File(rutaCarpeta);
        
        if (!carpeta.exists()) carpeta.mkdirs();
        
        File archivo = new File(carpeta, id + ".json");
        if (archivo.exists()) {
            throw new IllegalArgumentException("La respuesta " + id + " ya existe");
        }

        try (FileWriter writer = new FileWriter(archivo)) {
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
            } else if (respuesta instanceof Double) {
                obj.addProperty("respuesta", (Double) respuesta);
            } else if (respuesta instanceof Integer) {
                obj.addProperty("respuesta", (Integer) respuesta);
            } else if (respuesta instanceof String) {
                obj.addProperty("respuesta", (String) respuesta);
            }
            
            gson.toJson(obj, writer);
        }
        catch (IOException e) {
            throw new RuntimeException("Error guardando respuesta " + id, e);
        }
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
     * Actualiza una respuesta existente.
     * 
     * @param r Respuesta a actualizar
     * @param idEncuesta ID de la encuesta
     * @throws IllegalArgumentException si la respuesta no existe
     */
    public void actualizarRespuesta(Respuesta r, int idEncuesta) {
        int id = r.getId();
        File archivo = new File("data/encuestas/" + idEncuesta + "/respuestas", id + ".json");
        
        if (!archivo.exists()) {
            throw new IllegalArgumentException("La respuesta " + id + " no existe");
        }

        try (FileWriter writer = new FileWriter(archivo)) {
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
            } else if (respuesta instanceof Double) {
                obj.addProperty("respuesta", (Double) respuesta);
            } else if (respuesta instanceof Integer) {
                obj.addProperty("respuesta", (Integer) respuesta);
            } else if (respuesta instanceof String) {
                obj.addProperty("respuesta", (String) respuesta);
            }
            
            gson.toJson(obj, writer);
        }
        catch (IOException e) {
            throw new RuntimeException("Error actualizando respuesta " + id, e);
        }
    }

    /**
     * Elimina una respuesta del disco.
     * 
     * @param idRespuesta ID de la respuesta
     * @param idEncuesta ID de la encuesta
     * @throws IllegalArgumentException si la respuesta no existe
     */
    public void eliminarRespuesta(int idRespuesta, int idEncuesta) {
        File archivo = new File("data/encuestas/" + idEncuesta + "/respuestas", idRespuesta + ".json");
        
        if (!archivo.delete()) {
            throw new IllegalArgumentException("No existe la respuesta " + idRespuesta);
        }
    }
}
