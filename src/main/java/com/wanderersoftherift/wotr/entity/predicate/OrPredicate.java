package com.wanderersoftherift.wotr.entity.predicate;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Predicate requiring at least one child predicate to pass
 * 
 * @param predicates
 */
public record OrPredicate(List<EntitySubPredicate> predicates) implements EntitySubPredicate {
    public static final MapCodec<OrPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EntitySubPredicate.CODEC.listOf().fieldOf("predicates").forGetter(OrPredicate::predicates)
    ).apply(instance, OrPredicate::new));

    @Override
    public @NotNull MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean matches(@NotNull Entity entity, @NotNull ServerLevel level, @Nullable Vec3 position) {
        return predicates.stream().anyMatch(x -> x.matches(entity, level, position));
    }
}
