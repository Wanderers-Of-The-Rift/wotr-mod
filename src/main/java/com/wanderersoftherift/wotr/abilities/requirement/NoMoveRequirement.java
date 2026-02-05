package com.wanderersoftherift.wotr.abilities.requirement;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.AbilityRequirement;
import net.minecraft.world.phys.Vec3;

public record NoMoveRequirement(float maxSpeed, boolean includeY) implements AbilityRequirement {
    public static final MapCodec<NoMoveRequirement> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            Codec.FLOAT.optionalFieldOf("max_speed", 0.08f).forGetter(NoMoveRequirement::maxSpeed),
            Codec.BOOL.optionalFieldOf("include_y", false).forGetter(NoMoveRequirement::includeY)
    ).apply(instance, NoMoveRequirement::new));

    @Override
    public MapCodec<? extends AbilityRequirement> getCodec() {
        return CODEC;
    }

    @Override
    public boolean check(AbilityContext context) {
        if (context.level().isClientSide()) {
            return true;
        }

        double dY;
        dY = (!includeY) ? 0 : context.caster().yo - context.caster().yOld;

        Vec3 delta = new Vec3(
                context.caster().xo - context.caster().xOld, dY, context.caster().zo - context.caster().zOld
        );
        return delta.lengthSqr() < (this.maxSpeed * this.maxSpeed);
    }
}
