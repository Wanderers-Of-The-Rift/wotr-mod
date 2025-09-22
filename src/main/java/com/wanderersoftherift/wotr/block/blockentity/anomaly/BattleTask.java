package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.block.blockentity.AnomalyBlockEntity;
import com.wanderersoftherift.wotr.entity.mob.RiftZombie;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import com.wanderersoftherift.wotr.init.WotrRegistries;
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
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.event.EventHooks;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
            var type = spawnData.types.random(randomSource);
            var mob = type.entityType().value().create(serverLevel, EntitySpawnReason.SPAWNER);
            if (mob == null) {
                continue;
            }
            mob.moveTo(anomalyBlockEntity.getBlockPos().above(), 0, 0);

            if (mob instanceof Mob mob2) {
                type.spawnFunctions.forEach(it -> it.applyToMob(mob2, anomalyBlockEntity, randomSource));
            }
            serverLevel.addFreshEntityWithPassengers(mob);
            mobUUIDs.add(mob.getUUID());
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

    // todo move everything below elsewhere
    public interface SpawnFunction {
        Codec<SpawnFunction> CODEC = WotrRegistries.SPAWN_FUNCTION_TYPES.byNameCodec()
                .dispatch(SpawnFunction::codec, Function.identity());

        MapCodec<? extends SpawnFunction> codec();

        void applyToMob(Mob mob, BlockEntity spawner, RandomSource random);
    }

    // todo fix after #344
    public record ApplyMobVariant(String variant) implements SpawnFunction {
        public static final MapCodec<ApplyMobVariant> MAP_CODEC = Codec.STRING.fieldOf("variant")
                .xmap(ApplyMobVariant::new, ApplyMobVariant::variant);

        @Override
        public MapCodec<? extends SpawnFunction> codec() {
            return MAP_CODEC;
        }

        @Override
        public void applyToMob(Mob mob, BlockEntity spawner, RandomSource random) {
            if (mob instanceof RiftZombie zombie) {
                zombie.setVariant(variant);
            }
        }
    }

    public record AddDeathNotifier() implements SpawnFunction {
        public static final AddDeathNotifier INSTANCE = new AddDeathNotifier();
        public static final MapCodec<AddDeathNotifier> MAP_CODEC = MapCodec.unit(INSTANCE);

        @Override
        public MapCodec<? extends SpawnFunction> codec() {
            return MAP_CODEC;
        }

        @Override
        public void applyToMob(Mob mob, BlockEntity spawner, RandomSource random) {
            mob.setData(WotrAttachments.BATTLE_TASK_MOB, new BattleMobAttachment(spawner.getBlockPos()));
        }
    }

    public record FinalizeSpawn() implements SpawnFunction {
        public static final FinalizeSpawn INSTANCE = new FinalizeSpawn();
        public static final MapCodec<FinalizeSpawn> MAP_CODEC = MapCodec.unit(INSTANCE);

        @Override
        public MapCodec<? extends SpawnFunction> codec() {
            return MAP_CODEC;
        }

        @Override
        public void applyToMob(Mob mob, BlockEntity spawner, RandomSource random) {
            if (!(spawner.getLevel() instanceof ServerLevel serverLevel)) {
                return;
            }
            EventHooks.finalizeMobSpawn(mob, serverLevel, serverLevel.getCurrentDifficultyAt(mob.blockPosition()),
                    EntitySpawnReason.SPAWNER, null);
        }
    }

    public record SpawnType(Holder<EntityType<?>> entityType, List<SpawnFunction> spawnFunctions) {
        public static final Codec<SpawnType> CODEC = RecordCodecBuilder.create(
                instance -> instance.group(
                        BuiltInRegistries.ENTITY_TYPE.holderByNameCodec()
                                .fieldOf("entity_type")
                                .forGetter(SpawnType::entityType),
                        SpawnFunction.CODEC.listOf()
                                .optionalFieldOf("spawn_functions", Collections.emptyList())
                                .forGetter(SpawnType::spawnFunctions)
                ).apply(instance, SpawnType::new)
        );
    }

    public record SpawnData(FastWeightedList<SpawnType> types, IntProvider count) {
        public static final Codec<SpawnData> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                FastWeightedList.listCodec(SpawnType.CODEC).fieldOf("mobs").forGetter(SpawnData::types),
                IntProvider.CODEC.fieldOf("count").forGetter(SpawnData::count)
        ).apply(instance, SpawnData::new));
    }
}
