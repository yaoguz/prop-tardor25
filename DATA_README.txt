═══════════════════════════════════════════════════════════════════════════
  ESTRUCTURA DE DATOS - SISTEMA DE ENCUESTAS
═══════════════════════════════════════════════════════════════════════════

RESUMEN:
- 5 encuestas con 17 preguntas totales
- 10 usuarios con nombres simplificados
- 145 respuestas distribuidas en 17 archivos (agrupadas por pregunta)
- Estructura optimizada para acceso O(N) en clustering

═══════════════════════════════════════════════════════════════════════════
1. ESTRUCTURA DE DIRECTORIOS
═══════════════════════════════════════════════════════════════════════════

data/
├── encuestas/
│   ├── index.json                    # Índice rápido de todas las encuestas
│   ├── 1/encuesta.json              # Encuesta + preguntas completas
│   ├── 2/encuesta.json
│   ├── 3/encuesta.json
│   ├── 4/encuesta.json
│   └── 5/encuesta.json
├── usuarios/
│   ├── ana.json                     # Un archivo por usuario
│   ├── carlos.json
│   ├── david.json
│   ├── elena.json
│   ├── isabel.json
│   ├── juan.json
│   ├── laura.json
│   ├── maria.json
│   ├── pedro.json
│   └── sergio.json
└── respuestas/
    └── por_encuesta/
        ├── 1.json                   # TODAS las respuestas de encuesta 1
        ├── 2.json                   # TODAS las respuestas de encuesta 2
        ├── 3.json                   # TODAS las respuestas de encuesta 3
        ├── 4.json                   # TODAS las respuestas de encuesta 4
        └── 5.json                   # TODAS las respuestas de encuesta 5

═══════════════════════════════════════════════════════════════════════════
2. FORMATO DE ARCHIVOS
═══════════════════════════════════════════════════════════════════════════

--- USUARIO (usuarios/[nombre].json) ---
{
  "nombreUsuario": "maria",
  "contrasena": "password123",
  "idEncuestasCreadas": [1, 3],
  "idRespuestas": [10, 20, 30, 40, 73, 82, 91, 100, 129, 137, 145]
}

Campo idRespuestas: ArrayList<Integer> con IDs de las respuestas dadas

--- ENCUESTA (encuestas/[id]/encuesta.json) ---
{
  "id": 1,
  "titulo": "Hábitos de Estudio Universitarios",
  "autor": "maria",
  "fechaCreacion": "2025-11-15",
  "preguntas": [
    {
      "idPregunta": 1,
      "tipo": "numerica",
      "enunciado": "¿Cuántas horas dedicas al estudio diariamente?",
      "rangoInf": 0,
      "rangoSup": 12,
      "idRespuestas": [1, 2, 3, 4, 5, 6, 7, 8, 9, 10]
    },
    ...
  ]
}

Tipos de pregunta: "numerica", "opcion_unica", "opcion_multiple", "libre"

--- RESPUESTAS (respuestas/por_encuesta/[idEncuesta].json) ---
[
  {
    "idRespuesta": 1,
    "idPregunta": 1,
    "nomUsuario": "juan",
    "tipo": "numerica",
    "respuesta": 5.0
  },
  {
    "idRespuesta": 2,
    "idPregunta": 1,
    "nomUsuario": "pedro",
    "tipo": "numerica",
    "respuesta": 6.5
  },
  {
    "idRespuesta": 11,
    "idPregunta": 2,
    "nomUsuario": "juan",
    "tipo": "opcion_unica",
    "respuesta": 0
  },
  ...
]

TODAS las respuestas de TODAS las preguntas de esta encuesta en un solo archivo.

═══════════════════════════════════════════════════════════════════════════
3. INVENTARIO COMPLETO
═══════════════════════════════════════════════════════════════════════════

ENCUESTAS (5):
  1. "Hábitos de Estudio Universitarios" (maria) - 4 preguntas
  2. "Preferencias de Ocio y Entretenimiento" (juan) - 3 preguntas
  3. "Salud y Bienestar Personal" (maria) - 4 preguntas
  4. "Hábitos Alimenticios Saludables" (pedro) - 3 preguntas
  5. "Uso de Tecnología y Redes Sociales" (ana) - 3 preguntas

USUARIOS (10):
  maria (creó: 1, 3)
  juan (creó: 2)
  pedro (creó: 4)
  ana (creó: 5)
  carlos, laura, sergio, elena, david, isabel (participantes)

RESPUESTAS (145 total, agrupadas en 5 archivos por encuesta):
  Encuesta 1: 40 respuestas (IDs 1-40, preguntas 1-4)
  Encuesta 2: 24 respuestas (IDs 41-64, preguntas 5-7)
  Encuesta 3: 36 respuestas (IDs 65-100, preguntas 8-11)
  Encuesta 4: 21 respuestas (IDs 101-121, preguntas 12-14)
  Encuesta 5: 24 respuestas (IDs 122-145, preguntas 15-17)

═══════════════════════════════════════════════════════════════════════════
4. COHERENCIA Y VALIDACIÓN
═══════════════════════════════════════════════════════════════════════════

