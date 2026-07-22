# Migration Guide

This guide covers migrating from earlier internal or unstable Observer builds to the new public API.

## Breaking Changes

### Keyboard Manager (Internal)
Previously, some plugins may have accessed `ObserverPaper.getInstance().getKeyboardManager()` directly. This internal manager has been locked down and its methods may change.

**Old Way:**
```java
ObserverPaper.getInstance().getKeyboardManager().isKeyDown(player.getUniqueId(), (byte) 71);
```

**New Way:**
```java
PaperObserverKeyboardAPI.isKeyDown(player, 'G'); // Uses ASCII char natively
```

### Environment Synchronization
You no longer need to construct `EnvironmentUpdatePayload` packets yourself using ByteBufs. The API handles payload construction automatically.

**Old Way:**
```java
EnvironmentUpdatePayload payload = new EnvironmentUpdatePayload(EnvironmentUpdateType.SET_SKY_COLOR, 255, 0, 0, false, 0, 0, 0);
ObserverAPI.send(player, "observer:environment", payload, EnvironmentUpdatePayload.CODEC);
```

**New Way:**
```java
PaperObserverEnvironmentAPI.setSkyColor(player, 0xFF0000);
```

## Why the changes?

The new API introduces the `observer-api` core module which abstracts away Bukkit specifics. This ensures that:
1. Payloads can be safely refactored internally without breaking your plugins.
2. The exact same plugin logic can eventually be ported to NeoForge or Velocity when those Observer implementations are released.
3. Thread safety is handled by the API internally (e.g., buffering packets if sent asynchronously).

## Removed Features
- `ObserverAPI.send()` is now deprecated for internal packets. Use the respective Facades instead.
- Internal packet codecs are no longer guaranteed to be stable between minor versions. Always use the API!
