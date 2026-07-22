# Observer Keyboard API

## Overview
The Keyboard API allows you to precisely detect what keys a player is pressing on their physical keyboard, without requiring chat or inventory hacks. It is perfect for skill casting, custom movement, and interactive minigames.

## API Reference

The main entry point for Paper plugins is `com.observer.paper.api.PaperObserverKeyboardAPI`.

### Methods

#### `isKeyDown(Player player, int asciiKey)`
Checks if a player is currently holding down a specific ASCII key.
- **Parameters:**
  - `player`: The target Bukkit `Player`.
  - `asciiKey`: The ASCII byte code of the key (e.g., `71` for 'G', `65` for 'A').
- **Returns:** `true` if the key is pressed, `false` otherwise.

#### `getPressedKeys(Player player)`
Gets all currently pressed keys for a player.
- **Returns:** A `Set<Integer>` containing the ASCII byte codes of all pressed keys.

#### `flushKeys(Player player)`
Clears the internal state of pressed keys for a player. Useful if a player gets stuck in a menu.

## Examples

**Checking for a specific key press:**
```java
if (PaperObserverKeyboardAPI.isKeyDown(player, 'G')) {
    player.sendMessage("You are pressing the G key!");
}
```

**Listening to Key Events:**
```java
@EventHandler
public void onKeyPress(PlayerKeyPressEvent event) {
    if (event.getAsciiKey() == 'F') {
        event.getPlayer().sendMessage("Respects paid.");
    }
}
```

## Events

The following events are available in `com.observer.paper.api.events`:

- **`PlayerKeyPressEvent`**: Fired when a key goes from unpressed to pressed.
- **`PlayerKeyReleaseEvent`**: Fired when a key is released.
- **`PlayerKeyMatchEvent`**: Fired when a configured keybind match group (defined in `keys/` folder) is triggered.

## Command Support

The keyboard system includes built-in commands for testing and debugging:
- `/observer key iskeydown <player> <ascii>`
- `/observer key matchgroup <player> <group>`
- `/observer key flush <player>`

## Threading Notes
- Key events are received asynchronously via Netty but are **dispatched synchronously** to the main server thread. 
- You can safely interact with the Bukkit API inside the event listeners.
- The `isKeyDown` and `getPressedKeys` methods are thread-safe and can be called async.

## Compatibility Notes
- **Clients without the mod:** Will never send key events. `isKeyDown` will always return `false`.
- **Fabric/Sodium/Iris:** Fully compatible. The client mixin captures keys cleanly before passing them to the screen renderer.
- **Mac/Linux:** Keycodes are mapped accurately to the standard ASCII layout regardless of OS.

## Best Practices
- **Do not spam:** Avoid running heavy logic inside `PlayerKeyPressEvent` unless absolutely necessary.
- **Flush on teleport:** If a player is teleported across dimensions, it's good practice to call `flushKeys(player)` to prevent keys from being stuck down during loading screens.
