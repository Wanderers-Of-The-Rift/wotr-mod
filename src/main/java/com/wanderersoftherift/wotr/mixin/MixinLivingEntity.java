package com.wanderersoftherift.wotr.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.wanderersoftherift.wotr.modifier.ModifierHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(LivingEntity.class)
public class MixinLivingEntity {

    @Inject(method = "onChangedBlock", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;runLocationChangedEffects(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/entity/LivingEntity;)V"))
    private void onChangedBlockRunLocationChangedEffects(ServerLevel level, BlockPos pos, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        ModifierHelper.enableModifier(livingEntity);
    }

    @Inject(method = "collectEquipmentChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;runLocationChangedEffects(Lnet/minecraft/server/level/ServerLevel;Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V"))
    private void collectEquipmentChangesRunLocationChangedEffects(CallbackInfoReturnable<Map> cir,
            @Local EquipmentSlot equipmentslot1, @Local ItemStack itemstack2) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        ModifierHelper.enableModifier(itemstack2, livingEntity, equipmentslot1);
    }

    @Inject(method = "stopLocationBasedEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/enchantment/EnchantmentHelper;stopLocationBasedEffects(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/entity/EquipmentSlot;)V"))
    private void stopLocationBasedEffectsStopLocationBasedEffects(ItemStack stack, EquipmentSlot slot,
            AttributeMap attributeMap, CallbackInfo ci) {
        LivingEntity livingEntity = (LivingEntity) (Object) this;
        ModifierHelper.disableModifier(stack, livingEntity, slot);
    }

}
