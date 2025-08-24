package com.wanderersoftherift.wotr.entity.player;

import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrDataMaps;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

/**
 * Event subscriptions relating to managing a player's primary stats. This includes applying their stats when they login
 * and applying updates when the stats change.
 */
@EventBusSubscriber
public final class StatisticsEventHandler {

    @SubscribeEvent
    public static void onPlayerSpawnEvent(PlayerEvent.PlayerRespawnEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getData(WotrAttachments.PRIMARY_STATISTICS).applyStatistics();
        }
    }

    @SubscribeEvent
    public static void onPlayerSpawnEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getData(WotrAttachments.PRIMARY_STATISTICS).applyStatistics();
        }
    }

    @SubscribeEvent
    public static void onPlayerSpawnEvent(PlayerEvent.PlayerChangedDimensionEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.getData(WotrAttachments.PRIMARY_STATISTICS).applyStatistics();
        }
    }

    @SubscribeEvent
    public static void applySecondaryAttributeChanges(PlayerAttributeChangedEvent event) {
        SecondaryAttributes data = event.getAttribute().getData(WotrDataMaps.SECONDARY_ATTRIBUTES);
        if (data != null) {
            int value = (int) event.getEntity().getAttributeValue(event.getAttribute());
            data.apply(event.getEntity(), value);
        }
    }

}
