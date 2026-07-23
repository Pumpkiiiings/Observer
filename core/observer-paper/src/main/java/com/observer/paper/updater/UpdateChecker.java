package com.observer.paper.updater;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.observer.paper.ObserverPaper;
import org.bukkit.Bukkit;

import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class UpdateChecker {
    private final ObserverPaper plugin;
    private static final String UPDATE_URL = "https://raw.githubusercontent.com/TuUsuario/Observer/main/version.json";

    private String latestPluginVersion = "1.0.0";
    private String latestModVersion = "1.0.0";
    private boolean isCritical = false;
    private List<String> pluginMessages = new ArrayList<>();
    private List<String> modMessages = new ArrayList<>();
    
    private boolean hasPluginUpdate = false;

    public UpdateChecker(ObserverPaper plugin) {
        this.plugin = plugin;
    }

    public void check() {
        if (!plugin.getConfig().getBoolean("updater.enabled", true)) {
            return;
        }

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL(UPDATE_URL).openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(5000);
                connection.setReadTimeout(5000);

                if (connection.getResponseCode() == 200) {
                    try (InputStreamReader reader = new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8)) {
                        Gson gson = new Gson();
                        JsonObject json = gson.fromJson(reader, JsonObject.class);

                        if (json.has("plugin_version")) {
                            this.latestPluginVersion = json.get("plugin_version").getAsString();
                        }
                        if (json.has("mod_version")) {
                            this.latestModVersion = json.get("mod_version").getAsString();
                        }
                        if (json.has("critical")) {
                            this.isCritical = json.get("critical").getAsBoolean();
                        }
                        if (json.has("message-plugin") && json.get("message-plugin").isJsonArray()) {
                            JsonArray msgArray = json.getAsJsonArray("message-plugin");
                            pluginMessages.clear();
                            for (JsonElement el : msgArray) {
                                pluginMessages.add(el.getAsString());
                            }
                        }
                        if (json.has("message-mod") && json.get("message-mod").isJsonArray()) {
                            JsonArray msgArray = json.getAsJsonArray("message-mod");
                            modMessages.clear();
                            for (JsonElement el : msgArray) {
                                modMessages.add(el.getAsString());
                            }
                        }

                        // Check if plugin is outdated
                        String currentPluginVer = plugin.getPluginMeta().getVersion();
                        if (isNewerVersion(currentPluginVer, latestPluginVersion)) {
                            this.hasPluginUpdate = true;
                            plugin.getLogger().warning("========================================");
                            plugin.getLogger().warning("A new Observer version is available!");
                            plugin.getLogger().warning("Current: " + currentPluginVer + " -> New: " + latestPluginVersion);
                            if (isCritical) {
                                plugin.getLogger().warning("THIS UPDATE IS MARKED AS CRITICAL.");
                            }
                            plugin.getLogger().warning("========================================");
                        }
                    }
                }
            } catch (Exception e) {
                plugin.getLogger().warning("Failed to check for updates: " + e.getMessage());
            }
        });
    }

    public static boolean isNewerVersion(String current, String latest) {
        try {
            String[] currentParts = current.replaceAll("[^0-9.]", "").split("\\.");
            String[] latestParts = latest.replaceAll("[^0-9.]", "").split("\\.");
            int length = Math.max(currentParts.length, latestParts.length);
            for (int i = 0; i < length; i++) {
                int c = i < currentParts.length ? Integer.parseInt(currentParts[i]) : 0;
                int l = i < latestParts.length ? Integer.parseInt(latestParts[i]) : 0;
                if (c < l) return true;
                if (c > l) return false;
            }
        } catch (Exception e) {
            // Fallback
            return !current.equals(latest);
        }
        return false;
    }

    public String getLatestModVersion() {
        return latestModVersion;
    }

    public boolean hasPluginUpdate() {
        return hasPluginUpdate;
    }

    public boolean isCritical() {
        return isCritical;
    }

    public List<String> getPluginMessages() {
        return pluginMessages;
    }

    public List<String> getModMessages() {
        return modMessages;
    }
}
