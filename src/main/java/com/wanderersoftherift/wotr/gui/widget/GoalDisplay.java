package com.wanderersoftherift.wotr.gui.widget;

import net.minecraft.network.chat.Style;

public interface GoalDisplay extends ScrollContainerEntry {

    void setProgress(int amount);

    void setTextStyle(Style style);

}
