# Guia de Importacion: JSON y CSV

## 1. Importar Encuestas desde JSON

### Estructura esperada del JSON

```json
{
  "titulo": "Encuesta Habitos Deportivos",
  "preguntas": [
    {
      "tipo": "numerica",
      "enunciado": "Cuantas horas a la semana haces deporte?",
      "min": 0,
      "max": 40
    },
    {
      "tipo": "opcion_unica",
      "enunciado": "Cual es tu nivel de actividad?",
      "ordenada": true,
      "opciones": ["Bajo", "Medio", "Alto"]
    },
    {
      "tipo": "opcion_multiple",
      "enunciado": "Que deportes practicas?",
      "opciones": ["Futbol", "Basquet", "Natacion", "Running"],
      "numMaxOpc": 3
    },
    {
      "tipo": "libre",
      "enunciado": "Describe tu rutina"
    }
  ]
}
```

### Codigo para importar

```java
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonArray;
import java.io.FileReader;

public void importarEncuestaJSON(String rutaJSON, String nomUsuario) {
    Gson gson = new Gson();
    JsonObject json = gson.fromJson(new FileReader(rutaJSON), JsonObject.class);
    
    // Crear encuesta
    String titulo = json.get("titulo").getAsString();
    int idEncuesta = ctrlDominioEncuesta.crearEncuesta(nomUsuario, titulo, ctrlUsuario);
    
    // Crear preguntas
    JsonArray preguntas = json.getAsJsonArray("preguntas");
    for (int i = 0; i < preguntas.size(); i++) {
        JsonObject p = preguntas.get(i).getAsJsonObject();
        String tipo = p.get("tipo").getAsString();
        String enunciado = p.get("enunciado").getAsString();
        
        switch (tipo) {
            case "numerica":
                int min = p.get("min").getAsInt();
                int max = p.get("max").getAsInt();
                ctrlDominioPregunta.crearPreguntaNumerica(idEncuesta, enunciado, 
                    min, max, ctrlEncuesta);
                break;
                
            case "opcion_unica":
                boolean ordenada = p.get("ordenada").getAsBoolean();
                List<String> opcionesOU = new ArrayList<>();
                JsonArray opcs = p.getAsJsonArray("opciones");
                for (int j = 0; j < opcs.size(); j++) {
                    opcionesOU.add(opcs.get(j).getAsString());
                }
                ctrlDominioPregunta.crearPreguntaOpcionUnica(idEncuesta, enunciado, 
                    opcionesOU, ordenada, ctrlEncuesta);
                break;
                
            case "opcion_multiple":
                List<String> opcionesOM = new ArrayList<>();
                JsonArray opcsM = p.getAsJsonArray("opciones");
                for (int j = 0; j < opcsM.size(); j++) {
                    opcionesOM.add(opcsM.get(j).getAsString());
                }
                int numMaxOpc = p.get("numMaxOpc").getAsInt();
                ctrlDominioPregunta.crearPreguntaOpcionMultiple(idEncuesta, enunciado, 
                    opcionesOM, numMaxOpc, ctrlEncuesta);
                break;
                
            case "libre":
                ctrlDominioPregunta.crearPreguntaLibre(idEncuesta, enunciado, ctrlEncuesta);
                break;
        }
    }
}
```

### Libreria GSON

Ya la teneis en el proyecto (en `build.gradle` o `lib/`). Si no:

```gradle
dependencies {
    implementation 'com.google.code.gson:gson:2.8.9'
}
```

---

## 2. Importar Respuestas desde CSV

### Estructura esperada del CSV

**Formato 1 (una respuesta por linea):**
```csv
usuario,idPregunta,respuesta
ana,1,5.5
pol,1,3.0
ana,2,1
pol,2,0
ana,3,"0,2,4"
pol,4,Hago running por las mananas
```

**Formato 2 (matriz de respuestas):**
```csv
usuario,pregunta1,pregunta2,pregunta3,pregunta4
ana,5.5,1,"0,2,4",Hago running
pol,3.0,0,"1,3",Voy al gimnasio
joan,7.0,2,"0,1,2,3",Natacion y ciclismo
```

