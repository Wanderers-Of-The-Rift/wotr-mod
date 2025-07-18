package com.wanderersoftherift.wotr.core.guild.quest;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.WeakHashMap;

public final class GoalEventListenerRegistrar<T extends Event> {
    private final Multimap<ServerPlayer, Subscription<T>> subscriptions = Multimaps.newListMultimap(new WeakHashMap<>(),
            ArrayList::new);
    private final Handler<T> handler;

    public GoalEventListenerRegistrar(Handler<T> handler) {
        this.handler = handler;
    }

    public void register(ServerPlayer player, QuestState state, int index) {
        subscriptions.put(player, new Subscription<>(new WeakReference<>(state), index));
    }

    public void trigger(ServerPlayer player, T event) {
        var iterator = subscriptions.get(player).iterator();
        while (iterator.hasNext()) {
            Subscription<T> subscription = iterator.next();
            QuestState state = subscription.state.get();
            if (state == null) {
                iterator.remove();
            } else {
                handler.onEvent(event, player, state, subscription.index);
            }
        }
    }

    public void unregister(ServerPlayer player) {
        subscriptions.removeAll(player);
    }

    private record Subscription<T extends Event>(WeakReference<QuestState> state, int index) {
    }

    public interface Handler<T extends Event> {
        void onEvent(T event, ServerPlayer player, QuestState state, int index);
    }
}
