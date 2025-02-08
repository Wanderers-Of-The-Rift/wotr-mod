package com.dimensiondelvers.dimensiondelvers.gui.screen;

import com.dimensiondelvers.dimensiondelvers.gui.menu.AbilityBagMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.ContainerListener;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.*;


public class AbilityBagScreen extends AbstractContainerScreen<AbilityBagMenu> implements ContainerListener {
    //private static final ResourceLocation BACKGROUND = ResourceLocation.tryParse("minecraft:textures/gui/container/inventory.png");
    private static final ResourceLocation SLOT = ResourceLocation.withDefaultNamespace("textures/gui/container/slot.png");
    public AbilityBagScreen(AbilityBagMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.imageHeight = 248;
        this.inventoryLabelY = this.imageHeight - 94;
    }
    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }
    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        //guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }
    @Override
    protected void renderSlot(@NotNull GuiGraphics guiGraphics, @NotNull Slot slot) {
        if (!slot.isFake()) {
            int x = slot.x - 1;
            int y = slot.y - 1;
            guiGraphics.blit(SLOT, x, y, 0, 0, 18, 18);
        }
        super.renderSlot(guiGraphics, slot);
    }
    @Override
    public void containerChanged(Container container) {

    }
}
