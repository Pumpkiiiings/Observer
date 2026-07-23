package com.observer.api.menu.layout;

/**
 * Defines the layout properties for a menu or a container element.
 */
public record LayoutData(
        LayoutType type,
        int gap,
        int columns, // Used for GRID layout
        String alignment // "start", "center", "end", "space-between", etc.
) {
    public static LayoutData absolute() {
        return new LayoutData(LayoutType.ABSOLUTE, 0, 0, "start");
    }
}
