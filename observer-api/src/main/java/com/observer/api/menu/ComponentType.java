package com.observer.api.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum ComponentType {
    TEXTURE, TEXT, BUTTON;

    public static final StreamCodec<FriendlyByteBuf, ComponentType> CODEC = StreamCodec.of(
            (buf, type) -> buf.writeEnum(type),
            buf -> buf.readEnum(ComponentType.class)
    );
}
