package com.observer.fabric.client.input;

import com.observer.api.model.KeyAction;
import com.observer.api.model.ObserverKey;
import com.observer.api.payload.event.KeyEventPayload;
import com.observer.fabric.client.ObserverClient;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

import java.util.HashMap;
import java.util.Map;

/**
 * Layer 1 Input: Raw GLFW Key Polling.
 *
 * Captures specific raw physical key events without registering native Minecraft KeyMappings,
 * ensuring strict server-authoritative control logic and version-independent compatibility.
 */
@Environment(EnvType.CLIENT)
public final class ObserverKeybindings {

    private static final Map<Integer, KeyData> RAW_KEYS = new HashMap<>();

    private static class KeyData {
        final ObserverKey observerKey;
        boolean lastState;

        KeyData(ObserverKey observerKey) {
            this.observerKey = observerKey;
            this.lastState = false;
        }
    }

    public static void register() {
        RAW_KEYS.put(GLFW.GLFW_KEY_W, new KeyData(ObserverKey.W));
        RAW_KEYS.put(GLFW.GLFW_KEY_A, new KeyData(ObserverKey.A));
        RAW_KEYS.put(GLFW.GLFW_KEY_S, new KeyData(ObserverKey.S));
        RAW_KEYS.put(GLFW.GLFW_KEY_D, new KeyData(ObserverKey.D));
        RAW_KEYS.put(GLFW.GLFW_KEY_LEFT_SHIFT, new KeyData(ObserverKey.SHIFT));
        RAW_KEYS.put(GLFW.GLFW_KEY_LEFT_CONTROL, new KeyData(ObserverKey.CTRL));
        RAW_KEYS.put(GLFW.GLFW_KEY_SPACE, new KeyData(ObserverKey.SPACE));
        RAW_KEYS.put(GLFW.GLFW_KEY_TAB, new KeyData(ObserverKey.TAB));
        RAW_KEYS.put(GLFW.GLFW_KEY_ESCAPE, new KeyData(ObserverKey.ESC));
        RAW_KEYS.put(GLFW.GLFW_KEY_F, new KeyData(ObserverKey.F));
        RAW_KEYS.put(GLFW.GLFW_KEY_R, new KeyData(ObserverKey.R));
        RAW_KEYS.put(GLFW.GLFW_KEY_G, new KeyData(ObserverKey.G));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            // Cannot send packets if not in-game or no connection
            if (client.player == null || client.getConnection() == null) return;

            long window = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
            long timestamp = System.currentTimeMillis();

            boolean shift = hasModifier(window, GLFW.GLFW_KEY_LEFT_SHIFT) || hasModifier(window, GLFW.GLFW_KEY_RIGHT_SHIFT);
            boolean ctrl = hasModifier(window, GLFW.GLFW_KEY_LEFT_CONTROL) || hasModifier(window, GLFW.GLFW_KEY_RIGHT_CONTROL);
            boolean alt = hasModifier(window, GLFW.GLFW_KEY_LEFT_ALT) || hasModifier(window, GLFW.GLFW_KEY_RIGHT_ALT);

            for (Map.Entry<Integer, KeyData> entry : RAW_KEYS.entrySet()) {
                int glfwKey = entry.getKey();
                KeyData data = entry.getValue();

                boolean isDown = GLFW.glfwGetKey(window, glfwKey) == GLFW.GLFW_PRESS;

                if (isDown != data.lastState) {
                    data.lastState = isDown;
                    KeyAction action = isDown ? KeyAction.PRESS : KeyAction.RELEASE;

                    KeyEventPayload payload = new KeyEventPayload(
                            data.observerKey,
                            action,
                            timestamp,
                            shift,
                            ctrl,
                            alt
                    );

                    ClientPlayNetworking.send(payload);
                    ObserverClient.LOGGER.debug("[Observer] Sent key event: {} {}", data.observerKey, action);
                }
            }
        });
    }

    private static boolean hasModifier(long window, int glfwKey) {
        return GLFW.glfwGetKey(window, glfwKey) == GLFW.GLFW_PRESS;
    }
}
