# Observer Sound API

## Overview
The Observer Spatial Audio API allows developers to trigger custom client-side sounds with exact volume, pitch, and 3D positional tracking. It is fully integrated with Kyori Adventure but abstracts away the complexities of manual packet handling.

## API Reference

The main entry point for Paper plugins is `com.observer.paper.api.PaperObserverSoundAPI`.

### Models

#### `SoundDefinition`
A builder-based model representing a sound to be played.
- `soundId`: The name of the sound (e.g. `entity.generic.explode`).
- `namespace`: The namespace of the sound (defaults to `minecraft`).
- `volume`: The volume (defaults to `1.0`).
- `pitch`: The pitch (defaults to `1.0`).
- `position(x, y, z)`: Optional exact coordinates for spatial audio.

### Methods

#### `playSound(Player player, SoundDefinition sound)`
Plays a spatial sound for the specified player.
- **Parameters:**
  - `player`: The target Bukkit `Player`.
  - `sound`: The `SoundDefinition` configured with your parameters.

#### `stopSound(Player player, String soundId)`
Stops a specific sound currently playing for the player.
- **Parameters:**
  - `player`: The target Bukkit `Player`.
  - `soundId`: The ID of the sound to stop (e.g., `minecraft:entity.generic.explode`).

#### `stopAllSounds(Player player)`
Stops all custom spatial sounds playing for the player.

## Examples

**Playing a Spatial Sound:**
```java
SoundDefinition boom = SoundDefinition.builder()
    .sound("minecraft:entity.generic.explode")
    .position(player.getLocation().getX(), player.getLocation().getY(), player.getLocation().getZ())
    .volume(2.0f)
    .pitch(0.8f)
    .build();

PaperObserverSoundAPI.playSound(player, boom);
```

**Stopping a Sound:**
```java
PaperObserverSoundAPI.stopSound(player, "minecraft:music_disc.stal");
```

## Events

The following events are available in `com.observer.paper.api.events`:

- **`ObserverSoundStartEvent`**: Fired *before* a sound is sent to the client. Implements `Cancellable`, allowing you to block specific sounds from playing.
- **`ObserverSoundStopEvent`**: Fired when a plugin requests a sound to stop.

## Threading Notes
- Sound packets must be dispatched from the **main server thread**. 
- Calling `playSound` asynchronously will result in an exception from the Bukkit API.

## Compatibility Notes
- **Fabric/Sodium:** Fully compatible.
- Sounds must exist in the client's resource pack or standard Minecraft assets to be heard. Invalid sound IDs are ignored by the client without crashing.

## Best Practices
- **Pre-load assets:** Ensure custom sounds are loaded via a server resource pack.
- **Clean up:** If a player leaves an area where a looping ambient sound is playing, use `stopSound` to clean it up.
