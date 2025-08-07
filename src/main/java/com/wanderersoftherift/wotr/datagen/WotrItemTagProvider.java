package com.wanderersoftherift.wotr.datagen;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.init.WotrItems;
import com.wanderersoftherift.wotr.init.WotrTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.Tags;

import java.util.concurrent.CompletableFuture;

/* Handles Data Generation for Block Tags of the Wotr mod */
public class WotrItemTagProvider extends ItemTagsProvider {
    public WotrItemTagProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider,
            CompletableFuture<TagLookup<Block>> blockTags) {
        super(output, lookupProvider, blockTags, WanderersOfTheRift.MODID);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        // spotless:off

        tag(Tags.Items.HIDDEN_FROM_RECIPE_VIEWERS)
                .add(WotrItems.BASE_ABILITY_HOLDER.get())
                .add(WotrItems.BASE_CURRENCY_BAG.get());

        /* Handles all the socketable Armor */
        tag(WotrTags.Items.SOCKETABLE_HELMET_SLOT)
                .add(Items.LEATHER_HELMET)
                .add(Items.CHAINMAIL_HELMET)
                .add(Items.IRON_HELMET)
                .add(Items.GOLDEN_HELMET)
                .add(Items.DIAMOND_HELMET)
                .add(Items.NETHERITE_HELMET);
        tag(WotrTags.Items.SOCKETABLE_CHESTPLATE_SLOT)
                .add(Items.LEATHER_CHESTPLATE)
                .add(Items.CHAINMAIL_CHESTPLATE)
                .add(Items.IRON_CHESTPLATE)
                .add(Items.GOLDEN_CHESTPLATE)
                .add(Items.DIAMOND_CHESTPLATE)
                .add(Items.NETHERITE_CHESTPLATE);
        tag(WotrTags.Items.SOCKETABLE_LEGGINGS_SLOT)
                .add(Items.LEATHER_LEGGINGS)
                .add(Items.CHAINMAIL_LEGGINGS)
                .add(Items.IRON_LEGGINGS)
                .add(Items.GOLDEN_LEGGINGS)
                .add(Items.DIAMOND_LEGGINGS)
                .add(Items.NETHERITE_LEGGINGS);
        tag(WotrTags.Items.SOCKETABLE_BOOTS_SLOT)
                .add(Items.LEATHER_BOOTS)
                .add(Items.CHAINMAIL_BOOTS)
                .add(Items.IRON_BOOTS)
                .add(Items.GOLDEN_BOOTS)
                .add(Items.DIAMOND_BOOTS)
                .add(Items.NETHERITE_BOOTS);

        /* Handles all the socketable main/off hand items */
        tag(WotrTags.Items.SOCKETABLE_MAIN_HAND_SLOT)
                .add(Items.WOODEN_AXE)
                .add(Items.WOODEN_PICKAXE)
                .add(Items.WOODEN_HOE)
                .add(Items.WOODEN_SHOVEL)
                .add(Items.WOODEN_SWORD)
                .add(Items.STONE_AXE)
                .add(Items.STONE_PICKAXE)
                .add(Items.STONE_HOE)
                .add(Items.STONE_SHOVEL)
                .add(Items.STONE_SWORD)
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
                .add(Items.NETHERITE_SWORD)
                .add(Items.BOW)
                .add(Items.CROSSBOW);

        tag(WotrTags.Items.SOCKETABLE_OFF_HAND_SLOT)
                .add(Items.SHIELD);

        // adds back more generic socketable tag by adding all other socketable tags
        tag(WotrTags.Items.SOCKETABLE)
                .addTag(WotrTags.Items.SOCKETABLE_HELMET_SLOT)
                .addTag(WotrTags.Items.SOCKETABLE_CHESTPLATE_SLOT)
                .addTag(WotrTags.Items.SOCKETABLE_LEGGINGS_SLOT)
                .addTag(WotrTags.Items.SOCKETABLE_BOOTS_SLOT)
                .addTag(WotrTags.Items.SOCKETABLE_MAIN_HAND_SLOT)
                .addTag(WotrTags.Items.SOCKETABLE_OFF_HAND_SLOT);

        tag(WotrTags.Items.ROGUE_TYPE_GEAR)
                .add(Items.LEATHER_HELMET)
                .add(Items.LEATHER_CHESTPLATE)
                .add(Items.LEATHER_LEGGINGS)
                .add(Items.LEATHER_BOOTS);

        tag(WotrTags.Items.TANK_TYPE_GEAR)
                .add(Items.IRON_HELMET)
                .add(Items.IRON_CHESTPLATE)
                .add(Items.IRON_LEGGINGS)
                .add(Items.IRON_BOOTS);

        tag(WotrTags.Items.BARBARIAN_TYPE_GEAR)
                .add(Items.DIAMOND_HELMET)
                .add(Items.DIAMOND_CHESTPLATE)
                .add(Items.DIAMOND_LEGGINGS)
                .add(Items.DIAMOND_BOOTS);

        tag(WotrTags.Items.WIZARD_TYPE_GEAR)
                .add(Items.GOLDEN_HELMET)
                .add(Items.GOLDEN_CHESTPLATE)
                .add(Items.GOLDEN_LEGGINGS)
                .add(Items.GOLDEN_BOOTS);

        tag(WotrTags.Items.ROGUE_TYPE_WEAPON)
                .add(Items.WOODEN_SWORD)
                .add(Items.WOODEN_AXE)
                .add(Items.BOW);

        tag(WotrTags.Items.TANK_TYPE_WEAPON)
                .add(Items.IRON_SWORD)
                .add(Items.IRON_AXE)
                .add(Items.SHIELD);

        tag(WotrTags.Items.BARBARIAN_TYPE_WEAPON)
                .add(Items.DIAMOND_SWORD)
                .add(Items.DIAMOND_AXE);

        tag(WotrTags.Items.WIZARD_TYPE_WEAPON)
                .add(Items.GOLDEN_SWORD)
                .add(Items.GOLDEN_AXE)
                .add(Items.CROSSBOW);

        // spotless:on
    }
}