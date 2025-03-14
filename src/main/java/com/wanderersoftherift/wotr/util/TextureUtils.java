package com.wanderersoftherift.wotr.util;

import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

public class TextureUtils {
    /**
     * Returns the file image width of a given {@link ResourceLocation}
     *
     * @param resource the {@link ResourceLocation} to read
     * @return The Image width as an int
     */
    public static int getTextureWidth(ResourceLocation resource) {
        try {
            Optional<Resource> mcResource = Minecraft.getInstance().getResourceManager().getResource(resource);
            if (mcResource.isPresent()) {
                try (InputStream inputStream = mcResource.get().open()) {
                    BufferedImage image = ImageIO.read(inputStream);
                    return image.getWidth();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if the texture can't be loaded
    }

    /**
     * Returns the file image width of a given {@link ResourceLocation}
     *
     * @param resource the {@link ResourceLocation} to read
     * @return The Image height as an int
     */
    public static int getTextureHeight(ResourceLocation resource) {
        try {
            Optional<Resource> mcResource = Minecraft.getInstance().getResourceManager().getResource(resource);
            if (mcResource.isPresent()) {
                try (InputStream inputStream = mcResource.get().open()) {
                    BufferedImage image = ImageIO.read(inputStream);
                    return image.getHeight();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1; // Return -1 if the texture can't be loaded
    }
}
