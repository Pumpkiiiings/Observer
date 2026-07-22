package com.observer.paper.layout.component;

import com.observer.api.model.ComponentAlignment;
import com.observer.api.model.ComponentType;
import com.observer.paper.layout.LayoutComponent;

public class TextComponentImpl implements LayoutComponent {
    private final String id;
    private final ComponentAlignment alignment;
    private final int offsetX;
    private final int offsetY;
    private final float scale;
    private final String content;
    private final com.observer.api.model.TextAlignment textAlignment;
    private final java.util.Optional<Integer> backgroundColor;

    public TextComponentImpl(String id, ComponentAlignment alignment, int offsetX, int offsetY, float scale, String content, com.observer.api.model.TextAlignment textAlignment, java.util.Optional<Integer> backgroundColor) {
        this.id = id;
        this.alignment = alignment;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scale = scale;
        this.content = content;
        this.textAlignment = textAlignment;
        this.backgroundColor = backgroundColor;
        org.bukkit.Bukkit.getLogger().info("[Observer-Debug] TextComponentImpl constructor scale=" + scale);
    }

    @Override
    public String getId() { return id; }

    @Override
    public ComponentType getType() { return ComponentType.TEXT; }

    @Override
    public ComponentAlignment getAlignment() { return alignment; }

    @Override
    public int getOffsetX() { return offsetX; }

    @Override
    public int getOffsetY() { return offsetY; }

    @Override
    public float getScale() { return scale; }

    @Override
    public com.observer.api.model.TextAlignment getTextAlignment() { return textAlignment; }

    @Override
    public java.util.Optional<Integer> getBackgroundColor() { return backgroundColor; }

    public String getContent() { return content; }
}
