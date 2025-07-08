package com.wanderersoftherift.wotr.gui.widget.quest;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.guild.quest.goal.KillMobGoal;
import com.wanderersoftherift.wotr.gui.widget.GoalDisplay;
import com.wanderersoftherift.wotr.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public class KillMobGoalWidget extends AbstractWidget implements GoalDisplay {
    private static final String GOAL_MESSAGE = WanderersOfTheRift.translationId("container", "quest.goal.kill");
    private final Font font;
    private final KillMobGoal goal;

    private int progress;

    public KillMobGoalWidget(KillMobGoal goal) {
        super(0, 0, 100, 18, Component.empty());
        this.font = Minecraft.getInstance().font;
        this.goal = goal;
    }

    @Override
    public int getHeight(int width) {
        return 18;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        Component name;
        if (goal.mob().types().size() == 1) {
            name = goal.mob().types().get(0).value().getDescription();
        } else {
            name = goal.mob()
                    .types()
                    .unwrapKey()
                    .<Component>map(
                            key -> Component.translatable(WanderersOfTheRift.translationId("mobgroup", key.location())))
                    .orElse(goal.mob().types().get(0).value().getDescription());
        }

        int remainder = goal.progressTarget() - progress;
        guiGraphics.drawString(font, Component.translatable(GOAL_MESSAGE, remainder, name), getX(),
                getY() + 9 - font.lineHeight / 2, ColorUtil.OFF_BLACK, false);
    }

    @Override
    protected void updateWidgetNarration(@NotNull NarrationElementOutput narrationElementOutput) {

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
