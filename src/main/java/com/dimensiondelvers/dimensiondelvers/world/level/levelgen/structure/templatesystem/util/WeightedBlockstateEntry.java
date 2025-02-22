package com.dimensiondelvers.dimensiondelvers.world.level.levelgen.structure.templatesystem.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.world.level.block.state.BlockState;

import static com.dimensiondelvers.dimensiondelvers.util.ModCodecs.BLOCK_STATE_CODEC;

public class WeightedBlockstateEntry implements WeightedEntry {

    public static final Codec<WeightedBlockstateEntry> CODEC = RecordCodecBuilder.create(builder ->
            builder.group(
                    BLOCK_STATE_CODEC.fieldOf("blockstate").forGetter(WeightedBlockstateEntry::getBlockState),
                    Codec.INT.fieldOf("weight").xmap(Weight::of, Weight::asInt).forGetter(WeightedBlockstateEntry::getWeight)
            ).apply(builder, WeightedBlockstateEntry::new));

    private final BlockState blockState;
    private final Weight weight;

    public WeightedBlockstateEntry(BlockState blockState, Weight weight) {
        this.blockState = blockState;
        this.weight = weight;
    }

    public BlockState getBlockState() {
        return blockState;
    }

    @Override
    public Weight getWeight() {
        return weight;
    }
}
