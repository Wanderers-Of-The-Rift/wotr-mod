package com.wanderersoftherift.wotr.util;

import net.minecraft.client.Minecraft;
import org.joml.Vector2i;

public final class GuiUtil {
    private GuiUtil() {
    }

    /**
     * @return The current mouse screen position
     */
    public static Vector2i getMouseScreenPosition() {
        Minecraft minecraft = Minecraft.getInstance();
        int x = (int) (minecraft.mouseHandler.xpos() * minecraft.getWindow().getGuiScaledWidth()
                / minecraft.getWindow().getScreenWidth());
        int y = (int) (minecraft.mouseHandler.ypos() * minecraft.getWindow().getGuiScaledHeight()
                / minecraft.getWindow().getScreenHeight());
        return new Vector2i(x, y);
    }
}
