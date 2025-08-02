package com.wanderersoftherift.wotr.item.ability;

import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

/**
 * Item for holding abilities. Any item can really, but this one handles displaying the ability and its name
 */
public class AbilityHolder extends Item {
    public AbilityHolder(Properties properties) {
        super(properties);
    }

    @Override
    public @NotNull Component getName(ItemStack stack) {
        Holder<Ability> abilityHolder = stack.get(WotrDataComponentType.ABILITY);
        if (abilityHolder != null) {
            return Ability.getDisplayName(abilityHolder);
        }
        return super.getName(stack);
    }

}
