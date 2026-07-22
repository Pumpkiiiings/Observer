package com.observer.api.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum HorizontalAlignment {
    LEFT, CENTER, RIGHT;

    public static final StreamCodec<FriendlyByteBuf, HorizontalAlignment> CODEC = StreamCodec.of(
            (buf, align) -> buf.writeEnum(align),
            buf -> buf.readEnum(HorizontalAlignment.class)
    );
}
