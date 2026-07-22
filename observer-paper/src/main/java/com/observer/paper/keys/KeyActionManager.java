package com.observer.paper.keys;

import com.observer.api.model.KeyAction;
import com.observer.api.model.ObserverKey;
import com.observer.paper.ObserverPaper;
import com.observer.paper.api.event.ObserverKeyEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class KeyActionManager implements Listener {

    private final ObserverPaper plugin;
    private final List<KeyBind> keyBinds = new ArrayList<>();

    public KeyActionManager(ObserverPaper plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
        loadBinds();
    }

    public void loadBinds() {
        keyBinds.clear();
        File keysFolder = new File(plugin.getDataFolder(), "keys");
        if (!keysFolder.exists()) {
            keysFolder.mkdirs();
            // Create a default example
            File example = new File(keysFolder, "test.yml");
            YamlConfiguration config = new YamlConfiguration();
            config.set("key", "F");
            config.set("action", "PRESS");
            
            ConfigurationSection modifiers = config.createSection("modifiers");
            modifiers.set("shift", false);
            modifiers.set("ctrl", false);
            modifiers.set("alt", false);
            
            List<java.util.Map<String, Object>> actions = new ArrayList<>();
            java.util.Map<String, Object> msgAction = new java.util.HashMap<>();
            msgAction.put("type", "MESSAGE");
            msgAction.put("value", "&aHas presionado la tecla F!");
            actions.add(msgAction);
            
            config.set("actions", actions);
            try {
                config.save(example);
            } catch (Exception ignored) {}
        }

        File[] files = keysFolder.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files != null) {
            for (File file : files) {
                try {
                    YamlConfiguration config = YamlConfiguration.loadConfiguration(file);
                    ObserverKey key = ObserverKey.valueOf(config.getString("key", "UNKNOWN").toUpperCase());
                    KeyAction action = KeyAction.valueOf(config.getString("action", "PRESS").toUpperCase());
                    
                    boolean requireShift = config.getBoolean("modifiers.shift", false);
                    boolean requireCtrl = config.getBoolean("modifiers.ctrl", false);
                    boolean requireAlt = config.getBoolean("modifiers.alt", false);

                    List<ActionConfig> actions = new ArrayList<>();
                    List<?> actionsList = config.getList("actions");
                    if (actionsList != null) {
                        for (Object obj : actionsList) {
                            if (obj instanceof java.util.Map) {
                                java.util.Map<?, ?> map = (java.util.Map<?, ?>) obj;
                                String type = (String) map.get("type");
                                String value = (String) map.get("value");
                                if (type != null && value != null) {
                                    actions.add(new ActionConfig(type.toUpperCase(), value));
                                }
                            }
                        }
                    }

                    keyBinds.add(new KeyBind(key, action, requireShift, requireCtrl, requireAlt, actions));
                } catch (Exception e) {
                    plugin.getLogger().warning("Failed to load keybind from " + file.getName() + ": " + e.getMessage());
                }
            }
        }
        plugin.getLogger().info("Loaded " + keyBinds.size() + " key binds.");
    }

    @EventHandler
    public void onKeyEvent(ObserverKeyEvent event) {
        Player player = event.getPlayer();
        for (KeyBind bind : keyBinds) {
            if (bind.key == event.getKey() && bind.action == event.getAction()) {
                if (bind.requireShift == event.isShiftDown() &&
                    bind.requireCtrl == event.isCtrlDown() &&
                    bind.requireAlt == event.isAltDown()) {
                    
                    // Execute actions synchronously
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        // Fire API Match Event
                        int asciiCode = getAsciiFromObserverKey(bind.key);
                        if (asciiCode != -1) {
                            Bukkit.getPluginManager().callEvent(new com.observer.paper.api.events.PlayerKeyMatchEvent(player, asciiCode));
                        }

                        for (ActionConfig action : bind.actions) {
                            String value = action.value.replace("%player%", player.getName());
                            if (action.type.equals("COMMAND")) {
                                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), value);
                            } else if (action.type.equals("MESSAGE")) {
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', value));
                            }
                        }
                    });
                }
            }
        }
    }

    private int getAsciiFromObserverKey(ObserverKey key) {
        if (key.ordinal() >= 0 && key.ordinal() <= 25) {
            return 'A' + key.ordinal();
        } else if (key.ordinal() >= 26 && key.ordinal() <= 35) {
            return '0' + (key.ordinal() - 26);
        }
        return -1;
    }

    private static class KeyBind {
        final ObserverKey key;
        final KeyAction action;
        final boolean requireShift;
        final boolean requireCtrl;
        final boolean requireAlt;
        final List<ActionConfig> actions;

        public KeyBind(ObserverKey key, KeyAction action, boolean requireShift, boolean requireCtrl, boolean requireAlt, List<ActionConfig> actions) {
            this.key = key;
            this.action = action;
            this.requireShift = requireShift;
            this.requireCtrl = requireCtrl;
            this.requireAlt = requireAlt;
            this.actions = actions;
        }
    }

    private static class ActionConfig {
        final String type;
        final String value;

        public ActionConfig(String type, String value) {
            this.type = type;
            this.value = value;
        }
    }
}
