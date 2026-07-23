package com.observer.api.payload.component.update;

import com.observer.api.ObserverChannels;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec; import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;


public record UpdateTextContentPayload(String id, Component text) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<UpdateTextContentPayload> TYPE = ObserverChannels.createType(ObserverChannels.UPDATE_TEXT_CONTENT);
    
    public static final StreamCodec<RegistryFriendlyByteBuf, UpdateTextContentPayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, UpdateTextContentPayload::id,
            ComponentSerialization.STREAM_CODEC, UpdateTextContentPayload::text,
            UpdateTextContentPayload::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
