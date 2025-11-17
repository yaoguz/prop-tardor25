package domain.algorithm.estructuras;

import java.util.List;

/**
 * Representa un perfil de usuario resultante del clustering.
 * Contiene un representante, el centroide del cluster y la lista de miembros.
 * 
 * @author Alessandro Jin
 */
public class Perfil {
    private final String representante;

    private final Centroide centroide;
    private final List<String> miembros;

    /**
     * Constructor del perfil.
     * @param representante Nombre del usuario representante del cluster
     * @param centroide Centroide del cluster
     * @param miembros Lista de nombres de usuarios que pertenecen al cluster
     */
    public Perfil (String representante,  Centroide centroide, List<String> miembros) {
        this.representante = representante;
        this.centroide = centroide;
        this.miembros = miembros;
    }

    /**
     * Obtiene el nombre del usuario representante del cluster.
     * @return Nombre del representante
     */
    public String getRepresentante () {
        return representante;
    }

    /**
     * Obtiene el centroide del cluster.
     * @return Centroide del cluster
     */
    public Centroide getCentroide () {
        return centroide;
    }

    /**
     * Obtiene la lista de nombres de usuarios del cluster.
     * @return Lista de nombres de miembros
     */
    public List<String> getMiembros () {
        return miembros;
    }
}
