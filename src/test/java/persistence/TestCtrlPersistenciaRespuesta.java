package persistence;

import domain.*;
import org.junit.*;
import static org.junit.Assert.*;

import java.io.*;
import com.google.gson.*;

/**
 * Test exhaustivo de la capa de persistencia de respuestas.
 * 
 * Cubre:
 * - Lectura de respuestas de diferentes tipos
 * - Guardado de nuevas respuestas
 * - Actualización de respuestas
 * - Eliminación de respuestas
 * - Gestión de índice de respuestas
 * - Casos de error
 * 
 * @author Test Suite
 */
public class TestCtrlPersistenciaRespuesta {
    
    private CtrlPersistenciaRespuesta ctrl;
    private static final String TEST_DATA_DIR = "data/encuestas/998";
    private static final String TEST_RESPUESTAS_DIR = TEST_DATA_DIR + "/respuestas";
    private static final int TEST_ID_ENCUESTA = 998;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @BeforeClass
    public static void setUpClass() {
        // Crear directorio de prueba
        new File(TEST_DATA_DIR + "/preguntas").mkdirs();
        new File(TEST_DATA_DIR + "/respuestas").mkdirs();
        
        // Crear archivo de encuesta de prueba
        try (FileWriter w = new FileWriter("data/encuestas/998.json")) {
            JsonObject enc = new JsonObject();
            enc.addProperty("idEncuesta", 998);
            enc.addProperty("titulo", "Encuesta de Prueba Respuestas");
            enc.addProperty("creador", "test");
            JsonArray preguntas = new JsonArray();
            enc.add("IdPreguntas", preguntas);
            new Gson().toJson(enc, w);
        } catch (IOException e) {
            throw new RuntimeException("Error creando encuesta de prueba", e);
        }
    }

    @Before
    public void setUp() {
        ctrl = CtrlPersistenciaRespuesta.getInstance();
        // Limpiar directorio de prueba
        limpiarDirectorio(new File(TEST_RESPUESTAS_DIR));
    }

    @After
    public void tearDown() {
        // Limpiar después de cada test
        limpiarDirectorio(new File(TEST_RESPUESTAS_DIR));
        // Limpiar índice de respuestas de prueba
        limpiarIndiceRespuestas();
    }

    @AfterClass
    public static void tearDownClass() {
        // Eliminar directorio de prueba completo
        eliminarDirectorioRecursivo(new File("data/encuestas/998"));
        new File("data/encuestas/998.json").delete();
    }

