package com.wanderersoftherift.wotr.gui.screen.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.ActiveQuest;
import com.wanderersoftherift.wotr.gui.widget.GoalDisplay;
import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import com.wanderersoftherift.wotr.gui.widget.lookup.GoalDisplays;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class GoalStateWidget extends AbstractWidget implements ScrollContainerEntry {
    private static final ResourceLocation INCOMPLETE_BOX = WanderersOfTheRift
            .id("textures/gui/container/quest/incomplete.png");
    private static final ResourceLocation COMPLETE_BOX = WanderersOfTheRift
            .id("textures/gui/container/quest/complete.png");
    private static final int STATE_BOX_SIZE = 8;
    private static final int STATE_BOX_VERT_OFFSET = 4;
    private static final int STATE_BOX_HORIZ_OFFSET = 2;
    private final ActiveQuest quest;
    private final int goalIndex;
    private final GoalDisplay goalWidget;

    public GoalStateWidget(ActiveQuest quest, int goalIndex) {
        super(0, 0, 0, 0, Component.empty());
        this.quest = quest;
        this.goalIndex = goalIndex;

        this.goalWidget = GoalDisplays.createFor(quest.getGoal(goalIndex)).orElse(null);
    }

    @Override
    public int getHeight(int width) {
        if (goalWidget != null) {
            return Math.max(STATE_BOX_SIZE + STATE_BOX_VERT_OFFSET,
                    goalWidget.getHeight(width - STATE_BOX_SIZE - STATE_BOX_HORIZ_OFFSET));
        }
        return STATE_BOX_SIZE + STATE_BOX_VERT_OFFSET;
    }

    @Override
    protected void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        int x = getX();
        int y = getY();
        ResourceLocation texture;
        if (quest.isGoalComplete(goalIndex)) {
            texture = COMPLETE_BOX;
        } else {
            texture = INCOMPLETE_BOX;
        }
        guiGraphics.blit(RenderType::guiTextured, texture, x, y + STATE_BOX_VERT_OFFSET, 0, 0, STATE_BOX_SIZE,
                STATE_BOX_SIZE, STATE_BOX_SIZE, STATE_BOX_SIZE);
        x += STATE_BOX_SIZE + STATE_BOX_HORIZ_OFFSET;

        if (goalWidget != null) {
            goalWidget.setProgress(quest.getGoalProgress(goalIndex));
            goalWidget.setWidth(getWidth() - STATE_BOX_SIZE - STATE_BOX_HORIZ_OFFSET);
            goalWidget.setHeight(getHeight());
            goalWidget.setX(x);
            goalWidget.setY(y);
            goalWidget.render(guiGraphics, mouseX, mouseY, partialTick);
        }
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {

    }
}
