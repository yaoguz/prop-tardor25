package domain.algorithm.funciones;

import java.util.List;
import java.util.ArrayList;
import domain.*;
import domain.algorithm.estructuras.*;

/**
 * Clase utilitaria para calcular la distancia entre un usuario y un centroide.
 * Utiliza diferentes métricas de distancia según el tipo de pregunta.
 * 
 * @author Alessandro Jin
 */
public class CalculoDistanciaCentroide {
    
    //p1 y p2 tiene misma longitud y mismas columnas (variables)
    //variables son las columnas
    /**
     * Calcula la distancia entre las respuestas de un usuario y un centroide.
     * @param p Lista de respuestas del usuario
     * @param centro Centroide del cluster
     * @param variables Lista de preguntas/variables
     * @return Distancia normalizada entre usuario y centroide
     */
    public static double calcularDistanciaCentroide(List<Respuesta> p, Centroide centro, List<Pregunta> variables) {
        

        int N = variables.size();

        double sumaDistanciaLocales = 0.0;
        int contadorValidas = 0;

        //distancia local de cada variable
        for (int i = 0; i < N; ++i) {
            Respuesta r = p.get(i);
            ComponenteCentroide c = centro.getComponente(i);
            if (r != null && c != null) {
                sumaDistanciaLocales += calcularDistanciaLocal(r, centro.getComponente(i), variables.get(i));
                contadorValidas++;
            }
            // si r es null, ignoramos esa variable en el calculo
        }

        return (contadorValidas > 0) ? sumaDistanciaLocales/contadorValidas : 0.0; // distancia global

    }
    /**
     * Calcula la distancia local entre una respuesta y el componente del centroide según el tipo.
     * @param r Respuesta del usuario (puede ser numérica, única, múltiple o libre)
     * @param comp Componente correspondiente del centroide
     * @param variable Pregunta que define la métrica (rango, ordenación, etc.)
     * @return Distancia normalizada entre 0.0 y 1.0 para esa variable
     */
    private static double  calcularDistanciaLocal (Respuesta r, ComponenteCentroide comp, Pregunta variable) {
        if (r instanceof RespuestaNumerica && comp instanceof ComponenteNumerico && variable instanceof PreguntaNumerica) {
            return distanciaLocalNumerica((RespuestaNumerica) r, (ComponenteNumerico) comp, (PreguntaNumerica) variable);
        }

        else if (r instanceof RespuestaOpcionUnica && comp instanceof ComponenteUnico && variable instanceof PreguntaOpcionUnica) {
            return distanciaLocalOpcionUnica((RespuestaOpcionUnica) r, (ComponenteUnico) comp, (PreguntaOpcionUnica) variable);
        }

        else if (r instanceof RespuestaOpcionMultiple && comp instanceof ComponenteMultiple && variable instanceof PreguntaOpcionMultiple) {
            return distanciaLocalOpcionMultiple((RespuestaOpcionMultiple) r, (ComponenteMultiple) comp, (PreguntaOpcionMultiple) variable);
        }

        else if (r instanceof RespuestaLibre && comp instanceof ComponenteLibre && variable instanceof PreguntaLibre) {
            return distanciaLocalLibre((RespuestaLibre) r, (ComponenteLibre) comp);
        }

        //incompatibles
        throw new IllegalArgumentException("Columna respuesta no concuerdan");
       

    }

    /**
     * Distancia normalizada para respuestas numéricas.
     * @param r Respuesta numérica del usuario
     * @param comp Componente numérico del centroide
     * @param variable Pregunta numérica que contiene rango min/max
     * @return Distancia en [0,1] proporcional a la diferencia relativa
     */
    private static double distanciaLocalNumerica(RespuestaNumerica r, ComponenteNumerico comp, PreguntaNumerica variable) {
        int maxim = variable.getRangoSup();
        int minim = variable.getRangoInf();

        double n1 = r.getRespuesta();
        double n2 = comp.getValor();

        double n = Math.abs(n1-n2);
        double rango = (double) (maxim-minim);
        if (rango == 0) return 0.0;
        return n/rango;
    }

    /**
     * Distancia para preguntas de opción única (considera ordenación si aplica).
     * @param r Respuesta de opción única
     * @param comp Componente único del centroide
     * @param variable Pregunta de tipo opción única
     * @return Distancia local en [0,1]
     */
    private static double distanciaLocalOpcionUnica(RespuestaOpcionUnica r, ComponenteUnico comp, PreguntaOpcionUnica variable) {
        if (variable.esOrdenada()) {
            int n = variable.getCantidad(); //size del arraylist

            return distanciaLocalOrdenada(r, comp, n);
        }

        return distanciaLocalNoOrdenada(r, comp);
    }

    /**
     * Distancia para opciones ordenadas: diferencia normalizada por rango.
     * @param r Respuesta del usuario
     * @param comp Componente del centroide
     * @param n Número de opciones
     * @return Distancia normalizada
     */
    private static double distanciaLocalOrdenada(RespuestaOpcionUnica r, ComponenteUnico comp, int n) {
        int index1 = r.getRespuesta();
        int index2 = comp.getIndice();

        double deno = (double) (n-1);
        if (deno == 0.0) return 0.0;

        double res = (double) (Math.abs(index1-index2)) / deno;

        return res;
    }

    /**
     * Distancia para opciones no ordenadas: 0 si coincide, 1 si no.
     * @param r Respuesta del usuario
     * @param comp Componente del centroide
     * @return 0.0 si mismo índice, 1.0 si distinto
     */
    private static double distanciaLocalNoOrdenada(RespuestaOpcionUnica r, ComponenteUnico comp) {
        int index1 = r.getRespuesta();
        int index2 = comp.getIndice();

        return (index1 == index2) ? 0.0 : 1.0;
    }

    /**
     * Distancia para preguntas de opción múltiple (1 - Jaccard similarity).
     * @param r Respuesta múltiple del usuario
     * @param comp Componente múltiple del centroide
     * @param variable Pregunta de tipo opción múltiple
     * @return Distancia entre 0.0 y 1.0 basada en intersección/union
     */
    private static double distanciaLocalOpcionMultiple(RespuestaOpcionMultiple r, ComponenteMultiple comp, PreguntaOpcionMultiple variable) {
        int[] indices1Array = r.getRespuesta();
        List<Integer> indices1 = new ArrayList<>();
        for (int i : indices1Array) {
            indices1.add(i);
        }
        List<Integer> indices2 = new ArrayList<>(comp.getIndices());

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
     * @param r Respuesta libre del usuario
     * @param comp Componente libre del centroide (texto representante)
     * @return Distancia normalizada en [0,1]
     */
    private static double distanciaLocalLibre(RespuestaLibre r, ComponenteLibre comp) {
        String s1 = r.getRespuesta();
        String s2 = comp.getTexto();

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
