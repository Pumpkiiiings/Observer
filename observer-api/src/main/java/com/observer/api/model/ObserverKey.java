package com.observer.api.model;

/**
 * Defines physical keyboard keys and generic semantic actions.
 * Used by the Fabric client to send input events to the Paper server.
 */
public enum ObserverKey {
    // Letters
    A, B, C, D, E, F, G, H, I, J, K, L, M,
    N, O, P, Q, R, S, T, U, V, W, X, Y, Z,

    // Numbers
    NUM_0, NUM_1, NUM_2, NUM_3, NUM_4,
    NUM_5, NUM_6, NUM_7, NUM_8, NUM_9,

    // Modifiers & Control
    SPACE,
    SHIFT,
    CTRL,
    ALT,
    TAB,
    ESC,
    ENTER,
    BACKSPACE,

    // Mouse (for future expansion)
    MOUSE_LEFT,
    MOUSE_RIGHT,
    MOUSE_MIDDLE,

    // Semantic / Rebindable Keys (Layer 1)
    ABILITY_1,
    ABILITY_2,
    ABILITY_3,
    MENU_PRIMARY,
    MENU_SECONDARY,
    INTERACT,

    UNKNOWN
}
