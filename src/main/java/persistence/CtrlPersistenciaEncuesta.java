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
 * @author PROP Cluster XX
 */
public class CtrlPersistenciaEncuesta {
    private static final String FILE = "DATA/encuestas.json";
    private final Gson gson;

    /**
     * Crea controlador de persistencia de encuestas.
     */
    public CtrlPersistenciaEncuesta() {
        this.gson = new Gson();
        crearDirectorio();
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

    /**
     * Crea directorio DATA si no existe.
     */
    private void crearDirectorio() {
        new File("DATA").mkdirs();
    }
}
