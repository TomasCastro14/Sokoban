# New Sokoban!

Versión del Sokoban clásico hecha en **Java 21 + JavaFX 21** para el TPO de Proceso de Desarrollo de Software.
Tiene 5 niveles (Lunes a Viernes) con mecánicas nuevas, objetivos con estrellas, deshacer, progreso persistente,
música y efectos de sonido. El diseño aplica patrones GoF y GRASP sobre una arquitectura MVC: el modelo no conoce
JavaFX y se comunica con la vista solo mediante eventos.

## Cómo ejecutar

### Requisitos

- **JDK 21** (el proyecto compila con `release 21`).
- **Conexión a internet la primera vez**, para que Maven descargue JavaFX 21.0.5 (`javafx-controls`, `javafx-media`)
  y JUnit 5. No hace falta instalar JavaFX aparte.
- **Maven no es obligatorio**: el repositorio incluye el wrapper (`mvnw` / `mvnw.cmd`).
- Salida de audio opcional: sin ella el juego funciona en silencio.

### Desde la terminal

```bash
git clone https://github.com/TomasCastro14/Sokoban.git
cd Sokoban
./mvnw clean javafx:run      # Windows: mvnw.cmd clean javafx:run
./mvnw test                  # correr los tests
```

### Desde IntelliJ IDEA

1. Abrir la carpeta como proyecto Maven y esperar a que descargue las dependencias.
2. Configurar el SDK del proyecto en Java 21.
3. Ejecutar el goal `javafx:run` desde el panel de Maven, o la clase `app.Launcher`.

### Controles

| Tecla / botón | Acción |
| --- | --- |
| Flechas | Mover al jugador |
| ESC | Pausar o reanudar en el juego; volver atrás en los menús |
| Botón deshacer (HUD) | Retroceder 5 movimientos |
| Botón pausa (HUD) | Abrir el menú de pausa (salir o reiniciar) |

## Características principales

- **Movimiento casillero a casillero**, sin diagonales. Un movimiento bloqueado no cuenta.
- **Victoria**: todas las cajas guardadas en su punto de destino.
- **Cinco niveles** cargados desde `.txt`, con nombre, tiempo límite y objetivos en su encabezado.
- **Deshacer**: cada uso retrocede 5 movimientos; máximo 3 usos consecutivos y un historial de 15 movimientos.
- **Objetivos y estrellas**: cada nivel define un máximo de movimientos, de empujes y de undos. Cada objetivo
  cumplido es una estrella (0 a 3).
- **Puntaje**: 5.000 puntos por objetivo cumplido, multiplicados por los segundos restantes.
- **Tiempo límite** por nivel; si se agota, el nivel se pierde.
- **HUD** con nombre del nivel, tiempo, movimientos y empujes, más botones de deshacer y pausa.
- **Pantallas en una sola ventana** con transiciones: splash, menú principal, selección de niveles (carrusel con
  vista previa y estrellas), juego y configuración.
- **Popups de nivel**: objetivos antes de empezar, pausa, derrota (con el motivo) y victoria (estrellas, puntaje y resumen).
- **Progreso persistente** en un archivo local (ver más abajo).
- **Audio**: efectos por evento y música de fondo con playlist rotativa; volumen y silencio desde Configuración.

## Funcionalidades nuevas

| Mecánica | Comportamiento |
| --- | --- |
| Tres tipos de caja | Normal, débil y llave, cada una con su color de destino. Guardarla en un destino de otro color hace perder el nivel. |
| Caja débil | Resiste de 1 a 9 empujes. Si se rompe, se pierde el nivel. Un globo muestra el desgaste cuando el jugador está al lado. |
| Caja llave, baldosas llave y puertas | Una caja llave sobre una baldosa llave abre todas las puertas de su grupo. Guardarla antes de abrir su puerta hace perder el nivel. |
| Hielo | Lo que entra sigue deslizándose en la misma dirección hasta que algo lo frena. |
| Portales | Van en pares. Al entrar en uno se sale una casilla debajo de su pareja. |
| Destinos como pozos | El jugador no puede pisar un destino vacío; con la caja guardada, sí. |
| Niebla | Solo se ve lo que el jugador puede alcanzar; al abrir una puerta la nube se disipa. Las paredes se ven siempre. |
| Deadlock | Si una caja queda trabada en una esquina o en un bloque de 2×2, el juego se frena; se recupera con undo o reiniciando. |
| Cámara | En mapas más grandes que la pantalla, la vista sigue al jugador y se frena en los bordes. |

