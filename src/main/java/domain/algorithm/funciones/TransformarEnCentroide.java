package domain.algoritmo.estructuras;

import java.util.ArrayList;
import java.util.List;
import domain.*;

/**
 * @author Alessandro Jin
 */
/**
 * Transforma un perfil de respuestas en un Centroide.
 */
public class TransformarEnCentroide {
    public static Centroide transformarEnCentroide(List<Respuesta> respuesta) {
        List<ComponenteCentroide> centroide = new ArrayList<>();

        for (Respuesta r: respuesta) {
            if (r == null) {
                // para respuestas nulas, añadir componente con valor por defecto (pnd revisar)
                centroide.add(new ComponenteNumerico(0.0));
            }
            else if (r instanceof RespuestaNumerica) {
                double valor = ((RespuestaNumerica) r).getRespuesta();
                centroide.add(new ComponenteNumerico(valor));
            }
            else if (r instanceof RespuestaOpcionUnica) {
                int indice = ((RespuestaOpcionUnica) r).getRespuesta();
                centroide.add(new ComponenteUnico(indice));
            }
            else if (r instanceof RespuestaOpcionMultiple) {
                int[] indices = ((RespuestaOpcionMultiple) r).getRespuesta();
                List<Integer> listaIndices = new ArrayList<>();
                for (int i : indices) {
                    listaIndices.add(i);
                }
                centroide.add(new ComponenteMultiple(listaIndices));
            }
            else if (r instanceof RespuestaLibre) {
                String text = ((RespuestaLibre) r).getRespuesta();
                centroide.add(new ComponenteLibre(text));
            }
        }

        return new Centroide(centroide);
    }
}
