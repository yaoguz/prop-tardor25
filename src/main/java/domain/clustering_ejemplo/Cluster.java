// domain/clustering/Cluster.java
package domain.clustering_ejemplo;

import java.util.*;

/**
 * Representa un cluster (grupo) de usuarios con comportamiento similar.
 */
public class Cluster {
    private int id;
    private List<String> miembros;  // IDs de usuarios
    private List<Double> centroide;  // Centroide del cluster
    
    public Cluster(int id) {
        this.id = id;
        this.miembros = new ArrayList<>();
        this.centroide = new ArrayList<>();
    }
    
    public void agregarMiembro(String idUsuario) {
        miembros.add(idUsuario);
    }
    
    public void limpiarMiembros() {
        miembros.clear();
    }
    
    public List<String> getMiembros() {
        return new ArrayList<>(miembros);
    }
    
    public void setCentroide(List<Double> centroide) {
        this.centroide = new ArrayList<>(centroide);
    }
    
    public List<Double> getCentroide() {
        return new ArrayList<>(centroide);
    }
    
    public int getId() { return id; }
    public int size() { return miembros.size(); }
    public boolean isEmpty() { return miembros.isEmpty(); }
}