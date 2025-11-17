package domain.algorithm.estructuras;

import java.util.List;
import java.util.ArrayList;

/**
 * Representa un cluster en el algoritmo de clustering.
 * Contiene un centroide y una lista de índices de usuarios que pertenecen al cluster.
 * 
 * @author Alessandro Jin
 */
public class Cluster {
    private Centroide centroide;
    private List<Integer> miembros;

    /**
     * Constructor de cluster con centroide inicial.
     * @param c Centroide inicial del cluster
     */
    public Cluster(Centroide c) {
        this.centroide = c;
        this.miembros = new ArrayList<>();
    }

    /**
     * Constructor de cluster con centroide y lista de miembros.
     * @param c Centroide del cluster
     * @param miembros Lista de índices de usuarios en el cluster
     */
    public Cluster (Centroide c, List<Integer> miembros) {
        this.centroide = c;
        this.miembros = miembros;
    }

    /**
     * Cambia el centroide del cluster.
     * @param c Nuevo centroide
     */
    public void changeCentroide (Centroide c) {
        this.centroide = c;
    }

    /**
     * Reemplaza la lista de miembros del cluster.
     * @param miembros Nueva lista de índices de usuarios
     */
    public void changeMiembros (List<Integer> miembros) {
        this.miembros = miembros;
    }

    /**
     * Añade un miembro al cluster.
     * @param i Índice del usuario a añadir
     */
    public void addMiembro(Integer i) {
        miembros.add(i);
    }

    /**
     * Obtiene el centroide del cluster.
     * @return Centroide actual
     */
    public Centroide getCentroide () {
        return centroide;
    }

    /**
     * Obtiene la lista de índices de usuarios del cluster.
     * @return Lista de índices de miembros
     */
    public List<Integer> getMiembros() {
        return miembros;
    }

    /**
     * Elimina todos los miembros del cluster.
     */
    public void borrarMiembros() {
        this.miembros.clear();
    }
}
