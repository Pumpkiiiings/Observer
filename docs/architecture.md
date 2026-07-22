# Observer Architecture

## Overview
Observer is designed with a strict platform-agnostic core (`observer-api`) and platform-specific implementations (`observer-paper`, `observer-fabric`, etc.). This architecture ensures that the core payloads, models, and network channels are decoupled from Bukkit or Fabric specifics, allowing future support for Velocity, NeoForge, or Sponge without breaking plugin compatibility.

## Modules

### `observer-api`
The universal core library. It only defines:
- **Models**: `SoundDefinition`, `EnvironmentProfile`, `ObserverKey`, `ComponentAlignment`
- **Interfaces**: Contracts like `ObserverSoundAPI`, `ObserverKeyboardAPI`, `ObserverEnvironmentAPI` that operate on platform-agnostic types (like UUID).
- **Network**: Payload definitions (e.g., `KeyEventPayload`, `EnvironmentUpdatePayload`) using Minecraft's standard ByteBuf codecs.

**Rules for observer-api:**
- No Bukkit imports (`org.bukkit.*`).
- No Fabric imports (`net.fabricmc.*`).
- Only standard Java, Minecraft core mappings (`com.mojang`), and networking basics.

### `observer-paper`
The Bukkit/Paper server implementation. It handles:
- **Facades**: Exposing simple static API classes for Bukkit plugin developers (`PaperObserverSoundAPI`, `PaperObserverEnvironmentAPI`, `PaperObserverKeyboardAPI`) which use `org.bukkit.entity.Player`.
- **Events**: Defines and fires standard Bukkit Events (e.g., `ObserverSoundStartEvent`, `PlayerKeyPressEvent`).
- **Managers**: Manages state like `ObserverEnvironmentManager`, `KeyActionManager`, and packet dispatching.

### `observer-fabric`
The Fabric client implementation. It handles:
- **Input**: Captures raw keyboard input and sends `KeyEventPayload` to the server.
- **Rendering**: Receives `EnvironmentUpdatePayload` and customizes the sky/fog/lighting renderers natively on the client.

## API Philosophy
The public developer API uses a **Facade Pattern**. Plugin developers should never interact with Packets, Networking, Payloads, Mixins, Renderers, or Internal Managers. 

Everything is callable through the platform facade (e.g., `PaperObserverKeyboardAPI.isKeyDown(player, 'G')`) to ensure extreme simplicity and long-term stability.

## Packet Flow

### Keyboard System
1. **Client**: Player presses 'G' key.
2. **Client**: Fabric mixin intercepts the key press.
3. **Client**: `KeyEventPayload` is serialized and sent on `observer:event` channel.
4. **Server**: `KeyInputManager` decodes the payload.
5. **Server**: `PlayerKeyPressEvent` is fired.
6. **Server**: Plugin listener receives the event.

### Environment System
1. **Server**: Plugin calls `PaperObserverEnvironmentAPI.setSkyColor(player, 0xFF0000)`.
2. **Server**: `EnvironmentApplyEvent` is fired.
3. **Server**: `ObserverEnvironmentManager` serializes an `EnvironmentUpdatePayload`.
4. **Server**: Packet is dispatched over `observer:environment` channel.
5. **Client**: Payload is decoded by Fabric network receiver.
6. **Client**: Client renderer interpolates the new sky color smoothly.

## Future Expansion
The architecture supports modular expansion. Future APIs (Menu API, Animation API, NPC API, Camera API) should follow the same pattern:
1. Define models and payloads in `observer-api`.
2. Provide a platform-specific facade and Bukkit Events in `observer-paper`.
3. Keep internal managers hidden from developers.
