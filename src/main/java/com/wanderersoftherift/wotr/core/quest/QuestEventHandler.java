package com.wanderersoftherift.wotr.core.quest;

import com.wanderersoftherift.wotr.core.npc.NpcEvent;
import com.wanderersoftherift.wotr.core.npc.NpcIdentity;
import com.wanderersoftherift.wotr.core.rift.RiftEvent;
import com.wanderersoftherift.wotr.gui.menu.quest.QuestCompletionMenu;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.SimpleMenuProvider;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

import java.util.Optional;

/**
 * Reset available quests after each rift (success or failure)
 */
@EventBusSubscriber
public class QuestEventHandler {

    @SubscribeEvent
    public static void onDiedInRift(RiftEvent.PlayerDied event) {
        event.getPlayer().removeData(WotrAttachments.AVAILABLE_QUESTS);
    }

    @SubscribeEvent
    public static void onCompletedRift(RiftEvent.PlayerCompletedRift event) {
        event.getPlayer().removeData(WotrAttachments.AVAILABLE_QUESTS);
    }

    @SubscribeEvent
    public static void onQuestReceiverInteract(NpcEvent.OnInteract event) {
        if (event.getResult() != InteractionResult.PASS) {
            return;
        }
        ActiveQuests activeQuests = event.getPlayer().getData(WotrAttachments.ACTIVE_QUESTS);
        Optional<QuestState> questToHandIn = activeQuests.getQuestList()
                .stream()
                .filter(quest -> quest.getHandInTo().equals(event.getNpc()))
                .findFirst();
        if (questToHandIn.isPresent()) {
            event.getPlayer()
                    .openMenu(
                            new SimpleMenuProvider(
                                    (containerId, playerInventory, p) -> new QuestCompletionMenu(containerId,
                                            playerInventory, event.getAccess(), activeQuests,
                                            questToHandIn.get().getId()),
                                    NpcIdentity.getDisplayName(event.getNpc()))
                    );
            event.setResult(InteractionResult.CONSUME);
        }
    }
}
