package com.wanderersoftherift.wotr.client.toast;

import com.wanderersoftherift.wotr.core.quest.Quest;
import com.wanderersoftherift.wotr.core.quest.QuestState;
import com.wanderersoftherift.wotr.gui.widget.quest.GoalStateWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import org.jetbrains.annotations.NotNull;

import java.util.List;

/**
 * A toast for when a subgoal of a quest is completed (but not the entire quest)
 */
public class GoalToast implements Toast {
    private static final ResourceLocation BACKGROUND_SPRITE = ResourceLocation
            .withDefaultNamespace("toast/advancement");
    private static final int TOAST_WIDTH = 160;
    private static final int PADDING = 6;
    private static final int DISPLAY_TIME = 5000;

    private final QuestState quest;
    private final GoalStateWidget goal;
    private Visibility wantedVisibility = Visibility.HIDE;

    public GoalToast(QuestState quest, int goalIndex) {
        this.quest = quest;
        this.goal = new GoalStateWidget(quest, goalIndex, Style.EMPTY.withColor(ChatFormatting.GRAY));
        goal.setX(PADDING);
        goal.setY(13);
        goal.setWidth(TOAST_WIDTH - 2 * PADDING);
        goal.setHeight(16);
    }

    @Override
    public @NotNull Visibility getWantedVisibility() {
        return wantedVisibility;
    }

    @Override
    public void update(@NotNull ToastManager toastManager, long visibilityTime) {
        if (visibilityTime >= DISPLAY_TIME * toastManager.getNotificationDisplayTimeMultiplier()) {
            wantedVisibility = Visibility.HIDE;
        } else {
            wantedVisibility = Visibility.SHOW;
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, Font font, long visibilityTime) {
        guiGraphics.blitSprite(RenderType::guiTextured, BACKGROUND_SPRITE, 0, 0, this.width(), this.height());
        List<FormattedCharSequence> list = font.split(Quest.title(quest.getOrigin()), TOAST_WIDTH - 2 * PADDING);
        guiGraphics.drawString(font, list.getFirst(), PADDING, 5, ChatFormatting.WHITE.getColor(), false);
        goal.render(guiGraphics, 0, 0, 0);
    }
}
