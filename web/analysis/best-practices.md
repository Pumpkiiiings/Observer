# Observer Best Practices (Mejores Prácticas)

Para garantizar un rendimiento óptimo y un código limpio al desarrollar plugins usando Observer, se deben seguir estos patrones y evitar ciertos antipatrones.

## Patrones Recomendados

### 1. Escuchar Eventos de Teclado en vez de Polling
✅ **HACER**: Usa `@EventHandler` para escuchar `PlayerKeyPressEvent`.
❌ **NO HACER**: No uses un bucle `BukkitRunnable` (polling) que llame a `isKeyDown()` cada tick. El polling desperdicia CPU y puede fallar si la tecla fue presionada y soltada entre ticks.

### 2. Uso del MenuBuilder
✅ **HACER**: Utiliza el `MenuBuilder` (Fluent API) para generar y abrir menús en una sola cadena.
❌ **NO HACER**: Evita instanciar manualmente las listas de `MenuComponent` y llamar al `ObserverNetworkManager` directamente. Esto acopla tu código a versiones internas que podrían cambiar.

### 3. Precarga de Assets
✅ **HACER**: Asegúrate de que las texturas y sonidos (ej. `texturePath` en `ButtonComponent`) existan en el Resource Pack del cliente *antes* de llamarlas. Si el cliente no las tiene, se renderizará el clásico cuadro de textura faltante (púrpura y negro).

## Errores Comunes y Patrones Prohibidos

### 1. Actualizaciones de Entorno Excesivas (Spamming)
❌ **PROHIBIDO**: Llamar a `PaperObserverEnvironmentAPI.setSkyColor` de manera continua (ej. creando un bucle para hacer un efecto "arcoíris" frame-por-frame).
*Por qué*: Esto satura el ancho de banda del canal de red del jugador y provocará un descarte de paquetes, lag visual y un posible kick por "Spam de paquetes". 
*Solución*: Realiza las transiciones de forma discreta o implementa el efecto interpolado directamente en el cliente.

### 2. Manipular las colecciones de Teclas
❌ **PROHIBIDO**: Modificar el `Set<Integer>` devuelto por `getPressedKeys()`.
*Por qué*: La colección de teclas es un reflejo del estado real del cliente. Alterarla en el servidor causa inconsistencias lógicas. Se recomienda tratarla siempre como inmutable.

### 3. Acceso Inseguro por Hilos (Thread Safety)
❌ **PROHIBIDO**: Llamar a métodos de las APIs o manipular estado de Bukkit desde hilos asíncronos (`runTaskAsynchronously`) sin devolver el control al hilo principal (Main Thread).
✅ **HACER**: Toda apertura de menús o cambio de entorno debe ejecutarse de forma síncrona en el Main Thread de PaperMC.
