package com.observer.paper.command.subcommands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.observer.paper.ObserverPaper;
import com.observer.paper.api.ObserverAPI;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class SystemCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("system")
            // /observer system reload
            .then(Commands.literal("reload")
                .executes(ctx -> {
                    ObserverPaper.getInstance().reload();
                    com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "general.reload_success");
                    return 1;
                })
            )
            // /observer system debug [colors]
            .then(Commands.literal("debug")
                .executes(ctx -> {
                    ctx.getSource().getSender().sendMessage(net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().deserialize("<yellow>=== Observer Debug ==="));
                    com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.system.debug_version", "version", ObserverPaper.getInstance().getPluginMeta().getVersion());
                    com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.system.debug_layouts", "count", ObserverPaper.getInstance().getLayoutManager().getRegistry().getAll().size());
                    com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.system.debug_saves", "count", ObserverPaper.getInstance().getSaveManager().getAll().size());
                    com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.system.debug_clients", "count", ObserverPaper.getInstance().getPlayerManager().getObserverPlayerCount());
                    
                    com.observer.paper.audio.ObserverSoundManager sm = ObserverPaper.getInstance().getSoundManager();
                    com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.system.debug_audio");
                    com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.system.debug_audio_sent", "count", sm.getTotalSoundsSent());
                    com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.system.debug_audio_last", "sound", sm.getLastPlayedSound());
                    return 1;
                })
                .then(Commands.literal("colors")
                    .then(Commands.argument("text", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String input = StringArgumentType.getString(ctx, "text");
                            String sanitized = com.observer.paper.text.ObserverTextParser.sanitize(input);
                            net.kyori.adventure.text.Component result = com.observer.paper.text.ObserverTextParser.parse(input);
                            String json = net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(result);
                            com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.hud.parse.original", "text", input);
                            com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.hud.parse.sanitized", "text", sanitized);
                            com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.hud.parse.minimessage", "text", net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().serialize(result));
                            com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.hud.parse.json", "text", json);
                            try {
                                net.minecraft.network.chat.Component vanilla = com.observer.paper.text.ObserverTextParser.parseVanilla(input);
                                String payloadJson = net.minecraft.network.chat.Component.Serializer.toJson(vanilla, net.minecraft.server.MinecraftServer.getServer().registryAccess());
                                com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.system.colors_payload", "json", payloadJson);
                            } catch (Exception | Error e) {
                                com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.system.colors_error", "error", e.getMessage());
                            }
                            return 1;
                        })
                    )
                )
            )
            // /observer system resources reload
            .then(Commands.literal("resources")
                .then(Commands.literal("reload")
                    .executes(ctx -> {
                        com.observer.api.payload.ResourceReloadPayload payload = new com.observer.api.payload.ResourceReloadPayload();
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            if (ObserverAPI.isObserverPlayer(p)) {
                                ObserverPaper.getInstance().getNetworkManager().sendPayload(
                                    p,
                                    com.observer.api.payload.ResourceReloadPayload.TYPE.id().toString(),
                                    payload,
                                    com.observer.api.payload.ResourceReloadPayload.CODEC
                                );
                            }
                        }
                        com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.system.resources_reload");
                        return 1;
                    })
                )
            )
            // /observer system menu <menuId> <targets>
            .then(Commands.literal("menu")
                .then(Commands.argument("menuId", StringArgumentType.string())
                    .then(Commands.argument("targets", ArgumentTypes.players())
                        .executes(ctx -> {
                            String menuId = StringArgumentType.getString(ctx, "menuId");
                            List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                            for (Player p : targets) {
                                ObserverAPI.openMenu(p, menuId);
                            }
                            com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.system.menu_opened", "menu", menuId, "count", targets.size());
                            return 1;
                        })
                    )
                )
            )
            // /observer system sound <targets> <sound> [volume] [pitch]
            .then(Commands.literal("sound")
                .then(Commands.argument("targets", ArgumentTypes.players())
                    .then(Commands.argument("sound", StringArgumentType.word())
                        .executes(ctx -> executeSound(ctx, 1.0f, 1.0f))
                        .then(Commands.argument("volume", FloatArgumentType.floatArg(0.0f))
                            .executes(ctx -> executeSound(ctx, FloatArgumentType.getFloat(ctx, "volume"), 1.0f))
                            .then(Commands.argument("pitch", FloatArgumentType.floatArg(0.0f, 2.0f))
                                .executes(ctx -> executeSound(ctx, FloatArgumentType.getFloat(ctx, "volume"), FloatArgumentType.getFloat(ctx, "pitch")))
                            )
                        )
                    )
                )
            );
    }

    private static int executeSound(CommandContext<CommandSourceStack> ctx, float volume, float pitch) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
        String sound = StringArgumentType.getString(ctx, "sound");
        for (Player target : targets) {
            ObserverAPI.playSound(target, sound, volume, pitch);
        }
        com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.system.sound_played", "sound", sound, "count", targets.size(), "volume", volume, "pitch", pitch);
        return 1;
    }
}
