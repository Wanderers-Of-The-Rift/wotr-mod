package com.wanderersoftherift.wotr.init.loot;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.loot.functions.AbilityHolderFunction;
import com.wanderersoftherift.wotr.loot.functions.GearSocketsFunction;
import com.wanderersoftherift.wotr.loot.functions.RewardInstantLootFunction;
import com.wanderersoftherift.wotr.loot.functions.RollGearFunction;
import com.wanderersoftherift.wotr.loot.functions.RunegemsFunction;
import com.wanderersoftherift.wotr.loot.functions.SetPriceFunction;
import com.wanderersoftherift.wotr.loot.functions.WrappingConditionalLootFunction;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WotrLootItemFunctionTypes {
    public static final DeferredRegister<LootItemFunctionType<?>> LOOT_ITEM_FUNCTION_TYPES = DeferredRegister
            .create(Registries.LOOT_FUNCTION_TYPE, WanderersOfTheRift.MODID);

    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<GearSocketsFunction>> GEAR_SOCKETS_FUNCTION = LOOT_ITEM_FUNCTION_TYPES
            .register("gear_sockets", () -> new LootItemFunctionType<>(GearSocketsFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<RollGearFunction>> ROLL_GEAR_FUNCTION = LOOT_ITEM_FUNCTION_TYPES
            .register("roll_gear", () -> new LootItemFunctionType<>(RollGearFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<RunegemsFunction>> RUNEGEMS_FUNCTION = LOOT_ITEM_FUNCTION_TYPES
            .register("runegems", () -> new LootItemFunctionType<>(RunegemsFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<AbilityHolderFunction>> ABILITY_HOLDER_FUNCTION = LOOT_ITEM_FUNCTION_TYPES
            .register("ability_holder", () -> new LootItemFunctionType<>(AbilityHolderFunction.CODEC));
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<SetPriceFunction>> SET_PRICE_FUNCTION = LOOT_ITEM_FUNCTION_TYPES
            .register(
                    "set_price", () -> new LootItemFunctionType<>(SetPriceFunction.CODEC)
            );
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<RewardInstantLootFunction>> REWARD_INSTANT_LOOT_FUNCTION = LOOT_ITEM_FUNCTION_TYPES
            .register("reward_instant_loot", () -> RewardInstantLootFunction.TYPE);
    public static final DeferredHolder<LootItemFunctionType<?>, LootItemFunctionType<WrappingConditionalLootFunction>> WRAP_CONDITION_FUNCTION = LOOT_ITEM_FUNCTION_TYPES
            .register("wrap_condition", () -> WrappingConditionalLootFunction.TYPE);
}
