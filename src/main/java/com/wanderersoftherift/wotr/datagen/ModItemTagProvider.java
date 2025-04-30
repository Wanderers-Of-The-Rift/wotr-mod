package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.concurrent.CompletableFuture;

/* Handles Data Generation for Block Tags of the Wotr mod */
public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags, WanderersOfTheRift.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {

        /* Handles all the socketable Armor */
        tag(ModTags.Items.SOCKETABLE_HELMET_SLOT).add(Items.LEATHER_HELMET)
                .add(Items.CHAINMAIL_HELMET)
                .add(Items.IRON_HELMET)
                .add(Items.GOLDEN_HELMET)
                .add(Items.DIAMOND_HELMET)
                .add(Items.NETHERITE_HELMET);
        tag(ModTags.Items.SOCKETABLE_CHESTPLATE_SLOT).add(Items.LEATHER_CHESTPLATE)
                .add(Items.CHAINMAIL_CHESTPLATE)
                .add(Items.IRON_CHESTPLATE)
                .add(Items.GOLDEN_CHESTPLATE)
                .add(Items.DIAMOND_CHESTPLATE)
                .add(Items.NETHERITE_CHESTPLATE);
        tag(ModTags.Items.SOCKETABLE_LEGGINGS_SLOT).add(Items.LEATHER_LEGGINGS)
                .add(Items.CHAINMAIL_LEGGINGS)
                .add(Items.IRON_LEGGINGS)
                .add(Items.GOLDEN_LEGGINGS)
                .add(Items.DIAMOND_LEGGINGS)
                .add(Items.NETHERITE_LEGGINGS);
        tag(ModTags.Items.SOCKETABLE_BOOTS_SLOT).add(Items.LEATHER_BOOTS)
                .add(Items.CHAINMAIL_BOOTS)
                .add(Items.IRON_BOOTS)
                .add(Items.GOLDEN_BOOTS)
                .add(Items.DIAMOND_BOOTS)
                .add(Items.NETHERITE_BOOTS);

        /* Handles all the socketable main/off hand items */
        tag(ModTags.Items.SOCKETABLE_MAIN_HAND_SLOT).add(Items.WOODEN_AXE)
                .add(Items.WOODEN_PICKAXE)
                .add(Items.WOODEN_HOE)
                .add(Items.WOODEN_SHOVEL)
                .add(Items.WOODEN_SWORD)
                .add(Items.IRON_AXE)
                .add(Items.IRON_PICKAXE)
                .add(Items.IRON_HOE)
                .add(Items.IRON_SHOVEL)
                .add(Items.IRON_SWORD)
                .add(Items.GOLDEN_AXE)
                .add(Items.GOLDEN_PICKAXE)
                .add(Items.GOLDEN_HOE)
                .add(Items.GOLDEN_SHOVEL)
                .add(Items.GOLDEN_SWORD)
                .add(Items.DIAMOND_AXE)
                .add(Items.DIAMOND_PICKAXE)
                .add(Items.DIAMOND_HOE)
                .add(Items.DIAMOND_SHOVEL)
                .add(Items.DIAMOND_SWORD)
                .add(Items.NETHERITE_AXE)
                .add(Items.NETHERITE_PICKAXE)
                .add(Items.NETHERITE_HOE)
                .add(Items.NETHERITE_SHOVEL)
                .add(Items.NETHERITE_SWORD);

        tag(ModTags.Items.SOCKETABLE_OFF_HAND_SLOT).add(Items.SHIELD);

    }
}