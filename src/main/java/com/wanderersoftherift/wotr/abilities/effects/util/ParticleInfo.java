package com.wanderersoftherift.wotr.abilities.effects.util;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;

import java.util.Optional;

public record ParticleInfo(Optional<ParticleOptions> userParticle, Optional<ParticleOptions> targetParticle,
        Optional<ParticleOptions> targetBlockParticle) {

    public static final MapCodec<ParticleInfo> CODEC = RecordCodecBuilder.mapCodec(
            instance -> instance
                    .group(ParticleTypes.CODEC.optionalFieldOf("user").forGetter(ParticleInfo::userParticle),
                            ParticleTypes.CODEC.optionalFieldOf("target").forGetter(ParticleInfo::targetParticle),
                            ParticleTypes.CODEC.optionalFieldOf("target_block")
                                    .forGetter(ParticleInfo::targetBlockParticle))
                    .apply(instance, ParticleInfo::new));
}
