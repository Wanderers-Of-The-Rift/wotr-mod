package com.dimensiondelvers.dimensiondelvers.init;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.loot.functions.GearSocketsFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModLootItemFunctionTypes {
    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_ITEM_FUNCTION_TYPES = DeferredRegister.create(Registries.LOOT_FUNCTION_TYPE, DimensionDelvers.MODID);

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<GearSocketsFunction>> GEAR_SOCKETS_FUNCTION = LOOT_ITEM_FUNCTION_TYPES.register("gear_sockets", () -> new LootItemFunctionType<>(GearSocketsFunction.CODEC));
}
