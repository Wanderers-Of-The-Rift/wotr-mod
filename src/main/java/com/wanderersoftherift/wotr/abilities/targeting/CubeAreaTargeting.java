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
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import java.util.function.Predicate;

/**
 * Provides cubic targeting around a source
 */
public class CubeAreaTargeting extends AreaTargeting {
    public static final MapCodec<CubeAreaTargeting> CODEC = RecordCodecBuilder.mapCodec(
            instance -> commonFields(instance
            ).and(Codec.FLOAT.fieldOf("range").forGetter(CubeAreaTargeting::getBaseRange))
                    .apply(instance, CubeAreaTargeting::new));

    public final float baseRange;

    public CubeAreaTargeting(TargetEntityPredicate entities, TargetBlockPredicate blocks, boolean alignToBlock,
            float range) {
        super(entities, blocks, alignToBlock);
        this.baseRange = range;
    }

    @Override
    public MapCodec<? extends AbilityTargeting> getCodec() {
        return CODEC;
    }

    @Override
    protected AABB getArea(HitResult source, AbilityContext context, boolean alignToBlock) {
        Vec3 center = source.getLocation();
        if (alignToBlock) {
            center = BlockPos.containing(center).getCenter();
        }
        float size = 2 * getRange(context);
        return AABB.ofSize(center, size, size, size);
    }

    @Override
    protected Predicate<Entity> generateEntityInAreaPredicate(
            HitResult source,
            AABB broadArea,
            AbilityContext context,
            boolean alignToBlock) {
        return entity -> true;
    }

    @Override
    protected Predicate<BlockPos> generateBlockInAreaPredicate(
            HitResult source,
            AABB broadArea,
            AbilityContext context,
            boolean alignToBlock) {
        return pos -> true;
    }

    @Override
    public boolean isRelevant(ModifierEffect modifierEffect) {
        return modifierEffect instanceof AttributeModifierEffect attributeModifierEffect
                && WotrAttributes.ABILITY_AOE.equals(attributeModifierEffect.attribute());
    }

    public float getBaseRange() {
        return baseRange;
    }

    public float getRange(AbilityContext context) {
        return context.getAbilityAttribute(WotrAttributes.ABILITY_AOE, baseRange);
    }
}
