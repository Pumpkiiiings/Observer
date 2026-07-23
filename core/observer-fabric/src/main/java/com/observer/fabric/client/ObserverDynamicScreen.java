package com.observer.fabric.client;

import com.observer.api.menu.ButtonComponent;
import com.observer.api.menu.MenuComponent;
import com.observer.api.menu.TextComponent;
import com.observer.api.menu.TextureComponent;
import com.observer.api.payload.ui.MenuActionPayload;
import com.observer.api.payload.ui.MenuClosePayload;
import com.observer.api.payload.ui.MenuOpenPayload;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

public class ObserverDynamicScreen extends Screen {

    private final MenuOpenPayload payload;

    public ObserverDynamicScreen(MenuOpenPayload payload) {
        super(Component.literal("Observer UI"));
        this.payload = payload;
    }

    @Override
    protected void init() {
        super.init();

        for (MenuComponent comp : payload.components()) {
            if (comp instanceof ButtonComponent btn) {
                // Calculate position
                int x = this.width / 2 + (int) btn.transform().x();
                int y = this.height / 2 + (int) btn.transform().y();
                
                // Adjust for alignment (assuming centered by default for our test)
                // In a real implementation, we would use the transform.horizontalAlignment()
                if (btn.transform().horizontalAlignment() == com.observer.api.menu.HorizontalAlignment.CENTER) {
                    x -= btn.width() / 2;
                }
                if (btn.transform().verticalAlignment() == com.observer.api.menu.VerticalAlignment.CENTER) {
                    y -= btn.height() / 2;
                }

                Button button = Button.builder(Component.empty(), b -> {
                    // Send click event
                    ClientPlayNetworking.send(new MenuActionPayload(payload.menuId(), btn.id()));
                }).bounds(x, y, btn.width(), btn.height()).build();
                
                this.addRenderableWidget(button);
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(graphics, mouseX, mouseY, partialTick);

        for (MenuComponent comp : payload.components()) {
            if (comp instanceof TextureComponent tex) {
                int x = this.width / 2 + (int) tex.transform().x();
                int y = this.height / 2 + (int) tex.transform().y();
                
                if (tex.transform().horizontalAlignment() == com.observer.api.menu.HorizontalAlignment.CENTER) {
                    x -= tex.width() / 2;
                }
                if (tex.transform().verticalAlignment() == com.observer.api.menu.VerticalAlignment.CENTER) {
                    y -= tex.height() / 2;
                }
                
                Identifier rl = Identifier.tryParse(tex.texturePath());
                if (rl != null) {
                    // Simple blit, assuming 256x256 texture sheet for now
                    graphics.blit(net.minecraft.client.renderer.RenderType::guiTextured, rl, x, y, 0f, 0f, tex.width(), tex.height(), tex.width(), tex.height());
                }
            }
        }
        
        // Render text on top
        for (MenuComponent comp : payload.components()) {
            if (comp instanceof TextComponent txt) {
                int x = this.width / 2 + (int) txt.transform().x();
                int y = this.height / 2 + (int) txt.transform().y();
                
                Component textComp = net.minecraft.network.chat.Component.literal(txt.text());
                
                if (txt.transform().horizontalAlignment() == com.observer.api.menu.HorizontalAlignment.CENTER) {
                    x -= this.font.width(textComp) / 2;
                }
                
                // MC 1.21.10: PoseStack → Matrix3x2fStack (JOML), no pushPose/popPose/3D translate.
                // Draw directly at computed x,y — scale is handled by font renderer if needed.
                graphics.drawString(this.font, textComp, x, y, 0xFFFFFF, true);
            }
        }

        super.render(graphics, mouseX, mouseY, partialTick);
    }

    @Override
    public void onClose() {
        super.onClose();
        // Send close event
        ClientPlayNetworking.send(new MenuClosePayload(payload.menuId()));
    }
}
