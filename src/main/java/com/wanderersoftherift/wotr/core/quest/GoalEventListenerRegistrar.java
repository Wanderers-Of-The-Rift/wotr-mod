package com.wanderersoftherift.wotr.core.quest;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.WeakHashMap;

/**
 * GoalEventListenerRegistrar is used to link quest goals against behavior when a particular event occurs against the
 * player with those goals.
 * <p>
 * The GoalEventListenerRegistrar handles cleanup of the event subscriptions when either the player or the quest state
 * is no longer relevant.
 * </p>
 *
 * @param <T> The type of the event being handled
 */
public final class GoalEventListenerRegistrar<T extends Event> {
    private final Multimap<ServerPlayer, Subscription> subscriptions = Multimaps.newListMultimap(new WeakHashMap<>(),
            ArrayList::new);
    private final Handler<T> handler;

    /**
     * @param handler
     */
    public GoalEventListenerRegistrar(Handler<T> handler) {
        this.handler = handler;
    }

    /**
     * Subscribes a goal to receive event updates
     * 
     * @param player The player with the goal
     * @param state  The quest state with the goal
     * @param index  The index of the goal in the quest state
     */
    public void register(ServerPlayer player, QuestState state, int index) {
        subscriptions.put(player, new Subscription(new WeakReference<>(state), index));
    }

    /**
     * Triggers the processing of the event for any goals the player has that are listening
     *
     * @param player
     * @param event
     */
    public void trigger(ServerPlayer player, T event) {
        var iterator = subscriptions.get(player).iterator();
        while (iterator.hasNext()) {
            Subscription subscription = iterator.next();
            QuestState state = subscription.state.get();
            if (state == null) {
                iterator.remove();
            } else {
                handler.onEvent(event, player, state, subscription.index);
            }
        }
    }

    /**
     * Unregisters all subscribers for a player
     *
     * @param player
     */
    public void unregister(ServerPlayer player) {
        subscriptions.removeAll(player);
    }

    private record Subscription(WeakReference<QuestState> state, int index) {
    }

    public interface Handler<T extends Event> {
        /**
         * @param event  The event that triggered the handler
         * @param player The player associated with the player and the quest state
         * @param state  The quest state with the subscribed goal
         * @param index  The index of the subscribed goal
         */
        void onEvent(T event, ServerPlayer player, QuestState state, int index);
    }
}
