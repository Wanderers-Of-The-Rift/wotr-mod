package com.wanderersoftherift.wotr.datagen.textures;

import net.minecraft.resources.ResourceLocation;

import java.awt.*;

public class TintTransform extends TextureTransform {
    Color tint;

    public TintTransform(ResourceLocation sourcePath, ResourceLocation destinationPath, int color) {
        super(sourcePath, destinationPath);
        this.tint = new Color(color);
    }

    public TintTransform(ResourceLocation sourcePath, ResourceLocation destinationPath, Color color) {
        super(sourcePath, destinationPath);
        this.tint = color;
    }

    @Override
    public int transform(int x, int y, int pix) {
        Color current = new Color(pix, true);
        return new Color(
                (current.getRed() * tint.getRed()) / 255,
                (current.getGreen() * tint.getGreen()) / 255,
                (current.getBlue() * tint.getBlue()) / 255,
                current.getAlpha()
        ).getRGB();
    }
}