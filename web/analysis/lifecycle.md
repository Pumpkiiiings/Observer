# Observer Lifecycle (Ciclo de Vida)

Este documento detalla el ciclo de vida del framework Observer, desde el arranque del servidor/cliente hasta la desconexión.

## 1. Inicialización (Initialization)

### Lado del Servidor (`ObserverPaper`)
1. Bukkit carga el plugin y ejecuta `onLoad()`.
2. Se llama a `onEnable()`.
3. Se instancian los Managers: `ObserverNetworkManager`, `ObserverPlayerManager`, `KeyInputManager`, `ObserverMenuManager`, `ObserverEnvironmentManager`.
4. El `ObserverNetworkManager` registra el canal entrante/saliente `"observer:main"` con la API de mensajería de Bukkit.
5. Se registran los comandos (ej. `ObserverCommand`) y los Event Listeners de Bukkit.

### Lado del Cliente (`ObserverFabric` / `ObserverClient`)
1. Fabric Loader carga el mod y llama a `onInitializeClient()`.
2. Se registra el Payload Receiver para el canal `"observer:main"`.
3. Se inicializa el `KeyboardTrackerClient` registrando hooks en el bucle principal de ticks del cliente (ClientTickEvents) para leer el estado del teclado.
4. Se inyectan los Mixins (`GuiMixin`, `EntityMixin`) en el juego base.

## 2. Conexión del Jugador (Player Join & Handshake)

1. El jugador entra al servidor (`PlayerJoinEvent` en Bukkit).
2. El `ObserverPlayerManager` envía un paquete de Handshake de inicialización al cliente.
3. El cliente, a través del Payload Handler, recibe el ping y responde con su propio `HandshakePayload`, informando su `protocolVersion`, `observerVersion` y un `EnumSet<ObserverFeature>`.
4. El servidor verifica la respuesta. Si es compatible, crea una instancia de `ObserverPlayer` y lo marca como "Observer-Enabled". A partir de este momento, el jugador puede recibir menús y efectos.

## 3. Fase Activa (Active Phase)

- El jugador juega normalmente.
- **Teclado**: El cliente revisa cada frame/tick las teclas. Si cambian, envía un `KeyEventPayload`. Periódicamente envía un `KeysUpdatePayload` para prevenir desincronización.
- **Menús**: Si el servidor envía un `MenuOpenPayload`, el cliente pausa el control de juego y abre el `ObserverDynamicScreen`.
- **Efectos**: El servidor envía paquetes (screenshake, skycolor) y el cliente modifica variables estáticas en `EnvironmentState` y `ScreenEffectState`, que son leídas por el motor de renderizado (`ScreenRenderIntegration`, `GuiMixin`) cada frame.

## 4. Desconexión del Jugador (Player Quit)

1. Bukkit dispara `PlayerQuitEvent`.
2. El `ObserverPlayerManager` destruye la instancia de `ObserverPlayer` para ese UUID.
3. Se limpia cualquier mapa asociado al UUID en los Managers (ej. limpiar teclas presionadas en `KeyInputManager`, limpiar entorno en `ObserverEnvironmentManager`).
4. Prevención de memory leaks (Fugas de memoria).

## 5. Apagado (Shutdown)

1. Bukkit ejecuta `onDisable()` en `ObserverPaper`.
2. Se desregistran los canales de red para evitar errores si se usa `/reload` (aunque no recomendado).
3. Se vacían todas las estructuras de datos en memoria.
