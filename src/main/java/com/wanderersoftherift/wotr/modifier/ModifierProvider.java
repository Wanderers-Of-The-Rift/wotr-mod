package com.wanderersoftherift.wotr.modifier;

import com.mojang.datafixers.util.Either;
import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.Collection;

/**
 * Interface for data components that provide modifiers
 */
public interface ModifierProvider {

    void forEachModifier(ItemStack stack, WotrEquipmentSlot slot, LivingEntity entity, Action action);

    Collection<Either<FormattedText, TooltipComponent>> tooltips(ItemStack stack);

    @FunctionalInterface
    interface Action {
        void accept(Holder<Modifier> modifierHolder, int tier, float roll, ModifierSource item);
    }
}
