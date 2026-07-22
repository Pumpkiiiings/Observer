package com.observer.api.model;

/**
 * Defines the 9 anchor points for HUD component positioning.
 *
 * The server sends an alignment anchor + pixel offsets (offsetX, offsetY).
 * The Fabric client resolves the final screen coordinates at render time
 * based on the current screen dimensions — the server never knows the
 * player's resolution.
 */
public enum ComponentAlignment {
    TOP_LEFT,
    TOP_CENTER,
    TOP_RIGHT,
    CENTER_LEFT,
    CENTER,
    CENTER_RIGHT,
    BOTTOM_LEFT,
    BOTTOM_CENTER,
    BOTTOM_RIGHT
}
