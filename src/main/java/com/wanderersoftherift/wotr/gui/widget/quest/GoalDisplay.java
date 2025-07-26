package com.wanderersoftherift.wotr.gui.widget.quest;

import com.wanderersoftherift.wotr.gui.widget.ScrollContainerEntry;
import net.minecraft.network.chat.Style;

/**
 * Interface for displaying a goal
 */
public interface GoalDisplay extends ScrollContainerEntry {

    /**
     * @param amount The current progress value for the goal
     */
    void setProgress(int amount);

    /**
     * @param style The base style for rendering any text
     */
    void setTextStyle(Style style);

}
