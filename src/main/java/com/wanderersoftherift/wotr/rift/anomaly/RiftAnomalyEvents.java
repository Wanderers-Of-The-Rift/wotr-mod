package com.wanderersoftherift.wotr.rift.anomaly;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.block.blockentity.AnomalyBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.living.LivingDeathEvent;

import java.util.Optional;

@EventBusSubscriber(modid = WanderersOfTheRift.MODID)
public class RiftAnomalyEvents {

    @SubscribeEvent
    public static void onMobDeath(LivingDeathEvent event) {
        // Tracks mob deaths for anomalies
        Entity entity = event.getEntity();
        if (!(entity.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        CompoundTag tag = entity.getPersistentData();
        if (tag.contains("AnomalyBlockPos")) {
            Optional<BlockPos> posOpt = NbtUtils.readBlockPos(tag, "AnomalyBlockPos");
            if (posOpt.isPresent()) {
                BlockPos pos = posOpt.get();
                BlockEntity be = serverLevel.getBlockEntity(pos);
                if (be instanceof AnomalyBlockEntity anomaly) {
                    anomaly.onMobKilled(entity.getUUID(), event.getSource().getEntity() instanceof Player p ? p : null);
                }
            }
        }
    }
}