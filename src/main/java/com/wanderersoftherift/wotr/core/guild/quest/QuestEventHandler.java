package com.wanderersoftherift.wotr.core.guild.quest;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.wanderersoftherift.wotr.core.rift.RiftEvent;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.WeakHashMap;

@EventBusSubscriber
public class QuestEventHandler {

    private static final Multimap<ServerPlayer, Listener<LivingDeathEvent>> killListeners = Multimaps
            .newListMultimap(new WeakHashMap<>(), ArrayList::new);
    private static final Multimap<ServerPlayer, Listener<RiftEvent.PlayerCompletedRift>> riftCompletionListeners = Multimaps
            .newListMultimap(new WeakHashMap<>(), ArrayList::new);
    private static final Multimap<ServerPlayer, Listener<RiftEvent.PlayerDied>> diedInRiftListeners = Multimaps
            .newListMultimap(new WeakHashMap<>(), ArrayList::new);

    public static void registerPlayerKillListener(ServerPlayer player, Listener<LivingDeathEvent> listener) {
        killListeners.put(player, listener);
    }

    public static void registerRiftCompletionListener(
            ServerPlayer player,
            Listener<RiftEvent.PlayerCompletedRift> listener) {
        riftCompletionListeners.put(player, listener);
    }

    public static void registerDiedInRiftListener(ServerPlayer player, Listener<RiftEvent.PlayerDied> listener) {
        diedInRiftListeners.put(player, listener);
    }

    @SubscribeEvent
    public static void onDiedInRift(RiftEvent.PlayerDied event) {
        Iterator<Listener<RiftEvent.PlayerDied>> iterator = diedInRiftListeners.get(event.getPlayer()).iterator();
        while (iterator.hasNext()) {
            Listener<RiftEvent.PlayerDied> listener = iterator.next();
            if (listener.isRelevant()) {
                listener.onEvent(event);
            } else {
                iterator.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onCompletedRift(RiftEvent.PlayerCompletedRift event) {
        Iterator<Listener<RiftEvent.PlayerCompletedRift>> iterator = riftCompletionListeners.get(event.getPlayer())
                .iterator();
        while (iterator.hasNext()) {
            Listener<RiftEvent.PlayerCompletedRift> listener = iterator.next();
            if (listener.isRelevant()) {
                listener.onEvent(event);
            } else {
                iterator.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onDeath(LivingDeathEvent event) {
        if (!(event.getSource().getEntity() instanceof ServerPlayer player)) {
            return;
        }

        Iterator<Listener<LivingDeathEvent>> iterator = killListeners.get(player).iterator();
        while (iterator.hasNext()) {
            Listener<LivingDeathEvent> listener = iterator.next();
            if (listener.isRelevant()) {
                listener.onEvent(event);
            } else {
                iterator.remove();
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerLeftEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) {
            return;
        }
        killListeners.removeAll(player);
        diedInRiftListeners.removeAll(player);
        riftCompletionListeners.removeAll(player);
    }

    public interface Listener<T extends Event> {

        /**
         * Called when the listener to event occurs
         * 
         * @param event
         */
        void onEvent(T event);

        /**
         * @return Whether this listener is still relevant
         */
        boolean isRelevant();

    }

}
