package com.observer.fabric.network.handler; import net.minecraft.resources.Identifier;

import com.observer.api.model.ComponentType;
import com.observer.api.payload.component.ComponentCreatePayload;
import com.observer.api.payload.component.ComponentRemovePayload;
import com.observer.api.payload.component.update.UpdatePositionPayload;
import com.observer.api.payload.component.update.UpdateTextContentPayload;
import com.observer.fabric.client.ObserverClient;
import com.observer.fabric.render.component.ObserverComponent;
import com.observer.fabric.render.component.TextComponent;
import com.observer.fabric.render.manager.ComponentManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

@Environment(EnvType.CLIENT)
public final class ComponentPayloadHandler {

    private ComponentPayloadHandler() {}

    public static void handleCreate(ComponentCreatePayload payload, ClientPlayNetworking.Context context) {
        ObserverClient.LOGGER.info("[Observer] Received ComponentCreatePayload id={} type={} alignment={}",
                Identifier.tryParse(payload.id()), payload.componentType(), payload.alignment());
        ObserverClient.LOGGER.info("[Observer-Debug] Received scale={}", payload.scale());
        context.client().execute(() -> {
            if (payload.componentType() == ComponentType.TEXT) {
                payload.text().ifPresent(text -> {
                    TextComponent component = new TextComponent(
                            payload.alignment(),
                            payload.offsetX(),
                            payload.offsetY(),
                            payload.scale(),
                            text,
                            payload.textAlignment(),
                            payload.backgroundColor()
                    );
                    ComponentManager.addComponent(Identifier.tryParse(payload.id()), component);
                    ObserverClient.LOGGER.info("[Observer] Component stored: {}", Identifier.tryParse(payload.id()));
                });
            } else if (payload.componentType() == ComponentType.ITEM) {
                payload.item().ifPresent(descriptor -> {
                    net.minecraft.resources.Identifier itemId = net.minecraft.resources.Identifier.tryParse(descriptor.material());
                    if (itemId != null) {
                        net.minecraft.world.item.Item item = net.minecraft.core.registries.BuiltInRegistries.ITEM.getOptional(itemId).orElse(net.minecraft.world.item.Items.AIR);
                        if (item != net.minecraft.world.item.Items.AIR) {
                            net.minecraft.world.item.ItemStack stack = new net.minecraft.world.item.ItemStack(item, descriptor.amount());
                            com.observer.fabric.render.component.ItemComponent component = new com.observer.fabric.render.component.ItemComponent(
                                    payload.alignment(),
                                    payload.offsetX(),
                                    payload.offsetY(),
                                    payload.scale(),
                                    stack
                            );
                            ComponentManager.addComponent(Identifier.tryParse(payload.id()), component);
                            ObserverClient.LOGGER.info("[Observer] ITEM Component stored: {}", Identifier.tryParse(payload.id()));
                        } else {
                            ObserverClient.LOGGER.warn("[Observer] ITEM Component material not found: {}", descriptor.material());
                        }
                    }
                });
            }
        });
    }

    public static void handleRemove(ComponentRemovePayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            ComponentManager.removeComponent(Identifier.tryParse(payload.id()));
            ObserverClient.LOGGER.info("Removed component: {}", Identifier.tryParse(payload.id()));
        });
    }

    public static void handleUpdateText(UpdateTextContentPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            ObserverComponent component = ComponentManager.getComponent(Identifier.tryParse(payload.id()));
            if (component instanceof TextComponent textComponent) {
                textComponent.setText(payload.text());
                ObserverClient.LOGGER.info("Updated text for component: {}", Identifier.tryParse(payload.id()));
            }
        });
    }

    public static void handleUpdatePosition(UpdatePositionPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            ObserverComponent component = ComponentManager.getComponent(Identifier.tryParse(payload.id()));
            if (component != null) {
                component.setPosition(payload.x(), payload.y());
                ObserverClient.LOGGER.info("Updated position for component: {}", Identifier.tryParse(payload.id()));
            }
        });
    }

    public static void handleClearHUD(com.observer.api.payload.component.ClearHUDPayload payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> {
            ComponentManager.clear();
            ObserverClient.LOGGER.info("[Observer] Cleared all HUD components.");
        });
    }
}
