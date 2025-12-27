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

public record SphereShape(float baseRange, boolean alignToBlock) implements TargetAreaShape {

    public static final MapCodec<SphereShape> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.fieldOf("range").forGetter(SphereShape::baseRange),
            Codec.BOOL.optionalFieldOf("align_to_block", false).forGetter(SphereShape::alignToBlock)
    ).apply(instance, SphereShape::new));

    @Override
    public MapCodec<? extends TargetAreaShape> codec() {
        return CODEC;
    }

    @Override
    public AABB getAABB(Vec3 location, Vec3 direction, @Nullable AbilityContext context) {
        float range = getRange(context);
        Vec3 center = location;
        if (alignToBlock) {
            center = BlockPos.containing(center).getCenter();
        }
        float size = 2 * range;
        return AABB.ofSize(center, size, size, size);
    }

    @Override
    public Predicate<Entity> getEntityPredicate(Vec3 location, Vec3 direction, @Nullable AbilityContext context) {
        var basePredicate = getBoundingBoxPredicate(location, direction, context);
        return (entity) -> basePredicate.test(entity.getBoundingBox());
    }

    private Predicate<AABB> getBoundingBoxPredicate(Vec3 location, Vec3 direction, @Nullable AbilityContext context) {
        float range = getRange(context);
        float rangeSqrd = range * range;
        Vec3 center;
        if (alignToBlock) {
            center = BlockPos.containing(location).getCenter();
        } else {
            center = location;
        }
        return (box) -> closestTo(box, center).distanceToSqr(center) < rangeSqrd;
    }

    private Vec3 closestTo(AABB boundingBox, Vec3 otherPos) {
        return new Vec3(Math.max(boundingBox.minX, Math.min(otherPos.x, boundingBox.maxX)),
                Math.max(boundingBox.minY, Math.min(otherPos.y, boundingBox.maxY)),
                Math.max(boundingBox.minZ, Math.min(otherPos.z, boundingBox.maxZ)));
    }

    @Override
    public Predicate<BlockPos> getBlockPredicate(Vec3 location, Vec3 direction, @Nullable AbilityContext context) {
        var basePredicate = getBoundingBoxPredicate(location, direction, context);
        return (block) -> basePredicate.test(new AABB(block.getX(), block.getY(), block.getZ(), block.getX() + 1,
                block.getY() + 1, block.getZ() + 1));
    }

    @Override
    public boolean isRelevant(ModifierEffect modifierEffect) {
        return modifierEffect instanceof AttributeModifierEffect attributeModifierEffect
                && WotrAttributes.ABILITY_AOE.equals(attributeModifierEffect.attribute());
    }

    private float getRange(@Nullable AbilityContext context) {
        if (context == null) {
            return baseRange;
        }
        return context.getAbilityAttribute(WotrAttributes.ABILITY_AOE, baseRange);
    }
}
