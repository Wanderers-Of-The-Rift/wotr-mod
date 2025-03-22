package com.wanderersoftherift.wotr.world.level.levelgen.processor.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.world.level.levelgen.processor.output.OutputBlockState;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;

public record WeightedBlockstateEntry(OutputBlockState outputBlockState, Weight weight) implements WeightedEntry {

    public static final Codec<WeightedBlockstateEntry> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    OutputBlockState.DIRECT_CODEC.fieldOf("output_state").forGetter(WeightedBlockstateEntry::outputBlockState),
                    Codec.INT.fieldOf("weight").xmap(Weight::of, Weight::asInt).forGetter(WeightedBlockstateEntry::getWeight)
            ).apply(builder, WeightedBlockstateEntry::new));

    @Override
    public Weight getWeight() {
        return weight;
    }
}
