# 👁️ Observer

[![PaperMC](https://img.shields.io/badge/PaperMC-1.21.4-gray.svg?style=flat-square)](https://papermc.io/)
[![Fabric](https://img.shields.io/badge/Fabric-1.21.4-blue.svg?style=flat-square)](https://fabricmc.net/)
[![Version](https://img.shields.io/badge/Version-1.0.1-green.svg?style=flat-square)](https://github.com/Pumpkiiiings/Observer)

**Observer** is a powerful Client-Server synchronization ecosystem for Minecraft that enables server-side developers (Paper) to natively manipulate the client's screen, HUD, keyboard inputs, and audio without relying on Server Resource Packs or vanilla workarounds.

By pairing a **Paper Plugin** with a **Fabric Client Mod**, Observer bridges the gap between server logic and client-side rendering.

## 🌟 Key Features

* 🖥️ **Dynamic HUD System:** Draw, move, update, and remove HUD components (Text, Items, and Images) natively on the client's screen directly from the server.
* ⌨️ **Keyboard Input API:** Listen to the client's raw keyboard inputs (Key Press, Key Release, Key Hold) on the server in real-time, mapped to native Bukkit Events.
* 🔊 **Advanced Audio Engine:** Play, track, and stop directional or ambient sounds instantly. Bypasses the limitations of vanilla `/playsound`.
* 🎬 **Screen Effects Engine:** Deliver immersive feedback by sending **Screenshake** (camera shaking) or **Screen Tint** (dynamic color overlay blending) packets to the client when taking damage, freezing, or experiencing explosions.
* ⚡ **Zero-Reflection Protocol:** Fully unified payload system resolving classloader mappings between Mojang mappings (Paper) and Yarn mappings (Fabric) at runtime dynamically.

---

## 📦 Modules Structure

The project is divided into several subprojects:

* `observer-api`: The core payload and network channels protocol shared across all modules.
* `observer-paper`: The Paper plugin and Server API meant to be used by server-side developers.
* `observer-client-api`: The core interfaces and client-side network handling used by the Fabric implementation.
* `observer-fabric-26`: The Fabric Mod implementation (specifically targeted for 1.21.4) that receives packets and renders the actual graphics/effects.

---

## 🛠️ API Usage Examples (Server-Side)

Observer provides an easy-to-use API via the `PaperObserverScreenAPI`, `PaperObserverHUDAPI`, `PaperObserverKeyboardAPI`, and `PaperObserverSoundAPI` classes.

### 1. Playing a Screen Effect (Screenshake & Tint)
Give your players immersive visual feedback when they are hit by custom abilities.
```java
// Play a screenshake with 0.6 intensity for 15 ticks
PaperObserverScreenAPI.playScreenshake(player, 0.6f, 15);

// Apply a red screen tint (blood effect) with 40% opacity for 20 ticks
PaperObserverScreenAPI.playScreenTint(player, 255, 50, 0, 0.4f, 20);

// Apply a blue screen tint (freeze effect) with smooth fade-in and fade-out
PaperObserverScreenAPI.playScreenTint(player, 100, 180, 255, 0.35f, 30, 10, 10);
```

### 2. Creating a Custom HUD Component
Render an element natively on the client screen.
```java
ObserverComponent component = new ObserverComponent("my_custom_text", ComponentType.TEXT)
    .setText("Hello from the Server!")
    .setAlignment(ComponentAlignment.CENTER)
    .setPosition(0, -50);

// Send the component to the player
PaperObserverHUDAPI.sendComponent(player, component);

// Later, update the text dynamically without re-creating the component
PaperObserverHUDAPI.updateText(player, "my_custom_text", "Updated Text!");
```

### 3. Listening to Keyboard Inputs
No need to rely on player movement packets or swapping items to detect inputs.
```java
@EventHandler
public void onPlayerKeyMatch(PlayerKeyMatchEvent event) {
    if (event.getKeyName().equalsIgnoreCase("key.keyboard.f")) {
        event.getPlayer().sendMessage("You pressed 'F'!");
    }
}
```

### 4. Advanced Audio Control
Start and stop audio precisely.
```java
// Play a sound to a player
PaperObserverSoundAPI.playSound(player, "minecraft:music_disc.pigstep", 1.0f, 1.0f);

// Stop the sound immediately before it finishes naturally
PaperObserverSoundAPI.stopSound(player, "minecraft:music_disc.pigstep");
```

### 5. Player Actions & Animations API
Detect when a player walks, runs, jumps, or clicks natively from the client, and instantly play custom Blockbench animations without lag.
```java
@EventHandler
public void onPlayerWalk(ObserverPlayerWalkEvent event) {
    if (event.isWalking()) {
        PaperObserverAnimationAPI.playAnimation(event.getPlayer(), "walk_animation");
    } else {
        PaperObserverAnimationAPI.stopAnimation(event.getPlayer());
    }
}

@EventHandler
public void onPlayerLeftClick(ObserverPlayerLeftClickEvent event) {
    PaperObserverAnimationAPI.playAnimation(event.getPlayer(), "attack_animation");
}
```

---

## 📥 Installation

1. **Server (Paper 1.21.4)**
   - Download the `observer-paper.jar` from the releases.
   - Drop it into your server's `plugins/` folder.
   
2. **Client (Fabric 1.21.4)**
   - Download the `observer-fabric-26.jar`.
   - Drop it into your Minecraft `mods/` folder.
   - Requires **Fabric API**.

*(Note: The server plugin can run independently, but players without the Fabric mod will simply not see the HUDs or screen effects).*

## 🔨 Building from Source

To compile the project yourself, clone the repository and run the Gradle build task:

```bash
git clone https://github.com/Pumpkiiiings/Observer.git
cd Observer
./gradlew build
```

The compiled jars will be available in the `build/libs` directory of each subproject:
- `observer-paper/build/libs/`
- `observer-fabric-26/build/libs/`
