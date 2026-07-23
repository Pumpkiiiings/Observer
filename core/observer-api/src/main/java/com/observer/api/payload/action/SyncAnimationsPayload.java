package com.observer.api.payload.action;

import com.observer.api.ObserverChannels;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import java.util.Map;

public record SyncAnimationsPayload(Map<String, String> animations) implements CustomPacketPayload {
    public static final Type<SyncAnimationsPayload> TYPE = ObserverChannels.createType(ObserverChannels.SYNC_ANIMATIONS);

    public static final StreamCodec<ByteBuf, SyncAnimationsPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.map(
                    java.util.HashMap::new,
                    ByteBufCodecs.STRING_UTF8,
                    // Use a string codec with a very large max length to hold the JSON payload safely.
                    ByteBufCodecs.stringUtf8(1048576) 
            ), SyncAnimationsPayload::animations,
            SyncAnimationsPayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
