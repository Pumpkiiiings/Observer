package com.observer.api.payload.ui;

import com.observer.api.ObserverChannels;
import com.observer.api.menu.MenuComponent;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;

import java.util.List;

public record MenuOpenPayload(String menuId, List<MenuComponent> components) implements CustomPacketPayload {

    public static final Type<MenuOpenPayload> TYPE = new Type<>(ObserverChannels.MENU_OPEN);

    public static final StreamCodec<FriendlyByteBuf, MenuOpenPayload> CODEC = StreamCodec.ofMember(
            MenuOpenPayload::write,
            MenuOpenPayload::new
    );

    public MenuOpenPayload(FriendlyByteBuf buf) {
        this(buf.readUtf(), MenuComponent.CODEC.apply(ByteBufCodecs.list()).decode(buf));
    }

    public void write(FriendlyByteBuf buf) {
        buf.writeUtf(menuId);
        MenuComponent.CODEC.apply(ByteBufCodecs.list()).encode(buf, components);
    }

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
