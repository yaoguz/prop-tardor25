# TODO: Implementaciones Pendientes

## 📋 Resumen
Este documento lista todos los métodos que necesitan ser implementados en los controladores de clase y entidades de dominio para que el sistema funcione completamente.

---

## 🔴 CRÍTICO: Métodos Faltantes en Controladores de Clase

### 1. CtrlUsuario.java ⚠️ ACTUALIZAR: int → String nomUsuario

**CAMBIO REQUERIDO**: Usar `String nomUsuario` como clave primaria en lugar de `int idUsuario`.

```java
package classcontroller;

import domain.Usuario;
import java.util.HashMap;
import java.util.Map;

public class CtrlUsuario {
    
    private static CtrlUsuario singletonObject;
    private Map<String, Usuario> usuarios;  // ⚠️ CAMBIO: String en lugar de Integer
    private Usuario usuarioActivo;
    
    private CtrlUsuario() {
        usuarios = new HashMap<>();
    }
    
    public static CtrlUsuario getInstance() {
        if (singletonObject == null)
            singletonObject = new CtrlUsuario();
        return singletonObject;
    }
    
    // ========== MÉTODOS FALTANTES ==========
    
    /**
     * Crea un nuevo usuario.
     * @return Usuario creado
     */
    public Usuario crearUsuario(String nomU, String contrasena) {
        if (usuarios.containsKey(nomU)) {
            throw new IllegalStateException("Ya existe un usuario con el nombre " + nomU);
        }
        
        Usuario u = new Usuario(nomU, contrasena);  // ⚠️ Sin ID numérico
        usuarios.put(nomU, u);
        return u;
    }
    
    /**
     * Obtiene un usuario por su nombre.
     * ⚠️ CAMBIO: String nomUsuario en lugar de int idUsuario
     */
    public Usuario getUsuario(String nomUsuario) {
        Usuario u = usuarios.get(nomUsuario);
        if (u == null) {
            throw new IllegalArgumentException("Usuario " + nomUsuario + " no existe");
        }
        return u;
    }
    
    /**
     * Verifica si existe un usuario.
     * ⚠️ CAMBIO: String nomUsuario en lugar de int idUsuario
     */
    public boolean existeUsuario(String nomUsuario) {
        return usuarios.containsKey(nomUsuario);
    }
    
    /**
     * Inicia sesión (marca usuario como activo).
     */
    public void iniciarSesion(String nomU, String contrasena) {
        Usuario u = getUsuario(nomU);
        if (!u.comprobarContrasena(contrasena)) {
            throw new IllegalStateException("Contraseña incorrecta");
        }
        usuarioActivo = u;
    }
    
    /**
     * Cierra sesión.
     */
    public void cerrarSesion() {
        usuarioActivo = null;
    }
    
    /**
     * Obtiene el usuario activo.
     */
    public Usuario getUsuarioActivo() {
        if (usuarioActivo == null) {
            throw new IllegalStateException("No hay sesión activa");
        }
        return usuarioActivo;
    }
    
    /**
     * Elimina un usuario.
     * ⚠️ CAMBIO: String nomUsuario en lugar de int id
     */
    public void eliminar(Usuario u) {
        usuarios.remove(u.getNomUsuario());
    }
}
```

---

### 2. CtrlEncuesta.java ⚠️ ACTUALIZAR: int idUsuario → String nomUsuario

**CAMBIO REQUERIDO**: Parámetro `String nomUsuario` en lugar de `int idUsuario`.

