package com.wanderersoftherift.wotr.abilities.requirement;

import com.mojang.math.Constants;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.AbilityRequirement;
import net.minecraft.world.phys.Vec3;

public final class NoMoveRequirement implements AbilityRequirement {
    public static final NoMoveRequirement INSTANCE = new NoMoveRequirement();
    public static final MapCodec<NoMoveRequirement> CODEC = MapCodec.unit(INSTANCE);

    private NoMoveRequirement() {
    }

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

        return delta.lengthSqr() < Constants.EPSILON;
    }
}
