package com.dimensiondelvers.dimensiondelvers.gui.screen;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.gui.menu.RuneAnvilMenu;
import com.dimensiondelvers.dimensiondelvers.gui.menu.RunegemSlot;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RuneGemShape;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RuneAnvilScreen extends AbstractContainerScreen<RuneAnvilMenu> implements ContainerListener {
    private static final ResourceLocation BACKGROUND = DimensionDelvers.id("textures/gui/container/rune_anvil/background.png");
    private static final ResourceLocation SLOTS = DimensionDelvers.id("textures/gui/container/rune_anvil/slots.png");

    public RuneAnvilScreen(RuneAnvilMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 248;
        this.inventoryLabelY = this.imageHeight - 94;

        Button coombineButton = Button
                .builder(Component.translatable("container.dimensiondelvers.rune_anvil.combine"), (button) -> menu.combine())
                .pos(115, 145)
                .size(54, 15)
                .build();
        this.addRenderableWidget(coombineButton);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
    }

    @Override
    protected void renderSlot(@NotNull GuiGraphics guiGraphics, @NotNull Slot slot) {
        if (!slot.isFake()) {
            int x = slot.x - 1;
            int y = slot.y - 1;
            if (slot instanceof RunegemSlot runegemSlot) {
                RuneGemShape shape = runegemSlot.getShape();
                if (shape == null) {
                    return;
                }

                switch (shape) {
                    case DIAMOND:
                        guiGraphics.blit(SLOTS, x, y, 18, 0, 18, 18);
                        break;
                    case TRIANGLE:
                        guiGraphics.blit(SLOTS, x, y, 36, 0, 18, 18);
                        break;
                    case HEART:
                        guiGraphics.blit(SLOTS, x, y, 54, 0, 18, 18);
                        break;
                    case CIRCLE:
                        guiGraphics.blit(SLOTS, x, y, 72, 0, 18, 18);
                        break;
                    case SQUARE:
                        guiGraphics.blit(SLOTS, x, y, 90, 0, 18, 18);
                        break;
                    case PENTAGON:
                        guiGraphics.blit(SLOTS, x, y, 108, 0, 18, 18);
                        break;
                }
            } else {
                guiGraphics.blit(SLOTS, x, y, 0, 0, 18, 18);
            }
        }
        super.renderSlot(guiGraphics, slot);
    }

    @Override
    protected void renderSlotHighlight(@NotNull GuiGraphics guiGraphics, @NotNull Slot slot, int mouseX, int mouseY, float partialTick) {
        if (!slot.isHighlightable()) {
            return;
        }

        if (slot instanceof RunegemSlot runegemSlot) {
            if (runegemSlot.isFake()) {
                return;
            }
            RuneGemShape shape = runegemSlot.getShape();
            if (shape != null) {
                int x = slot.x - 1;
                int y = slot.y - 1;
                switch (shape) {
                    case DIAMOND:
                        guiGraphics.blit(SLOTS, x, y, 18, 18, 18, 18);
                        break;
                    case TRIANGLE:
                        guiGraphics.blit(SLOTS, x, y, 36, 18, 18, 18);
                        break;
                    case HEART:
                        guiGraphics.blit(SLOTS, x, y, 54, 18, 18, 18);
                        break;
                    case CIRCLE:
                        guiGraphics.blit(SLOTS, x, y, 72, 18, 18, 18);
                        break;
                    case SQUARE:
                        guiGraphics.blit(SLOTS, x, y, 90, 18, 18, 18);
                        break;
                    case PENTAGON:
                        guiGraphics.blit(SLOTS, x, y, 108, 18, 18, 18);
                        break;
                }
                return;
            }
        }

        renderSlotHighlight(guiGraphics, slot.x, slot.y, 0, this.getSlotColor(slot.index));
    }

    @Override
    public void slotChanged(@NotNull AbstractContainerMenu abstractContainerMenu, int i, @NotNull ItemStack itemStack) {

    }

    @Override
    public void dataChanged(@NotNull AbstractContainerMenu abstractContainerMenu, int i, int i1) {

    }
}
