package com.wanderersoftherift.wotr.modifier;

import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

/**
 * Interface for data components that provide modifiers
 */
public interface ModifierProvider {

    void forEachModifier(ItemStack stack, WotrEquipmentSlot slot, LivingEntity entity, Visitor visitor);

    @FunctionalInterface
    interface Visitor {
        void accept(Holder<Modifier> modifierHolder, int tier, float roll, ModifierSource item);
    }
}
