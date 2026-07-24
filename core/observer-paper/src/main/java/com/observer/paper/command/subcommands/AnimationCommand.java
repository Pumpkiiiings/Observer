package com.observer.paper.command.subcommands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.resolvers.selector.PlayerSelectorArgumentResolver;
import org.bukkit.entity.Player;
import com.observer.paper.ObserverPaper;
@SuppressWarnings("UnstableApiUsage")
public class AnimationCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("animation")
            .then(Commands.literal("run")
                .then(Commands.argument("player", ArgumentTypes.player())
                    .then(Commands.argument("animation", StringArgumentType.string())
                        .suggests((ctx, builder) -> {
                            String current = builder.getRemaining().toLowerCase();
                            for (String anim : ObserverPaper.getInstance().getAnimationManager().getAnimations().keySet()) {
                                if (anim.toLowerCase().startsWith(current)) {
                                    builder.suggest(anim);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).get(0);
                            String anim = StringArgumentType.getString(ctx, "animation");
                            
                            if (!ObserverPaper.getInstance().getAnimationManager().getAnimations().containsKey(anim)) {
                                com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.animation.not_found", "animation", anim);
                                return 1;
                            }
                            
                            com.observer.paper.api.PaperObserverAnimationAPI.playAnimation(target, anim);
                            com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.animation.play", "animation", anim, "player", target.getName());
                            return 1;
                        })
                    )
                )
            )
            .then(Commands.literal("stop")
                .then(Commands.argument("player", ArgumentTypes.player())
                    .executes(ctx -> {
                        Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).get(0);
                        com.observer.paper.api.PaperObserverAnimationAPI.stopAnimation(target);
                        com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.animation.stop", "player", target.getName());
                        return 1;
                    })
                    .then(Commands.argument("animation", StringArgumentType.string())
                        .suggests((ctx, builder) -> {
                            String current = builder.getRemaining().toLowerCase();
                            for (String anim : ObserverPaper.getInstance().getAnimationManager().getAnimations().keySet()) {
                                if (anim.toLowerCase().startsWith(current)) {
                                    builder.suggest(anim);
                                }
                            }
                            return builder.buildFuture();
                        })
                        .executes(ctx -> {
                            Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).get(0);
                            String anim = StringArgumentType.getString(ctx, "animation");
                            com.observer.paper.api.PaperObserverAnimationAPI.stopAnimation(target);
                            com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.animation.stop", "player", target.getName());
                            return 1;
                        })
                    )
                )
            );
    }
}