✓ Todos los nomUsuario en respuestas usan nombres simplificados (sin apellidos)
✓ Todos los autores en encuestas actualizados correctamente
✓ Usuario.idRespuestas coinciden con respuestas existentes
✓ Pregunta.idRespuestas coinciden con archivos de respuestas
✓ IDs secuenciales sin gaps: 1-145
✓ Tipos de respuesta coinciden con tipos de pregunta
✓ Valores dentro de rangos declarados

═══════════════════════════════════════════════════════════════════════════
5. COMPATIBILIDAD CON CLASES DE DOMINIO
═══════════════════════════════════════════════════════════════════════════

Usuario.java:
  ✓ nombreUsuario (String)
  ✓ contrasena (String) - sin prefijo "hashed_"
  ✓ idEncuestasCreadas (ArrayList<Integer>)
  ✓ idRespuestas (ArrayList<Integer>)

Encuesta.java:
  ✓ id/idEncuesta (int)
  ✓ titulo (String)
  ✓ creador (String) - nombres simplificados
  ✓ IdPreguntas (ArrayList<Integer>) - se extrae de preguntas[]

Pregunta.java (abstracta):
  ✓ idPregunta (int)
  ✓ idEncuesta (int)
  ✓ enunciado (String)
  ✓ idRespuestas (ArrayList<Integer>)
  + Campos específicos según tipo

Respuesta.java (abstracta):
  ✓ id (int)
  ✓ nomUsuario (String)
  ✓ idPregunta (int)
  ✓ tipo (String)
  ✓ respuesta (Object) - tipo según subclase

═══════════════════════════════════════════════════════════════════════════
6. OPTIMIZACIÓN: ACCESO O(1) POR ENCUESTA
═══════════════════════════════════════════════════════════════════════════

ESTRUCTURA OPTIMIZADA (por_encuesta):
  - Respuestas agrupadas por encuesta en un solo archivo
  - Para obtener todas las respuestas de una encuesta:
    1. Leer respuestas/por_encuesta/[idEncuesta].json
    2. Filtrar/agrupar en memoria según necesidad
    3. Acceso: O(1) - UN SOLO archivo

CASOS DE USO Y SU RENDIMIENTO:

1. getRespuestasEncuesta(idEncuesta) [CtrlDominioEncuesta]
   - Leer 1 archivo → agrupar por pregunta en memoria
   - Acceso a disco: O(1) ✓

2. getRespuestasEncuesta(usuario, idEncuesta) [CtrlDominioUsuario]
   - Leer 1 archivo → filtrar por nomUsuario en memoria
   - Acceso a disco: O(1) ✓

3. getRespuestasParaClustering(idEncuesta) [CtrlDominioEncuesta]
   - Leer 1 archivo → reorganizar en matriz [usuarios × preguntas]
   - Acceso a disco: O(1) ✓

COMPARACIÓN CON ALTERNATIVAS:

Estructura por_pregunta (anterior):
  - getRespuestasEncuesta: O(N) accesos (N = número de preguntas)
  - getRespuestasEncuesta(usuario): O(M) accesos (M = respuestas del usuario)
  - getRespuestasParaClustering: O(N) accesos
  - Para encuesta con 10 preguntas: 10 accesos a disco

Estructura por_encuesta (actual):
  - getRespuestasEncuesta: O(1) acceso
  - getRespuestasEncuesta(usuario): O(1) acceso
  - getRespuestasParaClustering: O(1) acceso
  - Para encuesta con 10 preguntas: 1 acceso a disco

MEJORA: 10x más rápido en acceso a disco ✓

Estructura por_usuario (descartada):
  - getRespuestasEncuesta: O(U) accesos (U = usuarios que respondieron)
  - Ineficiente para clustering y vista de respuestas por encuesta

═══════════════════════════════════════════════════════════════════════════
7. IMPLEMENTACIÓN EN PERSISTENCIA
═══════════════════════════════════════════════════════════════════════════

NUEVO MÉTODO EN GestorPersistencia:

  /**
   * Obtiene todas las respuestas de una encuesta específica.
   * 
   * En implementación real con archivos JSON:
   * - Lee data/respuestas/por_encuesta/[idEncuesta].json
   * - Deserializa todas las respuestas del archivo
   * - Retorna la lista completa
   * 
   * VENTAJA: Un solo acceso a disco (O(1)) vs múltiples archivos
   */
  public ArrayList<Respuesta> getRespuestasEncuesta(int idEncuesta)

CAMBIOS EN CONTROLADORES DE DOMINIO:

CtrlDominioEncuesta:
  - getRespuestasEncuesta(): Usa gestor.getRespuestasEncuesta(id)
  - getRespuestasParaClustering(): Usa gestor.getRespuestasEncuesta(id)
  - Eliminados múltiples loops anidados
  - Agrupación/reorganización se hace en memoria (muy rápido)

CtrlDominioUsuario:
  - getRespuestasEncuesta(usuario, id): Usa gestor.getRespuestasEncuesta(id)
  - Filtrado por usuario en memoria
  - Eliminado acceso individual por idRespuestas del usuario

═══════════════════════════════════════════════════════════════════════════
FECHA DE CREACIÓN: 2025-11-29
═══════════════════════════════════════════════════════════════════════════
