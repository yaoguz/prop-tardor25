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

    /**
     * Test: Crear pregunta, guardarla, responderla y recuperar todo
     */
    @Test
    public void testFlujoPreguntaYRespuesta() {
        // 1. Crear y guardar pregunta numérica
        PreguntaNumerica pregunta = new PreguntaNumerica(TEST_ID_ENCUESTA, 7001, 
                                                         "¿Cuántos años tienes?", 0, 120);
        ctrlPregunta.guardar(pregunta);
        
        // 2. Verificar que se puede leer
        Pregunta preguntaLeida = ctrlPregunta.getPregunta(7001);
        assertNotNull("La pregunta debe existir", preguntaLeida);
        assertEquals("El enunciado debe coincidir", "¿Cuántos años tienes?", preguntaLeida.getEnunciado());
        
        // 3. Crear respuesta para esa pregunta
        // Simulamos guardar una respuesta (normalmente esto lo haría CtrlPersistenciaRespuesta)
        try (FileWriter w = new FileWriter(TEST_DATA_DIR + "/respuestas/7501.json")) {
            com.google.gson.JsonObject obj = new com.google.gson.JsonObject();
            obj.addProperty("idRespuesta", 7501);
            obj.addProperty("idPregunta", 7001);
            obj.addProperty("nomUsuario", "testuser");
            obj.addProperty("tipo", "numerica");
            obj.addProperty("respuesta", 25.0);
            new com.google.gson.Gson().toJson(obj, w);
        } catch (IOException e) {
            fail("Error guardando respuesta");
        }
        
        // Agregar al índice
        try {
            File indexFile = new File("data/indexRespuestas.json");
            com.google.gson.JsonObject idx;
            if (indexFile.exists()) {
                com.google.gson.JsonParser parser = new com.google.gson.JsonParser();
                idx = parser.parseReader(new FileReader(indexFile)).getAsJsonObject();
            } else {
                idx = new com.google.gson.JsonObject();
            }
            idx.addProperty("7501", TEST_ID_ENCUESTA);
            FileWriter w = new FileWriter(indexFile);
            new com.google.gson.Gson().toJson(idx, w);
            w.close();
        } catch (Exception e) {
            // OK si falla
        }
        
        // 4. Leer la respuesta
        Respuesta respuesta = ctrlRespuesta.getRespuesta(7501, TEST_ID_ENCUESTA);
        assertNotNull("La respuesta debe existir", respuesta);
        assertEquals("El tipo debe ser numérico", "numerica", respuesta.getTipo());
        assertEquals("El valor debe coincidir", 25.0, ((RespuestaNumerica)respuesta).getRespuesta(), 0.001);
        
        // 5. Actualizar la pregunta con la respuesta
        preguntaLeida.guardarIdRespuesta(7501);
        ctrlPregunta.sobreescribe((PreguntaNumerica)preguntaLeida);
        
        // 6. Verificar que la pregunta ahora tiene la respuesta
        Pregunta preguntaActualizada = ctrlPregunta.getPregunta(7001);
        assertTrue("La pregunta debe tener la respuesta registrada", 
                   preguntaActualizada.getIdRespuestas().contains(7501));
        
        // Cleanup
        ctrlPregunta.eliminarPregunta(7001);
        new File(TEST_DATA_DIR + "/respuestas/7501.json").delete();
    }

    /**
     * Test: Crear múltiples preguntas y verificar que todas se registran en la encuesta
     */
    @Test
    public void testMultiplesPreguntasEnEncuesta() throws IOException {
        // Crear y guardar 3 preguntas
        PreguntaNumerica p1 = new PreguntaNumerica(TEST_ID_ENCUESTA, 7010, "Pregunta 1", 0, 10);
        PreguntaNumerica p2 = new PreguntaNumerica(TEST_ID_ENCUESTA, 7011, "Pregunta 2", 0, 10);
        PreguntaNumerica p3 = new PreguntaNumerica(TEST_ID_ENCUESTA, 7012, "Pregunta 3", 0, 10);
        
        ctrlPregunta.guardar(p1);
        ctrlPregunta.guardar(p2);
        ctrlPregunta.guardar(p3);
        
        // Verificar que todas están en el archivo de encuesta
        FileReader reader = new FileReader("data/encuestas/997.json");
        com.google.gson.JsonObject encuesta = com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();
        reader.close();
        
        com.google.gson.JsonArray preguntas = encuesta.getAsJsonArray("IdPreguntas");
        assertTrue("Debe contener pregunta 7010", contiene(preguntas, 7010));
        assertTrue("Debe contener pregunta 7011", contiene(preguntas, 7011));
        assertTrue("Debe contener pregunta 7012", contiene(preguntas, 7012));
        
        // Verificar que todas se pueden leer
        assertNotNull(ctrlPregunta.getPregunta(7010));
        assertNotNull(ctrlPregunta.getPregunta(7011));
        assertNotNull(ctrlPregunta.getPregunta(7012));
        
        // Cleanup
        ctrlPregunta.eliminarPregunta(7010);
        ctrlPregunta.eliminarPregunta(7011);
        ctrlPregunta.eliminarPregunta(7012);
        
        // Verificar que se eliminaron del archivo de encuesta
        reader = new FileReader("data/encuestas/997.json");
        encuesta = com.google.gson.JsonParser.parseReader(reader).getAsJsonObject();
        reader.close();
        
        preguntas = encuesta.getAsJsonArray("IdPreguntas");
        assertFalse("No debe contener pregunta 7010", contiene(preguntas, 7010));
        assertFalse("No debe contener pregunta 7011", contiene(preguntas, 7011));
        assertFalse("No debe contener pregunta 7012", contiene(preguntas, 7012));
    }
    
    private boolean contiene(com.google.gson.JsonArray arr, int valor) {
        for (com.google.gson.JsonElement elem : arr) {
            if (elem.getAsInt() == valor) return true;
        }
        return false;
    }

    /**
     * Test: Pregunta con múltiples respuestas de diferentes usuarios
     */
    @Test
    public void testPreguntaConMultiplesRespuestas() {
        // Crear pregunta de opción única
        PreguntaOpcionUnica pregunta = new PreguntaOpcionUnica(TEST_ID_ENCUESTA, 7020, 
                                                                "¿Color favorito?", false);
        pregunta.addOpcion("Rojo");
        pregunta.addOpcion("Azul");
        pregunta.addOpcion("Verde");
        ctrlPregunta.guardar(pregunta);
        
        // Crear 3 respuestas diferentes
        for (int i = 0; i < 3; i++) {
            try (FileWriter w = new FileWriter(TEST_DATA_DIR + "/respuestas/" + (7520 + i) + ".json")) {
                com.google.gson.JsonObject obj = new com.google.gson.JsonObject();
                obj.addProperty("idRespuesta", 7520 + i);
                obj.addProperty("idPregunta", 7020);
                obj.addProperty("nomUsuario", "user" + i);
                obj.addProperty("tipo", "opcion_unica");
                obj.addProperty("respuesta", i); // Cada usuario elige opción diferente
                new com.google.gson.Gson().toJson(obj, w);
            } catch (IOException e) {
                fail("Error guardando respuesta");
            }
        }
        
        // Registrar respuestas en la pregunta
        Pregunta p = ctrlPregunta.getPregunta(7020);
        p.guardarIdRespuesta(7520);
        p.guardarIdRespuesta(7521);
        p.guardarIdRespuesta(7522);
        ctrlPregunta.sobreescribe(p);
        
        // Verificar que la pregunta tiene todas las respuestas
        Pregunta pActualizada = ctrlPregunta.getPregunta(7020);
        assertEquals("Debe tener 3 respuestas", 3, pActualizada.getIdRespuestas().size());
        
        // Cleanup
        ctrlPregunta.eliminarPregunta(7020);
        for (int i = 0; i < 3; i++) {
            new File(TEST_DATA_DIR + "/respuestas/" + (7520 + i) + ".json").delete();
        }
    }
}
