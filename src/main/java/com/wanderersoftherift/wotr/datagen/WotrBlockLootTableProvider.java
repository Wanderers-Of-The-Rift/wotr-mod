package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.init.WotrBlocks;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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

        add(WotrBlocks.NOGRAVGRAVEL.get(), createSingleItemTable(Blocks.GRAVEL));
        add(WotrBlocks.NOGRAVSAND.get(), createSingleItemTable(Blocks.SAND));
        add(WotrBlocks.NOGRAVREDSAND.get(), createSingleItemTable(Blocks.RED_SAND));
        add(WotrBlocks.NOGRAVWHITECONCRETEPOWDER.get(), createSingleItemTable(Blocks.WHITE_CONCRETE_POWDER));
        add(WotrBlocks.NOGRAVORANGECONCRETEPOWDER.get(), createSingleItemTable(Blocks.ORANGE_CONCRETE_POWDER));
        add(WotrBlocks.NOGRAVMAGENTACONCRETEPOWDER.get(), createSingleItemTable(Blocks.MAGENTA_CONCRETE_POWDER));
        add(WotrBlocks.NOGRAVLIGHTBLUECONCRETEPOWDER.get(), createSingleItemTable(Blocks.LIGHT_BLUE_CONCRETE_POWDER));
        add(WotrBlocks.NOGRAVYELLOWCONCRETEPOWDER.get(), createSingleItemTable(Blocks.YELLOW_CONCRETE_POWDER));
        add(WotrBlocks.NOGRAVLIMECONCRETEPOWDER.get(), createSingleItemTable(Blocks.LIME_CONCRETE_POWDER));
        add(WotrBlocks.NOGRAVPINKCONCRETEPOWDER.get(), createSingleItemTable(Blocks.PINK_CONCRETE_POWDER));
        add(WotrBlocks.NOGRAVGRAYCONCRETEPOWDER.get(), createSingleItemTable(Blocks.GRAY_CONCRETE_POWDER));
        add(WotrBlocks.NOGRAVLIGHTGRAYCONCRETEPOWDER.get(), createSingleItemTable(Blocks.LIGHT_GRAY_CONCRETE_POWDER));
        add(WotrBlocks.NOGRAVCYANCONCRETEPOWDER.get(), createSingleItemTable(Blocks.CYAN_CONCRETE_POWDER));
        add(WotrBlocks.NOGRAVPURPLECONCRETEPOWDER.get(), createSingleItemTable(Blocks.PURPLE_CONCRETE_POWDER));
        add(WotrBlocks.NOGRAVBLUECONCRETEPOWDER.get(), createSingleItemTable(Blocks.BLUE_CONCRETE_POWDER));
        add(WotrBlocks.NOGRAVBROWNCONCRETEPOWDER.get(), createSingleItemTable(Blocks.BROWN_CONCRETE_POWDER));
        add(WotrBlocks.NOGRAVGREENCONCRETEPOWDER.get(), createSingleItemTable(Blocks.GREEN_CONCRETE_POWDER));
        add(WotrBlocks.NOGRAVREDCONCRETEPOWDER.get(), createSingleItemTable(Blocks.RED_CONCRETE_POWDER));
        add(WotrBlocks.NOGRAVBLACKCONCRETEPOWDER.get(), createSingleItemTable(Blocks.BLACK_CONCRETE_POWDER));
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return WotrBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
