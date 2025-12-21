package persistence;

import domain.*;
import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;

/**
 * Test de integración de la capa de persistencia.
 * 
 * Prueba flujos completos que involucran múltiples controladores:
 * - Crear encuesta con preguntas y respuestas
 * - Leer y modificar datos
 * - Verificar consistencia entre componentes
 * 
 * @author Test Suite
 */
public class TestIntegracionPersistencia {
    
    private CtrlPersistenciaPregunta ctrlPregunta;
    private CtrlPersistenciaRespuesta ctrlRespuesta;
    private static final String TEST_DATA_DIR = "data/encuestas/997";
    private static final int TEST_ID_ENCUESTA = 997;

    @BeforeClass
    public static void setUpClass() {
        // Crear directorio de prueba
        new File(TEST_DATA_DIR + "/preguntas").mkdirs();
        new File(TEST_DATA_DIR + "/respuestas").mkdirs();
        
        // Crear archivo de encuesta de prueba
        try (FileWriter w = new FileWriter("data/encuestas/997.json")) {
            com.google.gson.JsonObject enc = new com.google.gson.JsonObject();
            enc.addProperty("idEncuesta", 997);
            enc.addProperty("titulo", "Encuesta de Integración");
            enc.addProperty("creador", "test");
            com.google.gson.JsonArray preguntas = new com.google.gson.JsonArray();
            enc.add("IdPreguntas", preguntas);
            new com.google.gson.Gson().toJson(enc, w);
        } catch (IOException e) {
            throw new RuntimeException("Error creando encuesta de prueba", e);
        }
    }

    @Before
    public void setUp() {
        ctrlPregunta = CtrlPersistenciaPregunta.getInstance();
        ctrlRespuesta = CtrlPersistenciaRespuesta.getInstance();
    }

    @AfterClass
    public static void tearDownClass() {
        // Eliminar directorio de prueba completo
        eliminarDirectorioRecursivo(new File("data/encuestas/997"));
        new File("data/encuestas/997.json").delete();
    }

    private static void eliminarDirectorioRecursivo(File dir) {
        if (dir.exists()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        eliminarDirectorioRecursivo(f);
                    } else {
                        f.delete();
                    }
                }
            }
            dir.delete();
        }
    }
    
    private boolean contiene(com.google.gson.JsonArray arr, int valor) {
        for (com.google.gson.JsonElement elem : arr) {
            if (elem.getAsInt() == valor) return true;
        }
        return false;
    }
}
