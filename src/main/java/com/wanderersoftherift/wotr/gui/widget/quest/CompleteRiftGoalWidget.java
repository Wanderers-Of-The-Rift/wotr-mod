package com.wanderersoftherift.wotr.gui.widget.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.goal.CompleteRiftGoal;
import com.wanderersoftherift.wotr.util.ColorUtil;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * Display widget for a {@link CompleteRiftGoal}
 */
public class CompleteRiftGoalWidget extends AbstractWidget implements GoalDisplay {

    private static final String SINGULAR_GOAL_MESSAGE = WanderersOfTheRift.translationId("container",
            "quest.goal.complete_rift");
    private static final String PLURAL_GOAL_MESSAGE = WanderersOfTheRift.translationId("container",
            "quest.goal.complete_rifts");

    private final Font font;
    private final CompleteRiftGoal goal;

    private Style textStyle = Style.EMPTY.withColor(ChatFormatting.DARK_GRAY);
    private int progress;

    public CompleteRiftGoalWidget(CompleteRiftGoal goal) {
        super(0, 0, 100, 18, Component.empty());
        this.goal = goal;
        this.font = Minecraft.getInstance().font;
        updateMessage();
    }

    @Override
    public void setProgress(int amount) {
        progress = amount;
        updateMessage();
    }

    @Override
    public void setTextStyle(Style style) {
        textStyle = style;
        updateMessage();
    }

    @Override
    public int getHeight(int width) {
        return font.lineHeight * font.split(getMessage(), width).size();
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        List<FormattedCharSequence> list = font.split(getMessage(), width);
        for (int i = 0; i < list.size(); i++) {
            guiGraphics.drawString(font, list.get(i), getX(), getY() + 9 - font.lineHeight / 2 + font.lineHeight * i,
                    ColorUtil.OFF_BLACK, false);
        }

    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {
        // TODO
    }

    private void updateMessage() {
        MutableComponent predicateText = goal.predicate().displayText();
        if (!predicateText.getString(1).isEmpty()) {
            predicateText.append(" ");
        }
        int target = goal.count();
        if (target == 1) {
            setMessage(
                    Component
                            .translatable(SINGULAR_GOAL_MESSAGE, goal.completionLevel().getDisplay(), predicateText,
                                    progress, goal.count())
                            .withStyle(textStyle));
        } else {
            setMessage(
                    Component
                            .translatable(PLURAL_GOAL_MESSAGE, goal.completionLevel().getDisplay(), predicateText,
                                    progress, goal.count())
                            .withStyle(textStyle));
        }
    }
}