```java
package classcontroller;

import domain.Encuesta;
import domain.Usuario;
import java.util.HashMap;
import java.util.Map;

public class CtrlEncuesta {
    
    private static CtrlEncuesta singletonObject;
    private Map<Integer, Encuesta> encuestas;
    private int nextId;
    
    private CtrlEncuesta() {
        encuestas = new HashMap<>();
        nextId = 1;
    }
    
    public static CtrlEncuesta getInstance() {
        if (singletonObject == null)
            singletonObject = new CtrlEncuesta();
        return singletonObject;
    }
    
    // ========== MÉTODOS FALTANTES ==========
    
    /**
     * Crea una nueva encuesta.
     * ⚠️ CAMBIO: String nomUsuario en lugar de int idUsuario
     * @return ID de la encuesta creada
     */
    public int crearEncuesta(String nomUsuario, String titulo) {
        int id = nextId++;
        Encuesta e = new Encuesta(id, titulo, nomUsuario);
        encuestas.put(id, e);
        return id;
    }
    
    /**
     * Obtiene una encuesta por su ID.
     */
    public Encuesta getEncuesta(int idEncuesta) {
        Encuesta e = encuestas.get(idEncuesta);
        if (e == null) {
            throw new IllegalArgumentException("Encuesta con ID " + idEncuesta + " no existe");
        }
        return e;
    }
    
    /**
     * Verifica si existe una encuesta.
     */
    public boolean existeEncuesta(int idEncuesta) {
        return encuestas.containsKey(idEncuesta);
    }
    
    /**
     * Elimina una encuesta.
     */
    public void eliminar(Encuesta e) {
        encuestas.remove(e.getIdEncuesta());
    }
    
    /**
     * Obtiene todas las encuestas.
     */
    public Map<Integer, Encuesta> getTodasEncuestas() {
        return new HashMap<>(encuestas);
    }
}
```

---

### 3. CtrlPregunta.java

```java
package classcontroller;

import domain.*;
import java.util.HashMap;
import java.util.Map;

public class CtrlPregunta {
    
    private static CtrlPregunta singletonObject;
    private Map<Integer, Pregunta> preguntas;
    private int nextId;
    
    private CtrlPregunta() {
        preguntas = new HashMap<>();
        nextId = 1;
    }
    
    public static CtrlPregunta getInstance() {
        if (singletonObject == null)
            singletonObject = new CtrlPregunta();
        return singletonObject;
    }
    
    // ========== MÉTODOS FALTANTES ==========
    
    /**
     * Crea una pregunta polimórfica según el tipo.
     * Factory Method Pattern.
     * 
     * @param idEncuesta ID de la encuesta
     * @param enunciado Enunciado de la pregunta
     * @param tipo Tipo: "libre", "numerica", "opcion_unica", "opcion_multiple"
     * @param atributos Atributos específicos del tipo
     * @return ID de la pregunta creada
     */
    public int crearPregunta(int idEncuesta, String enunciado, String tipo, 
                            Map<String, Object> atributos) {
        int id = nextId++;
        Pregunta p;
        
        switch (tipo.toLowerCase()) {
            case "libre":
                p = new PreguntaLibre(id, idEncuesta, enunciado);
                break;
                
            case "numerica":
                double min = (Double) atributos.getOrDefault("min", Double.MIN_VALUE);
                double max = (Double) atributos.getOrDefault("max", Double.MAX_VALUE);
                p = new PreguntaNumerica(id, idEncuesta, enunciado, min, max);
                break;
                
            case "opcion_unica":
                @SuppressWarnings("unchecked")
                List<String> opcionesU = (List<String>) atributos.get("opciones");
                if (opcionesU == null || opcionesU.isEmpty()) {
                    throw new IllegalArgumentException("Pregunta de opción única requiere opciones");
                }
                p = new PreguntaOpcionUnica(id, idEncuesta, enunciado, opcionesU);
                break;
                
            case "opcion_multiple":
                @SuppressWarnings("unchecked")
                List<String> opcionesM = (List<String>) atributos.get("opciones");
                if (opcionesM == null || opcionesM.isEmpty()) {
                    throw new IllegalArgumentException("Pregunta de opción múltiple requiere opciones");
                }
                p = new PreguntaOpcionMultiple(id, idEncuesta, enunciado, opcionesM);
                break;
                
            default:
                throw new IllegalArgumentException("Tipo de pregunta desconocido: " + tipo);
        }
        
        preguntas.put(id, p);
        return id;
    }
    
    /**
     * Obtiene una pregunta por su ID.
     */
    public Pregunta getPregunta(int idPregunta) {
        Pregunta p = preguntas.get(idPregunta);
        if (p == null) {
            throw new IllegalArgumentException("Pregunta con ID " + idPregunta + " no existe");
        }
        return p;
    }
    
    /**
     * Verifica si existe una pregunta.
     */
    public boolean existePregunta(int idPregunta) {
        return preguntas.containsKey(idPregunta);
    }
    
    /**
     * Elimina una pregunta.
     */
    public void eliminar(Pregunta p) {
        preguntas.remove(p.getId());
    }
}
```

