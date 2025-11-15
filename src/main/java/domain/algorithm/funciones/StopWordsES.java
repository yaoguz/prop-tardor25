package domain.algorithm.funciones;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

/**
 * @author Alessandro Jin
 */

public class StopWordsES {
    
    /**
     * Conjunto completo de stop words en español
     * Incluye: artículos, preposiciones, conjunciones, pronombres,
     * verbos auxiliares, adverbios comunes, etc.
     */
    public static final Set<String> STOP_WORDS = new HashSet<>(Arrays.asList(
        
        // ============ ARTÍCULOS ============
        "el", "la", "lo", "los", "las",
        "un", "una", "unos", "unas",
        
        // ============ PREPOSICIONES ============
        "a", "ante", "bajo", "cabe", "con", "contra", "de", "del", 
        "desde", "durante", "en", "entre", "hacia", "hasta", "mediante",
        "para", "por", "según", "segun", "sin", "so", "sobre", "tras",
        "versus", "via", "vía", "al",
        
        // ============ CONJUNCIONES ============
        "y", "e", "ni", "que", "o", "u", 
        "pero", "mas", "aunque", "sino", "si",
        "porque", "pues", "puesto", "luego",
        "conque", "así", "asique", "mientras",
        "cuando", "donde", "apenas",
        
        // ============ PRONOMBRES PERSONALES ============
        "yo", "tú", "tu", "él", "el", "ella", "ello",
        "nosotros", "nosotras", "vosotros", "vosotras",
        "ellos", "ellas", "usted", "ustedes",
        
        // ============ PRONOMBRES ÁTONOS ============
        "me", "te", "se", "le", "lo", "la", "les", "nos", "os",
        
        // ============ PRONOMBRES POSESIVOS ============
        "mi", "mis", "mí", "mio", "mío", "mia", "mía", "mios", "míos", "mias", "mías",
        "tu", "tus", "tuyo", "tuya", "tuyos", "tuyas",
        "su", "sus", "suyo", "suya", "suyos", "suyas",
        "nuestro", "nuestra", "nuestros", "nuestras",
        "vuestro", "vuestra", "vuestros", "vuestras",
        
        // ============ PRONOMBRES DEMOSTRATIVOS ============
        "este", "esta", "esto", "estos", "estas",
        "ese", "esa", "eso", "esos", "esas",
        "aquel", "aquella", "aquello", "aquellos", "aquellas",
        
        // ============ PRONOMBRES/ADVERBIOS INTERROGATIVOS ============
        "qué", "que", "quién", "quien", "quiénes", "quienes",
        "cuál", "cual", "cuáles", "cuales",
        "cuánto", "cuanto", "cuánta", "cuanta", "cuántos", "cuantos", "cuántas", "cuantas",
        "cómo", "como", "dónde", "donde", "cuándo", "cuando",
        "adónde", "adonde", "por qué", "porque", "porqué",
        
        // ============ VERBO SER (todas las conjugaciones) ============
        "ser", "soy", "eres", "es", "somos", "sois", "son",
        "era", "eras", "éramos", "erais", "eran",
        "fui", "fuiste", "fue", "fuimos", "fuisteis", "fueron",
        "seré", "serás", "será", "seremos", "seréis", "serán",
        "sería", "serías", "seriamos", "seríamos", "seriais", "seríais", "serían",
        "sea", "seas", "seamos", "seáis", "seais", "sean",
        "fuera", "fueras", "fuéramos", "fueramos", "fuerais", "fueran",
        "fuese", "fueses", "fuésemos", "fuesemos", "fueseis", "fuesen",
        "sido", "siendo",
        
        // ============ VERBO ESTAR (todas las conjugaciones) ============
        "estar", "estoy", "estás", "estas", "está", "esta", "estamos", "estáis", "estais", "están", "estan",
        "estaba", "estabas", "estábamos", "estabamos", "estabais", "estaban",
        "estuve", "estuviste", "estuvo", "estuvimos", "estuvisteis", "estuvieron",
        "estaré", "estarás", "estaras", "estará", "estara", "estaremos", "estaréis", "estareis", "estarán", "estaran",
        "estaría", "estarías", "estaria", "estarias", "estaríamos", "estariamos", "estaríais", "estariais", "estarían", "estarian",
        "esté", "este", "estés", "estes", "estemos", "estéis", "esteis", "estén", "esten",
        "estuviera", "estuvieras", "estuviéramos", "estuvieramos", "estuvierais", "estuvieran",
        "estuviese", "estuvieses", "estuviésemos", "estuviesemos", "estuvieseis", "estuviesen",
        "estado", "estando",
        
        // ============ VERBO HABER (todas las conjugaciones) ============
        "haber", "he", "has", "ha", "hemos", "habéis", "habeis", "han",
        "había", "habia", "habías", "habias", "habíamos", "habiamos", "habíais", "habiais", "habían", "habian",
        "hube", "hubiste", "hubo", "hubimos", "hubisteis", "hubieron",
        "habré", "habre", "habrás", "habras", "habrá", "habra", "habremos", "habréis", "habreis", "habrán", "habran",
        "habría", "habria", "habrías", "habrias", "habríamos", "habriamos", "habríais", "habriais", "habrían", "habrian",
        "haya", "hayas", "hayamos", "hayáis", "hayais", "hayan",
        "hubiera", "hubieras", "hubiéramos", "hubieramos", "hubierais", "hubieran",
        "hubiese", "hubieses", "hubiésemos", "hubiesemos", "hubieseis", "hubiesen",
        "habido", "habiendo", "hay",
        
        // ============ VERBO IR (todas las conjugaciones) ============
        "ir", "voy", "vas", "va", "vamos", "vais", "van",
        "iba", "ibas", "íbamos", "ibamos", "ibais", "iban",
        "fui", "fuiste", "fue", "fuimos", "fuisteis", "fueron",
        "iré", "ire", "irás", "iras", "irá", "ira", "iremos", "iréis", "ireis", "irán", "iran",
        "iría", "iria", "irías", "irias", "iríamos", "iriamos", "iríais", "iriais", "irían", "irian",
        "vaya", "vayas", "vayamos", "vayáis", "vayais", "vayan",
        "fuera", "fueras", "fuéramos", "fueramos", "fuerais", "fueran",
        "fuese", "fueses", "fuésemos", "fuesemos", "fueseis", "fuesen",
        "ido", "yendo",
        
        // ============ VERBO HACER ============
        "hacer", "hago", "haces", "hace", "hacemos", "hacéis", "haceis", "hacen",
        "hacía", "hacia", "hacías", "hacias", "hacíamos", "haciamos", "hacíais", "haciais", "hacían", "hacian",
        "hice", "hiciste", "hizo", "hicimos", "hicisteis", "hicieron",
        "haré", "hare", "harás", "haras", "hará", "hara", "haremos", "haréis", "hareis", "harán", "haran",
        "haría", "haria", "harías", "harias", "haríamos", "hariamos", "haríais", "hariais", "harían", "harian",
        "haga", "hagas", "hagamos", "hagáis", "hagais", "hagan",
        "hiciera", "hicieras", "hiciéramos", "hicieramos", "hicierais", "hicieran",
        "hiciese", "hicieses", "hiciésemos", "hiciesemos", "hicieseis", "hiciesen",
        "hecho", "haciendo",
        
        // ============ VERBO TENER ============
        "tener", "tengo", "tienes", "tiene", "tenemos", "tenéis", "teneis", "tienen",
        "tenía", "tenia", "tenías", "tenias", "teníamos", "teniamos", "teníais", "teniais", "tenían", "tenian",
        "tuve", "tuviste", "tuvo", "tuvimos", "tuvisteis", "tuvieron",
        "tendré", "tendre", "tendrás", "tendras", "tendrá", "tendra", "tendremos", "tendréis", "tendreis", "tendrán", "tendran",
        "tendría", "tendria", "tendrías", "tendrias", "tendríamos", "tendriamos", "tendríais", "tendriais", "tendrían", "tendrian",
        "tenga", "tengas", "tengamos", "tengáis", "tengais", "tengan",
        "tuviera", "tuvieras", "tuviéramos", "tuvieramos", "tuvierais", "tuvieran",
        "tuviese", "tuvieses", "tuviésemos", "tuviesemos", "tuvieseis", "tuviesen",
        "tenido", "teniendo",
        
        // ============ VERBOS MODALES ============
        "poder", "puedo", "puedes", "puede", "podemos", "podéis", "podeis", "pueden",
        "podía", "podia", "podías", "podias", "podíamos", "podiamos", "podíais", "podiais", "podían", "podian",
        "pude", "pudiste", "pudo", "pudimos", "pudisteis", "pudieron",
        "podré", "podre", "podrás", "podras", "podrá", "podra", "podremos", "podréis", "podreis", "podrán", "podran",
        "podría", "podria", "podrías", "podrias", "podríamos", "podriamos", "podríais", "podriais", "podrían", "podrian",
        "pueda", "puedas", "podamos", "podáis", "podais", "puedan",
        "pudiera", "pudieras", "pudiéramos", "pudieramos", "pudierais", "pudieran",
        "pudiese", "pudieses", "pudiésemos", "pudiesemos", "pudieseis", "pudiesen",
        "podido", "pudiendo",
        
        "deber", "debo", "debes", "debe", "debemos", "debéis", "debeis", "deben",
        "debía", "debia", "debías", "debias", "debíamos", "debiamos", "debíais", "debiais", "debían", "debian",
        "debí", "debi", "debiste", "debió", "debio", "debimos", "debisteis", "debieron",
        "deberé", "debere", "deberás", "deberas", "deberá", "debera", "deberemos", "deberéis", "debereis", "deberán", "deberan",
        "debería", "deberia", "deberías", "deberias", "deberíamos", "deberiamos", "deberíais", "deberiais", "deberían", "deberian",
        "deba", "debas", "debamos", "debáis", "debais", "deban",
        "debido", "debiendo",
        
        "querer", "quiero", "quieres", "quiere", "queremos", "queréis", "quereis", "quieren",
        "quería", "queria", "querías", "querias", "queríamos", "queriamos", "queríais", "queriais", "querían", "querian",
        "quise", "quisiste", "quiso", "quisimos", "quisisteis", "quisieron",
        "querré", "querre", "querrás", "querras", "querrá", "querra", "querremos", "querréis", "querreis", "querrán", "querran",
        "querría", "querria", "querrías", "querrias", "querríamos", "querriamos", "querríais", "querriais", "querrían", "querrian",
        "quiera", "quieras", "queramos", "queráis", "querais", "quieran",
        "quisiera", "quisieras", "quisiéramos", "quisieramos", "quisierais", "quisieran",
        "quisiese", "quisieses", "quisiésemos", "quisiesemos", "quisieseis", "quisiesen",
        "querido", "queriendo",
        
        // ============ ADVERBIOS DE CANTIDAD ============
        "muy", "más", "mas", "mucho", "muchos", "mucha", "muchas",
        "poco", "pocos", "poca", "pocas",
        "bastante", "bastantes", "demasiado", "demasiados", "demasiada", "demasiadas",
        "tanto", "tantos", "tanta", "tantas",
        "nada", "algo", "alguien", "nadie", "ninguno", "alguno",
        
        // ============ ADVERBIOS DE AFIRMACIÓN/NEGACIÓN ============
        "sí", "si", "no", "también", "tampoco", "jamás", "jamas", "nunca",
        
        // ============ ADVERBIOS TEMPORALES ============
        "ya", "todavía", "todavia", "aún", "aun",
        "siempre", "ahora", "hoy", "ayer", "mañana", "luego", "entonces",
        "antes", "después", "despues", "pronto", "tarde", "temprano",
        "recién", "recien", "recientemente",
        
        // ============ ADVERBIOS DE LUGAR ============
        "aquí", "aqui", "ahí", "ahi", "allí", "alli", "allá", "alla", "acá", "aca",
        "arriba", "abajo", "dentro", "fuera", "delante", "detrás", "detras",
        "cerca", "lejos", "encima", "debajo",
        
        // ============ ADVERBIOS DE MODO ============
        "bien", "mal", "mejor", "peor", "regular",
        "así", "asi", "tal", "tan",
        
        // ============ DETERMINANTES ============
        "algún", "algun", "alguno", "alguna", "algunos", "algunas",
        "ningún", "ningun", "ninguno", "ninguna", "ningunos", "ningunas",
        "todo", "toda", "todos", "todas",
        "otro", "otra", "otros", "otras",
        "mismo", "misma", "mismos", "mismas",
        "cierto", "cierta", "ciertos", "ciertas",
        "cada", "sendos", "sendas",
        "ambos", "ambas",
        
        // ============ PALABRAS RELACIONALES VACÍAS ============
        "vez", "veces", "cosa", "cosas",
        "parte", "partes", "forma", "formas",
        "modo", "modos", "manera", "maneras",
        "tipo", "tipos", "clase", "clases",
        "caso", "casos",
        
        // ============ NÚMEROS Y ORDINALES BÁSICOS ============
        "uno", "dos", "tres", "cuatro", "cinco",
        "primer", "primera", "primero", "segundo", "segunda",
        "tercer", "tercera", "tercero",
        "último", "ultima", "último", "últimos", "ultimos", "últimas", "ultimas",
        
        // ============ OTRAS PALABRAS FUNCIONALES ============
        "pues", "solamente", "solo", "sólo", "incluso",
        "además", "ademas", "casi", "apenas", "menos",
        "salvo", "excepto", "inclusive",
        "tal vez", "talvez", "quizá", "quiza", "quizás", "quizas", "acaso",
        "claro", "verdad", "cierto",
        
        // ============ INTERJECCIONES COMUNES ============
        "ah", "oh", "eh", "uy", "ay", "ojalá", "ojala"
    ));
    
    /**
     * Verifica si una palabra es una stop word
     */
    public static boolean isStopWord(String word) {
        if (word == null || word.isEmpty()) {
            return true;
        }
        return STOP_WORDS.contains(word.toLowerCase());
    }
    
} 
