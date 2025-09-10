package com.wanderersoftherift.wotr.gui.widget.quest;

import com.wanderersoftherift.wotr.core.quest.reward.ItemReward;
import com.wanderersoftherift.wotr.util.ComponentUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Display for an {@link ItemReward}
 */
public class ItemRewardWidget extends AbstractWidget {
    private final ItemStack rewardItem;

    public ItemRewardWidget(ItemReward reward) {
        super(0, 0, 16, 16, Component.empty());
        this.rewardItem = reward.generateItem();
        setTooltip(Tooltip.create(
                ComponentUtil.joinWithNewLines(Screen.getTooltipFromItem(Minecraft.getInstance(), rewardItem))));
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.renderFakeItem(rewardItem, getX(), getY());
        guiGraphics.renderItemDecorations(Minecraft.getInstance().font, rewardItem, getX(), getY());
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        // TODO
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return false;
    }
}
