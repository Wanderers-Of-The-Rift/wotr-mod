package com.wanderersoftherift.wotr.abilities.targeting;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.List;
import java.util.stream.Stream;

/**
 * Information on targets for ability effects
 * 
 * @param source  The immediate preceding target, for use in effect direction
 * @param targets The targets to apply an effect to
 */
public record TargetInfo(HitResult source, List<HitResult> targets) {
    /**
     * Creates a TargetInfo with the given entity as both the source and target
     * 
     * @param entity
     */
    public TargetInfo(Entity entity) {
        this(new EntityHitResult(entity, entity.position()), List.of(new EntityHitResult(entity, entity.position())));
    }

    /**
     * @return A stream over all {@link EntityHitResult} targets
     */
    public Stream<EntityHitResult> targetEntityHitResults() {
        return targets.stream().filter(x -> x.getType() == HitResult.Type.ENTITY).map(EntityHitResult.class::cast);
    }

    /**
     * @return A stream over all entity targets
     */
    public Stream<Entity> targetEntities() {
        return targetEntityHitResults().map(EntityHitResult::getEntity);
    }

    /**
     * @return A stream over all {@link BlockHitResult} targets that are not misses
     */
    public Stream<BlockHitResult> targetBlockHitResults() {
        return targets.stream().filter(x -> x.getType() == HitResult.Type.BLOCK).map(BlockHitResult.class::cast);
    }

    /**
     * @return A stream over all block targets
     */
    public Stream<BlockPos> targetBlocks() {
        return targetBlockHitResults().map(BlockHitResult::getBlockPos);
    }

    /**
     * @param random
     * @return A single random target out of the available targets.
     * @throws IllegalStateException if there are no targets
     */
    public HitResult getRandomTarget(RandomSource random) {
        Preconditions.checkState(!targets.isEmpty());
        if (targets.size() == 1) {
            return targets.getFirst();
        }
        return targets.get(random.nextIntBetweenInclusive(0, targets.size() - 1));
    }
}
