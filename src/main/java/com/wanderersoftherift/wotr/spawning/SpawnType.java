package com.wanderersoftherift.wotr.spawning;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.spawning.functions.SpawnFunction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.Collections;
import java.util.List;

public record SpawnType(Holder<EntityType<?>> entityType, List<Holder<SpawnFunction>> spawnFunctions) {
    public static final Codec<SpawnType> CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    BuiltInRegistries.ENTITY_TYPE.holderByNameCodec()
                            .fieldOf("entity_type")
                            .forGetter(SpawnType::entityType),
                    SpawnFunction.HOLDER_CODEC.listOf()
                            .optionalFieldOf("spawn_functions", Collections.emptyList())
                            .forGetter(SpawnType::spawnFunctions)
            ).apply(instance, SpawnType::new)
    );

    public Entity createSpawn(ServerLevel serverLevel, BlockEntity anomalyBlockEntity, RandomSource randomSource) {

        var mob = entityType().value().create(serverLevel, EntitySpawnReason.SPAWNER);
        if (mob == null) {
            return null;
        }

        if (mob instanceof Mob mob2) {
            spawnFunctions.forEach(it -> it.value().applyToMob(mob2, anomalyBlockEntity, randomSource));
        }
        return mob;
    }
}
