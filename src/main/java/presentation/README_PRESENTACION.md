# Capa de Presentación - Encuestas con Extractor de Perfiles

## Arquitectura

La capa de presentación sigue el patrón **MVC (Model-View-Controller)** con componentes reutilizables:

```
presentation/
├── Main.java                    # Punto de entrada
├── CtrlPresentacion.java        # Controlador principal
├── components/                  # Componentes reutilizables
│   ├── ComponenteBase.java      # Clase base para componentes UI
│   └── BarraNavegacion.java     # Barra superior con menú usuario
└── views/                       # Vistas de la aplicación
    ├── VistaLogin.java          # Inicio de sesión
    ├── VistaRegistro.java       # Registro de usuario
    └── VistaListaEncuestas.java # Lista principal de encuestas
```

## Patrones Aplicados

### 1. **Template Method** (ComponenteBase)
- Clase base abstracta con métodos de utilidad comunes
- `createStyledButton()`, `createStyledLabel()`, etc.
- Estilos consistentes en toda la aplicación

### 2. **Singleton** (CtrlDominio, GestorPersistencia)
- Instancia única de controladores
- Garantiza estado consistente del sistema

### 3. **MVC** (Model-View-Controller)
- **Model**: Capa de dominio y persistencia
- **View**: Vistas en `presentation.views`
- **Controller**: `CtrlPresentacion` coordina navegación

### 4. **Reutilización de Componentes**
- `BarraNavegacion`: Usado en múltiples vistas
- `ComponenteBase`: Herencia para estilos comunes
- Métodos de utilidad compartidos

## Vistas Implementadas

### VistaLogin
- **Funcionalidad**: Inicio de sesión con credenciales
- **Navegación**: → VistaRegistro | → VistaListaEncuestas
- **Validaciones**: Campos obligatorios, credenciales correctas

### VistaRegistro
- **Funcionalidad**: Crear nueva cuenta de usuario
- **Navegación**: → VistaLogin
- **Validaciones**: Contraseñas coincidentes, usuario único

### VistaListaEncuestas (Principal)
- **Funcionalidad**: Mostrar encuestas con filtros
- **Características**:
  - Buscador por palabras clave en título
  - Filtros: Mis Encuestas, Ajenas, Respondidas, No Respondidas
  - Botones según permisos (autor vs. no autor)
  - Barra de navegación con menú de usuario
- **Navegación**: → Crear/Modificar/Responder/Ver encuestas

## Vistas Pendientes (Estructura Definida)

Por limitaciones de espacio, las siguientes vistas siguen el mismo patrón:

### VistaModificarEncuesta
- Editar título y preguntas existentes
- Añadir nuevas preguntas (desplegable con tipos)
- Botón guardar/salir con confirmación de cambios

### VistaResponderEncuesta
- Mostrar preguntas de forma no editable
- Campos de respuesta según tipo de pregunta
- Guardar respuestas o modificar existentes

### VistaVerRespuestas
- Filtrar por usuarios o por preguntas
- Mostrar respuestas en formato estructurado

### VistaEstadisticas
- Input para k y selector de algoritmo
- Ejecución automática de clustering
- Mostrar perfiles con representante y miembros

## Componentes Reutilizables

### ComponenteBase
```java
// Colores del tema
COLOR_PRIMARY, COLOR_SUCCESS, COLOR_DANGER, etc.

// Fuentes consistentes
FONT_TITLE, FONT_SUBTITLE, FONT_NORMAL

// Métodos de utilidad
createStyledButton(text, color)
createStyledLabel(text, font)
createStyledTextField(columns)
```

### BarraNavegacion
```java
// Uso en cualquier vista
BarraNavegacion barra = new BarraNavegacion(nombreUsuario);
barra.setOnCerrarSesion(e -> cerrarSesion());
barra.setOnCambiarContrasena(e -> cambiarPass());
barra.setOnSalir(e -> System.exit(0));
panel.add(barra.getPanel(), BorderLayout.NORTH);
```

## Flujo de Navegación

```
[VistaLogin] ←→ [VistaRegistro]
     ↓
[VistaListaEncuestas]
     ├→ [VistaModificarEncuesta] (si es autor)
     ├→ [VistaResponderEncuesta]
     ├→ [VistaVerRespuestas] (si es autor)
     └→ [VistaEstadisticas] (si es autor)
```

## Comunicación con Capa de Dominio

Todas las llamadas pasan por `CtrlPresentacion` que delega a `CtrlDominio`:

```java
// Autenticación
ctrlPresentacion.registrarUsuario(nom, pass, confirm);
ctrlPresentacion.iniciarSesion(nom, pass);
ctrlPresentacion.cerrarSesion();

// Encuestas
ctrlPresentacion.obtenerEncuestas(usuario, busqueda, filtros...);
ctrlPresentacion.haRespondido(usuario, idEncuesta);

// Delegación a CtrlDominio
ctrlDominio.crearEncuesta(usuario, titulo);
ctrlDominio.anadirPreguntaLibre(idEncuesta, enunciado);
ctrlDominio.darRespuesta(usuario, idPregunta, respuesta);
ctrlDominio.ejecutarClustering(idEncuesta, k, algoritmo);
```

## Estilos y Convenciones

### Colores
- **Primary**: Azul (#3498db) - Acciones principales
- **Success**: Verde (#2ecc71) - Crear, confirmar
- **Danger**: Rojo (#e74c3c) - Eliminar, cancelar
- **Warning**: Amarillo (#f1c40f) - Modificar, editar

### Fuentes
- **Title**: Arial Bold 24pt
- **Subtitle**: Arial Bold 16pt
- **Normal**: Arial 14pt
- **Small**: Arial 12pt

### Layout
- `BorderLayout` para estructura principal
- `GridBagLayout` para formularios centrados
- `BoxLayout` para listas verticales
- `FlowLayout` para grupos de botones

## Ejecución

```bash
# Compilar
./gradlew build

# Ejecutar
./gradlew run

# O directamente
java -cp build/classes/java/main presentation.Main
```

## Notas de Implementación

1. **Validaciones**: Realizadas en CtrlDominio, presentación solo muestra errores
2. **Persistencia**: Automática mediante GestorPersistencia con cache LRU
3. **Thread Safety**: Todas las operaciones UI en Event Dispatch Thread
4. **Extensibilidad**: Nuevas vistas heredan de ComponenteBase
5. **Separación**: Presentación no modifica directamente dominio

## TODOs

- [ ] Implementar vistas detalladas pendientes
- [ ] Añadir internacionalización (i18n)
- [ ] Mejorar feedback visual (loading spinners)
- [ ] Implementar undo/redo en edición
- [ ] Añadir shortcuts de teclado
- [ ] Pruebas de UI automatizadas

---

**Versión**: 1.0  
**Java**: 21  
**UI Framework**: AWT + Swing  
**Autor**: PROP Cluster XX
