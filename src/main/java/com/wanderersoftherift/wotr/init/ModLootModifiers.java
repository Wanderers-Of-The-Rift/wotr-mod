package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.server.inventorySnapshot.RetainInventorySnapshotIdLootModifier;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, WanderersOfTheRift.MODID);

    public static final Supplier<MapCodec<RetainInventorySnapshotIdLootModifier>> RETAIN_INVENTORY_SNAPSHOT_ID =
            GLOBAL_LOOT_MODIFIER_SERIALIZERS.register("retain_inventory_snapshot_id", () -> RetainInventorySnapshotIdLootModifier.CODEC);
}
