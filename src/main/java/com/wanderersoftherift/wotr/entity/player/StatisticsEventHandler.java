package com.wanderersoftherift.wotr.entity.player;

import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

import java.util.HashMap;
import java.util.Map;

/**
 * Event subscriptions relating to managing a player's primary stats. This includes applying their stats when they login
 * and applying updates when the stats change.
 */
@EventBusSubscriber
public final class StatisticsEventHandler {

    private static final Map<Holder<Attribute>, PrimaryStatistic> statLookup = new HashMap<>();

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
    public static void onPrimaryStatChanged(PlayerAttributeChangedEvent event) {
        PrimaryStatistic stat = getStat(event.getAttribute(), event.getEntity().level().registryAccess());
        if (stat != null) {
            int value = (int) event.getEntity().getAttributeValue(event.getAttribute());
            stat.apply(event.getEntity(), value);
        }
    }

    private static PrimaryStatistic getStat(Holder<Attribute> attribute, RegistryAccess registries) {
        if (statLookup.isEmpty()) {
            registries.lookup(WotrRegistries.Keys.PRIMARY_STATISTICS)
                    .get()
                    .stream()
                    .forEach(stat -> statLookup.put(stat.attribute(), stat));
        }
        return statLookup.get(attribute);
    }
}