El nivel 4 (Jueves) combina todas las mecánicas.

## Arquitectura

```
app          SokobanApp: composition root, crea e inyecta cada pieza
view         pantallas JavaFX pasivas (menus/, gameplay/, components/)
controller   presenters MVC y entrada del teclado
model        reglas del juego, sin JavaFX (board, tile, box, game, history)
factory      lectura y validación de niveles (.txt)
port         interfaces de entrada/salida (LevelSource, ProgressStore)
service      implementaciones de los puertos y audio
exception    errores de carga de niveles
```

El controller envía comandos al modelo y escucha sus eventos (Observer) para actualizar las vistas, que dependen
de interfaces (`GameScreen`, `HudScreen`, `LevelDialogs`...). Los valores visuales viven en CSS modular
(`sokoban.css`, `buttons.css`, `dialogs.css`, `gameplay.css`), no en el código Java.

## Patrones de diseño

### GoF

| Patrón | Dónde | Qué resuelve |
| --- | --- | --- |
| Template Method | `Box` (`push()` → `onPushed()`), `AbstractLossCondition`, `AbstractGameState`, `AbstractAudioControl` | Comportamiento común con pasos que redefine cada subclase. |
| Observer | `KeyTile` → `DoorGroup`; `BoardListener`, `LevelFlowListener`, `LevelOutcomeListener`, `TimeListener`, `PauseListener`, `ProgressListener` | Las llaves abren puertas sin conocerlas; el modelo avisa a vista, HUD, sonido y guardado sin conocerlos. |
| Command | `Command`, `MoveCommand`, `UndoHistory` | Cada movimiento es una unidad que se ejecuta y se registra para deshacer. |
| Memento | `Memento`, `LevelMemento`, `BoxSnapshot`, `save()` de tiles, cajas y `VisibilityMap`; `ProgressData` | El undo restaura el estado exacto sin una operación inversa por cada efecto. |
| Builder | `LevelLoader` (director), `LevelBuilder` | Arma el nivel por pasos, cablea observers y portales y lo valida. |
| Factory Method | `CellCreator` y sus creadores (`WallCreator`, `WeakBoxCreator`, `KeyTileCreator`, `DoorCreator`, `PortalCreator`) | Cada carácter del mapa tiene su creador; un elemento nuevo no modifica el lector. |
| Strategy | `EntryEffect` (`NoEffect`, `SlideEffect`, `TeleportEffect`), `ScoreCalculator`, `ReachabilityCalculator` | Hielo y portales sin condicionales en el tablero; puntaje intercambiable. |
| State | `GameState`: `MenuState`, `PlayingState`, `PausedState`, `BlockedState`, `WonState`, `LostState`, `GameCompletedState` | Cada estado decide cómo responde a cada input. |
| Visitor | `TileVisitor`, `OccupantVisitor` | Actuar según el tipo concreto sin `instanceof`. |
| Flyweight | `SpriteCatalog`, `UiImages` | Cada imagen se carga una sola vez y se comparte. |
| Facade | `SoundService`, `MusicService` | Esconden `AudioClip` y `MediaPlayer`. |
| Composite | `AudioMixer` | Un solo control de volumen para todos los canales. |
| Null Object | `SilentSoundService`, `SilentMusicService` | Sin audio disponible, el juego sigue en silencio. |

### GRASP y SOLID

- **Experto en Información**: `Board` valida movimientos, `Level` detecta victoria y derrota, `UndoHistory` aplica las reglas del undo.
- **Creator**: `LevelBuilder` crea el `Level`.
- **Controller**: `Game` recibe los eventos del sistema.
- **Polimorfismo**: cada `Tile` responde `allowsPlayer()`, `allowsBox()`, `blocksVision()`.
- **Pure Fabrication**: `HeaderParser`, `BfsReachability`, `ClasspathLevelSource`, `FileProgressStore`, reglas de validación.
- **Protected Variations + DIP**: puertos `LevelSource` y `ProgressStore`.
- **Inyección por constructor**: todo se arma en `SokobanApp`; no hay Singletons de servicios.

## Decisiones de diseño

- **Undo con Memento y no con operaciones inversas.** Un movimiento puede deslizar varias casillas, desgastar una
  caja, abrir puertas y revelar niebla. El memento guarda datos, no copias de objetos, así no se rompen las
  suscripciones de los observers ni se usa Prototype (descartado por indicación del docente).
