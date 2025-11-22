package persistence;

import java.util.Set;
import java.util.List;
import java.util.ArrayList;

/**
 * Estrategia de evicción Last Recently Used (LRU).
 * 
 * Selecciona los elementos menos recientemente usados para descarte.
 * Asume que las claves están ordenadas por orden de acceso (las primeras
 * son las más antiguas) cuando se usan estructuras como LinkedHashMap
 * con accessOrder=true.
 * 
 * RESPONSABILIDADES:
 * - Implementar política LRU para evicción de cache
 * - Seleccionar N elementos menos recientemente accedidos
 * 
 * ATRIBUTOS PRINCIPALES:
 * - Ninguno (estrategia stateless)
 * 
 * MÉTODOS PRINCIPALES:
 * - seleccionarVictimas(): Retorna primeros N elementos (más antiguos)
 * 
 * @author PROP Cluster XX
 */
public class LRU implements EstrategiaEviccion {
    
    /**
     * Selecciona los N elementos menos recientemente usados.
     * 
     * En un LinkedHashMap con accessOrder=true, los primeros elementos
     * son los menos recientemente accedidos.
     * 
     * @param <K> Tipo de las claves
     * @param claves Conjunto de claves (ordenadas por acceso si es LinkedHashMap)
     * @param numElementos Número de elementos a seleccionar (ej: 16)
     * @return Lista con las claves menos recientemente usadas
     */
    @Override
    public <K> List<K> seleccionarVictimas(Set<K> claves, int numElementos) {
        List<K> victimas = new ArrayList<>();
        int count = 0;
        for (K clave : claves) {
            if (count >= numElementos) break;
            victimas.add(clave);
            count++;
        }
        return victimas;
    }
}
