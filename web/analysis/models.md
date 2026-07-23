# Observer Models & DTOs Reference

La capa de datos (Models) de Observer está construida principalmente con **Java Records**, garantizando inmutabilidad, thread-safety inherente, y facilidad de serialización/deserialización para la red.

## Payloads de Red (Network DTOs)

Todos los payloads implementan la interfaz `CustomPacketPayload`. Son utilizados exclusivamente para el transporte de datos entre el servidor Paper y el cliente Fabric.

### Server -> Client (Outbound)
- **`MenuOpenPayload`**: Contiene `String menuId` y `List<MenuComponent> components`. Abre un UI custom.
- **`ClearHUDPayload`**: Sin parámetros. Limpia la pantalla de componentes.
- **`EnvironmentUpdatePayload`**: Contiene el tipo de actualización (ej. `EnvironmentUpdateType.SKY_COLOR`) y datos adicionales (R, G, B).
- **`ScreenEffectPayload`**: Contiene el tipo de efecto (`ScreenEffectType.SCREENSHAKE`), intensidad, ticks de duración y variables extra (x, y, z).
- **`PlayAnimationPayload`**: Contiene `UUID targetPlayer` y `String animationName`.
- **`ComponentCreatePayload` / `ComponentRemovePayload`**: Permite añadir o eliminar dinámicamente elementos a un menú ya abierto sin reenviar todo el layout.

### Client -> Server (Inbound)
- **`HandshakePayload`**: Contiene `protocolVersion`, `observerVersion` y `EnumSet<ObserverFeature>`. Enviado al conectarse para verificar la integridad.
- **`KeyEventPayload`**: Contiene el código ASCII cuando ocurre un evento discreto de Presionar/Soltar.
- **`KeysUpdatePayload`**: Envío periódico (sincronización masiva) de un `Set<Byte>` con todas las teclas actualmente presionadas. Previene pérdida de estado por lag de red.
- **`MenuActionPayload`**: Contiene `menuId` y `reference` (ID del botón).
- **`PlayerActionPayload`**: Contiene `PlayerActionType` (ej. `JUMP`, `SNEAK`, `SPRINT`).

## Modelos de Dominio (Domain Models)

### Componentes de UI (UI Components)
Implementan la interfaz `MenuComponent`.
- **`ButtonComponent`**: `id`, `transform`, `texturePath`, `width`, `height`.
- **`TextComponent`**: `id`, `transform`, `text`, `scale`.
- **`TextureComponent`**: Similar al botón pero no es interactuable.

### Estructuras UI Anidadas
- **`MenuTransform`**: Contiene X, Y, capa Z (`layer`), opacidad (`alpha`). Usado para el renderizado.
- **`LayoutData`**: Define contenedores (Layouts) y alineaciones.
- **`Style`**: Opciones de estilo visual para un componente.

### Entorno y Audio
- **`SoundDefinition`**: Clase (builder-friendly) que almacena `namespace`, `soundId`, `volume`, `pitch`, y coordenadas (X, Y, Z).
