# Observer Code Examples

A continuación se presentan ejemplos extraídos o inferidos del uso recomendado del framework.

## Ejemplo Básico: Detección de Teclado
Cómo usar `PlayerKeyPressEvent` en el servidor:

```java
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class MyKeyboardListener implements Listener {
    @EventHandler
    public void onKeyPress(PlayerKeyPressEvent event) {
        // 32 = Espacio, 70 = Tecla 'F'
        if (event.getAsciiKey() == 70) {
            event.getPlayer().sendMessage("¡Presionaste la tecla F!");
        }
    }
}
```

## Ejemplo Recomendado: Construcción de un Menú Interactivo
Uso del `MenuBuilder` (Fluent API):

```java
public void openClassSelector(Player player) {
    MenuBuilder.create()
        .id("class_selector")
        .text("title", "Elige tu Clase", 50, 10)
        .button("btn_warrior", "textures/gui/warrior.png", 30, 40, 50, 20)
        .button("btn_mage", "textures/gui/mage.png", 80, 40, 50, 20)
        .onClick((clickPlayer, actionId) -> {
            if (actionId.equals("btn_warrior")) {
                clickPlayer.sendMessage("¡Has elegido Guerrero!");
                // Dar items...
            }
        })
        .open(player);
}
```

## Ejemplo Avanzado: Modificación de Entorno (Atmósfera)

```java
public void setBloodMoon(Player player) {
    // RGB = 0xFF0000 (Rojo)
    PaperObserverEnvironmentAPI.setSkyColor(player, 0xFF0000);
    
    // Añadir un sonido de tensión con volumen 1.0, pitch bajo (0.5)
    SoundDefinition ambientSound = new SoundDefinition("minecraft", "ambient.cave")
        .volume(1.0f)
        .pitch(0.5f);
    PaperObserverSoundAPI.playSound(player, ambientSound);
    
    // Screenshake dramático al iniciar el evento (intensidad 0.5, 60 ticks)
    PaperObserverScreenAPI.playScreenshake(player, 0.5f, 60);
}

public void endBloodMoon(Player player) {
    PaperObserverEnvironmentAPI.resetSkyColor(player);
}
```

## Ejemplo: Extensibilidad (Crear un nuevo Componente UI)
Si fueras a bifurcar/extender Observer:
1. Crea un `SliderComponent` (record) implementando `MenuComponent`.
2. Añádelo al manejo de Payloads.
3. En el cliente, crea un `SliderComponentRenderer` implementando `ComponentRenderer<SliderComponent>`.
4. Regístralo en el `RendererRegistry` de Fabric.
