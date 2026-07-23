# Observer Glossary (Glosario Técnico)

Este glosario define los términos arquitectónicos clave utilizados en el proyecto Observer.

- **ASCII Key**: Un código entero que representa un carácter de teclado según el estándar American Standard Code for Information Interchange (ej. 32 para el espacio, 65 para 'A').
- **Component (UI)**: Unidad mínima de construcción en la pantalla interactiva de Observer. Puede ser Texto (`TextComponent`), una Textura estática (`TextureComponent`) o un Botón interactivo (`ButtonComponent`).
- **DrawContext / GuiGraphics**: La clase interna de Minecraft que provee métodos OpenGL encapsulados para renderizar figuras y texturas en la pantalla.
- **Handshake (Apretón de manos)**: Proceso inicial de negociación donde el servidor y el cliente intercambian versiones (vía `HandshakePayload`) para asegurar compatibilidad.
- **MenuBuilder**: Patrón de diseño Builder expuesto por Observer para permitir a los desarrolladores crear menús jerárquicos a través de métodos encadenables (`.button().text().open()`).
- **Observer-Enabled Player**: Un jugador de Bukkit (`Player`) que ha completado exitosamente el Handshake, probando que el mod de cliente está instalado y corriendo activamente.
- **Payload / CustomPacketPayload**: Un paquete de red predefinido, serializado y enviado entre el servidor y el cliente a través de Plugin Channels (`observer:main`).
- **Plugin Channel**: La tubería subyacente de comunicación de red permitida por Minecraft/Bukkit para enviar datos personalizados sin modificar el protocolo central del juego.
- **Screenshake**: Efecto visual aplicado en el cliente donde la cámara tiembla para simular impactos o explosiones.
- **Thread-Safety (Seguridad de Hilos)**: La capacidad del código de funcionar correctamente (sin Race Conditions ni estados inconsistentes) cuando es ejecutado por múltiples hilos de la CPU simultáneamente.
- **Transform / MenuTransform**: Objeto que define las coordenadas absolutas o relativas (X, Y, capa de profundidad, alfa) en las que se renderizará un componente en la pantalla.
