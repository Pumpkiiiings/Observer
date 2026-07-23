# Observer Services Reference

Los servicios en Observer (Services) se encargan de manejar lógicas específicas o coordinar acciones entre múltiples Managers y APIs. En esta arquitectura, la separación entre Manager y Service es difusa, ya que muchos Managers asumen responsabilidades de Servicio.

## `PlayAnimationHandler` (Cliente)
- **Responsabilidad**: Escuchar el payload de `PlayAnimationPayload` desde el servidor y buscar el modelo correcto del jugador en el cliente (`UUID targetPlayer`) para iniciar la animación (`animationName`).
- **Inicialización**: Se registra como listener en `UiPayloadHandler` durante el inicio del cliente.
- **Uso recomendado**: `INTERNAL`. Funciona automáticamente en segundo plano acoplándose a la capa de rendering (usualmente Fabric API u otra API de animación si existe).

## `ObserverAnimationManager`
- **Responsabilidad**: Gestionar estados de animaciones que podrían estar corriendo para el jugador actual.

*(Nota: En la arquitectura actual de Observer, gran parte de la lógica tradicional de "Servicio" recae en los Managers, que ya se detallan en `managers.md`).*
