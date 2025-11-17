package domain.algorithm.funciones;

import java.util.List;
import java.util.ArrayList;
import domain.*;

/**
 * Clase utilitaria para calcular la distancia entre dos usuarios.
 * Utiliza diferentes métricas de distancia según el tipo de pregunta.
 * 
 * @author Alessandro Jin
 */
public class CalculoDistanciaUsuario {
    
    //p1 y p2 tiene misma longitud y mismas columnas (variables)
    //variables son las columnas
    /**
     * Calcula la distancia entre las respuestas de dos usuarios.
     * @param p1 Lista de respuestas del primer usuario
     * @param p2 Lista de respuestas del segundo usuario
     * @param variables Lista de preguntas/variables
     * @return Distancia normalizada entre los dos usuarios
     */
    public static double calcularDistanciaUsuario(List<Respuesta> p1, List<Respuesta> p2, List<Pregunta> variables) {
        
        if (p1.size() != p2.size()) throw new IllegalArgumentException("Tamaño vector no concuerdan");

        int N = p1.size();

        double sumaDistanciaLocales = 0.0;

        int contadorValidas = 0;
        //distancia local de cada variable
        for (int i = 0; i < N; ++i) {
            Respuesta r1 = p1.get(i);
            Respuesta r2 = p2.get(i);
            if (r1 != null && r2 != null) {
                sumaDistanciaLocales += calcularDistanciaLocal(p1.get(i), p2.get(i), variables.get(i));
                contadorValidas ++;
            }
        }
        return (contadorValidas > 0) ? sumaDistanciaLocales/contadorValidas : 0.0; // distancia global

    }

    /**
     * Calcula la distancia local entre dos respuestas según el tipo de pregunta.
     * @param r1 Respuesta del primer usuario
     * @param r2 Respuesta del segundo usuario
     * @param variable Pregunta que define la métrica
     * @return Distancia normalizada entre 0.0 y 1.0 para esa variable
     */
    private static double  calcularDistanciaLocal (Respuesta r1, Respuesta r2, Pregunta variable) {
        if (r1 instanceof RespuestaNumerica && r2 instanceof RespuestaNumerica && variable instanceof PreguntaNumerica) {
            return distanciaLocalNumerica((RespuestaNumerica) r1, (RespuestaNumerica) r2, (PreguntaNumerica) variable);
        }

        else if (r1 instanceof RespuestaOpcionUnica && r2 instanceof RespuestaOpcionUnica && variable instanceof PreguntaOpcionUnica) {
            return distanciaLocalOpcionUnica((RespuestaOpcionUnica) r1, (RespuestaOpcionUnica) r2, (PreguntaOpcionUnica) variable);
        }

        else if (r1 instanceof RespuestaOpcionMultiple && r2 instanceof RespuestaOpcionMultiple && variable instanceof PreguntaOpcionMultiple) {
            return distanciaLocalOpcionMultiple((RespuestaOpcionMultiple) r1, (RespuestaOpcionMultiple) r2, (PreguntaOpcionMultiple) variable);
        }

        else if (r1 instanceof RespuestaLibre && r2 instanceof RespuestaLibre && variable instanceof PreguntaLibre) {
            return distanciaLocalLibre((RespuestaLibre) r1, (RespuestaLibre) r2);
        }

        //incompatibles
        throw new IllegalArgumentException("Columna respuesta no concuerdan");
       

    }

    /**
     * Distancia normalizada para respuestas numéricas.
     * @param r1 Respuesta numérica del primer usuario
     * @param r2 Respuesta numérica del segundo usuario
     * @param variable Pregunta numérica que contiene rango min/max
     * @return Distancia en [0,1] proporcional a la diferencia relativa
     */
    private static double distanciaLocalNumerica(RespuestaNumerica r1, RespuestaNumerica r2, PreguntaNumerica variable) {
        int maxim = variable.getRangoSup();
        int minim = variable.getRangoInf();

        double n1 = r1.getRespuesta();
        double n2 = r2.getRespuesta();

        double n = Math.abs(n1-n2);
        double rango = (double) (maxim-minim);
        
        return n/rango;
    }

