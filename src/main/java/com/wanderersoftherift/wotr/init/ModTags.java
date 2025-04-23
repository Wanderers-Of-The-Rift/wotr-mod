package com.wanderersoftherift.wotr.init;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import com.wanderersoftherift.wotr.item.runegem.RunegemData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static class Blocks {

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(WanderersOfTheRift.id(name));
        }
    }

    public static class Items {
        public static final TagKey<Item> DEV_TOOLS = createTag("dev_tools");
        public static final TagKey<Item> UNBREAKABLE_EXCLUSIONS = createTag("unbreakable_exclusions");
        public static final TagKey<Item> SOCKETABLE = createTag("socketable");
        public static final TagKey<Item> ITEM_TIER_ONE = createTag("item_tier_one");
        public static final TagKey<Item> ITEM_TIER_TWO = createTag("item_tier_two");
        public static final TagKey<Item> ITEM_TIER_THREE = createTag("item_tier_three");
        public static final TagKey<Item> ITEM_TIER_FOUR = createTag("item_tier_four");
        public static final TagKey<Item> ITEM_TIER_FIVE = createTag("item_tier_five");
        public static final TagKey<Item> ITEM_TIER_SIX = createTag("item_tier_six");
        public static final TagKey<Item> UPGRADEABLE_ITEM_TIER_SIX = createTag("upgradeable_item_tier_six");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(WanderersOfTheRift.id(name));
        }
    }

    public static class Runegems {

        public static final TagKey<RunegemData> RAW = createTag("raw");
        public static final TagKey<RunegemData> SHAPED = createTag("shaped");
        public static final TagKey<RunegemData> CUT = createTag("cut");
        public static final TagKey<RunegemData> POLISHED = createTag("polished");
        public static final TagKey<RunegemData> FRAMED = createTag("framed");
        public static final TagKey<RunegemData> UNIQUE = createTag("unique");

        private static TagKey<RunegemData> createTag(String name) {
            return TagKey.create(ModDatapackRegistries.RUNEGEM_DATA_KEY,
                    ResourceLocation.fromNamespaceAndPath("wotr", name));
        }
    }
}
