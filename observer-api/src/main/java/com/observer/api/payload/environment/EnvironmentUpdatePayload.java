package com.observer.api.payload.environment;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;


public record EnvironmentUpdatePayload(
        EnvironmentUpdateType updateType,
        int r,
        int g,
        int b,
        boolean enabled,
        float fogStart,
        float fogEnd,
        float alpha
) implements CustomPacketPayload {

    public static final Type<EnvironmentUpdatePayload> TYPE = com.observer.api.ObserverChannels.createType(com.observer.api.ObserverChannels.ENVIRONMENT_UPDATE);

    public static final StreamCodec<RegistryFriendlyByteBuf, EnvironmentUpdatePayload> CODEC = StreamCodec.of(
        (buf, payload) -> {
            buf.writeInt(payload.updateType().ordinal());
            buf.writeInt(payload.r());
            buf.writeInt(payload.g());
            buf.writeInt(payload.b());
            buf.writeBoolean(payload.enabled());
            buf.writeFloat(payload.fogStart());
            buf.writeFloat(payload.fogEnd());
            buf.writeFloat(payload.alpha());
        },
        buf -> new EnvironmentUpdatePayload(
            EnvironmentUpdateType.values()[buf.readInt()],
            buf.readInt(),
            buf.readInt(),
            buf.readInt(),
            buf.readBoolean(),
            buf.readFloat(),
            buf.readFloat(),
            buf.readFloat()
        )
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
