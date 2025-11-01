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
 * Predicate requiring all child predicates to pass
 * 
 * @param predicates
 */
public record AndPredicate(List<EntitySubPredicate> predicates) implements EntitySubPredicate {
    public static final MapCodec<AndPredicate> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            EntitySubPredicate.CODEC.listOf().fieldOf("predicates").forGetter(AndPredicate::predicates)
    ).apply(instance, AndPredicate::new));

    @Override
    public @NotNull MapCodec<? extends EntitySubPredicate> codec() {
        return CODEC;
    }

    @Override
    public boolean matches(@NotNull Entity entity, @NotNull ServerLevel level, @Nullable Vec3 position) {
        return predicates.stream().allMatch(x -> x.matches(entity, level, position));
    }
}
