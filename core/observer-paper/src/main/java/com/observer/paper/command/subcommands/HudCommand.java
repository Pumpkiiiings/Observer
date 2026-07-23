package com.observer.paper.command.subcommands;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import com.observer.api.model.ComponentAlignment;
import com.observer.paper.ObserverPaper;
import com.observer.paper.api.ObserverAPI;
import com.observer.paper.layout.LayoutManager;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@SuppressWarnings("UnstableApiUsage")
public class HudCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("hud")
            // /observer hud itemtest
            .then(Commands.literal("itemtest")
                .executes(ctx -> {
                    if (!(ctx.getSource().getSender() instanceof Player player)) {
                        com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "general.only_players");
                        return 0;
                    }
                    ObserverAPI.createItem(
                            player,
                            "observer:itemtest",
                            "minecraft:diamond_sword",
                            64,
                            com.observer.api.model.ComponentAlignment.CENTER,
                            0,
                            0,
                            2.0f,
                            com.observer.api.model.TextAlignment.CENTER
                    );
                    com.observer.paper.config.MessageManager.sendMessage(player, "commands.hud.itemtest_success");
                    return 1;
                })
            )
            // /observer hud parse <text>
            .then(Commands.literal("parse")
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
                        com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.hud.parse.preview", "component", result);
                        return 1;
                    })
                )
            )
            // /observer hud layout reload
            .then(Commands.literal("layout")
                .then(Commands.literal("reload")
                    .executes(ctx -> {
                        ObserverPaper.getInstance().reload();
                        com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "general.reload_success");
                        return 1;
                    })
                )
            )
            // /observer hud text <scale> <text...>
            .then(Commands.literal("text")
                .then(Commands.argument("scale", FloatArgumentType.floatArg(0.1f, 10.0f))
                    .then(Commands.argument("text", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            if (!(ctx.getSource().getSender() instanceof Player player)) {
                                com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "general.only_players");
                                return 0;
                            }
                            float scale = FloatArgumentType.getFloat(ctx, "scale");
                            String text = StringArgumentType.getString(ctx, "text");
                            ObserverAPI.createText(player, "test:hud", com.observer.paper.text.ObserverTextParser.sanitize(text),
                                    ComponentAlignment.CENTER, 0, 0, scale, com.observer.api.model.TextAlignment.LEFT);
                            com.observer.paper.config.MessageManager.sendMessage(player, "commands.hud.text_success");
                            return 1;
                        })
                    )
                )
            )
            // /observer hud clear <targets>
            .then(Commands.literal("clear")
                .then(Commands.argument("targets", ArgumentTypes.players())
                    .executes(ctx -> {
                        List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                        for (Player p : targets) {
                            ObserverAPI.clearHUD(p);
                        }
                        com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.hud.clear_success", "count", targets.size());
                        return 1;
                    })
                )
            )
            // /observer hud hide <layoutId> <targets>
            .then(Commands.literal("hide")
                .then(Commands.argument("layoutId", StringArgumentType.word())
                    .suggests(HudCommand::suggestLayouts)
                    .then(Commands.argument("targets", ArgumentTypes.players())
                        .executes(ctx -> {
                            String layoutId = StringArgumentType.getString(ctx, "layoutId");
                            List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                            LayoutManager lm = ObserverPaper.getInstance().getLayoutManager();
                            for (Player p : targets) {
                                lm.hide(layoutId, p);
                            }
                            com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.hud.hide_success", "layout", layoutId, "count", targets.size());
                            return 1;
                        })
                    )
                )
            )
            // /observer hud display <layoutId|saveId> <targets>
            .then(Commands.literal("display")
                .then(Commands.argument("id", StringArgumentType.word())
                    .suggests(HudCommand::suggestLayoutsAndSaves)
                    .then(Commands.argument("targets", ArgumentTypes.players())
                        .executes(ctx -> {
                            String id = StringArgumentType.getString(ctx, "id");
                            List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                            LayoutManager lm = ObserverPaper.getInstance().getLayoutManager();
                            com.observer.paper.save.SaveManager sm = ObserverPaper.getInstance().getSaveManager();

                            boolean isLayout = lm.getRegistry().get(id).isPresent();
                            boolean isSave = sm.getSave(id).isPresent();

                            if (!isLayout && !isSave) {
                                com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.hud.display_unknown", "id", id);
                                return 0;
                            }

                            for (Player p : targets) {
                                if (isLayout) lm.show(id, p);
                                else sm.display(id, p);
                            }
                            com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.hud.display_success", "id", id, "count", targets.size());
                            return 1;
                        })
                    )
                )
            );
    }

    private static CompletableFuture<Suggestions> suggestLayouts(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        ObserverPaper.getInstance().getLayoutManager().getRegistry().getAll().keySet().forEach(id -> {
            if (id.toLowerCase().startsWith(builder.getRemainingLowerCase())) {
                builder.suggest(id);
            }
        });
        return builder.buildFuture();
    }

    private static CompletableFuture<Suggestions> suggestLayoutsAndSaves(CommandContext<CommandSourceStack> context, SuggestionsBuilder builder) {
        ObserverPaper.getInstance().getLayoutManager().getRegistry().getAll().keySet().forEach(id -> {
            if (id.toLowerCase().startsWith(builder.getRemainingLowerCase())) {
                builder.suggest(id);
            }
        });
        ObserverPaper.getInstance().getSaveManager().getAll().keySet().forEach(id -> {
            if (id.toLowerCase().startsWith(builder.getRemainingLowerCase())) {
                builder.suggest(id);
            }
        });
        return builder.buildFuture();
    }
}
