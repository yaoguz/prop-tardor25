package domain.algorithm.funciones;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import domain.*;
import domain.algorithm.estructuras.*;

/**
 * Clase utilit aria para calcular el centroide de un cluster.
 * Proporciona métodos estáticos para calcular centroides basados en diferentes tipos de preguntas.
 * 
 * @author Alessandro Jin
 */
public class CalculoCentroide {
    
    /**
     * Calcula el centroide de un cluster dado las respuestas y variables.
     * @param respuestas Matriz de respuestas del cluster
     * @param variables Lista de preguntas/variables
     * @return Centroide calculado
     */
    public static Centroide calcularCentroide (List<List<Respuesta>> respuestas, List<Pregunta> variables) {

        int N = variables.size();

        //en orden de las variables
        List<ComponenteCentroide> centro = new ArrayList<> ();

        //para cada variable
        for (int i = 0; i < N; ++i) {
            Pregunta p = variables.get(i);

            // Extracción de la columna de respuestas para la variable actual
            List<Respuesta> columnaRespuestas = obtenerColumna(respuestas, i);

            if (p instanceof PreguntaNumerica) {
                double n = mediaNumerica(columnaRespuestas);
                centro.add(new ComponenteNumerico(n));
            }

            else if (p instanceof PreguntaOpcionUnica) {
                int cantidadOpciones = p.getCantidad();
                int indice = indiceUnicoFrecuente(columnaRespuestas, cantidadOpciones);
                centro.add(new ComponenteUnico(indice));
            }

            else if (p instanceof PreguntaOpcionMultiple) {
                int cantidadOpciones = p.getCantidad();
                List<Integer> l = indicesMultipleFrecuente(columnaRespuestas, cantidadOpciones);
                centro.add(new ComponenteMultiple(l));
            }

            else if (p instanceof PreguntaLibre) {
                String s = textoRepresentante(columnaRespuestas);
                centro.add(new ComponenteLibre(s));
            }

        }

        return new Centroide(centro);
    }

    // Función auxiliar para extraer una columna de respuestas
    /**
     * Extrae una columna (lista de respuestas para una variable) de la matriz.
     * @param respuestas Matriz de respuestas donde cada fila es un usuario
     * @param columna Índice de la columna a extraer
     * @return Lista de respuestas correspondiente a la columna indicada
     */
    private static List<Respuesta> obtenerColumna(List<List<Respuesta>> respuestas, int columna) {
        List<Respuesta> columnaRespuestas = new ArrayList<>();
        for (List<Respuesta> fila : respuestas) {
            columnaRespuestas.add(fila.get(columna));
        }
        return columnaRespuestas;
    }


    /**
     * Calcula la media de respuestas numéricas en una columna.
     * @param respuestas Lista de respuestas numéricas
     * @return Media aritmética de las respuestas (double)
     */
    private static double mediaNumerica (List<Respuesta> respuestas) {
        int size = respuestas.size();

        //para cada miembro de cluster
        double sum = 0.0;
        for (Respuesta r : respuestas) {
            sum += ((RespuestaNumerica) r).getRespuesta();
        }

        return sum/size;
    }

    /**
     * Obtiene el índice más frecuente en respuestas de opción única.
     * @param respuestas Lista de respuestas de opción única
     * @param N Número de opciones posibles
     * @return Índice (0-based) de la opción más frecuente
     */
    private static int indiceUnicoFrecuente (List<Respuesta> respuestas, int N) {
     
        int [] frecuencia = new int[N];

        for (Respuesta r : respuestas) {
            int opcion = ((RespuestaOpcionUnica) r).getRespuesta(); //0-based

            frecuencia[opcion]++;
        }

        int index = 0;
        int maxim = frecuencia[0];
        for (int i = 1; i < N; ++i) {
            if (frecuencia[i] > maxim) {
                index = i;
                maxim = frecuencia[i];
            }
        }

        return index;
    }

    /**
     * Obtiene la(s) opción(es) más frecuentes en respuestas múltiples.
     * @param respuestas Lista de respuestas de opción múltiple
     * @param N Número de opciones posibles
     * @return Lista de índices (0-based) de las opciones mayoritarias
     */
    private static List<Integer> indicesMultipleFrecuente(List<Respuesta> respuestas, int N) {
        
        int [] frecuencia = new int[N];

        for (Respuesta r : respuestas) {
            int[] opciones = ((RespuestaOpcionMultiple) r).getRespuesta();
            for (int opcion : opciones) {
                frecuencia[opcion]++;
            }
        }

        List<Integer> l = new ArrayList<> ();
        int maxim = frecuencia[0];
        for (int i = 0; i < N; ++i) {
            if (frecuencia[i] > maxim) {
                maxim = frecuencia[i];
                l.clear();

                l.add(i);
            }

            else if (frecuencia[i] == maxim) l.add(i);
        }

        return l;
    }

    /**
     * Selecciona palabras representativas de respuestas libres (tokeniza y quita stop words).
     * @param respuestas Lista de respuestas libres
     * @return Texto formado por la(s) palabra(s) con mayor frecuencia
     */
    private static String textoRepresentante(List<Respuesta> respuestas) {
       

        Map<String, Integer> freq = new HashMap<>();
        for (Respuesta r : respuestas) {
            String texto = ((RespuestaLibre) r).getRespuesta();
            texto = texto.toLowerCase();  

            //Limpiar caracteres especiales (dejar solo letras, números y espacios)
            texto = texto.replaceAll("[^\\p{L}\\p{Nd}\\s]", " ");  

            //Separar por espacios (tokenizar)
            String[] palabras = texto.trim().split("\\s+");

            for (String palabra : palabras) {
                if (!StopWordsES.isStopWord(palabra)) freq.put(palabra, freq.getOrDefault(palabra, 0) + 1);
            }
        }

        List<String> text = new ArrayList<>();
        int maxfreq = -1;
        for (Map.Entry<String, Integer> e : freq.entrySet()) {
            String palabra = e.getKey();     
            int frecuencia = e.getValue();     

            if (frecuencia > maxfreq) {
                maxfreq = frecuencia;
                text.clear();
                text.add(palabra);
            }

            else if (frecuencia == maxfreq) text.add(palabra);
        }

        return String.join(" ", text);

    }
 }