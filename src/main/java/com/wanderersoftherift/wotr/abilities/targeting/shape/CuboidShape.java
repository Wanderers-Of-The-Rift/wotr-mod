package com.wanderersoftherift.wotr.abilities.targeting.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.init.WotrAttributes;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public record CuboidShape(Vec3 area, boolean alignToBlock) implements TargetAreaShape {

    public static final MapCodec<CuboidShape> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Vec3.CODEC.fieldOf("area").forGetter(CuboidShape::area),
            Codec.BOOL.optionalFieldOf("align_to_block", false).forGetter(CuboidShape::alignToBlock)
    ).apply(instance, CuboidShape::new));

    @Override
    public MapCodec<? extends TargetAreaShape> codec() {
        return CODEC;
    }

    @Override
    public AABB getAABB(Vec3 location, Vec3 direction, @Nullable AbilityContext context) {
        Vec3 size = getArea(context);
        Vec3 center = location;

        if (alignToBlock) {
            center = BlockPos.containing(center).getCenter();
        }

        boolean flip = Math.abs(direction.x) > Math.abs(direction.z);

        return AABB.ofSize(center, flip ? size.z : size.x, size.y, flip ? size.x : size.z);
    }

    @Override
    public Predicate<Entity> getEntityPredicate(Vec3 location, Vec3 direction, @Nullable AbilityContext context) {
        return entity -> true;
    }

    @Override
    public Predicate<BlockPos> getBlockPredicate(Vec3 location, Vec3 direction, @Nullable AbilityContext context) {
        return pos -> true;
    }

    @Override
    public boolean isRelevant(ModifierEffect modifierEffect) {
        return modifierEffect instanceof AttributeModifierEffect attributeModifierEffect
                && WotrAttributes.ABILITY_AOE.equals(attributeModifierEffect.attribute());
    }

    private Vec3 getArea(@Nullable AbilityContext context) {
        if (context == null) {
            return area;
        }
        float modifier = context.getAbilityAttribute(WotrAttributes.ABILITY_AOE, (float) area.length());
        return area.scale(modifier / area.length());
    }
}
