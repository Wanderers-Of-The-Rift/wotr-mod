package com.wanderersoftherift.wotr.init.loot;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.loot.predicates.GuildRankCheck;
import com.wanderersoftherift.wotr.loot.predicates.PartialLootTableIdCondition;
import com.wanderersoftherift.wotr.loot.predicates.RiftLevelCheck;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class WotrLootItemConditionTypes {
    public static final DeferredRegister<LootItemConditionType> LOOT_ITEM_CONDITION_TYPES = DeferredRegister
            .create(Registries.LOOT_CONDITION_TYPE, WanderersOfTheRift.MODID);

    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> RIFT_LEVEL_CHECK = LOOT_ITEM_CONDITION_TYPES
            .register("rift_level_check", () -> new LootItemConditionType(RiftLevelCheck.CODEC));
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> PARTIAL_LOOT_TABLE_ID = LOOT_ITEM_CONDITION_TYPES
            .register("partial_loot_table_id", () -> new LootItemConditionType(PartialLootTableIdCondition.CODEC));
    public static final DeferredHolder<LootItemConditionType, LootItemConditionType> GUILD_RANK_CHECK = LOOT_ITEM_CONDITION_TYPES
            .register("guild_rank_check", () -> new LootItemConditionType(GuildRankCheck.CODEC));
}
