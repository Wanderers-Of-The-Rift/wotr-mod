package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.init.ModBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public class ModBlockLootTableProvider extends BlockLootSubProvider {
    public ModBlockLootTableProvider(HolderLookup.Provider registries) {
        super(Set.of(), FeatureFlags.REGISTRY.allFlags(), registries);
    }

    @Override
    protected void generate() {
        ModBlocks.BLOCK_FAMILY_HELPERS.forEach(helper -> {
            dropSelf(helper.getBlock().get());
            helper.getVariants().forEach((variant, block) -> dropSelf(block.get()));
            helper.getModVariants().forEach((variant, block) -> dropSelf(block.get()));
        });
        dropSelf(ModBlocks.KEY_FORGE.get());
        dropSelf(ModBlocks.ABILITY_BENCH.get());
        dropSelf(ModBlocks.RUNE_ANVIL_ENTITY_BLOCK.get());
        dropSelf(ModBlocks.RIFT_CHEST.get());
        dropSelf(ModBlocks.RIFT_SPAWNER.get());
        dropSelf(ModBlocks.DITTO_BLOCK.get());
        dropSelf(ModBlocks.TRAP_BLOCK.get());
        dropSelf(ModBlocks.PLAYER_TRAP_BLOCK.get());
        dropSelf(ModBlocks.MOB_TRAP_BLOCK.get());
        dropSelf(ModBlocks.SPRING_BLOCK.get());
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return ModBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
