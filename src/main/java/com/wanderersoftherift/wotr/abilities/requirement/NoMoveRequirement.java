package com.wanderersoftherift.wotr.abilities.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.AbilityRequirement;
import net.minecraft.world.phys.Vec3;

public record NoMoveRequirement(float maxSpeed) implements AbilityRequirement {
    public static final MapCodec<NoMoveRequirement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("max_speed", 0.01f).forGetter(NoMoveRequirement::maxSpeed)
    ).apply(instance, NoMoveRequirement::new));

    @Override
    public MapCodec<? extends AbilityRequirement> getCodec() {
        return CODEC;
    }

    @Override
    public boolean check(AbilityContext context) {
        Vec3 delta = context.caster().getDeltaMovement();
        if (context.caster().onGround()) {
            delta = delta.multiply(1, 0, 1);
        }

        return delta.lengthSqr() < maxSpeed * maxSpeed;
    }
}
