package com.wanderersoftherift.wotr.init.client;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.render.item.emblem.AbilityEmblemProvider;
import com.wanderersoftherift.wotr.client.render.item.emblem.CurrencyEmblemProvider;
import com.wanderersoftherift.wotr.client.render.item.emblem.EmblemProvider;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrEmblemProviders {
    public static final DeferredRegister<MapCodec<? extends EmblemProvider>> PROVIDERS = DeferredRegister
            .create(WotrClientRegistries.Keys.EMBLEM_PROVIDERS, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends EmblemProvider>> CURRENCY = PROVIDERS.register("currency",
            () -> CurrencyEmblemProvider.CODEC);

    public static final Supplier<MapCodec<? extends EmblemProvider>> ABILITY = PROVIDERS.register("ability",
            () -> AbilityEmblemProvider.CODEC);

}
