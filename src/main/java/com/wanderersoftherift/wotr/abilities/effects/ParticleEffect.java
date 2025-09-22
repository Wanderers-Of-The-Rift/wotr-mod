package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.targeting.TargetInfo;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

/**
 * Effect that applies particle effects
 */
public class ParticleEffect implements AbilityEffect {

    public static final MapCodec<ParticleEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ParticleTypes.CODEC.fieldOf("particle").forGetter(ParticleEffect::getParticle)
    ).apply(instance, ParticleEffect::new));

    private final ParticleOptions particle;

    public ParticleEffect(ParticleOptions particle) {
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
    public void apply(AbilityContext context, TargetInfo targetInfo) {
        if (!(context.level() instanceof ServerLevel serverLevel)) {
            return;
        }

        for (HitResult target : targetInfo.targets()) {
            spawnParticles(serverLevel, target.getLocation(), particle);
        }
    }

    private void spawnParticles(ServerLevel level, Vec3 position, ParticleOptions particleOptions) {
        level.sendParticles(particleOptions, false, true, position.x, position.y + 1.5, position.z, 10, Math.random(),
                Math.random(), Math.random(), 2);
    }
}
