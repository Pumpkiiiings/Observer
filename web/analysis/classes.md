# Observer Classes Reference

A continuación se documentan las clases principales del proyecto, categorizadas por su rol arquitectónico. No se incluyen DTOs ni Records aquí (ver `models.md`).

## Core Framework (Paper)

### `ObserverPaper`
- **Responsabilidad**: Clase principal del plugin de servidor. Extiende `JavaPlugin`.
- **Ciclo de vida**: Creada por el ClassLoader de Bukkit. Llama a la inicialización de todos los Managers en su `onEnable()` y registra los canales de mensajería Plugin Messaging.
- **Acceso**: Singleton accesible vía `JavaPlugin.getPlugin(ObserverPaper.class)`.
- **Estabilidad**: `PUBLIC` / `INTERNAL` - No debería ser instanciada por desarrolladores.

### `ObserverPlayer`
- **Responsabilidad**: Representa a un jugador conectado que ha confirmado tener el mod cliente instalado. Contiene metadata sobre la versión del cliente y las características soportadas.
- **Ciclo de vida**: Creada durante el Handshake exitoso. Eliminada en el `PlayerQuitEvent`.
- **Consumo**: Utilizada internamente por los managers para validar si es seguro enviar un Payload a ese jugador.

## Core Framework (Fabric)

### `ObserverFabric` y `ObserverClient`
- **Responsabilidad**: Entry points para el lado del cliente en Fabric (`ModInitializer` y `ClientModInitializer`). Inicializa el registro de payload listeners y renderizadores.
- **Estabilidad**: `INTERNAL`.

### `UiPayloadHandler`
- **Responsabilidad**: Escucha los paquetes entrantes desde el servidor y delega el trabajo al manejador correcto (ej. actualizar UI, cambiar cielo).
- **Ciclo de vida**: Registrado al inicio del cliente.

### `ObserverDynamicScreen`
- **Responsabilidad**: Extiende `Screen` de Minecraft. Es el lienzo principal donde se renderizan los componentes UI definidos por el servidor.
- **Estabilidad**: `INTERNAL`. Se abre/cierra automáticamente.

## Renderizado y Componentes

### `RendererRegistry` / `ComponentRenderRegistry`
- **Responsabilidad**: Mantiene un mapeo entre el tipo de Componente UI (ej. `ButtonComponent.class`) y su renderizador físico en pantalla (ej. `ComponentRenderer<ButtonComponent>`).
- **Consumo**: Interno del cliente. Permite extensibilidad (registrar nuevos tipos de componentes).

### `TextComponentRenderer`, `ItemComponentRenderer`
- **Responsabilidad**: Toman un componente de datos, extraen su Transform y lo dibujan en el `DrawContext` / `GuiGraphics` de Minecraft en el frame correspondiente.

## Sistemas de Estado (State)

### `EnvironmentState` y `ScreenEffectState`
- **Responsabilidad**: Mantienen el estado temporal del entorno (color del cielo) o los efectos de pantalla (intensidad y decadencia del screenshake) en el cliente.
- **Ciclo de vida**: Instancias Singleton (estáticas) actualizadas cada tick de renderizado.
