package data;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class ConjuntoObjetos<T> {
    private Map<Integer, T> objetos = new HashMap<Integer, T>();
    private int nextIndex = 1;


    public int newObject (T objeto) {
        int id = nextIndex;
        objetos.put(id, objeto);
        nextIndex++;
        return id;
    }

    public T getObject(int id) {
        return objetos.get(id);
    }

    public Boolean removeObject(int id) {
        T obj = objetos.remove(id);
        return (obj != null);
    }

    public ArrayList getAll() {
        return new ArrayList<T> (objetos.values);
    }
}