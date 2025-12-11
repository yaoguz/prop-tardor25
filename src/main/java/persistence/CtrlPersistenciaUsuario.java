package persistence;

import com.google.gson.*;
import domain.Usuario;
import java.io.*;
import java.util.*;

/**
 * Controlador de persistencia de usuarios en disco.
 * 
 * Gestiona la lectura y escritura de usuarios en formato JSON.
 * No mantiene cache interna - la cache es gestionada por GestorPersistenciaUsuario.
 * 
 * RESPONSABILIDADES:
 * - Cargar usuarios desde archivo JSON
 * - Guardar usuarios a archivo JSON
 * - Actualizar usuarios existentes
 * - Eliminar usuarios del disco
 * - Gestionar operaciones I/O con disco
 * 
 * ATRIBUTOS PRINCIPALES:
 * - gson: Serializador/deserializador JSON con pretty printing
 * 
 * MÉTODOS PRINCIPALES:
 * - getUsuario(): Lee un usuario específico del disco
 * - getAllUsuarios(): Lee todos los usuarios del sistema
 * - guardarUsuario(): Escribe un nuevo usuario a disco
 * - actualizarUsuario(): Actualiza un usuario existente
 * - eliminarUsuario(): Elimina un usuario del disco
 * - existeUsuario(): Verifica existencia de un usuario
 * 
 * @author Alessandro Jin 
 */
public class CtrlPersistenciaUsuario {
 
    private final Gson gson;

    /**
     * Crea controlador de persistencia de usuarios.
     */
    public CtrlPersistenciaUsuario() {
        this.gson = new GsonBuilder().setPrettyPrinting().create();
    }

    /**
     * Obtiene un usuario desde disco.
     * 
     * @param nom Nombre del usuario
     * @return Usuario encontrado
     * @throws RuntimeException si hay error leyendo el archivo
     */
    public Usuario getUsuario (String nom) {
        String FILE = "data/usuarios/" + nom + ".json";
    
        try (FileReader reader = new FileReader(FILE)) {

            JsonObject usuario = JsonParser.parseReader(reader).getAsJsonObject();
          
            Usuario u = new Gson().fromJson(usuario, Usuario.class);

            return u;
        }

        catch(Exception exc) {
            throw new RuntimeException("Error leyendo el archivo JSON", exc);
        }
    }

    /**
     * Obtiene todos los usuarios del sistema.
     * 
     * @return Lista con todos los usuarios
     */
    public ArrayList<Usuario> getAllUsuarios() {

        File carpetaRaiz = new File("data/usuarios");

        File[] jsonFiles = carpetaRaiz.listFiles(
            file -> file.isFile() && file.getName().endsWith(".json")
        );

        if (jsonFiles != null) {

            ArrayList<Usuario> usuarios = new ArrayList<>();

            for (File jsonFile: jsonFiles) {
                
                try{
                    String fileName = jsonFile.getName(); // "ana.json"
                    String userName = fileName.replace(".json", ""); // "ana"

                    usuarios.add(getUsuario(userName));
                }

                catch(Exception exc) {
                    throw new RuntimeException("Error el nombre de la carpeta no es un nombre válido", exc);
                }
            }

            return usuarios;
        }

        else return null;

    }

    /**
     * Guarda un nuevo usuario en disco.
     * 
     * @param u Usuario a guardar
     * @throws IllegalArgumentException si el usuario ya existe
     */
    public void guardarUsuario (Usuario u) {
        String newName = u.getNomUsuario();

        File archivoJson = new File("data/usuarios", newName + ".json");

        if (archivoJson.exists()) throw new IllegalArgumentException("El usuario Ya existe");

        try (FileWriter writer = new FileWriter(archivoJson)) {
        
            gson.toJson(u, writer);

        } 
        catch (IOException exc) {
            throw new RuntimeException("Error al guardar el archivo JSON", exc);
        }
    }

    /**
     * Verifica si existe un usuario.
     * 
     * @param name Nombre del usuario
     * @return true si existe
     */
    public boolean existeUsuario(String name) {

        File archivo = new File("data/usuarios", name + ".json");
        return archivo.exists();
    }

    /**
     * Actualiza un usuario existente.
     * 
     * @param u Usuario a actualizar
     * @throws IllegalArgumentException si el usuario no existe
     */
    public void actualizarUsuario (Usuario u) {

        String newName = u.getNomUsuario();

        File archivoJson = new File("data/usuarios", newName + ".json");

        if (!archivoJson.exists()) throw new IllegalArgumentException("El usuario No existe");

        //si existe devuelve la ruta sino la crea
        try (FileWriter writer = new FileWriter(archivoJson)) {
            
            //sobre escribe pq append es 0
            gson.toJson(u, writer);

        } 
        catch (IOException exc) {
            throw new RuntimeException("Error al guardar el archivo JSON", exc);
        }

    }

    /**
     * Elimina un usuario del disco.
     * 
     * @param name Nombre del usuario
     * @throws IllegalArgumentException si el usuario no existe
     */
    public void eliminarUsuario (String name) {

        File archivo = new File("data/usuarios", name + ".json"); //solo es  una ruta

        boolean eliminado = archivo.delete();
        
        if (!eliminado) {
            throw new IllegalArgumentException("No existe el usuario a eliminar");
        }
    }
}