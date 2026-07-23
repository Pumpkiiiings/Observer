# Observer Interfaces Reference

Las interfaces de Observer definen los contratos para los diferentes dominios del proyecto, asegurando bajo acoplamiento entre el núcleo, las APIs y los clientes.

## Interfaces de Dominio (Domain API)

### `ObserverEnvironmentAPI`
- **Propósito**: Definir el contrato para la manipulación del entorno.
- **Implementaciones**: Implementada indirectamente por `PaperObserverEnvironmentAPI` (fachada) o las clases internas del servidor.
- **Cuándo extender**: No se recomienda extender a menos que se implemente un servidor proxy / versión hibrida (ej. Folia, Velocity).

### `ObserverKeyboardAPI`
- **Propósito**: Contrato para el rastreo del teclado.
- **Contratos principales**:
  - `isKeyDown(UUID, int)`
  - `getPressedKeys(UUID)`

### `ObserverSoundAPI`
- **Propósito**: Contrato para emitir comandos de audio al cliente.

## Interfaces UI & Componentes

### `MenuComponent`
- **Propósito**: Interfaz marcadora (Marker Interface) para cualquier DTO que represente un elemento renderizable en un menú enviado al cliente.
- **Implementaciones**: `ButtonComponent`, `TextComponent`, `TextureComponent`.
- **Cuándo implementar**: Cuando necesites añadir un nuevo tipo de elemento UI (Ej. `SliderComponent`). Sin embargo, para que funcione, debes también implementar un renderizador en el cliente y un DTO.

### `LayoutComponent`
- **Propósito**: Definir estructuras de layout agrupables (contenedores).
- **Implementaciones**: `ItemComponentImpl`, `TextComponentImpl`.

### `MenuRenderer`
- **Propósito**: Contrato abstracto para un renderizador global.

### `ComponentRenderer<T extends LayoutComponent>`
- **Propósito**: Interfaz parametrizada para clases que saben cómo dibujar un componente específico en pantalla (Lado cliente).
- **Implementaciones**: `ItemComponentRenderer`, `TextComponentRenderer`.
- **Cuándo implementar**: Estrictamente necesario si implementas un nuevo `MenuComponent` personalizado. Debes implementarla en el cliente y registrarla en el `RendererRegistry`.

## Interfaces de Compatibilidad

### `ObserverComponent`
- **Propósito**: Interfaz general utilizada internamente para unificar el polimorfismo de distintos tipos de componentes antes de ser inyectados en la jerarquía del DOM del `ObserverDynamicScreen`.