---

### 4. CtrlRespuesta.java ⚠️ ACTUALIZAR: int idUsuario → String nomUsuario

**CAMBIO REQUERIDO**: Parámetro `String nomUsuario` en lugar de `int idUsuario`.

```java
package classcontroller;

import domain.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CtrlRespuesta {
    
    private static CtrlRespuesta singletonObject;
    private Map<Integer, Respuesta> respuestas;
    private int nextId;
    
    private CtrlRespuesta() {
        respuestas = new HashMap<>();
        nextId = 1;
    }
    
    public static CtrlRespuesta getInstance() {
        if (singletonObject == null)
            singletonObject = new CtrlRespuesta();
        return singletonObject;
    }
    
    // ========== MÉTODOS FALTANTES ==========
    
    /**
     * Crea una respuesta polimórfica según el tipo de pregunta.
     * Factory Method Pattern.
     * ⚠️ CAMBIO: String nomUsuario en lugar de int idUsuario
     * 
     * @param nomUsuario Nombre del usuario que responde
     * @param idPregunta ID de la pregunta
     * @param contenido Contenido de la respuesta (tipo genérico)
     * @return ID de la respuesta creada
     */
    public <T> int crearRespuesta(String nomUsuario, int idPregunta, T contenido) {
        // Obtener pregunta para determinar el tipo
        // NOTA: Aquí habría que inyectar CtrlPregunta o usar otro enfoque
        // Para simplicidad, asumimos que validación se hace en la clase Respuesta
        
        int id = nextId++;
        Respuesta r;
        
        // Determinar tipo de respuesta según tipo de contenido
        // ⚠️ Usando nomUsuario (String) en lugar de idUsuario (int)
        if (contenido instanceof String) {
            r = new RespuestaLibre(id, nomUsuario, idPregunta, (String) contenido);
        } else if (contenido instanceof Number) {
            r = new RespuestaNumerica(id, nomUsuario, idPregunta, 
                                     ((Number) contenido).doubleValue());
        } else if (contenido instanceof Integer) {
            // Asumiendo opción única (índice)
            r = new RespuestaOpcionUnica(id, nomUsuario, idPregunta, (Integer) contenido);
        } else if (contenido instanceof int[]) {
            // Asumiendo opción múltiple (array de índices)
            r = new RespuestaOpcionMultiple(id, nomUsuario, idPregunta, (int[]) contenido);
        } else {
            throw new IllegalArgumentException("Tipo de respuesta no soportado");
        }
        
        respuestas.put(id, r);
        return id;
    }
    
    /**
     * Obtiene una respuesta por su ID.
     */
    public Respuesta getRespuesta(int idRespuesta) {
        Respuesta r = respuestas.get(idRespuesta);
        if (r == null) {
            throw new IllegalArgumentException("Respuesta con ID " + idRespuesta + " no existe");
        }
        return r;
    }
    
    /**
     * Verifica si existe una respuesta.
     */
    public boolean existeRespuesta(int idRespuesta) {
        return respuestas.containsKey(idRespuesta);
    }
    
    /**
     * Elimina una respuesta.
     */
    public void eliminar(Respuesta r) {
        respuestas.remove(r.getId());
    }
    
    /**
     * Helper para determinar si una pregunta es de opción única.
     * NOTA: En implementación real, consultar CtrlPregunta.
     */
    private boolean isOpcionUnica(int idPregunta) {
        // TODO: Implementar consultando CtrlPregunta
        return false;
    }
}
```

