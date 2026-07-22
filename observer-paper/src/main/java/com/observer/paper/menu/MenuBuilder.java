package com.observer.paper.menu;

import com.observer.api.menu.ButtonComponent;
import com.observer.api.menu.MenuComponent;
import com.observer.api.menu.MenuTransform;
import com.observer.api.menu.TextComponent;
import com.observer.api.menu.TextureComponent;

import java.util.ArrayList;
import java.util.List;

public class MenuBuilder {
    private final List<MenuComponent> components = new ArrayList<>();

    public MenuBuilder addTexture(String id, String texturePath, MenuTransform transform, int width, int height) {
        components.add(new TextureComponent(id, transform, texturePath, width, height));
        return this;
    }

    public MenuBuilder addText(String id, String text, MenuTransform transform, float scale) {
        components.add(new TextComponent(id, transform, text, scale));
        return this;
    }

    public MenuBuilder addButton(String id, String texturePath, MenuTransform transform, int width, int height) {
        components.add(new ButtonComponent(id, transform, texturePath, width, height));
        return this;
    }

    public List<MenuComponent> build() {
        return components;
    }
}
