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
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

public abstract class AreaTargeting implements AbilityTargeting {
    private final TargetEntityPredicate entityPredicate;
    private final TargetBlockPredicate blockPredicate;
    private final float range;
    private final boolean alignToBlock;

    public AreaTargeting(TargetEntityPredicate entities, TargetBlockPredicate blocks, float range,
            boolean alignToBlock) {
        this.entityPredicate = entities;
        this.blockPredicate = blocks;
        this.range = range;
        this.alignToBlock = alignToBlock;
    }

    protected static <T extends AreaTargeting> Products.P4<RecordCodecBuilder.Mu<T>, TargetEntityPredicate, TargetBlockPredicate, Float, Boolean> commonFields(
            RecordCodecBuilder.Instance<T> instance) {
        return instance.group(
                TargetEntityPredicate.CODEC.optionalFieldOf("entities", TargetEntityPredicate.ALL)
                        .forGetter(AreaTargeting::getEntityPredicate),
                TargetBlockPredicate.CODEC.optionalFieldOf("blocks", TargetBlockPredicate.NONE)
                        .forGetter(AreaTargeting::getBlockPredicate),
                Codec.FLOAT.fieldOf("range").forGetter(AreaTargeting::getBaseRange),
                Codec.BOOL.optionalFieldOf("align_to_block", false).forGetter(AreaTargeting::getAlignToBlock)
        );
    }

    protected abstract AABB getArea(HitResult source, float range);

    protected abstract boolean inArea(Vec3 center, Vec3 pos, float range);

    protected abstract boolean inArea(Vec3 center, BlockPos pos, float range);

    @Override
    public final List<TargetInfo> getTargets(AbilityContext context, TargetInfo origin) {
        if (!(context.level() instanceof ServerLevel level)) {
            return List.of();
        }
        float range = getRange(context);
        List<TargetInfo> result = new ArrayList<>();
        for (HitResult source : origin.targets()) {
            AABB aabb = getArea(source, range);
            List<HitResult> hits = new ArrayList<>();
            if (entityPredicate != TargetEntityPredicate.NONE) {
                hits.addAll(TargetingUtil.getEntitiesInArea(level, aabb, (center, pos) -> inArea(center, pos, range),
                        (target) -> entityPredicate.matches(target, source, context)));
            }
            if (blockPredicate != TargetBlockPredicate.NONE) {
                hits.addAll(TargetingUtil.getBlocksInArea(aabb, (center, pos) -> inArea(center, pos, range),
                        (pos) -> blockPredicate.matches(pos, source, context)));
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
