package com.wanderersoftherift.wotr.server.inventorySnapshot;

import com.wanderersoftherift.wotr.init.ModDataComponentType;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

/**
 * This Loot Modifier ensures that the inventory snapshot id is retained when an item is placed as a block and then destroyed.
 */
public class RetainInventorySnapshotIdLootModifier extends LootModifier {

    public static final MapCodec<RetainInventorySnapshotIdLootModifier> CODEC = RecordCodecBuilder.mapCodec(inst ->
            LootModifier.codecStart(inst).apply(inst, RetainInventorySnapshotIdLootModifier::new));

    protected RetainInventorySnapshotIdLootModifier(LootItemCondition[] conditions) {
        super(conditions);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        BlockEntity blockEntity = context.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (generatedLoot.size() == 1 && blockEntity != null && blockEntity.components().has(ModDataComponentType.INVENTORY_SNAPSHOT_ID.get())) {
            UUID blockId = blockEntity.components().get(ModDataComponentType.INVENTORY_SNAPSHOT_ID.get());
            generatedLoot.getFirst().applyComponents(DataComponentPatch.builder().set(ModDataComponentType.INVENTORY_SNAPSHOT_ID.get(), blockId).build());
        }
        return generatedLoot;
    }

    @Override
    public @NotNull MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }
}