- **La caja nunca se elimina a sí misma (LSP).** `push()` devuelve un `PushResult` y el tablero decide.
- **Condiciones de derrota intercambiables**: cada regla es una `LossCondition`, inyectada en el builder mediante `LossRules`.
- **Deadlock recuperable, el resto definitivo**: `LossReason` indica si la derrota se puede deshacer
  (`BlockedState`) o no (`LostState`).
- **Visitor en lugar de `instanceof`**: se adoptó al crecer la vista (sprites por tipo, niebla, desgaste).
- **Sin métodos `default` en interfaces** (restricción de la cátedra): el comportamiento por defecto vive en clases abstractas.
- **Animación solo cosmética**: el modelo mueve al jugador de golpe; `PlayerAnimator` lo desliza en pantalla.
- **Audio tolerante a fallos**: si JavaFX Media no está disponible se usan los servicios silenciosos.

Patrones evaluados y descartados: Prototype (indicación del docente), Singleton (estado global, difícil de testear),
Decorator (las cajas no se combinan), State en la caja débil (solo cambia un contador), Chain of Responsibility
(forzado para pocas reglas) y Mediator (Observer por grupo alcanza).

## Formato de los niveles

Cada nivel es un `.txt` en `src/main/resources/levels/`: un encabezado `clave: valor`, una línea `---` y el mapa.

```
nombre: Lunes
tiempo: 600
movimientos: 470
empujes: 155
undo: 1
---
%%%%%%%#####%%%%
%%%%%%%#   #####
...
```

`tiempo` está en segundos; `movimientos`, `empujes` y `undo` son los límites de cada estrella.

| Carácter | Significado |
| --- | --- |
| `#` | Pared |
| `%` | Vacío (fuera del mapa) |
| (espacio) | Piso |
| `@` | Jugador |
| `$` | Caja normal |
| `1`–`9` | Caja débil (el dígito es la cantidad de empujes que resiste) |
| `K` | Caja llave |
| `.` `:` `;` | Destino de caja normal, débil y llave |
| `~` | Hielo |
| `a`–`e` | Baldosa llave del grupo |
| `A`–`E` | Puerta cerrada del grupo |
| `P`–`S` | Portal (cada letra aparece exactamente dos veces) |

Al cargar se valida que el borde esté cerrado, que haya un solo jugador, que cada portal tenga pareja y que cada
grupo tenga llave y puerta. Un error lanza `InvalidLevelException` con la regla rota y la línea del archivo.

## Progreso guardado

- Se guarda en `~/.sokoban/progress.properties` (en Windows, `C:\Users\<usuario>\.sokoban\progress.properties`),
  fuera del proyecto y del `.jar`.
- Recuerda los niveles desbloqueados y la mejor cantidad de estrellas y el mejor puntaje de cada nivel:
  ```
  lastUnlockedLevel=3
  level.1.stars=2
  level.1.score=1500
  ```
- Se guarda al ganar un nivel y al reiniciar el progreso; perder o salir a mitad de un nivel no lo modifica.
- La escritura es atómica (archivo temporal + reemplazo). Si el archivo falta o está roto, el juego arranca de cero.
- No se guarda una partida en curso: un nivel siempre empieza de nuevo desde su `.txt`.
- El botón "Reiniciar progreso" de la selección de niveles vuelve al estado inicial. También se puede borrar el archivo.

`Progress` (modelo) no sabe nada de archivos: entrega una foto `ProgressData`. El puerto `port.ProgressStore`
oculta dónde se guarda, `service.FileProgressStore` lo implementa y `controller.ProgressAutosave` lo carga al
iniciar y lo guarda en cada cambio.

## Tests

76 clases de test con JUnit 5 (unos 600 casos) sobre el modelo, la carga y validación de niveles, los presenters
y los servicios de sonido y progreso. Como el modelo no depende de JavaFX, se testea sin abrir ventanas.

## Créditos

- Interfaz (botones, paneles, íconos) y fuente Kenney Future: [Kenney – UI Pack](https://kenney.nl/assets/ui-pack), CC0.
- Efectos de sonido: [Kenney](https://kenney.nl/assets) (Impact Sounds, Interface Sounds, RPG Audio, Music Jingles), CC0.
  Detalle por archivo en `src/main/resources/assets/audio/CREDITS.md`.
