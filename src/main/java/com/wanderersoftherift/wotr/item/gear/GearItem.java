package com.wanderersoftherift.wotr.item.gear;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.level.Level;

import java.awt.event.ItemEvent;


public class GearItem extends SwordItem {
    public ResourceLocation basic;
    public ResourceLocation secondary;

    public float basicCooldown;
    public float secondaryCooldown;



    public GearItem(ToolMaterial material, float attackDamage, float attackSpeed, Item.Properties properties, ResourceLocation basic) {
        super(material.applySwordProperties(properties, attackDamage, attackSpeed));
        this.basic = basic;
    }

    public ResourceLocation getBasic(){return basic;}

    public void tickCooldowns(){
        basicCooldown++;
        secondaryCooldown++;
    }
}
