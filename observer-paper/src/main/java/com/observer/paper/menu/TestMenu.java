package com.observer.paper.menu;

import com.observer.api.menu.HorizontalAlignment;
import com.observer.api.menu.MenuTransform;
import com.observer.api.menu.VerticalAlignment;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TestMenu extends ObserverMenu {

    public TestMenu() {
        super("observer:test");
    }

    @Override
    protected void build(MenuBuilder builder) {
        // Fondo semi-transparente
        builder.addTexture("bg", "minecraft:textures/gui/demo_background.png", 
                MenuTransform.at(0, 0).centered(), 248, 166);

        // Título
        builder.addText("title", ChatColor.GOLD + "Observer " + ChatColor.WHITE + "Server-Driven UI", 
                MenuTransform.at(0, -60).centered(), 1.5f);

        // Texto descriptivo
        builder.addText("desc", "Esta UI fue construida en Java desde el Servidor.", 
                MenuTransform.at(0, -30).centered(), 1.0f);

        // Botón Discord
        builder.addButton("btn_discord", "minecraft:textures/gui/widgets.png", 
                MenuTransform.at(-50, 20).centered(), 80, 20);
        builder.addText("lbl_discord", "Discord", 
                MenuTransform.at(-50, 20).centered(), 1.0f);

        // Botón Cerrar
        builder.addButton("btn_close", "minecraft:textures/gui/widgets.png", 
                MenuTransform.at(50, 20).centered(), 80, 20);
        builder.addText("lbl_close", "Cerrar", 
                MenuTransform.at(50, 20).centered(), 1.0f);
    }

    @Override
    public void onClick(Player player, String reference) {
        if (reference.equals("btn_discord")) {
            player.sendMessage(ChatColor.AQUA + "¡Únete a nuestro Discord: discord.gg/ejemplo!");
        } else if (reference.equals("btn_close")) {
            // No hacemos nada explícito aquí porque el cliente cerrará la UI por su cuenta o podemos enviar un paquete de cierre.
            player.sendMessage(ChatColor.YELLOW + "Has cerrado el menú.");
        }
    }

    @Override
    public void onClose(Player player) {
        player.sendMessage(ChatColor.GRAY + "Menú test cerrado.");
    }
}