### Codigo para importar (Formato 1)

```java
import java.io.BufferedReader;
import java.io.FileReader;

public void importarRespuestasCSV(String rutaCSV) {
    BufferedReader br = new BufferedReader(new FileReader(rutaCSV));
    String linea;
    
    // Saltar cabecera
    br.readLine();
    
    while ((linea = br.readLine()) != null) {
        String[] partes = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)"); // Split respetando comillas
        
        String usuario = partes[0].trim();
        int idPregunta = Integer.parseInt(partes[1].trim());
        String respuestaStr = partes[2].trim().replace("\"", "");
        
        // Obtener tipo de pregunta
        Pregunta p = ctrlPregunta.getPregunta(idPregunta);
        
        if (p instanceof PreguntaNumerica) {
            double valor = Double.parseDouble(respuestaStr);
            ctrlRespuesta.darRespuesta(usuario, idPregunta, valor, 
                ctrlUsuario, ctrlPregunta);
        }
        else if (p instanceof PreguntaOpcionUnica) {
            int indice = Integer.parseInt(respuestaStr);
            ctrlRespuesta.darRespuesta(usuario, idPregunta, indice, 
                ctrlUsuario, ctrlPregunta);
        }
        else if (p instanceof PreguntaOpcionMultiple) {
            String[] indicesStr = respuestaStr.split(",");
            int[] indices = new int[indicesStr.length];
            for (int i = 0; i < indicesStr.length; i++) {
                indices[i] = Integer.parseInt(indicesStr[i].trim());
            }
            ctrlRespuesta.darRespuesta(usuario, idPregunta, indices, 
                ctrlUsuario, ctrlPregunta);
        }
        else if (p instanceof PreguntaLibre) {
            ctrlRespuesta.darRespuesta(usuario, idPregunta, respuestaStr, 
                ctrlUsuario, ctrlPregunta);
        }
    }
    br.close();
}
```

### Codigo para importar (Formato 2 - Matriz)

```java
public void importarRespuestasMatriz(String rutaCSV, List<Integer> idsPreguntas) {
    BufferedReader br = new BufferedReader(new FileReader(rutaCSV));
    
    // Saltar cabecera
    br.readLine();
    
    String linea;
    while ((linea = br.readLine()) != null) {
        String[] partes = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
        
        String usuario = partes[0].trim();
        
        // Procesar cada pregunta
        for (int i = 1; i < partes.length && i-1 < idsPreguntas.size(); i++) {
            int idPregunta = idsPreguntas.get(i-1);
            String respuestaStr = partes[i].trim().replace("\"", "");
            
            if (respuestaStr.isEmpty()) continue; // Saltar respuestas vacias
            
            Pregunta p = ctrlPregunta.getPregunta(idPregunta);
            
            // Mismo procesamiento que antes segun tipo
            if (p instanceof PreguntaNumerica) {
                double valor = Double.parseDouble(respuestaStr);
                ctrlRespuesta.darRespuesta(usuario, idPregunta, valor, 
                    ctrlUsuario, ctrlPregunta);
            }
            // ... resto igual
        }
    }
    br.close();
}
```

---

## 3. Como Tratar los CSV que te Dan

### Paso 1: Analizar el formato

Abre el CSV y mira:
- ¿Tiene cabecera? (primera linea con nombres de columnas)
- ¿Que columnas tiene? (usuario, idPregunta, respuesta?)
- ¿Usa comillas para texto con comas? ("opcion1,opcion2")
- ¿Que separador usa? (coma, punto y coma, tab)

### Paso 2: Adaptar el split

```java
// Coma normal
String[] partes = linea.split(",");

// Punto y coma
String[] partes = linea.split(";");

// Respeta comillas (para arrays en una celda)
String[] partes = linea.split(",(?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

// Tab
String[] partes = linea.split("\t");
```

### Paso 3: Limpiar datos

```java
// Quitar espacios
String dato = partes[i].trim();

// Quitar comillas
String dato = partes[i].replace("\"", "");

// Quitar comillas solo en los extremos
String dato = partes[i].trim();
if (dato.startsWith("\"") && dato.endsWith("\"")) {
    dato = dato.substring(1, dato.length()-1);
}
```

