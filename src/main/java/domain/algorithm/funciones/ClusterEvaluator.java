package domain.algorithm.funciones;

import java.util.List;
import domain.Respuesta;
import domain.Pregunta;
import domain.algorithm.*;

/**
 * Evaluador de calidad de clustering.
 * Calcula métricas para determinar la calidad de los clusters obtenidos.
 * 
 * @author Alessandro Jin
 */
public class ClusterEvaluator {

    /**
     * Calcula el coeficiente de silueta global para evaluar la calidad del clustering.
     * 
     * El coeficiente de silueta mide qué tan similar es un objeto a su propio cluster
     * comparado con otros clusters. El valor está en el rango [-1, 1]:
     * - Valores cercanos a 1: Buen clustering (objetos bien agrupados)
     * - Valores cercanos a 0: Clusters solapados
     * - Valores negativos: Objetos probablemente en el cluster equivocado
     * 
     * @param perfiles Lista de perfiles obtenidos del clustering
     * @param respuestas Todas las respuestas de usuarios (matriz)
     * @param variables Preguntas de la encuesta
     * @return Coeficiente de silueta global (promedio de todos los usuarios)
     */
    public static double calcularCoeficienteSilueta(List<Cluster> clusters, 
                                                    List<List<Respuesta>> respuestas, 
                                                    List<Pregunta> variables) {
        
        if (clusters.size() == 1) {
            // Si solo hay un cluster, la silueta no está definida
            return 0.0;
        }

        double sumaSiluetaTotal = 0.0;

        // Para cada usuario del cluster
        for (int ) {
            ArrayList<Integer> miembros = cluster.getMiembros();

            List<List<Respuesta>> cluster = sacarMiembros(miembros, respuestas);

            // Para cada miembro del cluster
            for (List<Respuesta> persona : cluster) {
                double siluetaDelCluster = calcularSiluetaCluster(persona, cluster);

                double siluetaTodos = calcularSiluetaConTodos(persona, respuestas);
            }
        }

        int totalUsuarios = respuestas.size();
        return totalUsuarios > 0 ? sumaSiluetaTotal / totalUsuarios : 0.0;
    }

    /**
     * Encuentra las respuestas de un usuario específico en la matriz de respuestas.
     */
    private static List<Respuesta> encontrarRespuestasUsuario(String nombreUsuario, List<List<Respuesta>> respuestas) {
        for (List<Respuesta> respuestasUsuario : respuestas) {
            if (!respuestasUsuario.isEmpty() && 
                respuestasUsuario.get(0).getNomUsuario().equals(nombreUsuario)) {
                return respuestasUsuario;
            }
        }
        return null;
    }

    /**
     * Calcula la distancia promedio de un usuario a todos los demás miembros de su cluster.
     */
    private static double calcularDistanciaPromedioIntraCluster(List<Respuesta> respuestasUsuario,
                                                                 Cluster perfil,
                                                                 List<List<Respuesta>> respuestas,
                                                                 List<Pregunta> variables,
                                                                 String nombreUsuario) {
        List<String> miembros = perfil.miembros();
        
        if (miembros.size() <= 1) {
            return 0.0;
        }

        double sumaDistancias = 0.0;
        int contador = 0;

        for (String otroMiembro : miembros) {
            if (otroMiembro.equals(nombreUsuario)) continue;

            List<Respuesta> respuestasOtro = encontrarRespuestasUsuario(otroMiembro, respuestas);
            if (respuestasOtro != null) {
                double distancia = CalculoDistanciaGlobal.calcularDistanciaGlobal(
                    respuestasUsuario, respuestasOtro, variables
                );
                sumaDistancias += distancia;
                contador++;
            }
        }

        return contador > 0 ? sumaDistancias / contador : 0.0;
    }

    /**
     * Calcula la distancia promedio mínima a los clusters vecinos (el más cercano).
     */
    private static double calcularDistanciaMinInterCluster(List<Respuesta> respuestasUsuario,
                                                           List<Cluster> perfiles,
                                                           Cluster perfilActual,
                                                           List<List<Respuesta>> respuestas,
                                                           List<Pregunta> variables) {
        double minDistanciaPromedio = Double.MAX_VALUE;

        for (Cluster otroCluster : perfiles) {
            if (otroCluster == perfilActual) continue;

            List<String> otrosMiembros = otroCluster.miembros();
            if (otrosMiembros.isEmpty()) continue;

            double sumaDistancias = 0.0;
            int contador = 0;

            for (String otroMiembro : otrosMiembros) {
                List<Respuesta> respuestasOtro = encontrarRespuestasUsuario(otroMiembro, respuestas);
                if (respuestasOtro != null) {
                    double distancia = CalculoDistanciaGlobal.calcularDistanciaGlobal(
                        respuestasUsuario, respuestasOtro, variables
                    );
                    sumaDistancias += distancia;
                    contador++;
                }
            }

            if (contador > 0) {
                double distanciaPromedio = sumaDistancias / contador;
                minDistanciaPromedio = Math.min(minDistanciaPromedio, distanciaPromedio);
            }
        }

        return minDistanciaPromedio == Double.MAX_VALUE ? 0.0 : minDistanciaPromedio;
    }

    /**
     * Interpreta el valor del coeficiente de silueta.
     * 
     * @param silueta Coeficiente de silueta calculado
     * @return Descripción textual de la calidad del clustering
     */
    public static String interpretarSilueta(double silueta) {
        if (silueta >= 0.7) {
            return "EXCELENTE: Estructura de clusters muy fuerte";
        } else if (silueta >= 0.5) {
            return "BUENO: Estructura de clusters razonable";
        } else if (silueta >= 0.25) {
            return "ACEPTABLE: Estructura de clusters débil";
        } else if (silueta >= 0) {
            return "POBRE: Estructura de clusters muy débil o artificial";
        } else {
            return "MALO: Muchos objetos asignados a clusters incorrectos";
        }
    }
}
