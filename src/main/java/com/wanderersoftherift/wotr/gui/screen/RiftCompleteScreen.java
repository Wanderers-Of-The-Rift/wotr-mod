package com.wanderersoftherift.wotr.gui.screen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.gui.menu.RiftCompleteMenu;
import com.wanderersoftherift.wotr.util.ColorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.stats.Stat;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.player.Inventory;
import org.jetbrains.annotations.NotNull;

public class RiftCompleteScreen extends AbstractContainerScreen<RiftCompleteMenu> {
    private static final ResourceLocation BACKGROUND = WanderersOfTheRift
            .id("textures/gui/container/rift_complete/background.png");
    private static final Component REWARDS_LABEL = Component.literal("Rewards");
    private static final int BACKGROUND_WIDTH = 296;
    private static final int BACKGROUND_HEIGHT = 236;

    private static final int REWARD_LABEL_X = 13;
    private static final int REWARD_LABEL_Y = 112;

    private static final int STAT_BLOCK_X = 13;
    private static final int STAT_BLOCK_Y = 20;

    public RiftCompleteScreen(RiftCompleteMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title);
        this.imageWidth = 296;
        this.imageHeight = 236;
        this.inventoryLabelX = 67;
        this.inventoryLabelY = this.imageHeight - 93;
    }

    @Override
    public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        renderStats(guiGraphics, mouseX, mouseY, partialTick);
        renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        guiGraphics.blit(RenderType::guiTextured, BACKGROUND, this.leftPos, this.topPos, 0, 0, this.imageWidth,
                this.imageHeight, BACKGROUND_WIDTH, BACKGROUND_HEIGHT);
        guiGraphics.drawString(font, REWARDS_LABEL, REWARD_LABEL_X + leftPos, REWARD_LABEL_Y + topPos,
                ColorUtil.OFF_BLACK, false);
    }

    private void renderStats(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Component status = switch (getMenu().getResult()) {
            case RiftCompleteMenu.FLAG_SUCCESS ->
                Component.translatable(WanderersOfTheRift.translationId("stat", "result.success"))
                        .withStyle(ChatFormatting.DARK_GREEN);
            case RiftCompleteMenu.FLAG_FAILED ->
                Component.translatable(WanderersOfTheRift.translationId("stat", "result.failed"))
                        .withStyle(ChatFormatting.DARK_RED);
            case RiftCompleteMenu.FLAG_SURVIVED ->
                Component.translatable(WanderersOfTheRift.translationId("stat", "result.survived"))
                        .withStyle(ChatFormatting.BLACK);
            default -> Component.empty();
        };
        guiGraphics.drawString(font,
                Component.translatable(WanderersOfTheRift.translationId("stat", "result")).append(status),
                STAT_BLOCK_X + leftPos, STAT_BLOCK_Y + topPos, ColorUtil.OFF_BLACK, false);

        for (int statIdx = 0; statIdx < RiftCompleteMenu.STATS_SLOTS.size(); statIdx++) {
            ResourceLocation statId = RiftCompleteMenu.STATS_SLOTS.get(statIdx);
            Stat<ResourceLocation> stat = Stats.CUSTOM.get(statId);

            Component label = Component.translatable(getTranslationKey(stat)).append(": ");
            guiGraphics.drawString(font, label, STAT_BLOCK_X + leftPos,
                    STAT_BLOCK_Y + (statIdx + 1) * font.lineHeight + topPos, ColorUtil.OFF_BLACK, false);
            guiGraphics.drawString(font, Component.literal(stat.format(menu.getStat(statIdx))),
                    STAT_BLOCK_X + leftPos + font.width(label), STAT_BLOCK_Y + (statIdx + 1) * font.lineHeight + topPos,
                    ChatFormatting.BLACK.getColor(), false);
        }
    }

    static String getTranslationKey(Stat<ResourceLocation> stat) {
        return "stat." + stat.getValue().toString().replace(':', '.');
    }
}
