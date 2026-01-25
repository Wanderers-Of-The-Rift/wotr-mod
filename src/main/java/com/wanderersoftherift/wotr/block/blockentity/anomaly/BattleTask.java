package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.block.blockentity.AnomalyBlockEntity;
import com.wanderersoftherift.wotr.spawning.SpawnType;
import com.wanderersoftherift.wotr.util.FastWeightedList;
import com.wanderersoftherift.wotr.util.RandomFactoryType;
import com.wanderersoftherift.wotr.util.RandomSourceFromJavaRandom;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

import java.util.HashSet;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;

public record BattleTask(SpawnData spawnData) implements AnomalyTask<BattleTaskState> {
    public static final AnomalyTaskType<BattleTaskState> TYPE = new AnomalyTaskType<>(
            SpawnData.CODEC.xmap(BattleTask::new, BattleTask::spawnData).fieldOf("spawns"), BattleTaskState.CODEC
    );
    private static final ItemStack ZOMBIE_HEAD = new ItemStack(Items.ZOMBIE_HEAD, 1);

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
        var randomSource = new RandomSourceFromJavaRandom(RandomSourceFromJavaRandom.get(RandomFactoryType.DEFAULT),
                System.currentTimeMillis());
        var count = spawnData.count.sample(randomSource);
        var mobUUIDs = new HashSet<UUID>();

        for (int i = 0; i < count; i++) {
            var type = spawnData.types.random(randomSource);
            var mob = type.createSpawn(serverLevel, anomalyBlockEntity, randomSource);
            if (mob == null) {
                continue;
            }
            serverLevel.addFreshEntityWithPassengers(mob);
            mob.getPassengersAndSelf().map(Entity::getUUID).forEach(mobUUIDs::add);
        }

        anomalyBlockEntity.updateTask(new BattleTaskState(mobUUIDs, Optional.of(player.getUUID())));
        return InteractionResult.SUCCESS;
    }

    @Override
    public AnomalyTaskType<BattleTaskState> type() {
        return TYPE;
    }

    @Override
    public BattleTaskState createState(RandomSource randomSource) {
        return new BattleTaskState(new HashSet<>(), Optional.empty());
    }

    @Override
    public int particleColor() {
        return 0xff_00_00;
    }

    @Override
    public AnomalyTaskDisplay taskDisplay(BattleTaskState task) {
        return new AnomalyTaskDisplay() {
            @Override
            public int getCount() {
                return task.mobs().size();
            }

            @Override
            public void forEachIndexed(BiConsumer<Integer, ItemStack> func) {
                for (int i = 0; i < getCount(); i++) {
                    func.accept(i, ZOMBIE_HEAD);
                }
            }
        };
    }

    @Override
    public void handleMobDeath(LivingEntity mob, AnomalyBlockEntity anomalyBlockEntity, BattleTaskState state) {
        if (!state.isInProgress()) {
            return;
        }
        state.mobs().remove(mob.getUUID());
        if (state.isRewarding()) {
            anomalyBlockEntity.getLevel()
                    .scheduleTick(anomalyBlockEntity.getBlockPos(), anomalyBlockEntity.getBlockState().getBlock(), 1);
        }
        anomalyBlockEntity.sendUpdateToPlayers();
    }

    @Override
    public int scheduledTick(ServerLevel serverLevel, AnomalyBlockEntity anomalyBlockEntity, BattleTaskState state) {
        if (state.isRewarding()) {
            var player = serverLevel.getPlayerByUUID(state.player().get());
            if (player != null) {
                anomalyBlockEntity.closeAndReward(player);
                return -1;
            }
        }
        return 1;
    }

    public record SpawnData(FastWeightedList<SpawnType> types, IntProvider count) {
        public static final Codec<SpawnData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                FastWeightedList.listCodec(SpawnType.CODEC).fieldOf("mobs").forGetter(SpawnData::types),
                IntProvider.CODEC.fieldOf("count").forGetter(SpawnData::count)
        ).apply(instance, SpawnData::new));
    }
}
