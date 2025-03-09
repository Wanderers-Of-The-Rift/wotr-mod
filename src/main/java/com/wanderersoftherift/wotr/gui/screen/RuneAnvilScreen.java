package com.wanderersoftherift.wotr.gui.screen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.RuneAnvilMenu;
import com.wanderersoftherift.wotr.gui.menu.RunegemSlot;
import com.wanderersoftherift.wotr.item.runegem.RuneGemShape;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RuneAnvilScreen extends AbstractContainerScreen<RuneAnvilMenu> implements ContainerListener {
    private static final ResourceLocation BACKGROUND = WanderersOfTheRift.id("textures/gui/container/rune_anvil/background.png");
    private static final ResourceLocation SLOTS = WanderersOfTheRift.id("textures/gui/container/rune_anvil/slots.png");
    //TODO: Make sure blit is passed correct texture size for all calls.
    public RuneAnvilScreen(RuneAnvilMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
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
        guiGraphics.blit(RenderType::guiTextured, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, 256, 256);
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
                        guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, 18, 0, 18, 18, 256, 256);
                        break;
                    case TRIANGLE:
                        guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, 36, 0, 18, 18, 256, 256);
                        break;
                    case HEART:
                        guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, 54, 0, 18, 18, 256, 256);
                        break;
                    case CIRCLE:
                        guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, 72, 0, 18, 18, 256, 256);
                        break;
                    case SQUARE:
                        guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, 90, 0, 18, 18, 256, 256);
                        break;
                    case PENTAGON:
                        guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, 108, 0, 18, 18, 256, 256);
                        break;
                }
            } else {
                guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, 0, 0, 18, 18, 256, 256);
            }
        }
        super.renderSlot(guiGraphics, slot);
    }

    //TODO: Find replacement for this override.
//    @Override
//    protected void renderSlotHighlight(@NotNull GuiGraphics guiGraphics, @NotNull Slot slot, int mouseX, int mouseY, float partialTick) {
//        if (!slot.isHighlightable()) {
//            return;
//        }
//
//        if (slot instanceof RunegemSlot runegemSlot) {
//            if (runegemSlot.isFake()) {
//                return;
//            }
//            RuneGemShape shape = runegemSlot.getShape();
//            if (shape != null) {
//                int x = slot.x - 1;
//                int y = slot.y - 1;
//                switch (shape) {
//                    case DIAMOND:
//                        guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, 18, 18, 18, 18);
//                        break;
//                    case TRIANGLE:
//                        guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, 36, 18, 18, 18);
//                        break;
//                    case HEART:
//                        guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, 54, 18, 18, 18);
//                        break;
//                    case CIRCLE:
//                        guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, 72, 18, 18, 18);
//                        break;
//                    case SQUARE:
//                        guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, 90, 18, 18, 18);
//                        break;
//                    case PENTAGON:
//                        guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, 108, 18, 18, 18);
//                        break;
//                }
//                return;
//            }
//        }

        //renderSlotHighlight(guiGraphics, slot.x, slot.y, 0, this.getSlotColor(slot.index));
   // }

    @Override
    public void slotChanged(@NotNull AbstractContainerMenu abstractContainerMenu, int i, @NotNull ItemStack itemStack) {

    }

    @Override
    public void dataChanged(@NotNull AbstractContainerMenu abstractContainerMenu, int i, int i1) {

    }
}
