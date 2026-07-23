# Observer Limitations & Internal APIs

Al desarrollar con el framework Observer, existen ciertas limitaciones y características experimentales.

## Limitaciones (Restrictions)
- **Dependencia de Cliente**: La mayor limitación de Observer es que requiere **obligatoriamente** que el jugador instale el mod cliente (`observer-fabric` o sus variantes). Los jugadores con clientes Vanilla no verán interfaces personalizadas ni escucharán los eventos de sonido.
- **Network Limits (Minecraft Packet Size)**: Los Payloads grandes, como un Menú con una cantidad masiva de texto, están sujetos a las limitaciones del tamaño máximo del paquete de Minecraft (típicamente 32KB a 2MB, dependiendo del canal).
- **Asincronía en Teclado**: No es posible prevenir el movimiento físico (cancelar un evento WASD de forma predictiva estricta) desde el servidor basándose en eventos de teclado debido a la latencia inherente de la red (ping). 

## APIs Internas (Internal APIs)
Las siguientes clases se consideran **INTERNAL** (Uso Exclusivo del Core):
- `ObserverNetworkManager`: Administra la conexión nativa de Payloads.
- `UiPayloadHandler`: El enrutador de paquetes del cliente.
- `ObserverDynamicScreen`: La instancia física del GUI de Minecraft. Modificarla usando Java Reflection está prohibido, ya que puede corromper el hilo de OpenGL.

## APIs Experimentales (Experimental)
- Componentes 3D o interacciones complejas de layout podrían no tener soporte total de alineación en todas las resoluciones o escalas de GUI (GUI Scale 1, 2, 3, Auto). La alineación actual de la UI depende fuertemente de las coordenadas dadas y podría requerir iteración.
