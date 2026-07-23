package com.observer.api.payload.screen;

/**
 * Types of screen effects that can be applied to a player.
 */
public enum ScreenEffectType {
    /** Shakes the player's screen with configurable intensity and duration. */
    SCREENSHAKE,
    /** Applies a color tint/vignette overlay on the player's screen. */
    TINT,
    /** Applies a vignette overlay on the player's screen. */
    VIGNETTE
}
