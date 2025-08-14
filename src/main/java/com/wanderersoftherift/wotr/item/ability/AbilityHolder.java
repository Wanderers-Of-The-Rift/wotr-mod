package com.wanderersoftherift.wotr.item.ability;

import com.wanderersoftherift.wotr.abilities.Ability;
import com.wanderersoftherift.wotr.init.WotrDataComponentType;
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
        ActivatableAbility abilityComponent = stack.get(WotrDataComponentType.ABILITY);
        if (abilityComponent != null) {
            return Ability.getDisplayName(abilityComponent.ability());
        }
        return super.getName(stack);
    }

}
