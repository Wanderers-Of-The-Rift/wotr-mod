package com.wanderersoftherift.wotr.client.render.item.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.client.WotrClientRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public interface DecoratorSource {
    Codec<DecoratorSource> CODEC = WotrClientRegistries.DECORATOR_SOURCES.byNameCodec()
            .dispatch(DecoratorSource::type, Function.identity());

    ResourceLocation getIcon(ItemStack item);

    MapCodec<? extends DecoratorSource> type();
}
