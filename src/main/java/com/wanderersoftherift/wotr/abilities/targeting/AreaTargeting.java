package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetBlockPredicate;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetEntityPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

/**
 * Base type for targeting an area. Subtypes provide logic for the broad AABB to target, and the more narrow targeting
 * within that area (e.g. for a sphere the AABB is the box that contains it, and the targets in the corners are
 * rejected).
 */
public abstract class AreaTargeting implements AbilityTargeting {
    private final TargetEntityPredicate entityPredicate;
    private final TargetBlockPredicate blockPredicate;
    private final boolean alignToBlock;

    public AreaTargeting(TargetEntityPredicate entities, TargetBlockPredicate blocks, boolean alignToBlock) {
        this.entityPredicate = entities;
        this.blockPredicate = blocks;
        this.alignToBlock = alignToBlock;
    }

    protected static <T extends AreaTargeting> Products.P3<RecordCodecBuilder.Mu<T>, TargetEntityPredicate, TargetBlockPredicate, Boolean> commonFields(
            RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                TargetEntityPredicate.CODEC.optionalFieldOf("entities", TargetEntityPredicate.Trivial.ALL)
                        .forGetter(AreaTargeting::getEntityPredicate),
                TargetBlockPredicate.CODEC.optionalFieldOf("blocks", TargetBlockPredicate.Trivial.NONE)
                        .forGetter(AreaTargeting::getBlockPredicate),
                Codec.BOOL.optionalFieldOf("align_to_block", false).forGetter(AreaTargeting::getAlignToBlock)
        );
    }

    /**
     * Produces the broadphase check for the targeting in the form of an AABB
     * 
     * @param source       The source to generate the area from
     * @param context
     * @param alignToBlock Whether the area should be aligned with blocks
     * @return The AABB to search for blocks and/or entities within
     */
    protected abstract AABB getArea(HitResult source, AbilityContext context, boolean alignToBlock);

    /**
     * Produces the narrowphase check for whether an entity is in the area
     * 
     * @param source       The source to generate the predicate from
     * @param broadArea    The broadphase area
     * @param context
     * @param alignToBlock Whether the area should be aligned with blocks
     * @return A predicate to check if individual entity hit results are in the area
     */
    protected abstract Predicate<Entity> generateEntityInAreaPredicate(
            HitResult source,
            AABB broadArea,
            AbilityContext context,
            boolean alignToBlock);

    /**
     * Produces the narrowphase check for whether a block is in the area
     * 
     * @param source       The source to generate the predicate from
     * @param broadArea    The broadphase area
     * @param context
     * @param alignToBlock Whether the area should be aligned with blocks
     * @return A predicate to check if individual block hit results are in the area
     */
    protected abstract Predicate<BlockPos> generateBlockInAreaPredicate(
            HitResult source,
            AABB broadArea,
            AbilityContext context,
            boolean alignToBlock);

    @Override
    public final List<TargetInfo> getTargets(AbilityContext context, TargetInfo origin) {
        if (!(context.level() instanceof ServerLevel level)) {
            return List.of();
        }
        List<TargetInfo> result = new ArrayList<>();
        for (HitResult source : origin.targets()) {
            AABB aabb = getArea(source, context, alignToBlock);
            List<HitResult> hits = new ArrayList<>();
            if (entityPredicate != TargetEntityPredicate.Trivial.NONE) {
                var entityNarrowphase = generateEntityInAreaPredicate(source, aabb, context, alignToBlock);
                hits.addAll(TargetingUtil.getEntitiesInArea(level, aabb, (target) -> entityNarrowphase.test(target)
                        && entityPredicate.matches(target, source, context)));
            }
            if (blockPredicate != TargetBlockPredicate.Trivial.NONE) {
                var blockNarrowphase = generateBlockInAreaPredicate(source, aabb, context, alignToBlock);
                hits.addAll(TargetingUtil.getBlocksInArea(aabb,
                        (pos) -> blockNarrowphase.test(pos) && blockPredicate.matches(pos, source, context)));
            }
            if (!hits.isEmpty()) {
                result.add(new TargetInfo(source, hits));
            }
        }

        return result;
    }

    public TargetEntityPredicate getEntityPredicate() {
        return entityPredicate;
    }

    public TargetBlockPredicate getBlockPredicate() {
        return blockPredicate;
    }

    public boolean getAlignToBlock() {
        return alignToBlock;
    }
}
