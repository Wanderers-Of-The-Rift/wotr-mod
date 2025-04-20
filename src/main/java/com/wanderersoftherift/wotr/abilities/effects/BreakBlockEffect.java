package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.effects.util.ParticleInfo;
import com.wanderersoftherift.wotr.abilities.targeting.AbstractTargeting;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;

import java.util.List;
import java.util.Optional;

public class BreakBlockEffect extends AbstractEffect {

    public static final MapCodec<BreakBlockEffect> CODEC = RecordCodecBuilder
            .mapCodec(instance -> AbstractEffect.commonFields(instance).apply(instance, BreakBlockEffect::new));

    public BreakBlockEffect(AbstractTargeting targeting, List<AbstractEffect> effects,
            Optional<ParticleInfo> particles) {
        super(targeting, effects, particles);
    }

    @Override
    public MapCodec<? extends AbstractEffect> getCodec() {
        return CODEC;
    }

    @Override
    public void apply(Entity user, List<BlockPos> blocks, AbilityContext context) {
        List<Entity> targets = getTargeting().getTargets(user, blocks, context);
        applyParticlesToUser(user);

        List<BlockPos> areaBlocks = getTargeting().getBlocksInArea(user, blocks, context);

        for (BlockPos pos : areaBlocks) {
            // TODO: Make fortune work maybe? (Apply tool enchants etc)
            if (context.level().getBlockState(pos).canEntityDestroy(context.level(), pos, context.caster())
                    && context.level().getBlockState(pos).getBlock().defaultDestroyTime() > -1) {
                context.level().destroyBlock(pos, true, context.caster());
            }
        }

        if (targets.isEmpty()) {
            super.apply(null, getTargeting().getBlocks(user), context);
        }
    }
}
