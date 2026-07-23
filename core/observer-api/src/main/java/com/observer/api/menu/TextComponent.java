package com.observer.api.menu;

import net.minecraft.network.FriendlyByteBuf;

public record TextComponent(String id, MenuTransform transform, String text, float scale) implements MenuComponent {
    @Override
    public ComponentType type() {
        return ComponentType.TEXT;
    }

    public void writeExtra(FriendlyByteBuf buf) {
        buf.writeUtf(text);
        buf.writeFloat(scale);
    }

    public static TextComponent readExtra(FriendlyByteBuf buf, String id, MenuTransform transform) {
        return new TextComponent(id, transform, buf.readUtf(), buf.readFloat());
    }
}
