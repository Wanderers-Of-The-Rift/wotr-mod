package com.wanderersoftherift.wotr.abilities.targeting;

import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;
import java.util.stream.Stream;

public record TargetInfo(HitResult source, List<HitResult> targets) {
    public TargetInfo(Entity source) {
        this(new EntityHitResult(source, source.position()), List.of(new EntityHitResult(source, source.position())));
    }

    public Stream<EntityHitResult> targetEntities() {
        return targets.stream().filter(x -> x.getType() == HitResult.Type.ENTITY).map(EntityHitResult.class::cast);
    }

    public Stream<BlockHitResult> targetBlocks() {
        return targets.stream().filter(x -> x.getType() == HitResult.Type.BLOCK).map(BlockHitResult.class::cast);
    }

    public HitResult getRandomTarget(RandomSource random) {
        return targets.get(random.nextIntBetweenInclusive(0, targets.size() - 1));
    }
}
