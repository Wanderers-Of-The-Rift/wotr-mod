package com.wanderersoftherift.wotr.item.block;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.world.level.block.Block;

public class AbilityBenchItem extends DefaultGeoBlockItem {

    public AbilityBenchItem(Block block, Properties properties) {
        super(block, properties, WanderersOfTheRift.id("ability_bench"));
    }
}
