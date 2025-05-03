package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.loot.functions.GearSocketsFunction;
import com.wanderersoftherift.wotr.loot.functions.RollGearFunction;
import com.wanderersoftherift.wotr.loot.functions.RunegemsFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModLootItemFunctionTypes {
    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_ITEM_FUNCTION_TYPES = DeferredRegister
            .create(Registries.LOOT_FUNCTION_TYPE, WanderersOfTheRift.MODID);

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<GearSocketsFunction>> GEAR_SOCKETS_FUNCTION = LOOT_ITEM_FUNCTION_TYPES
            .register("gear_sockets", () -> new LootItemFunctionType<>(GearSocketsFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<RollGearFunction>> ROLL_GEAR_FUNCTION = LOOT_ITEM_FUNCTION_TYPES
            .register("roll_gear", () -> new LootItemFunctionType<>(RollGearFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<RunegemsFunction>> RUNEGEMS_FUNCTION = LOOT_ITEM_FUNCTION_TYPES
            .register("runegems", () -> new LootItemFunctionType<>(RunegemsFunction.CODEC));
}
