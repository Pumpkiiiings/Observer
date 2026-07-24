package com.observer.paper.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.observer.paper.command.subcommands.*;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

@SuppressWarnings("UnstableApiUsage")
public class ObserverCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("observer")
            .then(HudCommand.build())
            .then(AnimationCommand.build())
            .then(EnvCommand.build())
            .then(KeyCommand.build())
            .then(SystemCommand.build())
            .then(HelpCommand.build())
            .then(ReloadCommand.build());
    }
}
