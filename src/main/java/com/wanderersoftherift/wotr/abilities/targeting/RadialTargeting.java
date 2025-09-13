package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetBlockPredicate;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetEntityPredicate;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

import java.util.ArrayList;
import java.util.List;

public class RadialTargeting implements AbilityTargeting {
    public static final MapCodec<RadialTargeting> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance.group(
                    TargetEntityPredicate.CODEC.optionalFieldOf("entities", TargetEntityPredicate.ALL)
                            .forGetter(RadialTargeting::getEntityPredicate),
                    TargetBlockPredicate.CODEC.optionalFieldOf("blocks", TargetBlockPredicate.NONE)
                            .forGetter(RadialTargeting::getBlockPredicate),
                    Codec.FLOAT.fieldOf("range").forGetter(RadialTargeting::getRange),
                    Codec.BOOL.optionalFieldOf("include_self", true).forGetter(RadialTargeting::getIncludeSelf)
            ).apply(instance, RadialTargeting::new));

    private final TargetEntityPredicate entityPredicate;
    private final TargetBlockPredicate blockPredicate;
    private final float range;
    private final boolean includeSelf;

    public RadialTargeting(TargetEntityPredicate entities, TargetBlockPredicate blocks, float range,
            boolean includeSelf) {
        this.entityPredicate = entities;
        this.blockPredicate = blocks;
        this.range = range;
        this.includeSelf = includeSelf;
    }

    @Override
    public MapCodec<? extends AbilityTargeting> getCodec() {
        return CODEC;
    }

    @Override
    public List<TargetInfo> getTargets(AbilityContext context, TargetInfo origin) {
        float finalRange = getRange(context);
        float finalRangeSqr = finalRange * finalRange;

        List<TargetInfo> result = new ArrayList<>();
        for (HitResult source : origin.targets()) {
            List<HitResult> hits = new ArrayList<>();
            AABB aabb = AABB.ofSize(source.getLocation(), 2 * finalRange, 2 * finalRange, 2 * finalRange);
            if (entityPredicate != TargetEntityPredicate.NONE) {
                Entity sourceEntity;
                if (source instanceof EntityHitResult sourceEntityHit) {
                    sourceEntity = sourceEntityHit.getEntity();
                } else {
                    sourceEntity = null;
                }
                hits.addAll(context.level()
                        .getEntities((Entity) null, aabb,
                                (target) -> target.distanceToSqr(source.getLocation()) < finalRangeSqr
                                        && entityPredicate.matches(target, context.caster())
                                        && (includeSelf || target != sourceEntity))
                        .stream()
                        .map(entity -> new EntityHitResult(entity, entity.position()))
                        .toList());
            }
            if (blockPredicate != TargetBlockPredicate.NONE) {
                for (BlockPos pos : BlockPos.betweenClosed(aabb)) {
                    if (pos.closerToCenterThan(source.getLocation(), finalRange)
                            && blockPredicate.matches(pos, context.level())) {
                        // TODO: review isInside
                        hits.add(new BlockHitResult(pos.getCenter(),
                                Direction.getApproximateNearest(pos.getCenter().subtract(source.getLocation())),
                                new BlockPos(pos), false));
                    }
                }
            }
            if (!hits.isEmpty()) {
                result.add(new TargetInfo(source, hits));
            }
        }

        return result;
    }

    @Override
    public boolean isRelevant(ModifierEffect modifierEffect) {
        return modifierEffect instanceof AttributeModifierEffect attributeModifierEffect
                && WotrAttributes.ABILITY_AOE.equals(attributeModifierEffect.attribute());
    }

    private TargetEntityPredicate getEntityPredicate() {
        return entityPredicate;
    }

    public TargetBlockPredicate getBlockPredicate() {
        return blockPredicate;
    }

    private float getRange() {
        return range;
    }

    private float getRange(AbilityContext context) {
        return context.getAbilityAttribute(WotrAttributes.ABILITY_AOE, range);
    }

    private boolean getIncludeSelf() {
        return includeSelf;
    }
}
