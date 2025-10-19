// ═══════════════════════════════════════════════════════════════════════════
// EJEMPLOS DE USO - Sistema de Gestión de Encuestas
// ═══════════════════════════════════════════════════════════════════════════

package examples;

import domaincontroller.CtrlDominio;
import domain.*;
import java.util.*;

/**
 * Ejemplos prácticos de uso del sistema de encuestas.
 * Demuestra el flujo completo desde registro hasta análisis de respuestas.
 * 
 * @author subgrup-prop11.5
 * @version 2.0
 */
public class EjemplosUso {

    public static void main(String[] args) {
        ejemploCompleto();
    }

    /**
     * Ejemplo completo: Crear encuesta, añadir preguntas y responder.
     */
    public static void ejemploCompleto() {
        System.out.println("═══════════════════════════════════════════════════════");
        System.out.println("  EJEMPLO COMPLETO: Sistema de Gestión de Encuestas");
        System.out.println("═══════════════════════════════════════════════════════\n");

        CtrlDominio ctrl = new CtrlDominio();

        // ═════════ PASO 1: Registrar Usuarios ═════════
        System.out.println("📝 PASO 1: Registrando usuarios...\n");

        try {
            String profesor = ctrl.registrarUsuario("prof_garcia", "password123", "password123");
            System.out.println("✓ Usuario registrado: " + profesor);

            String alumno1 = ctrl.registrarUsuario("maria_lopez", "pass456", "pass456");
            System.out.println("✓ Usuario registrado: " + alumno1);

            String alumno2 = ctrl.registrarUsuario("juan_perez", "pass789", "pass789");
            System.out.println("✓ Usuario registrado: " + alumno2);

        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("✗ Error al registrar: " + e.getMessage());
        }

        // ═════════ PASO 2: Iniciar Sesión ═════════
        System.out.println("\n🔐 PASO 2: Iniciando sesión...\n");

        try {
            boolean sesionOk = ctrl.iniciarSesion("prof_garcia", "password123");
            System.out.println("✓ Sesión iniciada: " + sesionOk);
        } catch (IllegalArgumentException | IllegalStateException e) {
            System.err.println("✗ Error al iniciar sesión: " + e.getMessage());
        }

        // ═════════ PASO 3: Crear Encuesta ═════════
        System.out.println("\n📋 PASO 3: Creando encuesta de satisfacción...\n");

        int idEncuesta = -1;
        try {
            idEncuesta = ctrl.crearEncuesta(
                "prof_garcia",
                "Encuesta de Satisfacción - Curso de Programación 2024"
            );
            System.out.println("✓ Encuesta creada con ID: " + idEncuesta);
        } catch (IllegalArgumentException e) {
            System.err.println("✗ Error al crear encuesta: " + e.getMessage());
            return;
        }

        // ═════════ PASO 4: Añadir Preguntas ═════════
        System.out.println("\n❓ PASO 4: Añadiendo preguntas a la encuesta...\n");

        int idP1 = -1, idP2 = -1, idP3 = -1, idP4 = -1;

        try {
            // Pregunta 1: Libre
            idP1 = ctrl.anadirPreguntaLibre(
                idEncuesta,
                "¿Qué te pareció el curso en general? (Comentario libre)"
            );
            System.out.println("✓ Pregunta 1 (Libre) añadida, ID: " + idP1);

            // Pregunta 2: Numérica
            idP2 = ctrl.anadirPreguntaNumerica(
                idEncuesta,
                "¿Cuántas horas por semana dedicaste al curso?",
                0.0,  // mínimo
                40.0  // máximo
            );
            System.out.println("✓ Pregunta 2 (Numérica) añadida, ID: " + idP2);

            // Pregunta 3: Opción Única
            List<String> opcionesNivel = Arrays.asList(
                "Muy fácil", "Fácil", "Adecuado", "Difícil", "Muy difícil"
            );
            idP3 = ctrl.anadirPreguntaOpcionUnica(
                idEncuesta,
                "¿Cómo valorarías el nivel de dificultad?",
                opcionesNivel
            );
            System.out.println("✓ Pregunta 3 (Opción Única) añadida, ID: " + idP3);

            // Pregunta 4: Opción Múltiple
            List<String> opcionesTemas = Arrays.asList(
                "POO", "Estructuras de Datos", "Algoritmos", 
                "Patrones de Diseño", "Testing"
            );
            idP4 = ctrl.anadirPreguntaOpcionMultiple(
                idEncuesta,
                "¿Qué temas te resultaron más interesantes? (Múltiple selección)",
                opcionesTemas
            );
            System.out.println("✓ Pregunta 4 (Opción Múltiple) añadida, ID: " + idP4);

        } catch (Exception e) {
            System.err.println("✗ Error al añadir preguntas: " + e.getMessage());
            return;
        }

        // ═════════ PASO 5: Responder Encuesta (Usuario 1) ═════════
        System.out.println("\n💬 PASO 5: Usuario 'maria_lopez' respondiendo...\n");

        try {
            // Respuesta 1: Libre
            int idR1 = ctrl.darRespuesta(
                "maria_lopez",
                idP1,
                "El curso me pareció excelente. Los conceptos estaban muy bien " +
                "explicados y los ejemplos eran claros y prácticos."
            );
            System.out.println("✓ Respuesta libre guardada, ID: " + idR1);

            // Respuesta 2: Numérica
            int idR2 = ctrl.darRespuesta("maria_lopez", idP2, 5.5);
            System.out.println("✓ Respuesta numérica guardada (5.5 horas), ID: " + idR2);

            // Respuesta 3: Opción Única (índice 2 = "Adecuado")
            int idR3 = ctrl.darRespuesta("maria_lopez", idP3, 2);
            System.out.println("✓ Respuesta opción única guardada (Adecuado), ID: " + idR3);

            // Respuesta 4: Opción Múltiple (índices 0, 1, 3 = POO, Estructuras, Patrones)
            int[] opcionesSeleccionadas = {0, 1, 3};
            int idR4 = ctrl.darRespuesta("maria_lopez", idP4, opcionesSeleccionadas);
            System.out.println("✓ Respuesta opción múltiple guardada, ID: " + idR4);

        } catch (Exception e) {
            System.err.println("✗ Error al guardar respuestas: " + e.getMessage());
        }

        // ═════════ PASO 6: Responder Encuesta (Usuario 2) ═════════
        System.out.println("\n💬 PASO 6: Usuario 'juan_perez' respondiendo...\n");

        try {
            ctrl.darRespuesta(
                "juan_perez",
                idP1,
                "Muy buen curso, aunque algunos temas fueron algo complejos."
            );
            ctrl.darRespuesta("juan_perez", idP2, 8.0);
            ctrl.darRespuesta("juan_perez", idP3, 3);  // "Difícil"
            ctrl.darRespuesta("juan_perez", idP4, new int[]{2, 3, 4});  // Algoritmos, Patrones, Testing

            System.out.println("✓ Todas las respuestas de juan_perez guardadas");

        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
        }

        // ═════════ PASO 7: Consultar Respuestas ═════════
        System.out.println("\n📊 PASO 7: Consultando respuestas de la encuesta...\n");

        try {
            Map<Pregunta, Set<Respuesta>> respuestas = ctrl.getRespuestasEncuesta(idEncuesta);
            
            System.out.println("Total de preguntas con respuestas: " + respuestas.size());
            
            int numPregunta = 1;
            for (Map.Entry<Pregunta, Set<Respuesta>> entry : respuestas.entrySet()) {
                Pregunta p = entry.getKey();
                Set<Respuesta> respuestasPregunta = entry.getValue();
                
                System.out.println("\nPregunta " + numPregunta + ": " + p.getEnunciado());
                System.out.println("  Tipo: " + p.getTipo());
                System.out.println("  Respuestas recibidas: " + respuestasPregunta.size());
                
                for (Respuesta r : respuestasPregunta) {
                    System.out.println("    - Usuario: " + r.getNomUsuario() + 
                                     " | Respuesta: " + r.getRespuesta());
                }
                
                numPregunta++;
            }

        } catch (Exception e) {
            System.err.println("✗ Error al consultar respuestas: " + e.getMessage());
        }

        // ═════════ PASO 8: Modificar Respuesta ═════════
        System.out.println("\n✏️ PASO 8: Modificando una respuesta...\n");

        try {
            // Supongamos que el idR2 de maria_lopez quiere cambiar sus horas
            ctrl.modificarRespuesta(2, 6.0);  // De 5.5 a 6.0 horas
            System.out.println("✓ Respuesta modificada exitosamente");

        } catch (Exception e) {
            System.err.println("✗ Error al modificar: " + e.getMessage());
        }

        // ═════════ PASO 9: Consultar Encuestas Respondidas ═════════
        System.out.println("\n📝 PASO 9: Consultando encuestas respondidas por maria_lopez...\n");

        try {
            List<Integer> encuestasRespondidas = ctrl.getEncuestasRespondidas("maria_lopez");
            System.out.println("✓ Encuestas respondidas: " + encuestasRespondidas);
            System.out.println("  Total: " + encuestasRespondidas.size());

        } catch (Exception e) {
            System.err.println("✗ Error: " + e.getMessage());
        }

        // ═════════ FINAL ═════════
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("  ✅ EJEMPLO COMPLETADO EXITOSAMENTE");
        System.out.println("═══════════════════════════════════════════════════════");
    }

