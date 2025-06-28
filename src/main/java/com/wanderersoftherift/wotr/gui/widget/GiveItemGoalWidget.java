package com.wanderersoftherift.wotr.gui.widget;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.goal.GiveItemGoal;
import com.wanderersoftherift.wotr.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;

public class GiveItemGoalWidget extends AbstractWidget implements GoalDisplay {
    private static final Component TEXT = Component
            .translatable(WanderersOfTheRift.translationId("container", "quest.goal.give"));
    private final Font font;
    private final GiveItemGoal goal;

    private int progress;

    public GiveItemGoalWidget(GiveItemGoal goal) {
        super(0, 0, 100, 18, TEXT);
        this.font = Minecraft.getInstance().font;
        this.goal = goal;
    }

    @Override
    public int getHeight(int width) {
        return 18;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.drawString(font, getMessage(), getX(), getY() + 9 - font.lineHeight / 2, ColorUtil.OFF_BLACK,
                false);
        int messageWidth = font.width(getMessage());
        ContextMap contextmap = SlotDisplayContext.fromLevel(Minecraft.getInstance().level);
        ItemStack displayItem = goal.item().display().resolveForFirstStack(contextmap);
        displayItem.setCount(Math.max(1, goal.progressTarget() - progress));
        guiGraphics.renderFakeItem(displayItem, getX() + messageWidth, getY());
        guiGraphics.renderItemDecorations(font, displayItem, getX() + messageWidth, getY());
        if (mouseX >= getX() + messageWidth && mouseX <= getX() + messageWidth + 16 && mouseY >= getY() + 1
                && mouseY <= getY() + 17) {
            guiGraphics.renderTooltip(font, displayItem, mouseX, mouseY);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }

    @Override
    protected boolean isValidClickButton(int button) {
        return false;
    }

    @Override
    public void setProgress(int amount) {
        progress = amount;
    }
}
