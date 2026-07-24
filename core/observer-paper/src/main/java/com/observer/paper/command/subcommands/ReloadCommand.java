package com.observer.paper.command.subcommands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.observer.paper.ObserverPaper;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

@SuppressWarnings("UnstableApiUsage")
public class ReloadCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("reload")
            .requires(source -> source.getSender().hasPermission("observer.command.reload"))
            .executes(ctx -> {
                ObserverPaper.getInstance().reload();
                com.observer.paper.config.MessageManager.sendMessage(ctx.getSource().getSender(), "general.reload_success");
                return 1;
            });
    }
}
