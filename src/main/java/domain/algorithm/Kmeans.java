package domain.algorithm;

import domain.algorithm.estrategias.*;

/**
 * @author Alessandro Jin
 */

public class Kmeans extends Kalgorithm {;

    //devolver listado de nombre por grupo
    @Override
    public String getNombre() {
        return "Kmeans";
    }
    
    @Override
    protected EstrategiaInicializacion getEstrategiaInicializacion() {
        return new InicializacionAleatoria();
    }
}
