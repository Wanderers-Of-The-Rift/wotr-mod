package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.AbilityTargeting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

import java.util.List;

public class ParticleEffect extends AbilityEffect {

    public static final MapCodec<ParticleEffect> CODEC = RecordCodecBuilder
            .mapCodec(instance -> AbilityEffect.commonFields(instance)
                    .and(ParticleTypes.CODEC.fieldOf("particle").forGetter(ParticleEffect::getParticle))
                    .apply(instance, ParticleEffect::new));

    private final ParticleOptions particle;

    public ParticleEffect(AbilityTargeting targeting, List<AbilityEffect> effects, ParticleOptions particle) {
        super(targeting, effects);
        this.particle = particle;
    }

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
    }

    public ParticleOptions getParticle() {
        return particle;
    }

    @Override
    public void apply(Entity user, List<BlockPos> blocks, AbilityContext context) {
        List<Entity> targets = getTargeting().getTargets(user, blocks, context);

        if (!(context.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        for (BlockPos block : blocks) {
            applyParticlesToPos(serverLevel, block.getCenter(), particle);
        }

        for (Entity target : targets) {
            applyParticlesToPos(serverLevel, target.position(), particle);

            // Then apply children effects to targets
            super.apply(target, getTargeting().getBlocks(user), context);
        }

        if (targets.isEmpty()) {
            super.apply(null, getTargeting().getBlocks(user), context);
        }
    }

    public void applyParticlesToPos(ServerLevel level, Vec3 position, ParticleOptions particleOptions) {
        level.sendParticles(particleOptions, false, true, position.x, position.y + 1.5, position.z, 10, Math.random(),
                Math.random(), Math.random(), 2);
    }
}
