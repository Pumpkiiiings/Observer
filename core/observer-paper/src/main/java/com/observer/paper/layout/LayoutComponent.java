package com.observer.paper.layout;

import com.observer.api.model.ComponentAlignment;
import com.observer.api.model.ComponentType;

/**
 * Base interface for all layout components.
 */
public interface LayoutComponent {

    String getId();
    ComponentType getType();
    ComponentAlignment getAlignment();
    int getOffsetX();
    int getOffsetY();
    float getScale();
    com.observer.api.model.TextAlignment getTextAlignment();
    java.util.Optional<Integer> getBackgroundColor();
}
