package com.observer.api.payload.ui;

import com.observer.api.ObserverChannels;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record MenuActionPayload(String menuId, String reference) implements CustomPacketPayload {

    public static final Type<MenuActionPayload> TYPE = ObserverChannels.createType(ObserverChannels.MENU_ACTION);

    public static final StreamCodec<FriendlyByteBuf, MenuActionPayload> CODEC = StreamCodec.ofMember(
            MenuActionPayload::write,
            MenuActionPayload::new
    );

    public MenuActionPayload(FriendlyByteBuf buf) {
        this(buf.readUtf(), buf.readUtf());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(menuId);
        buf.writeUtf(reference);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
