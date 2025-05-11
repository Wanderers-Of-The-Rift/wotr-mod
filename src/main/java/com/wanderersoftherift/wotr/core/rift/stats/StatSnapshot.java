package com.wanderersoftherift.wotr.core.rift.stats;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.util.FastUtils;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;

import java.util.Map;

/**
 * Captures stats for a player at a point in time, for use in generating a delta at a later point
 */
public class StatSnapshot {

    public static final Codec<StatSnapshot> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.unboundedMap(RegistryFixedCodec.create(Registries.CUSTOM_STAT), Codec.INT)
                    .fieldOf("customStats")
                    .forGetter(x -> FastUtils.toMap(x.customStats))
    ).apply(instance, StatSnapshot::new));

    private final Object2IntMap<Holder<ResourceLocation>> customStats;

    public StatSnapshot() {
        customStats = new Object2IntArrayMap<>();
        customStats.defaultReturnValue(0);
    }

    public StatSnapshot(ServerPlayer player) {
        customStats = new Object2IntArrayMap<>();
        customStats.defaultReturnValue(0);
        Registry<ResourceLocation> customStatRegistry = player.getServer()
                .registryAccess()
                .lookupOrThrow(Registries.CUSTOM_STAT);
        customStatRegistry.forEach(stat -> {
            int value = player.getStats().getValue(Stats.CUSTOM, stat);
            if (value != 0) {
                customStats.put(customStatRegistry.wrapAsHolder(stat), value);
            }
        });
    }

    private StatSnapshot(Map<Holder<ResourceLocation>, Integer> rawMap) {
        customStats = new Object2IntArrayMap<>(rawMap);
        customStats.defaultReturnValue(0);
    }

    public int getValue(ResourceLocation customStat) {
        return customStats.getInt(customStat);
    }

    public Object2IntMap<ResourceLocation> getCustomStatDelta(ServerPlayer player) {
        Object2IntMap<ResourceLocation> result = new Object2IntArrayMap<>();
        result.defaultReturnValue(0);
        customStats.forEach((stat, previous) -> {
            result.put(stat.value(), player.getStats().getValue(Stats.CUSTOM, stat.value()) - previous);
        });
        return result;
    }

}
