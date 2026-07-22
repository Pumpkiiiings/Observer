# Observer Environment API

## Overview
The Observer Environment API allows developers to dynamically modify client-side rendering features such as the sky color, fog color, fog density, and "True Darkness" mode. This enables you to create localized weather, horror ambiances, and immersive cutscenes.

## API Reference

The main entry point for Paper plugins is `com.observer.paper.api.PaperObserverEnvironmentAPI`.

### Models

#### `EnvironmentProfile`
A reusable preset system for environment configurations.
- `id`: A unique identifier for the profile.
- `skyColor`: Hex integer representing RGB sky color (e.g., `0xFF0000` for red).
- `fogColor`: Hex integer for fog color.
- `fogDensity`: Float multiplier for fog thickness (e.g., `0.05f`).
- `trueDarkness`: Boolean enabling pitch-black rendering in unlit areas.

Pre-defined presets are available: `EnvironmentProfile.NIGHTMARE`, `EnvironmentProfile.FOGGY`, `EnvironmentProfile.BLOOD_MOON`, `EnvironmentProfile.DEFAULT`.

### Methods

#### `setSkyColor(Player player, int rgbColor)`
Overrides the client's sky color.
#### `resetSkyColor(Player player)`
Restores the default vanilla sky color based on the current biome and time.

#### `setFogColor(Player player, int rgbColor)`
Overrides the client's fog color.
#### `resetFogColor(Player player)`
Restores vanilla fog colors.

#### `setFogDensity(Player player, float density)`
Modifies how thick the fog is. High values pull the fog start closer to the player.
#### `resetFogDensity(Player player)`
Restores vanilla fog distances.

#### `setTrueDarkness(Player player, boolean enabled)`
Forces light levels below 0 to render as completely black, ignoring vanilla ambient light.

#### `resetEnvironment(Player player)`
Completely resets the environment state (sky, fog, moon, darkness) back to vanilla.

#### `applyProfile(Player player, EnvironmentProfile profile)`
Applies all non-null fields from an `EnvironmentProfile` to the player instantly.

## Examples

**Setting a Blood Moon:**
```java
PaperObserverEnvironmentAPI.setSkyColor(player, 0xFF0000); // Red sky
PaperObserverEnvironmentAPI.setFogColor(player, 0x550000); // Dark red fog
PaperObserverEnvironmentAPI.setFogDensity(player, 0.1f);   // Thick fog
```

**Using Profiles:**
```java
EnvironmentProfile scary = EnvironmentProfile.NIGHTMARE;
PaperObserverEnvironmentAPI.applyProfile(player, scary);

// Later...
PaperObserverEnvironmentAPI.resetEnvironment(player);
```

## Events

The following events are available in `com.observer.paper.api.events`:

- **`EnvironmentApplyEvent`**: Fired when `applyProfile` is called.
- **`EnvironmentResetEvent`**: Fired when `resetEnvironment` is called.

## Command Support

The environment system includes built-in commands for testing:
- `/observer env <targets> darkness <true|false>`
- `/observer env <targets> dense <start> <end> <alpha>`
- `/observer env <targets> sky <r> <g> <b>`
- `/observer env <targets> sky reset`

## Threading Notes
- Safe to call asynchronously! The underlying packet dispatcher handles thread-safe buffering to the network manager.

## Compatibility Notes
- **Fabric/Sodium/Iris:** Fully compatible. The environment API modifies shader uniforms and mixins directly in the Fabric client.
- **Optifine:** Not supported. Observer requires modern rendering pipelines.

## Common Mistakes
- **Forgetting to reset:** Always call `resetEnvironment(player)` when a player leaves an area or disconnects to avoid visual glitches.