---

## 🟡 IMPORTANTE: Métodos Faltantes en Clases de Dominio

### 1. Usuario.java

```java
// Añadir a la clase Usuario:

private int id;

public Usuario(int id, String nomUsuario, String contrasena) {
    this.id = id;
    this.nomUsuario = nomUsuario;
    this.contrasena = contrasena;
    this.idPerfiles = new ArrayList<>();
    this.idEncuestasCreadas = new ArrayList<>();
    this.idRespuestas = new ArrayList<>();
}

public int getId() {
    return id;
}

public void setContrasena(String nuevaContrasena) {
    if (this.contrasena.equals(nuevaContrasena)) {
        throw new IllegalStateException("La nueva contraseña es igual a la actual");
    }
    this.contrasena = nuevaContrasena;
}

public void guardarIdEncuesta(int idEncuesta) {
    if (!idEncuestasCreadas.contains(idEncuesta)) {
        idEncuestasCreadas.add(idEncuesta);
    }
}

public void eliminarIdEncuestaCreada(int idEncuesta) {
    idEncuestasCreadas.remove(Integer.valueOf(idEncuesta));
}

public void guardarIdRespuesta(int idRespuesta) {
    if (!idRespuestas.contains(idRespuesta)) {
        idRespuestas.add(idRespuesta);
    }
}

public void eliminarIdRespuesta(int idRespuesta) {
    idRespuestas.remove(Integer.valueOf(idRespuesta));
}

public List<Integer> getIdRespuestas() {
    return new ArrayList<>(idRespuestas);
}

public List<Integer> getIdPerfiles() {
    return new ArrayList<>(idPerfiles);
}
```

---

### 2. Encuesta.java

```java
// Corregir y añadir a la clase Encuesta:

private int idEncuesta;
private String titulo;
private int idCreador; // En lugar de Usuario completo
private ArrayList<Integer> idPreguntas;

public Encuesta(int idEncuesta, String titulo, int idCreador) {
    this.idEncuesta = idEncuesta;
    this.titulo = titulo;
    this.idCreador = idCreador;
    this.idPreguntas = new ArrayList<>();
    
    if (titulo == null || titulo.trim().isEmpty()) {
        throw new IllegalArgumentException("Nombre de encuesta vacío");
    }
}

public int getIdEncuesta() {
    return idEncuesta;
}

public String getTitulo() {
    return titulo;
}

public void cambiarTitulo(String nuevoTitulo) {
    if (nuevoTitulo == null || nuevoTitulo.trim().isEmpty()) {
        throw new IllegalArgumentException("Título no puede estar vacío");
    }
    this.titulo = nuevoTitulo;
}

public int getIdCreador() {
    return idCreador;
}

public List<Integer> getIdPreguntas() {
    return new ArrayList<>(idPreguntas);
}

public void guardarIdPregunta(int idPregunta) {
    if (!idPreguntas.contains(idPregunta)) {
        idPreguntas.add(idPregunta);
    }
}

public void eliminarIdPregunta(int idPregunta) {
    idPreguntas.remove(Integer.valueOf(idPregunta));
}
```

---

### 3. Pregunta.java (abstracta)

