package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.functions.CopyComponentsFunction;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class WotrBlockLootTableProvider extends BlockLootSubProvider {
    public WotrBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        WotrBlocks.BLOCK_FAMILY_HELPERS.forEach(helper -> {
            dropSelf(helper.getBlock().get());
            helper.getVariants().forEach((variant, block) -> dropSelf(block.get()));
            helper.getModVariants().forEach((variant, block) -> dropSelf(block.get()));
        });
        dropSelf(WotrBlocks.KEY_FORGE.get());
        dropSelf(WotrBlocks.ABILITY_BENCH.get());
        dropSelf(WotrBlocks.RUNE_ANVIL_ENTITY_BLOCK.get());
        dropSelf(WotrBlocks.RIFT_CHEST.get());
        dropSelf(WotrBlocks.RIFT_SPAWNER.get());
        dropSelf(WotrBlocks.DITTO_BLOCK.get());
        dropSelf(WotrBlocks.TRAP_BLOCK.get());
        dropSelf(WotrBlocks.PLAYER_TRAP_BLOCK.get());
        dropSelf(WotrBlocks.MOB_TRAP_BLOCK.get());
        dropSelf(WotrBlocks.SPRING_BLOCK.get());
        dropSelf(WotrBlocks.NPC.get());
        add(WotrBlocks.RIFT_MOB_SPAWNER.get(), noDrop());
        add(WotrBlocks.ANOMALY.get(), noDrop());
        add(WotrBlocks.NPC.get(),
                LootTable.lootTable()
                        .withPool(this.applyExplosionCondition(WotrBlocks.NPC.asItem(),
                                LootPool.lootPool()
                                        .setRolls(ConstantValue.exactly(1.0F))
                                        .add(LootItem.lootTableItem(WotrBlocks.NPC.asItem())
                                                .apply(
                                                        CopyComponentsFunction.copyComponents(
                                                                CopyComponentsFunction.Source.BLOCK_ENTITY)
                                                                .include(WotrDataComponentType.NPC_IDENTITY.get())
                                                )))));

    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return WotrBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
