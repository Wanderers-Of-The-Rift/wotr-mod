package com.wanderersoftherift.wotr.init.client;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.client.render.item.decorator.CurrencyDecorator;
import com.wanderersoftherift.wotr.client.render.item.decorator.DecoratorSource;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrDecoratorSources {
    public static final DeferredRegister<MapCodec<? extends DecoratorSource>> SOURCES = DeferredRegister
            .create(WotrClientRegistries.Keys.DECORATOR_SOURCES, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<? extends DecoratorSource>> CURRENCY = SOURCES.register("currency",
            () -> CurrencyDecorator.CODEC);

}
