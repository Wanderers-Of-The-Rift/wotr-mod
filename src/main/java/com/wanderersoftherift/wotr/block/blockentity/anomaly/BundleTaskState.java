package com.wanderersoftherift.wotr.block.blockentity.anomaly;

import com.mojang.serialization.Codec;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;

public record BundleTaskState(Object2IntMap<Item> requirements) {
    public static final Codec<BundleTaskState> CODEC = Codec
            .unboundedMap(BuiltInRegistries.ITEM.byNameCodec(), Codec.INT)
            .xmap(it -> new BundleTaskState(new Object2IntOpenHashMap<>(it)), it -> it.requirements);
}
