package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.loot.provider.number.MultiplyNumberProvider;
import com.wanderersoftherift.wotr.loot.provider.number.RiftParameterNumberProvider;
import com.wanderersoftherift.wotr.loot.provider.number.SumNumberProvider;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class WotrNumberProviders {

    public static final DeferredRegister<LootNumberProviderType> NUMBER_PROVIDERS = DeferredRegister
            .create(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE, WanderersOfTheRift.MODID);

    public static final Supplier<LootNumberProviderType> RIFT_PARAMETER = NUMBER_PROVIDERS.register("rift_parameter",
            () -> new LootNumberProviderType(RiftParameterNumberProvider.CODEC));
    public static final Supplier<LootNumberProviderType> MULTIPLY = NUMBER_PROVIDERS.register("multiply",
            () -> new LootNumberProviderType(MultiplyNumberProvider.CODEC));
    public static final Supplier<LootNumberProviderType> SUM = NUMBER_PROVIDERS.register("sum",
            () -> new LootNumberProviderType(SumNumberProvider.CODEC));
}
