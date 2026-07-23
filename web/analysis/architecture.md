# Observer Architecture Analysis

## Estructura de Paquetes y Módulos

El proyecto `Observer` está estructurado en una arquitectura modular multi-proyecto utilizando Gradle. Se divide principalmente en las siguientes capas y responsabilidades:

1. **`observer-api`**: 
   - **Responsabilidad**: Contiene los contratos, interfaces, enumeraciones y Data Transfer Objects (DTOs / Records) compartidos. Define la estructura de las cargas útiles de red (Payloads) y las estructuras de datos inmutables para menús, entornos y teclados.
   - **Dependencias**: No tiene dependencias fuertes hacia implementaciones de servidor o cliente. Es el núcleo abstracto.

2. **`observer-paper`**:
   - **Responsabilidad**: Implementación del framework en el lado del servidor (PaperMC). Se encarga de la lógica de negocio central, la gestión de conexiones de jugadores (`ObserverPlayer`), envío y recepción de paquetes (`ObserverNetworkManager`), y la exposición de las APIs para los desarrolladores (ej. `PaperObserverScreenAPI`, `PaperObserverEnvironmentAPI`).
   - **Dependencias**: Depende de `observer-api` y del API de Paper.

3. **`observer-fabric` (y `observer-fabric-26`)**:
   - **Responsabilidad**: Implementación del lado del cliente (Fabric). Actúa como el receptor "tonto" (dumb client) que escucha las instrucciones del servidor (a través de `UiPayloadHandler`) y las renderiza en pantalla (usando `ObserverDynamicScreen`, `ScreenRenderIntegration`). También rastrea la entrada del teclado del usuario (`KeyboardTrackerClient`) y notifica al servidor.
   - **Dependencias**: Depende de `observer-api`, Fabric API y Minecraft Client.

4. **`observer-example-plugin`**:
   - **Responsabilidad**: Provee un caso de uso real de cómo un desarrollador externo debe consumir las APIs públicas de `observer-paper`.

## Capas del Sistema

- **Capa de Red (Network Layer)**: Basada en `CustomPacketPayload`. Tanto el cliente como el servidor intercambian payloads predefinidos (ej. `MenuOpenPayload`, `KeyEventPayload`).
- **Capa de Estado (State Layer)**: En el cliente (`EnvironmentState`, `ScreenEffectState`), mantiene el estado actual inyectado por el servidor para renderizar.
- **Capa de API (API Layer)**: Interfaces expuestas para el consumidor, agrupadas por dominio (Sound, Screen, Keyboard, Environment).

## Patrones de Diseño Utilizados

- **Facade (Fachada)**: Las clases `PaperObserver...API` actúan como fachadas simplificadas para ocultar la complejidad de los managers y el sistema de red subyacente.
- **Builder Pattern**: Utilizado extensamente en la construcción de menús (`MenuBuilder`), permitiendo configuraciones encadenables (Fluent API).
- **Observer / Listener**: El servidor utiliza el sistema de eventos de Bukkit para reaccionar a acciones (ej. `KeyActionManager implements Listener`).
- **Data Transfer Object (DTO)**: Todos los `*Payload` (ej. `HandshakePayload`, `ClearHUDPayload`) y componentes de menú (`ButtonComponent`, `TextComponent`) son `records` inmutables utilizados puramente para el transporte de datos.
- **Registry / Singleton**: Uso de registros centrales (ej. `RendererRegistry`, `ComponentRenderRegistry`) para manejar instanciaciones y renderizadores.

## Principios SOLID Aplicados

- **Single Responsibility Principle (SRP)**: Alta cohesión. Cada manager tiene una única responsabilidad (ej. `KeyInputManager` solo maneja teclas, `ObserverNetworkManager` solo maneja paquetes).
- **Interface Segregation Principle (ISP)**: Interfaces pequeñas y específicas como `ObserverComponent`, `LayoutComponent`.
- **Dependency Inversion Principle (DIP)**: El servidor y el cliente dependen de las abstracciones definidas en `observer-api` (los Payloads y Componentes), no de las implementaciones del otro.

## Flujo Interno (High Level)

1. El jugador se conecta al servidor. Se crea un `ObserverPlayer` y se realiza un Handshake (`HandshakePayload`) para verificar compatibilidad de versiones.
2. Un desarrollador utiliza un Manager/API en el servidor para crear una acción (ej. mostrar un menú).
3. El servidor serializa el estado en un `Payload` (ej. `MenuOpenPayload`) y lo envía a través del `ObserverNetworkManager`.
4. El cliente (Fabric) recibe el payload en `UiPayloadHandler`.
5. El cliente actualiza su estado interno (ej. `MenuState`) y desencadena un cambio visual (ej. abrir `ObserverDynamicScreen`).
6. Si el jugador interactúa (ej. presiona una tecla o botón), el cliente envía un payload de regreso (`PlayerActionPayload` o `KeyEventPayload`).
7. El servidor recibe la acción, dispara un Evento de Bukkit (`ObserverKeyEvent`), y los plugins escuchan y reaccionan a dicho evento.
