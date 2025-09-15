package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetBlockPredicate;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetEntityPredicate;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public abstract class AreaTargeting implements AbilityTargeting {
    private final TargetEntityPredicate entityPredicate;
    private final TargetBlockPredicate blockPredicate;
    private final float range;
    private final boolean includeSelf;
    private final boolean alignToBlock;

    public AreaTargeting(TargetEntityPredicate entities, TargetBlockPredicate blocks, float range, boolean includeSelf,
            boolean alignToBlock) {
        this.entityPredicate = entities;
        this.blockPredicate = blocks;
        this.range = range;
        this.includeSelf = includeSelf;
        this.alignToBlock = alignToBlock;
    }

    protected static <T extends AreaTargeting> Products.P5<RecordCodecBuilder.Mu<T>, TargetEntityPredicate, TargetBlockPredicate, Float, Boolean, Boolean> commonFields(
            RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                TargetEntityPredicate.CODEC.optionalFieldOf("entities", TargetEntityPredicate.ALL)
                        .forGetter(AreaTargeting::getEntityPredicate),
                TargetBlockPredicate.CODEC.optionalFieldOf("blocks", TargetBlockPredicate.NONE)
                        .forGetter(AreaTargeting::getBlockPredicate),
                Codec.FLOAT.fieldOf("range").forGetter(AreaTargeting::getBaseRange),
                Codec.BOOL.optionalFieldOf("include_self", true).forGetter(AreaTargeting::getIncludeSelf),
                Codec.BOOL.optionalFieldOf("align_to_block", false).forGetter(AreaTargeting::getAlignToBlock)
        );
    }

    protected abstract AABB getArea(HitResult source, float range);

    protected abstract boolean inArea(Vec3 center, Vec3 pos, float range);

    protected abstract boolean inArea(Vec3 center, BlockPos pos, float range);

    @Override
    public final List<TargetInfo> getTargets(AbilityContext context, TargetInfo origin) {
        float range = getRange(context);
        List<TargetInfo> result = new ArrayList<>();
        for (HitResult source : origin.targets()) {
            AABB aabb = getArea(source, range);
            List<HitResult> hits = new ArrayList<>();
            if (entityPredicate != TargetEntityPredicate.NONE) {
                Entity sourceEntity;
                if (source instanceof EntityHitResult sourceEntityHit) {
                    sourceEntity = sourceEntityHit.getEntity();
                } else {
                    sourceEntity = null;
                }
                hits.addAll(TargetingUtil.getEntitiesInArea(context.level(), aabb,
                        (center, pos) -> inArea(center, pos, range),
                        (target) -> entityPredicate.matches(target, context.caster())
                                && (includeSelf || target != sourceEntity)));
            }
            if (blockPredicate != TargetBlockPredicate.NONE) {
                hits.addAll(TargetingUtil.getBlocksInArea(aabb, (center, pos) -> inArea(center, pos, range),
                        (pos) -> blockPredicate.matches(pos, context.level())));
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

    public TargetEntityPredicate getEntityPredicate() {
        return entityPredicate;
    }

    public TargetBlockPredicate getBlockPredicate() {
        return blockPredicate;
    }

    public boolean getIncludeSelf() {
        return includeSelf;
    }

    public float getBaseRange() {
        return range;
    }

    public float getRange(AbilityContext context) {
        return context.getAbilityAttribute(WotrAttributes.ABILITY_AOE, range);
    }

    public boolean getAlignToBlock() {
        return alignToBlock;
    }
}
