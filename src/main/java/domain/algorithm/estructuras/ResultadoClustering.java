package domain.algorithm.estructuras;

import java.util.List;

/**
 * Representa el resultado de ejecutar un algoritmo de clustering.
 * 
 * Responsabilidades:
 * - Almacenar perfiles generados por el algoritmo
 * - Mantener métrica de calidad del clustering (silueta)
 * - Proporcionar datos inmutables del resultado
 * 
 * @author Alessandro Jin
 */
public class ResultadoClustering {
    /** Lista de perfiles (clusters) generados */
    private final List<Perfil> perfiles;
    
    /** Métrica de calidad (silueta) del clustering */
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
