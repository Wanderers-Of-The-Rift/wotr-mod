package com.wanderersoftherift.wotr.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.advancements.critereon.EnchantmentPredicate;
import net.minecraft.advancements.critereon.ItemEnchantmentsPredicate;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicates;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.MatchTool;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.HashMap;
import java.util.function.Predicate;

@Mixin(MatchTool.class)
public class MixinMatchTool {

    @Redirect(method = "test(Lnet/minecraft/world/level/storage/loot/LootContext;)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/advancements/critereon/ItemPredicate;test(Lnet/minecraft/world/item/ItemStack;)Z"))
    private <T> boolean testModifierEnchants(
            ItemPredicate instance,
            ItemStack itemStack,
            @Local(argsOnly = true) LootContext context) {
        var predicate = fixPredicate(instance);
        if (!predicate.test(itemStack)) {
            return false;
        }
        var requirements = getEnchantPredicate(instance);
        if (context.hasParameter(LootContextParams.THIS_ENTITY) && requirements != null) {
            var entity = context.getParameter(LootContextParams.THIS_ENTITY);
            var entityEnchants = entity.getData(WotrAttachments.ENCHANTS).getEnchants();
            for (EnchantmentPredicate it : requirements.enchantments) {
                if (!it.containedIn(entityEnchants)) {
                    return false;
                }
            }
        }
        return true;
    }

    private Predicate<ItemStack> fixPredicate(ItemPredicate instance) {
        var newSubPredicates = new HashMap<>(instance.subPredicates());
        newSubPredicates.remove(ItemSubPredicates.ENCHANTMENTS);
        return new ItemPredicate(instance.items(), instance.count(), instance.components(), newSubPredicates);
    }

    private ItemEnchantmentsPredicate.Enchantments getEnchantPredicate(ItemPredicate instance) {
        return (ItemEnchantmentsPredicate.Enchantments) instance.subPredicates().get(ItemSubPredicates.ENCHANTMENTS);
    }
}
