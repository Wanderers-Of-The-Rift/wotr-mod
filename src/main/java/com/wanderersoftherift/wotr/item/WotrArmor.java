package com.wanderersoftherift.wotr.item;

import com.wanderersoftherift.wotr.WanderersOfTheRift;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.equipment.Equippable;

public class WotrArmor extends Item {

    public WotrArmor(EquipmentSlot slot, String itemId, int durability) {
        super(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, WanderersOfTheRift.id(itemId)))
                .durability(durability)
                .component(DataComponents.EQUIPPABLE,
                        Equippable.builder(slot).setSwappable(true).setDamageOnHurt(true).build()));
    }
}