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
                ctx.getSource().getSender().sendMessage(Component.text("Observer has been reloaded.", NamedTextColor.GREEN));
                return 1;
            });
    }
}
