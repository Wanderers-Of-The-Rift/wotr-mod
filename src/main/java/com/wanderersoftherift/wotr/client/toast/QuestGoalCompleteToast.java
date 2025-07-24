package com.wanderersoftherift.wotr.client.toast;

import com.wanderersoftherift.wotr.core.quest.Quest;
import com.wanderersoftherift.wotr.core.quest.QuestState;
import com.wanderersoftherift.wotr.gui.widget.quest.GoalStateWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Style;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A toast for when a subgoal of a quest is completed (but not the entire quest)
 */
public class QuestGoalCompleteToast extends SimpleToast {
    private static final int PADDING = 6;

    private final QuestState quest;
    private final GoalStateWidget goal;

    public QuestGoalCompleteToast(QuestState quest, int goalIndex) {
        super(true);
        this.quest = quest;
        this.goal = new GoalStateWidget(quest, goalIndex, Style.EMPTY.withColor(ChatFormatting.GRAY));
        goal.setX(PADDING);
        goal.setY(13);
        goal.setHeight(16);
    }

    @Override
    protected void renderMessage(@NotNull GuiGraphics guiGraphics, Font font, long visibilityTime) {
        List<FormattedCharSequence> list = font.split(Quest.title(quest.getOrigin()), width() - 2 * PADDING);
        guiGraphics.drawString(font, list.getFirst(), PADDING, 5, ChatFormatting.WHITE.getColor(), false);
        goal.setWidth(width() - 2 * PADDING);
        goal.render(guiGraphics, 0, 0, 0);
    }
}
