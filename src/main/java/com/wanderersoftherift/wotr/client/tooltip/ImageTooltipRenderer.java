package com.wanderersoftherift.wotr.client.tooltip;

import com.wanderersoftherift.wotr.util.TextureUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;

/**
 * Responsible for being able to render assets passed in as a ResourceLocation<br>
 * Assets passed in get their width/height dynamically calculated, meaning that texturepacks may modify it with their
 * own asset
 */

public class ImageTooltipRenderer implements ClientTooltipComponent {
    private final ImageComponent component;
    private final int textureWidth;
    private final int textureHeight;

    public ImageTooltipRenderer(ImageComponent component) {
        this.component = component;

        this.textureWidth = TextureUtils.getTextureWidth(this.component.asset());
        this.textureHeight = TextureUtils.getTextureHeight(this.component.asset());
    }

    @Override
    public void renderImage(Font font, int x, int y, int width, int height, GuiGraphics guiGraphics) {
        guiGraphics.blit(RenderType::guiTextured, this.component.asset(), x, y - 1, 0F, 0F, this.textureWidth,
                this.textureHeight, this.textureWidth, this.textureHeight);
    }

    @Override
    public void renderText(
            Font pFont,
            int pX,
            int pY,
            Matrix4f pMatrix4f,
            MultiBufferSource.BufferSource pBufferSource) {
        pFont.drawInBatch(component.base(), pX + textureWidth + 2, pY + 1, 0xAABBCC, true, pMatrix4f, pBufferSource,
                Font.DisplayMode.NORMAL, 0, 15_728_880);
    }

    @Override
    public int getHeight(Font font) {
        return Math.max(textureHeight, Minecraft.getInstance().font.lineHeight);
    }

    @Override
    public int getWidth(Font font) {
        return Math.max(0, font.width(this.component.base().getString()) + textureWidth) + 2;
    }

}
