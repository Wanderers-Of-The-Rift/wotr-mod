package com.wanderersoftherift.wotr.gui.screen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.RuneAnvilMenu;
import com.wanderersoftherift.wotr.gui.menu.slot.RunegemSlot;
import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.wanderersoftherift.wotr.init.ModItems;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
import com.wanderersoftherift.wotr.item.runegem.RunegemTier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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

        this.renderItemPreview(guiGraphics);
    }

    private void renderItemPreview(@NotNull GuiGraphics guiGraphics) {
        ItemStack gearItemStack = this.menu.getGearSlotItem();
        renderItemPreviewSingle(guiGraphics, gearItemStack, false);

        ItemStack previewStack = this.menu.createPreviewStack();
        renderItemPreviewSingle(guiGraphics, previewStack, true);
    }

    private void renderItemPreviewSingle(@NotNull GuiGraphics guiGraphics, ItemStack itemStack, boolean right) {
        if (!itemStack.isEmpty()) {
            List<Component> tooltip = this.getTooltipFromContainerItem(itemStack);
            List<ClientTooltipComponent> clientTooltip = ClientHooks.gatherTooltipComponents(itemStack, tooltip, 0,
                    this.width, this.height, this.font);

            int width = clientTooltip.stream()
                    .map((component) -> component.getWidth(this.font))
                    .max(Integer::compareTo)
                    .orElse(0) + 8;
            int height = clientTooltip.stream()
                    .map((component) -> component.getHeight(this.font))
                    .reduce(0, Integer::sum) + 8;

            if (width > this.leftPos) return; // don't show tooltips if they would be wider than the available space

            int leftMiddle = this.leftPos / 2;
            int rightMiddle = this.leftPos + this.imageWidth + leftMiddle;

            int leftX = leftMiddle - width / 2;
            int rightX = rightMiddle - width / 2;

            int y = this.topPos + this.imageHeight / 2 - height / 2;

            // we have to offset this by x-8 and y+16 because we do not want it to use the mouse offset - thank mojank
            if (!right) {
                guiGraphics.renderTooltip(this.font, itemStack, leftX - 8, y + 16);
            } else {
                guiGraphics.renderTooltip(this.font, itemStack, rightX - 8, y + 16);
            }
        }
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
            if (runegemSlot.getShape() == null) {
                return;
            }
            RunegemShape shape = runegemSlot.getShape();

            guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, getSlotOffset(shape, false), 0, 18, 18, 256, 256);
        } else {
            guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, 0, 0, 18, 18, 256, 256);
        }

        if (!slot.hasItem() && slot instanceof RunegemSlot runegemSlot && runegemSlot.getLockedSocket() != null
                && runegemSlot.getShape() != null) {
            ItemStack stack = new ItemStack(ModItems.RUNEGEM.get());
            RunegemShape shape = runegemSlot.getShape();
            stack.set(ModDataComponentType.RUNEGEM_DATA, new RunegemData(shape, List.of(), RunegemTier.RAW));

            super.renderSlotContents(guiGraphics, stack, slot, null);
            guiGraphics.blit(RenderType::guiTexturedOverlay, SLOTS, x, y, getSlotOffset(shape, true), 18, 18, 18, 256,
                    256);
        } else {
            super.renderSlot(guiGraphics, slot);
        }

        // Render custom shaped slot overlay for runegem slots
        if (this.hoveredSlot != slot || !(slot instanceof RunegemSlot runegemSlot) || runegemSlot.getShape() == null) {
            return;
        }

        RunegemShape shape = runegemSlot.getShape();
        guiGraphics.blit(RenderType::guiTexturedOverlay, SLOTS, x, y, getSlotOffset(shape, false), 18, 18, 18, 256,
                256);
    }

    private int getSlotOffset(RunegemShape shape, boolean darkOffset) {
        int start;
        if (darkOffset) {
            start = 126;
        } else {
            start = 18;
        }
        return switch (shape) {
            case DIAMOND -> start;
            case TRIANGLE -> start + 18;
            case HEART -> start + 36;
            case CIRCLE -> start + 54;
            case SQUARE -> start + 72;
            case PENTAGON -> start + 90;
        };
    }
}
