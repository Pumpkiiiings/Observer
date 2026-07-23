package com.observer.fabric.render.component;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.DeltaTracker;

public interface ObserverComponent {
    void render(GuiGraphicsExtractor context, DeltaTracker tickCounter);
    
    void setPosition(int x, int y);
    void setScale(float scale);
    
    int getX();
    int getY();
    float getScale();
}
