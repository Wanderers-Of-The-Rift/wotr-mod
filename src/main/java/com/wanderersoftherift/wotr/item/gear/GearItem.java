package com.wanderersoftherift.wotr.item.gear;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;

import java.awt.event.ItemEvent;


public class GearItem extends SwordItem {
    public float basicCooldown;
    public float secondaryCooldown;



    public GearItem(ToolMaterial material, float attackDamage, float attackSpeed, Item.Properties properties) {
        super(material.applySwordProperties(properties, attackDamage, attackSpeed));
    }

    public void tickCooldowns(){
        basicCooldown++;
        secondaryCooldown++;
    }
}
