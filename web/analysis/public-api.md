# Public API Reference

Este documento detalla las firmas exactas y el comportamiento profundo de cada API expuesta por `Observer`.

## `PaperObserverEnvironmentAPI`

### `setSkyColor(Player player, int rgbColor)`
- **Tipo**: `PUBLIC`
- **Parámetros**: 
  - `player`: El `Player` de Bukkit objetivo.
  - `rgbColor`: Color en formato entero (ej. `0xFF0000` para rojo puro).
- **Retorno**: `void`
- **Excepciones**: Ninguna declarada. Falla silenciosamente si el jugador no tiene el cliente instalado.
- **Comportamiento Interno**: Desglosa el entero en canales R, G, B mediante bit-shifting, y delega a `ObserverEnvironmentManager.setSkyColor`.

### `resetSkyColor(Player player)`
- **Tipo**: `PUBLIC`
- **Propósito**: Restaura el color del cielo al comportamiento normal de Minecraft (dependiente del bioma/tiempo).

---

## `PaperObserverKeyboardAPI`

### `isKeyDown(Player player, int asciiKey)`
- **Tipo**: `PUBLIC`
- **Parámetros**: 
  - `player`: El jugador de Bukkit objetivo.
  - `asciiKey`: El código ASCII de la tecla a verificar (ej. `32` para espacio, `65` para 'A').
- **Retorno**: `boolean` - `true` si la tecla está actualmente presionada según el último reporte del cliente.
- **Comportamiento Interno**: Convierte el ASCII a un `ordinal` interno (index de tecla) y consulta el `KeyInputManager`. Si el ordinal es inválido (`-1`), retorna `false`.

### `getPressedKeys(Player player)`
- **Tipo**: `PUBLIC`
- **Retorno**: `Set<Integer>` - Un conjunto de los códigos ASCII de todas las teclas presionadas simultáneamente.
- **Advertencia**: Retorna una colección inmutable o una copia. No intentar modificarla.

---

## `PaperObserverScreenAPI`

### `playScreenshake(Player player, float intensity, int durationTicks)`
- **Tipo**: `PUBLIC`
- **Parámetros**:
  - `player`: Jugador que experimentará el temblor.
  - `intensity`: Magnitud del efecto (0.0 = nulo, 1.0 = fuerte).
  - `durationTicks`: Duración en ticks de servidor (1 segundo = 20 ticks).
- **Comportamiento Interno**: Crea un `ScreenEffectPayload` con el tipo `SCREENSHAKE` y envía el paquete inmediatamente vía red.

---

## `PaperObserverSoundAPI`

### `playSound(Player player, SoundDefinition sound)`
- **Tipo**: `PUBLIC`
- **Parámetros**:
  - `player`: Bukkit `Player`.
  - `sound`: Objeto `SoundDefinition` que encapsula el namespace, id, volumen, pitch y coordenadas opcionales.
- **Comportamiento Interno**: 
  1. Dispara un evento Bukkit síncrono `ObserverSoundStartEvent` que implementa `Cancellable`.
  2. Si no es cancelado, reproduce el sonido. Si el sonido tiene coordenadas (X, Y, Z != 0), lo reproduce espacialmente.

### `stopSound(Player player, String soundId)`
- **Tipo**: `PUBLIC`
- **Parámetros**:
  - `player`: Bukkit `Player`.
  - `soundId`: Identificador del sonido a detener (debe coincidir con el ID original).
- **Comportamiento Interno**: Dispara el evento `ObserverSoundStopEvent` y envía la señal de detención al cliente.

---

## `PaperObserverAnimationAPI`

### `playAnimation(Player targetPlayer, String animationName)`
- **Tipo**: `PUBLIC`
- **Parámetros**:
  - `targetPlayer`: El jugador cuyo modelo animará (afecta cómo lo ven los demás y él mismo si el mod lo permite).
  - `animationName`: ID de la animación.
- **Comportamiento Interno**: Usa `ObserverNetworkManager.broadcastAnimation` para avisar a *todos* los jugadores cercanos que carguen la animación de ese jugador objetivo.