    /**
     * Distancia para preguntas de opción única (considera ordenación si aplica).
     * @param r1 Respuesta del primer usuario
     * @param r2 Respuesta del segundo usuario
     * @param variable Pregunta de tipo opción única
     * @return Distancia local en [0,1]
     */
    private static double distanciaLocalOpcionUnica(RespuestaOpcionUnica r1, RespuestaOpcionUnica r2, PreguntaOpcionUnica variable) {
        if (variable.esOrdenada()) {
            int n = variable.getCantidad(); //size del arraylist

            return distanciaLocalOrdenada(r1, r2, n);
        }

        return distanciaLocalNoOrdenada(r1, r2);
    }

    /**
     * Distancia para opciones ordenadas: diferencia normalizada por rango.
     * @param r1 Respuesta del primer usuario
     * @param r2 Respuesta del segundo usuario
     * @param n Número de opciones
     * @return Distancia normalizada
     */
    private static double distanciaLocalOrdenada(RespuestaOpcionUnica r1, RespuestaOpcionUnica r2, int n) {
        int index1 = r1.getRespuesta();
        int index2 = r2.getRespuesta();

        double deno = (double) (n-1);
        if (deno == 0.0) return 0.0;

        double res = (double) (Math.abs(index1-index2)) / deno;

        return res;
    }

    /**
     * Distancia para opciones no ordenadas: 0 si coincide, 1 si no.
     * @param r1 Respuesta del primer usuario
     * @param r2 Respuesta del segundo usuario
     * @return 0.0 si mismo índice, 1.0 si distinto
     */
    private static double distanciaLocalNoOrdenada(RespuestaOpcionUnica r1, RespuestaOpcionUnica r2) {
        int index1 = r1.getRespuesta();
        int index2 = r2.getRespuesta();

        return (index1 == index2) ? 0.0 : 1.0;
    }

    /**
     * Distancia para preguntas de opción múltiple (1 - Jaccard similarity).
     * @param r1 Respuesta múltiple del primer usuario
     * @param r2 Respuesta múltiple del segundo usuario
     * @param variable Pregunta de tipo opción múltiple
     * @return Distancia entre 0.0 y 1.0 basada en intersección/union
     */
    private static double distanciaLocalOpcionMultiple(RespuestaOpcionMultiple r1, RespuestaOpcionMultiple r2, PreguntaOpcionMultiple variable) {
        int[] arr1 = r1.getRespuesta();
        int[] arr2 = r2.getRespuesta();
        
        List<Integer> indices1 = new ArrayList<>();
        for (int i : arr1) indices1.add(i);
        
        List<Integer> indices2 = new ArrayList<>();
        for (int i : arr2) indices2.add(i);

        List<Integer> inter = new ArrayList<>(indices1);
        inter.retainAll(indices2);
        int intersec = inter.size();

        indices1.removeAll(inter);
        indices1.addAll(indices2);
        int union = indices1.size();

        if (union == 0) return 0;

        return (1.0 - ((double)intersec/union));
    }

    /**
     * Distancia para respuestas libres basada en distancia de Levenshtein normalizada.
     * @param r1 Respuesta libre del primer usuario
     * @param r2 Respuesta libre del segundo usuario
     * @return Distancia normalizada en [0,1]
     */
    private static double distanciaLocalLibre(RespuestaLibre r1, RespuestaLibre r2) {
        String s1 = r1.getRespuesta();
        String s2 = r2.getRespuesta();

        int distance = levenshtein(s1, s2);

        int n1 = s1.length();
        int n2 = s2.length();

        double n = (double) Math.abs(n1-n2);

        double deno = (double) (Math.max(n1,n2) - n);
        if (deno == 0) return 0;

        double  res = (distance - n) / deno;
        return res;
    }

    /**
     * Calcula la distancia de Levenshtein (número mínimo de operaciones).
     * @param s1 Texto 1
     * @param s2 Texto 2
     * @return Distancia entera de Levenshtein
     */
    private static int levenshtein(String s1, String s2) {
        int n1 = s1.length();
        int n2 = s2.length();
        
        int dp[][] = new int [n1+1][n2+1];

        for (int i = 0; i < n1+1; ++i) dp[i][0] = i;
        for (int j = 0; j < n2+1; ++j) dp[0][j] = j;

        for (int i = 1; i < n1+1; ++i) {    
            for (int j = 1; j < n2+1; ++j) {
                int insert = dp[i][j-1] + 1;

                int elim = dp[i-1][j] + 1;

                int subs = dp[i-1][j-1];

                int cost = (s1.charAt(i-1) == s2.charAt(j-1)) ? 0 : 1;

                dp[i][j] = Math.min (Math.min(insert, elim), subs+cost);
            }
        }

        return dp[n1][n2];
        
    }
}
