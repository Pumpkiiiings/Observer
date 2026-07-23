package com.observer.paper.layout.component;

import com.observer.api.model.ComponentAlignment;
import com.observer.api.model.ComponentType;
import com.observer.api.model.TextAlignment;
import com.observer.paper.layout.LayoutComponent;

public class ItemComponentImpl implements LayoutComponent {

    private final String id;
    private final ComponentAlignment alignment;
    private final int offsetX;
    private final int offsetY;
    private final float scale;
    private final TextAlignment textAlignment;
    private final String material;
    private final int amount;

    public ItemComponentImpl(String id, ComponentAlignment alignment, int offsetX, int offsetY, float scale, TextAlignment textAlignment, String material, int amount) {
        this.id = id;
        this.alignment = alignment;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scale = scale;
        this.textAlignment = textAlignment;
        this.material = material;
        this.amount = amount;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public ComponentType getType() {
        return ComponentType.ITEM;
    }

    @Override
    public ComponentAlignment getAlignment() {
        return alignment;
    }

    @Override
    public int getOffsetX() {
        return offsetX;
    }

    @Override
    public int getOffsetY() {
        return offsetY;
    }

    @Override
    public float getScale() {
        return scale;
    }

    @Override
    public TextAlignment getTextAlignment() {
        return textAlignment;
    }

    @Override
    public java.util.Optional<Integer> getBackgroundColor() {
        return java.util.Optional.empty();
    }

    public String getMaterial() {
        return material;
    }

    public int getAmount() {
        return amount;
    }
}
