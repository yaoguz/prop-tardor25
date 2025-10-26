package examples;

import org.junit.Before;
import org.junit.After;
import org.junit.Test;
import org.junit.BeforeClass;
import org.junit.AfterClass;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import domain.Usuario;
import persistence.CtrlPersistence;
import java.util.ArrayList;

/**
 * EJEMPLO COMPLETO DE TEST CON JUNIT 4 Y MOCKITO
 * ================================================
 * 
 * Este archivo demuestra cómo hacer tests unitarios de la clase Usuario
 * usando JUnit 4 y Mockito para crear mocks (objetos simulados).
 * 
 * CONCEPTOS CLAVE:
 * ================
 * 
 * 1. @Test - Marca un método como caso de prueba
 *    - JUnit ejecutará automáticamente todos los métodos con @Test
 *    - Cada @Test debe probar UNA funcionalidad específica
 * 
 * 2. @Before - Se ejecuta ANTES de cada @Test
 *    - Usado para inicializar objetos comunes
 *    - Se ejecuta una vez por cada test (si tienes 5 tests, se ejecuta 5 veces)
 * 
 * 3. @After - Se ejecuta DESPUÉS de cada @Test
 *    - Usado para limpiar recursos
 *    - Ejemplo: cerrar conexiones, archivos, etc.
 * 
 * 4. @BeforeClass - Se ejecuta UNA SOLA VEZ antes de todos los tests
 *    - El método debe ser static
 *    - Útil para configuraciones costosas (cargar DB, etc.)
 * 
 * 5. @AfterClass - Se ejecuta UNA SOLA VEZ después de todos los tests
 *    - El método debe ser static
 * 
 * ASSERTS (Verificaciones):
 * =========================
 * - assertEquals(esperado, actual) - Verifica que dos valores sean iguales
 * - assertTrue(condicion) - Verifica que una condición sea verdadera
 * - assertFalse(condicion) - Verifica que una condición sea falsa
 * - assertNull(objeto) - Verifica que un objeto sea null
 * - assertNotNull(objeto) - Verifica que un objeto NO sea null
 * - assertSame(obj1, obj2) - Verifica que sean el MISMO objeto (misma referencia)
 * - assertNotSame(obj1, obj2) - Verifica que sean objetos diferentes
 * - assertArrayEquals(array1, array2) - Verifica que dos arrays sean iguales
 * - fail(mensaje) - Fuerza un fallo del test
 * 
 * MOCKS CON MOCKITO:
 * ==================
 * Un mock es un objeto "falso" que simula el comportamiento de un objeto real.
 * Útil cuando quieres probar una clase sin depender de otras clases.
 * 
 * - @Mock - Crea un objeto mock automáticamente
 * - when(...).thenReturn(...) - Define qué debe devolver el mock cuando se llama un método
 * - verify(...) - Verifica que se haya llamado un método del mock
 * 
 * ORDEN DE EJECUCIÓN:
 * ===================
 * @BeforeClass (una vez)
 *   ↓
 * @Before
 *   ↓
 * @Test (primer test)
 *   ↓
 * @After
 *   ↓
 * @Before
 *   ↓
 * @Test (segundo test)
 *   ↓
 * @After
 *   ↓
 * ... (se repite para cada test)
 *   ↓
 * @AfterClass (una vez)
 */
public class EjemploTestUsuarioConMocks {
    
    // ========== ATRIBUTOS DE LA CLASE DE TEST ==========
    
    /**
     * Usuario real que vamos a probar.
     * Se inicializa en @Before, así cada test tiene un usuario "fresco".
     */
    private Usuario usuario;
    
    /**
     * Mock de CtrlPersistence.
     * @Mock crea automáticamente un objeto falso de CtrlPersistence.
     * Este mock NO tiene comportamiento real, solo simula respuestas.
     */
    @Mock
    private CtrlPersistence mockPersistence;
    
