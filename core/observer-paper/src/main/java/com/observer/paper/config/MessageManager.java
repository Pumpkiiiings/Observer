package com.observer.paper.config;

import com.observer.paper.ObserverPaper;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class MessageManager {
    private final ObserverPaper plugin;
    private FileConfiguration config;
    private File configFile;
    private final Map<String, String> cache = new HashMap<>();
    private String prefix = "";

    public MessageManager(ObserverPaper plugin) {
        this.plugin = plugin;
        reload();
    }

    public void reload() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        if (!configFile.exists()) {
            plugin.saveResource("messages.yml", false);
        }

        config = YamlConfiguration.loadConfiguration(configFile);
        
        // Setup defaults
        InputStream defaultStream = plugin.getResource("messages.yml");
        if (defaultStream != null) {
            YamlConfiguration defaultConfig = YamlConfiguration.loadConfiguration(new InputStreamReader(defaultStream, StandardCharsets.UTF_8));
            config.setDefaults(defaultConfig);
        }

        cache.clear();
        loadMessages("", config);

        prefix = cache.getOrDefault("prefix", "");
    }

    private void loadMessages(String path, org.bukkit.configuration.ConfigurationSection section) {
        for (String key : section.getKeys(false)) {
            String currentPath = path.isEmpty() ? key : path + "." + key;
            if (section.isConfigurationSection(key)) {
                loadMessages(currentPath, section.getConfigurationSection(key));
            } else if (section.isString(key)) {
                cache.put(currentPath, section.getString(key));
            }
        }
    }

    public String getRaw(String path) {
        return cache.getOrDefault(path, "<red>Missing message: " + path);
    }

    /**
     * Parse a message using MiniMessage.
     * Placeholders should be passed as key-value pairs.
     * E.g. get("commands.hud.text_success", "player", "Steve", "count", "5")
     */
    public Component get(String path, Object... placeholders) {
        String raw = getRaw(path);
        
        // Auto replace prefix if the string contains <prefix>
        if (raw.contains("<prefix>")) {
            raw = raw.replace("<prefix>", prefix);
        }

        TagResolver.Builder resolver = TagResolver.builder();
        
        for (int i = 0; i < placeholders.length; i += 2) {
            if (i + 1 < placeholders.length) {
                String key = placeholders[i].toString();
                Object value = placeholders[i + 1];
                
                if (value instanceof Component comp) {
                    resolver.resolver(Placeholder.component(key, comp));
                } else {
                    resolver.resolver(Placeholder.unparsed(key, value.toString()));
                }
            }
        }

        return MiniMessage.miniMessage().deserialize(raw, resolver.build());
    }

    public void send(CommandSender sender, String path, Object... placeholders) {
        sender.sendMessage(get(path, placeholders));
    }
    
    public static void sendMessage(CommandSender sender, String path, Object... placeholders) {
        if (ObserverPaper.getInstance() != null && ObserverPaper.getInstance().getMessageManager() != null) {
            ObserverPaper.getInstance().getMessageManager().send(sender, path, placeholders);
        } else {
            sender.sendMessage("§c[Observer] Message manager not loaded. Missing: " + path);
        }
    }
}
