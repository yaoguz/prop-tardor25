package domain.clustering_ejemplo;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import domain.*;

/**
 * calcula distancias locales según tipo
 */
public class DistanceCalculator {
    
    /**
     * Calcula distancia global entre dos usuarios (Manhattan normalizada).
     * 
     * @param respuestasUsuario1 Respuestas del usuario 1
     * @param respuestasUsuario2 Respuestas del usuario 2
     * @return Distancia en [0,1]
     */
    public static double calcularDistanciaGlobal(List<Respuesta> respuestasUsuario1, 
                                                   List<Respuesta> respuestasUsuario2) {
        int n = Math.min(respuestasUsuario1.size(), respuestasUsuario2.size());
        double sumaDistancias = 0.0;
        
        for (int i = 0; i < n; i++) {
            sumaDistancias += calcularDistanciaLocal(respuestasUsuario1.get(i), 
                                                       respuestasUsuario2.get(i));
        }
        
        return sumaDistancias / n;  // Manhattan normalizada
    }
    
    /**
     * Calcula distancia local según tipo de respuesta.
     */
    private static double calcularDistanciaLocal(Respuesta r1, Respuesta r2) {
        if (r1 instanceof RespuestaNumerica && r2 instanceof RespuestaNumerica) {
            return distanciaNumerica((RespuestaNumerica)r1, (RespuestaNumerica)r2);
        } 
        else if (r1 instanceof RespuestaOpcionUnica && r2 instanceof RespuestaOpcionUnica) {
            return distanciaOpcionUnica((RespuestaOpcionUnica)r1, (RespuestaOpcionUnica)r2);
        }
        else if (r1 instanceof RespuestaOpcionMultiple && r2 instanceof RespuestaOpcionMultiple) {
            return distanciaOpcionMultiple((RespuestaOpcionMultiple)r1, (RespuestaOpcionMultiple)r2);
        }
        else if (r1 instanceof RespuestaLibre && r2 instanceof RespuestaLibre) {
            return distanciaLibre((RespuestaLibre)r1, (RespuestaLibre)r2);
        }
        
        return 1.0; // Tipos incompatibles = máxima distancia
    }
    
    /**
     * Distancia para variables numéricas (fórmula 1 del PDF).
     * 
     * TODO: Implementar normalización con Vmax y Vmin de la pregunta.
     * Opciones:
     * 1. Pasar PreguntaNumerica como parámetro para obtener min/max
     * 2. Calcular min/max globalmente antes de clustering
     * 3. Normalizar asumiendo rango conocido
     * 
     * @param r1 Primera respuesta numérica
     * @param r2 Segunda respuesta numérica
     * @return Distancia normalizada en [0,1]
     */
    private static double distanciaNumerica(RespuestaNumerica r1, RespuestaNumerica r2) {
        // getRespuesta() devuelve Double en RespuestaNumerica
        double valor1 = r1.getRespuesta();
        double valor2 = r2.getRespuesta();
        
        // TODO: Obtener Vmax y Vmin de la pregunta correspondiente
        // Por ahora, placeholder con normalización asumiendo rango [0, 100]
        double vmax = 100.0;
        double vmin = 0.0;
        
        if (vmax == vmin) return 0.0;  // Evitar división por cero
        
        return Math.abs(valor1 - valor2) / (vmax - vmin);
    }
    
    /**
     * Distancia para opción única (fórmula 3 del PDF).
     * Retorna 0 si las opciones son iguales, 1 si son diferentes.
     * 
     * @param r1 Primera respuesta de opción única
     * @param r2 Segunda respuesta de opción única
     * @return 0.0 si igual, 1.0 si diferente
     */
    private static double distanciaOpcionUnica(RespuestaOpcionUnica r1, RespuestaOpcionUnica r2) {
        // getRespuesta() devuelve Integer (índice de la opción escogida)
        Integer opcion1 = r1.getRespuesta();
        Integer opcion2 = r2.getRespuesta();
        
        return opcion1.equals(opcion2) ? 0.0 : 1.0;
    }
    
    /**
     * Distancia para opción múltiple - Coeficiente de Jaccard (fórmula 4 del PDF).
     * Distancia = 1 - Jaccard(A,B) donde Jaccard = |A ∩ B| / |A ∪ B|
     * 
     * @param r1 Primera respuesta de opción múltiple
     * @param r2 Segunda respuesta de opción múltiple
     * @return Distancia en [0,1]
     */
    private static double distanciaOpcionMultiple(RespuestaOpcionMultiple r1, RespuestaOpcionMultiple r2) {
        // getRespuesta() devuelve int[] (array de índices de opciones)
        int[] indices1 = r1.getRespuesta();
        int[] indices2 = r2.getRespuesta();
        
        // Convertir a Set para calcular intersección y unión
        Set<Integer> set1 = new HashSet<>();
        for (int idx : indices1) set1.add(idx);
        
        Set<Integer> set2 = new HashSet<>();
        for (int idx : indices2) set2.add(idx);
        
        // Intersección
        Set<Integer> interseccion = new HashSet<>(set1);
        interseccion.retainAll(set2);
        
        // Unión
        Set<Integer> union = new HashSet<>(set1);
        union.addAll(set2);
        
        if (union.isEmpty()) return 0.0;
        
        // Coeficiente de Jaccard
        double jaccard = (double) interseccion.size() / union.size();
        
        // Distancia = 1 - similitud
        return 1.0 - jaccard;
    }
    
    /**
     * Distancia de Levenshtein normalizada para strings libres (fórmula 5 del PDF).
     * 
     * @param r1 Primera respuesta libre
     * @param r2 Segunda respuesta libre
     * @return Distancia normalizada en [0,1]
     */
    private static double distanciaLibre(RespuestaLibre r1, RespuestaLibre r2) {
        String s1 = r1.getRespuesta();
        String s2 = r2.getRespuesta();
        
        int lev = levenshtein(s1, s2);
        int maxLen = Math.max(s1.length(), s2.length());
        
        if (maxLen == 0) return 0.0;
        
        // Normalización según fórmula del PDF
        int difLongitud = Math.abs(s1.length() - s2.length());
        return (double)(lev - difLongitud) / (maxLen - difLongitud);
    }
    
    /**
     * Distancia de Levenshtein (programación dinámica).
     */
    private static int levenshtein(String a, String b) {
        int[][] dp = new int[a.length() + 1][b.length() + 1];
        
        for (int i = 0; i <= a.length(); i++) dp[i][0] = i;
        for (int j = 0; j <= b.length(); j++) dp[0][j] = j;
        
        for (int i = 1; i <= a.length(); i++) {
            for (int j = 1; j <= b.length(); j++) {
                int cost = (a.charAt(i-1) == b.charAt(j-1)) ? 0 : 1;
                dp[i][j] = Math.min(Math.min(
                    dp[i-1][j] + 1,      // deletion
                    dp[i][j-1] + 1),     // insertion
                    dp[i-1][j-1] + cost  // substitution
                );
            }
        }
        
        return dp[a.length()][b.length()];
    }
}
