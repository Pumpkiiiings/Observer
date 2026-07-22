package com.observer.api.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;

public interface MenuComponent {
    String id();
    MenuTransform transform();
    ComponentType type();

    StreamCodec<FriendlyByteBuf, MenuComponent> CODEC = StreamCodec.of(
            (buf, component) -> {
                ComponentType.CODEC.encode(buf, component.type());
                buf.writeUtf(component.id());
                MenuTransform.CODEC.encode(buf, component.transform());
                switch (component.type()) {
                    case TEXTURE -> ((TextureComponent) component).writeExtra(buf);
                    case TEXT -> ((TextComponent) component).writeExtra(buf);
                    case BUTTON -> ((ButtonComponent) component).writeExtra(buf);
                }
            },
            buf -> {
                ComponentType type = ComponentType.CODEC.decode(buf);
                String id = buf.readUtf();
                MenuTransform transform = MenuTransform.CODEC.decode(buf);
                return switch (type) {
                    case TEXTURE -> TextureComponent.readExtra(buf, id, transform);
                    case TEXT -> TextComponent.readExtra(buf, id, transform);
                    case BUTTON -> ButtonComponent.readExtra(buf, id, transform);
                };
            }
    );
}
