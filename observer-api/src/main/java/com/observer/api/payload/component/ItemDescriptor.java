package com.observer.api.payload.component;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

/**
 * A platform-agnostic descriptor for an ITEM component.
 * Storing material strings instead of NMS ItemStacks ensures the Observer API
 * does not deeply couple its network protocol to a specific server implementation.
 */
public record ItemDescriptor(
        String material,
        int amount
        // future fields: customModelData, nbt
) {

    public static final StreamCodec<RegistryFriendlyByteBuf, ItemDescriptor> CODEC = new StreamCodec<>() {
        @Override
        public ItemDescriptor decode(RegistryFriendlyByteBuf buf) {
            String material = ByteBufCodecs.STRING_UTF8.decode(buf);
            int amount = ByteBufCodecs.INT.decode(buf);
            return new ItemDescriptor(material, amount);
        }

        @Override
        public void encode(RegistryFriendlyByteBuf buf, ItemDescriptor descriptor) {
            ByteBufCodecs.STRING_UTF8.encode(buf, descriptor.material());
            ByteBufCodecs.INT.encode(buf, descriptor.amount());
        }
    };
}
