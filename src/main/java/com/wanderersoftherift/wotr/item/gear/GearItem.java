package com.wanderersoftherift.wotr.item.gear;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ToolMaterial;

import java.awt.event.ItemEvent;


public class GearItem extends SwordItem {
    public GearItem(ToolMaterial material, float attackDamage, float attackSpeed, Item.Properties properties) {
        super(material.applySwordProperties(properties, attackDamage, attackSpeed));
    }

}
