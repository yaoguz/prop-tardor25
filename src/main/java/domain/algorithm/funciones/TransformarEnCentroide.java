package domain.algorithm.funciones;

import java.util.ArrayList;
import java.util.List;
import domain.*;
import domain.algorithm.estructuras.*;

/**
 * Transforma un perfil de respuestas en un Centroide.
 * 
 * @author Alessandro Jin
 */
public class TransformarEnCentroide {
    /**
     * Transforma una lista de respuestas en un centroide.
     * @param respuesta Lista de respuestas a transformar
     * @return Centroide generado
     */
    public static Centroide transformarEnCentroide(List<Respuesta> respuesta) {
        List<ComponenteCentroide> centroide = new ArrayList<>();

        for (Respuesta r: respuesta) {
            if (r == null) {
                // para respuestas nulas, añadir componente nulo
                centroide.add(null);
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
