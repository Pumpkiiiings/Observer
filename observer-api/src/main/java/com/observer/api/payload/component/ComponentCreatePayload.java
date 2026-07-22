package com.observer.api.payload.component;

import com.observer.api.ObserverChannels;
import com.observer.api.model.ComponentAlignment;
import com.observer.api.model.ComponentType;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.resources.Identifier;

import java.util.Optional;

/**
 * Sent from Paper → Fabric when a new HUD component is created.
 *
 * Component IDs must be namespaced as "layoutId:componentId"
 * to prevent collisions between layouts and third-party plugins.
 *
 * The alignment anchor + pixel offsets (offsetX, offsetY) allow the Fabric
 * client to resolve final absolute screen coordinates at render time,
 * adapting correctly to any screen resolution.
 *
 * Note: StreamCodec.composite() has a maximum of 6 fields.
 * This codec is implemented manually to support 7 fields.
 */
    public record ComponentCreatePayload(
        Identifier id,
        ComponentType componentType,
        ComponentAlignment alignment,
        int offsetX,
        int offsetY,
        float scale,
        Optional<Component> text,
        com.observer.api.model.TextAlignment textAlignment,
        Optional<ItemDescriptor> item,
        Optional<Integer> backgroundColor
) implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<ComponentCreatePayload> TYPE =
            new CustomPacketPayload.Type<>(ObserverChannels.COMPONENT_CREATE);

    public static final StreamCodec<RegistryFriendlyByteBuf, ComponentCreatePayload> CODEC =
            new StreamCodec<>() {
                @Override
                public ComponentCreatePayload decode(RegistryFriendlyByteBuf buf) {
                    Identifier id = Identifier.STREAM_CODEC.decode(buf);
                    ComponentType type = ComponentType.values()[ByteBufCodecs.INT.decode(buf)];
                    ComponentAlignment alignment = ComponentAlignment.values()[ByteBufCodecs.INT.decode(buf)];
                    int offsetX = ByteBufCodecs.INT.decode(buf);
                    int offsetY = ByteBufCodecs.INT.decode(buf);
                    float scale = ByteBufCodecs.FLOAT.decode(buf);
                    Optional<Component> text = ByteBufCodecs.optional(ComponentSerialization.STREAM_CODEC).decode(buf);
                    com.observer.api.model.TextAlignment textAlignment = com.observer.api.model.TextAlignment.values()[ByteBufCodecs.INT.decode(buf)];
                    Optional<ItemDescriptor> item = ByteBufCodecs.optional(ItemDescriptor.CODEC).decode(buf);
                    Optional<Integer> backgroundColor = ByteBufCodecs.optional(ByteBufCodecs.INT).decode(buf);
                    System.out.println("[Observer-Debug] StreamCodec DECODE scale=" + scale);
                    return new ComponentCreatePayload(id, type, alignment, offsetX, offsetY, scale, text, textAlignment, item, backgroundColor);
                }

                @Override
                public void encode(RegistryFriendlyByteBuf buf, ComponentCreatePayload payload) {
                    Identifier.STREAM_CODEC.encode(buf, payload.id());
                    ByteBufCodecs.INT.encode(buf, payload.componentType().ordinal());
                    ByteBufCodecs.INT.encode(buf, payload.alignment().ordinal());
                    ByteBufCodecs.INT.encode(buf, payload.offsetX());
                    ByteBufCodecs.INT.encode(buf, payload.offsetY());
                    ByteBufCodecs.FLOAT.encode(buf, payload.scale());
                    ByteBufCodecs.optional(ComponentSerialization.STREAM_CODEC).encode(buf, payload.text());
                    ByteBufCodecs.INT.encode(buf, payload.textAlignment().ordinal());
                    ByteBufCodecs.optional(ItemDescriptor.CODEC).encode(buf, payload.item());
                    ByteBufCodecs.optional(ByteBufCodecs.INT).encode(buf, payload.backgroundColor());
                    System.out.println("[Observer-Debug] StreamCodec ENCODE scale=" + payload.scale());
                }
            };

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
