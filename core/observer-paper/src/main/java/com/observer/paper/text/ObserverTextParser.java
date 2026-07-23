package com.observer.paper.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.Map;
import java.util.HashMap;

/**
 * Centralized parser for all Observer text.
 * Converts Legacy, Section, and Hex formats into MiniMessage tags,
 * then processes the entire string using MiniMessage for a unified output.
 */
public class ObserverTextParser {

    private static final Pattern HEX_PATTERN = Pattern.compile("[&§]#([0-9a-fA-F]{6})");
    private static final Pattern LEGACY_PATTERN = Pattern.compile("[&§]([0-9a-fk-orA-FK-OR])");
    private static final MiniMessage MINI_MESSAGE = MiniMessage.miniMessage();
    
    private static final Map<Character, String> LEGACY_MAP = new HashMap<>();
    
    static {
        LEGACY_MAP.put('0', "<black>");
        LEGACY_MAP.put('1', "<dark_blue>");
        LEGACY_MAP.put('2', "<dark_green>");
        LEGACY_MAP.put('3', "<dark_aqua>");
        LEGACY_MAP.put('4', "<dark_red>");
        LEGACY_MAP.put('5', "<dark_purple>");
        LEGACY_MAP.put('6', "<gold>");
        LEGACY_MAP.put('7', "<gray>");
        LEGACY_MAP.put('8', "<dark_gray>");
        LEGACY_MAP.put('9', "<blue>");
        LEGACY_MAP.put('a', "<green>");
        LEGACY_MAP.put('b', "<aqua>");
        LEGACY_MAP.put('c', "<red>");
        LEGACY_MAP.put('d', "<light_purple>");
        LEGACY_MAP.put('e', "<yellow>");
        LEGACY_MAP.put('f', "<white>");
        LEGACY_MAP.put('A', "<green>");
        LEGACY_MAP.put('B', "<aqua>");
        LEGACY_MAP.put('C', "<red>");
        LEGACY_MAP.put('D', "<light_purple>");
        LEGACY_MAP.put('E', "<yellow>");
        LEGACY_MAP.put('F', "<white>");
        
        LEGACY_MAP.put('k', "<obfuscated>");
        LEGACY_MAP.put('l', "<bold>");
        LEGACY_MAP.put('m', "<strikethrough>");
        LEGACY_MAP.put('n', "<underlined>");
        LEGACY_MAP.put('o', "<italic>");
        LEGACY_MAP.put('r', "<reset>");
        LEGACY_MAP.put('K', "<obfuscated>");
        LEGACY_MAP.put('L', "<bold>");
        LEGACY_MAP.put('M', "<strikethrough>");
        LEGACY_MAP.put('N', "<underlined>");
        LEGACY_MAP.put('O', "<italic>");
        LEGACY_MAP.put('R', "<reset>");
    }

    /**
     * Sanitizes mixed legacy, section, and hex formatting into pure MiniMessage tags.
     */
    public static String sanitize(String input) {
        if (input == null || input.isEmpty()) return "";
        
        // 1. Convert Hex (&#RRGGBB or §#RRGGBB) to <#RRGGBB>
        Matcher hexMatcher = HEX_PATTERN.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (hexMatcher.find()) {
            hexMatcher.appendReplacement(sb, "<#" + hexMatcher.group(1) + ">");
        }
        hexMatcher.appendTail(sb);
        String step1 = sb.toString();
        
        // 2. Convert standard legacy to tags
        Matcher legacyMatcher = LEGACY_PATTERN.matcher(step1);
        StringBuffer sb2 = new StringBuffer();
        while (legacyMatcher.find()) {
            char code = legacyMatcher.group(1).charAt(0);
            String replacement = LEGACY_MAP.getOrDefault(code, legacyMatcher.group());
            legacyMatcher.appendReplacement(sb2, replacement);
        }
        legacyMatcher.appendTail(sb2);
        
        // Note: Unlike strict legacy Bukkit parsing where color resets formats, 
        // mapping to MiniMessage allows `<green><bold>` and `<bold><green>` to combine cleanly.
        // This is modern standard and much friendlier for config writers.
        return sb2.toString();
    }
    
    /**
     * Parses the sanitized string into an Adventure Component.
     */
    public static Component parse(String input) {
        return MINI_MESSAGE.deserialize(sanitize(input));
    }
    
    /**
     * Parses the string and converts it directly to a Vanilla Component for network payloads.
     */
    public static net.minecraft.network.chat.Component parseVanilla(String input) {
        return io.papermc.paper.adventure.PaperAdventure.asVanilla(parse(input));
    }
}
