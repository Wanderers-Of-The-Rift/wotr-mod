package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetPredicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public class RaycastTargeting extends AbstractTargeting {
    private final double range;

    public static final MapCodec<RaycastTargeting> CODEC = RecordCodecBuilder.mapCodec(
            instance -> commonFields(instance).and(Codec.DOUBLE.fieldOf("range").forGetter(RaycastTargeting::getRange))
                    .apply(instance, RaycastTargeting::new));

    public RaycastTargeting(TargetPredicate targetPredicate, double range) {
        super(targetPredicate);
        this.range = range;
    }

    public double getRange() {
        return range;
    }

    @Override
    public MapCodec<? extends AbstractTargeting> getCodec() {
        return CODEC;
    }

    @Override
    public List<Entity> getTargetsFromEntity(Entity entity, AbilityContext context) {
        WanderersOfTheRift.LOGGER.debug("Targeting Raycast");

        // TODO optimize AABB to not look behind player
        List<LivingEntity> lookedAtEntities = entity.level()
                .getEntities(EntityTypeTest.forClass(LivingEntity.class),
                        new AABB(entity.position().x - (range / 2), entity.position().y - (range / 2),
                                entity.position().z - (range / 2), entity.position().x + (range / 2),
                                entity.position().y + (range / 2), entity.position().z + (range / 2)),
                        livingEntity -> !livingEntity.is(entity)
                                && getTargetPredicate().matches(livingEntity, context.caster())
                                && livingEntity.isLookingAtMe((LivingEntity) entity, 0.025, true, false,
                                        livingEntity.getEyeY()));

        return new ArrayList<>(lookedAtEntities);
    }

    @Override
    public List<BlockPos> getBlocks(Entity entity) {
        WanderersOfTheRift.LOGGER.debug("Raycasting blocks");

        List<BlockPos> blocks = new ArrayList<>();
        if (entity == null) return blocks;

        BlockHitResult hitBlock = getEntityPOVHitResult(entity, this.range);
        blocks.add(hitBlock.getBlockPos());

        return blocks;
    }

    @Override
    public List<BlockPos> getBlocksInArea(Entity entity, List<BlockPos> targetPos, AbilityContext context) {
        WanderersOfTheRift.LOGGER.debug("Raycasting blocks in area");

        List<BlockPos> blocks = new ArrayList<>();
        if (entity == null || targetPos.isEmpty()) return blocks;

        BlockHitResult hitBlock = getEntityPOVHitResult(entity, this.range);
        blocks.add(hitBlock.getBlockPos());

        return blocks;
    }

    private BlockHitResult getEntityPOVHitResult(Entity entity, double range) {
        Level level = entity.level();
        Vec3 eyePosition = entity.getEyePosition();
        Vec3 rayVector = eyePosition.add(entity.calculateViewVector(entity.getXRot(), entity.getYRot()).scale(range));
        return level.clip(new ClipContext(eyePosition, rayVector, net.minecraft.world.level.ClipContext.Block.OUTLINE,
                ClipContext.Fluid.NONE, entity));
    }
}
