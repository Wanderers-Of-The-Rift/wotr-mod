package com.wanderersoftherift.wotr.gui.layer.objective;

import com.wanderersoftherift.wotr.core.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.core.rift.objective.ongoing.GoalBasedOngoingObjective;
import com.wanderersoftherift.wotr.gui.config.HudElementConfig;
import com.wanderersoftherift.wotr.gui.widget.quest.GoalStateWidget;
import net.minecraft.ChatFormatting;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Style;
import org.joml.Vector2i;

import java.util.List;

/**
 * Renderer for the goal based objective. Just lists the goals and their progress
 */
public class GoalBasedObjectiveStatusRenderer extends ObjectiveRenderer {

    private static final int WIDTH = 120;

    private final List<GoalStateWidget> goalWidgets;

    public GoalBasedObjectiveStatusRenderer(GoalBasedOngoingObjective objective) {
        this.goalWidgets = objective.getGoalStates()
                .stream()
                .map(state -> new GoalStateWidget(state,
                        Style.EMPTY.withColor(ChatFormatting.WHITE).withShadowColor(ChatFormatting.BLACK.getColor())))
                .toList();
    }

    public static GoalBasedObjectiveStatusRenderer create(OngoingObjective objective) {
        if (objective instanceof GoalBasedOngoingObjective goalBasedObjective) {
            return new GoalBasedObjectiveStatusRenderer(goalBasedObjective);
        }
        return null;
    }

    @Override
    public void render(GuiGraphics guiGraphics, HudElementConfig config, DeltaTracker deltaTracker) {
        Font font = Minecraft.getInstance().font;
        Vector2i pos = config.getPosition(WIDTH, font.lineHeight, guiGraphics.guiWidth(), guiGraphics.guiHeight());
        for (GoalStateWidget widget : goalWidgets) {
            widget.setPosition(pos.x, pos.y);
            widget.setWidth(WIDTH);
            int height = widget.getHeight(WIDTH);
            widget.setHeight(height);
            widget.render(guiGraphics, 0, 0, deltaTracker.getGameTimeDeltaPartialTick(true));
            pos.add(0, height);
        }
    }
}
