package com.wanderersoftherift.wotr.item.gear;

import net.minecraft.tags.BlockTags;
import net.minecraft.world.item.ToolMaterial;
import net.neoforged.neoforge.common.Tags;

public class Gear_Material {

    public static final ToolMaterial RIFT_GEAR_MATERIAL = new ToolMaterial(
            BlockTags.NEEDS_STONE_TOOL,
            1,
            1f,
            1f,
            0,
            Tags.Items.INGOTS
    );
}
