package com.wanderersoftherift.wotr.item.essence;

import net.minecraft.world.item.Item;

public class EssenceItem extends Item {
    public EssenceItem(Properties properties) {
        super(properties.stacksTo(64));
    }
}
