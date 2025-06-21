package com.wanderersoftherift.wotr.gui.screen.status;

import com.wanderersoftherift.wotr.gui.menu.status.GuildMenu;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class GuildsScreen extends BaseStatusScreen<GuildMenu> {

    public GuildsScreen(GuildMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, Component.empty());
    }

    @Override
    protected void init() {
        super.init();
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float v, int i, int i1) {

    }
}
