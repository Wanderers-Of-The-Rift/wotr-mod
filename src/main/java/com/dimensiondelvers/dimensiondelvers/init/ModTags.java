package com.dimensiondelvers.dimensiondelvers.init;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
import com.dimensiondelvers.dimensiondelvers.item.runegem.RunegemData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class ModTags {

    public static class Blocks {

        private static TagKey<Block> createTag(String name) {
            return BlockTags.create(DimensionDelvers.id(name));
        }
    }

    public static class Items {
        public static final TagKey<Item> DEV_TOOLS = createTag("dev_tools");
        public static final TagKey<Item> SOCKETABLE = createTag("socketable");

        private static TagKey<Item> createTag(String name) {
            return ItemTags.create(DimensionDelvers.id(name));
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
            return TagKey.create(ModDatapackRegistries.RUNEGEM_DATA_KEY, ResourceLocation.fromNamespaceAndPath("dimensiondelvers", name));
        }
    }
}
