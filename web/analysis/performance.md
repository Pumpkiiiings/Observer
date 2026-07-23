# Observer Performance Considerations (Rendimiento)

Observer ha sido diseñado para minimizar el impacto en el rendimiento tanto del servidor (TPS) como del cliente (FPS).

## Servidor (PaperMC)
- **Networking**: Los Payloads se envían utilizando buffers binarios. Esto es altamente eficiente en comparación con enviar cadenas JSON o estructuras complejas.
- **Memoria**: El estado interno que guarda el servidor (ej. estado de teclas presionadas en `KeyInputManager`) utiliza estructuras de datos livianas como `Set<Byte>`, minimizando la huella de memoria (GC pressure) por jugador.
- **Recomendación para Desarrolladores**: No instanciar o enviar Menús u otros Payloads dentro del bucle de ticks principal sin una verificación de cambios previa. Reconstruir interfaces en cada frame saturará el canal de red del jugador.

## Cliente (Fabric)
- **Renderizado UI**: El `ObserverDynamicScreen` hereda del renderizador nativo de Minecraft y soporta renderizado por lotes (batching) en el `DrawContext`. Sin embargo, si un desarrollador envía un menú con miles de componentes simultáneos, los FPS del cliente caerán dramáticamente debido al overdraw.
- **Teclado**: El rastreo del teclado se ejecuta cada frame. La evaluación es muy barata ($O(1)$) consultando los key bindings nativos. Solo se envía un paquete cuando hay un evento real (KeyDown/KeyUp) o una sincronización en el tick timer.
- **Mixins**: Los Mixins inyectados (como `GuiMixin`, `EntityMixin`) interceptan el código a nivel de byte. Son altamente eficientes y operan sin reflexión en tiempo de ejecución.
