package com.observer.api.payload.component;

import com.observer.api.ObserverChannels;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record ComponentRemovePayload(Identifier id) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ComponentRemovePayload> TYPE = new CustomPacketPayload.Type<>(ObserverChannels.COMPONENT_REMOVE);
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ComponentRemovePayload> CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, ComponentRemovePayload::id,
            ComponentRemovePayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
