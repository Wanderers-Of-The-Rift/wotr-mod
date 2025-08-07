package com.wanderersoftherift.wotr.client;

import com.wanderersoftherift.wotr.client.toast.QuestCompleteToast;
import com.wanderersoftherift.wotr.client.toast.QuestGoalCompleteToast;
import com.wanderersoftherift.wotr.core.quest.QuestState;
import net.minecraft.client.Minecraft;

public class WotrClientHandlers {
    public static void handleQuestGoalUpdate(QuestState state, int goalIndex) {
        if (state.isComplete()) {
            Minecraft.getInstance().getToastManager().addToast(new QuestCompleteToast(state.getOrigin()));
        } else if (state.isGoalComplete(goalIndex)) {
            Minecraft.getInstance().getToastManager().addToast(new QuestGoalCompleteToast(state, goalIndex));
        }
    }
}