package com.wanderersoftherift.wotr.rift.objective.definition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.rift.objective.ongoing.KillOngoingObjective;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.level.LevelAccessor;

/**
 * A simple objective to defeat X mobs (and escape)
 */
public class KillObjective implements ObjectiveType {
    public static final MapCodec<KillObjective> CODEC = RecordCodecBuilder.mapCodec(inst -> inst
            .group(ExtraCodecs.POSITIVE_INT.fieldOf("min_quantity").forGetter(KillObjective::getMinQuantity),
                    ExtraCodecs.POSITIVE_INT.fieldOf("max_quantity").forGetter(KillObjective::getMaxQuantity))
            .apply(inst, KillObjective::new));

    private final int minQuantity;
    private final int maxQuantity;

    public KillObjective(int minQuantity, int maxQuantity) {
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
    }

    @Override
    public MapCodec<? extends ObjectiveType> getCodec() {
        return CODEC;
    }

    @Override
    public OngoingObjective generate(LevelAccessor level) {
        return new KillOngoingObjective(level.getRandom().nextIntBetweenInclusive(minQuantity, maxQuantity));
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }
}
