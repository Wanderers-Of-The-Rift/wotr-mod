package com.wanderersoftherift.wotr.mixin;

import com.wanderersoftherift.wotr.entity.player.LivingAttributeChangedEvent;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.neoforged.neoforge.common.NeoForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LivingEntity.class)
public abstract class MixinLivingEntity {

    @Inject(method = "onAttributeUpdated", at = @At(value = "TAIL"))
    private void sendPlayerEventOnAttributeUpdated(Holder<Attribute> attribute, CallbackInfo ci) {
        LivingEntity entity = (LivingEntity) (Object) this;
        NeoForge.EVENT_BUS.post(new LivingAttributeChangedEvent(entity, attribute));
    }
}