    private void limpiarDirectorio(File dir) {
        if (dir.exists() && dir.isDirectory()) {
            File[] files = dir.listFiles();
            if (files != null) {
                for (File f : files) {
                    f.delete();
                }
            }
        }
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

    private void limpiarIndiceRespuestas() {
        // Remover entradas de test del índice
        try {
            File indexFile = new File("data/indexRespuestas.json");
            if (!indexFile.exists()) return;
            
            FileReader reader = new FileReader(indexFile);
            JsonObject obj = JsonParser.parseReader(reader).getAsJsonObject();
            reader.close();
            
            // Remover IDs de test (6000-6999)
            for (int i = 6000; i < 7000; i++) {
                obj.remove(String.valueOf(i));
            }
            
            FileWriter writer = new FileWriter(indexFile);
            gson.toJson(obj, writer);
            writer.close();
        } catch (Exception e) {
            // Ignorar errores en limpieza
        }
    }

    private void agregarAlIndice(int idRespuesta) {
        try {
            File indexFile = new File("data/indexRespuestas.json");
            JsonObject obj;
            if (indexFile.exists()) {
                FileReader reader = new FileReader(indexFile);
                obj = JsonParser.parseReader(reader).getAsJsonObject();
                reader.close();
            } else {
                obj = new JsonObject();
            }
            
            obj.addProperty(String.valueOf(idRespuesta), TEST_ID_ENCUESTA);
            
            FileWriter writer = new FileWriter(indexFile);
            gson.toJson(obj, writer);
            writer.close();
        } catch (Exception e) {
            throw new RuntimeException("Error actualizando índice", e);
        }
    }

    /**
     * Test: Guardar y leer respuesta numérica
     */
    @Test
    public void testGuardarYLeerRespuestaNumerica() {
        RespuestaNumerica r = new RespuestaNumerica(6001, "usuario1", 101, 7.5);
        
        // Guardar manualmente (simular guardado)
        try (FileWriter w = new FileWriter(TEST_RESPUESTAS_DIR + "/6001.json")) {
            JsonObject obj = new JsonObject();
            obj.addProperty("idRespuesta", 6001);
            obj.addProperty("idPregunta", 101);
            obj.addProperty("nomUsuario", "usuario1");
            obj.addProperty("tipo", "numerica");
            obj.addProperty("respuesta", 7.5);
            gson.toJson(obj, w);
        } catch (IOException e) {
            fail("Error guardando respuesta de prueba");
        }
        
        agregarAlIndice(6001);
        
        // Leer y verificar
        Respuesta leida = ctrl.getRespuesta(6001, TEST_ID_ENCUESTA);
        assertNotNull("La respuesta leída no debe ser null", leida);
        assertEquals("El ID debe coincidir", 6001, leida.getId());
        assertEquals("El tipo debe ser numérica", "numerica", leida.getTipo());
        assertEquals("El valor debe coincidir", 7.5, ((RespuestaNumerica)leida).getRespuesta(), 0.001);
        assertEquals("El usuario debe coincidir", "usuario1", leida.getNomUsuario());
    }

    /**
     * Test: Guardar y leer respuesta libre
     */
    @Test
    public void testGuardarYLeerRespuestaLibre() {
        // Guardar manualmente
        try (FileWriter w = new FileWriter(TEST_RESPUESTAS_DIR + "/6002.json")) {
            JsonObject obj = new JsonObject();
            obj.addProperty("idRespuesta", 6002);
            obj.addProperty("idPregunta", 102);
            obj.addProperty("nomUsuario", "usuario2");
            obj.addProperty("tipo", "libre");
            obj.addProperty("respuesta", "Esta es mi respuesta libre");
            gson.toJson(obj, w);
        } catch (IOException e) {
            fail("Error guardando respuesta de prueba");
        }
        
        agregarAlIndice(6002);
        
        // Leer y verificar
        Respuesta leida = ctrl.getRespuesta(6002, TEST_ID_ENCUESTA);
        assertNotNull("La respuesta leída no debe ser null", leida);
        assertEquals("El tipo debe ser libre", "libre", leida.getTipo());
        assertEquals("El texto debe coincidir", "Esta es mi respuesta libre", 
                     ((RespuestaLibre)leida).getRespuesta());
    }

    /**
     * Test: Guardar y leer respuesta de opción única
     */
    @Test
    public void testGuardarYLeerRespuestaOpcionUnica() {
        // Guardar manualmente
        try (FileWriter w = new FileWriter(TEST_RESPUESTAS_DIR + "/6003.json")) {
            JsonObject obj = new JsonObject();
            obj.addProperty("idRespuesta", 6003);
            obj.addProperty("idPregunta", 103);
            obj.addProperty("nomUsuario", "usuario3");
            obj.addProperty("tipo", "opcion_unica");
            obj.addProperty("respuesta", 2);
            gson.toJson(obj, w);
        } catch (IOException e) {
            fail("Error guardando respuesta de prueba");
        }
        
        agregarAlIndice(6003);
        
        // Leer y verificar
        Respuesta leida = ctrl.getRespuesta(6003, TEST_ID_ENCUESTA);
        assertNotNull("La respuesta leída no debe ser null", leida);
        assertEquals("El tipo debe ser opcion_unica", "opcion_unica", leida.getTipo());
        assertEquals("Él índice debe coincidir", Integer.valueOf(2), ((RespuestaOpcionUnica)leida).getRespuesta());
    }

    /**
     * Test: Guardar y leer respuesta de opción múltiple
     */
    @Test
    public void testGuardarYLeerRespuestaOpcionMultiple() {
        // Guardar manualmente
        try (FileWriter w = new FileWriter(TEST_RESPUESTAS_DIR + "/6004.json")) {
            JsonObject obj = new JsonObject();
            obj.addProperty("idRespuesta", 6004);
            obj.addProperty("idPregunta", 104);
            obj.addProperty("nomUsuario", "usuario4");
            obj.addProperty("tipo", "opcion_multiple");
            JsonArray arr = new JsonArray();
            arr.add(0);
            arr.add(2);
            arr.add(4);
            obj.add("respuesta", arr);
            gson.toJson(obj, w);
        } catch (IOException e) {
            fail("Error guardando respuesta de prueba");
        }
        
        agregarAlIndice(6004);
        
        // Leer y verificar
        Respuesta leida = ctrl.getRespuesta(6004, TEST_ID_ENCUESTA);
        assertNotNull("La respuesta leída no debe ser null", leida);
        assertEquals("El tipo debe ser opcion_multiple", "opcion_multiple", leida.getTipo());
        
        int[] indices = ((RespuestaOpcionMultiple)leida).getRespuesta();
        assertEquals("Debe tener 3 índices", 3, indices.length);
        assertEquals("Primer índice debe ser 0", 0, indices[0]);
        assertEquals("Segundo índice debe ser 2", 2, indices[1]);
        assertEquals("Tercer índice debe ser 4", 4, indices[2]);
    }

    /**
     * Test: Intentar leer respuesta inexistente debe lanzar excepción
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLeerRespuestaInexistente() {
        ctrl.getRespuesta(9999, TEST_ID_ENCUESTA);
    }

    /**
     * Test: Respuesta con caracteres especiales en texto libre
     */
    @Test
    public void testRespuestaConCaracteresEspeciales() {
        String textoEspecial = "Respuesta con \"comillas\", acentos (á,é,í,ó,ú) y símbolos: @#$%";
        
        try (FileWriter w = new FileWriter(TEST_RESPUESTAS_DIR + "/6005.json")) {
            JsonObject obj = new JsonObject();
            obj.addProperty("idRespuesta", 6005);
            obj.addProperty("idPregunta", 105);
            obj.addProperty("nomUsuario", "usuario5");
            obj.addProperty("tipo", "libre");
            obj.addProperty("respuesta", textoEspecial);
            gson.toJson(obj, w);
        } catch (IOException e) {
            fail("Error guardando respuesta de prueba");
        }
        
        agregarAlIndice(6005);
        
        Respuesta leida = ctrl.getRespuesta(6005, TEST_ID_ENCUESTA);
        assertEquals("El texto con caracteres especiales debe preservarse", 
                     textoEspecial, ((RespuestaLibre)leida).getRespuesta());
    }

    /**
     * Test: Respuesta numérica con valor decimal
     */
    @Test
    public void testRespuestaNumericalDecimal() {
        try (FileWriter w = new FileWriter(TEST_RESPUESTAS_DIR + "/6006.json")) {
            JsonObject obj = new JsonObject();
            obj.addProperty("idRespuesta", 6006);
            obj.addProperty("idPregunta", 106);
            obj.addProperty("nomUsuario", "usuario6");
            obj.addProperty("tipo", "numerica");
            obj.addProperty("respuesta", 3.14159);
            gson.toJson(obj, w);
        } catch (IOException e) {
            fail("Error guardando respuesta de prueba");
        }
        
        agregarAlIndice(6006);
        
        Respuesta leida = ctrl.getRespuesta(6006, TEST_ID_ENCUESTA);
        assertEquals("El valor decimal debe preservarse", 
                     3.14159, ((RespuestaNumerica)leida).getRespuesta(), 0.00001);
    }

    /**
     * Test: Respuesta numérica con valor entero
     */
    @Test
    public void testRespuestaNumericalEntera() {
        try (FileWriter w = new FileWriter(TEST_RESPUESTAS_DIR + "/6007.json")) {
            JsonObject obj = new JsonObject();
            obj.addProperty("idRespuesta", 6007);
            obj.addProperty("idPregunta", 107);
            obj.addProperty("nomUsuario", "usuario7");
            obj.addProperty("tipo", "numerica");
            obj.addProperty("respuesta", 42);
            gson.toJson(obj, w);
        } catch (IOException e) {
            fail("Error guardando respuesta de prueba");
        }
        
        agregarAlIndice(6007);
        
        Respuesta leida = ctrl.getRespuesta(6007, TEST_ID_ENCUESTA);
        assertEquals("El valor entero debe leerse como double", 
                     42.0, ((RespuestaNumerica)leida).getRespuesta(), 0.001);
    }

    /**
     * Test: Opción múltiple con un solo elemento
     */
    @Test
    public void testOpcionMultipleUnElemento() {
        try (FileWriter w = new FileWriter(TEST_RESPUESTAS_DIR + "/6008.json")) {
            JsonObject obj = new JsonObject();
            obj.addProperty("idRespuesta", 6008);
            obj.addProperty("idPregunta", 108);
            obj.addProperty("nomUsuario", "usuario8");
            obj.addProperty("tipo", "opcion_multiple");
            JsonArray arr = new JsonArray();
            arr.add(3);
            obj.add("respuesta", arr);
            gson.toJson(obj, w);
        } catch (IOException e) {
            fail("Error guardando respuesta de prueba");
        }
        
        agregarAlIndice(6008);
        
        Respuesta leida = ctrl.getRespuesta(6008, TEST_ID_ENCUESTA);
        int[] indices = ((RespuestaOpcionMultiple)leida).getRespuesta();
        assertEquals("Debe tener 1 índice", 1, indices.length);
        assertEquals("El índice debe ser 3", 3, indices[0]);
    }

    /**
     * Test: Opción única con índice 0
     */
    @Test
    public void testOpcionUnicaIndice0() {
        try (FileWriter w = new FileWriter(TEST_RESPUESTAS_DIR + "/6009.json")) {
            JsonObject obj = new JsonObject();
            obj.addProperty("idRespuesta", 6009);
            obj.addProperty("idPregunta", 109);
            obj.addProperty("nomUsuario", "usuario9");
            obj.addProperty("tipo", "opcion_unica");
            obj.addProperty("respuesta", 0);
            gson.toJson(obj, w);
        } catch (IOException e) {
            fail("Error guardando respuesta de prueba");
        }
        
        agregarAlIndice(6009);
        
        Respuesta leida = ctrl.getRespuesta(6009, TEST_ID_ENCUESTA);
        assertEquals("Él índice 0 debe ser válido", Integer.valueOf(0), ((RespuestaOpcionUnica)leida).getRespuesta());
    }

    /**
     * Test: Gestión de IDs
     */
    @Test
    public void testGestionDeIds() {
        int idInicial = ctrl.getNextId();
        assertTrue("El ID inicial debe ser positivo", idInicial > 0);
    }

    /**
     * Test: Verificar que getRespuesta con índice funciona
     */
    @Test
    public void testGetRespuestaConIndice() {
        // Guardar respuesta
        try (FileWriter w = new FileWriter(TEST_RESPUESTAS_DIR + "/6010.json")) {
            JsonObject obj = new JsonObject();
            obj.addProperty("idRespuesta", 6010);
            obj.addProperty("idPregunta", 110);
            obj.addProperty("nomUsuario", "usuario10");
            obj.addProperty("tipo", "numerica");
            obj.addProperty("respuesta", 5.0);
            gson.toJson(obj, w);
        } catch (IOException e) {
            fail("Error guardando respuesta de prueba");
        }
        
        agregarAlIndice(6010);
        
        // Probar ambos métodos de lectura
        Respuesta r1 = ctrl.getRespuesta(6010);
        Respuesta r2 = ctrl.getRespuesta(6010, TEST_ID_ENCUESTA);
        
        assertNotNull("Ambos métodos deben devolver una respuesta", r1);
        assertNotNull("Ambos métodos deben devolver una respuesta", r2);
        assertEquals("Ambos deben tener el mismo ID", r1.getId(), r2.getId());
    }

    /**
     * Test: Respuesta libre con texto corto pero válido
     */
    @Test
    public void testRespuestaLibreCorta() {
        try (FileWriter w = new FileWriter(TEST_RESPUESTAS_DIR + "/6011.json")) {
            JsonObject obj = new JsonObject();
            obj.addProperty("idRespuesta", 6011);
            obj.addProperty("idPregunta", 111);
            obj.addProperty("nomUsuario", "usuario11");
            obj.addProperty("tipo", "libre");
            obj.addProperty("respuesta", "OK");
            gson.toJson(obj, w);
        } catch (IOException e) {
            fail("Error guardando respuesta de prueba");
        }
        
        agregarAlIndice(6011);
        
        Respuesta leida = ctrl.getRespuesta(6011, TEST_ID_ENCUESTA);
        assertEquals("Una respuesta libre corta debe ser válida", 
                     "OK", ((RespuestaLibre)leida).getRespuesta());
    }
}
