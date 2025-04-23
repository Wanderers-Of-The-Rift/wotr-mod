package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.loot.predicates.RiftLevelCheck;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModLootItemConditionTypes {
    public static final DeferredRegister<LootItemConditionType> LOOT_ITEM_CONDITION_TYPES = DeferredRegister
            .create(Registries.LOOT_CONDITION_TYPE, WanderersOfTheRift.MODID);

    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> RIFT_LEVEL_CHECK = LOOT_ITEM_CONDITION_TYPES
            .register("rift_level_check", () -> new LootItemConditionType(RiftLevelCheck.CODEC));
}
