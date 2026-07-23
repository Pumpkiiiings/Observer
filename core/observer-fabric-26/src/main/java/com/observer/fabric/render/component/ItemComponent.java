package com.observer.fabric.render.component;

import com.observer.api.model.ComponentAlignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.DeltaTracker;
import net.minecraft.world.item.ItemStack;

public class ItemComponent implements ObserverComponent {

    private ComponentAlignment alignment;
    private int offsetX;
    private int offsetY;
    private float scale;
    private ItemStack itemStack;

    public ItemComponent(ComponentAlignment alignment, int offsetX, int offsetY, float scale, ItemStack itemStack) {
        this.alignment = alignment;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scale = scale;
        this.itemStack = itemStack;
    }

    public void setItemStack(ItemStack itemStack) {
        this.itemStack = itemStack;
    }

    @Override
    public void setPosition(int x, int y) {
        this.offsetX = x;
        this.offsetY = y;
    }

    @Override
    public void setScale(float scale) {
        this.scale = scale;
    }

    @Override
    public int getX() { return offsetX; }

    @Override
    public int getY() { return offsetY; }

    @Override
    public float getScale() { return scale; }

    @Override
    public void render(GuiGraphicsExtractor context, DeltaTracker tickCounter) {
        if (itemStack == null || itemStack.isEmpty()) return;

        Minecraft client = Minecraft.getInstance();
        int screenW = context.guiWidth();
        int screenH = context.guiHeight();

        // Items are always 16x16 in logical GUI space
        int itemW = 16;
        int itemH = 16;

        float anchorX = resolveAnchorX(alignment, screenW, itemW, scale);
        float anchorY = resolveAnchorY(alignment, screenH, itemH, scale);

        float scaledOffsetX = offsetX / scale;
        float scaledOffsetY = offsetY / scale;

        float finalX = anchorX + scaledOffsetX;
        float finalY = anchorY + scaledOffsetY;

        com.mojang.blaze3d.vertex.PoseStack pose = getPoseStackSafe(context);
        if (pose != null) {
            pose.pushPose();
            pose.scale(scale, scale, 1.0f);
        }

        // item inherently handles depth and lighting for 3D item models
        context.item(itemStack, (int) finalX, (int) finalY);
        context.itemDecorations(client.font, itemStack, (int) finalX, (int) finalY);
        
        if (pose != null) {
            pose.popPose();
        }
    }

    private static float resolveAnchorX(ComponentAlignment alignment, int screenW, int itemW, float scale) {
        return switch (alignment) {
            case TOP_LEFT, CENTER_LEFT, BOTTOM_LEFT -> 0f;
            case TOP_CENTER, CENTER, BOTTOM_CENTER  -> (screenW / (2f * scale)) - (itemW / 2f);
            case TOP_RIGHT, CENTER_RIGHT, BOTTOM_RIGHT -> (screenW / scale) - itemW;
        };
    }

    private static float resolveAnchorY(ComponentAlignment alignment, int screenH, int itemH, float scale) {
        return switch (alignment) {
            case TOP_LEFT, TOP_CENTER, TOP_RIGHT       -> 0f;
            case CENTER_LEFT, CENTER, CENTER_RIGHT     -> (screenH / (2f * scale)) - (itemH / 2f);
            case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> (screenH / scale) - itemH;
        };
    }

    private static java.lang.reflect.Method cachedPoseMethod = null;
    private static java.lang.reflect.Field cachedPoseField = null;
    private static boolean poseSearched = false;

    private static com.mojang.blaze3d.vertex.PoseStack getPoseStackSafe(GuiGraphicsExtractor context) {
        if (!poseSearched) {
            poseSearched = true;
            for (java.lang.reflect.Method m : GuiGraphicsExtractor.class.getMethods()) {
                if (m.getParameterCount() == 0 && com.mojang.blaze3d.vertex.PoseStack.class.isAssignableFrom(m.getReturnType())) {
                    cachedPoseMethod = m;
                    break;
                }
            }
            if (cachedPoseMethod == null) {
                for (java.lang.reflect.Field f : GuiGraphicsExtractor.class.getDeclaredFields()) {
                    if (com.mojang.blaze3d.vertex.PoseStack.class.isAssignableFrom(f.getType())) {
                        cachedPoseField = f;
                        cachedPoseField.setAccessible(true);
                        break;
                    }
                }
            }
        }

        if (cachedPoseMethod != null) {
            try {
                return (com.mojang.blaze3d.vertex.PoseStack) cachedPoseMethod.invoke(context);
            } catch (Exception e) {}
        }
        
        if (cachedPoseField != null) {
            try {
                return (com.mojang.blaze3d.vertex.PoseStack) cachedPoseField.get(context);
            } catch (Exception e) {}
        }
        
        for (java.lang.reflect.Method m : context.getClass().getMethods()) {
            if (m.getParameterCount() == 0 && com.mojang.blaze3d.vertex.PoseStack.class.isAssignableFrom(m.getReturnType())) {
                try {
                    return (com.mojang.blaze3d.vertex.PoseStack) m.invoke(context);
                } catch (Exception e) {}
            }
        }
        
        return null;
    }
}
