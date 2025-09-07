package com.wanderersoftherift.wotr.client.tooltip;

import com.wanderersoftherift.wotr.util.TextureUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import org.joml.Matrix4f;

import java.util.function.Function;

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

        this.textureWidth = TextureUtils.getTextureWidthGL(this.component.asset());
        this.textureHeight = TextureUtils.getTextureHeightGL(this.component.asset());
    }

    public static void renderImage(ImageComponent component, int x, int y, GuiGraphics guiGraphics) {
        if (component.asset() == null) {
            return;
        }
        var textureWidth = TextureUtils.getTextureWidthGL(component.asset());
        var textureHeight = TextureUtils.getTextureHeightGL(component.asset());
        guiGraphics.blit(RenderType::guiTextured, component.asset(), x, y - 1, 0F, 0F, textureWidth, textureHeight,
                textureWidth, textureHeight);
    }

    public static void renderText(
            ImageComponent component,
            Font pFont,
            int pX,
            int pY,
            Matrix4f pMatrix4f,
            MultiBufferSource.BufferSource pBufferSource,
            Function<Component, Component> textTransform) {
        if (component.asset() == null) {
            pFont.drawInBatch(textTransform.apply(component.base()), pX + 2, pY + 1, 0xAABBCC, true, pMatrix4f,
                    pBufferSource, Font.DisplayMode.NORMAL, 0, 0x00f0_00f0);
            return;
        }
        var textureWidth = TextureUtils.getTextureWidthGL(component.asset());
        pFont.drawInBatch(textTransform.apply(component.base()), pX + textureWidth + 2, pY + 1, 0xAABBCC, true,
                pMatrix4f, pBufferSource, Font.DisplayMode.NORMAL, 0, 0x00f0_00f0);
    }

    public static int getHeight(ImageComponent component, Font font) {
        if (component.asset() == null) {
            return font.lineHeight;
        }
        var textureHeight = TextureUtils.getTextureHeightGL(component.asset());
        return Math.max(textureHeight, font.lineHeight);
    }

    public static int getWidth(ImageComponent component, Font font) {
        if (component.asset() == null) {
            return Math.max(0, font.width(component.base().getString())) + 2;
        }
        var textureWidth = TextureUtils.getTextureWidthGL(component.asset());
        return Math.max(0, font.width(component.base().getString()) + textureWidth) + 2;
    }

    @Override
    public void renderImage(Font font, int x, int y, int width, int height, GuiGraphics guiGraphics) {
        renderImage(component, x, y, guiGraphics);
    }

    @Override
    public void renderText(
            Font pFont,
            int pX,
            int pY,
            Matrix4f pMatrix4f,
            MultiBufferSource.BufferSource pBufferSource) {
        renderText(component, pFont, pX, pY, pMatrix4f, pBufferSource, Function.identity());
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