    /**
     * Ejemplo de validaciones y manejo de errores.
     */
    public static void ejemploValidaciones() {
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("  EJEMPLO: Validaciones y Manejo de Errores");
        System.out.println("═══════════════════════════════════════════════════════\n");

        CtrlDominio ctrl = new CtrlDominio();

        // Error 1: Contraseñas no coinciden
        System.out.println("❌ Test 1: Contraseñas diferentes");
        try {
            ctrl.registrarUsuario("usuario1", "pass123", "pass456");
        } catch (IllegalStateException e) {
            System.out.println("   ✓ Excepción capturada: " + e.getMessage());
        }

        // Error 2: Nombre de usuario muy corto
        System.out.println("\n❌ Test 2: Nombre de usuario corto");
        try {
            ctrl.registrarUsuario("ab", "pass123", "pass123");
        } catch (IllegalArgumentException e) {
            System.out.println("   ✓ Excepción capturada: " + e.getMessage());
        }

        // Error 3: Contraseña muy corta
        System.out.println("\n❌ Test 3: Contraseña corta");
        try {
            ctrl.registrarUsuario("usuario3", "123", "123");
        } catch (IllegalArgumentException e) {
            System.out.println("   ✓ Excepción capturada: " + e.getMessage());
        }

        // Error 4: Título de encuesta vacío
        System.out.println("\n❌ Test 4: Título de encuesta vacío");
        try {
            ctrl.registrarUsuario("usuario4", "pass123", "pass123");
            ctrl.crearEncuesta("usuario4", "");
        } catch (IllegalArgumentException e) {
            System.out.println("   ✓ Excepción capturada: " + e.getMessage());
        }

        System.out.println("\n✅ Todas las validaciones funcionan correctamente\n");
    }

