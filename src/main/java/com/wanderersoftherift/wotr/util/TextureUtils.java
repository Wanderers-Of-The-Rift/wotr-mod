package com.wanderersoftherift.wotr.util;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import org.lwjgl.opengl.GL33;

public class TextureUtils {

    /**
     * Returns the image width of a given {@link ResourceLocation}
     *
     * @param resource the {@link ResourceLocation} to read
     * @return The Image width as an int
     */
    public static int getTextureWidthGL(ResourceLocation resource) {
        if (!RenderSystem.isOnRenderThread()) {
            return -1;
        }
        var id = Minecraft.getInstance().getTextureManager().getTexture(resource).getId();
        // return GL45.glGetTextureLevelParameteri(id, 0, GL45.GL_TEXTURE_WIDTH); // requires OpenGL 4.5
        var current = GL33.glGetInteger(GL33.GL_TEXTURE_BINDING_2D);
        try {
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, id);
            return GL33.glGetTexLevelParameteri(GL33.GL_TEXTURE_2D, 0, GL33.GL_TEXTURE_WIDTH);
        } finally {
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, current);
        }
    }

    /**
     * Returns the image height of a given {@link ResourceLocation}
     *
     * @param resource the {@link ResourceLocation} to read
     * @return The Image height as an int
     */
    public static int getTextureHeightGL(ResourceLocation resource) {
        if (!RenderSystem.isOnRenderThread()) {
            return -1;
        }
        var id = Minecraft.getInstance().getTextureManager().getTexture(resource).getId();
        // return GL45.glGetTextureLevelParameteri(id, 0, GL45.GL_TEXTURE_HEIGHT); // requires OpenGL 4.5
        var current = GL33.glGetInteger(GL33.GL_TEXTURE_BINDING_2D);
        try {
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, id);
            return GL33.glGetTexLevelParameteri(GL33.GL_TEXTURE_2D, 0, GL33.GL_TEXTURE_HEIGHT);
        } finally {
            GL33.glBindTexture(GL33.GL_TEXTURE_2D, current);
        }
    }

}
