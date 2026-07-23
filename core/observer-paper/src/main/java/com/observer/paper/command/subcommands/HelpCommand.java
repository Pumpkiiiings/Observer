package com.observer.paper.command.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

@SuppressWarnings("UnstableApiUsage")
public class HelpCommand {
    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("help")
            .executes(ctx -> {
                com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.help.header");
                com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.help.hud");
                com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.help.animation");
                com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.help.env");
                com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.help.key");
                com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.help.system");
                com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "commands.help.footer");
                return 1;
            });
    }
}
