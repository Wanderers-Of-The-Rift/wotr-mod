package com.wanderersoftherift.wotr.mixin;

import net.minecraft.core.Holder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ToolMaterial;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ToolMaterial.class)
public class MixinToolMaterial {

    @Inject(method = "createSwordAttributes", at = @At("HEAD"), cancellable = true)
    private void deleteDefaultSwordProperties(
            float attackDamage,
            float attackSpeed,
            CallbackInfoReturnable<ItemAttributeModifiers> cir) {
        cir.setReturnValue(ItemAttributeModifiers.EMPTY);
    }

    @Redirect(method = "createToolAttributes", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/component/ItemAttributeModifiers$Builder;add(Lnet/minecraft/core/Holder;Lnet/minecraft/world/entity/ai/attributes/AttributeModifier;Lnet/minecraft/world/entity/EquipmentSlotGroup;)Lnet/minecraft/world/item/component/ItemAttributeModifiers$Builder;", ordinal = 1))
    private ItemAttributeModifiers.Builder fasterOtherAttackSpeed(
            ItemAttributeModifiers.Builder instance,
            Holder<Attribute> attribute,
            AttributeModifier modifier,
            EquipmentSlotGroup slot) {
        return instance.add(attribute,
                new AttributeModifier(modifier.id(), modifier.amount() + 3, modifier.operation()), slot);
    }
}
