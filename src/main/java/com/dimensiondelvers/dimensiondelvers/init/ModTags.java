package com.dimensiondelvers.dimensiondelvers.init;

import com.dimensiondelvers.dimensiondelvers.DimensionDelvers;
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
}
