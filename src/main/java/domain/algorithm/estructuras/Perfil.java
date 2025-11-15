package domain.algorithm.estructuras;

import java.util.List;

/**
 * @author Alessandro Jin
 */

public class Perfil {
    private final String representante;

    private final Centroide centroide;
    private final List<String> miembros;

    public Perfil (String representante,  Centroide centroide, List<String> miembros) {
        this.representante = representante;
        this.centroide = centroide;
        this.miembros = miembros;
    }

    public String getRepresentante () {
        return representante;
    }

    public Centroide getCentroide () {
        return centroide;
    }

    public List<String> miembros () {
        return miembros;
    }
}