    /**
     * Variable compartida entre todos los tests (ejemplo didáctico).
     */
    private static int contadorTests = 0;
    
    
    // ========== MÉTODOS DE INICIALIZACIÓN ==========
    
    /**
     * @BeforeClass - Se ejecuta UNA SOLA VEZ antes de todos los tests.
     * Debe ser static.
     * Útil para configuraciones costosas que se comparten entre tests.
     */
    @BeforeClass
    public static void setupClass() {
        System.out.println("=== INICIANDO BATERÍA DE TESTS ===");
        System.out.println("Configuración global realizada");
        contadorTests = 0;
    }
    
    /**
     * @Before - Se ejecuta ANTES de cada @Test.
     * Aquí inicializamos los objetos que necesitamos para los tests.
     * 
     * MockitoAnnotations.openMocks(this) inicializa todos los @Mock.
     */
    @Before
    public void setUp() {
        contadorTests++;
        System.out.println("\n--- Preparando test #" + contadorTests + " ---");
        
        // Inicializa los mocks (objetos anotados con @Mock)
        MockitoAnnotations.openMocks(this);
        
        // Creamos un usuario real para probar
        usuario = new Usuario("juanperez", "password123");
        
        System.out.println("Usuario creado: " + usuario.getNomUsuario());
    }
    
    /**
     * @After - Se ejecuta DESPUÉS de cada @Test.
     * Útil para limpiar recursos.
     */
    @After
    public void tearDown() {
        System.out.println("--- Test #" + contadorTests + " finalizado ---");
        // Aquí podrías cerrar conexiones, archivos, etc.
        usuario = null;
    }
    
    /**
     * @AfterClass - Se ejecuta UNA SOLA VEZ después de todos los tests.
     * Debe ser static.
     */
    @AfterClass
    public static void tearDownClass() {
        System.out.println("\n=== BATERÍA DE TESTS COMPLETADA ===");
        System.out.println("Total de tests ejecutados: " + contadorTests);
    }
    
    
    // ========== TESTS BÁSICOS (SIN MOCKS) ==========
    
    /**
     * TEST 1: Verificar que el constructor inicializa correctamente el nombre.
     * 
     * @Test marca este método como un caso de prueba.
     * JUnit lo ejecutará automáticamente.
     */
    @Test
    public void testConstructorInicializaNombre() {
        // ARRANGE (Preparar) - Ya tenemos el usuario creado en @Before
        
        // ACT (Actuar) - Obtenemos el nombre
        String nombreObtenido = usuario.getNomUsuario();
        
        // ASSERT (Verificar) - Comprobamos que sea el esperado
        assertEquals("El nombre debe ser 'juanperez'", "juanperez", nombreObtenido);
        
        System.out.println("✓ Test pasado: el nombre se inicializó correctamente");
    }
    
    /**
     * TEST 2: Verificar que el constructor inicializa las listas vacías.
     * 
     * assertNotNull verifica que el objeto NO sea null.
     * assertTrue verifica que una condición sea verdadera.
     */
    @Test
    public void testConstructorInicializaListasVacias() {
        // Verificamos que las listas existan (no sean null)
        assertNotNull("La lista de perfiles no debe ser null", usuario.getIdPerfiles());
        assertNotNull("La lista de encuestas no debe ser null", usuario.getIdEncuestasCreadas());
        assertNotNull("La lista de respuestas no debe ser null", usuario.getIdRespuestas());
        
        // Verificamos que las listas estén vacías
        assertTrue("La lista de perfiles debe estar vacía", usuario.getIdPerfiles().isEmpty());
        assertTrue("La lista de encuestas debe estar vacía", usuario.getIdEncuestasCreadas().isEmpty());
        assertTrue("La lista de respuestas debe estar vacía", usuario.getIdRespuestas().isEmpty());
        
        // También podemos verificar el tamaño
        assertEquals("La lista de perfiles debe tener tamaño 0", 0, usuario.getIdPerfiles().size());
        
        System.out.println("✓ Test pasado: todas las listas están vacías");
    }
    
