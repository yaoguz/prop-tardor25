package domain.algoritmo.estructuras;

import java.util.List;
import java.util.ArrayList;

/**
 * @author Alessandro Jin
 */

public class Cluster {
    private Centroide centroide;
    private List<Integer> miembros;


    public Cluster(Centroide c) {
        this.centroide = c;
        this.miembros = new ArrayList<>();
    }

    public Cluster (Centroide c, List<Integer> miembros) {
        this.centroide = c;
        this.miembros = miembros;
    }

    public void changeCentroide (Centroide c) {
        this.centroide = c;
    }

    public void changeMiembros (List<Integer> miembros) {
        this.miembros = miembros;
    }


    public void addMiembro(Integer i) {
        miembros.add(i);
    }

    public Centroide getCentroide () {
        return centroide;
    }

    public List<Integer> getMiembros() {
        return miembros;
    }


    public void borrarMiembros() {
        this.miembros.clear();
    }
}
