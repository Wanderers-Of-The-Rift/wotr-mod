package com.wanderersoftherift.wotr.core.quest;

import com.wanderersoftherift.wotr.core.rift.RiftEvent;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.ArrayList;

/**
 * Reset available quests after each rift (success or failure)
 */
@EventBusSubscriber
public class QuestEventHandler {

    @SubscribeEvent
    public static void onDiedInRift(RiftEvent.PlayerDied event) {
        event.getPlayer().setData(WotrAttachments.AVAILABLE_QUESTS, new ArrayList<>());
    }

    @SubscribeEvent
    public static void onCompletedRift(RiftEvent.PlayerCompletedRift event) {
        event.getPlayer().setData(WotrAttachments.AVAILABLE_QUESTS, new ArrayList<>());
    }
}
