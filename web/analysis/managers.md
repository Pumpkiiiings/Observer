# Observer Managers Reference

Los Managers (Gestores) son las clases singleton o de instancia única en el ciclo de vida del plugin de Paper que contienen la lógica pesada y la administración de estado.

## `ObserverNetworkManager`
- **Responsabilidades**: 
  - Registrar y gestionar los canales de mensajería (Plugin Channels) de Bukkit (`observer:main`).
  - Serializar y deserializar Payloads (usando ByteBuffer / DataOutputStream).
  - Enviar payloads individuales a un jugador o en `broadcast` a múltiples jugadores.
- **Ciclo de vida**: Se instancia una única vez durante el `onEnable` de `ObserverPaper`. Persiste hasta `onDisable`.
- **Acceso recomendado**: Generalmente **NO RECOMENDADO (INTERNAL)**. Los desarrolladores deben usar las fachadas `*API`, no el NetworkManager directamente.

## `ObserverPlayerManager`
- **Responsabilidades**:
  - Manejar los eventos de conexión (`PlayerJoinEvent`) y desconexión (`PlayerQuitEvent`).
  - Enviar el `HandshakePayload` al unirse el jugador para determinar si tiene el mod instalado (y qué features soporta).
  - Mantener un registro en memoria de los objetos `ObserverPlayer` conectados y validados.
- **Ciclo de vida**: Creado en `onEnable`. Implementa `Listener`.
- **Acceso recomendado**: A través de sus métodos públicos para verificar si un jugador es "Observer-Enabled".

## `ObserverMenuManager`
- **Responsabilidades**:
  - Almacenar temporalmente los menús registrados (`ObserverMenu` y `TestMenu`).
  - Recibir el tráfico de red de retorno (`MenuActionPayload`) e invocar los lambdas de callback en los menús de Bukkit o disparar `MenuActionEvent`.
- **Ciclo de vida**: Instanciado en `onEnable`.
- **Acceso recomendado**: Exclusivamente a través de la clase constructora `MenuBuilder` o las APIs de menú.

## `KeyInputManager` / `ObserverKeyboardManager`
- **Responsabilidades**:
  - Mantener un mapa (usualmente `Map<UUID, Set<Byte>>` o similar) con las teclas actualmente presionadas de cada jugador.
  - Actualizar este estado concurrentemente cuando se reciben paquetes `KeysUpdatePayload`.
  - Proveer la funcionalidad subyacente para `PaperObserverKeyboardAPI`.
- **Ciclo de vida**: Único. 
- **Acceso recomendado**: A través de `PaperObserverKeyboardAPI`.

## `ObserverEnvironmentManager`
- **Responsabilidades**:
  - Rastrear el estado actual del cielo / clima personalizado de cada jugador conectado para reenviarlo si cambian de mundo o re-ingresan.
- **Ciclo de vida**: Único.
- **Acceso recomendado**: A través de `PaperObserverEnvironmentAPI`.