### Paso 4: Manejar errores

```java
try {
    // ... codigo de importacion
} catch (NumberFormatException e) {
    System.err.println("Error en linea " + numLinea + ": formato numerico incorrecto");
} catch (ArrayIndexOutOfBoundsException e) {
    System.err.println("Error en linea " + numLinea + ": faltan columnas");
} catch (IllegalArgumentException e) {
    System.err.println("Error en linea " + numLinea + ": " + e.getMessage());
}
```

---

## 4. Ejemplo Completo: DriverImportacion

```java
package drivers;

import java.io.*;
import java.util.*;
import com.google.gson.*;
import domain.*;
import domaincontrollers.*;

public class DriverImportacion {
    
    private static CtrlDominioUsuario ctrlUsuario;
    private static CtrlDominioEncuesta ctrlEncuesta;
    private static CtrlDominioPregunta ctrlPregunta;
    private static CtrlDominioRespuesta ctrlRespuesta;
    
    public static void main(String[] args) {
        inicializarControladores();
        
        Scanner scanner = new Scanner(System.in);
        System.out.println("=== Importador de Encuestas y Respuestas ===");
        System.out.println("1. Importar encuesta desde JSON");
        System.out.println("2. Importar respuestas desde CSV");
        System.out.print("Opcion: ");
        
        String opcion = scanner.nextLine();
        
        if (opcion.equals("1")) {
            System.out.print("Ruta del JSON: ");
            String ruta = scanner.nextLine();
            System.out.print("Usuario creador: ");
            String usuario = scanner.nextLine();
            
            try {
                importarEncuestaJSON(ruta, usuario);
                System.out.println("Encuesta importada correctamente!");
            } catch (Exception e) {
                System.err.println("ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }
        else if (opcion.equals("2")) {
            System.out.print("Ruta del CSV: ");
            String ruta = scanner.nextLine();
            
            try {
                importarRespuestasCSV(ruta);
                System.out.println("Respuestas importadas correctamente!");
            } catch (Exception e) {
                System.err.println("ERROR: " + e.getMessage());
                e.printStackTrace();
            }
        }
        
        scanner.close();
    }
    
    private static void inicializarControladores() {
        ctrlUsuario = new CtrlDominioUsuario();
        ctrlEncuesta = new CtrlDominioEncuesta();
        ctrlPregunta = new CtrlDominioPregunta();
        ctrlRespuesta = new CtrlDominioRespuesta();
    }
    
    // Aqui van los metodos importarEncuestaJSON() e importarRespuestasCSV()
}
```

---

## 5. Tips y Trucos

### Detectar el tipo de pregunta sin saber el ID

Si el CSV no tiene IDs de pregunta, solo indices:

```java
List<Pregunta> preguntas = encuesta.getPreguntas(); // Asume que tienes este metodo
Pregunta p = preguntas.get(indicePregunta);
```

### Validar antes de importar

```java
// Contar lineas primero
int totalLineas = contarLineas(rutaCSV);
System.out.println("Se importaran " + totalLineas + " respuestas");

// Importar con progreso
for (int i = 0; i < totalLineas; i++) {
    // ... importar linea
    if (i % 100 == 0) {
        System.out.println("Procesadas " + i + "/" + totalLineas);
    }
}
```

### Encodings raros

Si ves caracteres raros (Ã±, Ã©):

```java
BufferedReader br = new BufferedReader(
    new InputStreamReader(new FileInputStream(rutaCSV), "UTF-8")
);
```

### Excel exporta con punto y coma

Si el CSV viene de Excel en español:

```java
String[] partes = linea.split(";"); // No coma
double valor = Double.parseDouble(respuestaStr.replace(",", ".")); // Coma decimal
```

---

## Resumen Rapido

1. **JSON** → usa GSON, parsea objeto por objeto
2. **CSV simple** → split por comas, procesa linea por linea
3. **CSV matriz** → primera columna = usuario, resto = respuestas a preguntas en orden
4. **Errores comunes**: comillas, comas decimales, encoding, columnas vacias
5. **Siempre**: try-catch, validar tipos, mostrar progreso

Suerte!
