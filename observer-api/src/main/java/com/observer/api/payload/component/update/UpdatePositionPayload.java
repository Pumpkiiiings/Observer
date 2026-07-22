package com.observer.api.payload.component.update;

import com.observer.api.ObserverChannels;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

public record UpdatePositionPayload(Identifier id, int x, int y) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdatePositionPayload> TYPE = new CustomPacketPayload.Type<>(ObserverChannels.UPDATE_POSITION);
    
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdatePositionPayload> CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, UpdatePositionPayload::id,
            ByteBufCodecs.INT, UpdatePositionPayload::x,
            ByteBufCodecs.INT, UpdatePositionPayload::y,
            UpdatePositionPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
