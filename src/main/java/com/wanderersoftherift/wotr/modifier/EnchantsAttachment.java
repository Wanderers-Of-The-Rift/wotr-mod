package com.wanderersoftherift.wotr.modifier;

import net.minecraft.core.Holder;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;

public class EnchantsAttachment {

    private final ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);

    public void addEnchantLevels(Holder<Enchantment> enchant, int level) {
        enchantments.set(enchant, enchantments.getLevel(enchant) + level);
    }

    public ItemEnchantments getEnchants() {
        return enchantments.toImmutable();
    }
}
