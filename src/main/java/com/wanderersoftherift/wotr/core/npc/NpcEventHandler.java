package com.wanderersoftherift.wotr.core.npc;

import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionResult;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;

@EventBusSubscriber
public class NpcEventHandler {

    @SubscribeEvent(priority = EventPriority.LOW)
    public static void onNpcInteract(NpcEvent.OnInteract event) {
        if (event.getResult() != InteractionResult.PASS) {
            return;
        }
        if (event.getMob() != null && !event.getMob().hasData(WotrAttachments.NPC_INTERACT)) {
            event.setResult(event.getNpc()
                    .value()
                    .npcInteraction()
                    .interactAsMob(event.getMob(), event.getPlayer(), event.getInteractionHand()));
        } else if (event.getBlockPos() != null) {
            event.setResult(event.getNpc()
                    .value()
                    .npcInteraction()
                    .interactAsBlock(event.getNpc(), (ServerLevel) event.getLevel(), event.getBlockPos(),
                            event.getLevel().getBlockState(event.getBlockPos()).getBlock(), event.getPlayer()));
        }
    }
}
