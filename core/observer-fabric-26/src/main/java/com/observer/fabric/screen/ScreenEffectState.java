package com.observer.fabric.screen;

/**
 * Holds the current active screen effect state on the client.
 * Updated by ScreenEffectPayloadHandler when a payload is received from the server.
 * Read by ScreenRenderIntegration each frame to apply the effect.
 */
public class ScreenEffectState {

    // --- Screenshake ---
    public static volatile boolean screenshakeActive = false;
    public static volatile float screenshakeIntensity = 0f;
    public static volatile int screenshakeTicksRemaining = 0;

    // --- Tint ---
    public static volatile boolean tintActive = false;
    public static volatile int tintR = 0;
    public static volatile int tintG = 0;
    public static volatile int tintB = 0;
    public static volatile float tintAlpha = 0f;
    public static volatile int tintTicksRemaining = 0;
    public static volatile int tintTotalTicks = 0;

    // --- Vignette ---
    public static volatile boolean vignetteActive = false;
    public static volatile int vignetteR = 0;
    public static volatile int vignetteG = 0;
    public static volatile int vignetteB = 0;
    public static volatile float vignetteAlpha = 0f;
    public static volatile int vignetteTicksRemaining = 0;
    public static volatile int vignetteTotalTicks = 0;

    /**
     * Called every client tick to count down active effects.
     */
    public static void tick() {
        if (screenshakeActive) {
            screenshakeTicksRemaining--;
            if (screenshakeTicksRemaining <= 0) {
                screenshakeActive = false;
                screenshakeIntensity = 0f;
            }
        }

        if (tintActive) {
            tintTicksRemaining--;
            if (tintTicksRemaining <= 0) {
                tintActive = false;
                tintAlpha = 0f;
            }
        }

        if (vignetteActive) {
            vignetteTicksRemaining--;
            if (vignetteTicksRemaining <= 0) {
                vignetteActive = false;
                vignetteAlpha = 0f;
            }
        }
    }

    /**
     * Clears all active effects immediately.
     */
    public static void reset() {
        screenshakeActive = false;
        screenshakeIntensity = 0f;
        screenshakeTicksRemaining = 0;

        tintActive = false;
        tintR = 0;
        tintG = 0;
        tintB = 0;
        tintAlpha = 0f;
        tintTicksRemaining = 0;
        tintTotalTicks = 0;

        vignetteActive = false;
        vignetteR = 0;
        vignetteG = 0;
        vignetteB = 0;
        vignetteAlpha = 0f;
        vignetteTicksRemaining = 0;
        vignetteTotalTicks = 0;
    }
}
