package com.wanderersoftherift.wotr.item.block;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.world.level.block.Block;

public class RiftSpawnerItem extends DefaultGeoBlockItem {
    public RiftSpawnerItem(Block block, Properties properties) {
        super(block, properties, WanderersOfTheRift.id("rift_spawner"));
    }
}
