package persistence;

import com.google.gson.*;
import domain.Usuario;
import java.io.*;
import java.util.*;

/**
 * Controlador de persistencia de usuarios en disco.
 * 
 * Gestiona la lectura y escritura de usuarios en formato JSON.
 * No mantiene cache interna - la cache es gestionada por GestorPersistencia.
 * 
 * RESPONSABILIDADES:
 * - Cargar usuarios desde archivo JSON
 * - Guardar usuarios a archivo JSON
 * - Gestionar operaciones I/O con disco
 * 
 * ATRIBUTOS PRINCIPALES:
 * - FILE: Ruta del archivo JSON
 * - gson: Serializador/deserializador JSON
 * 
 * MÉTODOS PRINCIPALES:
 * - cargarUsuario(): Lee un usuario específico del disco
 * - cargarTodos(): Lee todos los usuarios del disco
 * - guardar(): Escribe mapa completo de usuarios a disco
 * 
 * @author PROP Cluster XX
 */
public class CtrlPersistenciaUsuario {
    private static final String FILE = "DATA/usuarios.json";
    private final Gson gson;

    /**
     * Crea controlador de persistencia de usuarios.
     */
    public CtrlPersistenciaUsuario() {
        this.gson = new Gson();
        crearDirectorio();
    }

    /**
     * Carga un usuario específico desde disco.
     * 
     * @param nom Nombre del usuario
     * @return Usuario encontrado, o null si no existe
     */
    public Usuario cargarUsuario(String nom) {
        Map<String, Usuario> todos = cargarTodos();
        return todos.get(nom);
    }

    /**
     * Carga todos los usuarios desde disco.
     * 
     * @return Mapa de nombre → Usuario (vacío si archivo no existe)
     */
    public Map<String, Usuario> cargarTodos() {
        File f = new File(FILE);
        if (!f.exists()) return new HashMap<>();
        
        try (FileReader r = new FileReader(f)) {
            JsonObject root = JsonParser.parseReader(r).getAsJsonObject();
            if (!root.has("usuarios")) return new HashMap<>();
            
            JsonObject usuariosObj = root.getAsJsonObject("usuarios");
            Map<String, Usuario> result = new HashMap<>();
            
            for (Map.Entry<String, JsonElement> entry : usuariosObj.entrySet()) {
                Usuario u = gson.fromJson(entry.getValue(), Usuario.class);
                result.put(entry.getKey(), u);
            }
            return result;
        } catch (IOException e) {
            return new HashMap<>();
        }
    }

    /**
     * Guarda el mapa completo de usuarios a disco.
     * 
     * @param usuarios Mapa de nombre → Usuario
     */
    public void guardar(Map<String, Usuario> usuarios) {
        JsonObject root = new JsonObject();
        JsonObject usuariosObj = new JsonObject();
        
        for (Map.Entry<String, Usuario> e : usuarios.entrySet()) {
            usuariosObj.add(e.getKey(), gson.toJsonTree(e.getValue()));
        }
        root.add("usuarios", usuariosObj);
        
        try (FileWriter w = new FileWriter(FILE)) {
            gson.toJson(root, w);
        } catch (IOException e) {
            throw new RuntimeException("Error guardando usuarios: " + e.getMessage());
        }
    }

    /**
     * Crea directorio DATA si no existe.
     */
    private void crearDirectorio() {
        new File("DATA").mkdirs();
    }
}
