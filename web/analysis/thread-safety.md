# Observer Thread Safety & Concurrency

En la arquitectura cliente-servidor de Minecraft, la gestión de hilos es crítica. Observer sigue las convenciones estándar de PaperMC y Fabric.

## Lado del Servidor (Paper)

### Hilo Principal (Main Thread)
La gran mayoría de las llamadas a la API pública de Observer (`PaperObserverScreenAPI`, `MenuBuilder`) **deben** hacerse desde el Main Thread del servidor Bukkit. Las llamadas asíncronas no están garantizadas como seguras y podrían lanzar excepciones internas en el sistema de red de Bukkit si se intenta enviar un paquete mientras el buffer está siendo limpiado.

### Manejo de Red
El `ObserverNetworkManager` recibe paquetes (Payloads) desde Netty (que normalmente es asíncrono o de I/O), pero la API de PaperMC despacha eventos de PluginMessage de manera síncrona al hilo principal.
- Por tanto, eventos como `MenuActionEvent` o `PlayerKeyPressEvent` son disparados **sincrónicamente** en el Main Thread.
- Es seguro modificar bloques, dar ítems o invocar Bukkit API directamente dentro del manejador de estos eventos.

## Lado del Cliente (Fabric)

### El Hilo de Netty vs Main Client Thread
Cuando el cliente recibe un paquete (ej. `MenuOpenPayload`), el `UiPayloadHandler` intercepta los datos en el hilo de red.
- **Crítico**: El hilo de red NO tiene permiso para modificar el estado de la UI (OpenGL / Renderizado).
- El código en el cliente utiliza el contexto del cliente para encolar una tarea en el Main Thread (ej. `client.execute(() -> { client.setScreen(new ObserverDynamicScreen(...)) })`).

### Concurrencia de Estado (State Concurrency)
Clases estáticas como `EnvironmentState` pueden ser leídas simultáneamente por el hilo de Render (al dibujar el cielo) y escritas por el hilo que procesa los paquetes. Observer mitiga problemas de concurrencia inyectando las actualizaciones de estado en el Main Thread (mismo hilo que el render) usando `client.execute(...)`, garantizando ausencia de Race Conditions sin necesidad de pesados bloqueos `synchronized` o `ReentrantLock`.
