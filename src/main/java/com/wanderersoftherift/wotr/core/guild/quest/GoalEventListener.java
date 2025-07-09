package com.wanderersoftherift.wotr.core.guild.quest;

import net.neoforged.bus.api.Event;

import java.lang.ref.WeakReference;

public final class GoalEventListener<T extends Event> implements QuestEventHandler.Listener<T> {
    private final Handler<T> handler;
    private final WeakReference<QuestState> questState;
    private final int goalIndex;

    public GoalEventListener(QuestState state, int goalIndex, Handler<T> handler) {
        this.handler = handler;
        this.questState = new WeakReference<>(state);
        this.goalIndex = goalIndex;
    }

    @Override
    public void onEvent(T event) {
        QuestState state = questState.get();
        if (state != null) {
            handler.onEvent(event, state, goalIndex);
        }
    }

    @Override
    public boolean isRelevant() {
        return questState.get() != null;
    }

    public interface Handler<T extends Event> {
        void onEvent(T event, QuestState state, int goalIndex);
    }
}
