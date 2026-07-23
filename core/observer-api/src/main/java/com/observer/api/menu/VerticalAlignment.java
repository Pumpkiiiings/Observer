package com.observer.api.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public enum VerticalAlignment {
    TOP, CENTER, BOTTOM;

    public static final StreamCodec<FriendlyByteBuf, VerticalAlignment> CODEC = StreamCodec.of(
            (buf, align) -> buf.writeEnum(align),
            buf -> buf.readEnum(VerticalAlignment.class)
    );
}
