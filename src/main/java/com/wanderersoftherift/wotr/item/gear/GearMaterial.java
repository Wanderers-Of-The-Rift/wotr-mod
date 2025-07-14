package com.wanderersoftherift.wotr.item.gear;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;
import net.neoforged.neoforge.common.Tags;

public class GearMaterial {

    public static final ToolMaterial WAND_MATERIAL = new ToolMaterial(
            BlockTags.NEEDS_STONE_TOOL,
            1,
            -2.0f,
            1f,
            1,
            Tags.Items.INGOTS
    );
}
