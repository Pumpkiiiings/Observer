package com.observer.fabric.render.component;

import com.observer.api.model.ComponentAlignment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.DeltaTracker;
import net.minecraft.network.chat.Component;

/**
 * Renders a text string on the HUD.
 *
 * Coordinates are resolved at render time from an alignment anchor + pixel
 * offsets, so this component adapts correctly to any screen resolution.
 * The client has zero knowledge of layouts, placeholders, or business logic —
 * it only stores and draws the final resolved string received from Paper.
 */
public class TextComponent implements ObserverComponent {

    private ComponentAlignment alignment;
    private int offsetX;
    private int offsetY;
    private float scale;
    private Component text;
    private com.observer.api.model.TextAlignment textAlignment;
    private java.util.Optional<Integer> backgroundColor;

    public TextComponent(ComponentAlignment alignment, int offsetX, int offsetY, float scale, Component text, com.observer.api.model.TextAlignment textAlignment, java.util.Optional<Integer> backgroundColor) {
        this.alignment = alignment;
        this.offsetX = offsetX;
        this.offsetY = offsetY;
        this.scale = scale;
        this.text = text;
        this.textAlignment = textAlignment;
        this.backgroundColor = backgroundColor;
        com.observer.fabric.ObserverFabric.LOGGER.info("[Observer-Debug] Created TextComponent scale={}", scale);
    }

    public void setText(Component text) {
        this.text = text;
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
    public void render(GuiGraphics context, DeltaTracker tickCounter) {
        if (text == null) return;

        Minecraft client = Minecraft.getInstance();
        int screenW = context.guiWidth();
        int screenH = context.guiHeight();
        int textW = client.font.width(text);
        int textH = client.font.lineHeight;

        float anchorX = switch (alignment) {
            case TOP_LEFT, CENTER_LEFT, BOTTOM_LEFT -> 0f;
            case TOP_CENTER, CENTER, BOTTOM_CENTER  -> screenW / 2f;
            case TOP_RIGHT, CENTER_RIGHT, BOTTOM_RIGHT -> (float) screenW;
        };
        float anchorY = switch (alignment) {
            case TOP_LEFT, TOP_CENTER, TOP_RIGHT       -> 0f;
            case CENTER_LEFT, CENTER, CENTER_RIGHT     -> screenH / 2f;
            case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> (float) screenH;
        };

        float originX = anchorX + offsetX;
        float originY = anchorY + offsetY;

        float pivotX = switch (textAlignment) {
            case LEFT -> 0f;
            case CENTER -> -textW / 2f;
            case RIGHT -> -textW;
        };
        float pivotY = switch (alignment) {
            case TOP_LEFT, TOP_CENTER, TOP_RIGHT       -> 0f;
            case CENTER_LEFT, CENTER, CENTER_RIGHT     -> -textH / 2f;
            case BOTTOM_LEFT, BOTTOM_CENTER, BOTTOM_RIGHT -> -textH;
        };

        Object matrices = getMatricesSafe(context);
        boolean isPoseStack = matrices instanceof com.mojang.blaze3d.vertex.PoseStack;
        boolean isJomlStack = matrices != null && matrices.getClass().getName().contains("Matrix3x2fStack");

        if (isPoseStack) {
            ((com.mojang.blaze3d.vertex.PoseStack) matrices).pushPose();
            ((com.mojang.blaze3d.vertex.PoseStack) matrices).translate(originX, originY, 0.0f);
            ((com.mojang.blaze3d.vertex.PoseStack) matrices).scale(scale, scale, 1.0f);
        } else if (isJomlStack) {
            try {
                matrices.getClass().getMethod("pushMatrix").invoke(matrices);
                matrices.getClass().getMethod("translate", float.class, float.class).invoke(matrices, originX, originY);
                matrices.getClass().getMethod("scale", float.class, float.class).invoke(matrices, scale, scale);
            } catch (Exception e) {}
        }

        if (backgroundColor != null && backgroundColor.isPresent()) {
            int padding = 2;
            context.fill((int) pivotX - padding, (int) pivotY - padding, (int) pivotX + textW + padding, (int) pivotY + textH + padding, backgroundColor.get());
        }

        renderText(context, client, text, (int) pivotX, (int) pivotY);
        
        if (isPoseStack) {
            ((com.mojang.blaze3d.vertex.PoseStack) matrices).popPose();
        } else if (isJomlStack) {
            try {
                matrices.getClass().getMethod("popMatrix").invoke(matrices);
            } catch (Exception e) {}
        }
    }

    private static java.lang.reflect.Method cachedMatrixMethod = null;
    private static boolean matrixSearched = false;

    private static Object getMatricesSafe(GuiGraphics context) {
        if (!matrixSearched) {
            matrixSearched = true;
            for (java.lang.reflect.Method m : context.getClass().getMethods()) {
                if (m.getParameterCount() == 0) {
                    String rt = m.getReturnType().getName();
                    if (rt.contains("PoseStack") || rt.contains("Matrix3x2fStack")) {
                        cachedMatrixMethod = m;
                        break;
                    }
                }
            }
        }
        
        if (cachedMatrixMethod != null) {
            try {
                return cachedMatrixMethod.invoke(context);
            } catch (Exception e) {}
        }
        return null;
    }

    private static java.lang.reflect.Method cachedDrawMethod = null;
    private static boolean drawMethodSearched = false;

    private static void renderText(GuiGraphics context, Minecraft client, Component text, int x, int y) {
        if (!drawMethodSearched) {
            drawMethodSearched = true;
            for (java.lang.reflect.Method m : context.getClass().getMethods()) {
                Class<?>[] p = m.getParameterTypes();
                if (p.length == 5
                        && p[0] == net.minecraft.client.gui.Font.class
                        && p[1] == Component.class
                        && p[2] == int.class
                        && p[3] == int.class
                        && p[4] == int.class) {
                    cachedDrawMethod = m;
                    break;
                }
            }
        }

        try {
            if (cachedDrawMethod != null) {
                cachedDrawMethod.invoke(context, client.font, text, x, y, 0xFFFFFFFF);
            } else {
                context.drawString(client.font, text, x, y, 0xFFFFFFFF);
            }
        } catch (Exception e) {
            com.observer.fabric.ObserverFabric.LOGGER.warn("[Observer] TextComponent render failed at ({},{}): {}", x, y, e.getMessage());
        }
    }
}
