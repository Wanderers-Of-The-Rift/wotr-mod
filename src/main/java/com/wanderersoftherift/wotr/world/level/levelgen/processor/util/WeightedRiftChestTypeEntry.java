package com.wanderersoftherift.wotr.world.level.levelgen.processor.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.item.RiftChestType;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;

public class WeightedRiftChestTypeEntry implements WeightedEntry {

    public static final Codec<WeightedRiftChestTypeEntry> CODEC = RecordCodecBuilder.create(builder -> builder
            .group(RiftChestType.CODEC.fieldOf("chest_type").forGetter(WeightedRiftChestTypeEntry::getRiftChestType),
                    Codec.INT.fieldOf("weight")
                            .xmap(Weight::of, Weight::asInt)
                            .forGetter(WeightedRiftChestTypeEntry::getWeight))
            .apply(builder, WeightedRiftChestTypeEntry::new));

    private final RiftChestType riftChestType;
    private final Weight weight;

    public WeightedRiftChestTypeEntry(RiftChestType riftChestType, Weight weight) {
        this.riftChestType = riftChestType;
        this.weight = weight;
    }

    public RiftChestType getRiftChestType() {
        return riftChestType;
    }

    @Override
    public Weight getWeight() {
        return weight;
    }
}