```java
// Corregir y añadir:

private int id;
private int idEncuesta;
private String enunciado;
private ArrayList<Integer> idRespuestas;

public Pregunta(int id, int idEncuesta, String enunciado) {
    this.id = id;
    this.idEncuesta = idEncuesta;
    this.enunciado = enunciado;
    this.idRespuestas = new ArrayList<>();
}

public int getId() {
    return id;
}

public int getIdEncuesta() {
    return idEncuesta;
}

public String getEnunciado() {
    return enunciado;
}

public List<Integer> getIdRespuestas() {
    return new ArrayList<>(idRespuestas);
}

public void guardarIdRespuesta(int idRespuesta) {
    if (!idRespuestas.contains(idRespuesta)) {
        idRespuestas.add(idRespuesta);
    }
}

public void eliminarIdRespuesta(int idRespuesta) {
    idRespuestas.remove(Integer.valueOf(idRespuesta));
}

// Métodos abstractos para subclases
public abstract String getTipo();
public abstract boolean validarRespuesta(Object respuesta);
```

---

### 4. Respuesta.java (abstracta)

```java
// Añadir:

private int id;
private int idUsuario;
private int idPregunta;

public Respuesta(int id, int idUsuario, int idPregunta) {
    this.id = id;
    this.idUsuario = idUsuario;
    this.idPregunta = idPregunta;
}

public int getId() {
    return id;
}

public int getIdUsuario() {
    return idUsuario;
}

public int getIdPregunta() {
    return idPregunta;
}

// Métodos abstractos
public abstract Object getRespuesta();
public abstract String getTipo();
public abstract void modificar(Object nuevaRespuesta);
```

---

### 5. Subclases de Pregunta

```java
// PreguntaLibre.java
public class PreguntaLibre extends Pregunta {
    public PreguntaLibre(int id, int idEncuesta, String enunciado) {
        super(id, idEncuesta, enunciado);
    }
    
    @Override
    public String getTipo() {
        return "libre";
    }
    
    @Override
    public boolean validarRespuesta(Object respuesta) {
        return respuesta instanceof String;
    }
}

// PreguntaNumerica.java
public class PreguntaNumerica extends Pregunta {
    private double min;
    private double max;
    
    public PreguntaNumerica(int id, int idEncuesta, String enunciado, 
                           double min, double max) {
        super(id, idEncuesta, enunciado);
        this.min = min;
        this.max = max;
    }
    
    @Override
    public String getTipo() {
        return "numerica";
    }
    
    @Override
    public boolean validarRespuesta(Object respuesta) {
        if (!(respuesta instanceof Number)) return false;
        double valor = ((Number) respuesta).doubleValue();
        return valor >= min && valor <= max;
    }
    
    public double getMin() { return min; }
    public double getMax() { return max; }
}

// PreguntaOpcionUnica.java
public class PreguntaOpcionUnica extends Pregunta {
    private List<String> opciones;
    
    public PreguntaOpcionUnica(int id, int idEncuesta, String enunciado, 
                               List<String> opciones) {
        super(id, idEncuesta, enunciado);
        this.opciones = new ArrayList<>(opciones);
    }
    
    @Override
    public String getTipo() {
        return "opcion_unica";
    }
    
    @Override
    public boolean validarRespuesta(Object respuesta) {
        return respuesta instanceof String && opciones.contains(respuesta);
    }
    
    public List<String> getOpciones() {
        return new ArrayList<>(opciones);
    }
}

// PreguntaOpcionMultiple.java
public class PreguntaOpcionMultiple extends Pregunta {
    private List<String> opciones;
    
    public PreguntaOpcionMultiple(int id, int idEncuesta, String enunciado, 
                                  List<String> opciones) {
        super(id, idEncuesta, enunciado);
        this.opciones = new ArrayList<>(opciones);
    }
    
    @Override
    public String getTipo() {
        return "opcion_multiple";
    }
    
    @Override
    public boolean validarRespuesta(Object respuesta) {
        if (!(respuesta instanceof List)) return false;
        @SuppressWarnings("unchecked")
        List<String> seleccionadas = (List<String>) respuesta;
        return opciones.containsAll(seleccionadas);
    }
    
    public List<String> getOpciones() {
        return new ArrayList<>(opciones);
    }
}
```

