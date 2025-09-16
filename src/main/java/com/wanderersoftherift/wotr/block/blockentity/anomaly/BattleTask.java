package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.block.blockentity.AnomalyBlockEntity;
import com.wanderersoftherift.wotr.init.WotrEntities;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public record BattleTask() implements AnomalyTask<BattleTaskState> {
    public static final AnomalyTaskType<BattleTaskState> TYPE = new AnomalyTaskType<>(
            MapCodec.unit(new BattleTask()), BattleTaskState.CODEC
    );

    public InteractionResult interact(
            Player player,
            InteractionHand hand,
            AnomalyBlockEntity anomalyBlockEntity,
            BattleTaskState state) {
        if (!(anomalyBlockEntity.getLevel() instanceof ServerLevel serverLevel)) {
            return InteractionResult.SUCCESS;
        }
        if (!state.isPreparing()) {
            return InteractionResult.PASS;
        }

        var mobs = new ArrayList<Entity>();
        mobs.add(WotrEntities.RIFT_ZOMBIE.get()
                .spawn(serverLevel, anomalyBlockEntity.getBlockPos().above(), EntitySpawnReason.SPAWNER));
        var mobUUIDs = new HashSet<>(mobs.stream().filter(Objects::nonNull).map(Entity::getUUID).toList());

        anomalyBlockEntity.updateTask(new BattleTaskState(mobUUIDs, Optional.of(player.getUUID())));
        // todo spawn mobs
        return InteractionResult.SUCCESS;
    }

    @Override
    public AnomalyTaskType<BattleTaskState> type() {
        return TYPE;
    }

    public void handleMobDeath(UUID mob, BattleTaskState state, AnomalyBlockEntity anomalyBlockEntity) {
        if (!state.isInProgress()) {
            return;
        }
        state.mobs().remove(mob);
        if (state.isRewarding()) {
            anomalyBlockEntity.getLevel()
                    .scheduleTick(anomalyBlockEntity.getBlockPos(), anomalyBlockEntity.getBlockState().getBlock(), 1);
        }
    }
}
