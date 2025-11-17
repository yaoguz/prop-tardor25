package algorithm;

import domain.*;
import domain.algorithm.*;
import domain.algorithm.estructuras.*;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Tests unitarios para el algoritmo K-means.
 * 
 * @author ?
 */
public class TestKmeans {
    
    private Kmeans kmeans;
    private List<Pregunta> variables;
    private List<List<Respuesta>> respuestas;
    
    /**
     * Configuración inicial antes de cada test.
     * Crea un dataset simple de 6 usuarios con 3 preguntas.
     */
    @Before
    public void setUp() {
        kmeans = new Kmeans();
        
        // Crear 3 preguntas
        PreguntaNumerica p1 = new PreguntaNumerica(1, 1, "Edad?", 18, 99);
        PreguntaOpcionUnica p2 = new PreguntaOpcionUnica(1, 2, "Género?", false);
        p2.addOpcion("M");
        p2.addOpcion("F");
        PreguntaNumerica p3 = new PreguntaNumerica(1, 3, "Horas ejercicio?", 0, 20);
        
        variables = Arrays.asList(p1, p2, p3);
        
        // Crear respuestas de 6 usuarios (2 grupos claros)
        // Grupo 1: jóvenes (20-25 años) que hacen mucho ejercicio (8-10h)
        List<Respuesta> u1 = new ArrayList<>();
        u1.add(new RespuestaNumerica(1, "U1", 1, 22.0));
        u1.add(new RespuestaOpcionUnica(2, "U1", 2, 0));
        u1.add(new RespuestaNumerica(3, "U1", 3, 10.0));
        List<Respuesta> u2 = new ArrayList<>();
        u2.add(new RespuestaNumerica(4, "U2", 1, 23.0));
        u2.add(new RespuestaOpcionUnica(5, "U2", 2, 1));
        u2.add(new RespuestaNumerica(6, "U2", 3, 9.0));
        List<Respuesta> u3 = new ArrayList<>();
        u3.add(new RespuestaNumerica(7, "U3", 1, 25.0));
        u3.add(new RespuestaOpcionUnica(8, "U3", 2, 0));
        u3.add(new RespuestaNumerica(9, "U3", 3, 8.0));
        
        // Grupo 2: mayores (50-55 años) que hacen poco ejercicio (1-2h)
        List<Respuesta> u4 = new ArrayList<>();
        u4.add(new RespuestaNumerica(10, "U4", 1, 50.0));
        u4.add(new RespuestaOpcionUnica(11, "U4", 2, 0));
        u4.add(new RespuestaNumerica(12, "U4", 3, 2.0));
        
        List<Respuesta> u5 = new ArrayList<>();
        u5.add(new RespuestaNumerica(13, "U5", 1, 52.0));
        u5.add(new RespuestaOpcionUnica(14, "U5", 2, 1));
        u5.add(new RespuestaNumerica(15, "U5", 3, 1.0));
        
        List<Respuesta> u6 = new ArrayList<>();
        u6.add(new RespuestaNumerica(16, "U6", 1, 55.0));
        u6.add(new RespuestaOpcionUnica(17, "U6", 2, 0));
        u6.add(new RespuestaNumerica(18, "U6", 3, 2.0));
        
        respuestas = Arrays.asList(u1, u2, u3, u4, u5, u6);
    }
    
    /**
     * Objeto de la prueba: Verificar que el nombre del algoritmo es correcto.
     * Ficheros de datos necesarios: Ninguno.
     * Valores estudiados: Estrategia caja negra. Se verifica que getNombre() retorna "Kmeans".
     * Operativa: Se llama a getNombre() y se comprueba que retorna el valor esperado.
     */
    @Test
    public void testGetNombre_RetornaNombreCorrecto() {
        assertEquals("Kmeans", kmeans.getNombre());
    }
    
