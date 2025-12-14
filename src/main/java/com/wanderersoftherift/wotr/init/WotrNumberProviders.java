package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.loot.provider.number.CeilNumberProvider;
import com.wanderersoftherift.wotr.loot.provider.number.MaxNumberProvider;
import com.wanderersoftherift.wotr.loot.provider.number.MinNumberProvider;
import com.wanderersoftherift.wotr.loot.provider.number.MultiplyNumberProvider;
import com.wanderersoftherift.wotr.loot.provider.number.RiftJigsawCountNumberProvider;
import com.wanderersoftherift.wotr.loot.provider.number.RiftParameterNumberProvider;
import com.wanderersoftherift.wotr.loot.provider.number.RiftRoomCountNumberProvider;
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
    public static final Supplier<LootNumberProviderType> RIFT_STRUCTURE_COUNT = NUMBER_PROVIDERS
            .register("rift_structure_count", () -> new LootNumberProviderType(RiftJigsawCountNumberProvider.CODEC));
    public static final Supplier<LootNumberProviderType> RIFT_ROOM_COUNT = NUMBER_PROVIDERS.register("rift_room_count",
            () -> new LootNumberProviderType(RiftRoomCountNumberProvider.CODEC));

    // operators
    public static final Supplier<LootNumberProviderType> MULTIPLY = NUMBER_PROVIDERS.register("multiply",
            () -> new LootNumberProviderType(MultiplyNumberProvider.CODEC));
    public static final Supplier<LootNumberProviderType> SUM = NUMBER_PROVIDERS.register("sum",
            () -> new LootNumberProviderType(SumNumberProvider.CODEC));
    public static final Supplier<LootNumberProviderType> CEIL = NUMBER_PROVIDERS.register("ceil",
            () -> new LootNumberProviderType(CeilNumberProvider.CODEC));
    public static final Supplier<LootNumberProviderType> MAX = NUMBER_PROVIDERS.register("max",
            () -> new LootNumberProviderType(MaxNumberProvider.CODEC));
    public static final Supplier<LootNumberProviderType> MIN = NUMBER_PROVIDERS.register("min",
            () -> new LootNumberProviderType(MinNumberProvider.CODEC));
}
