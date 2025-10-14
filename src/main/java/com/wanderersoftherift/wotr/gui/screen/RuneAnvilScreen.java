package com.wanderersoftherift.wotr.gui.screen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.RuneAnvilMenu;
import com.wanderersoftherift.wotr.gui.menu.slot.RunegemSlot;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import com.wanderersoftherift.wotr.item.runegem.RunegemShape;
import com.wanderersoftherift.wotr.item.runegem.RunegemTier;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.gui.screens.inventory.tooltip.TooltipRenderUtil;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.ClientHooks;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector2i;

import java.util.List;

public class RuneAnvilScreen extends AbstractContainerScreen<RuneAnvilMenu> {
    private static final ResourceLocation BACKGROUND = WanderersOfTheRift
            .id("textures/gui/container/rune_anvil/background.png");
    private static final ResourceLocation SLOTS = WanderersOfTheRift.id("textures/gui/container/rune_anvil/slots.png");

    private static final int TEXTURE_WIDTH = 256;
    private static final int TEXTURE_HEIGHT = 256;
    private static final int TOOLTIP_BORDER = 8;

    // If the screen width is too small, remove a preview window
    private boolean fullDisplay = false;

    // TODO: Make sure blit is passed correct texture size for all calls.
    public RuneAnvilScreen(RuneAnvilMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageHeight = 248;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    protected void init() {
        super.init();

        fullDisplay = minecraft.screen.width > imageWidth * 3;
        if (!fullDisplay) {
            leftPos = 16;
        }

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

        ItemStack itemstack = this.menu.getGearSlotItem();
        if (!itemstack.isEmpty()) {
            List<ClientTooltipComponent> tooltipLines = ClientHooks.gatherTooltipComponents(itemstack,
                    getTooltipFromContainerItem(itemstack), itemstack.getTooltipImage(), 0, 280, height, font);
            Vector2i size = getTooltipsSize(tooltipLines);

            if (fullDisplay) {
                renderItemPane(guiGraphics, tooltipLines, this.leftPos - size.x - TOOLTIP_BORDER,
                        topPos + 2 * TOOLTIP_BORDER, size, partialTick);
            }
            renderItemPane(guiGraphics, tooltipLines, this.leftPos + imageWidth + TOOLTIP_BORDER,
                    topPos + 2 * TOOLTIP_BORDER, size, partialTick);
        }
    }

    private Vector2i getTooltipsSize(List<ClientTooltipComponent> tooltips) {
        Vector2i result = new Vector2i();
        for (var line : tooltips) {
            result.set(Math.max(line.getWidth(font), result.x), result.y + line.getHeight(font));
        }
        return result;
    }

    protected void renderItemPane(
            @NotNull GuiGraphics guiGraphics,
            List<ClientTooltipComponent> tooltips,
            int posX,
            int posY,
            Vector2i size,
            float partialTick) {
        if (tooltips.isEmpty()) {
            return;
        }
        int renderWidth = size.x;
        int renderHeight = size.y;
        if (tooltips.size() == 1) {
            renderHeight -= 2;
        }

        guiGraphics.pose().pushPose();
        TooltipRenderUtil.renderTooltipBackground(guiGraphics, posX, posY, renderWidth, renderHeight, 400, null);
        guiGraphics.pose().translate(0.0F, 0.0F, 400.0F);

        int currentY = posY;
        for (int index = 0; index < tooltips.size(); index++) {
            ClientTooltipComponent tooltip = tooltips.get(index);
            final int drawY = currentY;
            guiGraphics.drawSpecial(buffer -> {
                tooltip.renderText(font, posX, drawY, guiGraphics.pose().last().pose(),
                        (MultiBufferSource.BufferSource) buffer);
            });
            currentY += tooltip.getHeight(font) + (index == 0 ? 2 : 0);
        }

        currentY = posY;
        for (int index = 0; index < tooltips.size(); index++) {
            ClientTooltipComponent tooltip = tooltips.get(index);
            tooltip.renderImage(font, posX, currentY, renderWidth, renderHeight, guiGraphics);
            currentY += tooltip.getHeight(font) + (index == 0 ? 2 : 0);
        }

        guiGraphics.pose().popPose();
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderType::guiTextured, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth,
                this.imageHeight, TEXTURE_WIDTH, TEXTURE_HEIGHT);
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

            guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, getSlotOffset(shape, false), 0, 18, 18,
                    TEXTURE_WIDTH, TEXTURE_HEIGHT);
        } else {
            guiGraphics.blit(RenderType::guiTextured, SLOTS, x, y, 0, 0, 18, 18, TEXTURE_WIDTH, TEXTURE_HEIGHT);
        }

        if (!slot.hasItem() && slot instanceof RunegemSlot runegemSlot && runegemSlot.getLockedSocket() != null
                && runegemSlot.getShape() != null) {
            ItemStack stack = new ItemStack(WotrItems.RUNEGEM.get());
            RunegemShape shape = runegemSlot.getShape();
            stack.set(WotrDataComponentType.RUNEGEM_DATA,
                    new RunegemData(Component.empty(), shape, List.of(), RunegemTier.RAW));

            super.renderSlotContents(guiGraphics, stack, slot, null);
            guiGraphics.blit(RenderType::guiTexturedOverlay, SLOTS, x, y, getSlotOffset(shape, true), 18, 18, 18,
                    TEXTURE_WIDTH, TEXTURE_HEIGHT);
        } else {
            super.renderSlot(guiGraphics, slot);
        }

        // Render custom shaped slot overlay for runegem slots
        if (this.hoveredSlot != slot || !(slot instanceof RunegemSlot runegemSlot) || runegemSlot.getShape() == null) {
            return;
        }

        RunegemShape shape = runegemSlot.getShape();
        guiGraphics.blit(RenderType::guiTexturedOverlay, SLOTS, x, y, getSlotOffset(shape, false), 18, 18, 18,
                TEXTURE_WIDTH, TEXTURE_HEIGHT);
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
