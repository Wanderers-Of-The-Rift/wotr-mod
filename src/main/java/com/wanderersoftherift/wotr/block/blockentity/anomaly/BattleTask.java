package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.block.blockentity.AnomalyBlockEntity;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.util.FastWeightedList;
import com.wanderersoftherift.wotr.util.RandomSourceFromJavaRandom;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.IntProvider;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
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
        var randomSource = new RandomSourceFromJavaRandom(RandomSourceFromJavaRandom.get(0),
                System.currentTimeMillis());
        var count = spawnData.count.sample(randomSource);
        var mobUUIDs = new HashSet<UUID>();

        for (int i = 0; i < count; i++) {
            var mob = spawnData.types.random(randomSource)
                    .value()
                    .spawn(serverLevel, anomalyBlockEntity.getBlockPos().above(), EntitySpawnReason.SPAWNER);

            mobUUIDs.add(mob.getUUID());
            mob.setData(WotrAttachments.BATTLE_TASK_MOB, new BattleMobAttachment(anomalyBlockEntity.getBlockPos()));
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

    public void handleMobDeath(UUID mob, BattleTaskState state, AnomalyBlockEntity anomalyBlockEntity) {
        if (!state.isInProgress()) {
            return;
        }
        state.mobs().remove(mob);
        if (state.isRewarding()) {
            anomalyBlockEntity.getLevel()
                    .scheduleTick(anomalyBlockEntity.getBlockPos(), anomalyBlockEntity.getBlockState().getBlock(), 1);
        }
        anomalyBlockEntity.sendUpdateToPlayers();
    }

    public record SpawnData(FastWeightedList<Holder<EntityType<?>>> types, IntProvider count) {
        public static final Codec<SpawnData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                FastWeightedList.codec(BuiltInRegistries.ENTITY_TYPE.holderByNameCodec())
                        .fieldOf("mobs")
                        .forGetter(SpawnData::types),
                IntProvider.CODEC.fieldOf("count").forGetter(SpawnData::count)
        ).apply(instance, SpawnData::new));
    }
}
