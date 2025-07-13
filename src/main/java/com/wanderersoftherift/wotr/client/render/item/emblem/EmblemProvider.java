package com.wanderersoftherift.wotr.client.render.item.emblem;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.init.client.WotrClientRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.function.Function;

/**
 * EmblemProvider is the interface for providers of the icon to render as an emblem on an item, typically based on a
 * data component of the item.
 */
public interface EmblemProvider {
    Codec<EmblemProvider> CODEC = WotrClientRegistries.EMBLEM_PROVIDERS.byNameCodec()
            .dispatch(EmblemProvider::type, Function.identity());

    ResourceLocation getIcon(ItemStack item);

    MapCodec<? extends EmblemProvider> type();
}
