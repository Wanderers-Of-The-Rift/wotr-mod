package com.wanderersoftherift.wotr.loot.provider.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrNumberProviders;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record SumNumberProvider(List<NumberProvider> values) implements NumberProvider {
    public static final MapCodec<SumNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviders.CODEC.listOf(1, Integer.MAX_VALUE).fieldOf("values").forGetter(SumNumberProvider::values)
    ).apply(instance, SumNumberProvider::new));

    @Override
    public float getFloat(@NotNull LootContext lootContext) {
        return (float) values.stream().mapToDouble(value -> value.getFloat(lootContext)).sum();
    }

    @Override
    public @NotNull LootNumberProviderType getType() {
        return WotrNumberProviders.SUM.get();
    }
}
