package com.wanderersoftherift.wotr.abilities.effects;

import com.mojang.serialization.Codec;
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
public record ParticleEffect(ParticleOptions particle, int count, Vec3 distribution, float speed)
        implements AbilityEffect {

    public static final MapCodec<ParticleEffect> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            ParticleTypes.CODEC.fieldOf("particle").forGetter(ParticleEffect::particle),
            Codec.INT.optionalFieldOf("count", 1).forGetter(ParticleEffect::count),
            Vec3.CODEC.optionalFieldOf("distribution", Vec3.ZERO).forGetter(ParticleEffect::distribution),
            Codec.FLOAT.optionalFieldOf("speed", 0f).forGetter(ParticleEffect::speed)
    ).apply(instance, ParticleEffect::new));

    @Override
    public MapCodec<? extends AbilityEffect> getCodec() {
        return CODEC;
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
        level.sendParticles(particleOptions, false, true, position.x, position.y, position.z, count, distribution.x,
                distribution.y, distribution.z, speed);
    }
}
