package com.observer.paper.command.subcommands;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.observer.paper.ObserverPaper;
import com.observer.paper.api.ObserverAPI;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;

import java.util.List;

@SuppressWarnings("UnstableApiUsage")
public class EnvCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("env")
            // /observer env test
            .then(Commands.literal("test")
                .executes(ctx -> {
                    if (!(ctx.getSource().getSender() instanceof Player player)) {
                        com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "general.only_players");
                        return 0;
                    }
                    ObserverPaper.getInstance().getLogger().info(
                            "[STAGE-1][ENV-LOADED] /observer env test triggered by " + player.getName()
                            + " | isObserver=" + ObserverAPI.isObserverPlayer(player));

                    ObserverAPI.setSkyColor(player, 255, 0, 0);
                    ObserverAPI.setFogColor(player, 0, 255, 0);
                    ObserverAPI.setMoonColor(player, 0, 0, 255);
                    ObserverAPI.setTrueDarkness(player, true);
                    ObserverAPI.startDenseFog(player, 2f, 8f, 1f);

                    ObserverPaper.getInstance().getLogger().info(
                            "[STAGE-1][ENV-LOADED] /observer env test dispatched all 5 env packets for " + player.getName());
                    com.observer.paper.config.MessageManager.sendMessage(player, "commands.env.test_dispatched");
                    return 1;
                })
            )
            // /observer env darkness <enabled> <targets>
            .then(Commands.literal("darkness")
                .then(Commands.argument("enabled", BoolArgumentType.bool())
                    .then(Commands.argument("targets", ArgumentTypes.players())
                        .executes(ctx -> {
                            List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                            boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                            for (Player target : targets) {
                                ObserverAPI.setTrueDarkness(target, enabled);
                            }
                            com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.env.updated", "count", targets.size());
                            return 1;
                        })
                    )
                )
            )
            // /observer env dense reset <targets>
            // /observer env dense start <start> <end> <alpha> <targets>
            .then(Commands.literal("dense")
                .then(Commands.literal("reset")
                    .then(Commands.argument("targets", ArgumentTypes.players())
                        .executes(ctx -> {
                            List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                            for (Player target : targets) {
                                ObserverAPI.stopDenseFog(target);
                            }
                            com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.env.updated", "count", targets.size());
                            return 1;
                        })
                    )
                )
                .then(Commands.literal("start")
                    .then(Commands.argument("start", FloatArgumentType.floatArg())
                        .then(Commands.argument("end", FloatArgumentType.floatArg())
                            .then(Commands.argument("alpha", FloatArgumentType.floatArg())
                                .then(Commands.argument("targets", ArgumentTypes.players())
                                    .executes(ctx -> {
                                        List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                                        float start = FloatArgumentType.getFloat(ctx, "start");
                                        float end = FloatArgumentType.getFloat(ctx, "end");
                                        float alpha = FloatArgumentType.getFloat(ctx, "alpha");
                                        for (Player target : targets) {
                                            ObserverAPI.startDenseFog(target, start, end, alpha);
                                        }
                                        com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.env.updated", "count", targets.size());
                                        return 1;
                                    })
                                )
                            )
                        )
                    )
                )
            )
            // fog, sky, moon
            .then(buildEnvColorCommand("fog", 
                (p, r, g, b) -> ObserverAPI.setFogColor(p, r, g, b),
                (p) -> ObserverAPI.resetFogColor(p)))
            .then(buildEnvColorCommand("sky", 
                (p, r, g, b) -> ObserverAPI.setSkyColor(p, r, g, b),
                (p) -> ObserverAPI.resetSkyColor(p)))
            .then(buildEnvColorCommand("moon", 
                (p, r, g, b) -> ObserverAPI.setMoonColor(p, r, g, b),
                (p) -> ObserverAPI.resetMoonColor(p)))
            // effect
            .then(Commands.literal("effect")
                .then(Commands.literal("shake")
                    .then(Commands.argument("intensity", FloatArgumentType.floatArg(0.0f))
                        .then(Commands.argument("duration", IntegerArgumentType.integer(1))
                            .then(Commands.argument("targets", ArgumentTypes.players())
                                .executes(ctx -> {
                                    List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                                    float intensity = FloatArgumentType.getFloat(ctx, "intensity");
                                    int duration = IntegerArgumentType.getInteger(ctx, "duration");
                                    for (Player target : targets) {
                                        com.observer.paper.api.PaperObserverScreenAPI.playScreenshake(target, intensity, duration);
                                    }
                                    com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.env.shake", "count", targets.size());
                                    return 1;
                                })
                            )
                        )
                    )
                )
                .then(Commands.literal("tint")
                    .then(Commands.argument("r", IntegerArgumentType.integer(0, 255))
                        .then(Commands.argument("g", IntegerArgumentType.integer(0, 255))
                            .then(Commands.argument("b", IntegerArgumentType.integer(0, 255))
                                .then(Commands.argument("alpha", FloatArgumentType.floatArg(0.0f, 1.0f))
                                    .then(Commands.argument("duration", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("targets", ArgumentTypes.players())
                                            .executes(ctx -> {
                                                List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                                                int r = IntegerArgumentType.getInteger(ctx, "r");
                                                int g = IntegerArgumentType.getInteger(ctx, "g");
                                                int b = IntegerArgumentType.getInteger(ctx, "b");
                                                float alpha = FloatArgumentType.getFloat(ctx, "alpha");
                                                int duration = IntegerArgumentType.getInteger(ctx, "duration");
                                                for (Player target : targets) {
                                                    com.observer.paper.api.PaperObserverScreenAPI.playScreenTint(target, r, g, b, alpha, duration);
                                                }
                                                com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.env.tint", "count", targets.size());
                                                return 1;
                                            })
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
                .then(Commands.literal("vignette")
                    .then(Commands.argument("r", IntegerArgumentType.integer(0, 255))
                        .then(Commands.argument("g", IntegerArgumentType.integer(0, 255))
                            .then(Commands.argument("b", IntegerArgumentType.integer(0, 255))
                                .then(Commands.argument("alpha", FloatArgumentType.floatArg(0.0f, 1.0f))
                                    .then(Commands.argument("duration", IntegerArgumentType.integer(1))
                                        .then(Commands.argument("targets", ArgumentTypes.players())
                                            .executes(ctx -> {
                                                List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                                                int r = IntegerArgumentType.getInteger(ctx, "r");
                                                int g = IntegerArgumentType.getInteger(ctx, "g");
                                                int b = IntegerArgumentType.getInteger(ctx, "b");
                                                float alpha = FloatArgumentType.getFloat(ctx, "alpha");
                                                int duration = IntegerArgumentType.getInteger(ctx, "duration");
                                                for (Player target : targets) {
                                                    com.observer.paper.api.PaperObserverScreenAPI.playScreenVignette(target, r, g, b, alpha, duration);
                                                }
                                                com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.env.vignette", "count", targets.size());
                                                return 1;
                                            })
                                        )
                                    )
                                )
                            )
                        )
                    )
                )
            );
    }

    private interface ColorSetter { void set(Player p, int r, int g, int b); }
    private interface ColorResetter { void reset(Player p); }

    private static LiteralArgumentBuilder<CommandSourceStack> buildEnvColorCommand(String name, ColorSetter setter, ColorResetter resetter) {
        return Commands.literal(name)
            .then(Commands.literal("reset")
                .then(Commands.argument("targets", ArgumentTypes.players())
                    .executes(ctx -> {
                        List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                        for (Player target : targets) {
                            resetter.reset(target);
                        }
                        com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.env.updated", "count", targets.size());
                        return 1;
                    })
                )
            )
            .then(Commands.argument("r", IntegerArgumentType.integer(0, 255))
                .then(Commands.argument("g", IntegerArgumentType.integer(0, 255))
                    .then(Commands.argument("b", IntegerArgumentType.integer(0, 255))
                        .then(Commands.argument("targets", ArgumentTypes.players())
                            .executes(ctx -> {
                                List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                                int r = IntegerArgumentType.getInteger(ctx, "r");
                                int g = IntegerArgumentType.getInteger(ctx, "g");
                                int b = IntegerArgumentType.getInteger(ctx, "b");
                                for (Player target : targets) {
                                    setter.set(target, r, g, b);
                                }
                                com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.env.updated", "count", targets.size());
                                return 1;
                            })
                        )
                    )
                )
            );
    }
}
