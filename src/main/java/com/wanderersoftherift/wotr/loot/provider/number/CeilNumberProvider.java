package com.wanderersoftherift.wotr.loot.provider.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.wanderersoftherift.wotr.init.WotrNumberProviders;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;
import org.joml.Math;

public record CeilNumberProvider(NumberProvider value) implements NumberProvider {

    public static final MapCodec<CeilNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
            NumberProviders.CODEC.fieldOf("value").forGetter(CeilNumberProvider::value)
    ).apply(instance, CeilNumberProvider::new));

    @Override
    public float getFloat(@NotNull LootContext lootContext) {
        return Math.ceil(value.getFloat(lootContext));
    }

    @Override
    public @NotNull LootNumberProviderType getType() {
        return WotrNumberProviders.CEIL.get();
    }
}
