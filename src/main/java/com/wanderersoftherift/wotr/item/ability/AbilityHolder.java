package com.wanderersoftherift.wotr.item.ability;

import com.wanderersoftherift.wotr.abilities.AbstractAbility;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

/**
 * Item for holding abilities. Any item can really, but this one handles displaying the ability and its name
 */
public class AbilityHolder extends Item {
    public AbilityHolder(Properties properties) {
        super(properties);
    }

    @Override
    public Component getName(ItemStack stack) {
        Holder<AbstractAbility> abilityHolder = stack.get(WotrDataComponentType.ABILITY);
        if (abilityHolder != null) {
            return abilityHolder.value().getDisplayName();
        }
        return super.getName(stack);
    }

}
