package com.observer.api.payload.component;

import com.observer.api.ObserverChannels;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record ClearHUDPayload() implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ClearHUDPayload> TYPE = ObserverChannels.createType(ObserverChannels.CLEAR_HUD);
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ClearHUDPayload> CODEC = StreamCodec.unit(new ClearHUDPayload());

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
