package com.wanderersoftherift.wotr.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.wanderersoftherift.wotr.init.WotrAttachments;
import net.minecraft.core.Holder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.ApplyBonusCount;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ApplyBonusCount.class)
public class MixinApplyBonusCount {

    @Redirect(method = "run", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;getItemEnchantmentLevel(Lnet/minecraft/core/Holder;Lnet/minecraft/world/item/ItemStack;)I"))
    private int getEnchantLevelIncludingEntity(
            Holder<Enchantment> enchantment,
            ItemStack stack,
            @Local(argsOnly = true) LootContext context) {
        var result = EnchantmentHelper.getItemEnchantmentLevel(enchantment, stack);
        if (context.hasParameter(LootContextParams.THIS_ENTITY)) {
            var entity = context.getParameter(LootContextParams.THIS_ENTITY);
            var entityEnchants = entity.getData(WotrAttachments.ENCHANTS).getEnchants();
            result += entityEnchants.getLevel(enchantment);
        }
        return result;
    }
}
