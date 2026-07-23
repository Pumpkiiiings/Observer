# Observer Events Reference

El framework expone múltiples Eventos (extienden de `org.bukkit.event.Event`) que permiten a los desarrolladores escuchar y reaccionar a acciones asíncronas originadas por el cliente de los jugadores.

## Keyboard Events (Eventos de Teclado)

### `PlayerKeyPressEvent`
- **Cuándo ocurre**: Cuando un jugador presiona una tecla hacia abajo en su teclado (Key down).
- **Quién lo dispara**: `KeyActionManager` al recibir el `KeyEventPayload` del cliente indicando una nueva pulsación.
- **Datos disponibles**:
  - `Player player`: El jugador que presionó la tecla.
  - `int asciiKey`: El código ASCII de la tecla.
- **Casos de uso**: Iniciar la carga de un ataque, abrir un menú de habilidades.

### `PlayerKeyReleaseEvent`
- **Cuándo ocurre**: Cuando un jugador suelta una tecla que previamente estaba presionada (Key up).
- **Quién lo dispara**: `KeyActionManager`.
- **Datos disponibles**:
  - `Player player`
  - `int asciiKey`
- **Casos de uso**: Ejecutar la habilidad que se estaba cargando en el evento Press.

### `PlayerKeyMatchEvent` (y `ObserverPlayerKeyMatchEvent`)
- **Cuándo ocurre**: Cuando una combinación específica de teclas, pre-registrada, se cumple exactamente.
- **Quién lo dispara**: El sistema de tracking de teclado cuando procesa las teclas presionadas concurrentemente (`getPressedKeys()`).
- **Casos de uso**: Combos de combate o atajos de administración (Ej. Ctrl+Shift+K).

## Environment Events (Eventos de Entorno)

### `EnvironmentApplyEvent`
- **Cuándo ocurre**: Justo antes de que un nuevo color de cielo (u otro efecto de entorno futuro) sea enviado al cliente.
- **Quién lo dispara**: `ObserverEnvironmentManager`.
- **Datos disponibles**: `Player player`, `int r`, `int g`, `int b`.
- **Casos de uso**: Interceptar y modificar el color del entorno antes de que llegue al jugador, o cancelarlo (`Cancellable`).

### `EnvironmentResetEvent`
- **Cuándo ocurre**: Cuando se solicita resetear el entorno del jugador a los valores de Minecraft vanilla.
- **Quién lo dispara**: `ObserverEnvironmentManager`.
- **Casos de uso**: Notificar a otros plugins que la "atmósfera" custom ha finalizado.

## Menu Events (Eventos de Interfaz UI)

### `MenuActionEvent` / `ObserverMenuActionEvent`
- **Cuándo ocurre**: Cuando un jugador hace clic en un botón interactivo dentro de un menú custom de Observer (`ObserverDynamicScreen`).
- **Quién lo dispara**: `ObserverMenuManager` al recibir un `MenuActionPayload`.
- **Datos disponibles**:
  - `Player player`: Quien hizo clic.
  - `String menuId`: El identificador único del menú.
  - `String reference`: El ID o referencia del componente (botón) cliqueado.
- **Casos de uso**: Lógica de tiendas, selección de clases, o botones de navegación.

### `MenuCloseEvent`
- **Cuándo ocurre**: Cuando el jugador cierra el menú usando la tecla ESC o una acción interna de cierre.
- **Quién lo dispara**: `ObserverMenuManager`.
- **Datos disponibles**: `Player player`, `String menuId`.
- **Casos de uso**: Limpiar caché, des-pausar la partida del jugador si estaba en un estado protegido mientras el menú estaba abierto.

## Movement / Action Events (Eventos de Acción)

### `ObserverPlayerJumpEvent`, `ObserverPlayerSneakEvent`, `ObserverPlayerSprintEvent`
- **Cuándo ocurre**: Cuando el cliente detecta y envía la confirmación de la acción física en el juego a través de `PlayerActionPayload`.
- **Quién lo dispara**: Manejador de Payload de Acciones del Jugador.
- **Casos de uso**: Dobles saltos precisos, mecánicas de sigilo exactas sincronizadas con el frame del cliente en vez del servidor tick.

## Sound Events (Eventos de Sonido)

### `ObserverSoundStartEvent`
- **Cuándo ocurre**: Inmediatamente antes de que `ObserverSoundAPI` procese un sonido.
- **Características**: Implementa `Cancellable`.
- **Casos de uso**: Bloquear sonidos si el jugador tiene una opción de "silencio" activada.

### `ObserverSoundStopEvent`
- **Cuándo ocurre**: Cuando el servidor pide detener un sonido.
