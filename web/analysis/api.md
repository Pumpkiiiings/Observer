# Observer API Overview

El framework `Observer` expone varias APIs públicas, diseñadas para ser consumidas por los desarrolladores de plugins en el lado del servidor (PaperMC). Estas APIs actúan como fachadas que ocultan la complejidad de la comunicación de red con el cliente (Fabric).

## APIs Disponibles

### 1. `PaperObserverEnvironmentAPI`
- **Propósito**: Modificar elementos del entorno visual del jugador en el cliente.
- **Cuándo usarla**: Cuando necesites crear atmósferas inmersivas, como cambiar el color del cielo durante un evento especial (ej. cielo rojo durante una "Luna de Sangre").
- **Cuándo NO usarla**: No debe usarse en ticks continuos (`onTick`), ya que el envío masivo de paquetes de entorno podría saturar la red del cliente.
- **Limitaciones**: Requiere que el jugador tenga instalado el mod cliente `observer-fabric`.

### 2. `PaperObserverKeyboardAPI`
- **Propósito**: Consultar el estado de las teclas del jugador en tiempo real.
- **Cuándo usarla**: Cuando necesites saber si una tecla específica está presionada (ej. verificar si el jugador mantiene presionado 'Shift' o 'Espacio' para habilidades).
- **Cuándo NO usarla**: Para capturar "pulsaciones" puntuales (press/release). Para eso es mejor escuchar los eventos asíncronos (`PlayerKeyPressEvent`).
- **Thread Safety**: La lectura es Thread-Safe ya que consulta una colección en memoria, pero los eventos de teclado se procesan asíncronamente.

### 3. `PaperObserverSoundAPI`
- **Propósito**: Reproducir y detener sonidos personalizados 2D y 3D en el cliente usando `SoundDefinition`.
- **Cuándo usarla**: Para efectos de sonido atmosféricos, música de fondo de menús o efectos posicionales, especialmente cuando provienen de Resource Packs custom.
- **Cuándo NO usarla**: Para reproducir sonidos vanilla de Minecraft simples; el API de Bukkit nativo (`player.playSound`) ya maneja eso eficientemente.

### 4. `PaperObserverAnimationAPI`
- **Propósito**: Obligar a un jugador a reproducir una animación específica (sincronizada con el cliente).
- **Cuándo usarla**: Para animaciones custom de combate (ej. *heavy_attack*, *cast_spell*).
- **Limitaciones**: La animación debe estar definida y registrada previamente en el cliente (usualmente a través de mods de animación de modelos).

### 5. `PaperObserverScreenAPI`
- **Propósito**: Manejar efectos de pantalla en primera persona, como screenshakes (temblores).
- **Cuándo usarla**: Al recibir daño crítico, explosiones cercanas, o durante cinemáticas de boss fights.
- **Parámetros principales**: `intensity` (magnitud del temblor, típicamente de 0.1 a 1.0) y `durationTicks` (duración).

## Flujo de Uso Recomendado

1. **Escuchar Eventos**: Configurar `Listeners` de Bukkit para escuchar `ObserverPlayerKeyEvent` o `PlayerActionPayload`.
2. **Llamar APIs**: En respuesta al evento, llamar estáticamente a las APIs (ej. `PaperObserverScreenAPI.playScreenshake(...)`).
3. **Validación**: Las APIs generalmente delegan al `ObserverNetworkManager` que solo enviará el paquete si el jugador está conectado y realizó el Handshake exitosamente (confirmando que tiene el mod instalado).

## Nivel de Estabilidad
Las APIs públicas ubicadas en los paquetes `.api` de `observer-paper` se consideran **ESTABLES (PUBLIC)**, mientras que las interfaces base en `observer-api` podrían sufrir ligeros cambios si se añaden nuevos payloads (**EXPERIMENTAL**).
