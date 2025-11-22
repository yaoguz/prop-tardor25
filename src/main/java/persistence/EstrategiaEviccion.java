package persistence;

import java.util.Set;
import java.util.List;

/**
 * Estrategia para seleccionar elementos a descartar de la cache.
 * 
 * Permite implementar diferentes políticas de evicción (LRU, FIFO, LFU, etc.)
 * usando el patrón Strategy. Cuando la cache alcanza su capacidad máxima,
 * se debe descartar un bloque de elementos según la estrategia elegida.
 * 
 * RESPONSABILIDADES:
 * - Definir interfaz para selección de víctimas de evicción
 * - Permitir extensibilidad con nuevas estrategias
 * 
 * @author PROP Cluster XX
 */
public interface EstrategiaEviccion {
    
    /**
     * Selecciona un conjunto de claves a descartar de la cache.
     * 
     * @param <K> Tipo de las claves
     * @param claves Conjunto de todas las claves disponibles en la cache
     * @param numElementos Número de elementos a seleccionar para descarte
     * @return Lista de claves seleccionadas para evicción
     */
    <K> List<K> seleccionarVictimas(Set<K> claves, int numElementos);
}
