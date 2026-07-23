# Observer Builders & Fluent APIs

Observer proporciona implementaciones del patrón Builder para simplificar la creación de estructuras de datos complejas, permitiendo un código altamente legible y encadenable (Fluent API).

## `MenuBuilder`

### Propósito
La creación de un `MenuOpenPayload` o un menú completo de Observer requiere inicializar decenas de componentes, asignarles identificadores, definir su posicionamiento (Transforms) y empaquetarlos. Hacer esto instanciando records manualmente resulta verboso e ilegible. `MenuBuilder` soluciona esto.

### Ciclo de vida
Se crea una nueva instancia temporal, se encadenan los métodos de configuración, y finalmente se llama a `.build()` u `.open(player)`.

### Métodos del Fluent API

- **`.id(String menuId)`**: Define el identificador principal del menú (necesario para manejar eventos de cierre o clics).
- **`.text(String id, String text, float x, float y)`**: (Y sus sobrecargas). Añade un componente de texto en la pantalla.
- **`.button(String id, String texturePath, float x, float y, int width, int height)`**: Añade un botón clickeable. El `id` será devuelto en el `MenuActionEvent`.
- **`.image(String id, String texturePath, float x, float y)`**: Renderiza una textura estática.

### Ejemplo de uso (Extraído)

```java
MenuBuilder.create()
    .id("main_shop")
    .text("title", "Tienda de Observer", 50, 20)
    .button("buy_sword", "textures/gui/buy.png", 50, 50, 100, 30)
    .onClose(player -> {
        player.sendMessage("Cerraste la tienda.");
    })
    .onClick((player, actionId) -> {
        if (actionId.equals("buy_sword")) {
            player.sendMessage("¡Compraste una espada!");
        }
    })
    .open(player);
```

## Internal Builders (Para `SoundDefinition`)

La clase `SoundDefinition` suele implementarse con un patrón builder interno o encadenable:
```java
SoundDefinition sound = new SoundDefinition("minecraft", "entity.zombie.ambient")
    .volume(1.0f)
    .pitch(0.8f);
```
*(El uso exacto de setters fluidos depende de la implementación específica encontrada en observer-api).*
