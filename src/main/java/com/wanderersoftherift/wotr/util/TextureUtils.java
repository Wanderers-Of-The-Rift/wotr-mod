package com.wanderersoftherift.wotr.util;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL45;

public class TextureUtils {

    /**
     * Returns the image width of a given {@link ResourceLocation}
     *
     * @param resource the {@link ResourceLocation} to read
     * @return The Image width as an int
     */
    public static int getTextureWidthGL(ResourceLocation resource) {
        var id = Minecraft.getInstance().getTextureManager().getTexture(resource).getId();
        return GL45.glGetTextureLevelParameteri(id, 0, GL45.GL_TEXTURE_WIDTH);
    }

    /**
     * Returns the image height of a given {@link ResourceLocation}
     *
     * @param resource the {@link ResourceLocation} to read
     * @return The Image height as an int
     */
    public static int getTextureHeightGL(ResourceLocation resource) {
        var id = Minecraft.getInstance().getTextureManager().getTexture(resource).getId();
        return GL45.glGetTextureLevelParameteri(id, 0, GL45.GL_TEXTURE_HEIGHT);
    }

}
