package com.wanderersoftherift.wotr.gui.screen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.KeyForgeMenu;
import com.wanderersoftherift.wotr.init.ModDataMaps;
import com.wanderersoftherift.wotr.item.essence.EssenceValue;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.awt.Color;
import java.util.List;

/**
 * This screen renders the Key Forge Menu.
 * <p>
 * This includes an output tier level and contribution meter and tooltips showing the essence value and type of items.
 * </p>
 */
public class KeyForgeScreen extends AbstractContainerScreen<KeyForgeMenu> {
    private static final String ESSENCE_TOOLTIP = "tooltip." + WanderersOfTheRift.MODID + ".essence_value";
    private static final String ESSENCE_HEADER = "tooltip." + WanderersOfTheRift.MODID + ".essence_header";
    private static final ResourceLocation BACKGROUND = WanderersOfTheRift.id("textures/gui/container/key_forge/background.png");
    private static final int BACKGROUND_WIDTH = 256;
    private static final int BACKGROUND_HEIGHT = 256;
    private static final int BAR_WIDTH = 14;
    private static final int BAR_HEIGHT = 45;
    private static final int BAR_X = 89;
    private static final int BAR_Y = 34;
    private static final int TIER_X = 96;
    private static final int TIER_Y = 23;

    public KeyForgeScreen(KeyForgeMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 176;
        this.imageHeight = 196;
        this.inventoryLabelY = this.imageHeight - 94;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderTooltip(@NotNull GuiGraphics guiGraphics, int x, int y) {
        if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            ItemStack itemStack = this.hoveredSlot.getItem();
            if (this.menu.getCarried().isEmpty() || itemStack.getTooltipImage().map(ClientTooltipComponent::create).map(ClientTooltipComponent::showTooltipWithItemInHand).orElse(false)) {
                List<Component> tooltips = this.getTooltipFromContainerItem(itemStack);
                EssenceValue essenceValue = itemStack.getItemHolder().getData(ModDataMaps.ESSENCE_VALUE_DATA);
                if (essenceValue != null) {
                    if (essenceValue.values().size() > 1) {
                        tooltips.add(Component.translatable(ESSENCE_HEADER).withColor(Color.GRAY.getRGB()));
                        essenceValue.values().forEach((key, value) -> {
                            tooltips.add(Component.literal(value + " ").withColor(Color.GRAY.getRGB()).append(Component.translatable(EssenceValue.ESSENCE_TYPE_PREFIX + "." + key.getNamespace() + "." + key.getPath())).withColor(Color.GRAY.getRGB()));
                        });
                    } else {
                        essenceValue.values().forEach((key, value) -> {
                            Component essenceType = Component.translatable(EssenceValue.ESSENCE_TYPE_PREFIX + "." + key.getNamespace() + "." + key.getPath());
                            tooltips.add(Component.translatable(ESSENCE_TOOLTIP, value, essenceType).withColor(Color.GRAY.getRGB()));
                        });
                    }
                }
                guiGraphics.renderTooltip(
                        this.font,
                        tooltips,
                        itemStack.getTooltipImage(),
                        itemStack,
                        x,
                        y,
                        itemStack.get(DataComponents.TOOLTIP_STYLE)
                );
            }
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderType::guiTextured, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);

        int completion = menu.getTierPercent();
        int tier = completion / 100;
        int barHeight = BAR_HEIGHT * (completion % 100) / 100;

        int barOffset = (tier > 0) ? BAR_WIDTH : 0;
        guiGraphics.blit(RenderType::guiTextured, BACKGROUND, this.leftPos + BAR_X, this.topPos + BAR_Y + (BAR_HEIGHT - barHeight), barOffset, imageHeight + (BAR_HEIGHT - barHeight), BAR_WIDTH, barHeight, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);

        guiGraphics.drawCenteredString(font, Integer.toString(tier), this.leftPos + TIER_X, this.topPos + TIER_Y, Color.WHITE.getRGB());
    }

}