---

### 6. Subclases de Respuesta

```java
// RespuestaLibre.java
public class RespuestaLibre extends Respuesta {
    private String texto;
    
    public RespuestaLibre(int id, int idUsuario, int idPregunta, String texto) {
        super(id, idUsuario, idPregunta);
        this.texto = texto;
    }
    
    @Override
    public Object getRespuesta() {
        return texto;
    }
    
    @Override
    public String getTipo() {
        return "libre";
    }
    
    @Override
    public void modificar(Object nuevaRespuesta) {
        if (!(nuevaRespuesta instanceof String)) {
            throw new IllegalArgumentException("Tipo incorrecto: esperado String");
        }
        this.texto = (String) nuevaRespuesta;
    }
}

// RespuestaNumerica.java
public class RespuestaNumerica extends Respuesta {
    private double valor;
    
    public RespuestaNumerica(int id, int idUsuario, int idPregunta, double valor) {
        super(id, idUsuario, idPregunta);
        this.valor = valor;
    }
    
    @Override
    public Object getRespuesta() {
        return valor;
    }
    
    @Override
    public String getTipo() {
        return "numerica";
    }
    
    @Override
    public void modificar(Object nuevaRespuesta) {
        if (!(nuevaRespuesta instanceof Number)) {
            throw new IllegalArgumentException("Tipo incorrecto: esperado Number");
        }
        this.valor = ((Number) nuevaRespuesta).doubleValue();
    }
}

// RespuestaOpcionUnica.java
public class RespuestaOpcionUnica extends Respuesta {
    private String opcionSeleccionada;
    
    public RespuestaOpcionUnica(int id, int idUsuario, int idPregunta, 
                                String opcion) {
        super(id, idUsuario, idPregunta);
        this.opcionSeleccionada = opcion;
    }
    
    @Override
    public Object getRespuesta() {
        return opcionSeleccionada;
    }
    
    @Override
    public String getTipo() {
        return "opcion_unica";
    }
    
    @Override
    public void modificar(Object nuevaRespuesta) {
        if (!(nuevaRespuesta instanceof String)) {
            throw new IllegalArgumentException("Tipo incorrecto: esperado String");
        }
        this.opcionSeleccionada = (String) nuevaRespuesta;
    }
}

// RespuestaOpcionMultiple.java
public class RespuestaOpcionMultiple extends Respuesta {
    private List<String> opcionesSeleccionadas;
    
    public RespuestaOpcionMultiple(int id, int idUsuario, int idPregunta, 
                                   List<String> opciones) {
        super(id, idUsuario, idPregunta);
        this.opcionesSeleccionadas = new ArrayList<>(opciones);
    }
    
    @Override
    public Object getRespuesta() {
        return new ArrayList<>(opcionesSeleccionadas);
    }
    
    @Override
    public String getTipo() {
        return "opcion_multiple";
    }
    
    @Override
    public void modificar(Object nuevaRespuesta) {
        if (!(nuevaRespuesta instanceof List)) {
            throw new IllegalArgumentException("Tipo incorrecto: esperado List<String>");
        }
        @SuppressWarnings("unchecked")
        List<String> nuevasOpciones = (List<String>) nuevaRespuesta;
        this.opcionesSeleccionadas = new ArrayList<>(nuevasOpciones);
    }
}
```

---

## 🟢 OPCIONAL: CtrlPersistence.java

