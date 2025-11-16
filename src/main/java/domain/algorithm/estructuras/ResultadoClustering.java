package domain.algorithm.estructuras;

import java.util.List;
import domain.algorithm.estructuras.*;
import domain.algorithm.funciones.*;
import domain.algorithm.estrategias.*;

/**
 * @author Alessandro Jin
 */

public class ResultadoClustering {
    private final List<Perfil> perfiles;
    private final double calidad;

    public ResultadoClustering(List<Perfil> perfiles, double calidad) {
        this.perfiles = perfiles;
        this.calidad = calidad;
    }

    public List<Perfil> getPerfiles() { 
        return perfiles; 
    }
    public double getCalidad() { 
        return calidad; 
    }

}