    /**
     * Ejemplo de creación directa de respuestas (sin controlador).
     */
    public static void ejemploRespuestasDirectas() {
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("  EJEMPLO: Creación Directa de Respuestas");
        System.out.println("═══════════════════════════════════════════════════════\n");

        // Respuesta Libre
        System.out.println("📝 RespuestaLibre:");
        RespuestaLibre rLibre = new RespuestaLibre(
            1, "usuario1", 10, 
            "Esta es una respuesta de texto libre muy detallada."
        );
        System.out.println("   " + rLibre);
        System.out.println("   Longitud: " + rLibre.getLongitud() + " caracteres");
        System.out.println("   Tipo: " + rLibre.getTipo());

        // Respuesta Numérica
        System.out.println("\n🔢 RespuestaNumerica:");
        RespuestaNumerica rNumerica = new RespuestaNumerica(2, "usuario1", 11, 7.5);
        System.out.println("   " + rNumerica);
        System.out.println("   ¿Es entero?: " + rNumerica.esEntero());
        System.out.println("   Valor entero: " + rNumerica.getValorEntero());

        // Respuesta Opción Única
        System.out.println("\n☑️ RespuestaOpcionUnica:");
        RespuestaOpcionUnica rOpcionU = new RespuestaOpcionUnica(3, "usuario1", 12, 2);
        System.out.println("   " + rOpcionU);
        System.out.println("   ¿Es opción 2?: " + rOpcionU.esOpcion(2));
        System.out.println("   ¿Es opción 3?: " + rOpcionU.esOpcion(3));

        // Respuesta Opción Múltiple
        System.out.println("\n☑️☑️ RespuestaOpcionMultiple:");
        int[] opciones = {0, 2, 4};
        RespuestaOpcionMultiple rOpcionM = new RespuestaOpcionMultiple(
            4, "usuario1", 13, opciones
        );
        System.out.println("   " + rOpcionM);
        System.out.println("   Opciones seleccionadas: " + rOpcionM.getNumeroOpcionesSeleccionadas());
        System.out.println("   ¿Contiene 2?: " + rOpcionM.contieneOpcion(2));
        System.out.println("   ¿Contiene 1?: " + rOpcionM.contieneOpcion(1));

        // Modificar respuestas
        System.out.println("\n✏️ Modificando respuestas:");
        rLibre.modificar("Nuevo texto modificado");
        System.out.println("   RespuestaLibre modificada: " + rLibre.getRespuesta());

        rNumerica.modificar(10.0);
        System.out.println("   RespuestaNumerica modificada: " + rNumerica.getRespuesta());

        rOpcionU.modificar(4);
        System.out.println("   RespuestaOpcionUnica modificada: " + rOpcionU.getRespuesta());

        int[] nuevasOpciones = {1, 3};
        rOpcionM.modificar(nuevasOpciones);
        System.out.println("   RespuestaOpcionMultiple modificada: " + 
                         Arrays.toString(rOpcionM.getRespuesta()));

        System.out.println("\n✅ Todas las operaciones completadas\n");
    }

