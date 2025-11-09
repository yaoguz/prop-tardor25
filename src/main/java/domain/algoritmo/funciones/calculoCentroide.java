package domain.algoritmo;

import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

import domain.*;

/**
 * @author Alessandro Jin
 */
 public class CalculoCentroide {
    
    //dado un cluster devuelve un centroide
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
                int N = p.getCantidad();
                int indice = indiceUnicoFrecuente(columnaRespuestas, N);
                centro.add(new ComponenteUnico(indice));
            }

            else if (p instanceof PreguntaOpcionMultiple) {
                int N = p.getCantidad();
                List<Integer> l = indicesMultipleFrecuente(columnaRespuestas, N);
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
    private static List<Respuesta> obtenerColumna(List<List<Respuesta>> respuestas, int columna) {
        List<Respuesta> columnaRespuestas = new ArrayList<>();
        for (List<Respuesta> fila : respuestas) {
            columnaRespuestas.add(fila.get(columna));
        }
        return columnaRespuestas;
    }


    private static double mediaNumerica (List<Respuesta> respuestas) {
        int size = respuestas.size();

        //para cada miembro de cluster
        double sum = 0.0;
        for (Respuesta r : respuestas) {
            sum += r.getRespuesta();
        }

        return sum/size;
    }

    private static int indiceUnicoFrecuente (List<Respuesta> respuestas, int N) {
     
        int [] frecuencia = new int[N];

        for (Respuesta r : respuestas) {
            int opcion = r.getRespuesta(); //0-based

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

    private static List<Integer> indicesMultipleFrecuente(List<Respuesta> respuestas, int N) {
        
        int [] frecuencia = new int[N];

        for (Respuesta r : respuestas) {
            int opcion = r.getRespuesta(); //0-based

            frecuencia[opcion]++;
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

    private static String textoRepresentante(List<Respuesta> respuestas) {
       

        Map<String, Integer> freq = new HashMap<>();
        for (Respuesta r : respuestas) {
            String texto = r.getRespuesta();
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