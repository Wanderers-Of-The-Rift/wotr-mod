package com.wanderersoftherift.wotr.gui.widget.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.quest.goal.GiveItemGoal;
import com.wanderersoftherift.wotr.util.ColorUtil;
import com.wanderersoftherift.wotr.util.ComponentUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Tooltip;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.util.context.ContextMap;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.display.SlotDisplayContext;
import org.jetbrains.annotations.NotNull;

/**
 * Display widget for the {@link GiveItemGoal}
 */
public class GiveItemGoalWidget extends AbstractWidget implements GoalDisplay {
    private static final String TEXT_ID = WanderersOfTheRift.translationId("container", "quest.goal.give");
    private static final int ICON_SIZE = 16;
    private final Font font;
    private final GiveItemGoal goal;

    private int progress;
    private Style textStyle = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY);

    public GiveItemGoalWidget(GiveItemGoal goal) {
        super(0, 0, 100, ICON_SIZE, Component.empty());
        this.font = Minecraft.getInstance().font;
        this.goal = goal;
        updateMessage();
    }

    @Override
    public void setTextStyle(Style textStyle) {
        this.textStyle = textStyle;
        updateMessage();
    }

    @Override
    public int getHeight(int width) {
        return ICON_SIZE;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        guiGraphics.drawString(font, getMessage(), getX(), getY() + 9 - font.lineHeight / 2, ColorUtil.OFF_BLACK,
                false);
        int messageWidth = font.width(getMessage());
        ContextMap contextmap = SlotDisplayContext.fromLevel(Minecraft.getInstance().level);
        ItemStack displayItem = goal.item().display().resolveForFirstStack(contextmap);
        guiGraphics.renderFakeItem(displayItem, getX() + messageWidth, getY());

        if (mouseX >= getX() + messageWidth && mouseX <= getX() + messageWidth + ICON_SIZE && mouseY >= getY() + 1
                && mouseY <= getY() + ICON_SIZE + 1) {
            setTooltip(Tooltip.create(
                    ComponentUtil.joinWithNewLines(Screen.getTooltipFromItem(Minecraft.getInstance(), displayItem))));
        } else {
            setTooltip(null);
        }
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        // TODO
    }

    @Override
    protected boolean isValidClickButton(int button) {
        return false;
    }

    @Override
    public void setProgress(int amount) {
        progress = amount;
        updateMessage();
    }

    private void updateMessage() {
        setMessage(Component.translatable(TEXT_ID, progress, goal.count()).withStyle(textStyle));
    }
}
