package com.wanderersoftherift.wotr.client.render.item.decorator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.client.WotrClientRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

public interface EmblemProvider {
    Codec<EmblemProvider> CODEC = WotrClientRegistries.EMBLEM_PROVIDERS.byNameCodec()
            .dispatch(EmblemProvider::type, Function.identity());

    ResourceLocation getIcon(ItemStack item);

    MapCodec<? extends EmblemProvider> type();
}
