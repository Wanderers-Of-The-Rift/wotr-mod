package com.wanderersoftherift.wotr.core.npc.trading;

import com.wanderersoftherift.wotr.core.rift.RiftEvent;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

/**
 * Reset available quests after each rift (success or failure)
 */
@EventBusSubscriber
public class TradeEventHandler {

    @SubscribeEvent
    public static void onDiedInRift(RiftEvent.PlayerDied event) {
        event.getPlayer().removeData(WotrAttachments.AVAILABLE_TRADES);
    }

    @SubscribeEvent
    public static void onCompletedRift(RiftEvent.PlayerCompletedRift event) {
        event.getPlayer().removeData(WotrAttachments.AVAILABLE_TRADES);
    }
}
