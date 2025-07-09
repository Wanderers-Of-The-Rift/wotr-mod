package com.wanderersoftherift.wotr.core.guild.quest;

import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
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

    public static void registerPlayerKillListener(ServerPlayer player, Listener<LivingDeathEvent> listener) {
        killListeners.put(player, listener);
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
