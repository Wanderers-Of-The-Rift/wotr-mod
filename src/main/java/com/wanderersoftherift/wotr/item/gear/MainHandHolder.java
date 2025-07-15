package com.wanderersoftherift.wotr.item.gear;

import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Item for holding gear data.
 */
public class MainHandHolder extends Item {
    public MainHandHolder(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        Holder<AbstractGear> gearHolder = stack.get(WotrDataComponentType.GEAR);
        if (gearHolder != null) {
            return gearHolder.value().getDisplayName();
        }
        return super.getName(stack);
    }
}
