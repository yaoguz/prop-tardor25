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
 * Pruebas del algoritmo K-means.
 * @author Alessandro Jin
 */
public class TestKmeans {
    
    private Kmeans kmeans;
    private List<Pregunta> variables;
    private List<List<Respuesta>> respuestas;
    
    /**
     * Inicializa dataset de prueba con 6 usuarios.
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
     * Verifica que el nombre del algoritmo es "Kmeans".
     */
    @Test
    public void testGetNombre_RetornaNombreCorrecto() {
        assertEquals("Deberia ser Kmeans", "Kmeans", kmeans.getNombre());
    }
    
    /**
     * Genera 2 clusters correctamente.
     */
    @Test
    public void testEjecutarClustering_K2_GeneraDosPerfiles() {
        ResultadoClustering resultado = kmeans.ejecutarClustering(variables, respuestas, 2);
        
        assertNotNull("Esta nulo", resultado);
        assertEquals("No genera cantidad perfil bien", 2, resultado.getPerfiles().size());
        
        // Verificar que cada perfil tiene 3 miembros por grupo
        for (Perfil p : resultado.getPerfiles()) {
            assertTrue("Deberia ser grupos de 3 perfectos",p.getMiembros().size() == 3);
            assertNotNull("Debe de haber representante", p.getRepresentante());
        }
        
        // Calidad debe estar entre -1 y 1
        assertTrue("Silueta esta mal", resultado.getCalidad() >= -1.0 && resultado.getCalidad() <= 1.0);
    }
    
    /**
     * Todos los usuarios son asignados a un cluster.
     */
    @Test
    public void testEjecutarClustering_TodosUsuariosAsignados() {
        ResultadoClustering resultado = kmeans.ejecutarClustering(variables, respuestas, 2);
        
        int totalMiembros = 0;
        for (Perfil p : resultado.getPerfiles()) {
            totalMiembros += p.getMiembros().size();
        }
        
        assertEquals("No ha asignado a donde mundo", respuestas.size(), totalMiembros);
    }
    
    
    /**
     * El centroide está en la lista de miembros.
     */
    @Test
    public void testEjecutarClustering_RepresentanteEnMiembros() {
        ResultadoClustering resultado = kmeans.ejecutarClustering(variables, respuestas, 2);
        
        for (Perfil p : resultado.getPerfiles()) {
            if (p.getMiembros().size() > 0) {
                assertTrue("Representante no esta en miembro", p.getMiembros().contains(p.getRepresentante()));
            }
        }
    }
    
    /**
     * Múltiples ejecuciones generan el mismo número de clusters.
     */
    @Test
    public void testEjecutarClustering_ConsistenciaMultiplesEjecuciones() {
        ResultadoClustering r1 = kmeans.ejecutarClustering(variables, respuestas, 4);
        ResultadoClustering r2 = kmeans.ejecutarClustering(variables, respuestas, 5);
        ResultadoClustering r3 = kmeans.ejecutarClustering(variables, respuestas, 6);
   
        assertEquals("No genera cantidad perfil bien",4, r1.getPerfiles().size());
        assertEquals("No genera cantidad perfil bien",5, r2.getPerfiles().size());
        assertEquals("No genera cantidad perfil bien",6, r3.getPerfiles().size());
    }

    /**
     * Ningún usuario aparece en múltiples clusters.
     */
    @Test
    public void testEjecutarClustering_UsuariosSinDuplicar() {
        ResultadoClustering resultado = kmeans.ejecutarClustering(variables, respuestas, 2);
        
        List<String> todosUsuarios = new ArrayList<>();
        for (Perfil p : resultado.getPerfiles()) {
            todosUsuarios.addAll(p.getMiembros());
        }
        
        // Verificar que no hay duplicados
        for (String usuario : todosUsuarios) {
            int count = 0;
            for (String u : todosUsuarios) {
                if (u.equals(usuario)) count++;
            }
            assertEquals("Duplicados",1, count);
        }
    }
}
