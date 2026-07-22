package com.observer.paper.api;

import com.observer.api.model.ComponentAlignment;
import com.observer.api.model.ComponentType;
import com.observer.api.payload.component.ComponentCreatePayload;
import com.observer.api.payload.component.ComponentRemovePayload;
import com.observer.api.payload.component.update.UpdateTextContentPayload;
import com.observer.paper.ObserverPaper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import net.minecraft.resources.Identifier;
import org.bukkit.entity.Player;
import io.papermc.paper.adventure.PaperAdventure;

import java.util.Optional;

/**
 * Public API surface for Observer Paper.
 *
 * All methods are static. The server resolves all placeholder values
 * before calling these methods — the client only receives final strings.
 */
public final class ObserverAPI {

    private ObserverAPI() {}

    public static boolean isObserverPlayer(Player player) {
        return ObserverPaper.getInstance().getPlayerManager().isObserver(player);
    }

    // -----------------------------------------------------------------------
    // Internal helpers
    // -----------------------------------------------------------------------
    /**
     * Converts a string with colors into a Minecraft Component.
     * Supports:
     * - MiniMessage (<red>, <gradient>, etc)
     * - Legacy Ampersand (&a, &l)
     * - Legacy Section (§a, §l)
     * - Hex Colors (&#RRGGBB)
     */
    private static net.minecraft.network.chat.Component convertText(String text) {
        return com.observer.paper.text.ObserverTextParser.parseVanilla(text);
    }

    public static void send(Player player, String channel, Object payload,
                             net.minecraft.network.codec.StreamCodec<? super net.minecraft.network.RegistryFriendlyByteBuf, ?> codec) {
        //noinspection unchecked
        ObserverPaper.getInstance().getNetworkManager().sendPayload(
                player, channel, (net.minecraft.network.protocol.common.custom.CustomPacketPayload) payload,
                (net.minecraft.network.codec.StreamCodec<? super net.minecraft.network.RegistryFriendlyByteBuf,
                        net.minecraft.network.protocol.common.custom.CustomPacketPayload>) codec);
    }

    // -----------------------------------------------------------------------
    // Component lifecycle
    // -----------------------------------------------------------------------

    /**
     * Creates a text component on the player's HUD.
     *
     * The component ID must be namespaced: "layoutId:componentId"
     * Coordinates are expressed as pixel offsets from the alignment anchor.
     */
    public static void createText(Player player, String id, String textContent,
                                  ComponentAlignment alignment, int offsetX, int offsetY, float scale, com.observer.api.model.TextAlignment textAlignment) {
        createText(player, id, textContent, alignment, offsetX, offsetY, scale, textAlignment, Optional.empty());
    }

    public static void createText(Player player, String id, String textContent,
                                  ComponentAlignment alignment, int offsetX, int offsetY, float scale, com.observer.api.model.TextAlignment textAlignment, Optional<Integer> backgroundColor) {
        if (!isObserverPlayer(player)) return;

        Identifier identifier = Identifier.parse(id);

        ComponentCreatePayload payload = new ComponentCreatePayload(
                identifier,
                ComponentType.TEXT,
                alignment,
                offsetX,
                offsetY,
                scale,
                Optional.of(convertText(textContent)),
                textAlignment,
                Optional.empty(),
                backgroundColor
        );

        ObserverPaper.getInstance().getLogger().info("[Observer-Debug] Sent scale=" + scale);
        send(player, payload.type().id().toString(), payload, ComponentCreatePayload.CODEC);
    }

    /**
     * Creates an item component on the player's HUD.
     */
    public static void createItem(Player player, String id, String material, int amount,
                                  ComponentAlignment alignment, int offsetX, int offsetY, float scale, com.observer.api.model.TextAlignment textAlignment) {
        if (!isObserverPlayer(player)) return;

        Identifier identifier = Identifier.parse(id);
        com.observer.api.payload.component.ItemDescriptor descriptor = new com.observer.api.payload.component.ItemDescriptor(material, amount);

        ComponentCreatePayload payload = new ComponentCreatePayload(
                identifier,
                ComponentType.ITEM,
                alignment,
                offsetX,
                offsetY,
                scale,
                Optional.empty(),
                textAlignment,
                Optional.of(descriptor),
                Optional.empty()
        );

        send(player, payload.type().id().toString(), payload, ComponentCreatePayload.CODEC);
    }

