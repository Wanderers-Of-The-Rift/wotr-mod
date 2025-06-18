package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.loot.predicates.RiftLevelCheck;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.loot.BlockLootSubProvider;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.storage.loot.LootPool;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.LootItem;
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

        addRiftConditionalDrop(WotrBlocks.NOGRAVGRAVEL.get(), Blocks.GRAVEL.asItem());
        addRiftConditionalDrop(WotrBlocks.NOGRAVSAND.get(), Blocks.SAND.asItem());
        addRiftConditionalDrop(WotrBlocks.NOGRAVREDSAND.get(), Blocks.RED_SAND.asItem());
        addRiftConditionalDrop(WotrBlocks.NOGRAVWHITECONCRETEPOWDER.get(), Blocks.WHITE_CONCRETE_POWDER.asItem());
        addRiftConditionalDrop(WotrBlocks.NOGRAVORANGECONCRETEPOWDER.get(), Blocks.ORANGE_CONCRETE_POWDER.asItem());
        addRiftConditionalDrop(WotrBlocks.NOGRAVMAGENTACONCRETEPOWDER.get(), Blocks.MAGENTA_CONCRETE_POWDER.asItem());
        addRiftConditionalDrop(WotrBlocks.NOGRAVLIGHTBLUECONCRETEPOWDER.get(),
                Blocks.LIGHT_BLUE_CONCRETE_POWDER.asItem());
        addRiftConditionalDrop(WotrBlocks.NOGRAVYELLOWCONCRETEPOWDER.get(), Blocks.YELLOW_CONCRETE_POWDER.asItem());
        addRiftConditionalDrop(WotrBlocks.NOGRAVLIMECONCRETEPOWDER.get(), Blocks.LIME_CONCRETE_POWDER.asItem());
        addRiftConditionalDrop(WotrBlocks.NOGRAVPINKCONCRETEPOWDER.get(), Blocks.PINK_CONCRETE_POWDER.asItem());
        addRiftConditionalDrop(WotrBlocks.NOGRAVGRAYCONCRETEPOWDER.get(), Blocks.GRAY_CONCRETE_POWDER.asItem());
        addRiftConditionalDrop(WotrBlocks.NOGRAVLIGHTGRAYCONCRETEPOWDER.get(),
                Blocks.LIGHT_GRAY_CONCRETE_POWDER.asItem());
        addRiftConditionalDrop(WotrBlocks.NOGRAVCYANCONCRETEPOWDER.get(), Blocks.CYAN_CONCRETE_POWDER.asItem());
        addRiftConditionalDrop(WotrBlocks.NOGRAVPURPLECONCRETEPOWDER.get(), Blocks.PURPLE_CONCRETE_POWDER.asItem());
        addRiftConditionalDrop(WotrBlocks.NOGRAVBLUECONCRETEPOWDER.get(), Blocks.BLUE_CONCRETE_POWDER.asItem());
        addRiftConditionalDrop(WotrBlocks.NOGRAVBROWNCONCRETEPOWDER.get(), Blocks.BROWN_CONCRETE_POWDER.asItem());
        addRiftConditionalDrop(WotrBlocks.NOGRAVGREENCONCRETEPOWDER.get(), Blocks.GREEN_CONCRETE_POWDER.asItem());
        addRiftConditionalDrop(WotrBlocks.NOGRAVREDCONCRETEPOWDER.get(), Blocks.RED_CONCRETE_POWDER.asItem());
        addRiftConditionalDrop(WotrBlocks.NOGRAVBLACKCONCRETEPOWDER.get(), Blocks.BLACK_CONCRETE_POWDER.asItem());

    }

    private void addRiftConditionalDrop(Block block, Item riftDrop) {
        this.add(block, LootTable.lootTable()
                .withPool(LootPool.lootPool()
                        .add(AlternativesEntry.alternatives(
                                LootItem.lootTableItem(riftDrop)
                                        .when(RiftLevelCheck.riftTier().min(1).max(Integer.MAX_VALUE)),
                                LootItem.lootTableItem(block)
                        ))
                )
        );
    }

    @Override
    protected @NotNull Iterable<Block> getKnownBlocks() {
        return WotrBlocks.BLOCKS.getEntries().stream().map(Holder::value)::iterator;
    }
}
