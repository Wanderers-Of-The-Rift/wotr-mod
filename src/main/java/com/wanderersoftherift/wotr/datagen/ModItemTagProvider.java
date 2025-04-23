package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.ModTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

/* Handles Data Generation for Block Tags of the Wotr mod */
public class ModItemTagProvider extends ItemTagsProvider {
    public ModItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags, WanderersOfTheRift.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModTags.Items.SOCKETABLE).addTag(Tags.Items.ARMORS).addTag(Tags.Items.TOOLS);

        // potentailly look at changing this tag to 'item tier' instead of tying to max sockets

        // Add Tier 1 items (wood & leather)
        tag(ModTags.Items.ITEM_TIER_ONE).add(Items.WOODEN_SWORD);
        tag(ModTags.Items.ITEM_TIER_ONE).add(Items.WOODEN_PICKAXE);
        tag(ModTags.Items.ITEM_TIER_ONE).add(Items.WOODEN_AXE);
        tag(ModTags.Items.ITEM_TIER_ONE).add(Items.WOODEN_SHOVEL);
        tag(ModTags.Items.ITEM_TIER_ONE).add(Items.WOODEN_HOE);
        tag(ModTags.Items.ITEM_TIER_ONE).add(Items.LEATHER_HELMET);
        tag(ModTags.Items.ITEM_TIER_ONE).add(Items.LEATHER_CHESTPLATE);
        tag(ModTags.Items.ITEM_TIER_ONE).add(Items.LEATHER_LEGGINGS);
        tag(ModTags.Items.ITEM_TIER_ONE).add(Items.LEATHER_BOOTS);

        // Add tier 2 items (stone)
        tag(ModTags.Items.ITEM_TIER_TWO).add(Items.STONE_SWORD);
        tag(ModTags.Items.ITEM_TIER_TWO).add(Items.STONE_PICKAXE);
        tag(ModTags.Items.ITEM_TIER_TWO).add(Items.STONE_AXE);
        tag(ModTags.Items.ITEM_TIER_TWO).add(Items.STONE_SHOVEL);
        tag(ModTags.Items.ITEM_TIER_TWO).add(Items.STONE_HOE);
        tag(ModTags.Items.ITEM_TIER_TWO).add(Items.CHAINMAIL_HELMET);
        tag(ModTags.Items.ITEM_TIER_TWO).add(Items.CHAINMAIL_CHESTPLATE);
        tag(ModTags.Items.ITEM_TIER_TWO).add(Items.CHAINMAIL_LEGGINGS);
        tag(ModTags.Items.ITEM_TIER_TWO).add(Items.CHAINMAIL_BOOTS);

        // Add tier 3 items (iron)
        tag(ModTags.Items.ITEM_TIER_THREE).add(Items.IRON_SWORD);
        tag(ModTags.Items.ITEM_TIER_THREE).add(Items.IRON_PICKAXE);
        tag(ModTags.Items.ITEM_TIER_THREE).add(Items.IRON_AXE);
        tag(ModTags.Items.ITEM_TIER_THREE).add(Items.IRON_SHOVEL);
        tag(ModTags.Items.ITEM_TIER_THREE).add(Items.IRON_HOE);
        tag(ModTags.Items.ITEM_TIER_THREE).add(Items.IRON_HELMET);
        tag(ModTags.Items.ITEM_TIER_THREE).add(Items.IRON_CHESTPLATE);
        tag(ModTags.Items.ITEM_TIER_THREE).add(Items.IRON_LEGGINGS);
        tag(ModTags.Items.ITEM_TIER_THREE).add(Items.IRON_BOOTS);

        // Add tier 4 items (gold)
        tag(ModTags.Items.ITEM_TIER_FOUR).add(Items.GOLDEN_SWORD);
        tag(ModTags.Items.ITEM_TIER_FOUR).add(Items.GOLDEN_PICKAXE);
        tag(ModTags.Items.ITEM_TIER_FOUR).add(Items.GOLDEN_AXE);
        tag(ModTags.Items.ITEM_TIER_FOUR).add(Items.GOLDEN_SHOVEL);
        tag(ModTags.Items.ITEM_TIER_FOUR).add(Items.GOLDEN_HOE);
        tag(ModTags.Items.ITEM_TIER_FOUR).add(Items.GOLDEN_HELMET);
        tag(ModTags.Items.ITEM_TIER_FOUR).add(Items.GOLDEN_CHESTPLATE);
        tag(ModTags.Items.ITEM_TIER_FOUR).add(Items.GOLDEN_LEGGINGS);
        tag(ModTags.Items.ITEM_TIER_FOUR).add(Items.GOLDEN_BOOTS);

        tag(ModTags.Items.ITEM_TIER_FOUR).add(Items.TURTLE_HELMET);
        tag(ModTags.Items.ITEM_TIER_FOUR).add(Items.CROSSBOW);
        tag(ModTags.Items.ITEM_TIER_FOUR).add(Items.BOW);
        tag(ModTags.Items.ITEM_TIER_FOUR).add(Items.FISHING_ROD);

        // Add tier 5 items (diamond)
        tag(ModTags.Items.ITEM_TIER_FIVE).add(Items.DIAMOND_SWORD);
        tag(ModTags.Items.ITEM_TIER_FIVE).add(Items.DIAMOND_PICKAXE);
        tag(ModTags.Items.ITEM_TIER_FIVE).add(Items.DIAMOND_AXE);
        tag(ModTags.Items.ITEM_TIER_FIVE).add(Items.DIAMOND_SHOVEL);
        tag(ModTags.Items.ITEM_TIER_FIVE).add(Items.DIAMOND_HOE);
        tag(ModTags.Items.ITEM_TIER_FIVE).add(Items.DIAMOND_HELMET);
        tag(ModTags.Items.ITEM_TIER_FIVE).add(Items.DIAMOND_CHESTPLATE);
        tag(ModTags.Items.ITEM_TIER_FIVE).add(Items.DIAMOND_LEGGINGS);
        tag(ModTags.Items.ITEM_TIER_FIVE).add(Items.DIAMOND_BOOTS);

        tag(ModTags.Items.ITEM_TIER_FIVE).add(Items.TRIDENT);

        // Add tier 6 items (netherite)
        tag(ModTags.Items.UPGRADEABLE_ITEM_TIER_SIX).add(Items.NETHERITE_SWORD);
        tag(ModTags.Items.UPGRADEABLE_ITEM_TIER_SIX).add(Items.NETHERITE_PICKAXE);
        tag(ModTags.Items.UPGRADEABLE_ITEM_TIER_SIX).add(Items.NETHERITE_AXE);
        tag(ModTags.Items.UPGRADEABLE_ITEM_TIER_SIX).add(Items.NETHERITE_SHOVEL);
        tag(ModTags.Items.UPGRADEABLE_ITEM_TIER_SIX).add(Items.NETHERITE_HOE);
        tag(ModTags.Items.UPGRADEABLE_ITEM_TIER_SIX).add(Items.NETHERITE_HELMET);
        tag(ModTags.Items.UPGRADEABLE_ITEM_TIER_SIX).add(Items.NETHERITE_CHESTPLATE);
        tag(ModTags.Items.UPGRADEABLE_ITEM_TIER_SIX).add(Items.NETHERITE_LEGGINGS);
        tag(ModTags.Items.UPGRADEABLE_ITEM_TIER_SIX).add(Items.NETHERITE_BOOTS);

        tag(ModTags.Items.ITEM_TIER_SIX).add(Items.ELYTRA);

    }
}