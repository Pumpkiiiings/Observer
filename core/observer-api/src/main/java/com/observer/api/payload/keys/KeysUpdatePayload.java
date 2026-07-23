package com.observer.api.payload.keys;

import com.observer.api.ObserverChannels;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public record KeysUpdatePayload(Set<Byte> pressedKeys) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<KeysUpdatePayload> TYPE = ObserverChannels.createType(ObserverChannels.OBSERVER_KEYS_UPDATE);

    public static final StreamCodec<FriendlyByteBuf, KeysUpdatePayload> CODEC = StreamCodec.of(
            (buf, payload) -> {
                buf.writeByte(payload.pressedKeys().size());
                for (int key : payload.pressedKeys()) {
                    buf.writeByte(key);
                }
            },
            buf -> {
                byte size = buf.readByte();
                Set<Byte> keys = new HashSet<>(size);
                for (int i = 0; i < size; i++) {
                    keys.add(buf.readByte());
                }
                return new KeysUpdatePayload(keys);
            }
    );

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
