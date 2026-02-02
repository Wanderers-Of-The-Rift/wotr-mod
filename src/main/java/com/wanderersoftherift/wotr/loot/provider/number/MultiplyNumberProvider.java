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

public record MultiplyNumberProvider(List<NumberProvider> values) implements NumberProvider {

    public static final MapCodec<MultiplyNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviders.CODEC.listOf(1, Integer.MAX_VALUE)
                    .fieldOf("values")
                    .forGetter(MultiplyNumberProvider::values)
    ).apply(instance, MultiplyNumberProvider::new));

    @Override
    public float getFloat(@NotNull LootContext lootContext) {
        return (float) values.stream()
                .mapToDouble(value -> value.getFloat(lootContext))
                .reduce(1, (left, right) -> left * right);
    }

    @Override
    public @NotNull LootNumberProviderType getType() {
        return WotrNumberProviders.MULTIPLY.get();
    }
}
