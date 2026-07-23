package com.observer.paper.command.subcommands;

import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.observer.paper.ObserverPaper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

@SuppressWarnings("UnstableApiUsage")
public class KeyCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("key")
            .then(Commands.literal("stats")
                .executes(ctx -> {
                    com.observer.paper.keys.ObserverKeyboardManager km = ObserverPaper.getInstance().getKeyboardManager();
                    com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.key.stats_header");
                    com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.key.stats_packets", "count", km.getPacketsReceived());
                    com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.key.stats_last_update", "time", (System.currentTimeMillis() - km.getLastUpdateGlobal()));
                    com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.key.stats_tracked", "count", km.getTrackedPlayersCount());
                    for (Player p : Bukkit.getOnlinePlayers()) {
                        java.util.Set<Byte> keys = km.getPressedKeys(p.getUniqueId());
                        if (!keys.isEmpty()) {
                            com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.key.stats_player", "player", p.getName(), "count", keys.size());
                        }
                    }
                    return 1;
                })
            )
            .then(Commands.literal("debug")
                .then(Commands.argument("player", ArgumentTypes.player())
                    .executes(ctx -> {
                        Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).get(0);
                        java.util.Set<Byte> keys = ObserverPaper.getInstance().getKeyboardManager().getPressedKeys(target.getUniqueId());
                        com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.key.debug_pressed", "player", target.getName(), "keys", keys.toString());
                        return 1;
                    })
                )
            )
            .then(Commands.literal("flush")
                .then(Commands.argument("player", ArgumentTypes.player())
                    .executes(ctx -> {
                        Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).get(0);
                        com.observer.paper.api.PaperObserverKeyboardAPI.flushKeys(target);
                        com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.key.flush", "player", target.getName());
                        return 1;
                    })
                )
            )
            .then(Commands.literal("iskeydown")
                .then(Commands.argument("player", ArgumentTypes.player())
                    .then(Commands.argument("ascii", IntegerArgumentType.integer(32, 127))
                        .executes(ctx -> {
                            Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).get(0);
                            int ascii = IntegerArgumentType.getInteger(ctx, "ascii");
                            boolean down = com.observer.paper.api.PaperObserverKeyboardAPI.isKeyDown(target, ascii);
                            if (!down) {
                                throw new com.mojang.brigadier.exceptions.SimpleCommandExceptionType(new com.mojang.brigadier.LiteralMessage("Key not pressed")).create();
                            }
                            com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.key.is_down", "key", ascii, "player", target.getName());
                            return 1;
                        })
                    )
                )
            )
            .then(Commands.literal("matchgroup")
                .then(Commands.argument("player", ArgumentTypes.player())
                    .then(Commands.argument("group", StringArgumentType.string())
                        .executes(ctx -> {
                            Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).get(0);
                            String groupStr = StringArgumentType.getString(ctx, "group");
                            java.util.Set<Integer> current = com.observer.paper.api.PaperObserverKeyboardAPI.getPressedKeys(target);
                            
                            java.util.List<Integer> expected = java.util.Arrays.stream(groupStr.split(","))
                                .map(Integer::parseInt)
                                .toList();
                            
                            java.util.List<Integer> missing = new java.util.ArrayList<>();
                            java.util.List<Integer> extra = new java.util.ArrayList<>(current);
                            
                            for (Integer b : expected) {
                                if (!current.contains(b)) {
                                    missing.add(b);
                                } else {
                                    extra.remove(b);
                                }
                            }
                            
                            if (missing.isEmpty() && extra.isEmpty()) {
                                com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.key.match_perfect", "keys", expected.toString());
                                return 1;
                            } else {
                                throw new com.mojang.brigadier.exceptions.SimpleCommandExceptionType(
                                    new com.mojang.brigadier.LiteralMessage("Missing keys: " + missing + ", Extra keys: " + extra)
                                ).create();
                            }
                        })
                    )
                )
            );
    }
}
