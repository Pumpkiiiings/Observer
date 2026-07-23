package com.observer.api.menu;

import net.minecraft.network.FriendlyByteBuf;

public record ButtonComponent(String id, MenuTransform transform, String texturePath, int width, int height) implements MenuComponent {
    @Override
    public ComponentType type() {
        return ComponentType.BUTTON;
    }

    public void writeExtra(FriendlyByteBuf buf) {
        buf.writeUtf(texturePath);
        buf.writeInt(width);
        buf.writeInt(height);
    }

    public static ButtonComponent readExtra(FriendlyByteBuf buf, String id, MenuTransform transform) {
        return new ButtonComponent(id, transform, buf.readUtf(), buf.readInt(), buf.readInt());
    }
}