    /**
     * Updates only the text content of an existing component.
     * No component is recreated — the client updates in place with no flicker.
     */
    public static void updateText(Player player, String id, String textContent) {
        if (!isObserverPlayer(player)) return;

        Identifier identifier = Identifier.parse(id);

        UpdateTextContentPayload payload = new UpdateTextContentPayload(
                identifier,
                convertText(textContent)
        );

        send(player, payload.type().id().toString(), payload, UpdateTextContentPayload.CODEC);
    }

    /** Removes a component from the player's HUD. */
    public static void removeComponent(Player player, String id) {
        if (!isObserverPlayer(player)) return;

        Identifier identifier = Identifier.parse(id);

        ComponentRemovePayload payload = new ComponentRemovePayload(identifier);

        send(player, payload.type().id().toString(), payload, ComponentRemovePayload.CODEC);
    }

    /** Clears all components from the player's HUD. */
    public static void clearHUD(Player player) {
        if (!isObserverPlayer(player)) return;
        com.observer.api.payload.component.ClearHUDPayload payload = new com.observer.api.payload.component.ClearHUDPayload();
        send(player, payload.type().id().toString(), payload, com.observer.api.payload.component.ClearHUDPayload.CODEC);
    }

    /** Clears all components from all connected Observer players' HUDs. */
    public static void clearHUD() {
        com.observer.api.payload.component.ClearHUDPayload payload = new com.observer.api.payload.component.ClearHUDPayload();
        for (Player player : org.bukkit.Bukkit.getOnlinePlayers()) {
            if (isObserverPlayer(player)) {
                send(player, payload.type().id().toString(), payload, com.observer.api.payload.component.ClearHUDPayload.CODEC);
            }
        }
    }

    // -----------------------------------------------------------------------
    // Audio
    // -----------------------------------------------------------------------

    public static void playSound(Player player, String soundId) {
        ObserverPaper.getInstance().getSoundManager().playSound(player, soundId);
    }

    public static void playSound(Player player, String soundId, float volume, float pitch) {
        ObserverPaper.getInstance().getSoundManager().playSound(player, soundId, volume, pitch);
    }

    // -----------------------------------------------------------------------
    // Environment
    // -----------------------------------------------------------------------

    public static void setFogColor(Player player, int r, int g, int b) {
        ObserverPaper.getInstance().getEnvironmentManager().setFogColor(player, r, g, b);
    }

    public static void resetFogColor(Player player) {
        ObserverPaper.getInstance().getEnvironmentManager().resetFogColor(player);
    }

    public static void setSkyColor(Player player, int r, int g, int b) {
        ObserverPaper.getInstance().getEnvironmentManager().setSkyColor(player, r, g, b);
    }

    public static void resetSkyColor(Player player) {
        ObserverPaper.getInstance().getEnvironmentManager().resetSkyColor(player);
    }

    public static void setMoonColor(Player player, int r, int g, int b) {
        ObserverPaper.getInstance().getEnvironmentManager().setMoonColor(player, r, g, b);
    }

    public static void resetMoonColor(Player player) {
        ObserverPaper.getInstance().getEnvironmentManager().resetMoonColor(player);
    }

    public static void setTrueDarkness(Player player, boolean enabled) {
        ObserverPaper.getInstance().getEnvironmentManager().setTrueDarkness(player, enabled);
    }

    public static void startDenseFog(Player player, float fogStart, float fogEnd, float alpha) {
        ObserverPaper.getInstance().getEnvironmentManager().startDenseFog(player, fogStart, fogEnd, alpha);
    }

    public static void stopDenseFog(Player player) {
        ObserverPaper.getInstance().getEnvironmentManager().stopDenseFog(player);
    }

    // -----------------------------------------------------------------------
    // UI (Server-Driven API)
    // -----------------------------------------------------------------------

    public static void openMenu(Player player, String menuId) {
        if (!isObserverPlayer(player)) {
            player.sendMessage("§c[Observer] Cannot open menu: You are not recognized as an Observer player! (Handshake missing)");
            // FORCE it for testing!
            ObserverPaper.getInstance().getLogger().info("Forcing menu open anyway for " + player.getName());
        }
        
        com.observer.paper.menu.ObserverMenu menu = ObserverPaper.getInstance().getMenuManager().getMenu(menuId);
        if (menu == null) {
            player.sendMessage("§c[Observer] Menu not found: " + menuId);
            return;
        }
        
        menu.open(player);
    }
}