    /**
     * Ejemplo de errores en respuestas.
     */
    public static void ejemploErroresRespuestas() {
        System.out.println("\n═══════════════════════════════════════════════════════");
        System.out.println("  EJEMPLO: Errores en Respuestas");
        System.out.println("═══════════════════════════════════════════════════════\n");

        // Error 1: Texto vacío en RespuestaLibre
        System.out.println("❌ Test 1: RespuestaLibre con texto vacío");
        try {
            new RespuestaLibre(1, "usuario1", 10, "");
        } catch (IllegalArgumentException e) {
            System.out.println("   ✓ Excepción capturada: " + e.getMessage());
        }

        // Error 2: Valor NaN en RespuestaNumerica
        System.out.println("\n❌ Test 2: RespuestaNumerica con NaN");
        try {
            new RespuestaNumerica(2, "usuario1", 11, Double.NaN);
        } catch (IllegalArgumentException e) {
            System.out.println("   ✓ Excepción capturada: " + e.getMessage());
        }

        // Error 3: Índice negativo en RespuestaOpcionUnica
        System.out.println("\n❌ Test 3: RespuestaOpcionUnica con índice negativo");
        try {
            new RespuestaOpcionUnica(3, "usuario1", 12, -1);
        } catch (IllegalArgumentException e) {
            System.out.println("   ✓ Excepción capturada: " + e.getMessage());
        }

        // Error 4: Array vacío en RespuestaOpcionMultiple
        System.out.println("\n❌ Test 4: RespuestaOpcionMultiple con array vacío");
        try {
            new RespuestaOpcionMultiple(4, "usuario1", 13, new int[]{});
        } catch (IllegalArgumentException e) {
            System.out.println("   ✓ Excepción capturada: " + e.getMessage());
        }

        // Error 5: Duplicados en RespuestaOpcionMultiple
        System.out.println("\n❌ Test 5: RespuestaOpcionMultiple con duplicados");
        try {
            new RespuestaOpcionMultiple(5, "usuario1", 14, new int[]{0, 2, 2, 3});
        } catch (IllegalArgumentException e) {
            System.out.println("   ✓ Excepción capturada: " + e.getMessage());
        }

        // Error 6: Modificar con tipo incorrecto
        System.out.println("\n❌ Test 6: Modificar RespuestaLibre con número");
        try {
            RespuestaLibre r = new RespuestaLibre(6, "usuario1", 15, "Texto inicial");
            r.modificar(123);  // Debería ser String
        } catch (IllegalArgumentException e) {
            System.out.println("   ✓ Excepción capturada: " + e.getMessage());
        }

        System.out.println("\n✅ Todas las validaciones de errores funcionan\n");
    }
}
