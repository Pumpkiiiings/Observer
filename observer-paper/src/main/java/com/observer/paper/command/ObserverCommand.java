package com.observer.paper.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
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
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;


@SuppressWarnings("UnstableApiUsage")
public class ObserverCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> build() {
        return Commands.literal("observer")
            
            // /observer reload
            .then(Commands.literal("reload")
                .executes(ctx -> {
                    ObserverPaper.getInstance().reload();
                    ctx.getSource().getSender().sendMessage("§a[Observer] All systems reloaded successfully.");
                    return 1;
                })
            )
            


            // /observer key ...
            .then(Commands.literal("key")
                .then(Commands.literal("stats")
                    .executes(ctx -> {
                        com.observer.paper.keys.ObserverKeyboardManager km = ObserverPaper.getInstance().getKeyboardManager();
                        ctx.getSource().getSender().sendMessage("§e=== Keyboard Stats ===");
                        ctx.getSource().getSender().sendMessage("§7Packets Received: §f" + km.getPacketsReceived());
                        ctx.getSource().getSender().sendMessage("§7Last Global Update: §f" + (System.currentTimeMillis() - km.getLastUpdateGlobal()) + "ms ago");
                        ctx.getSource().getSender().sendMessage("§7Players Tracked: §f" + km.getTrackedPlayersCount());
                        for (Player p : Bukkit.getOnlinePlayers()) {
                            java.util.Set<Byte> keys = km.getPressedKeys(p.getUniqueId());
                            if (!keys.isEmpty()) {
                                ctx.getSource().getSender().sendMessage("§7- " + p.getName() + " Active Keys: §f" + keys.size());
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
                            ctx.getSource().getSender().sendMessage("§a[Observer] Keys pressed by " + target.getName() + ": §f" + keys);
                            return 1;
                        })
                    )
                )
                .then(Commands.literal("flush")
                    .then(Commands.argument("player", ArgumentTypes.player())
                        .executes(ctx -> {
                            Player target = ctx.getArgument("player", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource()).get(0);
                            com.observer.paper.api.PaperObserverKeyboardAPI.flushKeys(target);
                            ctx.getSource().getSender().sendMessage("§a[Observer] Flushed keyboard state for " + target.getName());
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
                                ctx.getSource().getSender().sendMessage("§a[Observer] Key " + ascii + " is pressed by " + target.getName());
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
                                    ctx.getSource().getSender().sendMessage("§a[Observer] Key group matched perfectly: " + expected);
                                    return 1;
                                } else {
                                    throw new com.mojang.brigadier.exceptions.SimpleCommandExceptionType(
                                        new com.mojang.brigadier.LiteralMessage("Missing keys: " + missing + ", Extra keys: " + extra)
                                    ).create();
                                }
                            })
                        )
                    )
                )
            )

            // /observer itemtest
            .then(Commands.literal("itemtest")
                .executes(ctx -> {
                    if (!(ctx.getSource().getSender() instanceof Player player)) {
                        ctx.getSource().getSender().sendMessage("§cOnly players can run this command.");
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
                    player.sendMessage("§aItem test component spawned in the center of your screen.");
                    return 1;
                })
            )

            // /observer envtest
            .then(Commands.literal("envtest")
                .executes(ctx -> {
                    if (!(ctx.getSource().getSender() instanceof Player player)) {
                        ctx.getSource().getSender().sendMessage("§cOnly players can run this command.");
                        return 0;
                    }
                    ObserverPaper.getInstance().getLogger().info(
                            "[STAGE-1][ENV-LOADED] /observer envtest triggered by " + player.getName()
                            + " | isObserver=" + ObserverAPI.isObserverPlayer(player));

                    ObserverAPI.setSkyColor(player, 255, 0, 0);
                    ObserverAPI.setFogColor(player, 0, 255, 0);
                    ObserverAPI.setMoonColor(player, 0, 0, 255);
                    ObserverAPI.setTrueDarkness(player, true);
                    ObserverAPI.startDenseFog(player, 2f, 8f, 1f);

                    ObserverPaper.getInstance().getLogger().info(
                            "[STAGE-1][ENV-LOADED] /observer envtest dispatched all 5 env packets for " + player.getName());
                    player.sendMessage("§a[Observer] Dispatched environment test sequence. Check server log for [STAGE-1/2] and client log for [STAGE-3/4/5/6].");
                    return 1;
                })
            )

            // /observer parse <text>
            .then(Commands.literal("parse")
                .then(Commands.argument("text", StringArgumentType.greedyString())
                    .executes(ctx -> {
                        String input = StringArgumentType.getString(ctx, "text");
                        String sanitized = com.observer.paper.text.ObserverTextParser.sanitize(input);
                        net.kyori.adventure.text.Component result = com.observer.paper.text.ObserverTextParser.parse(input);
                        String json = net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(result);
                        
                        ctx.getSource().getSender().sendMessage("§7Original: §f" + input);
                        ctx.getSource().getSender().sendMessage("§7Sanitized: §f" + sanitized);
                        ctx.getSource().getSender().sendMessage("§7MiniMessage: §f" + net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().serialize(result));
                        ctx.getSource().getSender().sendMessage("§7Component JSON: §f" + json);
                        ctx.getSource().getSender().sendMessage(net.kyori.adventure.text.Component.text("Rendered Preview: ").color(net.kyori.adventure.text.format.NamedTextColor.GRAY).append(result));
                        return 1;
                    })
                )
            )

            // /observer debug [colors]
            .then(Commands.literal("debug")
                .executes(ctx -> {
                    ctx.getSource().getSender().sendMessage("§e=== Observer Debug ===");
                    ctx.getSource().getSender().sendMessage("§7Version: §f" + ObserverPaper.getInstance().getPluginMeta().getVersion());
                    ctx.getSource().getSender().sendMessage("§7Loaded Layouts: §f" + ObserverPaper.getInstance().getLayoutManager().getRegistry().getAll().size());
                    ctx.getSource().getSender().sendMessage("§7Loaded Saves: §f" + ObserverPaper.getInstance().getSaveManager().getAll().size());
                    ctx.getSource().getSender().sendMessage("§7Observer Clients: §f" + ObserverPaper.getInstance().getPlayerManager().getObserverPlayerCount());
                    
                    com.observer.paper.audio.ObserverSoundManager sm = ObserverPaper.getInstance().getSoundManager();
                    ctx.getSource().getSender().sendMessage("§7Audio Manager: §fNative Adventure API (Lectern mode)");
                    ctx.getSource().getSender().sendMessage("§7Total Sounds Sent: §f" + sm.getTotalSoundsSent());
                    ctx.getSource().getSender().sendMessage("§7Last Played Sound: §f" + sm.getLastPlayedSound());
                    return 1;
                })
                .then(Commands.literal("colors")
                    .then(Commands.argument("text", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            String input = StringArgumentType.getString(ctx, "text");
                            String sanitized = com.observer.paper.text.ObserverTextParser.sanitize(input);
                            net.kyori.adventure.text.Component result = com.observer.paper.text.ObserverTextParser.parse(input);
                            String json = net.kyori.adventure.text.serializer.gson.GsonComponentSerializer.gson().serialize(result);
                            
                            ctx.getSource().getSender().sendMessage("§7Original: §f" + input);
                            ctx.getSource().getSender().sendMessage("§7Sanitized: §f" + sanitized);
                            ctx.getSource().getSender().sendMessage("§7MiniMessage: §f" + net.kyori.adventure.text.minimessage.MiniMessage.miniMessage().serialize(result));
                            ctx.getSource().getSender().sendMessage("§7Component JSON: §f" + json);
                            try {
                                net.minecraft.network.chat.Component vanilla = com.observer.paper.text.ObserverTextParser.parseVanilla(input);
                                String payloadJson = net.minecraft.network.chat.Component.Serializer.toJson(vanilla, net.minecraft.server.MinecraftServer.getServer().registryAccess());
                                ctx.getSource().getSender().sendMessage("§7Payload JSON: §f" + payloadJson);
                            } catch (Exception | Error e) {
                                ctx.getSource().getSender().sendMessage("§7Payload JSON: §cError: " + e.getMessage());
                            }
                            return 1;
                        })
                    )
                )
            )

            // /observer resources reload
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
                        ctx.getSource().getSender().sendMessage("§a[Observer] Sent resource reload payload to all clients.");
                        return 1;
                    })
                )
            )

            // /observer layout reload
            .then(Commands.literal("layout")
                .then(Commands.literal("reload")
                    .executes(ctx -> {
                        ObserverPaper.getInstance().reload();
                        ctx.getSource().getSender().sendMessage("§a[Observer] All systems reloaded successfully.");
                        return 1;
                    })
                )
            )

            // /observer text <scale> <text...>
            .then(Commands.literal("text")
                .then(Commands.argument("scale", FloatArgumentType.floatArg(0.1f, 10.0f))
                    .then(Commands.argument("text", StringArgumentType.greedyString())
                        .executes(ctx -> {
                            if (!(ctx.getSource().getSender() instanceof Player player)) {
                                ctx.getSource().getSender().sendMessage("Only players can use this command.");
                                return 0;
                            }
                            float scale = FloatArgumentType.getFloat(ctx, "scale");
                            String text = StringArgumentType.getString(ctx, "text");
                            ObserverAPI.createText(player, "test:hud", com.observer.paper.text.ObserverTextParser.sanitize(text),
                                    ComponentAlignment.CENTER, 0, 0, scale, com.observer.api.model.TextAlignment.LEFT);
                            player.sendMessage("§aSent test component to HUD!");
                            return 1;
                        })
                    )
                )
            )

            // /observer clear <targets>
            .then(Commands.literal("clear")
                .then(Commands.argument("targets", ArgumentTypes.players())
                    .executes(ctx -> {
                        List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                        for (Player p : targets) {
                            ObserverAPI.clearHUD(p);
                        }
                        ctx.getSource().getSender().sendMessage("§a[Observer] Cleared HUD for " + targets.size() + " players.");
                        return 1;
                    })
                )
            )

            // /observer hide <layoutId> <targets>
            .then(Commands.literal("hide")
                .then(Commands.argument("layoutId", StringArgumentType.word())
                    .suggests(ObserverCommand::suggestLayouts)
                    .then(Commands.argument("targets", ArgumentTypes.players())
                        .executes(ctx -> {
                            String layoutId = StringArgumentType.getString(ctx, "layoutId");
                            List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                            LayoutManager lm = ObserverPaper.getInstance().getLayoutManager();
                            for (Player p : targets) {
                                lm.hide(layoutId, p);
                            }
                            ctx.getSource().getSender().sendMessage("§a[Observer] Hidden layout §e" + layoutId + " §afrom " + targets.size() + " players.");
                            return 1;
                        })
                    )
                )
            )

            // /observer display <layoutId|saveId> <targets>
            .then(Commands.literal("display")
                .then(Commands.argument("id", StringArgumentType.word())
                    .suggests(ObserverCommand::suggestLayoutsAndSaves)
                    .then(Commands.argument("targets", ArgumentTypes.players())
                        .executes(ctx -> {
                            String id = StringArgumentType.getString(ctx, "id");
                            List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                            LayoutManager lm = ObserverPaper.getInstance().getLayoutManager();
                            com.observer.paper.save.SaveManager sm = ObserverPaper.getInstance().getSaveManager();

                            boolean isLayout = lm.getRegistry().get(id).isPresent();
                            boolean isSave = sm.getSave(id).isPresent();

                            if (!isLayout && !isSave) {
                                ctx.getSource().getSender().sendMessage("§cUnknown layout or save: " + id);
                                return 0;
                            }

                            for (Player p : targets) {
                                if (isLayout) lm.show(id, p);
                                else sm.display(id, p);
                            }
                            ctx.getSource().getSender().sendMessage("§a[Observer] Displayed §e" + id + " §ato " + targets.size() + " players.");
                            return 1;
                        })
                    )
                )
            )

            // /observer menu <menuId> <targets>
            .then(Commands.literal("menu")
                .then(Commands.argument("menuId", StringArgumentType.string())
                    .then(Commands.argument("targets", ArgumentTypes.players())
                        .executes(ctx -> {
                            String menuId = StringArgumentType.getString(ctx, "menuId");
                            List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                            for (Player p : targets) {
                                ObserverAPI.openMenu(p, menuId);
                            }
                            ctx.getSource().getSender().sendMessage("§a[Observer] Opened menu §e" + menuId + " §afor " + targets.size() + " players.");
                            return 1;
                        })
                    )
                )
            )

            // /observer sound <targets> <sound> [volume] [pitch]
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
            )

            // /observer env <targets> <fog|sky|moon|darkness|dense> ...
            .then(Commands.literal("env")
                .then(Commands.argument("targets", ArgumentTypes.players())
                    .then(Commands.literal("darkness")
                        .then(Commands.argument("enabled", BoolArgumentType.bool())
                            .executes(ctx -> {
                                List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                                boolean enabled = BoolArgumentType.getBool(ctx, "enabled");
                                for (Player target : targets) {
                                    ObserverAPI.setTrueDarkness(target, enabled);
                                }
                                ctx.getSource().getSender().sendMessage("§aEnvironment updated for " + targets.size() + " players.");
                                return 1;
                            })
                        )
                    )
                    .then(Commands.literal("dense")
                        .then(Commands.literal("reset")
                            .executes(ctx -> {
                                List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                                for (Player target : targets) {
                                    ObserverAPI.stopDenseFog(target);
                                }
                                ctx.getSource().getSender().sendMessage("§aEnvironment updated for " + targets.size() + " players.");
                                return 1;
                            })
                        )
                        .then(Commands.argument("start", FloatArgumentType.floatArg())
                            .then(Commands.argument("end", FloatArgumentType.floatArg())
                                .then(Commands.argument("alpha", FloatArgumentType.floatArg())
                                    .executes(ctx -> {
                                        List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                                        float start = FloatArgumentType.getFloat(ctx, "start");
                                        float end = FloatArgumentType.getFloat(ctx, "end");
                                        float alpha = FloatArgumentType.getFloat(ctx, "alpha");
                                        for (Player target : targets) {
                                            ObserverAPI.startDenseFog(target, start, end, alpha);
                                        }
                                        ctx.getSource().getSender().sendMessage("§aEnvironment updated for " + targets.size() + " players.");
                                        return 1;
                                    })
                                )
                            )
                        )
                    )
                    // fog, sky, moon ...
                    .then(buildEnvColorCommand("fog", 
                        (p, r, g, b) -> ObserverAPI.setFogColor(p, r, g, b),
                        (p) -> ObserverAPI.resetFogColor(p)))
                    .then(buildEnvColorCommand("sky", 
                        (p, r, g, b) -> ObserverAPI.setSkyColor(p, r, g, b),
                        (p) -> ObserverAPI.resetSkyColor(p)))
                    .then(buildEnvColorCommand("moon", 
                        (p, r, g, b) -> ObserverAPI.setMoonColor(p, r, g, b),
                        (p) -> ObserverAPI.resetMoonColor(p)))
                )
            )

            // /observer effect <targets> shake|tint ...
            .then(Commands.literal("effect")
                .then(Commands.argument("targets", ArgumentTypes.players())
                    .then(Commands.literal("shake")
                        .then(Commands.argument("intensity", FloatArgumentType.floatArg(0.0f))
                            .then(Commands.argument("duration", IntegerArgumentType.integer(1))
                                .executes(ctx -> {
                                    List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                                    float intensity = FloatArgumentType.getFloat(ctx, "intensity");
                                    int duration = IntegerArgumentType.getInteger(ctx, "duration");
                                    for (Player target : targets) {
                                        com.observer.paper.api.PaperObserverScreenAPI.playScreenshake(target, intensity, duration);
                                    }
                                    ctx.getSource().getSender().sendMessage("§a[Observer] Played screenshake on " + targets.size() + " players.");
                                    return 1;
                                })
                            )
                        )
                    )
                    .then(Commands.literal("tint")
                        .then(Commands.argument("r", IntegerArgumentType.integer(0, 255))
                            .then(Commands.argument("g", IntegerArgumentType.integer(0, 255))
                                .then(Commands.argument("b", IntegerArgumentType.integer(0, 255))
                                    .then(Commands.argument("alpha", FloatArgumentType.floatArg(0.0f, 1.0f))
                                        .then(Commands.argument("duration", IntegerArgumentType.integer(1))
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
                                                ctx.getSource().getSender().sendMessage("§a[Observer] Played screen tint on " + targets.size() + " players.");
                                                return 1;
                                            })
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
                                                ctx.getSource().getSender().sendMessage("§a[Observer] Played screen vignette on " + targets.size() + " players.");
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

    private static int executeSound(CommandContext<CommandSourceStack> ctx, float volume, float pitch) throws com.mojang.brigadier.exceptions.CommandSyntaxException {
        List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
        String sound = StringArgumentType.getString(ctx, "sound");
        for (Player target : targets) {
            ObserverAPI.playSound(target, sound, volume, pitch);
        }
        ctx.getSource().getSender().sendMessage("§aPlaying sound " + sound + " to " + targets.size() + " players (v:" + volume + " p:" + pitch + ")");
        return 1;
    }

    private interface ColorSetter { void set(Player p, int r, int g, int b); }
    private interface ColorResetter { void reset(Player p); }

    private static LiteralArgumentBuilder<CommandSourceStack> buildEnvColorCommand(String name, ColorSetter setter, ColorResetter resetter) {
        return Commands.literal(name)
            .then(Commands.literal("reset")
                .executes(ctx -> {
                    List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                    for (Player target : targets) {
                        resetter.reset(target);
                    }
                    ctx.getSource().getSender().sendMessage("§aEnvironment updated for " + targets.size() + " players.");
                    return 1;
                })
            )
            .then(Commands.argument("r", IntegerArgumentType.integer(0, 255))
                .then(Commands.argument("g", IntegerArgumentType.integer(0, 255))
                    .then(Commands.argument("b", IntegerArgumentType.integer(0, 255))
                        .executes(ctx -> {
                            List<Player> targets = ctx.getArgument("targets", PlayerSelectorArgumentResolver.class).resolve(ctx.getSource());
                            int r = IntegerArgumentType.getInteger(ctx, "r");
                            int g = IntegerArgumentType.getInteger(ctx, "g");
                            int b = IntegerArgumentType.getInteger(ctx, "b");
                            for (Player target : targets) {
                                setter.set(target, r, g, b);
                            }
                            ctx.getSource().getSender().sendMessage("§aEnvironment updated for " + targets.size() + " players.");
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
