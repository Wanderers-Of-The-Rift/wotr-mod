package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetPredicate;
import com.wanderersoftherift.wotr.init.ModAttributes;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import com.wanderersoftherift.wotr.modifier.effect.AttributeModifierEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;

import java.util.ArrayList;
import java.util.List;

public class CubeAreaTargeting extends AbstractTargeting {
    private float range = 0;
    private boolean includeSelf = true;

    public static final MapCodec<CubeAreaTargeting> CODEC = RecordCodecBuilder.mapCodec(
            instance -> commonFields(instance).and(Codec.FLOAT.fieldOf("range").forGetter(CubeAreaTargeting::getRange))
                    .and(Codec.BOOL.optionalFieldOf("include_self", true).forGetter(CubeAreaTargeting::getIncludeSelf))
                    .apply(instance, CubeAreaTargeting::new));

    public CubeAreaTargeting(TargetPredicate predicate, float range, boolean includeSelf) {
        super(predicate);
        this.range = range;
        this.includeSelf = includeSelf;
    }

    public float getRange() {
        return range;
    }

    private float getRange(AbilityContext context) {
        return context.getAbilityAttribute(ModAttributes.ABILITY_AOE, range);
    }

    public boolean getIncludeSelf() {
        return includeSelf;
    }

    @Override
    public MapCodec<? extends AbstractTargeting> getCodec() {
        return CODEC;
    }

    @Override
    public List<Entity> getTargetsFromEntity(Entity entity, AbilityContext context) {
        float finalRange = getRange(context);

        return entity.level()
                .getEntities((Entity) null, AABB.ofSize(entity.position(), finalRange, finalRange, finalRange),
                        (target) -> getTargetPredicate().matches(target, context.caster())
                                && (includeSelf || !target.is(entity)));
    }

    @Override
    public List<Entity> getTargetsFromBlocks(List<BlockPos> blocks, AbilityContext context) {
        WanderersOfTheRift.LOGGER.debug("Targeting from blocks via AOE");
        float finalRange = getRange(context);

        // Gets first block and makes an area around it where the block is in the center
        // TODO look into config for selecting conditions in area since we may want to select other players for large
        // scale heals etc
        return context.level()
                .getEntities(context.caster(),
                        new AABB(blocks.get(0).getX() - (finalRange / 2), blocks.get(0).getY() - (finalRange / 2),
                                blocks.get(0).getZ() - (finalRange / 2), blocks.get(0).getX() + (finalRange / 2),
                                blocks.get(0).getY() + (finalRange / 2), blocks.get(0).getZ() + (finalRange / 2)),
                        (target) -> getTargetPredicate().matches(target, context.caster()));
    }

//    @Override
//    public List<BlockPos> getBlocks(Entity entity) {
//        WanderersOfTheRift.LOGGER.info("Targeting AOE");
//         TODO think about handling all blocks in an AOE?
//         return entity.level().getEntities(user, new AABB(user.position().x - (range/2), user.position().y - (range/2), user.position().z - (range/2), user.position().x + (range/2), user.position().y + (range/2), user.position().z + (range/2)), (entity -> !(entity instanceof Player) && !entity.is(user)));
//    }

    @Override
    public List<BlockPos> getBlocksInArea(Entity entity, List<BlockPos> targetPos, AbilityContext context) {
        WanderersOfTheRift.LOGGER.info("Targeting blocks in area via AOE");
        float finalRange = getRange(context);

        int startX, startY, startZ;
        int endX, endY, endZ;
        List<BlockPos> blockPos = new ArrayList<>();

        // TODO maybe look into java ranges?
        if (entity == null) {

            BlockPos first = targetPos.get(0);
            startX = (int) (first.getX() - (finalRange / 2));
            startY = (int) (first.getY() - (finalRange / 2));
            startZ = (int) (first.getZ() - (finalRange / 2));

            endX = (int) (first.getX() + (finalRange / 2));
            endY = (int) (first.getY() + (finalRange / 2));
            endZ = (int) (first.getZ() + (finalRange / 2));
        } else {
            // TODO make this use player block position rather than absolute pos since it causes rounding issues
            // converting to int.
            startX = (int) (entity.getX() - (finalRange / 2));
            startY = (int) (entity.getY() - (finalRange / 2));
            startZ = (int) (entity.getZ() - (finalRange / 2));

            endX = (int) (entity.getX() + (finalRange / 2));
            endY = (int) (entity.getY() + (finalRange / 2));
            endZ = (int) (entity.getZ() + (finalRange / 2));
        }

        for (int x = startX; x < endX; x++) {
            for (int y = startY; y < endY; y++) {
                for (int z = startZ; z < endZ; z++) {
                    if (!context.level().getBlockState(new BlockPos(x, y, z)).isAir()) {
                        blockPos.add(new BlockPos(x, y, z));
                    }
                }
            }
        }

        return blockPos;
    }

    @Override
    public boolean isRelevant(AbstractModifierEffect modifierEffect) {
        return modifierEffect instanceof AttributeModifierEffect attributeModifierEffect
                && ModAttributes.ABILITY_AOE.equals(attributeModifierEffect.getAttribute());
    }
}
