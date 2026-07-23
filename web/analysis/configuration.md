# Observer Configuration

El proyecto soporta configuraciones que dictan el comportamiento tanto del núcleo como de los módulos cliente.

## `ObserverConfig` (Lado del Cliente)
- **Ubicación**: Típicamente gestionado en tiempo de ejecución, permitiendo al cliente o al servidor alterar parámetros.
- **Opciones Extraídas**: 
  - La capacidad de habilitar/deshabilitar temporalmente la entrada del usuario cuando un menú de Observer está abierto.
  - Sincronización de teclas (`Key sync delay`): Controla con qué frecuencia el cliente envía el `KeysUpdatePayload` completo para evitar desincronizaciones de teclado.

## Opciones del Entorno y Sonido
- Estas no provienen de un archivo `.yml`, sino que son configurables en tiempo de ejecución a través de las APIs (ej. color del cielo).

*(Nota: El núcleo actual en `observer-paper` asume configuraciones a través del propio código de otros plugins consumidores, más que a través de un `config.yml` tradicional, maximizando la programabilidad)*.
