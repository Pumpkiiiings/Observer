package com.observer.api.payload.action;

import com.observer.api.ObserverChannels;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.UUID;

public record PlayAnimationPayload(UUID targetPlayer, String animationName) implements CustomPacketPayload {
    public static final Type<PlayAnimationPayload> TYPE = ObserverChannels.createType(ObserverChannels.PLAY_ANIMATION);

    public static final StreamCodec<ByteBuf, PlayAnimationPayload> CODEC = StreamCodec.composite(
            net.minecraft.core.UUIDUtil.STREAM_CODEC, PlayAnimationPayload::targetPlayer,
            ByteBufCodecs.STRING_UTF8, PlayAnimationPayload::animationName,
            PlayAnimationPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
