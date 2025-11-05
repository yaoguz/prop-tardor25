package domain;

import main.domini.*;
import java.util.*;
import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests parametrizados para la clase Usuario usando JUnit 4.
 * 
 * Demuestra cómo usar @RunWith(Parameterized.class) para ejecutar
 * el mismo test con diferentes parámetros.
 * 
 * @author Equipo PROP
 * @version 1.0 - JUnit 4
 */
@RunWith(Parameterized.class)
public class TestUsuarioParametrizado {
    
    // Parámetros del test
    private String nombreUsuario;
    private String contrasena;
    private boolean deberiaSerValido;
    
    /**
     * Constructor que recibe los parámetros para cada ejecución del test.
     * 
     * @param nombreUsuario Nombre de usuario a probar
     * @param contrasena Contraseña a probar
     * @param deberiaSerValido Si debería crear usuario válido o lanzar excepción
     */
    public TestUsuarioParametrizado(String nombreUsuario, String contrasena, boolean deberiaSerValido) {
        this.nombreUsuario = nombreUsuario;
        this.contrasena = contrasena;
        this.deberiaSerValido = deberiaSerValido;
    }
    
    /**
     * Define los parámetros para cada ejecución del test.
     * Cada array interno representa un conjunto de parámetros.
     * 
     * @return Collection de arrays con parámetros de prueba
     */
    @Parameters(name = "Usuario({0}, {1}) debería ser válido: {2}")
    public static Collection<Object[]> parametrosDePrueba() {
        return Arrays.asList(new Object[][] {
            // {nombreUsuario, contrasena, deberiaSerValido}
            {"usuario1", "pass123", true},          // Caso válido normal
            {"admin", "admin123", true},            // Caso válido admin
            {"testUser", "p4ssw0rd!", true},        // Caso válido con símbolos
            {"u", "p", true},                       // Caso válido mínimo (1 carácter)
            {"", "pass123", false},                 // Caso inválido: nombre vacío
            {"usuario", "", false},                 // Caso inválido: contraseña vacía
            {null, "pass123", false},               // Caso inválido: nombre null
            {"usuario", null, false},               // Caso inválido: contraseña null
            {"  ", "pass123", false},               // Caso inválido: nombre solo espacios
            {"usuario", "  ", false}                // Caso inválido: contraseña solo espacios
        });
    }
    
    /**
     * Test parametrizado: crea usuario con diferentes combinaciones de parámetros.
     */
    @Test
    public void testCrearUsuario_ConDiferentesParametros() {
        if (deberiaSerValido) {
            // Caso válido: debería crear usuario sin problemas
            try {
                Usuario usuario = new Usuario(nombreUsuario, contrasena);
                assertNotNull("El usuario no debería ser null", usuario);
                assertEquals("El nombre debería coincidir", nombreUsuario, usuario.getNomUsuario());
                assertTrue("La contraseña debería ser válida", usuario.comprobarContrasena(contrasena));
            } catch (Exception e) {
                fail("No debería lanzar excepción con parámetros válidos: " + e.getMessage());
            }
        } else {
            // Caso inválido: debería lanzar excepción
            try {
                Usuario usuario = new Usuario(nombreUsuario, contrasena);
                fail("Debería haber lanzado IllegalArgumentException con parámetros inválidos");
            } catch (IllegalArgumentException e) {
                // Esperado - test pasa
                assertNotNull("La excepción debería tener mensaje", e.getMessage());
            } catch (NullPointerException e) {
                // También válido para parámetros null
                assertNotNull("La excepción debería existir", e);
            }
        }
    }
}
