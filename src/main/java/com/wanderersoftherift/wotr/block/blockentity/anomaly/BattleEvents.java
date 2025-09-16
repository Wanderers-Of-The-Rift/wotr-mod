package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.wanderersoftherift.wotr.block.blockentity.AnomalyBlockEntity;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

@EventBusSubscriber
public class BattleEvents {

    @SubscribeEvent
    private static void onDeath(LivingDeathEvent event) {
        event.getEntity().getExistingData(WotrAttachments.BATTLE_TASK_MOB).ifPresent(battleMobAttachment -> {
            if (event.getEntity().level() instanceof ServerLevel serverLevel) {
                if (serverLevel.getBlockEntity(
                        battleMobAttachment.blockPos()) instanceof AnomalyBlockEntity anomalyBlockEntity) {
                    anomalyBlockEntity.battleMobDeath(event.getEntity());
                }
            }
        });
    }
}
