package com.wanderersoftherift.wotr.datagen.textures;

import net.minecraft.resources.ResourceLocation;

public abstract class TextureTransform {
    public ResourceLocation sourcePath;
    public ResourceLocation destinationPath;

    TextureTransform(ResourceLocation sourcePath, ResourceLocation destinationPath) {
        this.sourcePath = sourcePath;
        this.destinationPath = destinationPath;
    }

    public abstract int transform(int x, int y, int pix);
}
