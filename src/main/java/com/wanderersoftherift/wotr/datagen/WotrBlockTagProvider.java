package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrBlocks;
import com.wanderersoftherift.wotr.init.WotrTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.BlockFamily;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.BlockTags;
import net.neoforged.neoforge.common.data.BlockTagsProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;

import static net.minecraft.tags.BlockTags.FENCES;
import static net.minecraft.tags.BlockTags.FENCE_GATES;
import static net.minecraft.tags.BlockTags.SLABS;
import static net.minecraft.tags.BlockTags.STAIRS;
import static net.minecraft.tags.BlockTags.WALLS;

/**
 * Handles Data Generation for Block Tags of the Wotr mod
 */
public class WotrBlockTagProvider extends BlockTagsProvider {
    public WotrBlockTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, lookupProvider, WanderersOfTheRift.MODID);
    }

    @Override
    protected void addTags(HolderLookup.@NotNull Provider provider) {
        // spotless:off
        tag(BlockTags.MINEABLE_WITH_PICKAXE)
                .add(WotrBlocks.RUNE_ANVIL_ENTITY_BLOCK.get())
                .add(WotrBlocks.KEY_FORGE.get())
                .add(WotrBlocks.ABILITY_BENCH.get())
                .add(WotrBlocks.RIFT_SPAWNER.get());

        tag(WotrTags.Blocks.BANNED_IN_RIFT)
                .add(WotrBlocks.RIFT_SPAWNER.get())
                .add(WotrBlocks.ABILITY_BENCH.get());

        tag(BlockTags.MINEABLE_WITH_SHOVEL)
                .add(WotrBlocks.NOGRAVGRAVEL.get())
                .add(WotrBlocks.NOGRAVSAND.get())
                .add(WotrBlocks.NOGRAVREDSAND.get())
                .add(WotrBlocks.NOGRAVWHITECONCRETEPOWDER.get())
                .add(WotrBlocks.NOGRAVORANGECONCRETEPOWDER.get())
                .add(WotrBlocks.NOGRAVLIGHTBLUECONCRETEPOWDER.get())
                .add(WotrBlocks.NOGRAVYELLOWCONCRETEPOWDER.get())
                .add(WotrBlocks.NOGRAVLIMECONCRETEPOWDER.get())
                .add(WotrBlocks.NOGRAVPINKCONCRETEPOWDER.get())
                .add(WotrBlocks.NOGRAVGRAYCONCRETEPOWDER.get())
                .add(WotrBlocks.NOGRAVLIGHTGRAYCONCRETEPOWDER.get())
                .add(WotrBlocks.NOGRAVCYANCONCRETEPOWDER.get())
                .add(WotrBlocks.NOGRAVPURPLECONCRETEPOWDER.get())
                .add(WotrBlocks.NOGRAVBLUECONCRETEPOWDER.get())
                .add(WotrBlocks.NOGRAVBROWNCONCRETEPOWDER.get())
                .add(WotrBlocks.NOGRAVGREENCONCRETEPOWDER.get())
                .add(WotrBlocks.NOGRAVREDCONCRETEPOWDER.get())
                .add(WotrBlocks.NOGRAVBLACKCONCRETEPOWDER.get());

        tag(BlockTags.SAND)
                .add(WotrBlocks.NOGRAVSAND.get())
                .add(WotrBlocks.NOGRAVREDSAND.get());

        tag(BlockTags.BAMBOO_PLANTABLE_ON)
                .add(WotrBlocks.NOGRAVGRAVEL.get());
        // spotless:on

        WotrBlocks.BLOCK_FAMILY_HELPERS.forEach(family -> {
            if (family.getVariant(BlockFamily.Variant.STAIRS) != null) {
                tag(STAIRS).add(family.getVariant(BlockFamily.Variant.STAIRS).get());
            }
            if (family.getVariant(BlockFamily.Variant.SLAB) != null) {
                tag(SLABS).add(family.getVariant(BlockFamily.Variant.SLAB).get());
            }
            if (family.getVariant(BlockFamily.Variant.WALL) != null) {
                tag(WALLS).add(family.getVariant(BlockFamily.Variant.WALL).get());
            }
            if (family.getVariant(BlockFamily.Variant.FENCE) != null) {
                tag(FENCES).add(family.getVariant(BlockFamily.Variant.FENCE).get());
            }
            if (family.getVariant(BlockFamily.Variant.FENCE_GATE) != null) {
                tag(FENCE_GATES).add(family.getVariant(BlockFamily.Variant.FENCE_GATE).get());
            }
        });
    }

}
