package com.observer.api.payload.action;

import com.observer.api.ObserverChannels;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record PlayerActionPayload(PlayerActionType actionType) implements CustomPacketPayload {
    public static final Type<PlayerActionPayload> TYPE = ObserverChannels.createType(ObserverChannels.PLAYER_ACTION);

    public static final StreamCodec<ByteBuf, PlayerActionPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.idMapper(i -> PlayerActionType.values()[i], PlayerActionType::ordinal), PlayerActionPayload::actionType,
            PlayerActionPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
