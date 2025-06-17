package com.wanderersoftherift.wotr.rift.objective.definition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.rift.objective.ongoing.StealthOngoingObjective;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.LevelAccessor;

/**
 * An objective to be stealthy
 */
public class StealthObjective implements ObjectiveType {
    public static final MapCodec<StealthObjective> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("min_stealth_ticks").forGetter(StealthObjective::getMinStealthTicks),
            ExtraCodecs.POSITIVE_INT.fieldOf("max_stealth_ticks").forGetter(StealthObjective::getMaxStealthTicks)
    ).apply(inst, StealthObjective::new));

    private final int minStealthTicks;
    private final int maxStealthTicks;

    public StealthObjective(int minStealthTicks, int maxStealthTicks) {
        this.minStealthTicks = minStealthTicks;
        this.maxStealthTicks = maxStealthTicks;
    }

    @Override
    public MapCodec<? extends ObjectiveType> getCodec() {
        return CODEC;
    }

    @Override
    public OngoingObjective generate(LevelAccessor level) {
        return new StealthOngoingObjective(
                level.getRandom().nextInt(minStealthTicks, Math.max(minStealthTicks, maxStealthTicks)));
    }

    public int getMinStealthTicks() {
        return minStealthTicks;
    }

    public int getMaxStealthTicks() {
        return maxStealthTicks;
    }
}
