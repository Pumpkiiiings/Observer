package com.observer.api.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public record MenuTransform(float x, float y, int layer, float alpha,
                            HorizontalAlignment horizontalAlignment,
                            VerticalAlignment verticalAlignment) {

    public static final MenuTransform DEFAULT = new MenuTransform(0, 0, 0, 1.0f, HorizontalAlignment.LEFT, VerticalAlignment.TOP);

    public static final StreamCodec<FriendlyByteBuf, MenuTransform> CODEC = StreamCodec.of(
            (buf, transform) -> {
                buf.writeFloat(transform.x());
                buf.writeFloat(transform.y());
                buf.writeInt(transform.layer());
                buf.writeFloat(transform.alpha());
                HorizontalAlignment.CODEC.encode(buf, transform.horizontalAlignment());
                VerticalAlignment.CODEC.encode(buf, transform.verticalAlignment());
            },
            buf -> new MenuTransform(
                    buf.readFloat(),
                    buf.readFloat(),
                    buf.readInt(),
                    buf.readFloat(),
                    HorizontalAlignment.CODEC.decode(buf),
                    VerticalAlignment.CODEC.decode(buf)
            )
    );

    public static MenuTransform at(float x, float y) {
        return new MenuTransform(x, y, 0, 1.0f, HorizontalAlignment.LEFT, VerticalAlignment.TOP);
    }

    public MenuTransform layer(int layer) {
        return new MenuTransform(x, y, layer, alpha, horizontalAlignment, verticalAlignment);
    }

    public MenuTransform alpha(float alpha) {
        return new MenuTransform(x, y, layer, alpha, horizontalAlignment, verticalAlignment);
    }

    public MenuTransform align(HorizontalAlignment h, VerticalAlignment v) {
        return new MenuTransform(x, y, layer, alpha, h, v);
    }

    public MenuTransform centered() {
        return align(HorizontalAlignment.CENTER, VerticalAlignment.CENTER);
    }
}
