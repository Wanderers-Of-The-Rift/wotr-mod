package com.wanderersoftherift.wotr.abilities.targeting.shape;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.init.WotrRegistries;
import com.wanderersoftherift.wotr.modifier.effect.ModifierEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Predicate;

public interface TargetAreaShape {

    Codec<TargetAreaShape> DIRECT_CODEC = WotrRegistries.TARGET_AREA_SHAPES.byNameCodec()
            .dispatch(TargetAreaShape::codec, Function.identity());

    MapCodec<? extends TargetAreaShape> codec();

    /**
     * Produces the broadphase check for the targeting in the form of an AABB
     *
     * @param location  The source location of the area
     * @param direction The source direction of area
     * @return The AABB to search for blocks and/or entities within
     */
    default AABB getAABB(Vec3 location, Vec3 direction) {
        return getAABB(location, direction, null);
    }

    /**
     * Produces the broadphase check for the targeting in the form of an AABB
     *
     * @param location  The source location of the area
     * @param direction The source direction of area
     * @param context   The context of the ability using the shape
     * @return The AABB to search for blocks and/or entities within
     */
    AABB getAABB(Vec3 location, Vec3 direction, @Nullable AbilityContext context);

    /**
     * Produces the narrowphase check for whether an entity is in the area
     *
     * @param location  The source location of the area
     * @param direction The source direction of area
     * @return A predicate to check if individual entity hit results are in the area
     */
    default Predicate<Entity> getEntityPredicate(Vec3 location, Vec3 direction) {
        return getEntityPredicate(location, direction, null);
    }

    /**
     * Produces the narrowphase check for whether an entity is in the area
     *
     * @param location  The source location of the area
     * @param direction The source direction of area
     * @param context   The context of the ability using the shape
     * @return A predicate to check if individual entity hit results are in the area
     */
    Predicate<Entity> getEntityPredicate(Vec3 location, Vec3 direction, @Nullable AbilityContext context);

    /**
     * Produces the narrowphase check for whether a block is in the area
     *
     * @param location  The source location of the area
     * @param direction The source direction of area
     * @return A predicate to check if individual block hit results are in the area
     */
    default Predicate<BlockPos> getBlockPredicate(Vec3 location, Vec3 direction) {
        return getBlockPredicate(location, direction, null);
    }

    /**
     * Produces the narrowphase check for whether a block is in the area
     *
     * @param location  The source location of the area
     * @param direction The source direction of area
     * @param context   The context of the ability using the shape
     * @return A predicate to check if individual block hit results are in the area
     */
    Predicate<BlockPos> getBlockPredicate(Vec3 location, Vec3 direction, @Nullable AbilityContext context);

    /**
     * @param modifierEffect
     * @return Whether the modifier effect is relevant to this target area shape
     */
    boolean isRelevant(ModifierEffect modifierEffect);
}
