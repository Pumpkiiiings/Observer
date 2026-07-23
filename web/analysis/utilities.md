# Observer Utilities Reference

Las utilidades (Utilities) son clases con métodos estáticos auxiliares para resolver tareas repetitivas y específicas del framework.

## `ObserverTextParser`

- **Propósito**: Convierte y formatea cadenas de texto crudas o JSON en representaciones válidas de componentes de texto (`Component` de Adventure API u otra librería de chat).
- **Cuándo usarla**: Cuando se necesita leer configuraciones de texto desde archivos externos y renderizarlas en el `ObserverDynamicScreen`.
- **Limitaciones**: Solo soporta los tags y marcadores de color que hayan sido implementados. Si el usuario ingresa un formato no soportado, podría fallar o renderizarse en bruto.

## Funciones Auxiliares (Wrappers implícitos)

- Algunas utilidades pueden estar empaquetadas dentro de `ObserverNetworkManager` (ej. utilidades de serialización de ByteBuffers).
- **Cuándo usarlas**: `INTERNAL`. No destinadas a consumidores de la API.