```java
package persistence;

import domain.*;
import java.util.HashMap;
import java.util.Map;

public class CtrlPersistence {
    
    private static CtrlPersistence singletonObject;
    
    // Almacenamiento en memoria (en producción sería BD o archivos)
    private Map<Integer, Usuario> usuariosDB;
    private Map<Integer, Encuesta> encuestasDB;
    private Map<Integer, Pregunta> preguntasDB;
    private Map<Integer, Respuesta> respuestasDB;
    
    private CtrlPersistence() {
        usuariosDB = new HashMap<>();
        encuestasDB = new HashMap<>();
        preguntasDB = new HashMap<>();
        respuestasDB = new HashMap<>();
    }
    
    public static CtrlPersistence getInstace() {
        if (singletonObject == null)
            singletonObject = new CtrlPersistence();
        return singletonObject;
    }
    
    // ========== USUARIOS ==========
    
    public void guardarUsuario(Usuario u) {
        if (usuariosDB.values().stream()
            .anyMatch(us -> us.getNomUsuario().equals(u.getNomUsuario()))) {
            throw new IllegalStateException("Ya existe un usuario con el nombre " + 
                                           u.getNomUsuario());
        }
        usuariosDB.put(u.getId(), u);
    }
    
    public Usuario cargarUsuario(String nombre) {
        return usuariosDB.values().stream()
            .filter(u -> u.getNomUsuario().equals(nombre))
            .findFirst()
            .orElse(null);
    }
    
    public void actualizarUsuario(Usuario u) {
        usuariosDB.put(u.getId(), u);
    }
    
    public void eliminarUsuario(int idUsuario) {
        usuariosDB.remove(idUsuario);
    }
    
    // ========== ENCUESTAS ==========
    
    public void guardarEncuesta(Encuesta e) {
        encuestasDB.put(e.getIdEncuesta(), e);
    }
    
    public void actualizarEncuesta(Encuesta e) {
        encuestasDB.put(e.getIdEncuesta(), e);
    }
    
    public void eliminarEncuesta(int idEncuesta) {
        encuestasDB.remove(idEncuesta);
    }
    
    // ========== PREGUNTAS ==========
    
    public void guardarPregunta(Pregunta p) {
        preguntasDB.put(p.getId(), p);
    }
    
    public void actualizarPregunta(Pregunta p) {
        preguntasDB.put(p.getId(), p);
    }
    
    public void eliminarPregunta(int idPregunta) {
        preguntasDB.remove(idPregunta);
    }
    
    // ========== RESPUESTAS ==========
    
    public void guardarRespuesta(Respuesta r) {
        respuestasDB.put(r.getId(), r);
    }
    
    public void actualizarRespuesta(Respuesta r) {
        respuestasDB.put(r.getId(), r);
    }
    
    public void eliminarRespuesta(int idRespuesta) {
        respuestasDB.remove(idRespuesta);
    }
}
```

---

## 📊 Prioridades de Implementación

### 1️⃣ ALTA PRIORIDAD (Sistema funcional básico)
- ✅ CtrlUsuario completo
- ✅ CtrlEncuesta completo
- ✅ Usuario.java con métodos faltantes
- ✅ Encuesta.java con métodos faltantes

### 2️⃣ MEDIA PRIORIDAD (Funcionalidad de preguntas/respuestas)
- ✅ CtrlPregunta con factory methods
- ✅ CtrlRespuesta con factory methods
- ✅ Pregunta.java y subclases completas
- ✅ Respuesta.java y subclases completas

### 3️⃣ BAJA PRIORIDAD (Persistencia y funcionalidades avanzadas)
- ⚪ CtrlPersistence completo
- ⚪ Importación/Exportación de archivos
- ⚪ Sistema de perfiles (clustering)

---

## 🧪 Testing Recomendado

Después de implementar, crear tests para:
1. Registro y login de usuarios
2. Creación de encuestas
3. Añadir preguntas de diferentes tipos
4. Responder preguntas
5. Eliminar en cascada
6. Validaciones de tipos

---

## 📌 Notas Finales

- Todos los métodos de CtrlDominio YA están implementados ✅
- Los controladores de clase necesitan las implementaciones de arriba
- Las clases de dominio necesitan los métodos auxiliares listados
- El sistema sigue principios SOLID y patrones de diseño correctos