    /**
     * TEST 3: Verificar que comprobarContrasena funciona correctamente.
     * 
     * assertTrue verifica que el resultado sea true.
     * assertFalse verifica que el resultado sea false.
     */
    @Test
    public void testComprobarContrasenaCorrecta() {
        // Probamos con la contraseña correcta
        boolean resultado = usuario.comprobarContrasena("password123");
        assertTrue("La contraseña correcta debe devolver true", resultado);
        
        System.out.println("✓ Test pasado: contraseña correcta verificada");
    }
    
    @Test
    public void testComprobarContrasenaIncorrecta() {
        // Probamos con una contraseña incorrecta
        boolean resultado = usuario.comprobarContrasena("wrongpassword");
        assertFalse("Una contraseña incorrecta debe devolver false", resultado);
        
        System.out.println("✓ Test pasado: contraseña incorrecta detectada");
    }
    
    /**
     * TEST 4: Verificar que se puede cambiar la contraseña.
     */
    @Test
    public void testCambiarContrasena() {
        // Cambiamos la contraseña
        usuario.newContrasena("nuevaPassword456");
        
        // Verificamos que la antigua ya no funciona
        assertFalse("La contraseña antigua no debe funcionar", 
                    usuario.comprobarContrasena("password123"));
        
        // Verificamos que la nueva sí funciona
        assertTrue("La nueva contraseña debe funcionar", 
                   usuario.comprobarContrasena("nuevaPassword456"));
        
        System.out.println("✓ Test pasado: contraseña cambiada correctamente");
    }
    
    /**
     * TEST 5: Verificar que se pueden guardar respuestas.
     */
    @Test
    public void testGuardarRespuestas() {
        // Guardamos algunas respuestas
        usuario.guardarIdRespuesta(1);
        usuario.guardarIdRespuesta(2);
        usuario.guardarIdRespuesta(3);
        
        // Verificamos que se guardaron
        ArrayList<Integer> respuestas = usuario.getIdRespuestas();
        assertEquals("Debe haber 3 respuestas", 3, respuestas.size());
        
        // Verificamos que contengan los IDs correctos
        assertTrue("Debe contener el ID 1", respuestas.contains(1));
        assertTrue("Debe contener el ID 2", respuestas.contains(2));
        assertTrue("Debe contener el ID 3", respuestas.contains(3));
        
        System.out.println("✓ Test pasado: respuestas guardadas correctamente");
    }
    
    /**
     * TEST 6: Verificar que se pueden guardar encuestas.
     */
    @Test
    public void testGuardarEncuestas() {
        // Guardamos algunas encuestas
        usuario.guardarIdEncuesta(10);
        usuario.guardarIdEncuesta(20);
        
        // Verificamos
        ArrayList<Integer> encuestas = usuario.getIdEncuestasCreadas();
        assertEquals("Debe haber 2 encuestas", 2, encuestas.size());
        assertEquals("La primera encuesta debe ser 10", Integer.valueOf(10), encuestas.get(0));
        assertEquals("La segunda encuesta debe ser 20", Integer.valueOf(20), encuestas.get(1));
        
        System.out.println("✓ Test pasado: encuestas guardadas correctamente");
    }
    
    
    // ========== TESTS CON MOCKS DE MOCKITO ==========
    
    /**
     * TEST 7: Ejemplo de uso de Mock - Simular que CtrlPersistence guarda un usuario.
     * 
     * IMPORTANTE: Este es un ejemplo didáctico para mostrar cómo usar mocks.
     * En un escenario real, probarías una clase que DEPENDE de CtrlPersistence,
     * no la clase Usuario directamente (que no tiene dependencias).
     * 
     * CONCEPTOS DE MOCKITO:
     * - when(...).thenReturn(...) - Define el comportamiento del mock
     * - verify(...) - Verifica que se llamó un método del mock
     */
    @Test
    public void testEjemploConMock_GuardarUsuario() {
        // ARRANGE: Configuramos el mock para que no lance excepción al guardar
        // doNothing() indica que el método no debe hacer nada (comportamiento por defecto)
        doNothing().when(mockPersistence).guardarUsuario(any(Usuario.class));
        
        // ACT: Simulamos guardar el usuario (en un caso real, esto sería parte de otra clase)
        mockPersistence.guardarUsuario(usuario);
        
        // ASSERT: Verificamos que se llamó al método guardarUsuario con nuestro usuario
        verify(mockPersistence).guardarUsuario(usuario);
        // También podemos verificar que se llamó exactamente 1 vez:
        verify(mockPersistence, times(1)).guardarUsuario(usuario);
        
        System.out.println("✓ Test pasado: mock verificado correctamente");
    }
    
