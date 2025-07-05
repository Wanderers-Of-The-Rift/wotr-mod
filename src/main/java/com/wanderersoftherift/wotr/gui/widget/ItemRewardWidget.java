package com.wanderersoftherift.wotr.gui.widget;

import com.wanderersoftherift.wotr.core.guild.quest.reward.ItemReward;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public class ItemRewardWidget extends AbstractWidget {
    private final ItemStack rewardItem;

    public ItemRewardWidget(ItemReward reward) {
        super(0, 0, 16, 16, Component.empty());
        this.rewardItem = reward.generateItem();
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.renderFakeItem(rewardItem, getX(), getY());
        guiGraphics.renderItemDecorations(Minecraft.getInstance().font, rewardItem, getX(), getY());
        if (mouseX >= getX() && mouseX <= getX() + getWidth() && mouseY >= getY() && mouseY <= getY() + getWidth()) {
            guiGraphics.renderTooltip(Minecraft.getInstance().font, rewardItem, mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    protected boolean isValidClickButton(int button) {
        return false;
    }
}
