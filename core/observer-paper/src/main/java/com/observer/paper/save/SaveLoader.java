package com.observer.paper.save;

import com.observer.api.model.ComponentAlignment;
import com.observer.api.model.ComponentType;
import com.observer.paper.layout.LayoutComponent;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

public final class SaveLoader {

    private final File savesDir;
    private final Logger logger;

    public SaveLoader(JavaPlugin plugin) {
        this.savesDir = new File(plugin.getDataFolder(), "saves");
        this.logger = plugin.getLogger();
    }

    public Map<String, Save> loadAll() {
        Map<String, Save> result = new LinkedHashMap<>();

        if (!savesDir.exists()) {
            savesDir.mkdirs();
            logger.info("[Observer] Created saves directory.");
            return result;
        }

        loadDirectory(savesDir, result);
        return result;
    }

    private void loadDirectory(File dir, Map<String, Save> result) {
        File[] files = dir.listFiles();
        if (files == null) return;

        for (File file : files) {
            if (file.isDirectory()) {
                loadDirectory(file, result);
            } else if (file.getName().endsWith(".yml")) {
                try {
                    Save save = parse(file);
                    if (save != null) {
                        if (result.containsKey(save.getId())) {
                            logger.warning("[Observer] Duplicate save ID found: " + save.getId() + " in file " + file.getName() + ". Ignoring duplicate.");
                        } else {
                            result.put(save.getId(), save);
                        }
                    }
                } catch (Exception e) {
                    logger.warning("[Observer] Failed to load save file: " + file.getName() + " — " + e.getMessage());
                }
            }
        }
    }

    private Save parse(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        // We use the filename without .yml as the fallback ID if not explicitly specified
        String fallbackId = file.getName().replace(".yml", "");
        String id = config.getString("id", fallbackId);

        ConfigurationSection componentSection = config.getConfigurationSection("components");
        if (componentSection == null) {
            logger.warning("[Observer] Save '" + id + "' has no components.");
            return null;
        }

        Map<String, LayoutComponent> components = new LinkedHashMap<>();

        for (String key : componentSection.getKeys(false)) {
            ConfigurationSection cs = componentSection.getConfigurationSection(key);
            if (cs == null) continue;

            try {
                ComponentType type = ComponentType.valueOf(cs.getString("type", "TEXT").toUpperCase());
                ComponentAlignment alignment = ComponentAlignment.TOP_LEFT;
                com.observer.api.model.TextAlignment textAlignment = com.observer.api.model.TextAlignment.LEFT;

                if (cs.contains("alignment")) {
                    String alignStr = cs.getString("alignment").toUpperCase().replace(" ", "_");
                    try {
                        alignment = ComponentAlignment.valueOf(alignStr);
                    } catch (IllegalArgumentException e) {
                        // ignore
                    }
                    try {
                        textAlignment = com.observer.api.model.TextAlignment.valueOf(alignStr);
                    } catch (IllegalArgumentException e) {
                        // ignore
                    }
                }

                if (cs.contains("text_alignment")) {
                    textAlignment = com.observer.api.model.TextAlignment.valueOf(cs.getString("text_alignment").toUpperCase());
                }
                if (cs.contains("anchor")) {
                    alignment = ComponentAlignment.valueOf(cs.getString("anchor").toUpperCase().replace(" ", "_"));
                }

                int offsetX = cs.getInt("offset_x", 0);
                int offsetY = cs.getInt("offset_y", 0);
                float scale = (float) cs.getDouble("scale", 1.0);
                String content = cs.getString("content", "");

                if (type == ComponentType.TEXT) {
                    java.util.Optional<Integer> backgroundColor = java.util.Optional.empty();
                    if (cs.contains("background_color")) {
                        String bg = cs.getString("background_color");
                        if (bg.startsWith("#")) bg = bg.substring(1);
                        try {
                            backgroundColor = java.util.Optional.of((int) Long.parseLong(bg, 16));
                        } catch (NumberFormatException e) {
                            logger.warning("[Observer] Invalid background_color: " + bg);
                        }
                    }

                    components.put(key, new com.observer.paper.layout.component.TextComponentImpl(key, alignment, offsetX, offsetY, scale, content, textAlignment, backgroundColor));
                }
            } catch (IllegalArgumentException e) {
                logger.warning("[Observer] Invalid value in save component '" + key + "' of save '" + id + "': " + e.getMessage());
            }
        }

        return new Save(id, components);
    }
}
