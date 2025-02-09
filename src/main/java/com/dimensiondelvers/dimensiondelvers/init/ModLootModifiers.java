package com.dimensiondelvers.dimensiondelvers.init;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.server.inventorySnapshot.RetainInventorySnapshotLootModifier;
import com.mojang.serialization.MapCodec;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;

import java.util.function.Supplier;

public class ModLootModifiers {
    public static final DeferredRegister<MapCodec<? extends IGlobalLootModifier>> GLOBAL_LOOT_MODIFIER_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.Keys.GLOBAL_LOOT_MODIFIER_SERIALIZERS, DimensionDelvers.MODID);

    public static final Supplier<MapCodec<RetainInventorySnapshotLootModifier>> RETAIN_INVENTORY_SNAPSHOT =
            GLOBAL_LOOT_MODIFIER_SERIALIZERS.register("retain_inventory_snapshot", () -> RetainInventorySnapshotLootModifier.CODEC);
}
