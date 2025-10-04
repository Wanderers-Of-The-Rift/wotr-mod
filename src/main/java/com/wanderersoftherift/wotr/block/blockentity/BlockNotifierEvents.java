package com.wanderersoftherift.wotr.block.blockentity;

import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber
public class BlockNotifierEvents {

    @SubscribeEvent
    private static void onDeath(LivingDeathEvent event) {
        if (!(event.getEntity().level() instanceof ServerLevel serverLevel)) {
            return;
        }
        event.getEntity().getExistingData(WotrAttachments.DEATH_NOTIFICATION).ifPresent(battleMobAttachment -> {
            if (serverLevel.getBlockEntity(battleMobAttachment.blockPos()) instanceof MobDeathNotifiable notifiable) {
                notifiable.notifyOfDeath(event.getEntity());
            }
        });
    }
}
