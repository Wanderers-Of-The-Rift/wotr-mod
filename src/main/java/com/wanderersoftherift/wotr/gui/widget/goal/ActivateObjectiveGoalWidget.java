package com.wanderersoftherift.wotr.gui.widget.goal;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.goal.type.ActivateObjectiveGoal;
import com.wanderersoftherift.wotr.util.ColorUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import org.jetbrains.annotations.NotNull;

/**
 * Display widget for the {@link ActivateObjectiveGoal}
 */
public class ActivateObjectiveGoalWidget extends AbstractWidget implements GoalDisplay {
    private static final String GOAL_MESSAGE = WanderersOfTheRift.translationId("container",
            "quest.goal.objective_block");
    private final Font font;
    private final ActivateObjectiveGoal goal;

    private Style textStyle = Style.EMPTY.withColor(ColorUtil.OFF_BLACK);
    private int progress;

    public ActivateObjectiveGoalWidget(ActivateObjectiveGoal goal) {
        super(0, 0, 100, 18, Component.empty());
        this.font = Minecraft.getInstance().font;
        this.goal = goal;
    }

    @Override
    public void setTextStyle(Style textStyle) {
        this.textStyle = textStyle;
    }

    @Override
    public int getHeight(int width) {
        return font.lineHeight;
    }

    @Override
    public void setProgress(int amount) {
        progress = amount;
    }

    @Override
    protected void renderWidget(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        MutableComponent label = Component.translatable(GOAL_MESSAGE, progress, goal.count());
        guiGraphics.drawString(font, label.withStyle(textStyle), getX(), getY() + 9 - font.lineHeight / 2,
                ColorUtil.OFF_BLACK, false);
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
