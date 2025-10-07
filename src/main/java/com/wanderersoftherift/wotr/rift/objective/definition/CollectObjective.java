package com.wanderersoftherift.wotr.rift.objective.definition;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.rift.objective.ObjectiveType;
import com.wanderersoftherift.wotr.rift.objective.OngoingObjective;
import com.wanderersoftherift.wotr.rift.objective.ongoing.CollectOngoingObjective;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelAccessor;

public class CollectObjective implements ObjectiveType {
    public static final MapCodec<CollectObjective> CODEC = RecordCodecBuilder.mapCodec(inst -> inst.group(
            ExtraCodecs.POSITIVE_INT.fieldOf("min_quantity").forGetter(CollectObjective::getMinQuantity),
            ExtraCodecs.POSITIVE_INT.fieldOf("max_quantity").forGetter(CollectObjective::getMaxQuantity),
            BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(CollectObjective::getItem)
    ).apply(inst, CollectObjective::new));

    private final int minQuantity;
    private final int maxQuantity;
    private final Item item;

    public CollectObjective(int minQuantity, int maxQuantity, Item item) {
        this.minQuantity = minQuantity;
        this.maxQuantity = maxQuantity;
        this.item = item;
    }

    @Override
    public MapCodec<? extends ObjectiveType> getCodec() {
        return CODEC;
    }

    @Override
    public OngoingObjective generate(LevelAccessor level) {
        int target = level.getRandom().nextIntBetweenInclusive(minQuantity, maxQuantity);
        return new CollectOngoingObjective(target, item);
    }

    public int getMinQuantity() {
        return minQuantity;
    }

    public int getMaxQuantity() {
        return maxQuantity;
    }

    public Item getItem() {
        return item;
    }
}
