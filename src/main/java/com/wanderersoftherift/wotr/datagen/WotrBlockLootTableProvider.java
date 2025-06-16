package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.init.WotrBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
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
        add(WotrBlocks.RIFT_MOB_SPAWNER.get(), noDrop());

        add(WotrBlocks.NOGRAVGRAVEL.get(), createSingleItemTable(net.minecraft.world.level.block.Blocks.GRAVEL));

    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return WotrBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
