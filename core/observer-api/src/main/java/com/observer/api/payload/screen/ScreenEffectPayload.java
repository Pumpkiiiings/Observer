package com.observer.api.payload.screen;

import com.observer.api.ObserverChannels;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Payload sent from the server to the client to trigger a screen effect.
 *
 * For SCREENSHAKE: intensity controls shake magnitude, durationTicks controls how long.
 *                  r, g, b, alpha are unused (set to 0).
 * For TINT:        r, g, b, alpha define the overlay color and opacity.
 *                  intensity is unused (set to 0). durationTicks controls fade duration.
 */
public record ScreenEffectPayload(
        ScreenEffectType effectType,
        float intensity,
        int durationTicks,
        int r,
        int g,
        int b,
        float alpha
) implements CustomPacketPayload {

    public static final Type<ScreenEffectPayload> TYPE = ObserverChannels.createType(ObserverChannels.SCREEN_EFFECT);

    public static final StreamCodec<RegistryFriendlyByteBuf, ScreenEffectPayload> CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeInt(payload.effectType().ordinal());
            buf.writeFloat(payload.intensity());
            buf.writeInt(payload.durationTicks());
            buf.writeInt(payload.r());
            buf.writeInt(payload.g());
            buf.writeInt(payload.b());
            buf.writeFloat(payload.alpha());
        },
        buf -> new ScreenEffectPayload(
            ScreenEffectType.values()[buf.readInt()],
            buf.readFloat(),
            buf.readInt(),
            buf.readInt(),
            buf.readInt(),
            buf.readInt(),
            buf.readFloat()
        )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
