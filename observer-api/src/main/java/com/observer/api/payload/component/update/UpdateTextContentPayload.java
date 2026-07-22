package com.observer.api.payload.component.update;

import com.observer.api.ObserverChannels;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;

public record UpdateTextContentPayload(Identifier id, Component text) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateTextContentPayload> TYPE = new CustomPacketPayload.Type<>(ObserverChannels.UPDATE_TEXT_CONTENT);
    
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateTextContentPayload> CODEC = StreamCodec.composite(
            Identifier.STREAM_CODEC, UpdateTextContentPayload::id,
            ComponentSerialization.STREAM_CODEC, UpdateTextContentPayload::text,
            UpdateTextContentPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
