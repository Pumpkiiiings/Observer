package com.observer.api.payload.component.update;

import com.observer.api.ObserverChannels;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec; import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;


public record UpdatePositionPayload(String id, int x, int y) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdatePositionPayload> TYPE = ObserverChannels.createType(ObserverChannels.UPDATE_POSITION);
    
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdatePositionPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, UpdatePositionPayload::id,
            ByteBufCodecs.INT, UpdatePositionPayload::x,
            ByteBufCodecs.INT, UpdatePositionPayload::y,
            UpdatePositionPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