    /**
     * TEST 8: Ejemplo de Mock que devuelve una lista de usuarios.
     * 
     * when(...).thenReturn(...) hace que el mock devuelva un valor específico.
     */
    @Test
    public void testEjemploConMock_ObtenerUsuarios() {
        // ARRANGE: Preparamos una lista falsa de usuarios
        ArrayList<Usuario> usuariosFalsos = new ArrayList<>();
        usuariosFalsos.add(new Usuario("usuario1", "pass1"));
        usuariosFalsos.add(new Usuario("usuario2", "pass2"));
        usuariosFalsos.add(usuario); // Añadimos nuestro usuario de prueba
        
        // Configuramos el mock para que devuelva esta lista
        when(mockPersistence.getUsuarios()).thenReturn(usuariosFalsos);
        
        // ACT: Llamamos al método del mock
        ArrayList<Usuario> resultado = mockPersistence.getUsuarios();
        
        // ASSERT: Verificamos que devuelve lo que esperamos
        assertNotNull("La lista no debe ser null", resultado);
        assertEquals("Debe haber 3 usuarios", 3, resultado.size());
        assertTrue("Debe contener nuestro usuario", resultado.contains(usuario));
        
        // Verificamos que se llamó al método
        verify(mockPersistence).getUsuarios();
        
        System.out.println("✓ Test pasado: mock devolvió la lista correctamente");
    }
    
    /**
     * TEST 9: Ejemplo de verificación con argumentos específicos.
     * 
     * eq(...) verifica que el argumento sea exactamente ese valor.
     * anyInt() acepta cualquier entero.
     */
    @Test
    public void testEjemploConMock_VerificarArgumentos() {
        // Guardamos algunas respuestas
        usuario.guardarIdRespuesta(100);
        usuario.guardarIdRespuesta(200);
        
        // Verificamos que se guardaron los IDs correctos
        ArrayList<Integer> respuestas = usuario.getIdRespuestas();
        assertEquals("Debe haber 2 respuestas", 2, respuestas.size());
        assertTrue("Debe contener 100", respuestas.contains(100));
        assertTrue("Debe contener 200", respuestas.contains(200));
        
        System.out.println("✓ Test pasado: argumentos verificados");
    }
    
    
    // ========== TESTS DE CASOS BORDE Y EXCEPCIONES ==========
    
    /**
     * TEST 10: Probar con cadenas vacías.
     */
    @Test
    public void testUsuarioConCadenasVacias() {
        Usuario usuarioVacio = new Usuario("", "");
        
        assertEquals("El nombre debe ser cadena vacía", "", usuarioVacio.getNomUsuario());
        assertTrue("Contraseña vacía debe validarse", usuarioVacio.comprobarContrasena(""));
        
        System.out.println("✓ Test pasado: cadenas vacías manejadas");
    }
    
    /**
     * TEST 11: Ejemplo de test que espera una excepción (si la hubiera).
     * 
     * @Test(expected = IllegalArgumentException.class) indica que el test
     * DEBE lanzar esa excepción para pasar.
     * 
     * Nota: Como Usuario.newContrasena no lanza excepción, este test fallaría.
     * Lo dejo comentado como ejemplo.
     */
    /*
    @Test(expected = IllegalArgumentException.class)
    public void testCambiarContrasenaLanzaExcepcion() {
        // Si newContrasena lanzara excepción al poner la misma contraseña:
        usuario.newContrasena("password123");
        // El test pasaría si se lanza IllegalArgumentException
    }
    */
    
