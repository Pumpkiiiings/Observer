package com.observer.api.payload.ui;

import com.observer.api.ObserverChannels;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

public record MenuClosePayload(String menuId) implements CustomPacketPayload {

    public static final Type<MenuClosePayload> TYPE = new Type<>(ObserverChannels.MENU_CLOSE);

    public static final StreamCodec<FriendlyByteBuf, MenuClosePayload> CODEC = StreamCodec.ofMember(
            MenuClosePayload::write,
            MenuClosePayload::new
    );

    public MenuClosePayload(FriendlyByteBuf buf) {
        this(buf.readUtf());
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(menuId);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
