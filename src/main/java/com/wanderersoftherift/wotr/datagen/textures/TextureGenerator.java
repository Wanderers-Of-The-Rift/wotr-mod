package com.wanderersoftherift.wotr.datagen.textures;

import net.minecraft.resources.ResourceLocation;

import java.awt.*;
import java.util.function.Consumer;

public class TextureGenerator {
    public final Consumer<TextureTransform> textureOutput;

    public TextureGenerator(Consumer<TextureTransform> textureOutput) {
        this.textureOutput = textureOutput;
    }

    public void tintGenerator(ResourceLocation sourcePath, ResourceLocation destinationPath, int color) {
        this.textureOutput.accept(new TintTransform(sourcePath, destinationPath, color));
    }

    public void tintGenerator(ResourceLocation sourcePath, ResourceLocation destinationPath, Color color) {
        this.textureOutput.accept(new TintTransform(sourcePath, destinationPath, color));
    }
}
