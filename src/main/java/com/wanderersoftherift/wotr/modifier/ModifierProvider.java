package com.wanderersoftherift.wotr.modifier;

import com.mojang.datafixers.util.Either;
import com.wanderersoftherift.wotr.core.inventory.slot.WotrEquipmentSlot;
import com.wanderersoftherift.wotr.modifier.source.ModifierSource;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.stream.Stream;

/**
 * Interface for data components that provide modifiers
 */
public interface ModifierProvider {

    Stream<ModifierEntry> modifiers(ItemStack stack, WotrEquipmentSlot slot, LivingEntity entity);

    default void forEachModifier(ItemStack stack, WotrEquipmentSlot slot, LivingEntity entity, Action action) {
        modifiers(stack, slot, entity).forEach(entry -> {
            action.accept(entry.instance.modifier(), entry.instance.tier(), entry.instance.roll(), entry.source);
        });
    }

    List<Either<FormattedText, TooltipComponent>> tooltips(int maxWidth);

    @FunctionalInterface
    interface Action {
        void accept(Holder<Modifier> modifierHolder, int tier, float roll, ModifierSource item);
    }

    record ModifierEntry(ModifierInstance instance, ModifierSource source) {
    }
}
