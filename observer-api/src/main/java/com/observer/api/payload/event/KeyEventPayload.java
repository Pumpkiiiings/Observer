package com.observer.api.payload.event;

import com.observer.api.ObserverChannels;
import com.observer.api.model.KeyAction;
import com.observer.api.model.ObserverKey;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

/**
 * Sent from Fabric → Paper when the client detects a key press or release.
 * Includes full future-proof metadata (timestamp, modifier states).
 *
 * StreamCodec.composite() has a limit of 6 fields.
 * We implement it manually because we have 6 fields here, but it's cleaner
 * and safer for future expansion.
 */
public record KeyEventPayload(
        ObserverKey key,
        KeyAction action,
        long timestamp,
        boolean shiftDown,
        boolean ctrlDown,
        boolean altDown
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<KeyEventPayload> TYPE =
            ObserverChannels.createType(ObserverChannels.OBSERVER_EVENT);

    public static final StreamCodec<RegistryFriendlyByteBuf, KeyEventPayload> CODEC =
            new StreamCodec<>() {
                @Override
                public KeyEventPayload decode(RegistryFriendlyByteBuf buf) {
                    ObserverKey key = ObserverKey.values()[ByteBufCodecs.INT.decode(buf)];
                    KeyAction action = KeyAction.values()[ByteBufCodecs.INT.decode(buf)];
                    long timestamp = ByteBufCodecs.VAR_LONG.decode(buf);
                    boolean shift = ByteBufCodecs.BOOL.decode(buf);
                    boolean ctrl = ByteBufCodecs.BOOL.decode(buf);
                    boolean alt = ByteBufCodecs.BOOL.decode(buf);
                    return new KeyEventPayload(key, action, timestamp, shift, ctrl, alt);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, KeyEventPayload payload) {
                    ByteBufCodecs.INT.encode(buf, payload.key().ordinal());
                    ByteBufCodecs.INT.encode(buf, payload.action().ordinal());
                    ByteBufCodecs.VAR_LONG.encode(buf, payload.timestamp());
                    ByteBufCodecs.BOOL.encode(buf, payload.shiftDown());
                    ByteBufCodecs.BOOL.encode(buf, payload.ctrlDown());
                    ByteBufCodecs.BOOL.encode(buf, payload.altDown());
                }
            };

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
