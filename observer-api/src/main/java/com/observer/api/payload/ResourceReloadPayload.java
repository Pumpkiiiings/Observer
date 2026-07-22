package com.observer.api.payload;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ResourceReloadPayload() implements CustomPacketPayload {

    public static final Type<ResourceReloadPayload> TYPE = new Type<>(Identifier.parse("observer:resource_reload"));

    public static final StreamCodec<RegistryFriendlyByteBuf, ResourceReloadPayload> CODEC = StreamCodec.of(
            (buf, payload) -> {},
            buf -> new ResourceReloadPayload()
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
