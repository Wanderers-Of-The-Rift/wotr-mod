package com.wanderersoftherift.wotr.abilities.triggers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.abilities.AbilityContext;
import com.wanderersoftherift.wotr.abilities.TrackedAbilityTrigger;
import net.minecraft.core.BlockPos;

public record BreakBlockTrigger(BlockPos pos) implements TrackedAbilityTrigger {

    public static final MapCodec<BreakBlockTrigger> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            BlockPos.CODEC.fieldOf("pos").forGetter(BreakBlockTrigger::pos)
    ).apply(instance, BreakBlockTrigger::new));

    public static final TriggerType<BreakBlockTrigger> TYPE = new TriggerType<>(CODEC, null);

    @Override
    public TriggerType<BreakBlockTrigger> type() {
        return TYPE;
    }

    @Override
    public void addComponents(AbilityContext context) {

    }
}
