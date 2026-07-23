package com.observer.paper.text;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ObserverTextParserTest {

    @Test
    public void testSanitizeLegacy() {
        String input = "&aHello";
        String expected = "<green>Hello";
        assertEquals(expected, ObserverTextParser.sanitize(input));
    }

    @Test
    public void testSanitizeSection() {
        String input = "§aHello";
        String expected = "<green>Hello";
        assertEquals(expected, ObserverTextParser.sanitize(input));
    }

    @Test
    public void testSanitizeHexAmpersand() {
        String input = "&#55FF55Hello";
        String expected = "<#55FF55>Hello";
        assertEquals(expected, ObserverTextParser.sanitize(input));
    }

    @Test
    public void testSanitizeHexSection() {
        String input = "§#55FF55Hello";
        String expected = "<#55FF55>Hello";
        assertEquals(expected, ObserverTextParser.sanitize(input));
    }

    @Test
    public void testSanitizeMixedFormatting() {
        String input = "&#55FF55&lHello";
        String expected = "<#55FF55><bold>Hello";
        assertEquals(expected, ObserverTextParser.sanitize(input));
        
        String input2 = "<bold>&aHello";
        String expected2 = "<bold><green>Hello";
        assertEquals(expected2, ObserverTextParser.sanitize(input2));
    }

    @Test
    public void testParseComponent() {
        Component parsed = ObserverTextParser.parse("&aHello");
        Component expected = Component.text("Hello", NamedTextColor.GREEN);
        assertEquals(expected, parsed);
        
        Component parsedHex = ObserverTextParser.parse("&#55FF55Hello");
        Component expectedHex = Component.text("Hello", TextColor.color(0x55FF55));
        assertEquals(expectedHex, parsedHex);
        
        Component parsedMixed = ObserverTextParser.parse("&#55FF55&lHello");
        Component expectedMixed = Component.text("Hello", TextColor.color(0x55FF55)).decorate(TextDecoration.BOLD);
        assertEquals(expectedMixed, parsedMixed);
    }
}