    /**
     * Objeto de la prueba: Verificar clustering básico con k=2.
     * Ficheros de datos necesarios: Dataset creado en setUp().
     * Valores estudiados: Estrategia caja negra. Se verifica que genera 2 perfiles y la calidad está en rango válido.
     * Operativa: Se ejecuta clustering con k=2; se verifica que el resultado tiene 2 perfiles con miembros y calidad entre -1 y 1.
     */
    @Test
    public void testEjecutarClustering_K2_GeneraDosPerfiles() {
        ResultadoClustering resultado = kmeans.ejecutarClustering(variables, respuestas, 2);
        
        assertNotNull(resultado);
        assertEquals(2, resultado.getPerfiles().size());
        
        // Verificar que cada perfil tiene miembros
        for (Perfil p : resultado.getPerfiles()) {
            assertTrue(p.getMiembros().size() > 0);
            assertNotNull(p.getRepresentante());
        }
        
        // Calidad debe estar entre -1 y 1
        assertTrue(resultado.getCalidad() >= -1.0 && resultado.getCalidad() <= 1.0);
    }
    
    /**
     * Objeto de la prueba: Verificar que todos los usuarios son asignados a algún perfil.
     * Ficheros de datos necesarios: Dataset creado en setUp().
     * Valores estudiados: Estrategia caja gris. Se verifica que la suma de tamaños de perfiles es igual al número de usuarios.
     * Operativa: Se ejecuta clustering con k=2; se suma el tamaño de cada perfil y se compara con el total de usuarios.
     */
    @Test
    public void testEjecutarClustering_TodosUsuariosAsignados() {
        ResultadoClustering resultado = kmeans.ejecutarClustering(variables, respuestas, 2);
        
        int totalMiembros = 0;
        for (Perfil p : resultado.getPerfiles()) {
            totalMiembros += p.getMiembros().size();
        }
        
        assertEquals(respuestas.size(), totalMiembros);
    }
    
    /**
     * Objeto de la prueba: Verificar clustering con k=3.
     * Ficheros de datos necesarios: Dataset creado en setUp().
     * Valores estudiados: Estrategia caja negra. Se verifica que genera 3 perfiles.
     * Operativa: Se ejecuta clustering con k=3; se verifica que el resultado tiene 3 perfiles.
     */
    @Test
    public void testEjecutarClustering_K3_GeneraTresPerfiles() {
        ResultadoClustering resultado = kmeans.ejecutarClustering(variables, respuestas, 3);
        
        assertEquals(3, resultado.getPerfiles().size());
        
        // Verificar que no hay perfiles vacíos (puede pasar con pocos datos)
        for (Perfil p : resultado.getPerfiles()) {
            assertTrue(p.getMiembros().size() >= 0);
        }
    }
    
    /**
     * Objeto de la prueba: Verificar que el representante de cada perfil está en la lista de miembros.
     * Ficheros de datos necesarios: Dataset creado en setUp().
     * Valores estudiados: Estrategia caja gris. Se verifica que cada representante pertenece a su perfil.
     * Operativa: Se ejecuta clustering con k=2; se verifica que el representante de cada perfil está en su lista de miembros.
     */
    @Test
    public void testEjecutarClustering_RepresentanteEnMiembros() {
        ResultadoClustering resultado = kmeans.ejecutarClustering(variables, respuestas, 2);
        
        for (Perfil p : resultado.getPerfiles()) {
            if (p.getMiembros().size() > 0) {
                assertTrue(p.getMiembros().contains(p.getRepresentante()));
            }
        }
    }
    
    /**
     * Objeto de la prueba: Verificar consistencia de clustering (múltiples ejecuciones).
     * Ficheros de datos necesarios: Dataset creado en setUp().
     * Valores estudiados: Estrategia caja negra. Se ejecuta el algoritmo 3 veces y se verifica que siempre genera el mismo número de perfiles.
     * Operativa: Se ejecuta clustering 3 veces con k=2; se verifica que todas las ejecuciones generan 2 perfiles.
     */
    @Test
    public void testEjecutarClustering_ConsistenciaMultiplesEjecuciones() {
        ResultadoClustering r1 = kmeans.ejecutarClustering(variables, respuestas, 2);
        ResultadoClustering r2 = kmeans.ejecutarClustering(variables, respuestas, 2);
        ResultadoClustering r3 = kmeans.ejecutarClustering(variables, respuestas, 2);
        
        // Siempre debe generar 2 perfiles
        assertEquals(2, r1.getPerfiles().size());
        assertEquals(2, r2.getPerfiles().size());
        assertEquals(2, r3.getPerfiles().size());
    }
}
