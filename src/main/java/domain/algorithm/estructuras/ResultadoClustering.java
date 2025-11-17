package domain.algorithm.estructuras;

import java.util.List;

/**
 * Representa el resultado de ejecutar un algoritmo de clustering.
 * Contiene los perfiles generados y la calidad del clustering.
 * 
 * @author Alessandro Jin
 */
public class ResultadoClustering {
    private final List<Perfil> perfiles;
    private final double calidad;

    /**
     * Constructor del resultado de clustering.
     * @param perfiles Lista de perfiles generados
     * @param calidad Métrica de calidad del clustering
     */
    public ResultadoClustering(List<Perfil> perfiles, double calidad) {
        this.perfiles = perfiles;
        this.calidad = calidad;
    }

    /**
     * Obtiene la lista de perfiles generados.
     * @return Lista de perfiles
     */
    public List<Perfil> getPerfiles() { 
        return perfiles; 
    }
    
    /**
     * Obtiene la métrica de calidad del clustering.
     * @return Valor de calidad
     */
    public double getCalidad() { 
        return calidad; 
    }

}
