package com.observer.api.payload.component;

import com.observer.api.ObserverChannels;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec; import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;


public record ComponentRemovePayload(String id) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ComponentRemovePayload> TYPE = ObserverChannels.createType(ObserverChannels.COMPONENT_REMOVE);
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ComponentRemovePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, ComponentRemovePayload::id,
            ComponentRemovePayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
