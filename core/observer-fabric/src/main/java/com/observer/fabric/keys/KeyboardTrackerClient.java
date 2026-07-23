package com.observer.fabric.keys;

import com.observer.api.payload.keys.KeysUpdatePayload;
import com.observer.fabric.ObserverFabric;
import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

import java.util.HashSet;
import java.util.Set;

public class KeyboardTrackerClient {
    private static Set<Byte> lastPressedKeys = new HashSet<>();

    private static boolean isKeyPressed(int keyCode) {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), keyCode);
    }

    public static void initialize() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                return;
            }

            Set<Byte> currentKeys = new HashSet<>();
            // Loop from 32 to 127 exactly like KeyboardDetector requested
            for (byte ascii = 32; ascii < 127; ascii++) {
                if (isKeyPressed(ascii)) {
                    currentKeys.add(ascii);
                }
            }

            // Only send if the keys have changed since last tick (avoids lag)
            if (!currentKeys.equals(lastPressedKeys)) {
                // If debug mode is on (to be added via config sync, but for now we just log if enabled locally)
                // We'll log everything.
                ObserverFabric.LOGGER.info("[Observer Keys Client] Key state changed. Pressed: {}", currentKeys);
                
                ClientPlayNetworking.send(new KeysUpdatePayload(currentKeys));
                lastPressedKeys = currentKeys;
            }
        });
    }
}
