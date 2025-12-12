package persistence;

import domain.*;
import org.junit.*;
import static org.junit.Assert.*;

import java.io.*;
import java.nio.file.*;
import com.google.gson.*;

/**
 * Test exhaustivo de la capa de persistencia de preguntas.
 * 
 * Cubre:
 * - Lectura de preguntas de diferentes tipos
 * - Guardado de nuevas preguntas
 * - Actualización de preguntas existentes
 * - Eliminación de preguntas
 * - Casos de error
 * - Gestión de IDs
 * 
 * @author Test Suite
 */
public class TestCtrlPersistenciaPregunta {
    
    private CtrlPersistenciaPregunta ctrl;
    private static final String TEST_DATA_DIR = "data/encuestas/999"; // Encuesta de prueba
    private static final String TEST_PREGUNTAS_DIR = TEST_DATA_DIR + "/preguntas";
    private static final int TEST_ID_ENCUESTA = 999;
    private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @BeforeClass
    public static void setUpClass() {
        // Crear directorio de prueba
        new File(TEST_DATA_DIR + "/preguntas").mkdirs();
        new File(TEST_DATA_DIR + "/respuestas").mkdirs();
        
        // Crear archivo de encuesta de prueba
        try (FileWriter w = new FileWriter("data/encuestas/999.json")) {
            JsonObject enc = new JsonObject();
            enc.addProperty("idEncuesta", 999);
            enc.addProperty("titulo", "Encuesta de Prueba");
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
        ctrl = CtrlPersistenciaPregunta.getInstance();
        // Limpiar directorio de prueba
        limpiarDirectorio(new File(TEST_PREGUNTAS_DIR));
    }

    @After
    public void tearDown() {
        // Limpiar después de cada test
        limpiarDirectorio(new File(TEST_PREGUNTAS_DIR));
    }

    @AfterClass
    public static void tearDownClass() {
        // Eliminar directorio de prueba completo
        eliminarDirectorioRecursivo(new File("data/encuestas/999"));
        new File("data/encuestas/999.json").delete();
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

    /**
     * Test: Guardar y leer pregunta numérica
     */
    @Test
    public void testGuardarYLeerPreguntaNumerica() {
        PreguntaNumerica p = new PreguntaNumerica(TEST_ID_ENCUESTA, 5001, "¿Cuántas horas?", 0, 24);
        
        // Guardar
        ctrl.guardar(p);
        
        // Verificar que se creó el archivo
        File archivo = new File(TEST_PREGUNTAS_DIR + "/5001.json");
        assertTrue("El archivo debe existir", archivo.exists());
        
        // Leer y verificar
        Pregunta leida = ctrl.getPregunta(5001);
        assertNotNull("La pregunta leída no debe ser null", leida);
        assertEquals("El ID debe coincidir", 5001, leida.getIdPregunta());
        assertEquals("El tipo debe ser numérica", "numerica", leida.tipoPregunta());
        assertEquals("El enunciado debe coincidir", "¿Cuántas horas?", leida.getEnunciado());
        assertEquals("Rango inferior debe coincidir", 0, leida.getRangoInf());
        assertEquals("Rango superior debe coincidir", 24, leida.getRangoSup());
        assertEquals("ID de encuesta debe coincidir", TEST_ID_ENCUESTA, leida.getIdEncuesta());
    }

    /**
     * Test: Guardar y leer pregunta de opción única
     */
    @Test
    public void testGuardarYLeerPreguntaOpcionUnica() {
        PreguntaOpcionUnica p = new PreguntaOpcionUnica(TEST_ID_ENCUESTA, 5002, "¿Color favorito?", false);
        p.addOpcion("Rojo");
        p.addOpcion("Azul");
        p.addOpcion("Verde");
        
        // Guardar
        ctrl.guardar(p);
        
        // Leer y verificar
        Pregunta leida = ctrl.getPregunta(5002);
        assertNotNull("La pregunta leída no debe ser null", leida);
        assertEquals("El tipo debe ser opcion_unica", "opcion_unica", leida.tipoPregunta());
        assertEquals("Debe tener 3 opciones", 3, leida.getOpciones().size());
        assertEquals("Primera opción debe ser Rojo", "Rojo", leida.getOpciones().get(0));
        assertFalse("No debe ser ordenada", leida.esOrdenada());
    }

    /**
     * Test: Guardar y leer pregunta de opción múltiple
     */
    @Test
    public void testGuardarYLeerPreguntaOpcionMultiple() {
        PreguntaOpcionMultiple p = new PreguntaOpcionMultiple(TEST_ID_ENCUESTA, 5003, "¿Qué deportes practicas?");
        p.addOpcion("Fútbol");
        p.addOpcion("Baloncesto");
        p.addOpcion("Tenis");
        p.setNumMaxOpc(2);
        
        // Guardar
        ctrl.guardar(p);
        
        // Leer y verificar
        Pregunta leida = ctrl.getPregunta(5003);
        assertNotNull("La pregunta leída no debe ser null", leida);
        assertEquals("El tipo debe ser opcion_multiple", "opcion_multiple", leida.tipoPregunta());
        assertEquals("Debe tener 3 opciones", 3, leida.getOpciones().size());
        assertEquals("Número máximo de opciones debe ser 2", 2, leida.getNumMaxOpc());
    }

    /**
     * Test: Guardar y leer pregunta libre
     */
    @Test
    public void testGuardarYLeerPreguntaLibre() {
        PreguntaLibre p = new PreguntaLibre(TEST_ID_ENCUESTA, 5004, "¿Cuál es tu opinión?");
        
        // Guardar
        ctrl.guardar(p);
        
        // Leer y verificar
        Pregunta leida = ctrl.getPregunta(5004);
        assertNotNull("La pregunta leída no debe ser null", leida);
        assertEquals("El tipo debe ser libre", "libre", leida.tipoPregunta());
        assertEquals("El enunciado debe coincidir", "¿Cuál es tu opinión?", leida.getEnunciado());
    }

    /**
     * Test: Sobreescribir pregunta existente
     */
    @Test
    public void testSobreescribirPregunta() {
        // Crear y guardar pregunta original
        PreguntaNumerica p1 = new PreguntaNumerica(TEST_ID_ENCUESTA, 5005, "Pregunta Original", 0, 10);
        ctrl.guardar(p1);
        
        // Modificar y sobreescribir
        PreguntaNumerica p2 = new PreguntaNumerica(TEST_ID_ENCUESTA, 5005, "Pregunta Modificada", 5, 15);
        ctrl.sobreescribe(p2);
        
        // Leer y verificar que se actualizó
        Pregunta leida = ctrl.getPregunta(5005);
        assertEquals("El enunciado debe estar actualizado", "Pregunta Modificada", leida.getEnunciado());
        assertEquals("Rango inferior debe estar actualizado", 5, leida.getRangoInf());
        assertEquals("Rango superior debe estar actualizado", 15, leida.getRangoSup());
    }

    /**
     * Test: Intentar sobreescribir pregunta inexistente debe lanzar excepción
     */
    @Test(expected = IllegalArgumentException.class)
    public void testSobreescribirPreguntaInexistente() {
        PreguntaNumerica p = new PreguntaNumerica(TEST_ID_ENCUESTA, 9999, "No existe", 0, 10);
        ctrl.sobreescribe(p);
    }

    /**
     * Test: Eliminar pregunta
     */
    @Test
    public void testEliminarPregunta() {
        // Crear y guardar pregunta
        PreguntaNumerica p = new PreguntaNumerica(TEST_ID_ENCUESTA, 5006, "Para eliminar", 0, 10);
        ctrl.guardar(p);
        
        // Verificar que existe
        File archivo = new File(TEST_PREGUNTAS_DIR + "/5006.json");
        assertTrue("El archivo debe existir antes de eliminar", archivo.exists());
        
        // Eliminar
        ctrl.eliminarPregunta(5006);
        
        // Verificar que ya no existe
        assertFalse("El archivo no debe existir después de eliminar", archivo.exists());
    }

    /**
     * Test: Intentar eliminar pregunta inexistente debe lanzar excepción
     */
    @Test(expected = IllegalArgumentException.class)
    public void testEliminarPreguntaInexistente() {
        ctrl.eliminarPregunta(9999);
    }

    /**
     * Test: Intentar leer pregunta inexistente debe lanzar excepción
     */
    @Test(expected = IllegalArgumentException.class)
    public void testLeerPreguntaInexistente() {
        ctrl.getPregunta(9999);
    }

    /**
     * Test: Verificar que el JSON guardado tiene todos los campos necesarios
     */
    @Test
    public void testJsonTieneTodosLosCampos() throws IOException {
        PreguntaNumerica p = new PreguntaNumerica(TEST_ID_ENCUESTA, 5007, "Test campos", 0, 100);
        ctrl.guardar(p);
        
        // Leer el JSON directamente
        String jsonPath = TEST_PREGUNTAS_DIR + "/5007.json";
        JsonObject obj = JsonParser.parseReader(new FileReader(jsonPath)).getAsJsonObject();
        
        // Verificar campos obligatorios
        assertTrue("Debe tener idEncuesta", obj.has("idEncuesta"));
        assertTrue("Debe tener idPregunta", obj.has("idPregunta"));
        assertTrue("Debe tener tipo", obj.has("tipo"));
        assertTrue("Debe tener enunciado", obj.has("enunciado"));
        assertTrue("Debe tener idRespuestas", obj.has("idRespuestas"));
        
        // Verificar campos específicos de numérica
        assertTrue("Debe tener rangoInf", obj.has("rangoInf"));
        assertTrue("Debe tener rangoSup", obj.has("rangoSup"));
        
        // Verificar valores
        assertEquals("idEncuesta debe ser correcto", TEST_ID_ENCUESTA, obj.get("idEncuesta").getAsInt());
        assertEquals("idPregunta debe ser correcto", 5007, obj.get("idPregunta").getAsInt());
        assertEquals("tipo debe ser correcto", "numerica", obj.get("tipo").getAsString());
    }

    /**
     * Test: Gestión de IDs
     */
    @Test
    public void testGestionDeIds() {
        int idInicial = ctrl.getNextId();
        assertTrue("El ID inicial debe ser positivo", idInicial > 0);
        
        int nuevoId = idInicial + 10;
        ctrl.guardarNextId(nuevoId);
        
        int idGuardado = ctrl.getNextId();
        assertEquals("El ID guardado debe ser el correcto", nuevoId, idGuardado);
        
        // Restaurar ID original para no afectar otros tests
        ctrl.guardarNextId(idInicial);
    }

    /**
     * Test: Pregunta con respuestas asociadas
     */
    @Test
    public void testPreguntaConRespuestas() {
        PreguntaNumerica p = new PreguntaNumerica(TEST_ID_ENCUESTA, 5008, "Con respuestas", 0, 10);
        p.guardarIdRespuesta(1001);
        p.guardarIdRespuesta(1002);
        p.guardarIdRespuesta(1003);
        
        ctrl.guardar(p);
        
        Pregunta leida = ctrl.getPregunta(5008);
        assertEquals("Debe tener 3 respuestas", 3, leida.getIdRespuestas().size());
        assertTrue("Debe contener el ID 1001", leida.getIdRespuestas().contains(1001));
        assertTrue("Debe contener el ID 1002", leida.getIdRespuestas().contains(1002));
        assertTrue("Debe contener el ID 1003", leida.getIdRespuestas().contains(1003));
    }

    /**
     * Test: Pregunta de opción única ordenada
     */
    @Test
    public void testPreguntaOpcionUnicaOrdenada() {
        PreguntaOpcionUnica p = new PreguntaOpcionUnica(TEST_ID_ENCUESTA, 5009, "¿Nivel de satisfacción?", true);
        p.addOpcion("Muy insatisfecho");
        p.addOpcion("Insatisfecho");
        p.addOpcion("Neutral");
        p.addOpcion("Satisfecho");
        p.addOpcion("Muy satisfecho");
        
        ctrl.guardar(p);
        
        Pregunta leida = ctrl.getPregunta(5009);
        assertTrue("Debe ser ordenada", leida.esOrdenada());
        assertEquals("Debe tener 5 opciones", 5, leida.getOpciones().size());
    }

    /**
     * Test: Pregunta con enunciado especial (caracteres especiales)
     */
    @Test
    public void testPreguntaConCaracteresEspeciales() {
        String enunciado = "¿Cuál es tu opinión sobre \"esto\"? ¡Importante!";
        PreguntaLibre p = new PreguntaLibre(TEST_ID_ENCUESTA, 5010, enunciado);
        
        ctrl.guardar(p);
        
        Pregunta leida = ctrl.getPregunta(5010);
        assertEquals("El enunciado con caracteres especiales debe preservarse", enunciado, leida.getEnunciado());
    }

    /**
     * Test: Guardar múltiples preguntas y verificar que todas se guardan correctamente
     */
    @Test
    public void testGuardarMultiplesPreguntas() {
        // Guardar 5 preguntas diferentes
        for (int i = 0; i < 5; i++) {
            PreguntaNumerica p = new PreguntaNumerica(TEST_ID_ENCUESTA, 5100 + i, "Pregunta " + i, 0, 10);
            ctrl.guardar(p);
        }
        
        // Verificar que todas existen
        for (int i = 0; i < 5; i++) {
            Pregunta leida = ctrl.getPregunta(5100 + i);
            assertNotNull("La pregunta " + (5100 + i) + " debe existir", leida);
            assertEquals("El enunciado debe coincidir", "Pregunta " + i, leida.getEnunciado());
        }
    }
}