    /**
     * TEST 12: Verificar comportamiento con muchos elementos.
     */
    @Test
    public void testGuardarMuchasRespuestas() {
        // Guardamos 100 respuestas
        for (int i = 0; i < 100; i++) {
            usuario.guardarIdRespuesta(i);
        }
        
        assertEquals("Debe haber 100 respuestas", 100, usuario.getIdRespuestas().size());
        
        System.out.println("✓ Test pasado: 100 respuestas guardadas");
    }
    
    
    // ========== EJEMPLO DE TEST QUE FALLA INTENCIONALMENTE ==========
    
    /**
     * TEST 13: Ejemplo de test que falla (comentado para que no rompa la suite).
     * 
     * Descomentar para ver cómo falla un test.
     */
    /*
    @Test
    public void testEjemploFallido() {
        // Este test fallará porque 1 no es igual a 2
        assertEquals("Esto fallará", 1, 2);
    }
    */
    
    /**
     * TEST 14: Forzar un fallo con fail().
     * 
     * fail() hace que el test falle inmediatamente.
     * Útil para marcar código que no debería ejecutarse.
     */
    /*
    @Test
    public void testEjemploConFail() {
        boolean condicion = false;
        
        if (condicion) {
            // Si llegamos aquí, algo salió mal
            fail("No debería llegar a este punto");
        }
        
        // Test pasa si no se llama a fail()
    }
    */
    
    
    // ========== RESUMEN DE BUENAS PRÁCTICAS ==========
    
    /**
     * BUENAS PRÁCTICAS DE TESTING:
     * =============================
     * 
     * 1. Nombres descriptivos: testNombreMetodo_CasoEspecifico_ResultadoEsperado
     *    Ejemplo: testComprobarContrasena_ContrasenaCorrecta_DevuelveTrue
     * 
     * 2. Patrón AAA (Arrange-Act-Assert):
     *    - Arrange: Preparar los datos y objetos necesarios
     *    - Act: Ejecutar la acción que queremos probar
     *    - Assert: Verificar que el resultado es el esperado
     * 
     * 3. Un test debe probar UNA SOLA cosa:
     *    - No mezcles múltiples funcionalidades en un test
     *    - Si falla, debe ser obvio qué salió mal
     * 
     * 4. Tests independientes:
     *    - Cada test debe poder ejecutarse solo
     *    - No dependas del orden de ejecución
     *    - Usa @Before para inicializar estado limpio
     * 
     * 5. Usa mocks para aislar dependencias:
     *    - Si tu clase depende de otras, mockea las dependencias
     *    - Así pruebas solo TU código, no el de otros
     * 
     * 6. Nombres de variables claros:
     *    - valorEsperado, valorObtenido, etc.
     * 
     * 7. Mensajes descriptivos en asserts:
     *    - assertEquals("El usuario debe tener 3 respuestas", 3, actual);
     *    - Ayuda a entender qué falló
     * 
     * 8. Probar casos normales Y casos borde:
     *    - Valores normales
     *    - Valores límite (0, -1, máximo, etc.)
     *    - Valores null
     *    - Listas vacías
     *    - Excepciones esperadas
     * 
     * PARA EJECUTAR LOS TESTS:
     * ========================
     * 
     * En terminal (PowerShell):
     *   .\gradlew test
     * 
     * Para ejecutar solo esta clase:
     *   .\gradlew test --tests examples.EjemploTestUsuarioConMocks
     * 
     * Para ejecutar un test específico:
     *   .\gradlew test --tests examples.EjemploTestUsuarioConMocks.testConstructorInicializaNombre
     * 
     * Para ver más detalles:
     *   .\gradlew test --info
     * 
     * Los resultados están en:
     *   build/reports/tests/test/index.html (ábrelo en un navegador)
     */
}
