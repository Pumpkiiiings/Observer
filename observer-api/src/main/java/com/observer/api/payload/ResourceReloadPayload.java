package com.observer.api.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload; import com.observer.api.ObserverChannels;


public record ResourceReloadPayload() implements CustomPacketPayload {

    public static final Type<ResourceReloadPayload> TYPE = ObserverChannels.createType("resource_reload");

    public static final StreamCodec<RegistryFriendlyByteBuf, ResourceReloadPayload> CODEC = StreamCodec.of(
            (buf, payload) -> {},
            buf -> new ResourceReloadPayload()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
