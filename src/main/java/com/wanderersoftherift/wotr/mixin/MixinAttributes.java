package com.wanderersoftherift.wotr.mixin;

import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.RangedAttribute;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Attributes.class)
public class MixinAttributes {

    @Redirect(method = "<clinit>", at = @At(value = "NEW", target = "(Ljava/lang/String;DDD)Lnet/minecraft/world/entity/ai/attributes/RangedAttribute;"))
    private static RangedAttribute redirect(String descriptionId, double defaultValue, double min, double max) {
        if ("attribute.name.attack_speed".equals(descriptionId)) {
            // Default attack speed of 4.0 is too much and would make fists almost stronger than wooden swords without
            // I-frames so it's reduced
            return new RangedAttribute(descriptionId, 1.0, min, max);
        }

        return new RangedAttribute(descriptionId, defaultValue, min, max);
    }
}
