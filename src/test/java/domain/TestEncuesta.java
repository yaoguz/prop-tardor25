package domain;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.ArrayList;

/**
 * Tests unitarios para la clase Encuesta - JUnit 4.
 * 
 * Las clases de dominio puras no necesitan mocks porque no tienen dependencias externas.
 * Solo prueban lógica interna.
 * 
 * @author Peiyu Wang
 */
public class TestEncuesta {
    
    private Encuesta encuesta;
    private final int ID_ENCUESTA = 1;
    private final String NOMBRE_CREADOR = "testUser";
    private final String TITULO_ENCUESTA = "Encuesta de Prueba";
    
    /**
     * Configuración inicial antes de cada test.
     */
    @Before
    public void setUp() {
        encuesta = new Encuesta(ID_ENCUESTA, TITULO_ENCUESTA, NOMBRE_CREADOR);
    }
    
    /**
     * Objeto de la prueba: Casos de uso de construcción e inicialización de una encuesta.
     * Ficheros de datos necesarios: Datos introducidos manualmente. No se requieren ficheros adicionales.
     * Valores estudiados: Estrategia caja gris. Se verifica que el constructor crea los atributos correctamente y las listas están vacías.
     * Operativa: Se crea una encuesta con ID, creador y título; se comprueba que los getters retornan los valores esperados y getIdPreguntas() está vacío.
     */
    @Test
    public void testConstructor_CreaEncuestaCorrectamente() {
        // Verificar ID asignado correctamente
        assertEquals(ID_ENCUESTA, encuesta.getIdEncuesta());
        
        // Verificar creador asignado correctamente
        assertEquals(NOMBRE_CREADOR, encuesta.getIdCreador());
        
        // Verificar titulo asignado correctamente
        assertEquals(TITULO_ENCUESTA, encuesta.getTitulo());
        
        // Verificar listas vacias
        assertNotNull(encuesta.getIdPreguntas());
        assertTrue(encuesta.getIdPreguntas().isEmpty());
    }
    

    /**
     * Objeto de la prueba: Casos de uso de guardamiento de múltiples preguntas en una encuesta.
     * Ficheros de datos necesarios: Datos introducidos manualmente. No se requieren ficheros adicionales.
     * Valores estudiados: Estrategia caja gris. Se prueban entradas válidas: guardamiento de 3 ids de preguntas.
     * Operativa: Se crea una encuesta; se guardan ids (10,20,30); se verifica que getIdPreguntas() retorna una lista de tamaño 3 con los elementos esperados.
     */
    @Test
    public void testGuardarPregunta_AgregaCorrectamente() {
        encuesta.guardarPregunta(10);
        encuesta.guardarPregunta(20);
        encuesta.guardarPregunta(30);

        ArrayList<Integer> lista = encuesta.getIdPreguntas();
        assertEquals(3, lista.size());
        assertTrue(lista.contains(Integer.valueOf(10)));
        assertTrue(lista.contains(Integer.valueOf(20)));
        assertTrue(lista.contains(Integer.valueOf(30)));
    }
     
    /**
     * Objeto de la prueba: Casos de uso de eliminación de preguntas existentes en una encuesta.
     * Ficheros de datos necesarios: Datos introducidos manualmente. No se requieren ficheros adicionales.
     * Valores estudiados: Estrategia caja gris. Se prueban entradas válidas: eliminación de un id existente de la lista.
     * Operativa: Se guardan ids (1,2,3); se elimina id 2; se verifica que la lista tiene tamaño 2 y ya no contiene el 2, pero sí contiene 1 y 3.
     */
    @Test
    public void testEliminarPregunta_EliminaCorrectamente() {
        encuesta.guardarPregunta(1);
        encuesta.guardarPregunta(2);
        encuesta.guardarPregunta(3);

        encuesta.eliminarPregunta(2);

        ArrayList<Integer> lista = encuesta.getIdPreguntas();
        assertEquals(2, lista.size());
        assertTrue(lista.contains(Integer.valueOf(1)));
        assertFalse(lista.contains(Integer.valueOf(2)));
        assertTrue(lista.contains(Integer.valueOf(3)));
    }
    
    /**
     * Objeto de la prueba: Casos de error: intentar eliminar una pregunta inexistente de la encuesta.
     * Ficheros de datos necesarios: Datos introducidos manualmente. No se requieren ficheros adicionales.
     * Valores estudiados: Estrategia caja negra. Se prueba con entrada inválida: eliminación de id inexistente (999).
     * Operativa: Se guarda id 5; se intenta eliminar id 999 (no existente); se verifica que la lista sigue con 1 elemento (5) sin error.
     */
    @Test
    public void testEliminarPregunta_SiNoExiste_NoFalla() {
        encuesta.guardarPregunta(5);
        encuesta.eliminarPregunta(999); // no existe
        ArrayList<Integer> lista = encuesta.getIdPreguntas();
        assertEquals(1, lista.size());
        assertTrue(lista.contains(Integer.valueOf(5)));
        assertFalse(lista.contains(Integer.valueOf(999)));
    }
    
    /**
     * Objeto de la prueba: Casos de uso de cambio del título de una encuesta.
     * Ficheros de datos necesarios: Datos introducidos manualmente. No se requieren ficheros adicionales.
     * Valores estudiados: Estrategia caja gris. Se prueba con una entrada válida: título nuevo diferente al original.
     * Operativa: Se crea una encuesta con título "Encuesta de Prueba"; se cambia a "Nuevo título"; se verifica que getTitulo() retorna "Nuevo título".
     */
    @Test
    public void testCambiarTitulo() {
        String nuevo = "Nuevo título";
        encuesta.cambiarTitulo(nuevo);
        assertEquals(nuevo, encuesta.getTitulo());
    }
    
    /**
     * Objeto de la prueba: Casos de uso complejos: añadir y eliminar múltiples preguntas en secuencia.
     * Ficheros de datos necesarios: Datos introducidos manualmente. No se requieren ficheros adicionales.
     * Valores estudiados: Estrategia caja gris. Se prueban múltiples operaciones válidas con conjuntos de datos homogéneo (ids 1-10).
     * Operativa: Se guardan ids 1-10 (10 preguntas); se eliminan los pares (2,4,6,8,10); se verifica que quedan 5 preguntas (impares 1,3,5,7,9).
     */
    @Test
    public void testAgregarYEliminarMultiplesPreguntas() {
        // Agregar varias preguntas
        for (int i = 1; i <= 10; i++) {
            encuesta.guardarPregunta(i);
        }
        
        assertEquals(10, encuesta.getIdPreguntas().size());
        
        // Eliminar preguntas pares
        for (int i = 2; i <= 10; i += 2) {
            encuesta.eliminarPregunta(i);
        }
        
        // Deberian quedar solo las impares
        ArrayList<Integer> preguntas = encuesta.getIdPreguntas();
        assertEquals(5, preguntas.size());
        assertTrue(preguntas.contains(1));
        assertTrue(preguntas.contains(3));
        assertTrue(preguntas.contains(5));
        assertTrue(preguntas.contains(7));
        assertTrue(preguntas.contains(9));
    }
    
    
}
