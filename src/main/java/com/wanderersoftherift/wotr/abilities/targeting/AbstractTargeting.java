package com.wanderersoftherift.wotr.abilities.targeting;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.predicate.TargetPredicate;
import com.wanderersoftherift.wotr.init.ModTargetingTypes;
import com.wanderersoftherift.wotr.modifier.effect.AbstractModifierEffect;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public abstract class AbstractTargeting {
    public abstract MapCodec<? extends AbstractTargeting> getCodec();

    public static final Codec<AbstractTargeting> DIRECT_CODEC = ModTargetingTypes.EFFECT_TARGETING_REGISTRY
            .byNameCodec()
            .dispatch(AbstractTargeting::getCodec, Function.identity());

    private final TargetPredicate targetPredicate;

    public AbstractTargeting(TargetPredicate targetPredicate) {
        this.targetPredicate = targetPredicate;
    }

    /**
     * @param currentEntity This is the entity which is using the effect, this can be any entity down a chain based on
     *                      the effect list, this determines the location around where the effect is targeting
     * @param blocks        A list of blocks which can be a point of reference for targeting enemies around them. This
     *                      is mainly used for raycasting based effects
     * @param context       Context of the effect
     * @return The list of entities selected by the targeting method.
     */
    public List<Entity> getTargets(Entity currentEntity, List<BlockPos> blocks, AbilityContext context) {
        List<Entity> targets = new ArrayList<>();
        if (currentEntity != null) {
            targets.addAll(getTargetsFromEntity(currentEntity, context));
        } else {
            targets.addAll(getTargetsFromBlocks(blocks, context));
        }

        return targets;
    }

    protected List<Entity> getTargetsFromEntity(Entity entity, AbilityContext context) {
        return new ArrayList<>();
    }

    protected List<Entity> getTargetsFromBlocks(List<BlockPos> blocks, AbilityContext context) {
        return new ArrayList<>();
    }

    public List<BlockPos> getBlocks(Entity user) {
        return new ArrayList<>();
    }

    public List<BlockPos> getBlocksInArea(Entity entity, List<BlockPos> targetPos, AbilityContext context) {
        return new ArrayList<>();
    }

    public boolean isRelevant(AbstractModifierEffect modifierEffect) {
        return false;
    }

    public TargetPredicate getTargetPredicate() {
        return targetPredicate;
    }

    protected static <T extends AbstractTargeting> Products.P1<RecordCodecBuilder.Mu<T>, TargetPredicate> commonFields(
            RecordCodecBuilder.Instance<T> instance) {
        return instance.group(TargetPredicate.CODEC.optionalFieldOf("target", new TargetPredicate())
                .forGetter(AbstractTargeting::getTargetPredicate));
    }
}
