# Observer Internal Flow (Paso a Paso)

Este documento detalla el flujo de ejecución interno de una llamada típica a la API de Observer, desde el plugin del servidor hasta la pantalla del cliente. Tomaremos como ejemplo la apertura de un Menú.

## Flujo: Apertura de un Menú UI

### 1. Invocación de la API (Server)
Un desarrollador llama a `MenuBuilder.create()...open(player)`.

### 2. Conversión a Payload (Server)
El `MenuBuilder` recolecta todos los `MenuComponent` (DTOs), genera el objeto inmutable `MenuOpenPayload`, y lo pasa al `ObserverNetworkManager`.

### 3. Serialización (Server -> Red)
El `ObserverNetworkManager` toma el `MenuOpenPayload`. Dado que los `CustomPacketPayload` de Minecraft requieren un códec, los datos son convertidos a bytes y escritos en un buffer. Se adjunta el identificador del canal (`"observer:main"`) y se envía a través de la tubería de Bukkit hacia la conexión del cliente objetivo.

### 4. Deserialización y Ruteo (Cliente)
El cliente recibe el paquete en el hilo de red (Netty).
El `UiPayloadHandler` del cliente (registrado en Fabric) intercepta el paquete `"observer:main"`, lee el byte discriminador (ID del payload) y reconstruye el objeto `MenuOpenPayload` de vuelta a un record de Java.

### 5. Actualización de Estado y Render (Cliente)
El `UiPayloadHandler` determina que es un menú. Se agenda una tarea en el hilo principal del cliente (Main Client Thread, ya que no se pueden modificar UIs desde el hilo de red).
Una vez en el hilo principal, Minecraft abre la pantalla `ObserverDynamicScreen`, pasándole la lista de `MenuComponent`.

### 6. Ejecución del Render Loop (Cliente)
Cada frame (60+ veces por segundo), el método `render()` de `ObserverDynamicScreen` se ejecuta:
- Itera sobre todos los componentes del payload.
- Por cada componente, consulta al `ComponentRenderRegistry` por su renderizador (`MenuRenderer`).
- El renderizador toma el objeto `MenuTransform` (X, Y, capa Z, alpha) y utiliza `DrawContext` para pintar texturas y texto en pantalla.

### 7. Interacción del Usuario (Cliente -> Server)
El usuario hace clic en un botón. `ObserverDynamicScreen` captura el clic del mouse y sus coordenadas. Determina si intercepta un `ButtonComponent` válido.
Si es así, crea un `MenuActionPayload` con el ID del menú y el ID del botón.
Lo envía por la red hacia el servidor.

### 8. Callback y Resolución (Server)
El servidor recibe el paquete. El `ObserverNetworkManager` lo pasa al `ObserverMenuManager`.
El Manager dispara el `MenuActionEvent`.
Si el desarrollador había asociado un bloque `onClick` en el `MenuBuilder`, se ejecuta. El desarrollador puede entonces realizar una acción de juego (ej. dar un ítem) basándose en este input.
