package com.observer.paper.layout;

import com.observer.api.model.ComponentAlignment;
import com.observer.api.model.ComponentType;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

/**
 * Loads Layout objects from YAML files in the plugin's /layouts directory.
 *
 * YAML format:
 * <pre>
 * id: lobby
 * components:
 *   title:
 *     type: TEXT
 *     alignment: TOP_LEFT
 *     offset_x: 10
 *     offset_y: 10
 *     scale: 2.0
 *     content: "<aqua>MISTAKEN"
 *   players:
 *     type: TEXT
 *     alignment: TOP_LEFT
 *     offset_x: 10
 *     offset_y: 35
 *     scale: 1.0
 *     content: "&fPlayers: &a%server_online%"
 * </pre>
 */
public final class LayoutLoader {

    private final File layoutsDir;
    private final Logger logger;

    public LayoutLoader(JavaPlugin plugin) {
        this.layoutsDir = new File(plugin.getDataFolder(), "layouts");
        this.logger = plugin.getLogger();
    }

    /**
     * Scans the layouts/ directory and parses every .yml file.
     * @return Map of layout ID → Layout
     */
    public Map<String, Layout> loadAll() {
        Map<String, Layout> result = new LinkedHashMap<>();

        if (!layoutsDir.exists()) {
            layoutsDir.mkdirs();
            logger.info("[Observer] Created layouts directory.");
            return result;
        }

        File[] files = layoutsDir.listFiles((dir, name) -> name.endsWith(".yml"));
        if (files == null) return result;

        for (File file : files) {
            try {
                Layout layout = parse(file);
                if (layout != null) {
                    result.put(layout.getId(), layout);
                    logger.info("[Observer] Loaded layout: " + layout.getId()
                            + " (" + layout.getComponents().size() + " components)");
                }
            } catch (Exception e) {
                logger.warning("[Observer] Failed to load layout file: " + file.getName() + " — " + e.getMessage());
            }
        }

        return result;
    }

    private Layout parse(File file) {
        YamlConfiguration config = YamlConfiguration.loadConfiguration(file);

        String id = config.getString("id");
        if (id == null || id.isBlank()) {
            logger.warning("[Observer] Layout file missing 'id': " + file.getName());
            return null;
        }

        ConfigurationSection componentSection = config.getConfigurationSection("components");
        if (componentSection == null) {
            logger.warning("[Observer] Layout '" + id + "' has no components.");
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
                        if (alignStr.contains("RIGHT")) {
                            textAlignment = com.observer.api.model.TextAlignment.RIGHT;
                        } else if (alignStr.contains("CENTER") && !alignStr.equals("CENTER_LEFT") && !alignStr.equals("CENTER_RIGHT")) {
                            textAlignment = com.observer.api.model.TextAlignment.CENTER;
                        } else {
                            textAlignment = com.observer.api.model.TextAlignment.LEFT;
                        }
                    } catch (IllegalArgumentException e) {
                        // ignore
                    }
                }
                
                if (cs.contains("text_alignment")) {
                    textAlignment = com.observer.api.model.TextAlignment.valueOf(cs.getString("text_alignment").toUpperCase());
                }
                if (cs.contains("anchor")) {
                    String alignStr = cs.getString("anchor").toUpperCase().replace(" ", "_");
                    try {
                        alignment = ComponentAlignment.valueOf(alignStr);
                        if (alignStr.contains("RIGHT")) {
                            textAlignment = com.observer.api.model.TextAlignment.RIGHT;
                        } else if (alignStr.contains("CENTER") && !alignStr.equals("CENTER_LEFT") && !alignStr.equals("CENTER_RIGHT")) {
                            textAlignment = com.observer.api.model.TextAlignment.CENTER;
                        } else {
                            textAlignment = com.observer.api.model.TextAlignment.LEFT;
                        }
                    } catch (IllegalArgumentException e) {
                        // ignore
                    }
                }

                int offsetX = cs.getInt("offset_x", 0);
                int offsetY = cs.getInt("offset_y", 0);
                float scale = (float) cs.getDouble("scale", 1.0);
                logger.info("[Observer-Debug] LayoutLoader resolved scale=" + scale + " for component type=" + type);

                switch (type) {
                    case TEXT -> {
                        String content = cs.getString("content", "");
                        
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

                        // Sound stub parsing
                        if (cs.contains("sound.id")) {
                            String soundId = cs.getString("sound.id");
                            float soundVol = (float) cs.getDouble("sound.volume", 1.0);
                            float soundPitch = (float) cs.getDouble("sound.pitch", 1.0);
                            logger.info("[Observer] Parsed Sound Stub for component " + key + ": " + soundId);
                        }
                        
                        LayoutComponent comp = new com.observer.paper.layout.component.TextComponentImpl(
                                key, alignment, offsetX, offsetY, scale, content, textAlignment, backgroundColor
                        );
                        logger.info("[Observer-Debug] Loaded scale=" + scale + " for " + key);
                        components.put(key, comp);
                    }
                    case ITEM -> {
                        String material = cs.getString("material", "minecraft:stone");
                        int amount = cs.getInt("amount", 1);
                        logger.info("[Observer] Loaded ITEM component: " + key);
                        logger.info(" - Material: " + material);
                        logger.info(" - Amount: " + amount);
                        logger.info(" - Scale: " + scale);
                        logger.info(" - Alignment: " + alignment);
                        
                        LayoutComponent comp = new com.observer.paper.layout.component.ItemComponentImpl(
                                key, alignment, offsetX, offsetY, scale, textAlignment, material, amount
                        );
                        components.put(key, comp);
                    }
                    default -> {
                        logger.warning("[Observer] Unsupported component type: " + type.name());
                    }
                }
            } catch (IllegalArgumentException e) {
                logger.warning("[Observer] Invalid value in component '" + key + "' of layout '" + id + "': " + e.getMessage());
            }
        }

        return new Layout(id, components);
    }
}
