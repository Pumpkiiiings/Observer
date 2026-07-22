package com.observer.api.payload;

import com.observer.api.ObserverChannels;
import com.observer.api.ObserverFeature;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;

import java.util.EnumSet;

public record HandshakePayload(int protocolVersion, String observerVersion, EnumSet<ObserverFeature> features) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<HandshakePayload> TYPE = new CustomPacketPayload.Type<>(ObserverChannels.HANDSHAKE);

    private static final StreamCodec<ByteBuf, EnumSet<ObserverFeature>> FEATURES_CODEC = ByteBufCodecs.INT
            .map(
                    bitmask -> {
                        EnumSet<ObserverFeature> set = EnumSet.noneOf(ObserverFeature.class);
                        ObserverFeature[] values = ObserverFeature.values();
                        for (int i = 0; i < values.length; i++) {
                            if ((bitmask & (1 << i)) != 0) {
                                set.add(values[i]);
                            }
                        }
                        return set;
                    },
                    set -> {
                        int bitmask = 0;
                        ObserverFeature[] values = ObserverFeature.values();
                        for (int i = 0; i < values.length; i++) {
                            if (set.contains(values[i])) {
                                bitmask |= (1 << i);
                            }
                        }
                        return bitmask;
                    }
            );

    public static final StreamCodec<RegistryFriendlyByteBuf, HandshakePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.INT, HandshakePayload::protocolVersion,
            ByteBufCodecs.STRING_UTF8, HandshakePayload::observerVersion,
            FEATURES_CODEC.cast(), HandshakePayload::features,
            HandshakePayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
