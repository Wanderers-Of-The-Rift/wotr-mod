package com.wanderersoftherift.wotr.gui.screen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.RuneAnvilMenu;
import com.wanderersoftherift.wotr.gui.menu.slot.RunegemSlot;
import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import org.jetbrains.annotations.NotNull;

public class RuneAnvilScreen extends AbstractContainerScreen<RuneAnvilMenu> {
    private static final ResourceLocation BACKGROUND = WanderersOfTheRift
            .id("textures/gui/container/rune_anvil/background.png");
    private static final ResourceLocation SLOTS = WanderersOfTheRift.id("textures/gui/container/rune_anvil/slots.png");

    // TODO: Make sure blit is passed correct texture size for all calls.
    public RuneAnvilScreen(RuneAnvilMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 248;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        Button applyButtonWidget = Button
                .builder(Component.translatable("container.wotr.rune_anvil.apply"), (button) -> this.menu.apply())
                .pos(this.leftPos + 115, this.topPos + 145)
                .size(54, 15)
                .build();
        this.addRenderableWidget(applyButtonWidget);
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderType::guiTextured, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth,
                this.imageHeight, 256, 256);
    }

    @Override
    protected void renderSlot(@NotNull GuiGraphics guiGraphics, @NotNull Slot slot) {
        int x = slot.x - 1;
        int y = slot.y - 1;
        if (slot instanceof RunegemSlot runegemSlot) {
            if (runegemSlot.getShape() == null) return;
            RunegemShape shape = runegemSlot.getShape();

            guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, getSlotOffset(shape), 0, 18, 18, 256, 256);
        } else {
            guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, 0, 0, 18, 18, 256, 256);
        }

        super.renderSlot(guiGraphics, slot);

        // Render custom shaped slot overlay for runegem slots
        if (this.hoveredSlot != slot || !(slot instanceof RunegemSlot runegemSlot) || runegemSlot.getShape() == null) {
            return;
        }

        RunegemShape shape = runegemSlot.getShape();
        guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, getSlotOffset(shape), 18, 18, 18, 256, 256);
    }

    private int getSlotOffset(RunegemShape shape) {
        return switch (shape) {
            case DIAMOND -> 18;
            case TRIANGLE -> 36;
            case HEART -> 54;
            case CIRCLE -> 72;
            case SQUARE -> 90;
            case PENTAGON -> 108;
        };
    }
}
