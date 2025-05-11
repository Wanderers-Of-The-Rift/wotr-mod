package com.wanderersoftherift.wotr.init;

import com.mojang.serialization.MapCodec;
import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.core.inventory.snapshot.RetainInventorySnapshotIdLootModifier;
import com.wanderersoftherift.wotr.loot.lootmodifiers.OverrideVanillaLootModifier;
import com.wanderersoftherift.wotr.loot.lootmodifiers.RemoveEnchantedItemsModifier;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS = DeferredRegister
            .create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<RetainInventorySnapshotIdLootModifier>> RETAIN_INVENTORY_SNAPSHOT_ID = GLOBAL_LOOT_MODIFIER_SERIALIZERS
            .register("retain_inventory_snapshot_id", () -> RetainInventorySnapshotIdLootModifier.CODEC);

    // loot modifier to remove enchanted items from vanilla loot tables
    public static final Supplier<MapCodec<RemoveEnchantedItemsModifier>> REMOVE_ENCHANTS_FROM_LOOT = GLOBAL_LOOT_MODIFIER_SERIALIZERS
            .register("remove_enchants_from_loot", () -> RemoveEnchantedItemsModifier.CODEC);

    // loot modifier for specific vanilla loot tables to roll socked gear and add runegems to potential drops
    public static final Supplier<MapCodec<OverrideVanillaLootModifier>> OVERRIDE_LOOT_CHESTS = GLOBAL_LOOT_MODIFIER_SERIALIZERS
            .register("override_loot_chests", () -> OverrideVanillaLootModifier.CODEC);
}
